package com.aac.kpi.system.controller;


import com.aac.kpi.common.model.ApiResult;
import com.aac.kpi.system.entity.SysPermission;
import com.aac.kpi.system.entity.SysRolePermission;
import com.aac.kpi.system.model.tree.SysPermissionTree;
import com.aac.kpi.system.model.tree.TreeModel;
import com.aac.kpi.system.service.*;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequestMapping("/permission")
public class SysPermissionController extends BaseManagementController<SysPermission, SysPermissionService> {

    @Resource
    private SysRolePermissionService sysRolePermissionService;

    @Resource
    private SysDictService dictService;

    @Resource
    private ObjectMapper objectMapper;

    @Resource
    public void setS(SysPermissionService service) {
        this.service = service;
    }

    @Resource
    public void setSysUserService(SysUserService sysUserService) {
        this.sysUserService = sysUserService;
    }

    @Override
    public ApiResult add(@RequestBody SysPermission model) {
        if (service.add(model)) {
            return ApiResult.ofSuccess(model);
        }
        return ApiResult.ofFailServer(model);
    }

    @Override
    public ApiResult delete(@RequestParam(name = "id") String id) {
        SysPermission model = service.getById(id);
        if (model == null) {
            return ApiResult.ofFailClientNotFound();
        } else {
            try {
                service.delete(model);
                return ApiResult.ofSuccess();
            } catch (Exception e) {
                log.error(e.getMessage(), e);
                return ApiResult.ofFailServer(e.getMessage());
            }
        }
    }

    @Override
    public ApiResult deleteBatch(@RequestParam(name = "ids") String ids) {
        try {
            service.deleteBatch(Arrays.asList(ids.split(",")));
            return ApiResult.ofSuccess();
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return ApiResult.ofFailServer(e.getMessage());
        }
    }

    @Override
    public ApiResult edit(@RequestBody SysPermission model) {
        try {
            service.edit(model);
            return ApiResult.ofSuccess();
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return ApiResult.ofFailServer(e.getMessage());
        }
    }

    /**
     * 权限树列表
     */
    @GetMapping(value = "/treeList")
    public ApiResult treeList() {
        try {
            LambdaQueryWrapper<SysPermission> query = new LambdaQueryWrapper<>();
            query.eq(SysPermission::getDelFlag, 0);
            query.orderByAsc(SysPermission::getSortNo);
            List<SysPermission> list = service.list(query);
            List<SysPermissionTree> treeList = new ArrayList<>();
            service.getTreeList(treeList, list, null);
            return ApiResult.ofSuccess(treeList);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return ApiResult.ofFailServer();
        }
    }

    /**
     * 异步加载数据节点
     */
    @GetMapping(value = "/queryListAsync")
    public ApiResult queryAsync(@RequestParam(name = "pid", required = false) String parentId) {
        try {
            List<TreeModel> list = service.queryListByParentId(parentId);
            if (list == null || list.isEmpty()) {
                return ApiResult.ofFailServer("未找到角色信息");
            } else {
                return ApiResult.ofSuccess(list);
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return ApiResult.ofFailServer("查询失败:" + e.getMessage());
        }
    }

    /**
     * 查询用户的权限
     */
    @GetMapping(value = "/queryByUser")
    public ApiResult queryByUser(@RequestParam("username") String username) {
        try {
            ArrayNode jsonArray = service.getPermission(username);
            return ApiResult.ofSuccess(jsonArray);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return ApiResult.ofFailServer("查询失败:" + e.getMessage());
        }
    }

    /**
     * 查询角色授权
     */
    @GetMapping(value = "/queryRolePermission")
    public ApiResult queryRolePermission(@RequestParam(name = "roleId") String roleId) {
        try {
            List<SysRolePermission> list = sysRolePermissionService.list(new QueryWrapper<SysRolePermission>().lambda().eq(SysRolePermission::getRoleId, roleId));
            final List<String> collect = list.stream().map(SysRolePermission -> String.valueOf(SysRolePermission.getPermissionId())).collect(Collectors.toList());
            return ApiResult.ofSuccess(collect);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return ApiResult.ofFailServer("查询失败:" + e.getMessage());
        }
    }

    /**
     * 获取全部的权限树
     */
    @GetMapping(value = "/treeModelList")
    public ApiResult treeModelList() {
        //全部权限ids
        List<String> ids = new ArrayList<>();
        try {
            LambdaQueryWrapper<SysPermission> query = new LambdaQueryWrapper<>();
            query.eq(SysPermission::getDelFlag, 0);
            query.orderByAsc(SysPermission::getSortNo);
            List<SysPermission> list = service.list(query);
            for (SysPermission sysPer : list) {
                ids.add(sysPer.getId());
            }

            List<TreeModel> treeList = new ArrayList<>();
            service.getTreeModelList(treeList, list, null);

            Map<String, Object> resMap = new HashMap<>();
            resMap.put("treeList", treeList); //全部树节点数据
            resMap.put("ids", ids);//全部树ids
            return ApiResult.ofSuccess(resMap);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return ApiResult.ofFailServer("查询失败:" + e.getMessage());
        }
    }

    /**
     * 保存角色授权
     */
    @PostMapping(value = "/saveRolePermission")
    public ApiResult saveRolePermission(@RequestBody JsonNode json) {
        try {
            String roleId = json.get("roleId").asText();
            String permissionIds = json.get("permissionIds").asText();
            this.sysRolePermissionService.saveRolePermission(roleId, permissionIds);
            return ApiResult.ofSuccess(null);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return ApiResult.ofFailServer("授权失败！");
        }
    }
}
