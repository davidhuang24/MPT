package com.dh.exam.mpt.avtivity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.Toast;

import com.dh.exam.mpt.R;
import com.dh.exam.mpt.Utils.NetworkUtil;
import com.dh.exam.mpt.entity.MPTUser;
import com.dh.exam.mpt.entity.Question;
import com.dh.exam.mpt.entity.ScoreRecord;
import com.dh.exam.mpt.entity.TestRecordAdapter;
import com.dh.exam.mpt.entity.TestResultAdapter;

import java.util.ArrayList;
import java.util.List;

import cn.bmob.v3.BmobObject;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.datatype.BmobPointer;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;

public class TestRecordActivity extends BaseActivity {

    private List<ScoreRecord>recordList=new ArrayList<>();
    private TestRecordAdapter adapter;
    private BmobUser currentUser;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_record);
        init();
    }

    private void init(){

        initScoreRecordData();
        RecyclerView recyclerView= findViewById(R.id.test_record_recycler_view);
        LinearLayoutManager layoutManager=new LinearLayoutManager(TestRecordActivity.this);
        recyclerView.setLayoutManager(layoutManager);
        adapter=new TestRecordAdapter(recordList);
        recyclerView.setAdapter(adapter);
    }

    private void initScoreRecordData(){
        currentUser= BmobUser.getCurrentUser(MPTUser.class);
        BmobQuery<ScoreRecord>query=new BmobQuery<>();
        query.include("user,paper");
        query.addWhereEqualTo("user",new BmobPointer(currentUser));
        boolean isCacheExisted=query.hasCachedResult(ScoreRecord.class);
        if(isCacheExisted){
            query.setCachePolicy(BmobQuery.CachePolicy.CACHE_ONLY);//只从缓存获取数据
        }else {
            query.setCachePolicy(BmobQuery.CachePolicy.NETWORK_ONLY);//只从网络获取数据，同时会在本地缓存数据
        }
        if(!isCacheExisted&&!NetworkUtil.isNetworkAvailable()){
            Toast.makeText(TestRecordActivity.this, "网络不可用,请连接网络后返回重进", Toast.LENGTH_SHORT).show();
            return;
        }
        query.findObjects(new FindListener<ScoreRecord>() {
            @Override
            public void done(List<ScoreRecord> list, BmobException e) {
                if(e==null){
                    recordList.addAll(list);
                    if(recordList.size()==0){
                        Toast.makeText(TestRecordActivity.this, "您没有考试记录", Toast.LENGTH_SHORT).show();
                    }
                    adapter.notifyDataSetChanged();
                }else {
                    Toast.makeText(TestRecordActivity.this,
                            "获取考试记录数据失败,错误码:"+e.getErrorCode()+",错误信息:"+e.getMessage(),
                            Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
