package com.dh.exam.mpt.entity;

import cn.bmob.v3.BmobObject;

/**
 * 题目类
 *
 * @author DavidHuang  at 下午8:06 18-6-21
 */
public class Question extends BmobObject {
    private Paper paper;//一对多关系，一张试卷多个题目
    private Integer questionNum;
    private String title;
    private String []Option;//答案选项
    private String answer;

    public Question(String tableName, Paper paper, Integer questionNum, String title, String[] option, String answer) {
        super(tableName);
        this.paper = paper;
        this.questionNum = questionNum;
        this.title = title;
        Option = option;
        this.answer = answer;
    }

    public Paper getPaper() {
        return paper;
    }

    public void setPaper(Paper paper) {
        this.paper = paper;
    }

    public Integer getQuestionNum() {
        return questionNum;
    }

    public void setQuestionNum(Integer questionNum) {
        this.questionNum = questionNum;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String[] getOption() {
        return Option;
    }

    public void setOption(String[] option) {
        Option = option;
    }

    public String getAnswer() {
        return answer;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }
}
