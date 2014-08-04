package AdMob;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;

import android.content.Context;

public class AdMobFullscreen {
	private InterstitialAd interstitial;
	public Boolean close = false;
	
	public AdMobFullscreen(Context context, String adUnitId){
		
		interstitial = new InterstitialAd(context);
	    interstitial.setAdUnitId(adUnitId);
	    
	    AdRequest adRequest = new AdRequest.Builder()
	    .addTestDevice(AdRequest.DEVICE_ID_EMULATOR)
	    .addTestDevice("291C6D36BC647F589BDE42188DF3E9B8")
	    .build();
	    
	    interstitial.loadAd(adRequest);
	    interstitial.setAdListener(new AdListener() {
	    	  public void onAdLoaded() {displayInterstitial();}
	    	  public void onAdFailedToLoad(int errorcode) { close = true;}
	    	  public void onAdClosed() {close = true;}
	    	});
	}
	
	public void displayInterstitial() {
	    if (interstitial.isLoaded()) {
	      interstitial.show();
	    }
	  }
}
