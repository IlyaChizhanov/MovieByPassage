package screen;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import com.moviebypassage.R;

import android.app.Activity;
import android.content.Intent;
import android.content.res.AssetManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

public class Contents extends Activity {
	
	private int lvl;
	
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.contents);
		
		Intent intent = getIntent();
		lvl = intent.getIntExtra("lvl", 1);
		
		AssetManager am = getAssets();
		InputStream is;
		String text = "";
		try {
			is = am.open("contents/"+lvl+".txt");
			text = convertStreamToString(is);
			is.close();
		} catch (IOException e) {
			
		}
		
		Log.d("DEB","Text:"+text);
		
		TextView tw = (TextView)findViewById(R.id.ContentsBox);
		tw.setText(text);
	}
	
	private String convertStreamToString(InputStream is) {
	    ByteArrayOutputStream oas = new ByteArrayOutputStream();
	    copyStream(is, oas);
	    String t = oas.toString();
	    try {
	        oas.close();
	        oas = null;
	    } catch (IOException e) {
	        // TODO Auto-generated catch block
	        e.printStackTrace();
	    }
	    return t;
	}

	private void copyStream(InputStream is, OutputStream os)
	{
	    final int buffer_size = 1024;
	    try
	    {
	        byte[] bytes=new byte[buffer_size];
	        for(;;)
	        {
	          int count=is.read(bytes, 0, buffer_size);
	          if(count==-1)
	              break;
	          os.write(bytes, 0, count);
	        }
	    }
	    catch(Exception ex){}
	}
	
	
}
