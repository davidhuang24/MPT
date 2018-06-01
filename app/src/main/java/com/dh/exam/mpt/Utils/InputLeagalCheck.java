package com.dh.exam.mpt.Utils;

import android.text.TextUtils;


/**
 *附加输入合法性检测：虽然bmob SDK后台有合法性检测，
 *但没有相应的密码检测、手机号检测表达式可能不是与我
 *的想法一致、可以实现更清晰的不合法输入信息反馈.
 * <p>
 *ps:若会对输入进行相等equal判断则不用检测
 *
 *@author DavidHuang  at 下午3:33 18-5-31
 */
public class InputLeagalCheck {
    /**
     * 密码检测：6-16位字母或者数字，但不能是纯数字或纯密码
     * @param input 待检测字符串
     * @return 检测结果
     */
    public static boolean isPassword(String input){
        String regex="^(?![0-9]+$)(?![a-zA-Z]+$)[0-9A-Za-z]{6,16}$";//密码正则表达式
        if(TextUtils.isEmpty(input)){
            return false;
        }else{
            return input.matches(regex);
        }
    }
    /**
     * 手机号检测：第一位为1，后面10位0-9数字
     *
     * @param input
     * @return 检测结果
     */
    public static boolean isPhoneNum(String input){
        String regex = "[1]\\d{10}";//手机号正则表达式
        if(TextUtils.isEmpty(input)){
            return false;
        }else{
            return input.matches(regex);
        }
    }
    /**
     * Email检测
     *
     * @param input
     * @return 检测结果
     */
    public static boolean isEmail(String input){
        String regex = "^\\w+([-+.]\\w+)*@\\w+([-.]\\w+)*\\.\\w+([-.]\\w+)*$";//Email正则表达式
        if(TextUtils.isEmpty(input)){
            return false;
        }else{
            return input.matches(regex);
        }
    }
}
