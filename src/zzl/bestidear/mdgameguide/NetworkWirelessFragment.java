package zzl.bestidear.mdgameguide;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.w3c.dom.Text;

import android.app.Fragment;
import android.app.Instrumentation;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.View.OnKeyListener;
import android.view.ViewGroup;
import android.view.View.OnTouchListener;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;
import android.widget.Toast;

public class NetworkWirelessFragment extends Fragment{

	public enum WifiCipherType {
		WIFICIPHER_WEP, WIFICIPHER_WPA, WIFICIPHER_NOPASS, WIFICIPHER_INVALID
	}
	
	private View view;
	
	private TextView textView_title;
	private ListView listView;
	private TextView textView_open_wifi;
	private Button button_cancel;
	private Button button_jump;
	
	private TextView textView_ssid;
	private EditText editText_passwd;
	private Button button_connect;
	
	private TextView textView_a;
	private TextView textView_b;
	private TextView textView_x;
	
	private WiFiDevicesAdapter adapter;
		
	private int[] drawable_lock = {R.drawable.ic_wifi_lock_signal_1_light,R.drawable.ic_wifi_lock_signal_2_light,R.drawable.ic_wifi_lock_signal_3_light,R.drawable.ic_wifi_lock_signal_4_light};
	private int[] drawable_nolock = {R.drawable.ic_wifi_signal_1_light,R.drawable.ic_wifi_signal_2_light,R.drawable.ic_wifi_signal_3_light,R.drawable.ic_wifi_signal_4_light};
	
	private MySharedPreferences loginSharedPreferences; 
	private static List<ScanResult> lists = new ArrayList<ScanResult>();
	private String str_current_ssid;
	private String str_current_pass;
	
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		view = inflater.inflate(R.layout.wireless_network, container, false);
		
		textView_title = (TextView)view.findViewById(R.id.textview_wireless_function_name);
		listView = (ListView)view.findViewById(R.id.listview_wireless);
		textView_open_wifi = (TextView)view.findViewById(R.id.textview_open_wifi);
		button_cancel = (Button)view.findViewById(R.id.button_wireless_cancel);
		button_jump = (Button)view.findViewById(R.id.button_wireless_jump);
		
		textView_ssid = (TextView)view.findViewById(R.id.textview_wirless_ssid);
		editText_passwd = (EditText)view.findViewById(R.id.edittext_password);
		button_connect = (Button)view.findViewById(R.id.button_connect);
		
		textView_a = (TextView)view.findViewById(R.id.textview_a);
		textView_b = (TextView)view.findViewById(R.id.textview_b);
		textView_x = (TextView)view.findViewById(R.id.textview_x);
		
		adapter = new WiFiDevicesAdapter(this.getActivity(),
                R.layout.scanresult_wifi_item, R.id.textview_wifi_name,
                new ArrayList<ScanResult>());
		
		str_current_pass = "NO";
		str_current_ssid = "NO";
		
		loginSharedPreferences = new MySharedPreferences((MainActivity)getActivity());
		
		return view;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		listView.setAdapter(adapter);
		refreshWifiList();
				
		listView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				// TODO Auto-generated method stub

				//Toast.makeText(getActivity(), adapter.getItem(arg2).SSID, Toast.LENGTH_SHORT).show();
				
				loginSharedPreferences.putConnectingDevice(adapter.getItem(arg2).SSID);
				
