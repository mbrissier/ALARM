package com.example.arlam;

import android.os.CountDownTimer;

public class MyCount extends CountDownTimer {
	
	
	
	private static final String NOALRAM = "00 : 00 : 00";
	
	public MyCount(long millisInFuture, long countDownInterval) {
        super(millisInFuture, countDownInterval);
        
        
    }

    @Override
    public void onFinish() {
        
    	 
    	 Alarm.timePassed.setText(NOALRAM);
//    	 Alarm.playAlarm();
    }

    @Override
    public void onTick(long millisUntilFinished) {
    	Alarm.timePassed.setText(Alarm.formatTime(millisUntilFinished));
    }
    

   
    

}


