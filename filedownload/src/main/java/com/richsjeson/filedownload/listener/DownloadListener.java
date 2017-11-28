package com.richsjeson.filedownload.listener;

/**
 * Created by richsjeson on 2017/11/26.
 */

public interface DownloadListener {

    /**
     * 下载进度
     */
    void progress(long downloadId, int totalBytes, int currentBytes);
    /**
     * 下载完成
     */
    void complete(long downloadId);
    /**
     * 下载失败
     */
    void failure(long downloadId, int  errorCode);
    /**
     * 等待下载
     */
    void pending(long downloadId);
    /**
     * 暂停下载
     */
    void pause(long downloadId);
    /**
     * 下载前的流量提醒
     */
    void trafficReminder();

    void addTask(long downloadId);
}
