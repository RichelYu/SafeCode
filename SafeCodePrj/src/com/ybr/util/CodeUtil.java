package com.ybr.util;

import java.math.BigInteger;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import com.ybr.Constants;

public class CodeUtil {

	/**
	 * 获取uid
	 * 
	 * @return
	 */
	public static String getUID() {
		String uid = CommonUtil.getUUid();
		return uid;
	}

	/**
	 * 获取 ukey
	 * 
	 * @param uid
	 * @return
	 */
	public static String getUKey(String uid) throws NoSuchAlgorithmException {
		return CommonUtil.getStringMD5(uid.substring(0, 5) + new Date().getTime() + uid.substring(5));
	}

	/**
	 * 获取安全码 偏移
	 * 
	 * @return
	 * @throws NoSuchAlgorithmException
	 */
	public static String getSKey() throws NoSuchAlgorithmException {
		return CommonUtil.getStringMD5(
				"" + new Date().getTime() + "--" + new SimpleDateFormat("YYYY-MM-dd HH:mm:ss").format(new Date()));
	}

	/**
	 * 获取 渠道 id
	 * @return
	 */
	public static String getCID(){
		BigInteger bi = new BigInteger(CommonUtil.getUUid(), 16);
		bi = bi.add(new BigInteger("Company".getBytes())).add(BigInteger.valueOf(new Date().getTime()));
		return CommonUtil.stringCompletion(true, bi.toString(16).toUpperCase(), 32, '0').toUpperCase();
	}
	
	/**
	 * 获取 渠道 标识
	 * @param cid
	 * @return
	 * @throws NoSuchAlgorithmException
	 */
	public static String getCKey(String cid) throws NoSuchAlgorithmException{
		String skey = cid.substring(0, 10) + new Date().getTime() + cid.substring(10);
		return CommonUtil.getStringMD5(skey);
	}
	
	/**
	 * 获取安全码有效期
	 * @return
	 */
	public static long getValidityTime() {
		return (getNowDate() + 1) * Constants.safeCodeValidityDate;
	}

	/**
	 * 获取当前时间偏移
	 * 
	 * @return
	 */
	private static long getNowDate() {
		return (long) (new Date().getTime() / Constants.safeCodeValidityDate);
	}

	/**
	 * 获取当前安全码
	 * 
	 * @param uid
	 * @param ukey
	 * @param skey
	 * @return
	 * @throws NoSuchAlgorithmException
	 */
	public static String getNowSafeCode(String uid, String ukey, String skey) throws NoSuchAlgorithmException {
		return getSafeCode(uid, ukey, skey, 0);
	}

	/**
	 * 判断安全码是否正确
	 * 
	 * @param uid
	 * @param ukey
	 * @param skey
	 * @param scode
	 * @return
	 * @throws NoSuchAlgorithmException
	 */
	public static boolean equalsSafeCode(String uid, String ukey, String skey, String scode)
			throws NoSuchAlgorithmException {
		ArrayList<String> list = new ArrayList<String>();
		list.add(getSafeCode(uid, ukey, skey, -1));
		list.add(getSafeCode(uid, ukey, skey, 0));
		list.add(getSafeCode(uid, ukey, skey, 1));
		return list.contains(scode);
	}

	/**
	 * 获取安全码
	 * 
	 * @param uid
	 * @param ukey
	 * @param skey
	 * @param shift
	 * @return
	 * @throws NoSuchAlgorithmException
	 */
	public static String getSafeCode(String uid, String ukey, String skey, int shift) throws NoSuchAlgorithmException {
		
		long time_with_shift = getNowDate() + shift;
		BigInteger bi_uid = new BigInteger(uid, 16);
		BigInteger bi_ukey = new BigInteger(ukey, 16);
		BigInteger bi_skey = new BigInteger(skey, 16);
		BigInteger scode = bi_uid.xor(bi_ukey).xor(bi_skey)
				.flipBit(Math.abs(bi_skey.remainder(bi_ukey).remainder(new BigInteger(bi_uid.bitLength() + "", 10)).intValue()));
		BigInteger bi4 = scode.add(new BigInteger(time_with_shift + "").shiftLeft(scode.bitLength() / 2 - 1));
		BigInteger bi5 = new BigInteger(
				CommonUtil.getStringMD5(bi4.toString(16).toUpperCase() + time_with_shift).toUpperCase(), 16);
		byte[] temp = bi5.toByteArray();
		byte[] safe_code = new byte[4];

		safe_code[0] = temp[0];
		safe_code[1] = temp[1];
		safe_code[2] = temp[2];
		safe_code[3] = temp[3];
		for (int i = 8; i < temp.length; i += 4) {
			safe_code[0] ^= temp[i - 3];
			safe_code[1] ^= temp[i - 2];
			safe_code[2] ^= temp[i - 1];
			safe_code[3] ^= temp[i - 0];
		}
		return String.format("%08x", CommonUtil.bytesToInt(safe_code, 0)).toUpperCase();
	}

}
