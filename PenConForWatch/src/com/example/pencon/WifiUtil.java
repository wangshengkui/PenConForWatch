package com.example.pencon;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.net.DhcpInfo;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.util.Log;

/**
 * Created by melo on 2017/9/23.
 */

public class WifiUtil {

    private static final String TAG = "LocationUtils";
    String WIFI_HOTSPOT_SSID = "hh123456";
    private static volatile WifiUtil instance = null;

    private WifiManager mWifiManager;
    private boolean connected=false;
    private Context mContext;
    WifiConfiguration wifiConfig=null;
//    public WifiCipherType wifiCipherType;
    public  enum WifiCipherType {
        WIFICIPHER_WEP, WIFICIPHER_WPA, WIFICIPHER_NOPASS, WIFICIPHER_INVALID
    }
    WifiUtil(Context context) {
        mContext = context;
        mWifiManager = (WifiManager) mContext.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
    }

    public static WifiUtil getInstance(Context context) {
        if (instance == null) {
            synchronized (WifiUtil.class) {
                if (instance == null) {
                    instance = new WifiUtil(context);
                }
            }
        }
        return instance;
    }

    public boolean isWifiApEnabled() {
        try {
            Method method = mWifiManager.getClass().getMethod("isWifiApEnabled");
            method.setAccessible(true);
            return (Boolean) method.invoke(mWifiManager);

        } catch (NoSuchMethodException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }

    public String getLocalIPAddress() {
        WifiInfo wifiInfo = mWifiManager.getConnectionInfo();
        return intToIp(wifiInfo.getIpAddress());
    }

    public String getServerIPAddress() {
        DhcpInfo mDhcpInfo = mWifiManager.getDhcpInfo();
        return intToIp(mDhcpInfo.gateway);
    }

    private static String intToIp(int i) {
        return (i & 0xFF) + "." + ((i >> 8) & 0xFF) + "." + ((i >> 16) & 0xFF) + "."
                + ((i >> 24) & 0xFF);
    }

	/**
     * 创建Wifi热点
     */
    boolean createWifiAp(String ssid,String preSharedKey ) {
    	boolean enable=false;
        if (mWifiManager.isWifiEnabled()) {
            //如果wifi处于打开状态，则关闭wifi,
            mWifiManager.setWifiEnabled(false);          
        }
        WifiConfiguration config = new WifiConfiguration();
//        config.SSID = WIFI_HOTSPOT_SSID;
        config.SSID =ssid;
//        config.preSharedKey = "hh123456";
        config.preSharedKey = preSharedKey;
        config.hiddenSSID = true;
        config.allowedAuthAlgorithms
                .set(WifiConfiguration.AuthAlgorithm.OPEN);//开放系统认证
        config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.TKIP);
        config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.WPA_PSK);
        config.allowedPairwiseCiphers
                .set(WifiConfiguration.PairwiseCipher.TKIP);
        config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.CCMP);
        config.allowedPairwiseCiphers
                .set(WifiConfiguration.PairwiseCipher.CCMP);
        config.status = WifiConfiguration.Status.ENABLED;
        //通过反射调用设置热点
        try {
            Method method = mWifiManager.getClass().getMethod(
                    "setWifiApEnabled", WifiConfiguration.class, Boolean.TYPE);
             enable = (Boolean) method.invoke(mWifiManager, config, true);
/*            if (enable) {
            	wifiConnected=true;
                textView.setText("热点已开启 SSID:" + WIFI_HOTSPOT_SSID + " password:123456789");
            } else {
            	wifiConnected=false;
                textView.setText("创建热点失败");
            }*/
             if (enable) {
				Log.e("zgm", "0409:热点已经开启");
			}
             return enable;
        } catch (Exception e) {
            e.printStackTrace();
            Log.e("zgm", "0409:"+e);
            return enable;
//            wifiConnected=false;
//            textView.setText("创建热点失败");
        }
    }

    public boolean connectWifi(String SSID, String Password, WifiCipherType Type) {
       if (connected==true) {
		if (wifiConfig.SSID==SSID) {
			return true;
		}
		connected=false;
	}
    	
    	if (!mWifiManager.isWifiEnabled()) {
            return false;
        }
        // 开启wifi需要一段时间,要等到wifi状态变成WIFI_STATE_ENABLED
        while (mWifiManager.getWifiState() == WifiManager.WIFI_STATE_ENABLING) {
            try {
                // 避免程序不停循环
                Thread.currentThread();
                Thread.sleep(500);
            }
            catch (InterruptedException ie) {
            }
        }

         wifiConfig = createWifiInfo(SSID, Password, Type);
        if (wifiConfig == null) {
            return false;
        }

        WifiConfiguration tempConfig = isExsits(SSID);

        if (tempConfig != null) {
            mWifiManager.removeNetwork(tempConfig.networkId);
        }

        int netID = mWifiManager.addNetwork(wifiConfig);

        // 断开连接
        mWifiManager.disconnect();
        
        // 设置为true,使其他的连接断开
        boolean bRet = mWifiManager.enableNetwork(netID, true);
//        mWifiManager.reconnect();
        if (bRet) {
			connected=true;
		}
        return bRet;
    }
    
 // 查看以前是否也配置过这个网络
    private  WifiConfiguration isExsits(String SSID) {
        List<WifiConfiguration> existingConfigs = mWifiManager.getConfiguredNetworks();
        for (WifiConfiguration existingConfig : existingConfigs) {
            if (existingConfig.SSID.equals("\"" + SSID + "\"")) {
                return existingConfig;
            }
        }
        return null;
    }

    private  WifiConfiguration createWifiInfo(String SSID, String Password,
            WifiCipherType Type) {
        WifiConfiguration config = new WifiConfiguration();
        config.allowedAuthAlgorithms.clear();
        config.allowedGroupCiphers.clear();
        config.allowedKeyManagement.clear();
        config.allowedPairwiseCiphers.clear();
        config.allowedProtocols.clear();
        config.SSID = "\"" + SSID + "\"";
        if (Type == WifiCipherType.WIFICIPHER_NOPASS) {
            config.wepKeys[0] = "";
            config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
            config.wepTxKeyIndex = 0;
        }
        if (Type == WifiCipherType.WIFICIPHER_WEP) {
            config.preSharedKey = "\"" + Password + "\"";
            config.hiddenSSID = true;
            config.allowedAuthAlgorithms.set(WifiConfiguration.AuthAlgorithm.SHARED);
            config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.CCMP);
            config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.TKIP);
            config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.WEP40);
            config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.WEP104);
            config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
            config.wepTxKeyIndex = 0;
        }
        if (Type == WifiCipherType.WIFICIPHER_WPA) {

            config.preSharedKey = "\"" + Password + "\"";
            config.hiddenSSID = true;
            config.allowedAuthAlgorithms.set(WifiConfiguration.AuthAlgorithm.OPEN);
            config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.TKIP);
            config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.WPA_PSK);
            config.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.TKIP);
            // config.allowedProtocols.set(WifiConfiguration.Protocol.WPA);
            config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.CCMP);
            config.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.CCMP);

        }
        else {
            return null;
        }
        return config;
    }

    /**
     * 关闭热点
     */
    public void closeWifiAp(){
//         wifiManager= (WifiManager) mContext.getSystemService(Context.WIFI_SERVICE);
        if (isWifiApEnabled()){
            try {
                Method method= mWifiManager.getClass().getMethod("getWifiApConfiguration");
                method.setAccessible(true);
                WifiConfiguration config= (WifiConfiguration) method.invoke( mWifiManager);
                Method method2= mWifiManager.getClass().getMethod("setWifiApEnabled",WifiConfiguration.class,boolean.class);
                method2.invoke( mWifiManager,config,false);
            } catch (NoSuchMethodException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 开热点手机获得其他连接手机IP的方法
     * @return 其他手机IP 数组列表
     */
    public ArrayList<String> getConnectedIP(){
        ArrayList<String> connectedIp=new ArrayList<String>();
        try {
            BufferedReader br=new BufferedReader(new FileReader(
                    "/proc/net/arp"));
            String line;
            while ((line=br.readLine())!=null){
                String[] splitted=line.split(" +");
                if (splitted !=null && splitted.length>=4){
                    String ip=splitted[0];
                    if (!ip.equalsIgnoreCase("ip")){
                        connectedIp.add(ip);
                    }
                }
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
 
        return connectedIp;
    }
 
}
