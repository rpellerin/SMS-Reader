package eu.romainpellerin.smsreader.activities;

import android.app.Activity;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.RemoteViews;
import android.widget.Switch;
import eu.romainpellerin.smsreader.R;
import eu.romainpellerin.smsreader.appwidgetproviders.WidgetProvider;
import eu.romainpellerin.smsreader.services.MusicService;

public class MainActivity extends Activity {

	private SharedPreferences prefs;
	private Switch enable_all;
	private CheckBox headphones;
	private Switch play_on_plugin;
	private EditText playlist;
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        prefs = getSharedPreferences("eu.romainpellerin.smsreader", MODE_PRIVATE);
        enable_all = (Switch) findViewById(R.id.enable_all);
        headphones = (CheckBox) findViewById(R.id.headphones);
        play_on_plugin = (Switch) findViewById(R.id.play_on_plugin);
        playlist = (EditText) findViewById(R.id.playlist);
        
        enable_all.setChecked(prefs.getBoolean("enable_all", true));
        enable_all.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView,	boolean isChecked) {
				prefs.edit().putBoolean("enable_all", isChecked).commit();
			}
		});
        headphones.setChecked(prefs.getBoolean("only_when_headphones", true));
        headphones.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView,	boolean isChecked) {
				prefs.edit().putBoolean("only_when_headphones", isChecked).commit();
			}
		});
        play_on_plugin.setChecked(prefs.getBoolean("play_on_plugin", false));
        play_on_plugin.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView,	boolean isChecked) {
				prefs.edit().putBoolean("play_on_plugin", isChecked).commit();
			}
		});
        playlist.setText(prefs.getString("playlist", ""));
        playlist.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
                String str = s.toString();
                Log.e("called",playlist.getText().toString());
				prefs.edit().putString("playlist", str).commit();
            }

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {}

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {}
		});
        prefs.registerOnSharedPreferenceChangeListener(new OnSharedPreferenceChangeListener() {
			@Override
			public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
				if (key.equals("enable_all")) {
					headphones.setEnabled(sharedPreferences.getBoolean(key, true));
					// Widget below
					RemoteViews view = new RemoteViews(getPackageName(), R.layout.widget);
			        view.setTextViewText(R.id.button1, (prefs.getBoolean("enable_all", true) ? "ON" : "OFF"));
			        ComponentName thisWidget = new ComponentName(MainActivity.this, WidgetProvider.class);
			        AppWidgetManager manager = AppWidgetManager.getInstance(MainActivity.this);
			        manager.updateAppWidget(thisWidget, view);
				}
				else if (key.equals("play_on_plugin")) {
					playlist.setEnabled(sharedPreferences.getBoolean(key, false));
				}
			}
        });
        startService(new Intent(this, MusicService.class));
    }
    
    public void param(View v) {
    	Intent intent = new Intent(); // Cet intent redirige l'utilisateur vers les paramètres du téléphone pour activer TTS
		intent.setAction("com.android.settings.TTS_SETTINGS");
		startActivity(intent);
    }
    
    @Override
    public void onResume() {
    	super.onResume();
    	enable_all.setChecked(prefs.getBoolean("enable_all", true));
    	headphones.setEnabled(prefs.getBoolean("enable_all", true));
    	play_on_plugin.setChecked(prefs.getBoolean("play_on_plugin", false));
    	playlist.setEnabled(prefs.getBoolean("play_on_plugin", false));
    }
}
