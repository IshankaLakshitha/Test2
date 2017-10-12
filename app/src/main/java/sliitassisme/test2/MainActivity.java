package sliitassisme.test2;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import java.util.Calendar;

import sliitassisme.test2.FirstTime.FirstTimeDevicesActivity;
import sliitassisme.test2.database.DBhandler;

public class MainActivity extends AppCompatActivity {

    public static DBhandler DATABASEHANDLER;
    public static int firsttime=0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //CallAlrm();



        DATABASEHANDLER=new DBhandler(this,null,null,1);

        if(isFirstTime()) {
            firsttime=1;
        }else{
            firsttime=0;
            //btndone.setVisibility(View.INVISIBLE);
        }
        Intent intent = new Intent(this, FirstTimeDevicesActivity.class);
        startActivity(intent);



    }

    public void CallAlrm(){
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        Intent notificationIntent = new Intent("android.media.action.DISPLAY_NOTIFICATION");
        notificationIntent.addCategory("android.intent.category.DEFAULT");
        PendingIntent broadcast = PendingIntent.getBroadcast(this, 100, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.SECOND, 10);
        /*cal.set(Calendar.HOUR_OF_DAY,22);
        cal.set(Calendar.MINUTE,00);
        cal.set(Calendar.SECOND,00);*/
        alarmManager.setExact(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(), broadcast);

    }

    private boolean isFirstTime()
    {
        SharedPreferences preferences = getPreferences(MODE_PRIVATE);
        boolean ranBefore = preferences.getBoolean("RanBefore", false);
        if (!ranBefore) {
            // first time
            SharedPreferences.Editor editor = preferences.edit();
            editor.putBoolean("RanBefore", true);
            editor.commit();
        }
        return !ranBefore;
    }
}
