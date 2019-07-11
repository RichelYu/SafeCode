package com.ybr.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.lang.reflect.Field;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Date;
import java.util.Map;
import java.util.UUID;

import com.ybr.Constants;
import com.ybr.entity.MsgHead;
import com.ybr.entity.ObjBase;
import com.ybr.exception.MsgException;
import com.ybr.exception.UnSupportTypeException;
import com.ybr.struct.ArrayList;
import com.ybr.struct.HashMap;

/**
 * 通用工具类
 * 
 * @author Administrator
 *
 */
public class CommonUtil {

	/**
	 * 异常转 String
	 * 
	 * @param e
	 * @return
	 */
	public static String exceptionToString(Exception e) {
		if (e == null)
			return "";
		if (e.getClass().equals(MsgException.class))
			return e.getMessage();
		StringBuffer msg = new StringBuffer();
		msg.append("\n" + e.toString());
		for (StackTraceElement stackTraceElement : e.getStackTrace())
			msg.append("\n" + stackTraceElement);
		return msg.toString();
	}

	/**
	 * 将报文翻译成消息基类
	 * 
	 * @param msg
	 * @return
	 * @throws ClassNotFoundException
	 * @throws IllegalAccessException
	 * @throws InstantiationException
	 */
	public static ObjBase msgToBeanBase(String msg) throws Exception {
		if (msg == null || msg.length() < 1)
			return null;
		if (msg.charAt(0) != Constants.CHAR_BEANBASE_BRACKET_LEFT
				|| msg.charAt(msg.length() - 1) != Constants.CHAR_BEANBASE_BRACKET_RIGHT)
			throw new Exception("错误的报文格式");
		Map<String, String> map = new java.util.HashMap<String, String>();
		for (int i = 2; i < msg.length() - 1; i++) {
			String key = "";
			String value = "";
			while (i < msg.length() && msg.charAt(i) != Constants.CHAR_MSG_EQUAL)
				key += "" + msg.charAt(i++);
			i++;
			while (i < msg.length() - 1 && msg.charAt(i) != Constants.CHAR_MSG_SEPARATE) {
				int a_b_num = 0;
				int b_b_num = 0;
				int h_b_num = 0;
				if (msg.charAt(i) == Constants.CHAR_ARRAYLIST_BRACKET_LEFT)
					a_b_num++;
				else if (msg.charAt(i) == Constants.CHAR_BEANBASE_BRACKET_LEFT)
					b_b_num++;
				else if (msg.charAt(i) == Constants.CHAR_HASHMAP_BRACKET_LEFT)
					h_b_num++;
				do {
					if (msg.charAt(i) == Constants.CHAR_ARRAYLIST_BRACKET_RIGHT)
						a_b_num--;
					else if (msg.charAt(i) == Constants.CHAR_BEANBASE_BRACKET_RIGHT)
						b_b_num--;
					else if (msg.charAt(i) == Constants.CHAR_HASHMAP_BRACKET_RIGHT)
						h_b_num--;
					value += msg.charAt(i++);
					if (i < msg.length() && msg.charAt(i) == Constants.CHAR_ARRAYLIST_BRACKET_LEFT)
						a_b_num++;
					else if (i < msg.length() && msg.charAt(i) == Constants.CHAR_BEANBASE_BRACKET_LEFT)
						b_b_num++;
					else if (i < msg.length() && msg.charAt(i) == Constants.CHAR_HASHMAP_BRACKET_LEFT)
						h_b_num++;
				} while (a_b_num != 0 || h_b_num != 0 || b_b_num != 0);
			}
			map.put(key, value);
		}
		Class<?> c = Class.forName(map.get("className"));
		Object obj = c.newInstance();
		Field[] fields = c.getFields();
		for (Field field : fields)
			if (map.get(field.getName()) != null) {
				field.set(obj, stringToOwnType(field, map.get(field.getName())));
			}
		return (ObjBase) obj;
	}

