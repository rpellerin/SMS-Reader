package eu.romainpellerin.smsreader.appwidgetproviders;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.widget.RemoteViews;
import eu.romainpellerin.smsreader.R;

public class WidgetProvider extends AppWidgetProvider {

	@Override
	public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {

		// Get all ids
		ComponentName thisWidget = new ComponentName(context, WidgetProvider.class);
		int[] allWidgetIds = appWidgetManager.getAppWidgetIds(thisWidget);
		for (int widgetId : allWidgetIds) {

			RemoteViews remoteViews = new RemoteViews(context.getPackageName(),	R.layout.widget);
			// Set the state of switch button
			SharedPreferences prefs = context.getSharedPreferences("eu.romainpellerin.smsreader", Context.MODE_PRIVATE);
			remoteViews.setTextViewText(R.id.button1, (prefs.getBoolean("enable_all", true) ? "ON" : "OFF"));



			// Register an onClickListener			
			Intent intent = new Intent(context, WidgetProvider.class);
			intent.putExtra("maj", "0");

			intent.setAction(AppWidgetManager.ACTION_APPWIDGET_UPDATE);
			intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, appWidgetIds);

			PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
			remoteViews.setOnClickPendingIntent(R.id.button1, pendingIntent);
			appWidgetManager.updateAppWidget(widgetId, remoteViews);
		}
	}

	@Override
	public void onReceive(Context context, Intent intent) {
		if (intent.hasExtra("maj")) {
			SharedPreferences prefs = context.getSharedPreferences("eu.romainpellerin.smsreader", Context.MODE_PRIVATE);
			prefs.edit().putBoolean("enable_all", !prefs.getBoolean("enable_all", true)).commit();
		}
		super.onReceive(context, intent);
	}
}
