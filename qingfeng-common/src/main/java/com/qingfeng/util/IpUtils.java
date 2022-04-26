package com.qingfeng.util;

import javax.servlet.http.HttpServletRequest;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.UnknownHostException;
import java.util.Enumeration;

/**
 * @ProjectName IpUtils
 * @author Administrator
 * @version 1.0.0
 * @Description 获取IP方法
 * @createTime 2022/1/17 9:56
 */
public class IpUtils
{
    // 获取ip地址
    public static String getIpAddress() {
        try {
            Enumeration<NetworkInterface> allNetInterfaces = NetworkInterface.getNetworkInterfaces();
            InetAddress ip = null;
            while (allNetInterfaces.hasMoreElements()) {
                NetworkInterface netInterface = (NetworkInterface) allNetInterfaces.nextElement();
                // 过滤回环、虚拟、断开的网卡
                if (netInterface.isLoopback() || netInterface.isVirtual() || !netInterface.isUp()) {
                    continue;
                }

                // 过滤虚拟机网卡
                if (netInterface.getDisplayName().contains("Virtual")) {
                    continue;
                }

                // 过滤VPN
                if (netInterface.getDisplayName().contains("VPN")) {
                    continue;
                }

                // 获取真实可用的网卡
                Enumeration<InetAddress> addresses = netInterface.getInetAddresses();
                while (addresses.hasMoreElements()) {
                    ip = addresses.nextElement();
                    if (ip != null && ip instanceof Inet4Address) { // 必须是IPv4
                        return ip.getHostAddress();
                    }
                }
            }
        } catch (Exception e) {
            // _logger.error("IP地址获取失败", e);
            e.printStackTrace();
        }
        return "";
    }

    // 获取mac地址
    public static String getMacAddress() {
        try {
            Enumeration<NetworkInterface> allNetInterfaces = NetworkInterface.getNetworkInterfaces();
            byte[] mac = null;
            while (allNetInterfaces.hasMoreElements()) {
                NetworkInterface netInterface = (NetworkInterface) allNetInterfaces.nextElement();
                // 过滤回环、虚拟、断开的网卡
                if (netInterface.isLoopback() || netInterface.isVirtual() || !netInterface.isUp()) {
                    continue;
                }

                // 过滤虚拟机网卡
                if (netInterface.getDisplayName().contains("Virtual")) {
                    continue;
                }

                // 获取真实可用的网卡
                mac = netInterface.getHardwareAddress();
                if (mac != null) {
                    StringBuilder sb = new StringBuilder();
                    for (int i = 0; i < mac.length; i++) {
                        sb.append(String.format("%02X%s", mac[i], (i < mac.length - 1) ? "-" : ""));
                    }
                    if (sb.length() > 0) {
                        return sb.toString();
                    }
                }
            }
        } catch (Exception e) {
            // _logger.error("MAC地址获取失败", e);
            e.printStackTrace();
        }
        return "";
    }
}