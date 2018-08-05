package com.dh.exam.mpt.entity;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.style.QuoteSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.dh.exam.mpt.R;
import java.util.List;
import de.hdodenhof.circleimageview.CircleImageView;

/**
 * 考试结果适配器For RecyclerView
 * @author DavidHuang  at 上午10:11 18-8-4
 */
public class TestResultAdapter extends RecyclerView.Adapter<TestResultAdapter.ViewHolder> {
    private Context context;
    private List<Result> resultList;
    private List<Question>questionList;
    private TextView tv_question_num;
    private TextView tv_question_title;
    private TextView tv_option_a;
    private TextView tv_option_b;
    private TextView tv_option_c;
    private TextView tv_option_d;
    private TextView tv_right_answer;
    private TextView tv_user_answer;

    static class ViewHolder extends RecyclerView.ViewHolder{
        TextView tv_question_num;
        CircleImageView iv_question_result;
        ViewHolder(View itemView) {
            super(itemView);
            tv_question_num=itemView.findViewById(R.id.tv_num);
            iv_question_result=itemView.findViewById(R.id.cim_is_correct);
        }
    }

    public TestResultAdapter(List<Result> resultList,List<Question> questionList) {
        this.resultList = resultList;
        this.questionList=questionList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if(context==null){
            context=parent.getContext();
        }
        View view= LayoutInflater.from(context).inflate(R.layout.test_result_item,parent,false);
        final ViewHolder holder=new ViewHolder(view);
        view.setOnClickListener(v -> {
            int position=holder.getAdapterPosition();
            Result result=resultList.get(position);
            Question question=questionList.get(position);
            showQuestionDialog(question);
        });
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Result result=resultList.get(position);
        holder.tv_question_num.setText(String.valueOf(result.getQuestionNum()+1));
        if(result.isResult()){
            holder.iv_question_result.setImageResource(R.drawable.correct);
        }else {
            holder.iv_question_result.setImageResource(R.drawable.incorrect);
        }
    }

    @Override
    public int getItemCount() {

        return resultList.size();
    }

    /**
     * 在Dialog上显示问题
     * @param question 题目
     */
    private void showQuestionDialog(Question question){
        Dialog mDialog=new Dialog(context);
        mDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        LayoutInflater inflater = LayoutInflater.from(context);
        @SuppressLint("InflateParams")
        View dialogView= inflater.inflate(R.layout.dialog_question_answer, null);
        mDialog.setContentView(dialogView);

        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        Window window = mDialog.getWindow();
        if(window!=null){
            lp.copyFrom(window.getAttributes());
            lp.width = WindowManager.LayoutParams.MATCH_PARENT;
            lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
            mDialog.show();
            window.setAttributes(lp);
        }
        initDialogView(dialogView,question);
    }

    @SuppressLint("SetTextI18n")
    private void initDialogView(View dialogView, Question question){
        tv_question_num=dialogView.findViewById(R.id.tv_question_num);
        tv_question_title=dialogView.findViewById(R.id.tv_question_title);
        tv_option_a=dialogView.findViewById(R.id.tv_optionA);
        tv_option_b=dialogView.findViewById(R.id.tv_optionB);
        tv_option_c=dialogView.findViewById(R.id.tv_optionC);
        tv_option_d=dialogView.findViewById(R.id.tv_optionD);
        tv_right_answer=dialogView.findViewById(R.id.tv_right_answer);
        tv_user_answer=dialogView.findViewById(R.id.tv_user_answer);
        int i=question.getQuestionNum()+1;
        tv_question_num.setText(i+":");
        tv_question_title.setText(question.getTitle());
        tv_option_a.setText(question.getOptions()[0]);
        tv_option_b.setText(question.getOptions()[1]);
        tv_option_c.setText(question.getOptions()[2]);
        tv_option_d.setText(question.getOptions()[3]);
        //先后显示标准答案和用户答案
        setAnswerForDialog(question.getAnswer(),true);
        setAnswerForDialog(resultList.get(question.getQuestionNum()).getUserAnswer(),false);
    }

    /**
     * 在Dialog上显示答案
     * @param answer int类型的答案
     * @param isStandard 答案类型：true 标准答案；false 用户答案
     */
    @SuppressLint("ResourceAsColor")
    private void setAnswerForDialog(int answer , boolean isStandard){
        switch(answer){
            case 1:
                if(isStandard){
                    tv_right_answer.setText(R.string.right_answer_d);
                }else {
                    tv_user_answer.setText(R.string.user_answer_d);
                }
                break;
            case 2:
                if(isStandard){
                    tv_right_answer.setText(R.string.right_answer_c);
                }else {
                    tv_user_answer.setText(R.string.user_answer_c);
                }
                break;
            case 3:
                if(isStandard){
                    tv_right_answer.setText(R.string.right_answer_cd);
                }else {
                    tv_user_answer.setText(R.string.user_answer_cd);
                }
                break;
            case 4:
                if(isStandard){
                    tv_right_answer.setText(R.string.right_answer_b);
                }else {
                    tv_user_answer.setText(R.string.user_answer_b);
                }
                break;
            case 5:
                if(isStandard){
                    tv_right_answer.setText(R.string.right_answer_bd);
                }else {
                    tv_user_answer.setText(R.string.user_answer_bd);
                }
                break;
            case 6:
                if(isStandard){
                    tv_right_answer.setText(R.string.right_answer_bc);
                }else {
                    tv_user_answer.setText(R.string.user_answer_bc);
                }
                break;
            case 7:
                if(isStandard){
                    tv_right_answer.setText(R.string.right_answer_bcd);
                }else {
                    tv_user_answer.setText(R.string.user_answer_bcd);
                }
                break;
            case 8:
                if(isStandard){
                    tv_right_answer.setText(R.string.right_answer_a);
                }else {
                    tv_user_answer.setText(R.string.user_answer_a);
                }
                break;
            case 9:
                if(isStandard){
                    tv_right_answer.setText(R.string.right_answer_ad);
                }else {
                    tv_user_answer.setText(R.string.user_answer_ad);
                }
                break;
            case 10:
                if(isStandard){
                    tv_right_answer.setText(R.string.right_answer_ac);
                }else {
                    tv_user_answer.setText(R.string.user_answer_ac);
                }
                break;
            case 11:
                if(isStandard){
                    tv_right_answer.setText(R.string.right_answer_acd);
                }else {
                    tv_user_answer.setText(R.string.user_answer_acd);
                }
                break;
            case 12:
                if(isStandard){
                    tv_right_answer.setText(R.string.right_answer_ab);
                }else {
                    tv_user_answer.setText(R.string.user_answer_ab);
                }
                break;
            case 13:
                if(isStandard){
                    tv_right_answer.setText(R.string.right_answer_abd);
                }else {
                    tv_user_answer.setText(R.string.user_answer_abd);
                }
                break;
            case 14:
                if(isStandard){
                    tv_right_answer.setText(R.string.right_answer_abc);
                }else {
                    tv_user_answer.setText(R.string.user_answer_abc);
                }
                break;
            case 15:
                if(isStandard){
                    tv_right_answer.setText(R.string.right_answer_abcd);
                }else {
                    tv_user_answer.setText(R.string.user_answer_abcd);
                }
                break;
                default:
                    if(isStandard){
                        tv_right_answer.setText(R.string.right_answer_null);
                    }else {
                        tv_user_answer.setText(R.string.user_answer_null);
                    }
        }

    }
}
