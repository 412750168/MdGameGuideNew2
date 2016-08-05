package zzl.bestidear.mdgameguide;


import java.util.List;

import android.app.Activity;
import android.app.Service;
import android.content.Context;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.util.Log;
import zzl.bestidear.mdgameguide.WifiConfig;
import zzl.bestidear.mdgameguide.WifiConfig.WifiCipherType;

public class SettingWifiThread extends Thread {

	public static final int AP = 1;
	public static final int router = 2;
	
	String ssid;
	String password;
	Activity activity;
	int networkid;
	WifiConfiguration config;
	WifiManager mWifiManager;
	List<ScanResult> results;
	int mode ;
	
	public SettingWifiThread(int mode,String ssid, String password, Activity activity) {
		super();
		this.mode = mode;
		this.ssid = ssid;
		this.password = password;
		this.activity = activity;
		mWifiManager = (WifiManager) activity
				.getSystemService(Context.WIFI_SERVICE);
	}

	@Override
	public void run() {
		//wifi
		// TODO Auto-generated method stub
		/*
		 * 提前获取wifi设备列表，以免造成设备类型获取失败
		 */
		mWifiManager.startScan();
	    results = mWifiManager.getScanResults();
		
/*		try {
			mWifiManager.setWifiEnabled(false);
			Thread.sleep(5000);
			mWifiManager.setWifiEnabled(true);
			Thread.sleep(3000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		*/
		WifiInfo currentInfo = null;
		synchronized (this) {
			/*
			 * 防止重复设置密码
			 */
			if(mode != AP)
			{
				currentInfo = mWifiManager.getConnectionInfo();
				if (currentInfo != null) {
					if (currentInfo.getSSID().equals("\"" + ssid + "\""))
						return;
					mWifiManager.disableNetwork(currentInfo.getNetworkId());// 先关闭已连的wifi
					
					currentInfo = null;
				}
			}
			/*
			 * 调用已有网络配置
			 */
			WifiConfiguration oldConfig = IsExsits(ssid); 
			if (oldConfig != null && (oldConfig.preSharedKey == null || oldConfig.preSharedKey.equals("\"" + password + "\""))) {
				config = oldConfig;
				networkid = config.networkId;
			} else {
				if(oldConfig != null)
					mWifiManager.removeNetwork(networkid);
				
				WifiConfig configmanager = new WifiConfig();
				
				WifiCipherType wifiType ;
				/*
				 * 设置ap设备类型
				 */
				if(mode == AP)
					wifiType = WifiCipherType.WIFICIPHER_WPA;
				else wifiType= getSecurityType(ssid);
				
				Log.d("zzl:::","type ::"+ wifiType+ssid+password);
				config = configmanager.CreateWifiInfo(ssid,
						password, wifiType);
				networkid = mWifiManager.addNetwork(config);
			}
			if (config != null) {

				boolean state = mWifiManager.enableNetwork(networkid, false);
				mWifiManager.saveConfiguration();
				Log.d("zzl:::","guide is connect to wifi");				
			/*	if (!state) {
					//activity.getHandler()
					//		.obtainMessage(WiFiDiscoveryService.MY_ERROR, this)
					//		.sendToTarget();
					Log.d("zzl:::","error");
					mWifiManager.removeNetwork(networkid);
				}else{
					//mWifiManager.saveConfiguration();
				}
				
				*/
			}

		}

	}

	private WifiConfiguration IsExsits(String SSID) {
		List<WifiConfiguration> existingConfigs = mWifiManager
				.getConfiguredNetworks();
		if (existingConfigs == null)
			return null;
		for (WifiConfiguration existingConfig : existingConfigs) {
			if (existingConfig.SSID.equals("\"" + SSID + "\"")) {
				return existingConfig;
			}
		}
		return null;
	}

	private WifiCipherType getSecurityType(String ssid) {
		WifiCipherType security_type = WifiCipherType.WIFICIPHER_INVALID;
		if (results != null) {
			for (ScanResult result : results) {
				// Ignore hidden and ad-hoc networks.
				if (result.SSID == null || result.SSID.length() == 0
						|| result.capabilities.contains("[IBSS]")) {
					continue;
				}
				
				if (result.SSID.equals(ssid)) {
					if (result.capabilities.contains("WEP")) {
						security_type = WifiCipherType.WIFICIPHER_WEP;
					} else if (result.capabilities.contains("PSK")) {
						security_type = WifiCipherType.WIFICIPHER_WPA;
					} else if (result.capabilities.contains("EAP")) {
						security_type = WifiCipherType.WIFICIPHER_WPA;
					} else {
						security_type = WifiCipherType.WIFICIPHER_NOPASS;
					}
					break;
				}
			}
		}
		return security_type;
	}

}
