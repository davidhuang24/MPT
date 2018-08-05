package com.dh.exam.mpt.entity;

/**
 * @author DavidHuang  at 上午10:14 18-8-4
 */
public class Result {
    private int questionNum;
    private int userAnswer;
    private boolean result;

    public int getUserAnswer() {
        return userAnswer;
    }

    public void setUserAnswer(int userAnswer) {
        this.userAnswer = userAnswer;
    }

    public int getQuestionNum() {
        return questionNum;
    }

    public void setQuestionNum(int questionNum) {
        this.questionNum = questionNum;
    }

    public boolean isResult() {
        return result;
    }

    public void setResult(boolean result) {
        this.result = result;
    }
}
