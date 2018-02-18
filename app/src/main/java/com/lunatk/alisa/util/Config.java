package com.lunatk.alisa.util;

/**
 * Created by LunaTK on 2018. 1. 19..
 */

public class Config {

    public static final String IP = "192.168.0.24";
//        public static final String IP = "115.145.177.64";
    public static final int PORT = 9949;

    public static final int FLUSH_REQUEST_PERIOD = 10 * 1000;//milli second
    public static final int BLE_SCAN_PERIOD = 5 * 1000; //스캔 주기
    public static final int BLE_SCAN_TIME = 5 * 1000; // 스캔 진행 시간
    public static final int NETWORK_UNREACHABLE_SLEEP_PERIOD = 10*1000;
    public static final int SOCKET_TIMEOUT = 5 * 1000;
}
