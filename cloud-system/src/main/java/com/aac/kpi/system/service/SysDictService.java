package com.aac.kpi.system.service;

import com.aac.kpi.system.entity.SysDict;
import com.aac.kpi.system.entity.SysDictItem;
import com.aac.kpi.common.model.DictModel;
import com.aac.kpi.system.model.tree.TreeSelectModel;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

public interface SysDictService extends IService<SysDict> {
    /**
     * 查询所有部门 作为字典信息 id -->value,departName -->text
     *
     * @return
     */
    List<DictModel> queryAllDepartBackDictModel();

    /**
     * 查询所有用户  作为字典信息 username -->value,realname -->text
     *
     * @return
     */
    List<DictModel> queryAllUserBackDictModel();

    List<DictModel> queryDictItemsByCode(String code);

    String queryDictTextByKey(String code, String key);

    /**
     * 通过关键字查询字典表
     *
     * @param table
     * @param text
     * @param code
     * @param keyword
     * @return
     */
    List<DictModel> queryTableDictItems(String table, String text, String code, String keyword);

    List<DictModel> queryTableDictItemsByCode(String table, String text, String code);

    List<DictModel> queryTableDictItemsByCodeAndFilter(String table, String text, String code, String filterSql);

    String queryTableDictTextByKey(String table, String text, String code, String key);

    /**
     * 根据表名、显示字段名、存储字段名 查询树
     *
     * @param table
     * @param text
     * @param code
     * @param pidField
     * @param pid
     * @param hasChildField
     * @return
     */
    List<TreeSelectModel> queryTreeList(String table, String text, String code, String pidField, String pid, String hasChildField);

    /**
     * 添加一对多
     */
    void saveMain(SysDict sysDict, List<SysDictItem> sysDictItemList);

    /**
     * 根据字典code，获取数据
     * @param dictCode
     * @return
     */
    SysDict getByDictCode(String dictCode);
}
