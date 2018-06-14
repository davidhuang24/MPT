package com.dh.exam.mpt.avtivity.Fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.dh.exam.mpt.MPTApplication;
import com.dh.exam.mpt.R;
import com.dh.exam.mpt.avtivity.MainActivity;
import com.dh.exam.mpt.entity.Paper;
import com.dh.exam.mpt.entity.PaperAdapter;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;


public class PaperLibraryFragment extends Fragment implements View.OnClickListener{

    private Paper[] papers = {
            new Paper("2018计算机二级考试","计算机","教育部计算机办公室",50,false),
            new Paper("2017全国高等招生考试","高考","教育部高考办公室",40,false),
            new Paper("2017全国硕士生招生考试英语","考研","教育部考研办公室",78,false),
            new Paper("2018雅思考试","英语","英语",20,false),
            new Paper("2016-2017软件学院数理逻辑期末考试","数理","北邮理学院",60,false),};
    private List<Paper> paperList=new ArrayList<>();
    private PaperAdapter adapter;
    private SwipeRefreshLayout swipeRefreshLayout;
    private MainActivity currentActivity;

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
        swipeRefreshLayout.setColorSchemeResources(R.color.colorAccent);//设置刷新进度条颜色
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {//下拉刷新
                refreshPapers();
            }
        });

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
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                currentActivity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        initPapers();//模拟获取新数据
                        adapter.notifyDataSetChanged();//模拟展示新数据
                        swipeRefreshLayout.setRefreshing(false);//隐藏刷新进度条
                    }
                });
            }
        }).start();
    }

    /**
     * 初始化试卷数据
     */
    private void initPapers() {//初始化试卷数据
        paperList.clear();
        for (int i = 0; i < 20; i++) {
            Random random = new Random();
            int index = random.nextInt(papers.length);
            paperList.add(papers[index]);
        }
    }

    @Override
    public void onClick(View v) {

    }
}
