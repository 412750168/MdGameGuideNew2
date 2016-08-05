package zzl.bestidear.mdgameguide;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Timer;
import java.util.TimerTask;

import android.app.Fragment;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.hardware.usb.UsbConfiguration;
import android.hardware.usb.UsbConstants;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbManager;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.ScaleAnimation;
import android.widget.AbsoluteLayout;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class HandFragment extends Fragment {

	private static final String DEVICE_NAME = "Bluetooth Gamepad";
	private static final String DEVICE_NAME_NEW = "MGA_XY13264";
	private static final String DEVICE_NAME_NEW_2 = "Ä§µ°ÊÖ±ú";
	private static final String DEVICE_NAME_NEW_3 = "Modan_gamepad";
	// private static final String DEVICE_NAME = "YK BT RCU";

	private View view;
	private BluetoothAdapter bluetoothAdapter;
	private IntentFilter intentFilter;

	private ImageView imageView_home;
	private TextView textView_noti;
	private TextView textView_noti_connecting;
	private TextView textView_hand;
	private ImageView imageView_arrow_up;
	private ImageView imageView_arrow_down;
	
	private ImageView imageView_box;
	private ImageView imageView_handler;
	private ImageView imageView_handler_connecting;
	private ImageView imageView_line;

	private boolean NODEVICE_FLAG = false;
	private boolean USB_FLAG = false;

	private TimerTask timerTask_g;
	private Timer timer_g;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		view = inflater.inflate(R.layout.hand, container, false);

		imageView_home = (ImageView) view.findViewById(R.id.image_home);
		// imageView_home.startAnimation(getAlphaAnimatin(300));

		textView_noti = (TextView) view.findViewById(R.id.textview_noti);
		textView_noti_connecting = (TextView) view
				.findViewById(R.id.textview_noti_connecting);

		textView_hand = (TextView) view.findViewById(R.id.textview_hand_title);
		imageView_arrow_up = (ImageView) view
				.findViewById(R.id.imageView_arrow_up);
		imageView_arrow_down = (ImageView) view
				.findViewById(R.id.imageView_arrow_down);
		
		imageView_box = (ImageView)view.findViewById(R.id.box);
		imageView_line = (ImageView)view.findViewById(R.id.imageview_line);
		imageView_handler = (ImageView)view.findViewById(R.id.hander);
		imageView_handler_connecting = (ImageView)view.findViewById(R.id.hander_connecting);

		return view;
	}

	public void setHandJumpButtonVisible(boolean flag) {

		if (flag) {
			if (isHaveUsbDevice()) {

				startBluetoothDiscovery();
				USB_FLAG = true;
				imageView_handler_connecting.setVisibility(View.VISIBLE);
				imageView_box.setVisibility(View.GONE);
				imageView_line.setVisibility(View.GONE);
				imageView_handler.setVisibility(View.GONE);
				imageView_arrow_up.setVisibility(View.GONE);
				imageView_arrow_down.setVisibility(View.GONE);
				textView_noti.setVisibility(View.GONE);
				textView_noti_connecting.setVisibility(View.VISIBLE);
				imageView_home.setVisibility(View.VISIBLE);
				imageView_home.startAnimation(getAlphaAnimatin(300));

			}
		} else {
			if (USB_FLAG) {
				stopBluetoothDiscovery();
				stopAnimation();
				startProblemAnimation();
				USB_FLAG = false;
			}
		}

	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onActivityCreated(savedInstanceState);

		intentFilter = new IntentFilter();
		intentFilter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
		intentFilter.addAction(BluetoothDevice.ACTION_FOUND);

		getActivity().registerReceiver(mReceiver, intentFilter);

	}

	@Override
	public void onResume() {
		// TODO Auto-generated method stub
		new Handler().postDelayed(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

			}
		}, 5000);
		
		
		timerTask_g = new TimerTask() {
			
			@Override
			public void run() {
				// TODO Auto-generated method stub
				
				if(bluetoothAdapter != null)
					if(bluetoothAdapter.getBondedDevices().size() > 0){
						((MainActivity) getActivity())
						.getHandler().obtainMessage(
								MainActivity.HANDLE_HAND_BLUETOOTH_CONNECT_STATE)
								.sendToTarget();
						
						timerTask_g.cancel();
						timer_g.cancel();
						timerTask_g = null;
						timer_g = null;
					}
			}
		};
		
		timer_g = new Timer();
		timer_g.scheduleAtFixedRate(timerTask_g, 10000, 3000);
		
		super.onResume();

	}

	private Animation getAlphaAnimatin(int time) {

		Animation alphaAnimation = new AlphaAnimation(1, 0);
		alphaAnimation.setDuration(time);
		alphaAnimation.setInterpolator(new LinearInterpolator());
		alphaAnimation.setRepeatCount(Animation.INFINITE);
		alphaAnimation.setRepeatMode(Animation.REVERSE);
		return alphaAnimation;
	}

	private Animation getScaleAnimation(int time) {

		Animation scaleAnimation = new ScaleAnimation(1.0f, 1.1f, 1.0f, 1.1f);
		scaleAnimation.setDuration(time);
		scaleAnimation.setInterpolator(new AccelerateDecelerateInterpolator());
		scaleAnimation.setRepeatCount(Animation.INFINITE);
		scaleAnimation.setRepeatMode(Animation.REVERSE);
		return scaleAnimation;
	}

	public void startProblemAnimation() {

		imageView_home.clearAnimation();
		imageView_handler_connecting.setVisibility(View.GONE);
		imageView_home.setVisibility(View.GONE);
		textView_noti_connecting.setVisibility(View.GONE);

		imageView_box.setVisibility(View.VISIBLE);
		imageView_line.setVisibility(View.VISIBLE);
		imageView_handler.setVisibility(View.VISIBLE);
		imageView_arrow_up.setVisibility(View.VISIBLE);
		imageView_arrow_down.setVisibility(View.VISIBLE);
		textView_noti.setVisibility(View.VISIBLE);
		textView_noti.setTextColor(Color.RED);
		textView_noti.setText(R.string.noti_fail);
	}

	public void stopAnimation() {
		imageView_home.clearAnimation();
	}

	public void setConnectSuccess() {

		// textView_hand.setText(R.string.hand_success);
		
		imageView_box.setVisibility(View.GONE);
		imageView_handler.setVisibility(View.GONE);
		imageView_line.setVisibility(View.GONE);
		imageView_arrow_down.setVisibility(View.GONE);
		imageView_arrow_up.setVisibility(View.GONE);
		textView_noti.setVisibility(View.GONE);
		
		imageView_handler_connecting.setVisibility(View.VISIBLE);
		textView_noti_connecting.setVisibility(View.VISIBLE);
		imageView_home.setVisibility(View.VISIBLE);
		textView_noti_connecting.setTextColor(Color.WHITE);
		textView_noti_connecting.setText(R.string.hand_success_2);

	}

	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		if (bluetoothAdapter.isDiscovering()) {
			bluetoothAdapter.cancelDiscovery();
		}
		getActivity().unregisterReceiver(mReceiver);
		super.onDestroy();
	}

	public void startBluetoothDiscovery() {

		NODEVICE_FLAG = false;
		if (bluetoothAdapter != null)
			bluetoothAdapter.startDiscovery();
		else {
			bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
			bluetoothAdapter.startDiscovery();
		}
	}

	public void stopBluetoothDiscovery() {

		NODEVICE_FLAG = true;

		if (bluetoothAdapter != null && bluetoothAdapter.isDiscovering()) {
			bluetoothAdapter.cancelDiscovery();
		}
	}

	private boolean isHaveUsbDevice() {

		boolean flag = false;

		UsbManager manager = (UsbManager) getActivity().getSystemService(
				Context.USB_SERVICE);
		if (manager != null) {
			HashMap<String, UsbDevice> deviceList = manager.getDeviceList();
			if (deviceList != null) {
				Iterator<UsbDevice> deviceIterator = deviceList.values()
						.iterator();
				if (deviceIterator != null) {
					while (deviceIterator.hasNext()) {
						UsbDevice device = deviceIterator.next();
						int vendorId = device.getVendorId();
						int productIt = device.getProductId();
						// if(vendorId ==1035 && productIt != -1)
						// if(vendorId !=7531 && productIt != -1)
						// if(device.getDeviceClass() ==
						// UsbConstants.USB_CLASS_HID)
						for (int i = 0; i < device.getInterfaceCount(); i++) {
							Log.d("zzl:::",
									"+++++++++++++++++%%%%%%%%%%%%%%%::::"
											+ "vendor::"
											+ vendorId
											+ " "
											+ "product::"
											+ productIt
											+ " "
											+ "class::"
											+ device.getInterface(i)
													.getInterfaceClass());
							if (device.getInterface(i).getInterfaceClass() == UsbConstants.USB_CLASS_HID
									|| device.getInterface(i)
											.getInterfaceClass() == UsbConstants.USB_CLASS_VENDOR_SPEC)
								flag = true;

						}
					}
				}
			}
		}

		return flag;
	}

	// this is new add function
	public void setEnterIsHaveUsb() {

		setHandJumpButtonVisible(isHaveUsbDevice());
	}

	public void setHandFragmentHide() {

		if (bluetoothAdapter.isDiscovering()) {
			bluetoothAdapter.cancelDiscovery();
		}
		TimerTask timerTask = new TimerTask() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				((MainActivity) getActivity())
						.getHandler()
						.obtainMessage(
								MainActivity.HANDLE_HAND_FRAGMENT_DISMISS)
						.sendToTarget();

			}
		};
		Timer timer = new Timer();
		timer.schedule(timerTask, 4000);
	}

	public void setHandFragmentHideNotDelay() {

		if (bluetoothAdapter.isDiscovering()) {
			bluetoothAdapter.cancelDiscovery();
		}

		((MainActivity) getActivity()).getHandler()
				.obtainMessage(MainActivity.HANDLE_HAND_FRAGMENT_DISMISS)
				.sendToTarget();

	}

	BroadcastReceiver mReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			// final BluetoothDevice device ;
			// When discovery finds a device
			if (BluetoothDevice.ACTION_FOUND.equals(action)) {
				// Get the BluetoothDevice object from the Intent
				final BluetoothDevice device = intent
						.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
				// If it's already paired, skip it, because it's been listed
				// already
				if (device != null) {
					String name = device.getName();
					if (name != null
							&& device.getBondState() != BluetoothDevice.BOND_BONDED
							&& (name.equals(DEVICE_NAME)
									|| name.equals(DEVICE_NAME_NEW)
									|| name.equals(DEVICE_NAME_NEW_2) || name
										.equals(DEVICE_NAME_NEW_3))) {
						Toast.makeText(getActivity(), name, Toast.LENGTH_SHORT)
								.show();

						NODEVICE_FLAG = true;

						if (bluetoothAdapter.isDiscovering()) {
							bluetoothAdapter.cancelDiscovery();
						}
						TimerTask timerTask = new TimerTask() {

							@Override
							public void run() {
								// TODO Auto-generated method stub
								device.createBond();
							}
						};
						Timer timer = new Timer();
						timer.schedule(timerTask, 5000);
					}
				}
				// When discovery is finished, change the Activity title
			} else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED
					.equals(action)) {
				if (!NODEVICE_FLAG) {
					startBluetoothDiscovery();
					NODEVICE_FLAG = false;
				}

			}
		}
	};

}
