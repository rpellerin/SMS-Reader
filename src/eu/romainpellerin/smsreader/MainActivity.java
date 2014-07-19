package eu.romainpellerin.smsreader;

import android.app.Activity;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.RemoteViews;
import android.widget.Switch;

public class MainActivity extends Activity {

	private SharedPreferences prefs;
	private Switch enable_all;
	private CheckBox headphones;
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        prefs = getSharedPreferences("eu.romainpellerin.smsreader", MODE_PRIVATE);
        enable_all = (Switch) findViewById(R.id.enable_all);
        enable_all.setChecked(prefs.getBoolean("enable_all", true));
        enable_all.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView,	boolean isChecked) {
				prefs.edit().putBoolean("enable_all", isChecked).commit();
			}
		});
        headphones = (CheckBox) findViewById(R.id.headphones);
        headphones.setChecked(prefs.getBoolean("only_when_headphones", true));
        headphones.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView,	boolean isChecked) {
				prefs.edit().putBoolean("only_when_headphones", isChecked).commit();
			}
		});
        prefs.registerOnSharedPreferenceChangeListener(new OnSharedPreferenceChangeListener() {
			@Override
			public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
				if (key.equals("enable_all")) {
					headphones.setEnabled(sharedPreferences.getBoolean(key, true));
					RemoteViews view = new RemoteViews(getPackageName(), R.layout.widget);
			        view.setTextViewText(R.id.button1, (prefs.getBoolean("enable_all", true) ? "ON" : "OFF"));
			        ComponentName thisWidget = new ComponentName(MainActivity.this, WidgetProvider.class);
			        AppWidgetManager manager = AppWidgetManager.getInstance(MainActivity.this);
			        manager.updateAppWidget(thisWidget, view);
				}
			}
        });
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
    }
}
