package com.ybr.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

import com.ybr.Constants;

public class SocketUtil {
	/**
	 * 发送文件
	 * 
	 * @param sc
	 * @param filePath
	 * @return
	 * @throws IOException
	 */
	public static void sendFile(SocketChannel sc, String filePath) throws IOException, FileNotFoundException {
		File f = new File(filePath);
		int fileLength = 0;
		byte[] buffer = new byte[4];
		int sendLength = 0;
		if (!f.exists()) {
			fileLength = 0;
			// 如果文件不存在发送长度0
			while ((sendLength += sc
					.write(ByteBuffer.wrap(buffer, sendLength, buffer.length - sendLength))) < buffer.length)
				;
			throw new FileNotFoundException("文件" + filePath + "不存在");
		}
		fileLength = (int) f.length();
		buffer = CommonUtil.intToBytes(fileLength);
		FileInputStream inS = new FileInputStream(f);
		// 发送文件长度 4byte
		while ((sendLength += sc
				.write(ByteBuffer.wrap(buffer, sendLength, buffer.length - sendLength))) < buffer.length)
			;

		while (sendLength < fileLength) {
			buffer = new byte[10240];// 重新初始化buffer
			int readLength = inS.read(buffer, 0, buffer.length);
			int sLength = 0;
			while ((sLength += sc.write(ByteBuffer.wrap(buffer, sLength, readLength - sLength))) < readLength)
				;
			sendLength += sLength;
		}
		inS.close();
	}

	public static void recevieFile(SocketChannel sc, String filePath) throws IOException {
		File f = new File(filePath);
		if (!f.getParentFile().exists())
			f.getParentFile().mkdirs();
		FileOutputStream outS = new FileOutputStream(f);
		// 初始化缓冲区
		ByteBuffer buffer = ByteBuffer.allocate(4);
		// 初始化已读长度
		int bufferReadSize = 0;
		// 读取报文 长度
		do {
			bufferReadSize += sc.read(buffer);
		} while (bufferReadSize < 4);
		byte[] bytes = buffer.array();
		buffer.clear();
		int fileLength = CommonUtil.bytesToInt(bytes, 0);
		buffer = ByteBuffer.allocate(10240);
		int receiveLength = 0;

		while (receiveLength < fileLength) {
			buffer.clear();
			int rl = sc.read(buffer);
			outS.write(buffer.array(), 0, rl);
			receiveLength += rl;
		}

		outS.close();

	}

	/**
	 * 发送报文
	 * 
	 * @param sc
	 * @param msg
	 * @throws IOException
	 */
	public static void sendMsg(SocketChannel sc, String msg) throws Exception {
		byte[] msgBuffer = msg.getBytes(Constants.MSG_ENCODING);
		msgBuffer = CryptUtil.encrypt(msgBuffer); // 加密报文
		int sendLength = 0;
		byte[] length = CommonUtil.intToBytes(msgBuffer.length);
		while ((sendLength += sc
				.write(ByteBuffer.wrap(length, sendLength, length.length - sendLength))) < length.length)
			;
		sendLength = 0;
		while ((sendLength += sc
				.write(ByteBuffer.wrap(msgBuffer, sendLength, msgBuffer.length - sendLength))) < msgBuffer.length)
			;
		LogUtil.WriteLog(Constants.LOG_LEVEL_DEBUG, "send msg length -> " + sendLength);
		LogUtil.WriteLog(Constants.LOG_LEVEL_INFO, "send msg -> " + msg);
	}

	/**
	 * 接收报文
	 * 
	 * @param sc
	 * @return
	 * @throws IOException
	 */
	public static String receiveMsg(SocketChannel sc) throws Exception {
		long nowSystemTime = System.currentTimeMillis();

		// 初始化缓冲区
		ByteBuffer buffer = ByteBuffer.allocate(100);
		// 初始化已读长度
		int bufferReadSize = 0;
		// 读取报文 长度
		do {
			bufferReadSize += sc.read(buffer);
			if (System.currentTimeMillis() - nowSystemTime > 30000)
				throw new Exception("receive time out");
		} while (bufferReadSize < 4);
		// 获取 buffer中的字节
		byte[] bytes = buffer.array();

		// 清空 buffer
		buffer.clear();
		// 取得报文总长
		int totalLength = CommonUtil.bytesToInt(bytes, 0);
		// 初始化 报文
		byte[] byteBuffer = new byte[totalLength];
		// 拷贝 除长度外 多余报文
		System.arraycopy(bytes, 4, byteBuffer, 0, bufferReadSize - 4);
		// 修正已读长度
		bufferReadSize -= 4;
		// 读取报文正文
		while (bufferReadSize < totalLength) {
			if (System.currentTimeMillis() - nowSystemTime > 40000)
				throw new Exception("receive time out");
			int tempSize = sc.read(buffer);
			bytes = buffer.array();
			System.arraycopy(bytes, 0, byteBuffer, bufferReadSize, tempSize);
			bufferReadSize += tempSize;
			buffer.clear();

		}
		byteBuffer = CryptUtil.decrypt(byteBuffer); // 结密报文
		String msg = new String(byteBuffer, Constants.MSG_ENCODING);
		LogUtil.WriteLog(Constants.LOG_LEVEL_INFO, "receive msg -> " + msg);
		return msg;
	}
}
