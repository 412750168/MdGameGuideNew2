package zzl.bestidear.mdgameguide;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

public class NetWorkSettingFragment extends Fragment{
	
	private View view;
	private Button button_wired;
	private Button button_wireless;
	private TextView textView_networksettingtitle;
	private boolean wired_flag = false;
	private boolean wireless_flag = false;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		view = inflater.inflate(R.layout.networksetting, container, false);	
		button_wired = (Button)view.findViewById(R.id.button_wired_network);
		button_wireless = (Button)view.findViewById(R.id.button_wireless_network);
		textView_networksettingtitle = (TextView)view.findViewById(R.id.textview_networktitle);
		
		return view;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		
		button_wired.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				((MainActivity)getActivity()).getHandler().obtainMessage(MainActivity.HANDLE_NETWORK_WIRED).sendToTarget();
				wired_flag = true;
				wireless_flag = false;
				setwiredFocus();
			}
		});
		
		button_wireless.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				((MainActivity)getActivity()).getHandler().obtainMessage(MainActivity.HANDLE_NETWORK_WIRELESS).sendToTarget();
				wired_flag = false;
				wireless_flag = true;
				setwirelessFocus();
			}
		});
			
		super.onActivityCreated(savedInstanceState);
	}

	public void setwirelessFocus(){
		button_wireless.setFocusable(true);
		button_wireless.requestFocus();
		button_wireless.setFocusableInTouchMode(true);
	}
	
	public void setwiredFocus(){
		button_wired.setFocusable(true);
		button_wired.requestFocus();
		button_wired.setFocusableInTouchMode(true);
	}
	
}
