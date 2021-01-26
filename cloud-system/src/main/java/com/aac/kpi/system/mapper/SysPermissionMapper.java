package com.aac.kpi.system.mapper;

import com.aac.kpi.system.entity.SysPermission;
import com.aac.kpi.system.model.tree.TreeModel;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface SysPermissionMapper extends BaseMapper<SysPermission> {

    /**
     * 根据用户查询用户权限
     */
    public List<SysPermission> queryByUser(@Param("username") String username);

    public List<TreeModel> queryListByParentId(@Param("parentId") String parentId);

}
