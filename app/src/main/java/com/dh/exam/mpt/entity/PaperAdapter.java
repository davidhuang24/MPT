package com.dh.exam.mpt.entity;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.dh.exam.mpt.R;

import java.util.List;

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

        public ViewHolder(View view) {
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

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if(context==null){
            context=parent.getContext();
        }
        View view= LayoutInflater.from(context).inflate(R.layout.paper_item,parent,false);
        final ViewHolder holder=new ViewHolder(view);
        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int position=holder.getAdapterPosition();
                Paper paper=paperList.get(position);
                Toast.makeText(context, paper.getPaperName(), Toast.LENGTH_SHORT).show();
//                PaperActivity.activityStart(context,paper.getPaperName(),paper.getPaperAuthor(),
//                        paper.getPaperKind(),paper.getQuestionCount());
            }
        });
        holder.iv_love.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int position=holder.getAdapterPosition();
                Paper paper=paperList.get(position);
                if(paper.getLove()){
                    holder.iv_love.setImageResource(R.drawable.love0);
                    paper.setLove(false);
                }else{
                    holder.iv_love.setImageResource(R.drawable.love1);
                    paper.setLove(true);
                }
            }
        });
        return holder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Paper paper=paperList.get(position);
        holder.tv_paper_name.setText(paper.getPaperName());
        holder.tv_question_count.setText(paper.getQuestionCount().toString());
        holder.tv_kind.setText(paper.getPaperKind());
        holder.tv_author.setText(paper.getPaperAuthor().getUsername());
    }

    @Override
    public int getItemCount() {
        return paperList.size();
    }

}
