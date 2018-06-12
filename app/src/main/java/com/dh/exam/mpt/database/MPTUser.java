package com.dh.exam.mpt.database;

import cn.bmob.v3.BmobUser;
import cn.bmob.v3.datatype.BmobFile;

/**
 *MPTUser 用户类，关联后台数据库_User表
 *
 *@author DavidHuang  at 下午3:37 18-5-31
 */
public class MPTUser extends BmobUser {
	private static final long serialVersionUID = 1L;
	private BmobFile headImg;//用户头像

	public MPTUser() {
	}

	public MPTUser(BmobFile headImg) {
		super();
		this.headImg = headImg;
	}

	public BmobFile getHeadImg() {
		return headImg;
	}

	public void setHeadImg(BmobFile headImg) {
		this.headImg = headImg;
	}


}