				executeInputPassword(adapter.getItem(arg2).SSID);
				//refreshWifiList();
				newRefreshWifiList();
			}
		});
		
	
		button_jump.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				((MainActivity)getActivity()).getHandler().obtainMessage(MainActivity.HANDLE_NETWORK_WIRELESS_DISMISS).sendToTarget();
				loginSharedPreferences.putConnectingDevice("");
			}
		});
		
		button_cancel.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				loginSharedPreferences.putConnectingDevice("");
				refreshWifiList();
				
				((MainActivity)getActivity()).getHandler().obtainMessage(MainActivity.HANDLE_NETWORK_WIRELESS_BACK).sendToTarget();
			}
		});
		
		
		editText_passwd.setOnEditorActionListener(new OnEditorActionListener() {
			
			@Override
			public boolean onEditorAction(TextView arg0, int arg1, KeyEvent arg2) {
				// TODO Auto-generated method stub
				
				if (arg1 == EditorInfo.IME_ACTION_DONE) {
					setConnectWifi();
					InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);  
					imm.hideSoftInputFromWindow(editText_passwd.getWindowToken(), 0);  
					
					}
				return false;
			}
		});
	/*	
		editText_passwd.setOnKeyListener(new OnKeyListener() {
			
			@Override
			public boolean onKey(View arg0, int arg1, KeyEvent arg2) {
				// TODO Auto-generated method stub
				
				if(arg2.getKeyCode() == KeyEvent.KEYCODE_ENTER){
					InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);  
					imm.hideSoftInputFromWindow(editText_passwd.getWindowToken(), 0);  
				}
				return false;
			}
		});
		*/
		
		super.onActivityCreated(savedInstanceState);
	}
	

	public void setConnectWifi(){
				
		String ssid[] = textView_ssid.getText().toString().split(":");
		String pass = editText_passwd.getText().toString().trim();
		
		if(pass == null || pass.equals(""))
			pass = "NoSecret";
		
		if(ssid != null && pass != null && pass.length() >7){
			str_current_ssid = ssid[1].trim();
			str_current_pass = pass;
			if(loginSharedPreferences.getPassProblemDevice(str_current_ssid).equals(MySharedPreferences.CONNECT_PASSWORD_PROBLEM))
				loginSharedPreferences.putPassProblemDeviceNULL(str_current_ssid);
			
			new SettingWifiThread(SettingWifiThread.router,ssid[1].trim(), pass, getActivity()).start();
			
		}else{
			
			loginSharedPreferences.putConnectingDevice("");
			
			Toast.makeText(getActivity(), R.string.pass_lenth, Toast.LENGTH_LONG).show();
		}

		setPasswordViewVisible(false);
		setWirelessViewVisible(true);
		
		((MainActivity)getActivity()).getHandler().obtainMessage(MainActivity.SETCURRENT_NETWORKLESS_STATUE).sendToTarget();

	}
	
	public void inputSecretBack(){
		
		loginSharedPreferences.putConnectingDevice("");
		InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);  
		imm.hideSoftInputFromWindow(editText_passwd.getWindowToken(), 0);
		setPasswordViewVisible(false);
		setWirelessViewVisible(true);
		
		//refreshWifiList();
		newRefreshWifiList();
	}
	
	public boolean isInListview(){
		
		if(editText_passwd.getVisibility() == View.VISIBLE)
			return false;
		return true;
	}
	
	public boolean isTextView_open_wifiVisible(){

		if(textView_open_wifi.getVisibility() == View.VISIBLE)
			return true;
		return false;
	}
	
	public WiFiDevicesAdapter getadapter(){
		
		return adapter;
	}
	
	public void refreshWifiList(){
		
		WifiManager mWifiManager = (WifiManager) getActivity()
				.getSystemService(Context.WIFI_SERVICE);
		synchronized (mWifiManager) {
			if (mWifiManager != null){
				mWifiManager.startScan();
				loginSharedPreferences.putDisableScanResult(MySharedPreferences.DISABLE_SACN_RESULT);
			
			}	
		}
		
	}
	
	public void newRefreshWifiList(){
		synchronized (listView) {
			List<ScanResult> temp_list = new ArrayList<ScanResult>();
			for(int i=0;i<listView.getCount();i++){
				Object tmp = (ScanResult)(listView.getAdapter().getItem(i));
				temp_list.add((ScanResult)tmp);
			}
			if(temp_list != null){
				setButtonLoseFoucus();
				adapter.clear();
				adapter.addAll(sortWifiList(temp_list));
				synchronized (adapter) {
					listView.setSelection(0);
					adapter.notify();
					
				}
			}
		}
	}
	
	
	public void updateData(){
		setButtonLoseFoucus();
		adapter.clear();
		lists = getWifiList(lists);
		adapter.addAll(lists);
		synchronized (adapter) {
			listView.setSelection(0);
			adapter.notify();
		}
	}
	
	private void setButtonLoseFoucus(){
		button_cancel.setFocusable(false);
		button_jump.setFocusable(false);
		new Handler().postDelayed(new Runnable() {
			
			@Override
			public void run() {
				// TODO Auto-generated method stub
				button_cancel.setFocusable(true);
				button_jump.setFocusable(true);
			}
		}, 500);
	}
	
	public void openWifiStatue(){
		listView.setVisibility(View.GONE);
		textView_open_wifi.setVisibility(View.VISIBLE);
	}
	
	public void closeWifiStatue(){
		
		listView.setVisibility(View.VISIBLE);
		textView_open_wifi.setVisibility(View.GONE);
	}
	
	public void authHasProblem(){
		
		loginSharedPreferences.putPassProblemDevice(loginSharedPreferences.getConnectingDevice());
		//refreshWifiList();
		newRefreshWifiList();
	}
	
	
	
	private void executeInputPassword(String ssid){
		setWirelessViewVisible(false);
		setPasswordViewVisible(true);
		String str = getActivity().getResources().getString(R.string.network);
		textView_ssid.setText(str +" :  "+ssid);
		if(loginSharedPreferences.getSSidPassword(ssid).equals(MySharedPreferences.CONNECT_PASSWORD_PROBLEM))
			editText_passwd.setText("");
		else editText_passwd.setText(loginSharedPreferences.getSSidPassword(ssid));

			
	}
	
	/*
	private String getSsidPassword(){
		
		String path = "/data/misc/wifi/wpa_supplicant.conf";
		File file = new File(path);
		StringBuilder stringBuilder = new StringBuilder();
		Log.d("zzl:::","file is file_____________________"+file.isFile());
		if(file.isFile()){
			Log.d("zzl:::","@@@@@@@@@@@@@@@@@@@zzl");
			FileInputStream inputStream;
			try {
				inputStream = new FileInputStream(file);
				InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
				BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
				String line = null;
				try {
					while((line = bufferedReader.readLine())!= null){
						stringBuilder.append(line);
						Log.d("zzl:::","++++++++++++++++++++++"+line+"++++++++++++++");
					}
					
					bufferedReader.close();
					inputStreamReader.close();
					
				} catch (IOException e) {
					// TODO Auto-generated catch block
					if(bufferedReader != null)
						try {
							bufferedReader.close();
						} catch (IOException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						}
					if(inputStreamReader != null)
						try {
							inputStreamReader.close();
						} catch (IOException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						}
					e.printStackTrace();
				}
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				
				e.printStackTrace();
			}
			
			Pattern network = Pattern.compile("network=\\{([^\\}]+)\\}", Pattern.DOTALL);
			Matcher networkMatcher = network.matcher(stringBuilder.toString() );
			
			String SSID = "";
			String PASSWORD = "";
			
			 while (networkMatcher.find() ) { 
				 String networkBlock = networkMatcher.group(); 
				 Pattern ssid = Pattern.compile("ssid=\"([^\"]+)\""); 
				 Matcher ssidMatcher = ssid.matcher(networkBlock); 
				 if (ssidMatcher.find() ) { 
					  SSID = ssidMatcher.group(1);
					 
					 Pattern psk = Pattern.compile("psk=\"([^\"]+)\""); 
					 Matcher pskMatcher = psk.matcher(networkBlock); 
					 if (pskMatcher.find() ) { 
						PASSWORD =pskMatcher.group(1); 
					 } else {
					 		PASSWORD = "";
					 		}
				}
				 Log.d("zzl:::","^^^^^^^^^^^^^^ssid/pass"+SSID+"^^^^^^"+PASSWORD);
			 }
			 return PASSWORD;
		}
		Log.d("zzl:::","@@@@@@@@@@@@@@@@@@@zzl-end");

		return "";
	}
	*/
	
	
	private void setWirelessViewVisible(boolean flag){
		textView_title.setText(R.string.connect_wireless_name);
		if(flag){
			listView.setVisibility(View.VISIBLE);
			textView_x.setVisibility(View.VISIBLE);

			//button_cancel.setVisibility(View.VISIBLE);
			//button_jump.setVisibility(View.VISIBLE);
			button_cancel.setVisibility(View.GONE);
			button_jump.setVisibility(View.GONE);
		}else{
			listView.setVisibility(View.GONE);
			button_cancel.setVisibility(View.GONE);
			button_jump.setVisibility(View.GONE);
			textView_x.setVisibility(View.GONE);

		}
	}
	
	private void setPasswordViewVisible(boolean flag){
		textView_title.setText(R.string.input_wireless_secret);
		if(flag){
			textView_ssid.setVisibility(View.VISIBLE);
			editText_passwd.setVisibility(View.VISIBLE);
			textView_x.setVisibility(View.GONE);

			editText_passwd.setFocusable(true);
			editText_passwd.requestFocus();
			editText_passwd.setFocusableInTouchMode(true);
			
			InputMethodManager inputManager =

                    (InputMethodManager)getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);

			inputManager.showSoftInput(editText_passwd, 0);
			
			button_connect.setVisibility(View.GONE);
		}else{
			textView_ssid.setVisibility(View.GONE);
			editText_passwd.setVisibility(View.GONE);
			
			editText_passwd.setText("");
			button_connect.setVisibility(View.GONE);
		}
		
		((MainActivity)getActivity()).getHandler().obtainMessage(MainActivity.SETCURRENT_NETWORKLESS_STATUE).sendToTarget();

	}
	
	private List<ScanResult> getWifiList(List<ScanResult> lists) {

		List<ScanResult> list = new ArrayList<ScanResult>();

		WifiManager mWifiManager = (WifiManager) getActivity()
				.getSystemService(Context.WIFI_SERVICE);
		synchronized (mWifiManager) {
			if (mWifiManager != null){
				list = mWifiManager.getScanResults();				
			}	
		}
		
		boolean flag = false;
		for(ScanResult list2 :list){
			for( int i = 0;i< lists.size() ;i ++)
				if(lists.get(i).SSID.equals(list2.SSID)&&!flag)
					flag = true;
			if(!flag)
				lists.add(list2);
			flag = false;
		}
		return sortWifiList(lists);
	}
	
	public List<ScanResult> sortWifiList(List<ScanResult> list)
	{
		
		WifiManager mWifiManager = (WifiManager) getActivity()
				.getSystemService(Context.WIFI_SERVICE);
		Collections.sort(list,new ComparatorUser());
		Collections.reverse(list);
		
		WifiInfo currentInfo  = mWifiManager.getConnectionInfo();
			
		ConnectivityManager conMan = (ConnectivityManager) ((MainActivity) getActivity())
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo info_wifi = conMan
				.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
		
		int k = 0;
 		for(int i=0;i<list.size();i++){
 			if(IsExsits2(list.get(i).SSID)){
 				ScanResult tmp = list.get(i);
 				//list.remove(i);
 				ScanResult K_tmp = list.get(k);
 				list.set(i, K_tmp);
 				list.set(k, tmp);
 				k = k+ 1;
 				if(k>=list.size())
 					k = list.size()-1;
 			}
 		}
		
		if (currentInfo != null && info_wifi.isConnected()) 
	 		for(int i=0;i<list.size();i++){
				if(currentInfo.getSSID().equals("\"" + list.get(i).SSID + "\""))
					
				{
					ScanResult tmp = list.get(i);
					//list.remove(i);
					ScanResult first = list.get(0);
					list.set(i, first);
					list.set(0, tmp);
					break;
				}
			}
		
 		if(!loginSharedPreferences.getConnectingDevice().equals(""))
			for(int i=0;i<list.size();i++){
				if(list.get(i).SSID.equals(loginSharedPreferences.getConnectingDevice()))
				{
					ScanResult tmp = list.get(i);
					//list.remove(i);
					ScanResult first = list.get(0);
					list.set(i, first);
					list.set(0, tmp);
					break;
				}
			}
		
 		
		
 		return list;
	}
	
	public class ComparatorUser implements Comparator<ScanResult>{

		@Override
		public int compare(ScanResult lhs, ScanResult rhs) {
			// TODO Auto-generated method stub
		
			return ((Integer)lhs.level).compareTo(rhs.level);
		}
		
	}
	

	public class WiFiDevicesAdapter extends ArrayAdapter<ScanResult> {

        private List<ScanResult> items;

        public WiFiDevicesAdapter(Context context, int resource,
                int textViewResourceId, List<ScanResult> items) {
            super(context, resource, textViewResourceId, items);
            this.items = items;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View v = convertView;
            if (v == null) {
                LayoutInflater vi = (LayoutInflater) getActivity()
                        .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                v = vi.inflate(R.layout.scanresult_wifi_item, null);
            }

            ScanResult item = items.get(position);
            if (item != null) {
	            TextView name = (TextView)v.findViewById(R.id.textview_wifi_name);
	            name.setText(item.SSID);
	     	               
	     //////////////       
	            TextView statue = (TextView)v.findViewById(R.id.textview_wifi_statue);
	            if(IsExsits2(item.SSID)){
	            	statue.setVisibility(View.VISIBLE);
	            	statue.setText(R.string.wifi_saved);
	            }
	            else statue.setVisibility(View.GONE);
	            
	            Log.d("zzl:::","#################"+IsExsits2(item.SSID)+ item.SSID);
	            
	            if(!loginSharedPreferences.getConnectingDevice().equals("")&&item.SSID.equals(loginSharedPreferences.getConnectingDevice())){
	    			statue.setVisibility(View.VISIBLE);
					statue.setText(R.string.connecting);
					//currentSelectedSSID = "NO";
	    		}
	            
	            WifiInfo currentInfo = null;    
	            WifiManager mWifiManager = (WifiManager) getActivity()
	    				.getSystemService(Context.WIFI_SERVICE);
	    		currentInfo = mWifiManager.getConnectionInfo();
	    		ConnectivityManager conMan = (ConnectivityManager) ((MainActivity) getActivity())
						.getSystemService(Context.CONNECTIVITY_SERVICE);
	    		NetworkInfo info_wifi = conMan
						.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
	    				if (currentInfo != null && info_wifi.isConnected()) {
	    					if (currentInfo.getSSID().equals("\"" + item.SSID + "\"")){
	    		            	statue.setVisibility(View.VISIBLE);
	    						statue.setText(R.string.connect);
	    						
	    						if(!str_current_ssid.equals("NO")&&str_current_pass != null && str_current_pass !=null && currentInfo.getSSID().equals("\"" + loginSharedPreferences.getConnectingDevice()+ "\"")){
	    							loginSharedPreferences.putSSidPassword(str_current_ssid,str_current_pass);
	    							
	    							if(str_current_pass.equals("NoSecret"))
	    								Settings.Global.putString(getActivity().getContentResolver(),str_current_ssid,"null");
	    							else Settings.Global.putString(getActivity().getContentResolver(),str_current_ssid,str_current_pass);

	    						    Settings.Global.putString(getActivity().getContentResolver(),"last_connect_ssid",str_current_ssid);

	    							str_current_pass = "NO";
	    							str_current_ssid = "NO";
	    						}

	    					}
	    				}
	    		
	    		if( loginSharedPreferences.getPassProblemDevice(item.SSID).equals(MySharedPreferences.CONNECT_PASSWORD_PROBLEM)){
	    	    	statue.setVisibility(View.VISIBLE);
	    			statue.setText(R.string.auth_problem);
	    			
	    	    	}	
	    	/////////////
	            ImageView imageView = (ImageView)v.findViewById(R.id.imageview_wireless_signal);
	            if(getSecurityType(item.SSID) == WifiCipherType.WIFICIPHER_NOPASS){
	            	if(getlevel(item.level)!=4){
	            		imageView.setVisibility(View.VISIBLE);
	            		imageView.setBackgroundResource(drawable_nolock[getlevel(item.level)]);
	            	}else{
	            		imageView.setVisibility(View.GONE);
	            		statue.setVisibility(View.VISIBLE);
	            		statue.setText(R.string.no_range);
	            	}
	            }else{
	            	if(getlevel(item.level)!=4){
	            		imageView.setVisibility(View.VISIBLE);
	            		imageView.setBackgroundResource(drawable_lock[getlevel(item.level)]);
	            	}else{
	            		imageView.setVisibility(View.GONE);
	            		statue.setVisibility(View.VISIBLE);
	            		statue.setText(R.string.no_range);
	            	}
	            }
            }
            return v;
        }

    }
	
	private int getlevel(int rssi){
		
		int level = 4;
		
		if (rssi <= 0 && rssi >= -50) {  
            level = 3;  
        } else if (rssi < -50 && rssi >= -70) {  
            level = 2;  
        } else if (rssi < -70 && rssi >= -80) {  
            level = 1;  
        } else if (rssi < -80 && rssi >= -100) {  
            level = 0;  
        } else {  
            level = 4;  
        }  
		
		return level;
	}

	private WifiCipherType getSecurityType(String ssid) {
		WifiCipherType security_type = WifiCipherType.WIFICIPHER_INVALID;
		WifiManager mWifiManager = (WifiManager) getActivity()
				.getSystemService(Context.WIFI_SERVICE);
		List<ScanResult> results = mWifiManager.getScanResults();
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
	
	
	private WifiConfiguration IsExsits(String SSID) {
		List<WifiConfiguration> existingConfigs = null;
		WifiManager mWifiManager = (WifiManager) getActivity()
				.getSystemService(Context.WIFI_SERVICE);
		synchronized (mWifiManager) {
			existingConfigs = mWifiManager
					.getConfiguredNetworks();
			
			if (existingConfigs == null)
				existingConfigs = mWifiManager.getConfiguredNetworks();
			if (existingConfigs == null)
				return null;
			
			for (WifiConfiguration existingConfig : existingConfigs) {
				if (existingConfig.SSID.equals("\"" + SSID + "\"")) {
					return existingConfig;
				}
			}
		}
			
		return null;
	}
	
	private boolean IsExsits2(String SSID) {
		if(loginSharedPreferences.getSSidPassword(SSID).length() > 7)
			return true;
		else	
			return false;
	}
	
	public void setConnectingDeviceNULL(){
		loginSharedPreferences.putConnectingDevice("");
	}
}
