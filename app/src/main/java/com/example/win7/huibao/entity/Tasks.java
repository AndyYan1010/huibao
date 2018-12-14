package com.example.win7.huibao.entity;

import java.util.List;

public class Tasks {
    String          finterid;
    String          fbillno;
    String          FBase3;//币别
    Double          FAmount4;//汇率
    String          FBase11;//组织机构
    String          FBase12;//区域部门
    String          FBase16;//责任部门
    String          FBase13;//制度所属部门
    String          FNote1;//制度操作细则
    String          contentid;
    String          FBase17;
    String          content;
    String          sup;
    List<TaskEntry> entryList;//字表

    public String getFinterid() {
        return finterid;
    }

    public void setFinterid(String finterid) {
        this.finterid = finterid;
    }

    public String getFbillno() {
        return fbillno;
    }

    public void setFbillno(String fbillno) {
        this.fbillno = fbillno;
    }

    public String getFBase3() {
        return FBase3;
    }

    public void setFBase3(String FBase3) {
        this.FBase3 = FBase3;
    }

    public Double getFAmount4() {
        return FAmount4;
    }

    public void setFAmount4(Double FAmount4) {
        this.FAmount4 = FAmount4;
    }

    public String getFBase11() {
        return FBase11;
    }

    public void setFBase11(String FBase11) {
        this.FBase11 = FBase11;
    }

    public String getFBase12() {
        return FBase12;
    }

    public void setFBase12(String FBase12) {
        this.FBase12 = FBase12;
    }

    public String getFBase16() {
        return FBase16;
    }

    public void setFBase16(String FBase16) {
        this.FBase16 = FBase16;
    }

    public String getFBase13() {
        return FBase13;
    }

    public void setFBase13(String FBase13) {
        this.FBase13 = FBase13;
    }

    public String getFNote1() {
        return FNote1;
    }

    public void setFNote1(String FNote1) {
        this.FNote1 = FNote1;
    }

    public String getContentid() {
        return contentid;
    }

    public void setContentid(String contentid) {
        this.contentid = contentid;
    }

    public String getFBase17() {
        return FBase17;
    }

    public void setFBase17(String FBase17) {
        this.FBase17 = FBase17;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getSup() {
        return sup;
    }

    public void setSup(String sup) {
        this.sup = sup;
    }

    public List<TaskEntry> getEntryList() {
        return entryList;
    }

    public void setEntryList(List<TaskEntry> entryList) {
        this.entryList = entryList;
    }
}