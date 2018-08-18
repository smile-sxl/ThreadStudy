package com.smile.threadstudy.DownloadFlie;

import android.Manifest;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.smile.threadstudy.R;


public class DownloadActivity extends AppCompatActivity implements View.OnClickListener {
    // 开始下载按钮
    private Button btnStart;
    // 暂停下载按钮
    private Button btnPause;
    // 取消下载按钮
    private Button btnCancel;
    // 服务
    private Intent intent;
    // 下载操作的实例
    private DownloadService.DownloadBinder downloadBinder;

    private ServiceConnection serviceConnection = new ServiceConnection() {
        /**
         * 活动与服务绑定成功后
         * @param name
         * @param service
         */
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            downloadBinder = (DownloadService.DownloadBinder) service;
        }

        /**
         * 活动与服务解绑后
         * @param name
         */
        @Override
        public void onServiceDisconnected(ComponentName name) {

        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_download);
        btnStart = (Button) findViewById(R.id.btnStart);
        btnPause = (Button) findViewById(R.id.btnPause);
        btnCancel = (Button) findViewById(R.id.btnCancel);

        btnStart.setOnClickListener(this);
        btnPause.setOnClickListener(this);
        btnCancel.setOnClickListener(this);
        // 启动服务并绑定
        intent = new Intent(this, DownloadService.class);
        startService(intent);
        bindService(intent, serviceConnection, BIND_AUTO_CREATE);
        // 获取写的权限
        if (ContextCompat.checkSelfPermission(DownloadActivity.this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(DownloadActivity.this,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
        }
    }

    @Override
    public void onClick(View v) {
        if (downloadBinder == null) {
            return;
        }
        switch (v.getId()) {
            case R.id.btnStart:
                String url = "https://raw.githubusercontent.com/guolindev/eclipse/master/eclipse-inst-win64.exe";
                downloadBinder.startDownload(url);
                break;
            case R.id.btnPause:
                downloadBinder.pauseDownload();
                break;
            case R.id.btnCancel:
                downloadBinder.cancelDownload();
                break;
        }
    }

    /**
     * 申请权限的返回结果
     *
     * @param requestCode
     * @param permissions
     * @param grantResults
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case 1:
                if (grantResults.length > 0 && grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(this, "拒绝权限将无法使用程序", Toast.LENGTH_SHORT).show();
                    finish();
                }
                break;
        }
    }

    /**
     * 结束活动的时候关闭服务
     */
    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbindService(serviceConnection);
        stopService(intent);
    }
}
