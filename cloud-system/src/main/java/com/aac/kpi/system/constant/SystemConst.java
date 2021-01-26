package com.aac.kpi.system.constant;

import org.omg.PortableInterceptor.Interceptor;

public interface SystemConst {

    /**
     * 0：一级菜单
     */
    Integer PERMISSION_TYPE_MENU = 0;
    /**
     * 1：子菜单
     */
    Integer PERMISSION_TYPE_SUBMENU = 1;
    /**
     * 2：按钮权限
     */
    Integer PERMISSION_TYPE_BUTTON = 2;

    /**
     * 1：用户状态-正常
     */
    Integer USER_STATUR_NORMAL = 1;

    /**
     * 2：用户状态-冻结
     */
    Integer USER_STATUR_FROZEN = 2;

    /**
     * 普通用户的角色编码，需要跟数据库一致
     */
    String ROLE_CODE_USER = "user";

    /**
     * 默认密码
     */
    String DEFAULT_PASSWORD = "123456!a";
}
