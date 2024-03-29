package com.android.statusbartest;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Build;
import android.os.IBinder;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.android.statusbartest.view.PanelView;

/**
 * inflate 方法
 * How SystemUI pull down
 * 点击状态栏动画显示状态栏
 * 点击的时候直接显示后下拉
 */
public class MainActivity extends AppCompatActivity {

    private PanelView statusBarView;
    private PanelView statusBarView1;
    private LinearLayout root;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button button = (Button) findViewById(R.id.btn);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, StatusBarService.class);
                stopService(intent);
                startService(intent);
//                unbindService(mServiceConnection);
//                bindService(intent, mServiceConnection, Context.BIND_AUTO_CREATE);
//                finish();

            }
        });

        Button button2 = (Button) findViewById(R.id.btn2);
        button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(MainActivity.this, "onPause", Toast.LENGTH_SHORT).show();
                Context context = null;
            }
        });

        if (Build.VERSION.SDK_INT >= 23) {
            if (!Settings.canDrawOverlays(this)) {
                Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivityForResult(intent, 1);
            } else {
                //TODO do something you need
            }
        }

    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.e("Main", "onPause");
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.e("Main", "onStop");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.e("Main", "onDestroy");
    }

    private ServiceConnection mServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            StatusBarService.MyBinder myBinder = (StatusBarService.MyBinder) iBinder;
            myBinder.addView();
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {

        }
    };

}
