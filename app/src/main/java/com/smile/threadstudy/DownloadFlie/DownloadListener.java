package com.smile.threadstudy.DownloadFlie;

/**
 * author: smile .
 * date: On 2018/5/6
 */
public interface DownloadListener {
    // 通知下载进度
    void onProgress(int progress);
    // 通知下载成功
    void onSuccess();
    // 通知下载失败
    void onFailed();
    // 通知下载暂停
    void onPaused();
    //通知下载失败
    void onCanceled();
}
