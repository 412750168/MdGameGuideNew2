package zzl.bestidear.mdgameguide;

import java.util.Timer;
import java.util.TimerTask;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class EndFragment extends Fragment {

	private View view;
	private TextView textView_end;

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		
		super.onActivityCreated(savedInstanceState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		view = inflater.inflate(R.layout.end, container, false);
		textView_end = (TextView)view.findViewById(R.id.TextView_end);
		
		return view;
	}
	
	public void setFinishActivity(){
		TimerTask timerTask = new TimerTask() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				((MainActivity) getActivity())
						.getHandler()
						.obtainMessage(
								MainActivity.HANDLE_END_FRAGMENT_FINISH)
						.sendToTarget();

			}
		};
		Timer timer = new Timer();
		timer.schedule(timerTask, 3000);
	}

}
