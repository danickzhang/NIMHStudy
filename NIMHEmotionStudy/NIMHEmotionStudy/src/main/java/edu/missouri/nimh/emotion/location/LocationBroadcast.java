package edu.missouri.nimh.emotion.location;

import edu.missouri.nimh.emotion.Utilities;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.location.LocationManager;

public class LocationBroadcast extends BroadcastReceiver {

	String TAG = "Location Broadcast";
	public static LocationManager locationM;
	public static String ID;
//	static WakeLock wl;
	
	@Override
	public void onReceive(Context context, Intent intent) {
		// TODO Auto-generated method stub

		
		String action = intent.getAction();
		locationM = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
		ID = Utilities.getSP(context, Utilities.SP_LOGIN).getString(Utilities.SP_KEY_LOGIN_USERID, "0000");
		
//		PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);  
//		wl = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "SurveyBroadcast");  
	    
		
		if(action.equals(LocationUtilities.ACTION_START_LOCATION)){
			Utilities.Log(TAG, "location recording start");
			LocationUtilities.requestLocation(locationM);
			
			/*acquire wake lock*/			
//			wl.acquire();
		}
		
		else if(action.equals(LocationUtilities.ACTION_STOP_LOCATION)){
			Utilities.Log(TAG, "location recording stop");
			LocationUtilities.removeLocation(locationM);
			
			/*release wake lock*/
//			wl.release();
		}
	}

}
