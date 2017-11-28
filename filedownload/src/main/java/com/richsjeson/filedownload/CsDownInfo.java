package com.richsjeson.filedownload;
//import android.app.DownloadManager;
import android.database.Cursor;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.util.Log;

import com.richsjeson.filedownload.api.DownloadInfo;
import com.richsjeson.filedownload.api.DownloadManager;
import com.richsjeson.filedownload.api.Downloads;
import com.richsjeson.filedownload.listener.DownloadDispatch;

/**
 * Created by richsjeson on 2017/11/26.
 *
 * 1.加入队列
 * 2.下载队列
 * 3.完成队列
 * 4.准备下载
 * 5.下载失败
 * 6.流量提醒
 *
 */

public class CsDownInfo {

    private long downId;

    public void addTaskDownload(long downId){
        this.downId=downId;

    }

    @RequiresApi(api = Build.VERSION_CODES.GINGERBREAD)
    public void update(DownloadDispatch listener, DownloadManager manager){

        updateProgress(this.downId,listener,manager);
    }


    @RequiresApi(api = Build.VERSION_CODES.GINGERBREAD)
    private void updateProgress(long downloadId, DownloadDispatch listener, DownloadManager manager) {
        getBytesAndStatus(downloadId,manager,listener);
    }

    /**
     * 通过query查询下载状态，包括已下载数据大小，总大小，下载状态
     *
     * @param downloadId
     * @return
     */
    @RequiresApi(api = Build.VERSION_CODES.GINGERBREAD)
    private void  getBytesAndStatus(long downloadId, DownloadManager manager, DownloadDispatch listener) {
        int[] downloadBytes = new int[2];
        DownloadManager.Query query = new DownloadManager.Query().setFilterById(downloadId);
        Cursor cursor = null;
        try {
            cursor = manager.query(query);
            if (cursor != null && cursor.moveToNext()) {
                //下载成功
                Log.i(this.getClass().getName(),"getBytesAndStatus:"+cursor.getColumnIndex(DownloadManager.COLUMN_STATUS));
                if(cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_STATUS))==DownloadManager.STATUS_SUCCESSFUL){
                    listener.complete(downloadId);
                }else if(cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_STATUS))==DownloadManager.STATUS_FAILED){
                    listener.failure(downloadId,cursor.getInt(cursor.getColumnIndex(Downloads.COLUMN_ERROR_CODE)));
                }else if(cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_STATUS))==DownloadManager.STATUS_PAUSED){
                    //暂停下载
                    listener.pause(downloadId);
                }else if(cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_STATUS))==DownloadManager.STATUS_PENDING){
                    listener.pending(downloadId);
                }else {
                    //已经下载文件大小
                    downloadBytes[0] = cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_BYTES_DOWNLOADED_SO_FAR));
                    //下载文件的总大小
                    downloadBytes[1] = cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_TOTAL_SIZE_BYTES));
                    listener.progress(downloadId,downloadBytes[0],downloadBytes[1]);
                }
            }
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }
}
