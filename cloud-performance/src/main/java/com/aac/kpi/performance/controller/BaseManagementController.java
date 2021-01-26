package com.aac.kpi.performance.controller;

import cn.hutool.core.util.StrUtil;
import com.aac.kpi.common.model.ApiResult;
import com.aac.kpi.performance.entity.BaseEntity;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;
import java.util.List;

public abstract class BaseManagementController<T extends BaseEntity, S extends IService<T>> {

    protected S service;

    @PostMapping(value = "/add")
    public ApiResult add(@RequestBody T model) {
        if (service.save(model)) {
            return ApiResult.ofSuccess(model);
        }
        return ApiResult.ofFailServer(model);
    }

    @PostMapping(value = "/addBatch")
    public ApiResult addBatch(@RequestBody List<T> modelList) {
        if (service.saveBatch(modelList)) {
            return ApiResult.ofSuccess(modelList);
        }
        return ApiResult.ofFailServer(modelList);
    }

    @DeleteMapping(value = "/delete")
    public ApiResult delete(@RequestParam(name = "id") String id) {
        T model = service.getById(id);
        if (model == null) {
            return ApiResult.ofFailClientNotFound();
        } else {
            if (service.removeById(model)) {
                return ApiResult.ofSuccess();
            }
            return ApiResult.ofFailServer(id);
        }
    }

    @DeleteMapping(value = "/deleteBatch")
    public ApiResult deleteBatch(@RequestParam(name = "ids") String ids) {
        if (ids == null || "".equals(ids.trim())) {
            return ApiResult.ofFailServer("参数不识别");
        } else {
            if (service.removeByIds(Arrays.asList(ids.split(",")))) {
                return ApiResult.ofSuccess();
            }
            return ApiResult.ofFailServer(ids);
        }
    }

    @PutMapping(value = "/edit")
    public ApiResult edit(@RequestBody T model) {
        T sysDictServiceById = service.getById(model.getId());
        if (sysDictServiceById == null) {
            return ApiResult.ofFailClientNotFound();
        } else {
            if (service.updateById(model)) {
                return ApiResult.ofSuccess(model);
            }
            return ApiResult.ofFailServer(model);
        }
    }

    @GetMapping(value = "/getById")
    public ApiResult getById(@RequestParam(name = "id") String id) {
        T model = service.getById(id);
        if (model == null) {
            return ApiResult.ofFailClientNotFound();
        }
        return ApiResult.ofSuccess(model);
    }

    /**
     * 分页查询
     */
    @GetMapping(value = "/pageList")
    public ApiResult pageList(T model,
                              @RequestParam(name = "pageNo", defaultValue = "1") Integer pageNo,
                              @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize,
                              HttpServletRequest req) {
        QueryWrapper<T> queryWrapper = new QueryWrapper<>(model);
        Page<T> page = new Page<>(pageNo, pageSize);
        //排序逻辑 处理
        String column = req.getParameter("column");
        String order = req.getParameter("order");
        if (StrUtil.isNotEmpty(column) && StrUtil.isNotEmpty(order)) {
            column = StrUtil.toUnderlineCase(column);
            if ("asc".equals(order)) {
                queryWrapper.orderByAsc(column);
            } else {
                queryWrapper.orderByDesc(column);
            }
        }
        IPage<T> pageList = service.page(page, queryWrapper);
        return ApiResult.ofSuccess(pageList);
    }


}
