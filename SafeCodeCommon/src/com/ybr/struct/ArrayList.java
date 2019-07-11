package com.ybr.struct;

import java.util.Iterator;

import com.ybr.Constants;
import com.ybr.exception.ArrayListStringFomatException;
import com.ybr.util.CommonUtil;

/**
 * 链表
 * 
 * @author ghf
 *
 */
public class ArrayList extends java.util.ArrayList<Object> {

	/**
	 * 将str转换为ArrayList<String>
	 * 
	 * @param str
	 * @return
	 * @throws Exception
	 */
	public static ArrayList parseArrayList(String str) throws Exception {
		if (str == null || str.length() < 1)
			return null;
		if (str.charAt(0) != Constants.CHAR_ARRAYLIST_BRACKET_LEFT
				|| str.charAt(str.length() - 1) != Constants.CHAR_ARRAYLIST_BRACKET_RIGHT)
			throw new ArrayListStringFomatException();
		ArrayList list = new ArrayList();
		char[] strA = str.toCharArray();
		for (int i = 1; i < strA.length; i++) {
			if (strA[i] == ' ' || strA[i] == Constants.CHAR_ARRAYLIST_BRACKET_RIGHT)
				continue;
			StringBuffer temp = new StringBuffer();
			while (i < strA.length && strA[i] != Constants.CHAR_ARRAYLIST_SEPARATE
					&& strA[i] != Constants.CHAR_ARRAYLIST_BRACKET_RIGHT) {
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
					temp.append(strA[i++]);
					if (strA[i] == Constants.CHAR_ARRAYLIST_BRACKET_LEFT)
						a_b_num++;
					else if (strA[i] == Constants.CHAR_HASHMAP_BRACKET_LEFT)
						h_b_num++;
				} while (a_b_num != 0 || h_b_num != 0);
			}
			String value = temp.toString();
			if (value.charAt(0) == Constants.CHAR_ARRAYLIST_BRACKET_LEFT
					&& value.charAt(value.length() - 1) == Constants.CHAR_ARRAYLIST_BRACKET_RIGHT)
				list.add(ArrayList.parseArrayList(value));
			else if (value.charAt(0) == Constants.CHAR_HASHMAP_BRACKET_LEFT
					&& value.charAt(value.length() - 1) == Constants.CHAR_HASHMAP_BRACKET_RIGHT)
				list.add(HashMap.parseHashMap(value));
			else if (value.length() > "className".length() && value.indexOf("className") > -1)
				list.add(CommonUtil.msgToBeanBase(temp.toString()));
			else
				list.add(value);
		}
		return list;
	}

	@Override
	public String toString() {
		Iterator<Object> it = iterator();
		if (!it.hasNext())
			return "" + Constants.CHAR_ARRAYLIST_BRACKET_LEFT + Constants.CHAR_ARRAYLIST_BRACKET_RIGHT;

		StringBuilder sb = new StringBuilder();
		sb.append(Constants.CHAR_ARRAYLIST_BRACKET_LEFT);
		for (;;) {
			Object e = it.next();
			sb.append(e == this ? "(this Collection)" : e);
			if (!it.hasNext())
				return sb.append(Constants.CHAR_ARRAYLIST_BRACKET_RIGHT).toString();
			sb.append(Constants.CHAR_ARRAYLIST_SEPARATE);
		}
	}
}
