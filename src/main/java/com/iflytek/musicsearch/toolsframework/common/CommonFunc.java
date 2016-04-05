package com.iflytek.musicsearch.toolsframework.common;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;

import org.apache.commons.lang3.StringUtils;

public class CommonFunc {

	private static String macStr = "";
	
	/**
	 * 获取本机mac地址
	 * 注意在linux下， /etc/sysconfig/network中的hostname的值，在 /etc/hosts中对应的ip地址不能是127.0.0.1
	 * 否则获取不到mac地址（127.0.0.1上还回地址，mac地址是空）
	 * @return
	 * @throws SocketException
	 * @throws UnknownHostException
	 */
	public static String getLocalMac() throws SocketException, UnknownHostException {
		if(StringUtils.isBlank(macStr)){
			InetAddress ia = InetAddress.getLocalHost();
			System.out.println("ia信息为： " + ia.toString());
			byte[] mac = NetworkInterface.getByInetAddress(ia).getHardwareAddress();
			StringBuffer sb = new StringBuffer("");
			for(int i=0; i<mac.length; i++) {
				//字节转换为整数
				int temp = mac[i]&0xff;
				String str = Integer.toHexString(temp);
				if(str.length() == 1) {
					sb.append("0" + str);
				}else {
					sb.append(str);
				}
			}
			macStr = sb.toString();
			System.out.println("本地mac地址为：" + macStr);
		}
		return macStr;
	}
	
}
