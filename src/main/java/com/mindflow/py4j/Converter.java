package com.mindflow.py4j;

import com.mindflow.py4j.exception.IllegalPinyinException;

/**
 * @author Ricky Fung
 */
public interface Converter {

    String[] getPinyin(char ch) throws IllegalPinyinException;

    /**
     * 方法 getPinyin 功能： 汉字转换成拼音
     * @param chinese  需要转换为拼音的汉字
     * @param caseType  转换成大写或小写， 可选值为包含：upper, lower; 参数为null或"" 时默认首字母大写
     * @param withBlank 各汉字拼音之间是否填充空格
     * @return String 拼音
     * @author ningning.jiang 2017年12月21日 15:57:44
     */
    String getPinyin(String chinese, String caseType, boolean withBlank) throws IllegalPinyinException;

    /**
     * 方法 getPinyin 功能： 汉字转换成拼音
     * @param chinese  需要转换为拼音的汉字
     * @param withBlank 各汉字拼音之间是否填充空格
     * @return String 拼音
     * @author ningning.jiang 2017年12月21日 15:57:44
     */
    String getPinyin(String chinese, boolean withBlank) throws IllegalPinyinException;

    /**
     * 方法 getPinyin 功能： 汉字转换成拼音
     * @param chinese  需要转换为拼音的汉字
     * @return String 拼音
     * @author ningning.jiang 2017年12月21日 15:57:44
     */
    String getPinyin(String chinese) throws IllegalPinyinException;
}
