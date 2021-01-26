package com.aac.kpi.projectmanage.constant;

import okhttp3.MediaType;

/**
 * @author carlkong
 */
public interface CommonConstant {

    /**
     * 未删除
     */
    Integer DEL_FLAG_0 = 0;
    /**
     * 已删除
     */
    Integer DEL_FLAG_1 = 1;

    /**
     * 字典翻译文本后缀
     */
    String DICT_TEXT_SUFFIX = "_dictText";

    /**
     * 系统日志类型： 登录日志
     */
    int LOG_TYPE_LOGIN = 1;
    /**
     * 系统日志类型： 操作日志
     */
    int LOG_TYPE_OPRT = 0;
    /**
     * 系统日志类型：定时任务日志
     */
    int LOG_TYPE_TIMMER = 2;

    /**
     * 0：一级菜单
     */
    Integer MENU_TYPE_0 = 0;
    /**
     * 1：子菜单
     */
    Integer MENU_TYPE_1 = 1;
    /**
     * 2：按钮权限
     */
    Integer MENU_TYPE_2 = 2;
    String PREFIX_USER_PERMISSION = "PREFIX_USER_PERMISSION ";
    String PREFIX_USER_ROLE = "PREFIX_USER_ROLE";
    String PREFIX_USER_TOKEN = "PREFIX_USER_TOKEN ";

    /**通告对象类型（USER:指定用户，ALL:全体用户）*/
    String MSG_TYPE_UESR  = "USER";
    String MSG_TYPE_ALL  = "ALL";

    /**发布状态（0未发布，1已发布，2已撤销）*/
    String PUBLISH_STATUS_NO_SEND  = "0";
    String PUBLISH_STATUS_HAS_SEND  = "1";
    String PUBLISH_STATUS_HAS_CANCLE  = "2";

    /**阅读状态（0未读，1已读）*/
    String HAS_READ_FLAG  = "1";
    String NO_READ_FLAG  = "0";

    /**
     * {@code 200 OK} (HTTP/1.0 - RFC 1945)
     */
    Integer SC_OK_200 = 200;
    /**
     * {@code 500 Server Error} (HTTP/1.0 - RFC 1945)
     */
    Integer SC_INTERNAL_SERVER_ERROR_500 = 500;
    /**
     * 访问权限认证未通过 510
     */
    Integer SC_JEECG_NO_AUTHZ = 510;
    /**
     * 访问http接口的json参数类型
     */
    MediaType JSON_TYPE = MediaType.parse("application/json; charset=utf-8");

    /**
     * 禁用状态
     */
    Integer STATUS_DISABLE = -1;
    /**
     * 正常状态
     */
    Integer STATUS_NORMAL = 0;

    /**
     * token 过期时间 单位小时
     */
    int TOKEN_EXPIRE_TIME = 12;
}
