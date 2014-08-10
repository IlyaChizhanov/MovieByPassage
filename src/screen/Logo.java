package screen;

import com.moviebypassage.R;

import download.Downloader;
import download.XAPKFile;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

public class Logo extends Activity {
	
	Downloader downloader;
	private static final XAPKFile[] xAPKS = {
    	new XAPKFile(5,93553179L,true),         
    };
	
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.logo);
		
		downloader = new Downloader(this, xAPKS, this.getClass(), this.getIntent());
        if(!downloader.expansionFilesDelivered()){
        	downloader.DownloaderSrart();
        }
		
		
		Thread logoTimer = new Thread()
        {
            @Override
			public void run()
            {
                try
                {
                	//ждём 5 секунд или пока не загрузяться файлы расширения
                    int logoTimer = 0;
                    while(logoTimer < 5000 || (downloader.inDowloader && downloader.status != 2))
                    {
                        sleep(100);
                        logoTimer = logoTimer +100;
                    };
                    
                    startActivity(new Intent("MoveByPassage.main"));
                } 
                catch (InterruptedException e)
                {
                    e.printStackTrace();
                }
                finally
                {
                    finish();
                }
            }
        };
        logoTimer.start();
	}
	
	@Override
    protected void onStart() {
    	if(downloader.inDowloader){
    		downloader.Connect();
    	}
        super.onStart();
    }
    @Override
    protected void onStop() {
    	if(downloader.inDowloader){
    		downloader.Disconnect();
    	}
        super.onStop();
    }
}
