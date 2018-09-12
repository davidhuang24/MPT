package com.dh.exam.mpt.entity;

import cn.bmob.v3.BmobObject;

public class Feedback extends BmobObject {
    private String contact;//联系方式
    private String content;//反馈内容
    private MPTUser user;

    public Feedback() {
    }

    public Feedback(String contact, String content, MPTUser user) {
        this.contact = contact;
        this.content = content;
        this.user = user;
    }

    public MPTUser getUser() {
        return user;
    }

    public void setUser(MPTUser user) {
        this.user = user;
    }

    public String getContact() {
        return contact;
    }

    public void setContact(String contact) {
        this.contact = contact;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
