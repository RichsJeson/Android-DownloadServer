package richsjeson.com.downloadmanagerlistener;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;

/**
 * Created by richsjeson on 2017/11/28.
 */

public class PermissionCheck {

    public static boolean readAndWriteExternalStorage(Context context){
        if(ActivityCompat.checkSelfPermission(context, "android.permission.WRITE_EXTERNAL_STORAG") != PackageManager.PERMISSION_GRANTED || ActivityCompat.checkSelfPermission(context, "android.permission.WRITE_EXTERNAL_STORAG") != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions((Activity) context, new String[]{"android.permission.WRITE_EXTERNAL_STORAG"}, 1);
            return false;
        }else{
            return true;
        }

    }

        //Just like this you can implement rest of the permissions.
}
