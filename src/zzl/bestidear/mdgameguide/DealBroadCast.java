package zzl.bestidear.mdgameguide;

import java.util.Timer;
import java.util.TimerTask;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothInputDevice;
import android.bluetooth.BluetoothProfile;
import android.bluetooth.BluetoothProfile.ServiceListener;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.hardware.usb.UsbManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;
import android.os.Handler;
import android.util.Log;

public class DealBroadCast extends BroadcastReceiver {

	private Handler mHandler;
	private Activity mActivity;
	private MySharedPreferences mySharedPreferences;

	DealBroadCast(Handler handler, Activity activity) {
		mHandler = handler;
		mActivity = activity;
		mySharedPreferences = new MySharedPreferences((MainActivity) activity);
	}

	@Override
	public void onReceive(Context context, Intent intent) {
		// TODO Auto-generated method stub
	/*	 if(intent.getAction().equals(MainActivity.INTENT_ACTION_USB) ||
				 intent.getAction().equals(Intent.ACTION_MEDIA_MOUNTED)){

			 mHandler.obtainMessage(MainActivity.HANDLE_HAND_USB_CONNECT_STATE,true).sendToTarget();
		 
		 }else if(intent.getAction().equals(Intent.ACTION_MEDIA_EJECT) ){
			 
		  mHandler.obtainMessage
		  (MainActivity.HANDLE_HAND_USB_CONNECT_STATE,false).sendToTarget();
		  
		 }else*/
		
		if(intent.getAction().equals(UsbManager.ACTION_USB_DEVICE_ATTACHED)){
			
			 mHandler.obtainMessage
			  (MainActivity.HANDLE_HAND_USB_CONNECT_STATE,true).sendToTarget();
			
		}else if(intent.getAction().equals(UsbManager.ACTION_USB_DEVICE_DETACHED)){
		
			 mHandler.obtainMessage
			  (MainActivity.HANDLE_HAND_USB_CONNECT_STATE,false).sendToTarget();
			
		}else if (intent.getAction().equals(WifiManager.WIFI_STATE_CHANGED_ACTION)
				|| WifiManager.NETWORK_STATE_CHANGED_ACTION.equals(intent
						.getAction())) {

			mHandler.obtainMessage(MainActivity.HANDLE_WIFI_STATE_NEW)
			.sendToTarget();
			
		} else if (intent.getAction().equals(
				ConnectivityManager.CONNECTIVITY_ACTION)) {
			ConnectivityManager conMan = (ConnectivityManager) ((MainActivity) mActivity)
					.getSystemService(Context.CONNECTIVITY_SERVICE);

			NetworkInfo info_eth = conMan
					.getNetworkInfo(ConnectivityManager.TYPE_ETHERNET);
			if (info_eth != null && info_eth.isAvailable()) {

				if (info_eth.getType() == ConnectivityManager.TYPE_ETHERNET) {
					mHandler.obtainMessage(MainActivity.HANDLE_ETH_STATE,
							"true").sendToTarget();
				}
			} else {
				mHandler.obtainMessage(MainActivity.HANDLE_ETH_STATE, "false")
						.sendToTarget();
			}

			NetworkInfo info_wifi = conMan
					.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
			if (info_wifi != null && info_wifi.isConnected()) {
				if (info_wifi.getType() == ConnectivityManager.TYPE_WIFI) {

					mHandler.obtainMessage(MainActivity.HANDLE_WIFI_STATE_NEW)
							.sendToTarget();
				}
			}

		} else if (intent.getAction().equals(
				BluetoothDevice.ACTION_BOND_STATE_CHANGED)) {

			BluetoothDevice device = null;
			device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
			switch (device.getBondState()) {
			case BluetoothDevice.BOND_BONDING:
				Log.d("BlueToothTestActivity", "pairing!!!!!!!");
				break;
			case BluetoothDevice.BOND_BONDED:
				Log.d("BlueToothTestActivity", "paired!!!!!!!");
				// connect(device);//连接设备
				connnetHidDevice(device);
				break;
			case BluetoothDevice.BOND_NONE:
				mHandler.obtainMessage(
						MainActivity.HANDLE_HAND_BLUETOOTH_BOND_NONE)
						.sendToTarget();

				Log.d("BlueToothTestActivity", "cancel-pair!!!!!!");
			default:
				break;
			}
		} else if (intent.getAction().equals(
				WifiManager.SUPPLICANT_STATE_CHANGED_ACTION)) {
			// 密码输入错误,重新连接以前的ssid
			int authState = intent.getIntExtra(
					WifiManager.EXTRA_SUPPLICANT_ERROR, 0);
			if (authState == WifiManager.ERROR_AUTHENTICATING) {
				mHandler.obtainMessage(
						MainActivity.HANDLE_NETWORK_WIRELESS_AUTH_PROBLEM)
						.sendToTarget();
			}

		} else if (intent.getAction().equals(
				WifiManager.SCAN_RESULTS_AVAILABLE_ACTION)) {
			// ||
			// WifiManager.CONFIGURED_NETWORKS_CHANGED_ACTION.equals(intent.getAction())
			// ||
			// WifiManager.LINK_CONFIGURATION_CHANGED_ACTION.equals(intent.getAction())){
			if (!mySharedPreferences.getDisableScanResult().equals("")) {
				mHandler.obtainMessage(MainActivity.HANDLE_WIFI_SCAN_RESULT)
						.sendToTarget();
				mySharedPreferences.putDisableScanResult("");
			}
			
		}
	}

	private void connnetHidDevice(final BluetoothDevice device) {

		BluetoothAdapter mBluetoothAdapter = BluetoothAdapter
				.getDefaultAdapter();
		mBluetoothAdapter.getProfileProxy(mActivity, new ServiceListener() {

			@Override
			public void onServiceDisconnected(int arg0) {
				// TODO Auto-generated method stub
			}

			@Override
			public void onServiceConnected(int arg0, BluetoothProfile arg1) {
				// TODO Auto-generated method stub
				boolean connectFlag = false;

				BluetoothInputDevice mService = (BluetoothInputDevice) arg1;
				if (mService != null) {
					connectFlag = mService.connect(device);
				/*	if (connectFlag)
						mHandler.obtainMessage(
								MainActivity.HANDLE_HAND_BLUETOOTH_CONNECT_STATE)
								.sendToTarget();
								*/
				}
			}
		}, BluetoothProfile.INPUT_DEVICE);
	}

}
