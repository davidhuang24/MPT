package com.dh.exam.mpt.entity;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.dh.exam.mpt.R;


import java.util.List;

/**
 * @author DavidHuang  at 下午7:32 18-8-7
 */
public class TestRecordAdapter extends RecyclerView.Adapter <TestRecordAdapter.ViewHolder>{

    private List<ScoreRecord>recordList;
    private Context context;
    private static final String TAG = "TestRecordAdapter";

    public TestRecordAdapter(List<ScoreRecord> recordList) {
        this.recordList = recordList;
    }

    static class ViewHolder extends RecyclerView.ViewHolder{

        TextView tv_paper_name;
        TextView tv_user_name;
        TextView tv_score;
        ViewHolder(View view) {
            super(view);
            tv_paper_name = view.findViewById(R.id.tv_paper_name);;
            tv_user_name= view.findViewById(R.id.tv_username);
            tv_score= view.findViewById(R.id.tv_score);
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if(context==null){
            context=parent.getContext();
        }
        View view= LayoutInflater.from(context).inflate(R.layout.test_record_item,parent,false);
        Log.e(TAG, "onCreateViewHolder: ----------------->");
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ScoreRecord record=recordList.get(position);
        holder.tv_paper_name.setText(record.getPaper().getPaperName());
        holder.tv_user_name.setText(record.getUser().getUsername());
        holder.tv_score.setText(String.valueOf(record.getScore()));
        Log.e(TAG, "onBindViewHolder: ---------------------->"+record.getUser().getUsername());
    }

    @Override
    public int getItemCount() {
        return recordList.size();
    }






}
