package com.aptl;

/**
 * @author Erik Hellman
 */
public interface Constants {
    public static final String NETWORK_PREFIX = "network.";
    public static final String UI_PREFIX = "ui.";
    public static final String NETWORK_RETRY_COUNT
            = NETWORK_PREFIX + "retryCount";
    public static final String NETWORK_CONNECTION_TIMEOUT
            = NETWORK_PREFIX + "connectionTimeout";
    public static final String NETWORK_WIFI_ONLY
            = NETWORK_PREFIX + "wifiOnly";
    public static final String UI_BACKGROUND_COLOR
            = UI_PREFIX + "backgroundColor";
    public static final String UI_FOREGROUND_COLOR
            = UI_PREFIX + "foregroundColor";
    public static final String UI_SORT_ORDER
            = UI_PREFIX + "sortOrder";
    public static final int SORT_ORDER_NAME = 10;
    public static final int SORT_ORDER_AGE = 20;
    public static final int SORT_ORDER_CITY = 30;
}
