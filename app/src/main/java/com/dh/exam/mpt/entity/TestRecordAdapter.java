package com.dh.exam.mpt.entity;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.dh.exam.mpt.R;

import java.util.ArrayList;
import java.util.List;

/**
 * @author DavidHuang  at 下午7:32 18-8-7
 */
public class TestRecordAdapter extends RecyclerView.Adapter {

    private List<ScoreRecord>recordList;
    private Context context;

    static class ViewHolder extends RecyclerView.ViewHolder{
        LinearLayout layoutView;
        TextView tv_paper_name;
        TextView tv_user_name;
        TextView tv_score;
        ViewHolder(View view) {
            super(view);
            layoutView= (LinearLayout) view;
            tv_paper_name = (TextView) view.findViewById(R.id.tv_paper_name);;
            tv_user_name= (TextView) view.findViewById(R.id.tv_username);
            tv_score= (TextView) view.findViewById(R.id.tv_score);
        }
    }









    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return 0;
    }
}
