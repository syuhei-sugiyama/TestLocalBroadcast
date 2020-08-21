package com.example.testlocalbroadcast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        IntentFilter messageFilter = new IntentFilter("AlarmEvent");
        // Broadcastを受け取るBroadcastReceiverを設定
        DataReceiver dataReceiver = new DataReceiver();
        // LocalBroadcastの設定
        LocalBroadcastManager.getInstance(this)
                .registerReceiver(dataReceiver, messageFilter);

        // 初期の画面表示
        setScreen("Alarm Start");
    }

    // Broadcastを受け取るBroadcastReceiver
    public class DataReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent){
            Log.d("DataReceiver", "onReceive");

            // Broadcastされたメッセージを取り出す
            String message = intent.getStringExtra("Message");
            // スクリーンにメッセージを表示
            setScreen(message);
        }
    }

    private void setScreen(String str){
        TextView textView = findViewById(R.id.text);
        // Broadcastで受け取った時刻を表示する
        textView.setText(str);

        // Alarmの開始
        Button buttonStart = findViewById(R.id.button_start);
        buttonStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Serviceを呼び出す
                Intent intent = new Intent(getApplication(), AlarmService.class);
                // Alarmの停止を解除
                intent.putExtra("StopAlarm", false);
                startService(intent);
            }
        });

        // Alarmを停止する
        Button buttonStop = findViewById(R.id.button_stop);
        buttonStop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplication(), AlarmService.class);
                intent.putExtra("StopAlarm", true);
                startService(intent);
            }
        });

        if (!(str.equals("Alarm Start"))){
            Toast toast = Toast.makeText(
                    this, "Recieved Message", Toast.LENGTH_SHORT
            );
            toast.show();
        }

    }
}
