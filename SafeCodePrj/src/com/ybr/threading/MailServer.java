package com.ybr.threading;

import java.util.Properties;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;


import com.ybr.Config;
import com.ybr.Constants;
import com.ybr.VarCache;
import com.ybr.entity.ent.Mail;
import com.ybr.util.LogUtil;

/**
 * 邮件服务
 * 
 * @author Hanf_R
 *
 */
public class MailServer implements Runnable {
	private Session session;
	private Transport ts;

	private static Properties getMailProperties() {
		Properties prop = new Properties();
		prop.setProperty("mail.host", Config.MailServerURL);
		prop.setProperty("mail.smtp.port", Config.MailServerPort);
		prop.setProperty("mail.transport.protocol", "smtp");
		prop.setProperty("mail.smtp.auth", "true");
		prop.setProperty("mail.smtp.starttls.enable", "true");
		LogUtil.WriteLog(5, prop.toString());
		return prop;
	}

	/**
	 * 获取 邮箱服务器的连接
	 * 
	 * @throws MessagingException
	 */
	private void connectMailServer() throws MessagingException {
		if (ts != null)
			ts.close();
		session = Session.getInstance(getMailProperties());
		// 日志等级为debug时 mail的日志开启
		if (Config.LOGLevel == Constants.LOG_LEVEL_DEBUG)
			session.setDebug(true);
		ts = session.getTransport();
		ts.connect(Config.MailServerURL, Config.MailAddress, Config.MailPswd);
	}

	private MimeMessage createSimpleMail(Mail m) throws MessagingException {
		// 创建邮件对象
		MimeMessage message = new MimeMessage(session);
		// 指明邮件的发件人
		message.setFrom(new InternetAddress(Config.MailAddress));
		// 指明邮件的收件人，现在发件人和收件人是一样的，那就是自己给自己发
		message.setRecipient(Message.RecipientType.TO, new InternetAddress(m.sendToEmail));
		// 邮件的标题
		message.setSubject(String.format("验证码[%s]", m.vCode));
		// 添加头 防止检测垃圾邮件
		message.setHeader("X-Mailer", "Microsoft Outlook Express 6.00.2900.2869");
		// 邮件的文本内容
		message.setContent(String.format("您好，您本次获取的验证码为[%s]", m.vCode), "text/html;charset=UTF-8");
		// 返回创建好的邮件对象
		return message;
	}

	@Override
	public void run() {
		try {
			connectMailServer();
		} catch (Exception e) {
			LogUtil.WriteLog(Constants.LOG_LEVEL_FATAL, e);
			return;
		}
		Mail m = null;
		while (true) {
			try {
				m = VarCache.mailQueue.take();
				if (m == null)
					continue;
				Message mmsg = createSimpleMail(m);
				ts.sendMessage(mmsg, mmsg.getAllRecipients());
			} catch (MessagingException e) {
				LogUtil.WriteLog(Constants.LOG_LEVEL_ERROR, e);
				LogUtil.WriteLog(Constants.LOG_LEVEL_ERROR, "出现错误,尝试重新连接");
				try {
					if (m != null)
						VarCache.mailQueue.put(m);
					connectMailServer();
					Thread.sleep(500); // 防止死循环一直重新连接
				} catch (Exception e1) {
					LogUtil.WriteLog(Constants.LOG_LEVEL_ERROR, e);
				}
			} catch (Exception e) {
				try {
					if (ts != null)
						ts.close();
				} catch (MessagingException e1) {
					LogUtil.WriteLog(Constants.LOG_LEVEL_ERROR, e1);
				}
				LogUtil.WriteLog(Constants.LOG_LEVEL_ERROR, e);
				LogUtil.WriteLog(Constants.LOG_LEVEL_FATAL, "位置异常,退出");
			}
		}
	}
}
