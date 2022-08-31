package com.icuxika.everything.warp.exchange.trading.engine.assets;

public enum Transfer {

    /**
     * 可用转可用
     */
    AVAILABLE_TO_AVAILABLE,

    /**
     * 可用转冻结
     */
    AVAILABLE_TO_FROZEN,

    /**
     * 冻结转可用
     */
    FROZEN_TO_AVAILABLE;
}
