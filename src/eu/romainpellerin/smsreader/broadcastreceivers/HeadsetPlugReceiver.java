package eu.romainpellerin.smsreader.broadcastreceivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.telephony.TelephonyManager;
import android.util.Log;
import eu.romainpellerin.smsreader.others.MusicPlayer;

public class HeadsetPlugReceiver extends BroadcastReceiver {
	
	private static long last_unplugged = System.currentTimeMillis();
	
	@Override
	public void onReceive(Context context, Intent intent) {
		if(intent.getAction().equals(Intent.ACTION_HEADSET_PLUG)) {
            int state = intent.getIntExtra("state", -1);
            switch(state) {
                case(0):
                    Log.d("ok", "Headset unplugged");
                	last_unplugged = System.currentTimeMillis();
                    break;
                case(1):
                    Log.d("ok", "Headset plugged");
                	if ((System.currentTimeMillis() - 3000) <= last_unplugged) // Only if unplugged since more than 2 seconds
                		return;
                	// If no call at the moment, let's play music :)
	                TelephonyManager telephony = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
	                SharedPreferences prefs = context.getSharedPreferences("eu.romainpellerin.smsreader", Context.MODE_PRIVATE);
	        		int callState = telephony.getCallState();
	        		if ((callState == TelephonyManager.CALL_STATE_IDLE) && (prefs.getBoolean("play_on_plugin", false)))
	        			MusicPlayer.playMusic(context, prefs.getString("playlist", ""));
                    break;
                default:
                    Log.d("ok", "Error");
            }
		}
	}
}