	/**
	 * 将String 转化为 相对应的对象
	 * 
	 * @param f
	 *            Field 对象
	 * @param value
	 *            String 值
	 * @return Object
	 * @throws Exception
	 */
	public static Object stringToOwnType(Field f, String value) throws Exception {
		if (f == null || value == null || value.length() < 1)
			return null;
		return stringToOwnType(f.getType(), value);
	}

	/**
	 * 将String 转化为 相对应的对象
	 * 
	 * @param c
	 *            Class 对象
	 * @param value
	 *            String 值
	 * @return Object
	 * @throws Exception
	 */
	public static Object stringToOwnType(Class<?> c, String value) throws Exception {
		if (c == null || value == null || value.length() < 1)
			return null;
		if (Byte.class.equals(c) || byte.class.equals(c)) {
			return Byte.parseByte(value);
		} else if (Short.class.equals(c) || short.class.equals(c)) {
			return Short.parseShort(value);
		} else if (Integer.class.equals(c) || int.class.equals(c)) {
			return Integer.parseInt(value);
		} else if (Long.class.equals(c) || long.class.equals(c)) {
			return Long.parseLong(value);
		} else if (Float.class.equals(c) || float.class.equals(c)) {
			return Float.parseFloat(value);
		} else if (Double.class.equals(c) || double.class.equals(c)) {
			return Double.parseDouble(value);
		} else if (Boolean.class.equals(c) || boolean.class.equals(c)) {
			return Boolean.parseBoolean(value);
		} else if (Character.class.equals(c) || char.class.equals(c)) {
			return value.charAt(0);
		} else if (HashMap.class.equals(c)) {
			return HashMap.parseHashMap(value);
		} else if (ArrayList.class.equals(c)) {
			return ArrayList.parseArrayList(value);
		} else if (String.class.equals(c)) {
			return msgToString(value);
		} else if (MsgHead.class.equals(c)) {
			return MsgHead.praseMsgHead(value);
		} else if (ObjBase.class.isAssignableFrom(c)) {
			return msgToBeanBase(value);
		} else if (Date.class.equals(c)) {
			return new Date(Long.parseLong(value));
		} else {
			throw new UnSupportTypeException();
		}
	}

	/**
	 * 将对象转为string 默认调用 toString方法 可以自定义 obj to string 的方式
	 * 
	 * @param obj
	 * @return
	 */
	public static String objectToString(Object obj) {
		if (obj == null)
			return "";
		if (Date.class.isAssignableFrom(obj.getClass()))
			return String.valueOf(((Date) obj).getTime());
		else if (String.class.equals(obj.getClass()))
			return stringToMsg(obj.toString());
		return obj.toString();
	}

	private static String stringToMsg(String context) {
		context = context.replaceAll("" + Constants.CHAR_STRING_MSG_HEAD, Constants.CHAR_STRING_MSG_HEAD + "000");
		context = context.replaceAll("\\" + Constants.CHAR_BEANBASE_BRACKET_LEFT,
				Constants.CHAR_STRING_MSG_HEAD + "001");
		context = context.replaceAll("\\" + Constants.CHAR_BEANBASE_BRACKET_RIGHT,
				Constants.CHAR_STRING_MSG_HEAD + "002");
		context = context.replaceAll("\\" + Constants.CHAR_HASHMAP_BRACKET_LEFT,
				Constants.CHAR_STRING_MSG_HEAD + "003");
		context = context.replaceAll("\\" + Constants.CHAR_HASHMAP_BRACKET_RIGHT,
				Constants.CHAR_STRING_MSG_HEAD + "004");
		context = context.replaceAll("" + Constants.CHAR_HASHMAP_SEPARATE_KEYVALUE,
				Constants.CHAR_STRING_MSG_HEAD + "005");
		context = context.replaceAll("" + Constants.CHAR_HASHMAP_SEPARATE_GROUP,
				Constants.CHAR_STRING_MSG_HEAD + "006");
		context = context.replaceAll("\\" + Constants.CHAR_ARRAYLIST_BRACKET_LEFT,
				Constants.CHAR_STRING_MSG_HEAD + "007");
		context = context.replaceAll("\\" + Constants.CHAR_ARRAYLIST_BRACKET_RIGHT,
				Constants.CHAR_STRING_MSG_HEAD + "008");
		context = context.replaceAll("" + Constants.CHAR_ARRAYLIST_SEPARATE, Constants.CHAR_STRING_MSG_HEAD + "009");
		context = context.replaceAll("" + Constants.CHAR_MSG_SEPARATE, Constants.CHAR_STRING_MSG_HEAD + "010");
		context = context.replaceAll("" + Constants.CHAR_MSG_EQUAL, Constants.CHAR_STRING_MSG_HEAD + "011");
		return context;
	}

