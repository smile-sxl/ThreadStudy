package com.smile.threadstudy.DownloadFlie;

import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * author: smile .
 * date: On 2018/5/6
 */
//   第一个参数  传给后台参数   第二个 使用整型数据作为进度显示单位   第三个  使用整型数据反馈执行结果
public class DownloadFileTask extends AsyncTask<String, Integer, Integer> {

    // 下载成功
    public static final int TYPE_SUCCESS = 0;
    // 下载失败
    public static final int TYPE_FAILED = 1;
    // 下载暂停
    public static final int TYPE_PAUSED = 2;
    // 下载取消
    public static final int TYPE_CANCELED = 3;
    // 下载状态监听回调
    private DownloadListener listener;
    // 是否取消
    private boolean isCancelled = false;
    // 是否暂停
    private boolean isPaused = false;
    // 当前进度
    private int lastProgress;

    /**
     * 带监听的构造函数
     *
     * @param listener
     */
    public DownloadFileTask(DownloadListener listener) {
        this.listener = listener;
    }

    /**
     * 在后台执行具体的下载逻辑  是在子线程里面 可以执行耗时操作
     */
    @Override
    protected Integer doInBackground(String... strings) {
        // 文件输入流
        InputStream is = null;
        RandomAccessFile accessFile = null;
        File file = null;
        // 记录已下载的文件长度
        long downloadedLength = 0;
        // 获取下载的URL地址
        String downloadUrl = strings[0];
        // 从URL下载地址中截取下载的文件名
        String fileName = downloadUrl.substring(downloadUrl.lastIndexOf("/"));
        // 获取SD卡的Download 目录
        String directory = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getPath();
        //  得到要保存的文件
        file = new File(directory + fileName);
        // 如果文件已经存在  获取文件的长度
        if (file.exists()) {
            downloadedLength = file.length();
        }
        //
        try {
            //   获取待下载文件的字节长度
            long contentLength = getContentLength(downloadUrl);
            //  如果待下载文件的字节长度为0 说明待下载文件有问题
            if (contentLength == 0) {
                return TYPE_FAILED;
            } else if (contentLength == downloadedLength) {
                //   已下载字节和文件总字节相等 说明已经下载完了
                return TYPE_SUCCESS;
            }
            //  获取OkHttpClient 对象
            OkHttpClient client = new OkHttpClient();
            //  创建请求
            Request request = new Request.Builder()
                    //  断点下载，指定从哪个字节开始下载
                    .addHeader("RANGE", "bytes=" + downloadedLength + "-")
                    // 设置下载地址
                    .url(downloadUrl)
                    .build();
            //  获取响应
            Response response = client.newCall(request).execute();
            if (response != null) {
                // 读取服务器响应的数据
                is = response.body().byteStream();
                // 获取随机读取文件类  可以随机读取一个文件中指定位置的数据
                accessFile = new RandomAccessFile(file, "rw");
                // 跳过已下载的字节
                accessFile.seek(downloadedLength);
                //指定每次读取文件缓存区的大小为1KB
                byte[] b = new byte[1024];
                int total = 0;
                int len;
                //   每次读取的字节长度
                while ((len = is.read(b)) != -1) {
                    if (isCancelled) {
                        return TYPE_CANCELED;
                    } else if (isPaused) {
                        return TYPE_PAUSED;
                    } else {
                        // 读取的全部字节的长度
                        total += len;
                        // 写入每次读取的字节长度
                        accessFile.write(b, 0, len);
                        // 计算已下载的百分比
                        int progress = (int) ((total + downloadedLength) * 100 / contentLength);
                        // 更新进度条
                        publishProgress(progress);
                    }
                }
                // 关闭连接  返回成功
                response.body().close();
                return TYPE_SUCCESS;
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                // 关闭输入流
                if (is != null) {
                    is.close();
                }
                // 关闭文件
                if (accessFile != null) {
                    accessFile.close();
                }
                Log.e("TAG", "这里永远都会执行 ");
                // 如果是取消的  就删除掉文件
                if (isCancelled && file != null) {
                    file.delete();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return null;
    }

    /**
     * 获取下载文件的长度
     *
     * @param downloadUrl
     * @return
     * @throws IOException
     */
    private long getContentLength(String downloadUrl) throws IOException {
        // 获取OkHttpClient
        OkHttpClient client = new OkHttpClient();
        // 创建请求
        Request request = new Request.Builder()
                .url(downloadUrl)
                .build();
        //  获取响应
        Response response = client.newCall(request).execute();
        //  如果响应是成功的话
        if (response != null && response.isSuccessful()) {
            // 获取文件的长度  清除响应
            long contentLength = response.body().contentLength();
            response.close();
            return contentLength;
        }
        return 0;
    }

    /**
     * 在界面上更新当前的下载进度
     *
     * @param values
     */
    @Override
    protected void onProgressUpdate(Integer... values) {
        super.onProgressUpdate(values);
        int progress = values[0];
        if (progress > lastProgress) {
            listener.onProgress(progress);
            lastProgress = progress;
        }
    }

    /**
     * 用于通知最后的下载结果
     *
     * @param integer
     */
    @Override
    protected void onPostExecute(Integer integer) {
        super.onPostExecute(integer);
        switch (integer) {
            case TYPE_SUCCESS:
                listener.onSuccess();
                break;
            case TYPE_FAILED:
                listener.onFailed();
                break;
            case TYPE_PAUSED:
                listener.onPaused();
                break;
            case TYPE_CANCELED:
                listener.onCanceled();
                break;
        }
    }

    /**
     * 暂停下载
     */
    public void pauseDownload() {
        isPaused = true;
    }

    /**
     * 取消下载
     */
    public void cancelDownload() {
        isCancelled = true;
    }
}
