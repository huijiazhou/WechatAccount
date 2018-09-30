package com.moxi.wechatpay.access;

/**
 * Created by zhou on 2018/3/7.
 */

public class WechatMeg {
    public String message;
    public  ResId result;
    public int errorcode;
    public class ResId {
        public String payId;
        public String numId;
        public String listviewId;
    }
}
