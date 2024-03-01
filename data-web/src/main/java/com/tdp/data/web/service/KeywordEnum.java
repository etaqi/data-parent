package com.tdp.data.web.service;

public enum KeywordEnum {
    ZONGHE(1,"综合"),
    DAOHANG(2,"导航"),
    CAIPIAO(3,"彩票"),
    QIPAI(4,"棋牌"),
    BUYU(5,"捕鱼"),
    DIANZI(6,"电子"),
    TIYU(7,"体育"),
    LIUHE(8,"六合"),
    HUANGWANG(9,"黄网"),
    WEIZHI(10,"未知"),
    BOOK(11, "图书"),
    CHUANQI(12, "传奇"),
    HMAN(13, "H漫");

    public int index;
    public String name;
    KeywordEnum(int index, String name){
        this.index = index;
        this.name = name;
    }
}
