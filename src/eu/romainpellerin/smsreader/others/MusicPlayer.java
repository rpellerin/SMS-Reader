package eu.romainpellerin.smsreader.others;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.provider.MediaStore;
import android.util.Log;

public final class MusicPlayer {
	private MusicPlayer() {
	}
	
	/**
	 * Start playing the music
	 * @param context The context
	 */
	public static void playMusic(Context context, String playlist) {
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
