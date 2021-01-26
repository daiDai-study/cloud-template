package com.aac.kpi.system.controller;


import cn.hutool.core.util.StrUtil;
import com.aac.kpi.common.model.ApiResult;
import com.aac.kpi.system.entity.SysDict;
import com.aac.kpi.common.model.DictModel;
import com.aac.kpi.system.model.tree.SysDictTree;
import com.aac.kpi.system.service.SysDictService;
import com.aac.kpi.system.service.SysUserService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/dict")
@Slf4j
public class SysDictController extends BaseManagementController<SysDict, SysDictService> {

    @Resource
    public void setS(SysDictService service){
        this.service = service;
    }

    @Resource
    public void setSysUserService(SysUserService sysUserService){
        this.sysUserService = sysUserService;
    }

    /**
     * 获取树形字典数据
     */
    @SuppressWarnings("unchecked")
    @GetMapping(value = "/treeList")
    public ApiResult treeList(SysDict sysDict,
                              @RequestParam(name = "pageNo", defaultValue = "1") Integer pageNo,
                              @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize,
                              HttpServletRequest req) {
        LambdaQueryWrapper<SysDict> query = new LambdaQueryWrapper<>();
        // 构造查询条件
        String dictName = sysDict.getDictName();
        if (StrUtil.isNotEmpty(dictName)) {
            query.like(true, SysDict::getDictName, dictName);
        }
        query.eq(true, SysDict::getDelFlag, false);
        query.orderByDesc(true, SysDict::getCreateTime);
        List<SysDict> list = service.list(query);
        List<SysDictTree> treeList = new ArrayList<>();
        for (SysDict node : list) {
            treeList.add(new SysDictTree(node));
        }
        return ApiResult.ofSuccess(treeList);
    }

    /**
     * 获取字典数据
     */
    @GetMapping(value = "/getDictItems/{dictCode}")
    public ApiResult getDictItems(@PathVariable String dictCode) {
        log.info(" dictCode : " + dictCode);
        List<DictModel> ls = null;
        try {
            if (dictCode.contains(",")) {
                //关联表字典（举例：sys_user,realname,id）
                String[] params = dictCode.split(",");

                if (params.length < 3) {
                    return ApiResult.ofFailServer("字典Code格式不正确！");
                }
                //SQL注入校验（只限制非法串改数据库）
                final String[] sqlInjCheck = {params[0], params[1], params[2]};
//                SqlInjectionUtil.filterContent(sqlInjCheck);

                if (params.length == 4) {
                    //SQL注入校验（查询条件SQL 特殊check，此方法仅供此处使用）
//                    SqlInjectionUtil.specialFilterContent(params[3]);
                    ls = service.queryTableDictItemsByCodeAndFilter(params[0], params[1], params[2], params[3]);
                } else if (params.length == 3) {
                    ls = service.queryTableDictItemsByCode(params[0], params[1], params[2]);
                } else {
                    return ApiResult.ofFailServer("字典Code格式不正确！");
                }
            } else {
                //字典表
                ls = service.queryDictItemsByCode(dictCode);
            }

            return ApiResult.ofSuccess(ls);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return ApiResult.ofFailServer("操作失败");
        }
    }

    /**
     * 获取字典数据
     */
    @GetMapping(value = "/getDictItemsDirectly/{dictCode}")
    public List<DictModel> getDictItemsDirectly(@PathVariable String dictCode) {
        log.info(" dictCode : " + dictCode);
        List<DictModel> ls = new ArrayList<>();
        try {
            if (dictCode.contains(",")) {
                //关联表字典（举例：sys_user,realname,id）
                String[] params = dictCode.split(",");

                if (params.length < 3) {
                    return ls;
                }
                //SQL注入校验（只限制非法串改数据库）
                final String[] sqlInjCheck = {params[0], params[1], params[2]};
//                SqlInjectionUtil.filterContent(sqlInjCheck);

                if (params.length == 4) {
                    //SQL注入校验（查询条件SQL 特殊check，此方法仅供此处使用）
//                    SqlInjectionUtil.specialFilterContent(params[3]);
                    ls = service.queryTableDictItemsByCodeAndFilter(params[0], params[1], params[2], params[3]);
                } else if (params.length == 3) {
                    ls = service.queryTableDictItemsByCode(params[0], params[1], params[2]);
                }
            } else {
                //字典表
                ls = service.queryDictItemsByCode(dictCode);
            }

            return ls;
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return ls;
        }
    }

    /**
     * 获取字典数据
     *
     * @param dictCode
     * @return
     */
    @GetMapping(value = "/getDictText/{dictCode}/{key}")
    public ApiResult getDictItems(@PathVariable("dictCode") String dictCode, @PathVariable("key") String key) {
        log.info(" dictCode : " + dictCode);
        String text = null;
        try {
            text = service.queryDictTextByKey(dictCode, key);
            return ApiResult.ofSuccess(text);
        } catch (Exception e) {
            log.info(e.getMessage(), e);
            return ApiResult.ofFailServer("操作失败");
        }
    }
}
