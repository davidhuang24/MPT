package com.dh.exam.mpt.entity;

import cn.bmob.v3.BmobObject;

/**
 * 得分记录实体
 * @author DavidHuang  at 下午4:08 18-8-5
 */
public class ScoreRecord extends BmobObject {
    private int score;
    private MPTUser user;
    private Paper paper;

    public ScoreRecord() {
    }

    public ScoreRecord(String tableName, int score, MPTUser user, Paper paper) {
        super(tableName);
        this.score = score;
        this.user = user;
        this.paper = paper;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public MPTUser getUser() {
        return user;
    }

    public void setUser(MPTUser user) {
        this.user = user;
    }

    public Paper getPaper() {
        return paper;
    }

    public void setPaper(Paper paper) {
        this.paper = paper;
    }
}
