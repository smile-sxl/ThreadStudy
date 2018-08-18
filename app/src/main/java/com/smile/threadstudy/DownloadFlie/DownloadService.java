package com.smile.threadstudy.DownloadFlie;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Binder;
import android.os.Environment;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.Toast;

import com.smile.threadstudy.R;

import java.io.File;

public class DownloadService extends Service {
    // 下载的异步操作类
    private DownloadFileTask downloadFileTask;
    // 下载地址
    private String downloadUrl;

    private static final String TAG = "DownloadService";
    // 下载状态的回调
    private DownloadListener listener = new DownloadListener() {
        /**
         *  更新下载进度状态
         * @param progress
         */
        @Override
        public void onProgress(int progress) {
            Log.d(TAG, "onProgress: -------" + progress);
            getNotificationManager().notify(1, getNotification("Downloading", progress));
        }

        /**
         * 下载成功
         */
        @Override
        public void onSuccess() {
            Log.d(TAG, "onSuccess: -------------");
            downloadFileTask = null;
            //下载成功时将前台服务通知关闭，并创建一个下载成功的通知
            stopForeground(true);
            getNotificationManager().notify(1, getNotification("Download Success", -1));
            Toast.makeText(DownloadService.this, "Download Success", Toast.LENGTH_SHORT).show();
        }

        /**
         * 下载失败
         */
        @Override
        public void onFailed() {
            Log.d(TAG, "onFailed: -------------");
            downloadFileTask = null;
            // 下载失败时将前台服务通知关闭，并创建一个下载失败的通知
            stopForeground(true);
            getNotificationManager().notify(1, getNotification("Download Failed", -1));
            Toast.makeText(DownloadService.this, "Download Failed", Toast.LENGTH_SHORT).show();
        }

        /**
         * 下载暂停
         */
        @Override
        public void onPaused() {
            Log.d(TAG, "onPaused: -------------");
            downloadFileTask = null;
            Toast.makeText(DownloadService.this, "Download Paused", Toast.LENGTH_SHORT).show();
        }

        /**
         * 下载取消
         */
        @Override
        public void onCanceled() {
            Log.d(TAG, "onCanceled: -------------");
            downloadFileTask = null;
            stopForeground(true);
            Toast.makeText(DownloadService.this, "Download Canceled", Toast.LENGTH_SHORT).show();
        }
    };

    DownloadBinder mBinder = new DownloadBinder();

    /**
     * 返回这个DownloadBinder 实例
     *
     * @param intent
     * @return
     */
    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    // 创建 DownloadBinder 实例
    class DownloadBinder extends Binder {
        // 开始下载
        public void startDownload(String url) {
            Log.d(TAG, "startDownload--------: 开始下载");
            if (downloadFileTask == null) {
                downloadUrl = url;
                downloadFileTask = new DownloadFileTask(listener);
                downloadFileTask.execute(downloadUrl);
                startForeground(1, getNotification("DownLoading", 0));
                Toast.makeText(DownloadService.this, "Downloading", Toast.LENGTH_SHORT).show();
            }
        }

        // 暂停下载
        public void pauseDownload() {
            Log.d(TAG, "pauseDownload--------: 暂停下载");
            if (downloadFileTask != null) {
                downloadFileTask.pauseDownload();
            }
        }

        // 取消下载
        public void cancelDownload() {
            Log.d(TAG, "cancelDownload--------: 取消下载---" + downloadFileTask);
            if (downloadFileTask != null) {
                downloadFileTask.cancelDownload();
            } else {
                if (downloadUrl != null) {
                    // 先暂停后取消   取消下载时需将文件删除，并通知关闭
                    String fileName = downloadUrl.substring(downloadUrl.lastIndexOf("/"));
                    String directory = Environment.getExternalStoragePublicDirectory
                            (Environment.DIRECTORY_DOWNLOADS).getPath();
                    File file = new File(directory + fileName);
                    if (file.exists()) {
                        file.delete();
                    }
                    getNotificationManager().cancel(1);
                    stopForeground(true);
                    Toast.makeText(DownloadService.this, "Canceled", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    /**
     * 获取通知栏管理器
     *
     * @return
     */
    public NotificationManager getNotificationManager() {
        return (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
    }

    /**
     * 设置通知栏的样式 并获取通知栏的实例
     *
     * @param title
     * @param progress
     * @return
     */
    private Notification getNotification(String title, int progress) {
        Intent[] intents = new Intent[]{(new Intent(this, DownloadActivity.class))};
        PendingIntent pi = PendingIntent.getActivities(this, 0, intents, 0);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this);
        builder.setSmallIcon(R.mipmap.ic_launcher);
        builder.setLargeIcon(BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher));
        builder.setContentIntent(pi);
        builder.setContentTitle(title);
        if (progress > 0) {
            builder.setContentText(progress + "%");
            builder.setProgress(100, progress, false);
        }
        return builder.build();
    }

}
