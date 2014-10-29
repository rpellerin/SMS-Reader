package eu.romainpellerin.smsreader.broadcastreceivers;

import android.app.SearchManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.provider.MediaStore;
import android.telephony.TelephonyManager;
import android.util.Log;

public class HeadsetPlugReceiver extends BroadcastReceiver {
	
	@Override
	public void onReceive(Context context, Intent intent) {
		if(intent.getAction().equals(Intent.ACTION_HEADSET_PLUG)) {
            int state = intent.getIntExtra("state", -1);
            switch(state) {
                case(0):
                    Log.d("ok", "Headset unplugged");
                    break;
                case(1):
                    Log.d("ok", "Headset plugged");
                	// If no call at the moment, let's play music :)
	                TelephonyManager telephony = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
	                SharedPreferences prefs = context.getSharedPreferences("eu.romainpellerin.smsreader", Context.MODE_PRIVATE);
	        		int callState = telephony.getCallState();
	        		if ((callState == TelephonyManager.CALL_STATE_IDLE) && (prefs.getBoolean("play_on_plugin", false)))
	        			playMusic(context, prefs.getString("playlist", ""));
                    break;
                default:
                    Log.d("ok", "Error");
            }
		}
	}

	/**
	 * Start playing the music
	 * @param context The context
	 */
	private void playMusic(Context context, String playlist) {
		Log.e("playlsit",playlist);
		Intent intent = new Intent(MediaStore.INTENT_ACTION_MEDIA_PLAY_FROM_SEARCH);
		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra(MediaStore.EXTRA_MEDIA_FOCUS, MediaStore.Audio.Playlists.ENTRY_CONTENT_TYPE);
        intent.putExtra(SearchManager.QUERY, playlist);
        if (intent.resolveActivity(context.getPackageManager()) != null) {
            context.startActivity(intent);
        }
	}
}
