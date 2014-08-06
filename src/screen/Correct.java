package screen;
import palette.NextBlock;

import com.moviebypassage.R;

import AdMob.AdMobFullscreen;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.RelativeLayout;


public class Correct extends Activity {
	int lvl = 1;
	int money = 0;
	NextBlock nb;
	AdMobFullscreen banner;
	
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.correct);
		

	    RelativeLayout rl = (RelativeLayout)findViewById(R.id.correct_layout);

		Intent intent = getIntent();
	    lvl = intent.getIntExtra("lvl", 1);
	    money = intent.getIntExtra("money", 0);
	    
	    
	    nb = new NextBlock(this);
	    nb.updateInfo(lvl, money);

	    rl.addView(nb);
	    
	    banner = new AdMobFullscreen(this, "ca-app-pub-0606408347038699/8807750516");
	    
	    
	    new Thread(new Runnable() {
			@Override
			public void run() {
				while(true){
					if(nb.getNext() && banner.close){
						Intent intent = new Intent();
						setResult(RESULT_OK, intent);
						finish();
						break;
					}
					try {
						Thread.sleep(100);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}
		}).start(); 
	}

}
