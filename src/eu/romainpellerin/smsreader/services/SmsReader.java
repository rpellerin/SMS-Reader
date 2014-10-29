package eu.romainpellerin.smsreader.services;

import java.util.HashMap;

import android.app.Service;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.media.AudioManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.provider.ContactsContract.PhoneLookup;
import android.speech.tts.TextToSpeech;
import android.speech.tts.UtteranceProgressListener;
import android.telephony.SmsMessage;

public class SmsReader extends Service {

	private TextToSpeech tts;
	private HashMap<String, String> hm;
	private String txt;
	private Handler hd;
	private Runnable r;
	private AudioManager audioManager;
	
	@Override
	public IBinder onBind(Intent arg0) {
		return null;
	}

	@Override
	public void onCreate() {
		hm = new HashMap<String, String>();
		hm.put(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID, "utterance");
		hd = new Handler();
		r = new Runnable() {
			@Override
			public synchronized void run() {
				if (!tts.isSpeaking()) stopSelf();
			}
		};
		audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		Bundle extras = intent.getExtras();
		if (extras != null) {
			Object[] messages = (Object[]) extras.get("pdus");

			SmsMessage[] sms = new SmsMessage[messages.length];

			StringBuffer messageFinal = new StringBuffer();

			for (int n = 0; n < messages.length; n++) {
				sms[n] = SmsMessage.createFromPdu((byte[]) messages[n]);
				messageFinal.append(sms[n].getMessageBody());
			}

			String from = sms[0].getOriginatingAddress();
			if (from != null) 
				from = getContactName(from);

			txt = from+": "+messageFinal.toString();
			if (tts == null) {
				tts = new TextToSpeech(this, new TextToSpeech.OnInitListener(){
					@Override
					public void onInit(int status) {
						tts.setOnUtteranceProgressListener(new UtteranceProgressListener() { // Listener
							@Override
							public void onDone(final String utteranceId) {
								audioManager.abandonAudioFocus(null);
								hd.postDelayed(r, 2000);
							}
							@Override
							public void onError(String utteranceId) {}
							@Override
							public void onStart(String utteranceId) {}
						});
						tts.setSpeechRate(0.7f);
						speakout(txt);
					}
				});
			}
			else 
				speakout(txt);
		}
		return START_REDELIVER_INTENT;
	}

	private void speakout(String text) {
		audioManager.requestAudioFocus(null, AudioManager.STREAM_MUSIC, AudioManager.AUDIOFOCUS_GAIN_TRANSIENT);
		tts.speak(text, TextToSpeech.QUEUE_ADD, hm);
	}

	private String getContactName(String phoneNumber) {
		ContentResolver cr = getContentResolver();
		Uri uri = Uri.withAppendedPath(PhoneLookup.CONTENT_FILTER_URI, Uri.encode(phoneNumber));
		Cursor cursor = cr.query(uri, new String[]{PhoneLookup.DISPLAY_NAME}, null, null, null);
		if (cursor == null) {
			return phoneNumber;
		}
		String contactName = null;
		if(cursor.moveToFirst()) {
			contactName = cursor.getString(cursor.getColumnIndex(PhoneLookup.DISPLAY_NAME));
		}

		if(cursor != null && !cursor.isClosed()) {
			cursor.close();
		}
		return (contactName != null) ? contactName : phoneNumber;
	}	

	@Override
	public void onDestroy() {
		super.onDestroy();
		if (tts != null) {
			tts.shutdown(); // D�truit d�finitivement
		}
	}
}
