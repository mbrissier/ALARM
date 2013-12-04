package com.example.arlam;

import android.app.Activity;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.SystemClock;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class Snooze extends Activity {

	protected static final int MY_NOTIFICATION_ID= 1234;
	
	MediaPlayer player;
	Button snooze;
	TaskStackBuilder stackBuilder;
	String snoozeTime;
	
	Alarm alarm;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_snooze);

		snooze = (Button) findViewById(R.id.buttonSnooze);
		snooze.setOnClickListener(snoozeListener);

		player = MediaPlayer.create(Snooze.this, R.raw.alarmtune);
		playAlarm();
		stackBuilder = TaskStackBuilder.create(this);

		SharedPreferences sharedPrefs = PreferenceManager
				.getDefaultSharedPreferences(this);
		snoozeTime = sharedPrefs.getString("snoozeInterval", "5");
		
		

	}

	OnClickListener snoozeListener = new OnClickListener() {
		@Override
		public void onClick(View v) {

			player.stop();

			
			NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(
					Snooze.this)
					.setSmallIcon(R.drawable.alarm_on)
					.setContentTitle("Alarm")
					.setContentText(
							"Snooze aktiviert, in: " + snoozeTime + " Min");
								
			// Creates an explicit intent for an Activity in your app
			Intent resultIntent = new Intent(Snooze.this, Alarm.class);

			// The stack builder object will contain an artificial back stack
			// for the
			// started Activity.
			// This ensures that navigating backward from the Activity leads out
			// of
			// your application to the Home screen.

			// Adds the back stack for the Intent (but not the Intent itself)
			stackBuilder.addParentStack(Alarm.class);
			// Adds the Intent that starts the Activity to the top of the stack
			stackBuilder.addNextIntent(resultIntent);
			PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(
					0, PendingIntent.FLAG_UPDATE_CURRENT);
			
			mBuilder.setContentIntent(resultPendingIntent);
			NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

			// mId allows you to update the notification later on.
			
			savePreferences("SnoozeAktiviert", true);
			mNotificationManager.notify(MY_NOTIFICATION_ID, mBuilder.build());
			
			finish();
			

		}

	};

	public void playAlarm() {

		new Thread(new Runnable() {
			@Override
			public void run() {
				player.start();
				SystemClock.sleep(20000);
				player.stop();
			}
		}).start();

	}
	
	private void savePreferences(String key, boolean value) {

		SharedPreferences sharedPreferences = PreferenceManager
				.getDefaultSharedPreferences(this);
		Editor editor = sharedPreferences.edit();
		editor.putBoolean(key, value);
		editor.commit();

	}

	

}
