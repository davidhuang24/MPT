package com.dh.exam.mpt.entity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.dh.exam.mpt.MPTApplication;
import com.dh.exam.mpt.R;
import com.dh.exam.mpt.Utils.NetworkUtil;
import com.dh.exam.mpt.activity.BaseActivity;
import com.dh.exam.mpt.activity.NewQuestionActivity;
import com.dh.exam.mpt.activity.PaperInfoActivity;

import java.util.List;

import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.UpdateListener;

/**
 *PaperAdapter
 *
 *@author DavidHuang  at 下午3:37 18-5-31
 */
public class PaperAdapter extends RecyclerView.Adapter <PaperAdapter.ViewHolder>{

    private static final String TAG = "PaperAdapter";
    private Context context;
    private List<Paper> paperList;

    static class ViewHolder extends RecyclerView.ViewHolder{
        CardView cardView;
        ImageView iv_paper;
        TextView tv_paper_name;
        ImageView iv_love;
        TextView tv_question_count;
        TextView tv_kind;
        TextView tv_author;
        ViewHolder(View view) {
            super(view);
            cardView = (CardView) view;
            iv_paper = (ImageView) view.findViewById(R.id.iv_paper_icon);
            tv_paper_name = (TextView) view.findViewById(R.id.tv_paper_name);
            iv_love= (ImageView) view.findViewById(R.id.iv_love);
            tv_question_count= (TextView) view.findViewById(R.id.tv_question_count);
            tv_kind= (TextView) view.findViewById(R.id.tv_paper_kind);
            tv_author= (TextView) view.findViewById(R.id.tv_paper_author);
        }
    }

    public PaperAdapter(List<Paper> paperList) {
        this.paperList = paperList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if(context==null){
            context=parent.getContext();
        }
        View view= LayoutInflater.from(context).inflate(R.layout.paper_item,parent,false);
        final ViewHolder holder=new ViewHolder(view);
        holder.cardView.setOnClickListener(v -> {
            int position=holder.getAdapterPosition();
            Paper paper=paperList.get(position);
            if(!NetworkUtil.isNetworkAvailable()){
                Toast.makeText(MPTApplication.getContext(), "网络不可用,请连接网络后再操作！", Toast.LENGTH_SHORT).show();
                return ;
            }else {
                PaperInfoActivity.activityStart(context,PaperInfoActivity.class,
                        paper.getObjectId(),null,null);
            }
        });
        holder.iv_love.setOnClickListener(v -> {
            if(!NetworkUtil.isNetworkAvailable()){
                Toast.makeText(MPTApplication.getContext(), "网络不可用,请连接网络后再操作！", Toast.LENGTH_SHORT).show();
                return ;
            }else {
                int position=holder.getAdapterPosition();
                Paper paper=paperList.get(position);
                if(paper.getLove()){
                    holder.iv_love.setImageResource(R.drawable.love0);
                    paper.setLove(false);
                }else{
                    holder.iv_love.setImageResource(R.drawable.love1);
                    paper.setLove(true);
                }
                paper.update(new UpdateListener() {//同步更新数据库
                    @Override
                    public void done(BmobException e) {
                        if(e==null){
                        }else{
                            Toast.makeText(context,
                                    "更新数据失败！", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
                BaseActivity.cachePapers();//同步更新缓存
            }
        });
        return holder;
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Paper paper=paperList.get(position);
        holder.tv_paper_name.setText(paper.getPaperName());
        holder.tv_question_count.setText(paper.getQuestionCount().toString());
        holder.tv_kind.setText(paper.getPaperKind());
        holder.tv_author.setText(paper.getPaperAuthor().getUsername());
        if(paper.getLove()){
            holder.iv_love.setImageResource(R.drawable.love1);
        }else{
            holder.iv_love.setImageResource(R.drawable.love0);
        }
    }

    @Override
    public int getItemCount() {
        return paperList.size();
    }


}
