package richsjeson.com.downloadmanagerlistener;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.richsjeson.filedownload.CsDownloadManager;
import com.richsjeson.filedownload.ICsDownloadManager;
import com.richsjeson.filedownload.api.DownloadService;
import com.richsjeson.filedownload.listener.DownloadListener;

/**
 * Created by richsjeson on 2017/11/27.
 */

public class MainActivity extends Activity {


    private ICsDownloadManager mCsDownloadManager;
    private Button btn_cs;
    private Button btn_pause;
    private Button btn_goon;
    private long downloadId;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

//        if(PermissionCheck.readAndWriteExternalStorage(this)){
            //Your read write code.
            mCsDownloadManager=new CsDownloadManager(MainActivity.this.getApplicationContext());
            btn_cs=findViewById(R.id.btn_csss);
            btn_pause=findViewById(R.id.btn_pause);
            btn_goon=findViewById(R.id.btn_goon);

            startService();

            btn_cs.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    mCsDownloadManager.download("https://www.blend4web.com/blender/release/Blender2.79/blender-2.79-macOS-10.6.tar.gz", new DownloadListener() {
                        @Override
                        public void progress(long downloadId, int totalBytes, int currentBytes) {

                            Log.i(this.getClass().getName(),String.format("downloadId=%d,totalBytes:%d,currentBytes:%d",downloadId,totalBytes,currentBytes));
                        }

                        @Override
                        public void complete(long downloadId) {
                            Log.i(this.getClass().getName(),"complete");
                        }

                        @Override
                        public void failure(long downloadId, int errorCode) {
                            Log.i(this.getClass().getName(),"failure errorCode:"+errorCode);
                        }

                        @Override
                        public void pending(long downloadId) {
                            Log.i(this.getClass().getName(),"pending");
                        }

                        @Override
                        public void pause(long downloadId) {
                            Log.i(this.getClass().getName(),"pause");
                        }

                        @Override
                        public void trafficReminder() {

                        }

                        @Override
                        public void addTask(long downloadId) {

                            MainActivity.this.downloadId=downloadId;
                        }
                    });
                }
            });

            btn_pause.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mCsDownloadManager.pause(downloadId);
                }
            });

            btn_goon.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mCsDownloadManager.run(downloadId);
                }
            });
//        }

    }

//
    private void startService() {
        Intent intent = new Intent();
        intent.setClass(this, DownloadService.class);
        startService(intent);
    }
}
