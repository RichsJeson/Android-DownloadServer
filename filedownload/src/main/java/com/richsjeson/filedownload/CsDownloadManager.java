package com.richsjeson.filedownload;

import android.annotation.TargetApi;
import android.content.Context;
import android.database.ContentObserver;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.RequiresApi;

import com.richsjeson.filedownload.api.DownloadManager;
import com.richsjeson.filedownload.api.Downloads;
import com.richsjeson.filedownload.listener.DownloadDispatch;
import com.richsjeson.filedownload.listener.DownloadListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

//import android.app.DownloadManager;
//

/**
 * Created by richsjeson on 2017/11/26.
 */

public class CsDownloadManager implements  ICsDownloadManager {


    private DownloadManager mDownloadManager;


    private Map<Long,DownloadListener> dispatches;

    private Context mContext;

    private List<CsDownInfo> mDownInfos;


    private  final int CONTROL_RUN = 0;
    private  final int CONTROL_PAUSED = 1;
    private  final int STATUS_PENDING = 190;
    private  final int STATUS_RUNNING = 192;
    private  final String CONTENT_COLUMN_CONTROLL="control";
    private  final String CONTENT_COLUMN_STATUS="status";


    private  final String BUNDLE_KEY_TOTALBYTE="totalBytes";
    private  final String BUNDLE_KEY_CURRENTBYTE="currentByte";
    private  final String BUNDLE_KEY_STATUS="status";
    private  final String BUNDLE_KEY_ERRORMSG="errorMsg";
    private  final String BUNDLE_KEY_DOWNLOADID="downloadId";
    /**
     * 消息队列中的KEY
     */
    private final int HANDLE_KEY_STATUS_PROGRESS=7001;
    private final int HANDLE_KEY_STATUS_PAUSE=7002;
    private final int HANDLE_KEY_STATUS_PENDING=7003;
    private final int HANDLE_KEY_STATUS_COMPLETE=7004;
    private final int HANDLE_KEY_STATUS_FAILURE=7005;

    public CsDownloadManager(Context context){

        this.mContext=context;
    }

    /**
     * 执行下载处理
     * @param downloadPath
     * @param listener
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    @RequiresApi(api = Build.VERSION_CODES.GINGERBREAD)
    public void download(String downloadPath, DownloadListener listener){

        if(mDownloadManager == null){
//            mDownloadManager= (DownloadManager) mContext.getSystemService(Context.DOWNLOAD_SERVICE);
            mDownloadManager = new DownloadManager(mContext.getContentResolver(),
                    mContext.getPackageName());
            mContext.getContentResolver().registerContentObserver(Downloads.ALL_DOWNLOADS_CONTENT_URI,
                    true,new DownloadManagerObserver());
        }

        if(mDownInfos==null){

            mDownInfos=new ArrayList<CsDownInfo>();
        }

        if(dispatches==null){
            dispatches=new HashMap<>();
        }


//        if(canDownloadAttachment(mContext)){
            DownloadManager.Request request=new DownloadManager.Request(Uri.parse(downloadPath));
            request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI);
            request.setDestinationInExternalPublicDir("download","download12345666666777.pptx");
//            request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE);
            request.setMimeType("application/vnd.ms-powerpoint");
            long downloadId=mDownloadManager.enqueue(request);
            //加入队列时执行分发操作
            CsDownInfo info=new CsDownInfo();
            info.addTaskDownload(downloadId);
            mDownInfos.add(info);
            listener.addTask(downloadId);
            if(listener != null){
                dispatches.put(downloadId,listener);
            }
//        }else{
//            listener.trafficReminder();
//        }
    }

    //执行状态监听。
    private class DownloadManagerObserver extends ContentObserver

    {

        public DownloadManagerObserver() {
            super(downloadHandler);
        }


        @Override
        public boolean deliverSelfNotifications() {
            return super.deliverSelfNotifications();
        }

        @Override
        public void onChange(boolean selfChange) {

            if(mDownInfos != null){

                for(int i=0;i<mDownInfos.size();i++){

                    CsDownInfo info =mDownInfos.get(i);
                    info.update(downloadListener,mDownloadManager);
                }
            }

        }
    }

    /**
     *  从消息队列中取出数据
     */
    private Handler downloadHandler=new Handler(){


        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            Bundle bData=msg.getData();
            if(bData != null){
                DownloadListener dispatch= dispatches.get(bData.getLong(BUNDLE_KEY_DOWNLOADID));
                switch (bData.getInt(CONTENT_COLUMN_STATUS)){

                    case HANDLE_KEY_STATUS_PROGRESS:
                        if(dispatch != null){
                            dispatch.progress(bData.getLong(BUNDLE_KEY_DOWNLOADID),bData.getInt(BUNDLE_KEY_TOTALBYTE),bData.getInt(BUNDLE_KEY_CURRENTBYTE));
                        }
                        break;
                    case HANDLE_KEY_STATUS_COMPLETE:

                        if(dispatch != null){
                            dispatch.complete(bData.getLong(BUNDLE_KEY_DOWNLOADID));
                        }

                        break;
                    case HANDLE_KEY_STATUS_FAILURE:
                        if(dispatch != null){
                            dispatch.failure(bData.getLong(BUNDLE_KEY_DOWNLOADID),bData.getInt(BUNDLE_KEY_ERRORMSG));
//                            mDownloadManager.pauseDownload(bData.getLong(BUNDLE_KEY_DOWNLOADID));
                        }
                        break;
                    case HANDLE_KEY_STATUS_PAUSE:
                        if(dispatch != null){
                            dispatch.pause(bData.getLong(BUNDLE_KEY_DOWNLOADID));
                        }
                        break;
                    case HANDLE_KEY_STATUS_PENDING:
                        if(dispatch != null){
                            dispatch.pending(bData.getLong(BUNDLE_KEY_DOWNLOADID));
                        }
                        break;
                }
            }


        }

