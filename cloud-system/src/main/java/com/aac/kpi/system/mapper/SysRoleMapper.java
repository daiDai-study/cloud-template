package com.aac.kpi.system.mapper;

import com.aac.kpi.system.entity.SysRole;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.ArrayList;
import java.util.List;


public interface SysRoleMapper extends BaseMapper<SysRole> {

    SysRole getByRoleCode(String roleCode);

    List<SysRole> listByUsername(@Param("username") String username);

    List<SysRole> listByUserId(@Param("userId") String userId);
}
