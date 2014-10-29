package eu.romainpellerin.smsreader.services;

import android.app.IntentService;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import eu.romainpellerin.smsreader.broadcastreceivers.HeadsetPlugReceiver;

/**
 * I made a service because I need the BroadcastReceiver to be registered even if the app is killed
 * Normally it should be a singleton
 * And it should never be killed
 */
public class MusicService extends IntentService {

	private HeadsetPlugReceiver headsetplugreceiver; // singleton

	public MusicService() {
		super("MusicService");
	}

	@Override
	public void onCreate() {
		super.onCreate();
		if (headsetplugreceiver == null) {
			headsetplugreceiver = new HeadsetPlugReceiver();
		}
		IntentFilter receiverFilter = new IntentFilter(Intent.ACTION_HEADSET_PLUG);
		// http://forum.xda-developers.com/showpost.php?s=9f31526e699263e5a233526c70468f40&p=45575003&postcount=4
		this.registerReceiver(headsetplugreceiver, receiverFilter);
		//Log.e("service","create");
	}

	@Override
	public IBinder onBind(Intent intent) {
		// We don't provide binding, so return null
		return null;
	}

	@Override
	public void onDestroy() {
		//Log.e("service","destroy");
		super.onDestroy();
		this.unregisterReceiver(headsetplugreceiver);
		// TODO relaunch the service
	}

	@Override
	protected void onHandleIntent(Intent intent) {
		//Log.e("ok","onhandleintent "+intent.toString());
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		//Log.e("ok","onstartcommand "+(intent!=null?intent.toString():"(null)"));
		// We want this service to continue running until it is explicitly
		// stopped, so return sticky.
		return START_STICKY;
	}

}