        @Override
        public void dispatchMessage(Message msg) {
            super.dispatchMessage(msg);
        }
    };

    private DownloadDispatch downloadListener =new DownloadDispatch() {

        @Override
        public void progress(long downloadId, int totalCount, int currentCount) {
            //此时加入消息队列
            Message message=Message.obtain();
            Bundle bundle=new Bundle();
            bundle.putInt(BUNDLE_KEY_CURRENTBYTE,currentCount);
            bundle.putInt(BUNDLE_KEY_TOTALBYTE,totalCount);
            bundle.putInt(BUNDLE_KEY_STATUS,HANDLE_KEY_STATUS_PROGRESS);
            bundle.putLong(BUNDLE_KEY_DOWNLOADID,downloadId);
            message.setData(bundle);
            downloadHandler.sendMessage(message);

        }

        @Override
        public void complete(long downloadId) {
            Message message=Message.obtain();
            Bundle bundle=new Bundle();
            bundle.putInt(BUNDLE_KEY_STATUS,HANDLE_KEY_STATUS_COMPLETE);
            bundle.putLong(BUNDLE_KEY_DOWNLOADID,downloadId);
            message.setData(bundle);
            downloadHandler.sendMessage(message);
        }

        @Override
        public void pause(long downloadId) {
            Message message=Message.obtain();
            Bundle bundle=new Bundle();
            bundle.putInt(BUNDLE_KEY_STATUS,HANDLE_KEY_STATUS_PAUSE);
            bundle.putLong(BUNDLE_KEY_DOWNLOADID,downloadId);
            message.setData(bundle);
            downloadHandler.sendMessage(message);
        }

        @Override
        public void failure(long downloadId, int  errorCode) {
            Message message=Message.obtain();
            Bundle bundle=new Bundle();
            bundle.putInt(BUNDLE_KEY_STATUS,HANDLE_KEY_STATUS_FAILURE);
            bundle.putLong(BUNDLE_KEY_DOWNLOADID,downloadId);
            bundle.putInt(BUNDLE_KEY_ERRORMSG,errorCode);
            message.setData(bundle);
            downloadHandler.sendMessage(message);

        }

        @Override
        public void pending(long downloadId) {
            Message message=Message.obtain();
            Bundle bundle=new Bundle();
            bundle.putInt(BUNDLE_KEY_STATUS,HANDLE_KEY_STATUS_PENDING);
            bundle.putLong(BUNDLE_KEY_DOWNLOADID,downloadId);;
            message.setData(bundle);
            downloadHandler.sendMessage(message);
        }
    };

    /**
     * 暂停下载
     * @param downloadId
     */
    public void pause(long downloadId){
        mDownloadManager.pauseDownload(downloadId);
    }

    /**
     * 继续下载,断点下载
     * @param downloadId
     */
    public void run(long downloadId){
        mDownloadManager.resumeDownload(downloadId);
    }

    public void remove(long downloadId){

        if(mDownloadManager != null){
            mDownloadManager.remove(downloadId);
        }
    }
    /**
     * @是否执行断点下载
     * @param context
     * @return
     */
//    public static boolean canDownloadAttachment(Context context) {
//        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(
//                Context.CONNECTIVITY_SERVICE);
//        NetworkInfo info = connectivityManager.getActiveNetworkInfo();
//        if (info == null) {
//            return false;
//        } else if (info.isConnected()) {
//            if (info.getType() != ConnectivityManager.TYPE_MOBILE) {
//                // not mobile network
//                return true;
//            } else {
//                // mobile network
//                Long maxBytes = DownloadManager.getMaxBytesOverMobile(context);
//                return maxBytes == null;
//            }
//        } else {
//            return false;
//        }
//    }
}
