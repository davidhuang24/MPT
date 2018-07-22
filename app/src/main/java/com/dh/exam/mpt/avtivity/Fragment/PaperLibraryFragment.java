
package com.dh.exam.mpt.avtivity.Fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.dh.exam.mpt.MPTApplication;
import com.dh.exam.mpt.R;
import com.dh.exam.mpt.Utils.NetworkUtil;
import com.dh.exam.mpt.avtivity.MainActivity;
import com.dh.exam.mpt.entity.Paper;
import com.dh.exam.mpt.entity.PaperAdapter;

import java.util.ArrayList;
import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
/**
 *Paper列表Fragment
 *
 *@author DavidHuang  at 下午8:31 18-7-22
 */
public class PaperLibraryFragment extends Fragment implements View.OnClickListener{

    private List<Paper> paperList=new ArrayList<>();
    private PaperAdapter adapter;
    private SwipeRefreshLayout swipeRefreshLayout;
    private MainActivity currentActivity;
    private int paperCount;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view=inflater.inflate(R.layout.fragment_paper_library, container, false);
        init(view);
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        initPapers();

    }

    public void init(View view){
        currentActivity=(MainActivity)getActivity();

        swipeRefreshLayout=(SwipeRefreshLayout) view.findViewById(R.id.swipe_refresh);
        swipeRefreshLayout.setColorSchemeResources(R.color.colorAccent);//设置刷新进度条颜色=
        swipeRefreshLayout.setOnRefreshListener(this::refreshPapers);//下拉刷新

        RecyclerView recyclerView=(RecyclerView) swipeRefreshLayout.findViewById(R.id.recycler_view);
        LinearLayoutManager layoutManager=new  LinearLayoutManager(MPTApplication.getContext());
        recyclerView.setLayoutManager(layoutManager);
        adapter=new PaperAdapter(paperList);
        recyclerView.setAdapter(adapter);


    }

    /**
     * 刷新逻辑
     */
    private  void refreshPapers(){//刷新逻辑
        new Thread(() -> {
            //获取新数据
            currentActivity.runOnUiThread(this::getNewPapers);
        }).start();

    }

    /**
     * 刷新从网络获取新数据,然后显示,缓存新数据
     */
    private void getNewPapers(){
        paperList.clear();
        BmobQuery<Paper> query=new BmobQuery<>();
        query.include("paperAuthor");
        query.setCachePolicy(BmobQuery.CachePolicy.NETWORK_ONLY);//只从网络获取数据，同时会在本地缓存数据
        if(!NetworkUtil.isNetworkAvailable()){
            Toast.makeText(currentActivity, "网络不可用,请连接网络后重新下拉刷新", Toast.LENGTH_SHORT).show();
            swipeRefreshLayout.setRefreshing(false);//隐藏刷新进度条
            return;
        }
        query.findObjects(new FindListener<Paper>() {
            @Override
            public void done(List<Paper> list, BmobException e) {
                if(e==null){
                    paperList.addAll(list);
                    if(paperCount==paperList.size()){
                        Toast.makeText(currentActivity, "没有更新数据了", Toast.LENGTH_SHORT).show();
                    }
                    paperCount=paperList.size();
                    adapter.notifyDataSetChanged();//显示新数据
                }else {
                    Toast.makeText(currentActivity,
                            "刷新失败,错误码:"+e.getErrorCode()+",错误信息:"+e.getMessage(),
                            Toast.LENGTH_SHORT).show();
                }
                swipeRefreshLayout.setRefreshing(false);
            }
        });
    }
    /**
     * 初始化试卷数据,从缓存读取
     */
    private void initPapers() {
        paperList.clear();
        BmobQuery<Paper> query=new BmobQuery<>();
        //必须设置,不然paper.getPaperAuthor().getUsername()为空;
        // 想在查询内获取Pointer类型的关联对象,就该这么设置,体现了数据关联性
        query.include("paperAuthor");
        boolean isCacheExisted=query.hasCachedResult(Paper.class);
        //
        if(isCacheExisted){
            query.setCachePolicy(BmobQuery.CachePolicy.CACHE_ONLY);//只从缓存获取数据
        }else {
            query.setCachePolicy(BmobQuery.CachePolicy.NETWORK_ONLY);//只从网络获取数据，同时会在本地缓存数据
        }
        if(!isCacheExisted&&!NetworkUtil.isNetworkAvailable()){
            Toast.makeText(currentActivity, "网络不可用,请连接网络后下拉刷新", Toast.LENGTH_SHORT).show();
            return;
        }
        query.findObjects(new FindListener<Paper>() {
            @Override
            public void done(List<Paper> list, BmobException e) {
                if(e==null){
                    paperList.addAll(list);//不能paperList=list;
                    paperCount=paperList.size();
                    adapter.notifyDataSetChanged();//更新Ui
                }else {
                    Toast.makeText(currentActivity,
                            "缓存Paper失败,错误码:"+e.getErrorCode()+",错误信息:"+e.getMessage(),
                            Toast.LENGTH_SHORT).show();
                }
            }
        });

    }
    @Override
    public void onClick(View v) {
    }
}