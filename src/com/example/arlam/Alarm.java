package com.example.arlam;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.SystemClock;
import android.preference.PreferenceManager;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.TimePicker;

public class Alarm extends Activity {

	private static final long DAYINMILLISECOND = 86400000;

	Button activateButton;
	Button deactivateButton;
	static View activateView;
	static View deactivateView;
	TimePicker timepicker;
	static TextView timePassed;
	SharedPreferences sharedPrefs;
	MyCount counter;
	
	Intent intent;
	PendingIntent pendingIntent;
	AlarmManager alarmManger;
	Calendar cal;

	long timeUntilAlarm;
	long currentTimeMilliSecond;
	long alarmTime;
	boolean alarmIsSet;
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

		if (loadSavedPreferences()) {

			activateView.setVisibility(View.VISIBLE);
			deactivateView.setVisibility(View.INVISIBLE);
		} else {
			activateView.setVisibility(View.VISIBLE);
			deactivateView.setVisibility(View.INVISIBLE);
		}

		timepicker = (TimePicker) findViewById(R.id.timePicker);
		timepicker.setIs24HourView(true);
		timepicker.setCurrentHour(hours);
		timepicker.setCurrentMinute(minutes);

		activateButton.setOnClickListener(activateListener);
		deactivateButton.setOnClickListener(deactivateListener);

		

		sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this);

	}

	OnClickListener activateListener = new OnClickListener() {
		@Override
		public void onClick(View v) {

			cal = Calendar.getInstance();
			cal.set(Calendar.HOUR_OF_DAY, timepicker.getCurrentHour());
			cal.set(Calendar.MINUTE, timepicker.getCurrentMinute());

			alarmTime = cal.getTimeInMillis();
			Log.v("Alarm.alarmTime:", "" + alarmTime);
			Log.v("Alarm.hours:", "" + hours);
			Log.v("Alarm.alarmTimeDate:",
					"" + getDate(alarmTime, "HH:mm:ss.SSS"));

			currentTimeMilliSecond = System.currentTimeMillis();
			Log.v("Alarm.currentTimeMilliSecond:", "" + currentTimeMilliSecond);

			timeUntilAlarm = alarmTime - currentTimeMilliSecond;
			Log.v("Alarm.timeUntilAlarm:", "" + timeUntilAlarm);

			if (timeUntilAlarm < 0)
				timeUntilAlarm = DAYINMILLISECOND + timeUntilAlarm;

			counter = new MyCount(timeUntilAlarm, 1000);
			if (loadSavedPreferences()) {

				counter.cancel();
				counter.onFinish();

			}
			counter.start();

			deactivateView.setVisibility(View.INVISIBLE);
			activateView.setVisibility(View.VISIBLE);

			savePreferences("alarmStatus", true);
			sendAlarm();

		}
	};

	OnClickListener deactivateListener = new OnClickListener() {
		@Override
		public void onClick(View v) {

			if (loadSavedPreferences()) {
				counter.cancel();
				counter.onFinish();

				activateView.setVisibility(View.INVISIBLE);
				deactivateView.setVisibility(View.VISIBLE);

				savePreferences("alarmStatus", true);
			} else {
				savePreferences("alarmStatus", false);

			}
			;
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
		alarmIsSet = sharedPreferences.getBoolean("alarmStatus", false);

		return alarmIsSet;
	}

	private void sendAlarm() {

		Log.v("Alarm", "Broadcast Alarm");
		
		Calendar calendar = Calendar.getInstance();

        calendar.setTimeInMillis(alarmTime);

        

		intent = new Intent(Alarm.this, MyReceiver.class);
		pendingIntent = PendingIntent.getBroadcast(Alarm.this, 0, intent, PendingIntent.FLAG_CANCEL_CURRENT);

		AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
		alarmManager.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
		
	}
}