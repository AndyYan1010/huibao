package com.example.win7.huibao.entity;


public class TaskEntry {
    String id;
    String FTime;//启日期
    String FTime1;//止日期
    String FBase4;//责任人
    String FBase15;//制单人
    String FBase;//计划预算进度
    String FNOTE;//备注
    String FBase10;//往来
    String FBase1;//内容
    String FBase2;//计量
    Double FDecimal;//数量
    Double FDecimal1;//单价
    Double FAmount2;//金额含税
    Double FAmount3;//人民币不含税
    Double FDecimal2;//辅量
    String FText;//发送消息
    String FText1;//回馈消息
    String FBase14;//评分
    String FBase5;//消息+接受1
    String FBase6;//消息+接受2
    String FBase7;//消息+接受3
    String FBase8;//消息+接受4
    String FBase9;//消息+接受5
    int    FCheckBox1;//消息+确认1
    int    FCheckBox2;//消息+确认2
    int    FCheckBox3;//消息+确认3
    int    FCheckBox4;//消息+确认4
    int    FCheckBox5;//消息+确认5
    private String mBitUrlList;//每个子表的图片地址

    public String getBitUrl() {
        return mBitUrlList;
    }

    public void setBitUrl(String bitUrlList) {
        this.mBitUrlList = bitUrlList;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getFTime() {
        return FTime;
    }

    public void setFTime(String FTime) {
        this.FTime = FTime;
    }

    public String getFTime1() {
        return FTime1;
    }

    public void setFTime1(String FTime1) {
        this.FTime1 = FTime1;
    }

    public String getFBase4() {
        return FBase4;
    }

    public void setFBase4(String FBase4) {
        this.FBase4 = FBase4;
    }

    public String getFBase15() {
        return FBase15;
    }

    public void setFBase15(String FBase15) {
        this.FBase15 = FBase15;
    }

    public String getFBase() {
        return FBase;
    }

    public void setFBase(String FBase) {
        this.FBase = FBase;
    }

    public String getFNOTE() {
        return FNOTE;
    }

    public void setFNOTE(String FNOTE) {
        this.FNOTE = FNOTE;
    }

    public String getFBase10() {
        return FBase10;
    }

    public void setFBase10(String FBase10) {
        this.FBase10 = FBase10;
    }

    public String getFBase1() {
        return FBase1;
    }

    public void setFBase1(String FBase1) {
        this.FBase1 = FBase1;
    }

    public String getFBase2() {
        return FBase2;
    }

    public void setFBase2(String FBase2) {
        this.FBase2 = FBase2;
    }

    public Double getFDecimal() {
        return FDecimal;
    }

    public void setFDecimal(Double FDecimal) {
        this.FDecimal = FDecimal;
    }

    public Double getFDecimal1() {
        return FDecimal1;
    }

    public void setFDecimal1(Double FDecimal1) {
        this.FDecimal1 = FDecimal1;
    }

    public Double getFAmount2() {
        return FAmount2;
    }

    public void setFAmount2(Double FAmount2) {
        this.FAmount2 = FAmount2;
    }

    public Double getFAmount3() {
        return FAmount3;
    }

    public void setFAmount3(Double FAmount3) {
        this.FAmount3 = FAmount3;
    }

    public Double getFDecimal2() {
        return FDecimal2;
    }

    public void setFDecimal2(Double FDecimal2) {
        this.FDecimal2 = FDecimal2;
    }

    public String getFText() {
        return FText;
    }

    public void setFText(String FText) {
        this.FText = FText;
    }

    public String getFText1() {
        return FText1;
    }

    public void setFText1(String FText1) {
        this.FText1 = FText1;
    }

    public String getFBase14() {
        return FBase14;
    }

    public void setFBase14(String FBase14) {
        this.FBase14 = FBase14;
    }

    public String getFBase5() {
        return FBase5;
    }

    public void setFBase5(String FBase5) {
        this.FBase5 = FBase5;
    }

    public String getFBase6() {
        return FBase6;
    }

    public void setFBase6(String FBase6) {
        this.FBase6 = FBase6;
    }

    public String getFBase7() {
        return FBase7;
    }

    public void setFBase7(String FBase7) {
        this.FBase7 = FBase7;
    }

    public String getFBase8() {
        return FBase8;
    }

    public void setFBase8(String FBase8) {
        this.FBase8 = FBase8;
    }

    public String getFBase9() {
        return FBase9;
    }

    public void setFBase9(String FBase9) {
        this.FBase9 = FBase9;
    }

    public int getFCheckBox1() {
        return FCheckBox1;
    }

    public void setFCheckBox1(int FCheckBox1) {
        this.FCheckBox1 = FCheckBox1;
    }

    public int getFCheckBox2() {
        return FCheckBox2;
    }

    public void setFCheckBox2(int FCheckBox2) {
        this.FCheckBox2 = FCheckBox2;
    }

    public int getFCheckBox3() {
        return FCheckBox3;
    }

    public void setFCheckBox3(int FCheckBox3) {
        this.FCheckBox3 = FCheckBox3;
    }

    public int getFCheckBox4() {
        return FCheckBox4;
    }

    public void setFCheckBox4(int FCheckBox4) {
        this.FCheckBox4 = FCheckBox4;
    }

    public int getFCheckBox5() {
        return FCheckBox5;
    }

    public void setFCheckBox5(int FCheckBox5) {
        this.FCheckBox5 = FCheckBox5;
    }
}
