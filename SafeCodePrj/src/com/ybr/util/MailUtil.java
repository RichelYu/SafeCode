package com.ybr.util;

import java.math.BigInteger;
import java.util.Date;

import com.ybr.VarCache;
import com.ybr.entity.ent.Mail;

/**
 * 邮件工具
 * @author Hanf_R
 *
 */
public class MailUtil {
	
	/**
	 * 发送邮箱
	 * @param mailAdd
	 * @return 验证码
	 */
	public static String sendValidateMail(String mailAddress) throws Exception{
		Mail m = new Mail();
		m.sendToEmail = mailAddress;
		m.vCode = geneValidateCode(mailAddress);
		VarCache.mailQueue.put(m); // 加入邮箱发送队列  用于邮箱发送线程进行发送
		return m.vCode;
	}
	
	/**
	 * 生成验证码
	 * @param mail
	 * @return
	 */
	private static String geneValidateCode(String mail) throws Exception{
		/**
		 * vCodeSource:
		 * [mail][date.longValue]safeCodeVCode
		 * md5
		 * 1234567890....
		 * vCodeBytes
		 * 12 ^ 34 ^ 56 ^ 78 ^ 90 ^ ...
		 * vCode
		 * vCodeBytes.toString 
		 */
		String vCodeSource = mail + new Date().getTime() + "safeCodeVCode";
		String md5 = CommonUtil.getStringMD5(vCodeSource);
		BigInteger bi = new BigInteger(md5, 16);
		byte[] md5Bytes = bi.toByteArray();
		byte[] vCodeBytes = new byte[2];
		vCodeBytes[0] = md5Bytes[0];
		vCodeBytes[1] = md5Bytes[1];
		for (int i = 4; i < md5Bytes.length; i += 2)
		{
			vCodeBytes[0] ^= md5Bytes[i - 2];
			vCodeBytes[1] ^= md5Bytes[i - 1];
		}
		return String.format("%02x%02x", vCodeBytes[0], vCodeBytes[1]).toUpperCase();
	}
}
