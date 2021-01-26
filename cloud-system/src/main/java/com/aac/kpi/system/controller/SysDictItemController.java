package com.aac.kpi.system.controller;


import com.aac.kpi.system.entity.SysDictItem;
import com.aac.kpi.system.service.SysDictItemService;
import com.aac.kpi.system.service.SysDictService;
import com.aac.kpi.system.service.SysUserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

@RestController
@RequestMapping("/dict/item")
@Slf4j
public class SysDictItemController extends BaseManagementController<SysDictItem, SysDictItemService> {

    @Resource
    public void setS(SysDictItemService service){
        this.service = service;
    }

    @Resource
    public void setSysUserService(SysUserService sysUserService){
        this.sysUserService = sysUserService;
    }
}
