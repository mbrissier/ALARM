package com.example.arlam;

import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.SystemClock;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.app.Activity;
import android.app.Notification;


public class Snooze extends Activity {

	
	MediaPlayer player;
	Button		snooze;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_snooze);
		
		
		
		snooze = (Button) findViewById(R.id.buttonSnooze);
		snooze.setOnClickListener(snoozeListener);
		
		player = MediaPlayer.create(Snooze.this, R.raw.alarmtune);
		playAlarm();
		
	}

	
	OnClickListener snoozeListener = new OnClickListener() {
		@Override
		public void onClick(View v) {
			
			player.stop();
			
			//TODO: Notification muss angezeigt werden, und neuer Alarm verschoben um snooze zeit
			
			
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

}