	private static String msgToString(String context) {
		context = context.replaceAll(Constants.CHAR_STRING_MSG_HEAD + "000", "" + Constants.CHAR_STRING_MSG_HEAD);
		context = context.replaceAll(Constants.CHAR_STRING_MSG_HEAD + "001", "" + Constants.CHAR_BEANBASE_BRACKET_LEFT);
		context = context.replaceAll(Constants.CHAR_STRING_MSG_HEAD + "002",
				"" + Constants.CHAR_BEANBASE_BRACKET_RIGHT);
		context = context.replaceAll(Constants.CHAR_STRING_MSG_HEAD + "003", "" + Constants.CHAR_HASHMAP_BRACKET_LEFT);
		context = context.replaceAll(Constants.CHAR_STRING_MSG_HEAD + "004", "" + Constants.CHAR_HASHMAP_BRACKET_RIGHT);
		context = context.replaceAll(Constants.CHAR_STRING_MSG_HEAD + "005",
				"" + Constants.CHAR_HASHMAP_SEPARATE_KEYVALUE);
		context = context.replaceAll(Constants.CHAR_STRING_MSG_HEAD + "006",
				"" + Constants.CHAR_HASHMAP_SEPARATE_GROUP);
		context = context.replaceAll(Constants.CHAR_STRING_MSG_HEAD + "007",
				"" + Constants.CHAR_ARRAYLIST_BRACKET_LEFT);
		context = context.replaceAll(Constants.CHAR_STRING_MSG_HEAD + "008",
				"" + Constants.CHAR_ARRAYLIST_BRACKET_RIGHT);
		context = context.replaceAll(Constants.CHAR_STRING_MSG_HEAD + "009", "" + Constants.CHAR_ARRAYLIST_SEPARATE);
		context = context.replaceAll(Constants.CHAR_STRING_MSG_HEAD + "010", "" + Constants.CHAR_MSG_SEPARATE);
		context = context.replaceAll(Constants.CHAR_STRING_MSG_HEAD + "011", "" + Constants.CHAR_MSG_EQUAL);
		return context;
	}

	/**
	 * 将 int32转换为 byte[4]
	 * 
	 * @param temp
	 *            int类型的值
	 * @return byte[4]
	 */
	public static byte[] intToBytes(int temp) {
		byte[] b = new byte[4];
		b[0] = (byte) ((temp >> 24) & 0xff);
		b[1] = (byte) ((temp >> 16) & 0xff);
		b[2] = (byte) ((temp >> 8) & 0xff);
		b[3] = (byte) (temp & 0xff);
		return b;
	}

	/**
	 * 将 byte[4]转换为 int32
	 * 
	 * @param b
	 *            要转换的byte[]
	 * @param off
	 *            第一位小标
	 * @return int32
	 */
	public static int bytesToInt(byte[] b, int off) {
		if (b.length - off < 4)
			return 0;
		return (((int) (b[off]) & 0xff) << 24) + (((int) b[off + 1] & 0xff) << 16) + (((int) b[off + 2] & 0xff) << 8)
				+ ((int) b[off + 3] & 0xff);
	}

