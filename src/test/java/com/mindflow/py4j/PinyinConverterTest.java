package com.mindflow.py4j;

import com.mindflow.py4j.exception.IllegalPinyinException;
import org.junit.*;

import java.util.Arrays;

/**
 * Unit test for simple App.
 */
public class PinyinConverterTest {
	private Converter converter;

	@Before
	public void init(){
		converter = new PinyinConverter();
	}

	@Test
	public void testChinesePy() throws IllegalPinyinException {

		final String[] arr = {"肯德基", "重庆银行", "长沙银行", "便宜坊", "西藏", "藏宝图", "出差", "参加", "列车长"};
		
		for (String chinese : arr){
			String py = converter.getPinyin(chinese, null, true);
			System.out.println(chinese+"\t\t"+py);
		}
	}

	@Test
	public void testChinesePy1() throws IllegalPinyinException {
		System.out.println(converter.getPinyin("肯德基", "", true));
	}

    @Test
    public void testChinesePy2() throws IllegalPinyinException {
        System.out.println(converter.getPinyin("重庆银行"));
    }

	@Test
	public void testCharPy() throws IllegalPinyinException {

		char[] chs = {'嗯', '长', '行', '藏', '度', '阿', '佛', '2', 'A', 'a'};
		for(char ch : chs){
			String[] arr_py = converter.getPinyin(ch);
			System.out.println(ch+"\t"+Arrays.toString(arr_py));
		}
	}

	@After
	public void destroy(){
		converter = null;
	}
}
