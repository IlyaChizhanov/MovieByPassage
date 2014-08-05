package tools;

import java.io.IOException;

import com.android.vending.expansion.zipfile.APKExpansionSupport;
import com.android.vending.expansion.zipfile.ZipResourceFile;

import android.content.Context;
import android.content.res.AssetFileDescriptor;

public class ObbTools {
	Context context;
	ZipResourceFile expansionFile;
	public ObbTools(Context context){
		this.context = context;
		try {
			expansionFile = APKExpansionSupport.getAPKExpansionZipFile(context,
				        3, 0);
		} catch (IOException e) {
			
		}

	}
	
	public AssetFileDescriptor getAssetFileDescriptor(String path){
		return expansionFile.getAssetFileDescriptor(path);
	}

	public void onDestroy() {
	}
	
	
}
