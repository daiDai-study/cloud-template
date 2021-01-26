package com.aac.kpi.system.service.impl;

import com.aac.kpi.system.entity.SysDict;
import com.aac.kpi.system.entity.SysDictItem;
import com.aac.kpi.system.mapper.SysDictMapper;
import com.aac.kpi.system.model.tree.TreeSelectModel;
import com.aac.kpi.common.model.DictModel;
import com.aac.kpi.system.service.SysDictItemService;
import com.aac.kpi.system.service.SysDictService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;

@Service
@Slf4j
@CacheConfig(cacheNames = "dictCache")
public class SysDictServiceImpl extends ServiceImpl<SysDictMapper, SysDict> implements SysDictService {

    @Resource
    private SysDictItemService sysDictItemService;

    /**
     * 通过查询指定 code 获取字典
     */
    @Override
    public List<DictModel> queryDictItemsByCode(String code) {
        return baseMapper.queryDictItemsByCode(code);
    }

    /**
     * 通过查询指定code 获取字典值text
     */

    @Override
    public String queryDictTextByKey(String code, String key) {
        return baseMapper.queryDictTextByKey(code, key);
    }

    /**
     * 通过查询指定table的 text code 获取字典
     * dictTableCache采用redis缓存有效期10分钟
     */
    @Override
    public List<DictModel> queryTableDictItemsByCode(String table, String text, String code) {
        return baseMapper.queryTableDictItemsByCode(table,text,code);
    }

    @Override
    public List<DictModel> queryTableDictItemsByCodeAndFilter(String table, String text, String code, String filterSql) {
        return baseMapper.queryTableDictItemsByCodeAndFilter(table,text,code,filterSql);
    }

    /**
     * 通过查询指定table的 text code 获取字典值text
     * dictTableCache采用redis缓存有效期10分钟
     */
    @Override
    public String queryTableDictTextByKey(String table,String text,String code, String key) {
        return baseMapper.queryTableDictTextByKey(table,text,code,key);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void saveMain(SysDict sysDict, List<SysDictItem> sysDictItemList) {
        baseMapper.insert(sysDict);
        if (sysDictItemList != null) {
            for (SysDictItem entity : sysDictItemList) {
                entity.setDictId(sysDict.getId());
                sysDictItemService.save(entity);
            }
        }
    }

    @Override
    public List<DictModel> queryAllDepartBackDictModel() {
        return baseMapper.queryAllDepartBackDictModel();
    }

    @Override
    public List<DictModel> queryAllUserBackDictModel() {
        return baseMapper.queryAllUserBackDictModel();
    }

    @Override
    public List<DictModel> queryTableDictItems(String table, String text, String code, String keyword) {
        return baseMapper.queryTableDictItems(table, text, code, "%"+keyword+"%");
    }

    @Override
    public List<TreeSelectModel> queryTreeList(String table, String text, String code, String pidField, String pid, String hasChildField) {
        return baseMapper.queryTreeList(table, text, code, pidField, pid,hasChildField);
    }


    @Override
    public SysDict getByDictCode(String dictCode) {
        QueryWrapper<SysDict> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("dict_code", dictCode);
        return this.getOne(queryWrapper);
    }
}
