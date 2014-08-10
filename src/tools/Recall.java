package tools;


import com.moviebypassage.R;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.net.Uri;

public class Recall{
	private AlertDialog.Builder adb;
    private Context context;
    
    public Recall(Context context){
		this.context = context;
		
		adb = new AlertDialog.Builder(context)
		.setTitle(R.string.recall_titile).setPositiveButton(R.string.recall_yes,//оценю
				new DialogInterface.OnClickListener() {
			        @Override
					public void onClick(DialogInterface dialog,
			                int id) {
			        	OpenMarket();
			        }
			    })
		.setNegativeButton(R.string.recall_no, //не спрашивать
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog,
			                int id) {
						recall_no();
			            dialog.cancel();
			        }
			    })
        .setNeutralButton(R.string.recall_later, //позже
				new DialogInterface.OnClickListener() {
				    @Override
					public void onClick(DialogInterface dialog,
				            int id) {
				        dialog.cancel();
				    }
				})
        .setMessage(R.string.recall_message);
		
	}
    
    public void show(){
    	adb.show();
    }
    
    private void OpenMarket(){
    	SharedPreferences settings = context.getSharedPreferences("MovieByPassage", 0);
    	Editor editor = settings.edit();
		editor.putInt("recall", 1);
		editor.commit();//готово
    	Intent intent = new Intent(Intent.ACTION_VIEW); 
    	intent.setData(Uri.parse("market://details?id="+context.getPackageName()));  
    	context.startActivity(intent);
    }
    
    private void recall_no(){
    	SharedPreferences settings = context.getSharedPreferences("MovieByPassage", 0);
    	Editor editor = settings.edit();
		editor.putInt("recall", 2);
		editor.commit();//готово
    }
}
