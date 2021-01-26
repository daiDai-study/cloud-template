package com.aac.kpi.system.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.crypto.digest.DigestUtil;
import com.aac.kpi.system.constant.SystemConst;
import com.aac.kpi.system.entity.SysPermission;
import com.aac.kpi.system.mapper.SysPermissionMapper;
import com.aac.kpi.system.model.tree.SysPermissionTree;
import com.aac.kpi.system.model.tree.TreeModel;
import com.aac.kpi.system.service.SysDictService;
import com.aac.kpi.system.service.SysPermissionService;
import com.alibaba.fastjson.JSONArray;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.node.POJONode;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class SysPermissionServiceImpl extends ServiceImpl<SysPermissionMapper, SysPermission> implements SysPermissionService {

    @Resource
    private ObjectMapper objectMapper;

    @Resource
    private SysDictService dictService;

    @Override
    public boolean add(SysPermission sysPermission) {
        //----------------------------------------------------------------------
        //判断是否是一级菜单，是的话清空父菜单
        if (SystemConst.PERMISSION_TYPE_MENU.equals(sysPermission.getMenuType())) {
            sysPermission.setParentId(null);
        }
        //----------------------------------------------------------------------
        String pid = sysPermission.getParentId();
        if (StrUtil.isNotEmpty(pid)) {
            //设置父节点不为叶子节点
            SysPermission sysPermissionToUpdate = new SysPermission();
            sysPermissionToUpdate.setIsLeaf(false);
            this.update(sysPermissionToUpdate, new UpdateWrapper<SysPermission>().eq("id", pid));
        }
        sysPermission.setIsLeaf(true);
        return this.save(sysPermission);
    }

    @Override
    public void delete(SysPermission sysPermission) {
        String id = sysPermission.getId();
        String pid = sysPermission.getParentId();
        int count = this.count(new QueryWrapper<SysPermission>().lambda().eq(SysPermission::getParentId, pid));
        if (count == 1) {
            //若父节点无其他子节点，则该父节点是叶子节点
            SysPermission sysPermissionToUpdate = new SysPermission();
            sysPermissionToUpdate.setIsLeaf(true);
            this.update(sysPermissionToUpdate, new UpdateWrapper<SysPermission>().eq("id", pid));
        }
        baseMapper.deleteById(id);

        // 递归删除其子节点
        List<SysPermission> children = this.list(new QueryWrapper<SysPermission>().lambda().eq(SysPermission::getParentId, id));
        for (SysPermission child : children) {
            this.delete(child);
        }
    }

    @Override
    public void deleteBatch(List<String> idList) {
        List<SysPermission> sysPermissionList = this.listByIds(idList);
        for (SysPermission item : sysPermissionList) {
            this.delete(item);
        }
    }

    @Override
    public void edit(SysPermission sysPermission) {
        SysPermission p = this.getById(sysPermission.getId());
        //TODO 该节点判断是否还有子节点
        if (p == null) {
            throw new RuntimeException("未找到菜单信息");
        } else {
            //----------------------------------------------------------------------
            //判断是否是一级菜单，是的话清空父菜单
            if (SystemConst.PERMISSION_TYPE_MENU.equals(sysPermission.getMenuType())) {
                sysPermission.setParentId("");
            }
            //----------------------------------------------------------------------
            this.updateById(sysPermission);
            String pid = sysPermission.getParentId();
            if (StrUtil.isNotEmpty(pid) && !pid.equals(p.getParentId())) {
                //设置父节点不为叶子节点
                SysPermission sysPermissionToUpdate = new SysPermission();
                sysPermissionToUpdate.setIsLeaf(false);
                this.update(sysPermissionToUpdate, new UpdateWrapper<SysPermission>().eq("id", pid));
            }
        }

    }

    @Override
    public List<SysPermission> listByUsername(String username) {
        return baseMapper.queryByUser(username);
    }

    @Override
    public List<String> listPermsByUsername(String username) {
        List<SysPermission> sysPermissions = this.listByUsername(username);
        return sysPermissions.stream().map(SysPermission::getPerms).collect(Collectors.toList());
    }

    @Override
    public List<TreeModel> queryListByParentId(String parentId) {
        return baseMapper.queryListByParentId(parentId);
    }

    @Override
    public ArrayNode getMenu(String username) {
        ArrayNode arrayNode = this.getPermission(username);
        ArrayNode menu_list = parseNgAlain(arrayNode);
        ObjectNode skyMenu = objectMapper.createObjectNode();
        skyMenu.put("text", "主菜单");
        skyMenu.put("group", false);
        skyMenu.put("hideInBreadcrumb", true);
        skyMenu.set("children", menu_list);
        ArrayNode menuList = objectMapper.createArrayNode();
        menuList.add(skyMenu);
        return menuList;
    }

    @Override
    public ArrayNode getPermission(String username) {
        List<SysPermission> sysPermissionList = this.listByUsername(username);
        ArrayNode arrayNode = objectMapper.createArrayNode();
        getPermissionJsonArray(arrayNode, sysPermissionList, null);
        return arrayNode;
    }

    @Override
    public void getTreeList(List<SysPermissionTree> treeList, List<SysPermission> metaList, SysPermissionTree temp) {
        for (SysPermission permission : metaList) {
            String tempPid = permission.getParentId();
            SysPermissionTree tree = new SysPermissionTree(permission);
            // 替换菜单放在外面，会重复执行同一个菜单，通过count可以查看到，之前执行次数708（59*12），现在执行次数59
            if (temp == null && StrUtil.isEmpty(tempPid)) {
                //替换菜单类型
                replaceTree(treeList,tree,metaList);
            } else if (temp != null && tempPid != null && tempPid.equals(temp.getId())) {
                //替换菜单类型
                replaceChild(tree,temp,treeList,metaList);
            }

        }
    }

    @Override
    public void getTreeModelList(List<TreeModel> treeList, List<SysPermission> metaList, TreeModel temp) {
        for (SysPermission permission : metaList) {
            String tempPid = permission.getParentId();
            TreeModel tree = new TreeModel(permission);
            if (temp == null && StrUtil.isEmpty(tempPid)) {
                treeList.add(tree);
                if (!permission.getIsLeaf()) {
                    getTreeModelList(treeList, metaList, tree);
                }
            } else if (temp != null && tempPid != null && tempPid.equals(temp.getKey())) {
                temp.getChildren().add(tree);
                if (!permission.getIsLeaf()) {
                    getTreeModelList(treeList, metaList, tree);
                }
            }

        }
    }

    /**
     * 获取菜单JSON数组
     */
    private void getPermissionJsonArray(ArrayNode jsonArray, List<SysPermission> metaList, ObjectNode parentJson) {
        for (SysPermission permission : metaList) {
            if (permission.getMenuType() == null) {
                continue;
            }
            String tempPid = permission.getParentId();
            ObjectNode json = getPermissionJsonObject(permission);
            if (parentJson == null && StrUtil.isEmpty(tempPid)) {
                jsonArray.add(json);
                if (!permission.getIsLeaf()) {
                    getPermissionJsonArray(jsonArray, metaList, json);
                }
            } else if (parentJson != null && StrUtil.isNotEmpty(tempPid) && tempPid.equals(parentJson.get("id") != null ? parentJson.get("id").asText("") : "")) {
                if (SystemConst.PERMISSION_TYPE_BUTTON.equals(permission.getMenuType())) {
                    ObjectNode metaJson = (ObjectNode)parentJson.get("meta");
                    if (metaJson.has("permissionList")) {
                        ((ArrayNode) metaJson.get("permissionList")).add(json);
                    } else {
                        ArrayNode permissionList = objectMapper.createArrayNode();
                        permissionList.add(json);
                        metaJson.set("permissionList", permissionList);
                    }

                    // 按钮权限的递归获取
                    if (!permission.getIsLeaf()) {
                        getPermissionJsonArray(metaList, json, parentJson);
                    }

                } else if (SystemConst.PERMISSION_TYPE_SUBMENU.equals(permission.getMenuType())) {
                    if (parentJson.has("children")) {
                        ((ArrayNode) parentJson.get("children")).add(json);
                    } else {
                        ArrayNode children = objectMapper.createArrayNode();
                        children.add(json);
                        parentJson.set("children", children);
                    }

                    if (!permission.getIsLeaf()) {
                        getPermissionJsonArray(jsonArray, metaList, json);
                    }
                }
            }
        }
    }

    private void getPermissionJsonArray(List<SysPermission> metaList, ObjectNode parentJson, ObjectNode targetJson){
        for (SysPermission permission : metaList) {
            if (permission.getMenuType() == null) {
                continue;
            }
            String tempPid = permission.getParentId();
            ObjectNode json = getPermissionJsonObject(permission);
            if (parentJson != null && StrUtil.isNotEmpty(tempPid) && tempPid.equals(parentJson.get("id") != null ? parentJson.get("id").asText("") : "")) {
                if (SystemConst.PERMISSION_TYPE_BUTTON.equals(permission.getMenuType())) {
                    ObjectNode metaJson = (ObjectNode)targetJson.get("meta");
                    if (metaJson.has("permissionList")) {
                        ((ArrayNode) metaJson.get("permissionList")).add(json);
                    } else {
                        ArrayNode permissionList = objectMapper.createArrayNode();
                        permissionList.add(json);
                        metaJson.set("permissionList", permissionList);
                    }

                    if (!permission.getIsLeaf()) {
                        getPermissionJsonArray(metaList, json, targetJson);
                    }

                }
            }
        }
    }

    private ObjectNode getPermissionJsonObject(SysPermission permission) {
        ObjectNode json = objectMapper.createObjectNode();
        if (SystemConst.PERMISSION_TYPE_BUTTON.equals(permission.getMenuType())) {
            json.put("id", permission.getId());
            json.put("action", permission.getPerms());
            json.put("describe", permission.getName());
        } else if (SystemConst.PERMISSION_TYPE_MENU.equals(permission.getMenuType()) || SystemConst.PERMISSION_TYPE_SUBMENU.equals(permission.getMenuType())) {
            json.put("id", permission.getId());
            if (permission.getUrl() != null && (permission.getUrl().startsWith("http://") || permission.getUrl().startsWith("https://"))) {
                json.put("path", DigestUtil.md5Hex(permission.getUrl(), "UTF-8"));
            } else {
                json.put("path", permission.getUrl());
            }

            //重要规则：路由name (通过URL生成路由name,路由name供前端开发，页面跳转使用)
            json.put("name", urlToRouteName(permission.getUrl()));

            //是否隐藏路由，默认都是显示的
            if (permission.isHidden()) {
                json.put("hidden", true);
            }
            //聚合路由
            if (permission.isAlwaysShow()) {
                json.put("alwaysShow", true);
            }
            json.put("component", permission.getComponent());
            ObjectNode meta = objectMapper.createObjectNode();
            meta.put("title", permission.getName());
            if (StrUtil.isEmpty(permission.getParentId())) {
                //一级菜单跳转地址
                json.put("redirect", permission.getRedirect());
            }
            meta.put("icon", StrUtil.trim(StrUtil.emptyToDefault(permission.getIcon(), "")));
            if (permission.getUrl() != null && (permission.getUrl().startsWith("http://") || permission.getUrl().startsWith("https://"))) {
                meta.put("url", permission.getUrl());
            }
            json.set("meta", meta);
        }

        return json;
    }


    private void replaceTree(List<SysPermissionTree> treeList,SysPermissionTree tree, List<SysPermission> metaList){
        if (null != tree.getMenuType()) {
            tree.setMenuType_dictText(dictService.queryDictTextByKey("menu_type", String.valueOf(tree.getMenuType())));
        }
        treeList.add(tree);
        if (!tree.getIsLeaf()) {
            getTreeList(treeList, metaList, tree);
        }
    }
    private void replaceChild(SysPermissionTree tree, SysPermissionTree temp, List<SysPermissionTree> treeList, List<SysPermission> metaList){
        if (null != tree.getMenuType()) {
            tree.setMenuType_dictText(dictService.queryDictTextByKey("menu_type", String.valueOf(tree.getMenuType())));
        }
        temp.getChildren().add(tree);
        if (!tree.getIsLeaf()) {
            getTreeList(treeList, metaList, tree);
        }
    }

    private ArrayNode parseNgAlain(ArrayNode arrayNode) {
        ArrayNode menulist = objectMapper.createArrayNode();
        for (JsonNode jsonNode : arrayNode) {
            String path = jsonNode.get("path") != null ? jsonNode.get("path").asText("") : "";
            ObjectNode menu = objectMapper.createObjectNode();
            JsonNode meta = null;
            if(jsonNode.has("meta") && (meta = jsonNode.get("meta")) != null){
                menu.put("text", meta.get("title") != null ? meta.get("title").asText("") : "");
                menu.set("permissionList", meta.get("permissionList"));
                menu.put("reuse", true);
                ObjectNode icon = objectMapper.createObjectNode();
                icon.put("type", "icon");
                icon.put("value", meta.get("icon") != null ? meta.get("icon").asText("") : "");
                menu.set("icon", icon);
            }
            if (jsonNode.has("children")) {

                // 需要注意（我的理解）
                // 在Vue里面，菜单中有个alwaysShow属性可以一直显示它本身（而不显示其子菜单）
                // 而在Angular，只有一个hide属性可以隐藏菜单，但隐藏时发现父菜单无法点击
                // 所以，此处做的处理就是，如果有菜单（且他有子菜单）的alwaysShow字段为true，则不将其子菜单的加入到children中

                // 此外，还需要注意一个地方，就是link属性，如果是上述情况，则需要在数据库里添加url字段（如个人设置菜单，其url和子菜单基本设置相同），至少目前数据库就是这样设置的
                boolean showChildren = true;
                if ("true".equals(jsonNode.get("alwaysShow") != null ? jsonNode.get("alwaysShow").asText("") : "")) {
                    showChildren = false;
                }
                if (showChildren){
                    ArrayNode child = parseNgAlain((ArrayNode) jsonNode.get("children"));
                    menu.set("children", child);
                }else {
                    menu.put("link", path);
                    menu.put("reuse", false);
                }

            } else {
                menu.put("link", path);
            }

            menulist.add(menu);
        }
        return menulist;
    }



    /**
     * 通过URL生成路由name（去掉URL前缀斜杠，替换内容中的斜杠‘/’为-）
     * 举例： URL = /isystem/role
     * RouteName = isystem-role
     *
     * @return
     */
    private String urlToRouteName(String url) {
        if (StrUtil.isNotEmpty(url)) {
            if (url.startsWith("/")) {
                url = url.substring(1);
            }
            url = url.replace("/", "-");
            return url;
        } else {
            return null;
        }
    }
}
