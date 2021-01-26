package com.aac.kpi.system.mapper;

import com.aac.kpi.system.entity.SysDict;
import com.aac.kpi.common.model.DictModel;
import com.aac.kpi.system.model.vo.DuplicateCheckModel;
import com.aac.kpi.system.model.tree.TreeSelectModel;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface SysDictMapper extends BaseMapper<SysDict> {

    /**
     * 指定表中是否已有指定字段为指定值且 id 为指定数据ID 的记录
     */
    Long duplicateCheckCountSql(DuplicateCheckModel duplicateCheckModel);

    /**
     * 指定表中是否已有指定字段为指定值的记录
     */
    Long duplicateCheckCountSqlNoDataId(DuplicateCheckModel duplicateCheckModel);

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

    List<DictModel> queryDictItemsByCode(@Param("code") String code);

    String queryDictTextByKey(@Param("code") String code, @Param("key") String key);

    /**
     * 通过关键字查询出字典表
     *
     * @param table
     * @param text
     * @param code
     * @param keyword
     * @return
     */
    List<DictModel> queryTableDictItems(@Param("table") String table, @Param("text") String text, @Param("code") String code, @Param("keyword") String keyword);

    List<DictModel> queryTableDictItemsByCode(@Param("table") String table, @Param("text") String text, @Param("code") String code);

    List<DictModel> queryTableDictItemsByCodeAndFilter(@Param("table") String table, @Param("text") String text, @Param("code") String code, @Param("filterSql") String filterSql);

    String queryTableDictTextByKey(@Param("table") String table, @Param("text") String text, @Param("code") String code, @Param("key") String key);

    /**
     * 根据表名、显示字段名、存储字段名 查询树
     *
     * @param table
     * @param text
     * @param code
     * @param pid
     * @param hasChildField
     * @return
     */
    List<TreeSelectModel> queryTreeList(@Param("table") String table, @Param("text") String text, @Param("code") String code, @Param("pidField") String pidField, @Param("pid") String pid, @Param("hasChildField") String hasChildField);

}
