package com.aac.kpi.system.service;

import com.aac.kpi.system.entity.SysPermission;
import com.aac.kpi.system.model.tree.SysPermissionTree;
import com.aac.kpi.system.model.tree.TreeModel;
import com.baomidou.mybatisplus.extension.service.IService;
import com.fasterxml.jackson.databind.node.ArrayNode;

import java.util.List;

public interface SysPermissionService extends IService<SysPermission> {

    boolean add(SysPermission sysPermission);

    /**
     * 递归删除
     */
    void delete(SysPermission sysPermission);

    void deleteBatch(List<String> idList);

    void edit(SysPermission sysPermission);

    List<SysPermission> listByUsername(String username);

    List<String> listPermsByUsername(String username);

    List<TreeModel> queryListByParentId(String parentId);

    /**
     * 获取菜单
     */
    ArrayNode getMenu(String username);

    ArrayNode getPermission(String username);

    void getTreeList(List<SysPermissionTree> treeList, List<SysPermission> metaList, SysPermissionTree temp);

    void getTreeModelList(List<TreeModel> treeList, List<SysPermission> metaList, TreeModel temp);
}
