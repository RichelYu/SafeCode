package com.ybr.struct;

import java.util.Iterator;

import com.ybr.Constants;
import com.ybr.exception.HashMapStringFormatException;
import com.ybr.util.CommonUtil;


@SuppressWarnings("serial")
public class HashMap extends java.util.HashMap<String, Object> {

	/**
	 * 
	 * @param str
	 * @return
	 * @throws Exception 
	 */
	public static HashMap parseHashMap(String str)
			throws Exception {
		if (str == null || str.length() < 1)
			return null;
		if (str.charAt(0) != Constants.CHAR_HASHMAP_BRACKET_LEFT
				|| str.charAt(str.length() - 1) != Constants.CHAR_HASHMAP_BRACKET_RIGHT)
			throw new HashMapStringFormatException();// 格式问题抛出异常
		HashMap map = new HashMap();
		char[] strA = str.toCharArray();
		for (int i = 1; i < strA.length; i++) {
			if (strA[i] == Constants.CHAR_HASHMAP_BRACKET_LEFT || strA[i] == ' ')
				continue;
			String key = "";
			StringBuffer value = new StringBuffer();
			while (i < strA.length && strA[i] != Constants.CHAR_HASHMAP_SEPARATE_KEYVALUE
					&& strA[i] != Constants.CHAR_HASHMAP_BRACKET_RIGHT) {
				key += "" + strA[i++];
			}
			i++;
			while (i < strA.length && strA[i] != Constants.CHAR_HASHMAP_SEPARATE_GROUP
					&& strA[i] != Constants.CHAR_HASHMAP_BRACKET_RIGHT) {
				int a_b_num = 0;
                int h_b_num = 0;
                if (strA[i] == Constants.CHAR_ARRAYLIST_BRACKET_LEFT)
                    a_b_num++;
                else if (strA[i] == Constants.CHAR_HASHMAP_BRACKET_LEFT)
                    h_b_num++;
                do {
                    if (strA[i] == Constants.CHAR_ARRAYLIST_BRACKET_RIGHT)
                        a_b_num--;
                    else if (strA[i] == Constants.CHAR_HASHMAP_BRACKET_RIGHT)
                        h_b_num--;
                    value.append(strA[i++]);
                    if (strA[i] == Constants.CHAR_ARRAYLIST_BRACKET_LEFT)
                        a_b_num++;
                    else if (strA[i] == Constants.CHAR_HASHMAP_BRACKET_LEFT)
                        h_b_num++;
                } while (a_b_num != 0 || h_b_num != 0);
			}
			String valueStr = value.toString();
			if (valueStr.charAt(0) == Constants.CHAR_ARRAYLIST_BRACKET_LEFT
					&& valueStr.charAt(valueStr.length() - 1) == Constants.CHAR_ARRAYLIST_BRACKET_RIGHT)
				map.put(key, ArrayList.parseArrayList(valueStr));
			else if (valueStr.charAt(0) == Constants.CHAR_HASHMAP_BRACKET_LEFT
					&& valueStr.charAt(valueStr.length() - 1) == Constants.CHAR_HASHMAP_BRACKET_RIGHT)
				map.put(key, HashMap.parseHashMap(valueStr));
			else if (valueStr.length() > "className".length() && valueStr.indexOf("className") > -1)
				map.put(key, CommonUtil.msgToBeanBase(valueStr));
			else
				map.put(key, valueStr);
		}
		return map;
	}

	/**
	 * toString 转成个性化字符串 {key0>value0^ key0>value2}
	 */
	@Override
	public String toString() {
		Iterator<Entry<String, Object>> i = entrySet().iterator();
		if (!i.hasNext())
			return "" + Constants.CHAR_HASHMAP_BRACKET_LEFT + Constants.CHAR_HASHMAP_BRACKET_RIGHT;

		StringBuilder sb = new StringBuilder();
		sb.append(Constants.CHAR_HASHMAP_BRACKET_LEFT);
		for (;;) {
			Entry<String, Object> e = i.next();
			String key = e.getKey();
			Object value = e.getValue();
			sb.append(key);
			sb.append(Constants.CHAR_HASHMAP_SEPARATE_KEYVALUE);
			sb.append(value == this ? "(this Map)" : value);
			if (!i.hasNext())
				return sb.append(Constants.CHAR_HASHMAP_BRACKET_RIGHT).toString();
			sb.append(Constants.CHAR_HASHMAP_SEPARATE_GROUP);
		}
	}
}
