package eu.romainpellerin.smsreader.broadcastreceivers;

import eu.romainpellerin.smsreader.others.MusicPlayer;
import android.bluetooth.BluetoothHeadset;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.telephony.TelephonyManager;
import android.util.Log;

public class BluetoothReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		if (BluetoothHeadset.ACTION_CONNECTION_STATE_CHANGED.equals(intent.getAction())) {
			int extra = intent.getIntExtra("android.bluetooth.profile.extra.STATE", -1);
			if (extra == android.bluetooth.BluetoothProfile.STATE_CONNECTED) {
				Log.d("bluetooth", "Bluetooth headset plugged");
				// If no call at the moment, let's play music :)
                TelephonyManager telephony = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
                SharedPreferences prefs = context.getSharedPreferences("eu.romainpellerin.smsreader", Context.MODE_PRIVATE);
        		int callState = telephony.getCallState();
        		if ((callState == TelephonyManager.CALL_STATE_IDLE) && (prefs.getBoolean("play_on_plugin", false)))
        			MusicPlayer.playMusic(context, prefs.getString("playlist", ""));
			}
		}
	}

	
}
