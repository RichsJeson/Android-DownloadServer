package com.richsjeson.filedownload.listener;

import android.os.Message;

/**
 * Created by richsjeson on 2017/11/26.
 * 基于downloadManager+状态机制定的下载器
 */

public interface DownloadDispatch {
    /*
    * 下载进度
    * @param downloadId
    */
    public void progress(long downloadId,int totlalCount,int currentCount);
    /*
     * 下载完成
     * @param downloadId
     */
    public void complete(long downloadId);

    /* 暂停下载
     * @param downloadId
     */
    public void pause(long downloadId);
    /**
     * 下载失败
     * @param downloadId
     * @param  erroCode;错误编码
     */
    public void failure(long downloadId,int erroCode);

    /**
     * 准备下载
     * @param downloadId
     */
    public void pending(long downloadId);
}
