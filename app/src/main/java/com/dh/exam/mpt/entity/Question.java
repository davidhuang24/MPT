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
    private String [] options;//答案选项
    private int answer;
    private String analysis;

    public Question() {
    }

    public Question(String tableName, Paper paper, Integer questionNum, String title, String[] options, int answer, String analysis) {
        super(tableName);
        this.paper = paper;
        this.questionNum = questionNum;
        this.title = title;
        this.options = options;
        this.answer = answer;
        this.analysis=analysis;
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

    public String[] getOptions() {
        return options;
    }

    public void setOptions(String[] options) {
        this.options = options;
    }

    public int getAnswer() {
        return answer;
    }

    public void setAnswer(int answer) {
        this.answer = answer;
    }

    public String getAnalysis() {
        return analysis;
    }

    public void setAnalysis(String analysis) {
        this.analysis = analysis;
    }
}
