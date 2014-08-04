package download;

import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Messenger;

import com.google.android.vending.expansion.downloader.DownloadProgressInfo;
import com.google.android.vending.expansion.downloader.DownloaderClientMarshaller;
import com.google.android.vending.expansion.downloader.DownloaderServiceMarshaller;
import com.google.android.vending.expansion.downloader.Helpers;
import com.google.android.vending.expansion.downloader.IDownloaderClient;
import com.google.android.vending.expansion.downloader.IDownloaderService;
import com.google.android.vending.expansion.downloader.IStub;
import com.moviebypassage.R;


public class Downloader implements IDownloaderClient{
	
	private Context context;
	private XAPKFile[] xAPKS;
	private IDownloaderService RemoteService;
	private IStub DownloaderClient;
	private Class<?> cls;
	private Intent intent;
	private ProgressDialog progressDialog;
	
	public int status = 0;
	public boolean inDowloader = false;
	
	//конструктор: констекст, массив файлов расширения, класс главное окна, интент главного окна
	public Downloader(Context context, XAPKFile[] xAPKS, Class<?> cls, Intent intent){
		this.context = context;
		this.xAPKS = xAPKS;
		this.cls = cls;
		this.intent = intent;
	}
	
	public void DownloaderSrart(){
		Intent LaunchIntent = new Intent(context, cls);
		LaunchIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TOP);
		LaunchIntent.setAction(intent.getAction());
		if (intent.getCategories() != null) {
            for (String category : intent.getCategories()) {
            	LaunchIntent.addCategory(category);
            }
        }
		PendingIntent pendingIntent = PendingIntent.getActivity(
                context,0, LaunchIntent,PendingIntent.FLAG_UPDATE_CURRENT);
		int startResult = 0;
		try {
			startResult = DownloaderClientMarshaller.startDownloadServiceIfRequired(context,
			        pendingIntent, DownloaderMainService.class);
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}
		if (startResult != DownloaderClientMarshaller.NO_DOWNLOAD_REQUIRED) {
			DownloaderClient = DownloaderClientMarshaller.CreateStub(this, DownloaderMainService.class);
			progressDialog = new ProgressDialog(context);
            progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
            progressDialog.setMessage(context.getResources().getString(R.string.app_name));
            progressDialog.setCancelable(false);
            progressDialog.show();
            inDowloader = true;
		}
	}
	
	public void Connect(){
		if (null != DownloaderClient) {
            DownloaderClient.connect(context);
        }
	}
	
	public void Disconnect(){
		if (null != DownloaderClient) {
			DownloaderClient.disconnect(context);
        }
	}
	
	@Override
	public void onServiceConnected(Messenger m) {
		RemoteService = DownloaderServiceMarshaller.CreateProxy(m);
        RemoteService.onClientUpdated(DownloaderClient.getMessenger());
	}

	@Override
	public void onDownloadStateChanged(int newState) {
		switch (newState) {
		case STATE_DOWNLOADING:
			status = 1;
		break;
		case STATE_COMPLETED:
			status = 2;
			progressDialog.setProgress(100);
			progressDialog.cancel();
		break;
		case STATE_FAILED_UNLICENSED:
		case STATE_FAILED_FETCHING_URL:
		case STATE_FAILED_SDCARD_FULL:
		case STATE_FAILED_CANCELED:
		case STATE_FAILED:
			status = -1;
		break;
		}
	}
	
	//прогрусс загрузки
	@Override
	public void onDownloadProgress(DownloadProgressInfo progress) {
		long percents = progress.mOverallProgress * 100 / progress.mOverallTotal;
		progressDialog.setProgress((int) percents);		
	}
	
	//проверяем наличие файлов. если false то нужна закачка
	public boolean expansionFilesDelivered() {
        for (XAPKFile xf : xAPKS) {
            String fileName = Helpers.getExpansionAPKFileName(context, xf.mainFile, xf.FileVersion);
            if (!Helpers.doesFileExist(context, fileName, xf.size, false))
                return false;
        }
        return true;
    }

}
