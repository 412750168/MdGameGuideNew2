package zzl.bestidear.mdgameguide;

import java.util.Timer;
import java.util.TimerTask;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.view.animation.ScaleAnimation;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

public class NetworkWiredFragment extends Fragment {

	private View view;
	private Button button_jump;
	private Button button_cancel;
	private TextView textView_wired_statue;
	private TextView textView_wiredtitle;
	private TextView textview_lancab;
	private ImageView imageView_circle;
	private FrameLayout framelayout;
	
	private TextView textView_b;
	private TextView textView_x;
	
	Timer timer ;
	TimerTask timerTask;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		view = inflater.inflate(R.layout.wired_network, container, false);
		button_jump = (Button) view.findViewById(R.id.button_wired_jump);
		button_cancel = (Button) view.findViewById(R.id.button_wired_cancel);

		textView_wired_statue = (TextView)view.findViewById(R.id.textView_wired_statue);
		textView_wiredtitle = (TextView)view.findViewById(R.id.textView_wiredtitle);
		textview_lancab = (TextView)view.findViewById(R.id.textview_lancable);
		
		textView_b = (TextView)view.findViewById(R.id.textview_b2);
		textView_x = (TextView)view.findViewById(R.id.textview_x2);
		

		imageView_circle = (ImageView)view.findViewById(R.id.imageView_circle);
		
		imageView_circle.startAnimation(getRotateAnimation(5000));
		
		framelayout = (FrameLayout)view.findViewById(R.id.FrameLayout1);
				
		return view;
	}

	private Animation getRotateAnimation(int time) {

		Animation  rotateAnimation = AnimationUtils.loadAnimation(getActivity(), R.anim.circle);  
		rotateAnimation.setDuration(time);
		LinearInterpolator lin = new LinearInterpolator();  
		rotateAnimation.setInterpolator(lin);  
		return rotateAnimation;
	}
	
	public void setWiredConnectSuccess(){
		
		imageView_circle.clearAnimation();
		framelayout.setBackgroundResource(R.drawable.wired_connect);
		imageView_circle.setVisibility(View.GONE);
		textView_wired_statue.setText(R.string.wired_connect_success);
		
		new Handler().postDelayed(new Runnable() {
			
			@Override
			public void run() {
				// TODO Auto-generated method stub
				((MainActivity) getActivity())
				.getHandler()
				.obtainMessage(
						MainActivity.HANDLE_NETWORK_WIRED_DISMISS)
				.sendToTarget();
			}
		}, 2000);
		
		
	}

	public void setWiredDisConnect(){
		framelayout.setBackgroundResource(R.drawable.wired_network);
		imageView_circle.setVisibility(View.VISIBLE);
		textView_wired_statue.setText(R.string.wired_input_line);
		imageView_circle.startAnimation(getRotateAnimation(5000));
	}
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		button_jump.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				((MainActivity) getActivity())
						.getHandler()
						.obtainMessage(
								MainActivity.HANDLE_NETWORK_WIRED_DISMISS)
						.sendToTarget();
			}
		});

		button_cancel.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				((MainActivity) getActivity()).getHandler()
						.obtainMessage(MainActivity.HANDLE_NETWORK_WIRED_BACK)
						.sendToTarget();

			}
		});
		
		  timerTask = new TimerTask() {
			
			@Override
			public void run() {
				// TODO Auto-generated method stub
				Activity activity = getActivity();
				if(activity != null){
				ConnectivityManager conMan = (ConnectivityManager) ((MainActivity) activity)
						.getSystemService(Context.CONNECTIVITY_SERVICE);
				if(conMan != null){
					NetworkInfo info_eth = conMan
							.getNetworkInfo(ConnectivityManager.TYPE_ETHERNET);
					if (info_eth != null && info_eth.isAvailable()) {
						
						if (info_eth.getType() == ConnectivityManager.TYPE_ETHERNET) {
							((MainActivity) getActivity())
							.getHandler().obtainMessage(MainActivity.HANDLE_ETH_STATE,"true")
									.sendToTarget();
							
							if(((MainActivity)getActivity()).isNetworkedFragment()){
								timerTask.cancel();
								timer.cancel();
								timerTask  = null;
								timer = null;
							}
							
							
						}
					}
				}
			}
			}
		};
		timer = new Timer();
		timer.schedule(timerTask, 2000, 5000);
		super.onActivityCreated(savedInstanceState);
	}

}
