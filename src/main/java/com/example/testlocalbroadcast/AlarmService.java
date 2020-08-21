package com.example.testlocalbroadcast;


import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.RequiresApi;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import java.util.Calendar;
import java.util.Locale;

public class AlarmService extends Service {
    // 繰り返し間隔、1分
    private long repeatPeriod = 1000 * 10;
    // setWindow()でのwindow幅、4秒
    private long windowLengthMillis = 1000 * 4;

    private Context context;

    @Override
    public IBinder onBind(Intent intent){
        return null;
    }

    @Override
    public void onCreate(){
        super.onCreate();
        Log.d("AlarmService", "start");

        context = getApplicationContext();
    }

    // Alarmによって呼び出される
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public int onStartCommand(Intent intent, int flags, int startId){
        Log.d("AlarmService", "received");

        Boolean stopFlag = intent.getBooleanExtra("StopAlarm", false);

        Log.d("stopFlag", ""+stopFlag);

        // Alarmが解除されない限り継続
        if(!stopFlag){
            setNextAlarmService();

            // 現在時刻を取り出す
            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(System.currentTimeMillis());
            int hour = calendar.get(Calendar.HOUR_OF_DAY);
            int minute = calendar.get(Calendar.MINUTE);
            int second = calendar.get(Calendar.SECOND);
            String time = String.format(
                    Locale.US, "%1$02d:%2$02d:%3$02d", hour, minute, second
            );
            Log.d("sendTime", ""+time);
            // Local Broadcastで発信する
            Intent messageIntent = new Intent("AlarmEvent");
            messageIntent.putExtra("Message", time);
            LocalBroadcastManager.getInstance(this).sendBroadcast(messageIntent);
        } else {
            stopAlarmService();
            // Local Broadcastで発信する
            Intent messageIntent = new Intent("AlarmEvent");
            messageIntent.putExtra("Message", "Alarm Stop");
            LocalBroadcastManager.getInstance(this).sendBroadcast(messageIntent);
        }

        return super.onStartCommand(intent, flags, startId);
    }

    // 次のアラームの設定
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private void setNextAlarmService(){

        Intent intent = new Intent(context, AlarmService.class);
        long startMillis = System.currentTimeMillis() + repeatPeriod;

        PendingIntent pendingIntent
                = PendingIntent.getService(context, 0, intent, 0);
        AlarmManager alarmManager
                = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

        if (alarmManager != null){
            // SDK19以下ではsetを使う
            if (Build.VERSION.SDK_INT < 19){
                alarmManager.set(AlarmManager.RTC_WAKEUP, startMillis, pendingIntent);
            }
        } else {
            alarmManager.setWindow(AlarmManager.RTC_WAKEUP,
                    startMillis, windowLengthMillis, pendingIntent);
        }
    }

    private void stopAlarmService(){
        Intent intent = new Intent(context, AlarmService.class);
        PendingIntent pendingIntent
                = PendingIntent.getService(context, 0, intent, 0);
        // アラームを解除する
        AlarmManager alarmManager
                =(AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        if (alarmManager != null){
            alarmManager.cancel(pendingIntent);
        }
    }
}
