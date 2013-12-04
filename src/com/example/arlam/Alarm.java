package com.example.arlam;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;



import android.app.Activity;
import android.app.AlarmManager;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

import android.os.Bundle;

import android.preference.PreferenceManager;

import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

public class Alarm extends Activity {

	private static final int DAYINMILLISECOND = 86400000;
	

	Button activateButton;
	Button deactivateButton;
	View activateView;
	View deactivateView;
	TimePicker timepicker;
	static TextView timePassed;
	SharedPreferences sharedPrefs;
	

	Intent intent;
	PendingIntent pendingIntent;
	AlarmManager alarmManger;
	Calendar cal;

	long timeUntilAlarm;
	long currentTimeMilliSecond;
	long alarmTime;
	int snoozeTime;

	static long seconds;

	Calendar c = Calendar.getInstance();
	int second = c.get(Calendar.SECOND);
	int minutes = c.get(Calendar.MINUTE);
	int hours = c.get(Calendar.HOUR_OF_DAY);

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_alarm);

		activateButton = (Button) findViewById(R.id.button_alarm_activate);
		deactivateButton = (Button) findViewById(R.id.button_alarm_deactivate);

		timePassed = (TextView) findViewById(R.id.textView_time_until_alarm);

		activateView = findViewById(R.id.view_activate);
		deactivateView = findViewById(R.id.view_deactivate);

		boolean status_prefs = loadSavedPreferences();

		Log.v("SharedPrefrences_alarmIsSet:", "" + status_prefs);
		if (loadSavedPreferences()) {

			activateView.setVisibility(View.VISIBLE);
			deactivateView.setVisibility(View.INVISIBLE);
		} else {
			activateView.setVisibility(View.INVISIBLE);
			deactivateView.setVisibility(View.VISIBLE);
		}

		timepicker = (TimePicker) findViewById(R.id.timePicker);
		timepicker.setIs24HourView(true);
		timepicker.setCurrentHour(hours);
		timepicker.setCurrentMinute(minutes);

		activateButton.setOnClickListener(activateListener);
		deactivateButton.setOnClickListener(deactivateListener);

		sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this);

		cancelNotification(Alarm.this, Snooze.MY_NOTIFICATION_ID);
		
		if(loadSavedPreferencesSnooze()) {
		
		setSnooze();
		}
	}

	OnClickListener activateListener = new OnClickListener() {
		@Override
		public void onClick(View v) {

			
			if (!loadSavedPreferences()) {
				cal = Calendar.getInstance();
				cal.set(Calendar.HOUR_OF_DAY, timepicker.getCurrentHour());
				cal.set(Calendar.MINUTE, timepicker.getCurrentMinute());

				alarmTime = cal.getTimeInMillis();
				Log.v("Alarm.alarmTime:", "" + alarmTime);
				Log.v("Alarm.hours:", "" + hours);
				Log.v("Alarm.alarmTimeDate:",
						"" + getDate(alarmTime, "HH:mm:ss.SSS"));

				currentTimeMilliSecond = System.currentTimeMillis();
				Log.v("Alarm.currentTimeMilliSecond:", ""
						+ currentTimeMilliSecond);

				timeUntilAlarm = alarmTime - currentTimeMilliSecond;
				Log.v("Alarm.timeUntilAlarm:", "" + timeUntilAlarm);

				if (timeUntilAlarm < 0)
					timeUntilAlarm = DAYINMILLISECOND + timeUntilAlarm;

				
				timePassed.setText(formatTime(timeUntilAlarm)); 
				
				savePreferences("alarmStatus", true);
				sendAlarm();

				deactivateView.setVisibility(View.INVISIBLE);
				activateView.setVisibility(View.VISIBLE);

			} else {

				Toast.makeText(Alarm.this, "Alarm bereits gesetzt!",
						Toast.LENGTH_SHORT).show();

			}

		}
	};

	OnClickListener deactivateListener = new OnClickListener() {
		@Override
		public void onClick(View v) {

			savePreferences("alarmStatus", false);
			savePreferences("SnoozeAktiviert", false);
			deactivateView.setVisibility(View.VISIBLE);
			activateView.setVisibility(View.INVISIBLE);

			timePassed.setText("");

			cancelAlarm();

		}
	};

	public static String formatTime(long millis) {
		String output = "";
		seconds = millis / 1000;
		long minutes = seconds / 60;
		long hours = minutes / 60;

		seconds = seconds % 60;
		minutes = minutes % 60;
		hours = hours % 60;

		String secondsD = String.valueOf(seconds);
		String minutesD = String.valueOf(minutes);
		String hoursD = String.valueOf(hours);

		if (seconds < 10)
			secondsD = "0" + seconds;
		if (minutes < 10)
			minutesD = "0" + minutes;

		if (hours < 10)
			hoursD = "0" + hours;

		output = hoursD + " : " + minutesD + " : " + secondsD;

		return output;
	}

	public static String getDate(long milliSeconds, String dateFormat) {
		// Create a DateFormatter object for displaying date in specified
		// format.
		DateFormat formatter = new SimpleDateFormat(dateFormat);

		// Create a calendar object that will convert the date and time value in
		// milliseconds to date.
		Calendar calendar = Calendar.getInstance();
		calendar.setTimeInMillis(milliSeconds);
		return formatter.format(calendar.getTime());
	}

	private void savePreferences(String key, boolean value) {

		SharedPreferences sharedPreferences = PreferenceManager
				.getDefaultSharedPreferences(this);
		Editor editor = sharedPreferences.edit();
		editor.putBoolean(key, value);
		editor.commit();

	}

	private boolean loadSavedPreferences() {

		SharedPreferences sharedPreferences = PreferenceManager
				.getDefaultSharedPreferences(this);
		boolean alarmIsSet = sharedPreferences.getBoolean("alarmStatus", false);

		return alarmIsSet;
	}
	
	public boolean loadSavedPreferencesSnooze() {

		SharedPreferences sharedPreferences = PreferenceManager
				.getDefaultSharedPreferences(this);
		boolean snoozeIsSet = sharedPreferences.getBoolean("SnoozeAktiviert", false);

		return snoozeIsSet;
	}

	public int loadSavedPreferencesGetSnoozeTime() {

		SharedPreferences sharedPreferences = PreferenceManager
				.getDefaultSharedPreferences(this);
		String snoozeIsSet = sharedPreferences.getString("snoozeInterval", "5");
		int snoozeTime = Integer.parseInt(snoozeIsSet);
		return snoozeTime;
	}
	
	private void cancelAlarm() {

		intent = new Intent(Alarm.this, MyReceiver.class);
		pendingIntent = PendingIntent.getBroadcast(Alarm.this, 0, intent,
				PendingIntent.FLAG_UPDATE_CURRENT);

		AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
		alarmManager.cancel(pendingIntent);

	}

	private void sendAlarm() {

		Log.v("Alarm", "Broadcast Alarm");

		intent = new Intent(Alarm.this, MyReceiver.class);
		pendingIntent = PendingIntent.getBroadcast(Alarm.this, 0, intent,
				PendingIntent.FLAG_CANCEL_CURRENT);

		if (timeUntilAlarm < 0) {

			Calendar cal = Calendar.getInstance();
			cal.add(Calendar.MILLISECOND, DAYINMILLISECOND);

			AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
			alarmManager.set(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(),
					pendingIntent);

		} else {

			Calendar calendar = Calendar.getInstance();
			calendar.setTimeInMillis(alarmTime);

			AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
			alarmManager.set(AlarmManager.RTC_WAKEUP,
					calendar.getTimeInMillis(), pendingIntent);
		}

	}

	/**
	 * Menu wird aufgebaut
	 */
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.alarm, menu);
		return true;
	}

	/**
	 * Wenn ein Menu-Item ausgewaehlt wird
	 */
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {

		switch (item.getItemId()) {

		case R.id.action_settings:

			// Start der Setting Activity
			Intent intent = new Intent(this, Setting.class);
			// Start der Setting_main, der rueckgabe wert wird ueber die shared
			// preferences ausgelesen
			startActivity(intent);

			return true;

		default:

			return super.onOptionsItemSelected(item);
		}
	}
	
	public static void cancelNotification(Context ctx, int notifyId) {
	    String ns = Context.NOTIFICATION_SERVICE;
	    NotificationManager nMgr = (NotificationManager) ctx.getSystemService(ns);
	    nMgr.cancel(notifyId);
	}
	
	public void setSnooze() {
		
		
			
			timePassed.setText("Snooze in: " + loadSavedPreferencesGetSnoozeTime());
			
			intent = new Intent(Alarm.this, MyReceiver.class);
			pendingIntent = PendingIntent.getBroadcast(Alarm.this, 0, intent,
					PendingIntent.FLAG_CANCEL_CURRENT);
			
			Calendar calendar = Calendar.getInstance();
			calendar.setTimeInMillis(System.currentTimeMillis());
			calendar.add(Calendar.MINUTE, loadSavedPreferencesGetSnoozeTime());

			AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
			alarmManager.set(AlarmManager.RTC_WAKEUP,
					calendar.getTimeInMillis(), pendingIntent);
			
		
		
	}
}