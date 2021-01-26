package com.aac.kpi.performance.constant;

public interface PerformanceConst {

    /**
     * 未确认或待确认状态
     */
    Integer CONFIRMED_STATUS_WAIT = 0;

    /**
     * 确认通过或接受状态
     */
    Integer CONFIRMED_STATUS_ACCEPT = 1;

    /**
     * 驳回或拒绝状态
     */
    Integer CONFIRMED_STATUS_REJECT = -1;
}
