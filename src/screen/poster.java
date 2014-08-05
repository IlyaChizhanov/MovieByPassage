package screen;

import java.io.IOException;
import java.io.InputStream;

import com.moviebypassage.R;

import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.widget.ImageView;

public class poster extends Activity {
	private int lvl;
	
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.poster);
		
		Intent intent = getIntent();
		lvl = intent.getIntExtra("lvl", 1);
		
		InputStream ims;
		try {
			ims = getAssets().open("poster/"+lvl+".jpg");
		} catch (IOException e) {
			return;
		}
    	Drawable d = Drawable.createFromStream(ims, null);
		
    	ImageView iw = (ImageView)findViewById(R.id.poster_img);
    	
		iw.setImageDrawable(d);
	}
	
}
