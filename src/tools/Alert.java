package tools;

import com.moviebypassage.R;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.MotionEvent;
import android.widget.TextView;

public class Alert extends Activity{
	String text;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.alert);
		
		Intent intent = getIntent();
		text = intent.getStringExtra("text");
		
		TextView tw = (TextView)findViewById(R.id.AlertBox);
		tw.setText(text);
	}
	
	@Override
	public boolean onTouchEvent(MotionEvent event){
		int Action=event.getAction();
		if(Action == 1){
			finish();
		}
		return true;
	}
}
