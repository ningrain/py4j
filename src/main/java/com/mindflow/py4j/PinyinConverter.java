package com.mindflow.py4j;

import com.mindflow.py4j.exception.IllegalPinyinException;
import com.mindflow.py4j.util.ArrayUtils;
import com.mindflow.py4j.util.StringUtils;
import com.google.common.collect.ArrayListMultimap;
import com.mindflow.py4j.voc.Py4jDictionary;
import net.sourceforge.pinyin4j.PinyinHelper;
import net.sourceforge.pinyin4j.format.HanyuPinyinCaseType;
import net.sourceforge.pinyin4j.format.HanyuPinyinOutputFormat;
import net.sourceforge.pinyin4j.format.HanyuPinyinToneType;
import net.sourceforge.pinyin4j.format.HanyuPinyinVCharType;
import net.sourceforge.pinyin4j.format.exception.BadHanyuPinyinOutputFormatCombination;

/**
 * @author Ricky Fung
 */
public class PinyinConverter implements Converter {
    private final ArrayListMultimap<String, String> duoYinZiMap;

    public PinyinConverter() {
        this.duoYinZiMap = Py4jDictionary.getDefault().getDuoYinZiMap();
    }

    @Override
    public String[] getPinyin(char ch) throws IllegalPinyinException {
        try {
            HanyuPinyinOutputFormat outputFormat = new HanyuPinyinOutputFormat();
            outputFormat.setToneType(HanyuPinyinToneType.WITHOUT_TONE);
            outputFormat.setCaseType(HanyuPinyinCaseType.LOWERCASE);
            outputFormat.setVCharType(HanyuPinyinVCharType.WITH_V);

            if (ch >= 32 && ch <= 125) {    //ASCII >=33 ASCII<=125的直接返回 ,ASCII码表：http://www.asciitable.com/
                return new String[]{String.valueOf(ch)};
            }
            return ArrayUtils.distinct(PinyinHelper.toHanyuPinyinStringArray(ch, outputFormat));
        } catch (BadHanyuPinyinOutputFormatCombination e) {
            throw new IllegalPinyinException(e);
        }

    }

    @Override
    public String getPinyin(String chinese, boolean withBlank) throws IllegalPinyinException {
        return getPinyin(chinese, null, withBlank);
    }

    @Override
    public String getPinyin(String chinese) throws IllegalPinyinException {
        return getPinyin(chinese, null, true);
    }

    @Override
    public String getPinyin(String chinese, String caseType, boolean withBlank) throws IllegalPinyinException {
        if (StringUtils.isEmpty(chinese)) {
            return null;
        }
        chinese = chinese.replaceAll("[\\.，\\,！·\\!？\\?；\\;\\(\\)（）\\[\\]\\:： ]+", " ").trim();
        StringBuilder py_sb = new StringBuilder(32);
        String result = "";
        char[] chs = chinese.toCharArray();
        for (int i = 0; i < chs.length; i++) {
            String[] py_arr = getPinyin(chs[i]);
            if (py_arr == null || py_arr.length < 1) {
                throw new IllegalPinyinException("pinyin array is empty, char:" + chs[i] + ",chinese:" + chinese);
            }
            if (py_arr.length == 1) {
                py_sb.append(convertInitialToUpperCase(py_arr[0].concat(" "), caseType));
                result = py_sb.toString();
            } else if (py_arr.length == 2 && py_arr[0].equals(py_arr[1])) {
                py_sb.append(convertInitialToUpperCase(py_arr[0].concat(" "), caseType));
                result = py_sb.toString();
            } else {
                String resultPy = null, defaultPy = null;
                for (String py : py_arr) {
                    String left;    //向左多取一个字,例如 银[行]
                    if (i >= 1 && i + 1 <= chinese.length()) {
                        left = chinese.substring(i - 1, i + 1);
                        if (duoYinZiMap.containsKey(py) && duoYinZiMap.get(py).contains(left)) {
                            resultPy = py;
                            break;
                        }
                    }

                    String right;    //向右多取一个字,例如 [长]沙
                    if (i <= chinese.length() - 2) {
                        right = chinese.substring(i, i + 2);
                        if (duoYinZiMap.containsKey(py) && duoYinZiMap.get(py).contains(right)) {
                            resultPy = py;
                            break;
                        }
                    }

                    String middle;    //左右各多取一个字,例如 龙[爪]槐
                    if (i >= 1 && i + 2 <= chinese.length()) {
                        middle = chinese.substring(i - 1, i + 2);
                        if (duoYinZiMap.containsKey(py) && duoYinZiMap.get(py).contains(middle)) {
                            resultPy = py;
                            break;
                        }
                    }
                    String left3;    //向左多取2个字,如 芈月[传],列车长
                    if (i >= 2 && i + 1 <= chinese.length()) {
                        left3 = chinese.substring(i - 2, i + 1);
                        if (duoYinZiMap.containsKey(py) && duoYinZiMap.get(py).contains(left3)) {
                            resultPy = py;
                            break;
                        }
                    }

                    String right3;    //向右多取2个字,如 [长]孙无忌
                    if (i <= chinese.length() - 3) {
                        right3 = chinese.substring(i, i + 3);
                        if (duoYinZiMap.containsKey(py) && duoYinZiMap.get(py).contains(right3)) {
                            resultPy = py;
                            break;
                        }
                    }

                    if (duoYinZiMap.containsKey(py) && duoYinZiMap.get(py).contains(String.valueOf(chs[i]))) {    //默认拼音
                        defaultPy = py;
                    }
                }

                if (StringUtils.isEmpty(resultPy)) {
                    if (StringUtils.isNotEmpty(defaultPy)) {
                        resultPy = defaultPy;
                    } else {
                        resultPy = py_arr[0];
                    }
                }
                resultPy = resultPy.concat(" ");
                result = py_sb.append(convertInitialToUpperCase(resultPy, caseType)).toString();
            }
        }
        if (!withBlank) {
            result = py_sb.toString().replaceAll(" ", "");
        } else {
            return result.substring(0, result.length() - 1);
        }
        return result;
    }

    private String convertInitialToUpperCase(String str, String caseType) throws IllegalPinyinException {
        if (str == null || str.length() == 0) {
            return "";
        }
        if (caseType != null && !"".equals(caseType)) {
            switch (caseType) {
                case "upper":
                    return str.toUpperCase();
                case "lower":
                    return str.toLowerCase();
                default:
                    throw new IllegalPinyinException("第二个参数输入有误，可选的输入值为：upper, lower, null或者\"\"");
            }
        }
        return str.substring(0, 1).toUpperCase() + str.substring(1);
    }

}

