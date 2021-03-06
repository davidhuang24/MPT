package com.dh.exam.mpt.entity;

import cn.bmob.v3.BmobObject;

/**
 *试卷类
 *
 *@author DavidHuang  at 下午3:37 18-5-31
 */
public class Paper extends BmobObject{
    private String paperName;
    private String paperKind;
    private MPTUser paperAuthor;//一对多关系，一个作者多张试卷
    private Integer questionCount;
    private Boolean love;

    public Paper() {
    }

    public Paper(String paperName, String paperKind, MPTUser paperAuthor, Integer questionCount, Boolean love) {
        this.paperName = paperName;
        this.paperKind = paperKind;
        this.paperAuthor = paperAuthor;
        this.questionCount = questionCount;
        this.love = love;
    }

    public String getPaperName() {
        return paperName;
    }

    public void setPaperName(String paperName) {
        this.paperName = paperName;
    }

    public String getPaperKind() {
        return paperKind;
    }

    public void setPaperKind(String paperKind) {
        this.paperKind = paperKind;
    }

    public MPTUser getPaperAuthor() {
        return paperAuthor;
    }

    public void setPaperAuthor(MPTUser paperAuthor) {
        this.paperAuthor = paperAuthor;
    }

    public Integer getQuestionCount() {
        return questionCount;
    }

    public void setQuestionCount(Integer questionCount) {
        this.questionCount = questionCount;
    }

    public Boolean getLove() {
        return love;
    }

    public void setLove(Boolean love) {
        this.love = love;
    }
}
