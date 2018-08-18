package com.smile.threadstudy.DownloadFlie;

import android.os.AsyncTask;

/**
 * author: smile .
 * date: On 2018/5/6
 */

public class DownloadTask extends AsyncTask<Void, Integer, Boolean> {

    /**
     * 刚开始执行的时候调用，可以用于进行一些界面上的初始化操作，比如说显示一个进度条对话框
     */
    @Override
    protected void onPreExecute() {
        super.onPreExecute();

    }

    /**
     * 这里的代码会在子线程中执行,可以执行耗时操作
     *
     * @param voids
     * @return
     */
    @Override
    protected Boolean doInBackground(Void... voids) {
        //  反馈当前任务的进度，执行完这个方法会调用onProgressUpdate 方法
        publishProgress(50);
        return null;
    }

    /**
     * 根据返回的数据更新UI
     *
     * @param values
     */
    @Override
    protected void onProgressUpdate(Integer... values) {
        super.onProgressUpdate(values);

    }

    /**
     * 后台任务执行完毕通过return语句进行返回时  也可以根据返回的数据更新UI
     *
     * @param aBoolean
     */
    @Override
    protected void onPostExecute(Boolean aBoolean) {
        super.onPostExecute(aBoolean);

    }
}
