package com.tdp.data.web.service;

public enum KeywordEnum {
    TIMEOUT(-10,"超时", false),
    FORBID(-5, "禁止", false),
    WHITELIST(0, "白名单", false),
    ZONGHE(1,"综合", true),
    DAOHANG(2,"导航", true),
    CAIPIAO(3,"彩票", true),
    QIPAI(4,"棋牌", true),
    BUYU(5,"捕鱼", true),
    DIANZI(6,"电子", true),
    TIYU(7,"体育", true),
    LIUHE(8,"六合", true),
    HUANGWANG(9,"黄网", true),
    WEIZHI(10,"未知", false),
    BOOK(11, "图书", true),
    CHUANQI(12, "传奇", true),
    HMAN(13, "H漫", true),
    HCITY(14, "同城约炮", true),
    HBOOK(15, "情色小说", true);

    public int index;
    public String name;
    public boolean checked;
    KeywordEnum(int index, String name, boolean checked){
        this.index = index;
        this.name = name;
        this.checked = checked;
    }
}
