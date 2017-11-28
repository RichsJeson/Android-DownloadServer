package com.richsjeson.filedownload;

import com.richsjeson.filedownload.listener.DownloadListener;

/**
 * Created by richsjeson on 2017/11/27.
 */

public interface ICsDownloadManager {

    public void download(String downloadPath,DownloadListener listener);

    public void pause(long downloadId);

    public void run(long downloadId);

    public void remove(long downloadId);

}
