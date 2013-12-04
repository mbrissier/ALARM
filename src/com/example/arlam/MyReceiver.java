package com.example.arlam;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class MyReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {

	

		// Start snooze activity
		Intent snooze = new Intent(context, Snooze.class);
		snooze.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		context.startActivity(snooze);

	}
}