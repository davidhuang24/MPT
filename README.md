# 1.项目简介部分

## 1.1项目简介

&emsp;&emsp;《MakePaperTest》是一款出题制作试卷并且能进行考试判卷的APP，旨在为老师、教育机构、学生等用户提供较为方便的出题考试服务。 后台使用的是Bmob。这个demo是作者学完Android基础知识后的第一个较为完整的小项目，主要目的是练手和熟悉Bmob SDK以及数据库，目前Bmob Android SDK开发文档的大部分要点已在本demo中体现。这就是一个新手的第一个demo，可能还有一些bug，请佛系参考学习，欢迎反馈交流，大神请绕道！

## 1.2项目功能详细介绍

1. 基于Bmob SDK的一系列账户功能实现，包括：账户注册登录、手机号一键登录、请求验证码、重置密码、修改用户名、修改手机号、解绑手机号、绑定手机号等；
2. 更换头像：显示头像-选择照片（拍照）-裁剪照片（UCrop支持圆形方形裁剪、旋转、缩放）-保存上传头像；
3. 第一次启动app显示滑动slider介绍页，非第一次启动倒计时进入应用；
4. 主页题库使用RecyclerView，支持上拉刷新、下拉加载；
5. 出题制卷、在线考试、提交判卷、错题查看、考试记录查看；
6. 多种缓存策略；
7. UI:滑动菜单，沉浸式状态栏、MaterialDesign；
8. 用户反馈。

## 1.4 项目配置

修改AppKey为你自己的Bmob Application ID:
Constat.DEFAULT_HEAD_IMG_URL也要修改成你自己的。

```java
public class ConStant {
    //Bmob Application ID
    public static final String BMOB_APP_KEY="your application id";
    public static final String DEFAULT_HEAD_IMG_URL ="your url";
}
```

## 1.5项目依赖库

build.gradle(project)

```java
    repositories {
        google()
        jcenter()
        //Bmob的maven仓库地址--必填
        maven { url "https://raw.github.com/bmob/bmob-android-sdk/master" }
        //ucrop的maven仓库地址
        maven { url "https://jitpack.io" }
    }
}
```

build.gradle(APP)

```java
    implementation 'cn.bmob.android:bmob-sdk:3.6.0'
    implementation 'com.daimajia.slider:library:1.1.5@aar'
    implementation('com.github.yalantis:ucrop:2.2.2')
            { exclude group: 'com.squareup.okhttp3' }//解决okhttp3依赖冲突bug
```

## 1.6项目运行截图

<center class="capture1">
    <img src="http://pg0dgjunx.bkt.clouddn.com/%E5%9B%BE%E7%89%872.png" width="200" height="355"/>
    <img src="http://pg0dgjunx.bkt.clouddn.com/WechatIMG10.jpeg" width="200" height="355"//>
    <img src="http://pg0dgjunx.bkt.clouddn.com/%E5%9B%BE%E7%89%871.png" width="200" height="355"//>
</center>
<center class="capture2">
    <img src="http://pg0dgjunx.bkt.clouddn.com/WechatIMG8.jpeg" width="200" height="355"//>
    <img src="http://pg0dgjunx.bkt.clouddn.com/%E5%9B%BE%E7%89%873.png" width="200" height="355"//>
    <img src="http://pg0dgjunx.bkt.clouddn.com/%E5%9B%BE%E7%89%874.png" width="200" height="355"//>
</center>

