package com.lunatk.alisa.network;

/**
 * Created by LunaTK on 2018. 1. 18..
 */

public class OPCode {
    public static final byte OK = 0x00, NOK = 0x01, REQ_LOGIN = 0x02, REQ_REGISTER = 0x03, SENSOR_DATA = 0x04;
    public static final byte ERR = (byte)0xF0, ERR_USER_EXIST = (byte)0xF1;
}