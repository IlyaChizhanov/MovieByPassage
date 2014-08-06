package screen;

import com.moviebypassage.R;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.MotionEvent;

public class Bonus extends Activity {
	
	int Width;
	int Height;
	
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.bonus);
		
		// Узнаем размеры экрана из ресурсов
		DisplayMetrics displaymetrics = getResources().getDisplayMetrics();
		Width = displaymetrics.widthPixels;
		Height = displaymetrics.heightPixels;
	}
	
	@Override
	public boolean onTouchEvent(MotionEvent event){
		int Action=event.getAction();
		if(Action == 1){			
			float x_click = event.getX();
			float y_click = event.getY();
			float x1 = (float)(Width * 0.083);
			float x2 = (float)(Width * 0.935);
			float y1 = (float)(Height * 0.252);
			float y2 = (float)(Height * 0.802);
			
			float y_but1 = (float)(Height * 0.451);
			float y_but2 = (float)(Height * 0.555);
			float y_but3 = (float)(Height * 0.662);
			
			
			if((x_click > x1 && x_click < x2)&&
				y_click > y1 && y_click < y2){//рамка
				
				if(y_click < y_but1){
					//убрать букву
					Intent intent = new Intent();
				    intent.putExtra("activ", 1);
				    setResult(RESULT_OK, intent);
				    finish();
				} else 
				if(y_click < y_but2){
					//открыть букву
					Intent intent = new Intent();
				    intent.putExtra("activ", 2);
				    setResult(RESULT_OK, intent);
				    finish();
				}else 
				if(y_click < y_but3){
					Intent intent = new Intent();
				    intent.putExtra("activ", 3);
				    setResult(RESULT_OK, intent);
				    finish();
				} else {
					Intent intent = new Intent();
				    intent.putExtra("activ", 4);
				    setResult(RESULT_OK, intent);
				    finish();
				}
			}
		}
		return true;
	}
}
