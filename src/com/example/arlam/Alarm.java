package com.example.arlam;


import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;


import android.app.Activity;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.TimePicker;



public class Alarm extends Activity {
	
	
	
	
	Button activate;
	Button deactivate;
	View active;
	View deactive;
	TimePicker timepicker;
	TextView timePassed;
	
	long seconds;
	
	Calendar c = Calendar.getInstance(); 
	int second = c.get(Calendar.SECOND);
	int minutes = c.get(Calendar.MINUTE);
	int hours = c.get(Calendar.HOUR_OF_DAY);
	
	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_alarm);
		
		activate = (Button) findViewById(R.id.button_alarm_activate);
		deactivate = (Button) findViewById(R.id.button_alarm_deactivate);
		
		timePassed = (TextView) findViewById(R.id.textView_time_until_alarm);
		
		active = findViewById(R.id.view_activate);
		deactive = findViewById(R.id.view_deactivate);
		
		timepicker = (TimePicker) findViewById(R.id.timePicker);
		timepicker.setIs24HourView(true);
		timepicker.setCurrentHour(hours);
		timepicker.setCurrentMinute(minutes);
		
		
		activate.setOnClickListener(activateListener);
		deactivate.setOnClickListener(deactivateListener);
		
		
	}

	OnClickListener activateListener = new OnClickListener() {
		@Override
		public void onClick(View v) {
		
			Calendar cal = Calendar.getInstance();
			cal.set(Calendar.HOUR_OF_DAY, timepicker.getCurrentHour());
			cal.set(Calendar.MINUTE, timepicker.getCurrentMinute());
			
			long alarmTime = cal.getTimeInMillis();
			Log.v("Alarm.alarmTime:", "" +alarmTime);
			Log.v("Alarm.hours:", "" +hours);
			Log.v("Alarm.alarmTimeDate:", "" + getDate(alarmTime, "HH:mm:ss.SSS"));
		
			long currentTimeMilliSecond = System.currentTimeMillis();
			Log.v("Alarm.currentTimeMilliSecond:", "" +currentTimeMilliSecond);
		
			long timeUntilAlarm = 	alarmTime - currentTimeMilliSecond;
			Log.v("Alarm.timeUntilAlarm:", "" +timeUntilAlarm);
		
			if(timeUntilAlarm < 0)
				timeUntilAlarm = 86400000 + timeUntilAlarm;
			
			new CountDownTimer(timeUntilAlarm, 1000) {

			     @Override
				public void onTick(long millisUntilFinished) {
			         timePassed.setText(formatTime(millisUntilFinished));
			     }

			     @Override
				public void onFinish() {
			         timePassed.setText("Alarm!");
			     }
			  }.start();
		  
			  	activate.setVisibility(View.VISIBLE);
		
		
			

		}
	};

	OnClickListener deactivateListener = new OnClickListener() {
		@Override
		public void onClick(View v) {

			

		}
	};
	
	public String formatTime(long millis) 
	{
	    String output = "";
	    seconds = millis / 1000;
	    long minutes = seconds / 60;
	    long hours=minutes/ 60;

	    seconds = seconds % 60;
	    minutes = minutes % 60;
	    hours=hours%60;

	    String secondsD = String.valueOf(seconds);
	    String minutesD = String.valueOf(minutes);
	    String hoursD=String.valueOf(hours);

	    if (seconds < 10)
	        secondsD = "0" + seconds;
	    if (minutes < 10)
	        minutesD = "0" + minutes;

	    if (hours < 10)
	        hoursD = "0" + hours;

	    output = hoursD+" : "+minutesD + " : " + secondsD;

	    return output;
	}
	
	
	
	public static String getDate(long milliSeconds, String dateFormat)
	{
	    // Create a DateFormatter object for displaying date in specified format.
	    DateFormat formatter = new SimpleDateFormat(dateFormat);

	    // Create a calendar object that will convert the date and time value in milliseconds to date. 
	     Calendar calendar = Calendar.getInstance();
	     calendar.setTimeInMillis(milliSeconds);
	     return formatter.format(calendar.getTime());
	}
	
}


