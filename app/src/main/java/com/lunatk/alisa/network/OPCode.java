package com.lunatk.alisa.network;

/**
 * Created by LunaTK on 2018. 1. 18..
 */

public class OPCode {
    //네트워킹용 OPCode
    public static final byte ERR = (byte)0xFF, OK = 0x01, NOK = 0x02;
    public static final byte REQ_LOGIN = 0x03, REP_LOGIN = 0x04, REQ_REGISTER = 0x05, REP_REGISTER = 0x06;
    public static final byte NEW_SENSOR_DATA = 0x07, REQ_SENSOR_HISTORY = 0x08, REP_SENSOR_HISTORY = 0x09,
            NEW_GPS_DATA = 0x0a, REQ_GPS_HISTORY = 0x0b, REP_GPS_HISTORY = 0x0c,
            UPDATE_MILEAGE = 0x0d, REQ_MILEAGE = 0x0e, REP_MILEAGE = 0x0f,
            UPDATE_AC = 0x10, REQ_AC = 0x11, REP_AC = 0x12,
            UPDATE_DC = 0x13, REQ_DC = 0x13, REP_DC = 0x14,
            REQ_AC_STATISTIC = 0x15, REP_AC_STATISTIC = 0x16,
            REQ_DC_STATISTIC = 0x17, REP_DC_STATISTIC = 0x18,
            UPDATE_FILTER_THR = 0x19, REQ_FILTER_THR = 0x1a, REP_FILTER_THR = 0x1b,
            REQ_FILTER_STATISTIC = 0x1c, REP_FILTER_STATISTIC = 0x1d,
            UPDATE_COMP_MILEAGE = 0x1e, REQ_COMP_MILEAGE = 0x1f, REP_COMP_MILEAGE = 0x20,
            REQ_COMP_STATISTIC = 0x21, REP_COMP_STATISTIC = 0x22,
            REQ_LOGOUT = 0x23, REP_LOGOUT = 0x24,
            NEW_EVENT_DATA = 0x25, REQ_EVENT_DATA = 0x26, REP_EVENT_DATA = 0x27;
    public static final byte ERR_USER_EXIST = (byte)0xF1;

    //어플내 브로드캐스팅용 OPCode
    public static final byte LOGIN_RESULT = 0x00, REGISTER_RESULT = 0x01, SENSOR_RESULT = 0x02;
}
