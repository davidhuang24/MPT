package com.dh.exam.mpt.activity;

import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.signature.StringSignature;
import com.dh.exam.mpt.CustomView.SheetDialog;
import com.dh.exam.mpt.Utils.ActivityCollector;
import com.dh.exam.mpt.Utils.CacheManager;
import com.dh.exam.mpt.Utils.ConStant;
import com.dh.exam.mpt.Utils.FirstThingListener;
import com.dh.exam.mpt.Utils.NetworkUtil;
import com.dh.exam.mpt.activity.Fragment.NewPaperFragment;
import com.dh.exam.mpt.activity.Fragment.PaperLibraryFragment;
import com.dh.exam.mpt.entity.MPTUser;

import com.dh.exam.mpt.R;

import java.io.File;


import cn.bmob.v3.BmobUser;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.UpdateListener;
import de.hdodenhof.circleimageview.CircleImageView;

public class MainActivity extends BaseActivity implements View.OnClickListener,
        BottomNavigationView.OnNavigationItemSelectedListener,NavigationView.OnNavigationItemSelectedListener{

    private static final String TAG = "MainActivity";

    private DrawerLayout drawerLayout;
    private BottomNavigationView bottomNavigationView;
    private Toolbar toolbar;
    private TextView tv_header_userName;
    private CircleImageView civ_header_userPic;

    private MPTUser currentUser;//当前缓存用户
    private Uri userHeadImgUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        init();
    }

    public void init(){
        replaceFragment(new PaperLibraryFragment(),ConStant.PAPER_LIBRARY_FRAG_TAG);

        bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setOnNavigationItemSelectedListener(this);
        toolbar= findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitle(getString(R.string.fragment_title_paper_library));
        drawerLayout= findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        View headerLayout = navigationView.getHeaderView(0); // 0-index header
        tv_header_userName = headerLayout.findViewById(R.id.username);
        civ_header_userPic = headerLayout.findViewById(R.id.user_image);
        tv_header_userName.setOnClickListener(this);
        civ_header_userPic.setOnClickListener(this);

        ActionBar actionBar=getSupportActionBar();
        if(actionBar!=null){//设置导航按钮，打开滑动菜单
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeAsUpIndicator(R.drawable.ic_menu);
        }

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        currentUser = BmobUser.getCurrentUser(MPTUser.class);
        if(currentUser==null){//显示用户名
            tv_header_userName.setText(getResources().getString(R.string.drawer_header_username_default));
        }else{
            tv_header_userName.setText(currentUser.getUsername());
        }

        setUserHeadImg();
    }

    /**
     * 更新用户头像
     */
    public void setUserHeadImg(){
        currentUser=BmobUser.getCurrentUser(MPTUser.class);
        File file;
        int headImgType;
        if(currentUser!=null&&currentUser.getHeadImg()!=null){//已登陆并且用户头像不为空，用户头像
            file=new File(CacheManager.DirsExistedOrCreate(ConStant.APP_Public_Dir_ROOT+File.separator+"HeadImages"),
                    currentUser.getHeadImg().getFilename());
            headImgType=1;
        }else{//未登陆或者用户头像为空，默认头像
            file=new File(CacheManager.DirsExistedOrCreate(ConStant.APP_Public_Dir_ROOT+File.separator+"HeadImages"),
                    ConStant.DEFAULT_HEAD_IMG_NAME);
            headImgType=0;
        }

        if(!file.exists()){//文件不存在，写缓存-读缓存-显示头像
            CacheManager.writeHeadImgToCache(headImgType,new FirstThingListener() {
                @Override
                public void done() {
                    showHeadImgFromCache();
                }
                @Override
                public void onError(Exception e) {
                    Toast.makeText(MainActivity.this, "缓存异常", Toast.LENGTH_SHORT).show();
                }
            });
        }else{//文件存在，读缓存-显示头像
            showHeadImgFromCache();
        }
    }

    /**
     * 读缓存-加载头像
     */
    public void showHeadImgFromCache(){
        File imgFile=CacheManager.getHeadImgFromCache();
        String updateTime = String.valueOf(System.currentTimeMillis());
        Glide.with(civ_header_userPic.getContext())
                .load(imgFile)
                //解决了问题-加载同一源但源内容改变后显示图片却不改变
                .signature(new StringSignature(updateTime))
                .into(civ_header_userPic);
        userHeadImgUri=Uri.fromFile(imgFile);
    }

    @Override
    public void onClick(View v) {
        currentUser=BmobUser.getCurrentUser(MPTUser.class);
        switch (v.getId()){
            case R.id.fab   :
                fabAction();
                break;
            case R.id.username  ://登陆
                if(currentUser==null){
                    LoginActivity.activityStart(MainActivity.this,LoginActivity.class,
                            null,null,null);
                }
                break;
            case R.id.user_image   ://设置头像
                if(currentUser!=null){
                    if(!NetworkUtil.isNetworkAvailable()){
                        Toast.makeText(MainActivity.this, "网络不可用,请连接网络后再操作！", Toast.LENGTH_SHORT).show();
                        return;
                    }else{
                        UserImageActivity.activityStart(MainActivity.this,
                                UserImageActivity.class,null,null,userHeadImgUri);
                    }

                }else{//未登陆
                    LoginActivity.activityStart(MainActivity.this,LoginActivity.class,
                            null,null,null);
                }
                break;
                default:
        }
    }

    public void fabAction(){
        if(bottomNavigationView.getSelectedItemId()==R.id.navigation_home){
            toolbar.setTitle(getString(R.string.fragment_title_new_paper));
            replaceFragment(new NewPaperFragment(),ConStant.NEW_PAPER_FRAG_TAG);
            bottomNavigationView.setSelectedItemId(R.id.navigation_add);
        }else{
            NewPaperFragment newPaperFragment=(NewPaperFragment)getSupportFragmentManager()
                    .findFragmentByTag(ConStant.NEW_PAPER_FRAG_TAG);
            newPaperFragment.newPaper();
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        currentUser=BmobUser.getCurrentUser(MPTUser.class);
        switch (item.getItemId()) {
            //BottomNavigationView
            case R.id.navigation_home:
                //题库主页
                toolbar.setTitle(getString(R.string.fragment_title_paper_library));
                replaceFragment(new PaperLibraryFragment(),ConStant.PAPER_LIBRARY_FRAG_TAG);
                return true;
            case R.id.navigation_add:
                //新建试卷
                toolbar.setTitle(getString(R.string.fragment_title_new_paper));
                replaceFragment(new NewPaperFragment(),ConStant.NEW_PAPER_FRAG_TAG);
                return true;
            case R.id.navigation_account:
                //打开抽屉菜单
                drawerLayout.openDrawer(GravityCompat.START);//打开抽屉菜单
                return true;
            //NavigationView
            case R.id.nav_account://账户编辑
                if(currentUser!=null){
                    showAccountSheetDialog();
                }else{
                    Toast.makeText(this, "您还未登陆", Toast.LENGTH_SHORT).show();
                }
                return true;
            case R.id.test_record://考试记录
                TestRecordActivity.activityStart(MainActivity.this,TestRecordActivity.class,
                        null,null,null);
                return true;
            case R.id.nav_setting://设置
                showSettingSheetDialog();
                return true;
            case R.id.nav_day_night:
                Toast.makeText(this, "该功能正在开发中，敬请期待...", Toast.LENGTH_SHORT).show();
                return true;
            case R.id.nav_logout:
                logout();
                return true;
            case R.id.nav_exit://退出APP
                ActivityCollector.finishAll();//finish all activities
                android.os.Process.killProcess(android.os.Process.myPid());//kill current process
                return true;
        }
        return false;
    }

    private void replaceFragment(Fragment fragment,String fragmentTag){
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.replace(R.id.fragment_in_main_layout, fragment, fragmentTag);
        transaction.commit();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.user_img_activity_toolbar,menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()){//HomeAsUp按钮
            case android.R.id.home:
                drawerLayout.openDrawer(GravityCompat.START);//打开抽屉菜单
                return true;
        }
        return false;
    }

    /**
     * 设置SheetDialog
     */
    public void showSettingSheetDialog(){
        new SheetDialog.Builder(MainActivity.this).setTitle(getString(R.string.drawer_title_setting))
                .addMenu(getString(R.string.feedback), (dialog, which) -> {
                    dialog.dismiss();
                    if(BmobUser.getCurrentUser(MPTUser.class)==null){
                        Toast.makeText(this, "请先登录", Toast.LENGTH_SHORT).show();
                    }else {
                        FeedbackActivity.activityStart(MainActivity.this,FeedbackActivity.class,
                                null,null,null);
                    }
                })
                .create().show();
    }


    /**
     * 账户编辑SheetDialog
     */
    public void showAccountSheetDialog(){
        currentUser=BmobUser.getCurrentUser(MPTUser.class);
        new SheetDialog.Builder(MainActivity.this).setTitle(getString(R.string.drawer_title_account))
                .addMenu(getString(R.string.update_username), (dialog, which) -> {
                    dialog.dismiss();
                    final EditText username_edit=new EditText(this);
                    username_edit.setText(currentUser.getUsername());
                    new AlertDialog.Builder(this)
                            .setTitle(R.string.update_username)
                            .setView(username_edit)
                            .setPositiveButton(R.string.label_ok, (dialog1, which1) -> {
                                String newUsername=username_edit.getText().toString();
                                updateUsername(newUsername,currentUser);
                            })
                            .setNegativeButton(R.string.label_cancel,null)
                            .show();
                })
                .addMenu(getString(R.string.reset_pwd), (dialog, which) -> {//修改密码
                    dialog.dismiss();
                    ResetPasswordActivity.activityStart(MainActivity.this,ResetPasswordActivity.class
                    ,null,null,null);
                })
                .addMenu(getString(R.string.update_phone_num), (dialog, which) -> {//修改手机号
                    dialog.dismiss();
                    if(currentUser.getMobilePhoneNumber()!=null&&currentUser.getMobilePhoneNumberVerified()){//已经绑定手机号
                        UnbindChangePhoneActivity.activityStart
                                (MainActivity.this,UnbindChangePhoneActivity.class,
                                        "2",null,null);
                    }else {
                        Toast.makeText(this, "请先绑定手机号", Toast.LENGTH_SHORT).show();
                    }
                })
                .addMenu(getString(R.string.unbind_phone), (dialog, which) -> {//解绑手机号
                    dialog.dismiss();
                    if(currentUser.getMobilePhoneNumber()!=null&&currentUser.getMobilePhoneNumberVerified()){//已经绑定手机号
                        UnbindChangePhoneActivity.activityStart
                                (MainActivity.this,UnbindChangePhoneActivity.class,
                                        "1",null,null);
                    }else {
                        Toast.makeText(this, "请先绑定手机号", Toast.LENGTH_SHORT).show();
                    }
                })
                .addMenu(getString(R.string.bind_phone), (dialog, which) -> {//绑定手机号
                    dialog.dismiss();
                    if(currentUser.getMobilePhoneNumber()!=null&&currentUser.getMobilePhoneNumberVerified()){//已经绑定手机号
                        Toast.makeText(this, "您已绑定手机号！", Toast.LENGTH_SHORT).show();
                    }else {
                        BindPhoneActivity.activityStart(MainActivity.this,BindPhoneActivity.class,"1",null,null);
                    }
                })
                .create().show();
    }

    /**
     * 更新用户名
     * @param newUsername
     * @param currentUser
     */
    public void updateUsername(String newUsername,MPTUser currentUser){
        if(!NetworkUtil.isNetworkAvailable()){
            Toast.makeText(MainActivity.this, "网络不可用,请连接网络后再操作！", Toast.LENGTH_SHORT).show();
            return;
        }else{
            MPTUser user=new MPTUser();
            user.setUsername(newUsername);
            user.update(currentUser.getObjectId(), new UpdateListener() {
                @Override
                public void done(BmobException e) {
                    if(e==null){
                        Toast.makeText(MainActivity.this, "修改成功", Toast.LENGTH_SHORT).show();
                        tv_header_userName.setText(newUsername);
                    }else{
                        Toast.makeText(MainActivity.this, "修改失败!", Toast.LENGTH_SHORT).show();
                    }
                }
            });

        }

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    /**
     * 登出
     */
    public void logout() {
        currentUser = BmobUser.getCurrentUser(MPTUser.class);
        if (currentUser == null) {//未登陆
            Toast.makeText(this, "您还未登陆", Toast.LENGTH_SHORT).show();
        } else {//已登陆
            BmobUser.logOut();
            currentUser = BmobUser.getCurrentUser(MPTUser.class);//更新当前用户
            tv_header_userName.setText(getResources().getString(R.string.drawer_header_username_default));
            setUserHeadImg();
            Toast.makeText(this, "您已退出登陆", Toast.LENGTH_SHORT).show();
        }
    }
}
