package zzl.bestidear.mdgameguide;

import android.app.Activity;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.util.Log;

public class MySharedPreferences {

	private static final String NAME = "Mdgame";
	private MainActivity activity;
	private final static String CONNING_DEVICE = "connecting";
	public final static String CONNECT_PASSWORD_PROBLEM = "problem";
	public final static String DISABLE_SACN_RESULT = "DISABLE";

	SharedPreferences mySharedPreferences;
	SharedPreferences.Editor editor;

	public MySharedPreferences(MainActivity activity) {
		super();
		this.activity = activity;
		
	}
	
	public void putSSidPassword(String ssid,String password){
		
		mySharedPreferences = this.activity.getSharedPreferences(NAME,
				Activity.MODE_PRIVATE);
		editor = mySharedPreferences.edit();
		editor.putString(ssid,password);
		editor.commit();
	}
	
	public String getSSidPassword(String ssid){
		mySharedPreferences = this.activity.getSharedPreferences(NAME,
				Activity.MODE_PRIVATE);
		String pass = mySharedPreferences.getString(ssid, "");
		return pass;
	}
	
	public void putConnectingDevice(String device){
		
		mySharedPreferences = this.activity.getSharedPreferences(NAME,
				Activity.MODE_PRIVATE);
		editor = mySharedPreferences.edit();
		editor.putString(CONNING_DEVICE,device);
		editor.commit();
	}
	
	public String getConnectingDevice(){
		mySharedPreferences = this.activity.getSharedPreferences(NAME,
				Activity.MODE_PRIVATE);
		String device = mySharedPreferences.getString(CONNING_DEVICE, "");
		return device;
	}
	
	public void putPassProblemDevice(String device){
		
		mySharedPreferences = this.activity.getSharedPreferences(NAME,
				Activity.MODE_PRIVATE);
		editor = mySharedPreferences.edit();
		editor.putString(device,CONNECT_PASSWORD_PROBLEM);
		editor.commit();
	}
	
	public void putPassProblemDeviceNULL(String device){
		
		mySharedPreferences = this.activity.getSharedPreferences(NAME,
				Activity.MODE_PRIVATE);
		editor = mySharedPreferences.edit();
		editor.putString(device,"");
		editor.commit();
	}
	
	public String getPassProblemDevice(String device){
		mySharedPreferences = this.activity.getSharedPreferences(NAME,
				Activity.MODE_PRIVATE);
		String problem = mySharedPreferences.getString(device, "");
		return problem;
	}
	
	public void putDisableScanResult(String str){
		
		mySharedPreferences = this.activity.getSharedPreferences(NAME,
				Activity.MODE_PRIVATE);
		editor = mySharedPreferences.edit();
		editor.putString(DISABLE_SACN_RESULT,str);
		editor.commit();
	}
	
	public String getDisableScanResult(){
		mySharedPreferences = this.activity.getSharedPreferences(NAME,
				Activity.MODE_PRIVATE);
		String problem = mySharedPreferences.getString(DISABLE_SACN_RESULT, "");
		return problem;
	}
}