	/**
	 * 生成RootId
	 * 
	 * @return
	 */
	public static String randomRootId() throws Exception {
		String uuStr = UUID.randomUUID().toString();
		return uuStr.substring(uuStr.length() - 10).toUpperCase();
	}

	/**
	 * 字符串补长 使用默认字符进行 左 补长 left -> true right -> false
	 * 
	 * @param src
	 * @param length
	 * @return
	 * @throws Exception
	 */
	public static String stringCompletion(String src, int length) {
		return stringCompletion(true, src, length, Constants.MSG_DEFAULT_COMPLETIONCHAR);
	}

	/**
	 * 字符串补长 使用默认字符进行补长 left -> true right -> false
	 * 
	 * @param direction
	 *            补长方向 左真右假
	 * @param src
	 *            源字符串
	 * @param length
	 * @return
	 * @throws Exception
	 */
	public static String stringCompletion(boolean direction, String src, int length) {
		return stringCompletion(direction, src, length, Constants.MSG_DEFAULT_COMPLETIONCHAR);
	}

	/**
	 * 字符串补长 left -> true right -> false
	 * 
	 * @param direction
	 *            补长方向 左真右假
	 * @param src
	 *            源字符串
	 * @param length
	 *            目标长度
	 * @param completChar
	 *            填充字符
	 * @return
	 * @throws Exception
	 */
	public static String stringCompletion(boolean direction, String src, int length, char completChar) {
		if (src.length() >= length)
			return src;

		StringBuffer str = new StringBuffer(src);
		if (direction) // 左补长
			for (int i = 0; i < length - src.length(); i++)
				str.insert(0, completChar);
		else // 右补长
			for (int i = 0; i < length - src.length(); i++)
				str.append(completChar);
		return str.toString();
	}

	/**
	 * 获取文件MD5
	 * 
	 * @param file
	 *            文件对象
	 * @return
	 * @throws NoSuchAlgorithmException
	 * @throws IOException
	 */
	public static String getFileMD5(String path) throws NoSuchAlgorithmException, IOException, Exception {
		String MD5 = "";
		File file = new File(path);
		if (file.exists() && file.isFile()) {
			FileInputStream fis = null;
			try {
				fis = new FileInputStream(file);
				MessageDigest md = MessageDigest.getInstance("MD5");
				byte[] buffer = new byte[1024];
				int length = -1;
				while ((length = fis.read(buffer, 0, 1024)) != -1) {
					md.update(buffer, 0, length);
				}
				BigInteger bigInt = new BigInteger(1, md.digest());
				MD5 = bigInt.toString(16);
				MD5 = stringCompletion(true, MD5, 32, '0'); // 左补长
			} catch (NoSuchAlgorithmException e) {
				throw e;
			} catch (IOException e) {
				throw e;
			} finally {
				if (fis != null)
					fis.close();
			}
		} else
			throw new Exception("路径不是文件或文件不存在");
		return MD5.toUpperCase();
	}

	/**
	 * 获取字符串MD5 用于加密密码
	 * 
	 * @param str
	 * @return
	 * @throws NoSuchAlgorithmException
	 */
	public static String getStringMD5(String str) throws NoSuchAlgorithmException {
		MessageDigest md = MessageDigest.getInstance("MD5");
		byte[] buffer = str.getBytes();
		md.update(buffer);
		BigInteger bigInt = new BigInteger(1, md.digest());
		String MD5 = bigInt.toString(16);
		return stringCompletion(true, MD5, 32, '0').toUpperCase();
	}

	/**
	 * 生成UUid
	 * 
	 * @return
	 */
	public static String getUUid() {
		return stringCompletion(true, UUID.randomUUID().toString().replace("-", ""), 32, '0').toUpperCase();
	}

}
