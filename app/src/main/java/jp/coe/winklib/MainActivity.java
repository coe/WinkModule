package jp.coe.winklib;

import android.Manifest;
import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.media.MediaScannerConnection;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

//import com.afollestad.materialdialogs.DialogAction;
//import com.afollestad.materialdialogs.MaterialDialog;
//import com.afollestad.materialdialogs.Theme;
import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.vision.CameraSource;

import java.io.File;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import jp.coe.winkfragment.WinkFragment;
//import permissions.dispatcher.NeedsPermission;
//import permissions.dispatcher.OnNeverAskAgain;
//import permissions.dispatcher.OnPermissionDenied;
//import permissions.dispatcher.OnShowRationale;
//import permissions.dispatcher.PermissionRequest;
//import permissions.dispatcher.PermissionUtils;
//import permissions.dispatcher.RuntimePermissions;

//@RuntimePermissions
public class MainActivity extends AppCompatActivity implements WinkFragment.OnFragmentInteractionListener {

    private static final String TAG = "MainActivity";
    Ringtone mRingtoneClose;
    Ringtone mRingtoneLeftClose;
    Ringtone mRingtoneRightClose;
    Ringtone mRingtoneLongClose;

    private Runnable mRunnable;
    private Handler mHandler = new Handler();

    public CameraSource mCameraSource;
    WinkFragment mWinkFragment;
    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mRingtoneClose = RingtoneManager.getRingtone(getApplicationContext(), RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION));
        mRingtoneLongClose = RingtoneManager.getRingtone(getApplicationContext(), RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM));
        mRingtoneLeftClose =
                RingtoneManager.getRingtone(getApplicationContext(), RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE));
        mRingtoneRightClose =
                RingtoneManager.getRingtone(getApplicationContext(), RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE));
        mWinkFragment = (WinkFragment) this.getSupportFragmentManager().findFragmentById(R.id.wink_fragment);


        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
    }

    @Override
    public void onClose() {
        Log.d(TAG, "onClose");
        execTone(mRingtoneClose);
//        mWinkFragment.takePicture(new CameraSource.ShutterCallback() {
//            @Override
//            public void onShutter() {
//                Log.d(TAG, "onShutter");
//            }
//        }, mPicJpgListener);
    }

//    @NeedsPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
    public void savePhoto(byte[] bytes){
        if (bytes == null) {
            return;
        }

        String saveDir = Environment.getExternalStorageDirectory().getPath() + "/test";

        // SD カードフォルダを取得
        File file = new File(saveDir);

        // フォルダ作成
        if (!file.exists()) {
            if (!file.mkdir()) {
                Log.e("Debug", "Make Dir Error");
            }
        }

        // 画像保存パス
        Calendar cal = Calendar.getInstance();
        SimpleDateFormat sf = new SimpleDateFormat("yyyyMMdd_HHmmss");
        String imgPath = saveDir + "/" + sf.format(cal.getTime()) + ".jpg";
        Log.d("Debug","imgPath "+imgPath);

        // ファイル保存
        FileOutputStream fos;
        try {
            fos = new FileOutputStream(imgPath, true);
            fos.write(bytes);
            fos.close();

            MediaScannerConnection.scanFile(this, new String[]{imgPath}, null, new MediaScannerConnection.MediaScannerConnectionClient() {
                @Override
                public void onMediaScannerConnected() {
                    Log.d("Debug", "onMediaScannerConnected ");
                }

                @Override
                public void onScanCompleted(String path, Uri uri) {
                    Log.d("Debug", "onScanCompleted " + path);
                }
            });

        } catch (Exception e) {
            Log.e("Debug", e.getMessage());
        }

        fos = null;
    }



//    /**
//     * JPEG データ生成完了時のコールバック
//     */
//    private CameraSource.PictureCallback mPicJpgListener = new CameraSource.PictureCallback() {
//        public void onPictureTaken(byte[] bytes) {
//            MainActivityPermissionsDispatcher.savePhotoWithCheck(MainActivity.this,bytes);
//        }
//    };

    /**
     * アンドロイドのデータベースへ画像のパスを登録
     * @param path 登録するパス
     */
    private void registAndroidDB(String path) {

//        ContentValues values = new ContentValues();
//        ContentResolver contentResolver = getContentResolver();
//        values.put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg");
//        values.put(MediaStore.Images.Media.TITLE, "test");
//        values.put("_data", path);
//        contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
        MediaScannerConnection.scanFile(this, new String[]{path}, null, new MediaScannerConnection.MediaScannerConnectionClient() {
            @Override
            public void onMediaScannerConnected() {
                Log.d("Debug","onMediaScannerConnected ");
            }

            @Override
            public void onScanCompleted(String path, Uri uri) {
                Log.d("Debug","onScanCompleted "+ path);
            }
        });
    }


    @Override
    public void onLongClose() {
        Log.d(TAG, "onLongClose");
        mRingtoneLongClose.play();
        mRunnable = new Runnable() {
            public void run() {
                Log.d(TAG, "mRingtoneLongClose stop");
                mRingtoneLongClose.stop();
            }
        };
        mHandler.postDelayed(mRunnable, 2000);

    }

    @Override
    public void onLeftClose() {
        Log.d(TAG, "onLeftClose");
        execTone(mRingtoneLeftClose);
    }

    @Override
    public void onRightClose() {
        Log.d(TAG, "onRightClose");
        execTone(mRingtoneRightClose);
    }


    private void execTone(Ringtone tone) {
//        if(tone.isPlaying()) {
//            tone.stop();
//        } else {
//            tone.play();
//        }
    }

    @Override
    public void onStart() {
        super.onStart();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client.connect();
        Action viewAction = Action.newAction(
                Action.TYPE_VIEW, // TODO: choose an action type.
                "Main Page", // TODO: Define a title for the content shown.
                // TODO: If you have web page content that matches this app activity's content,
                // make sure this auto-generated web page URL is correct.
                // Otherwise, set the URL to null.
                Uri.parse("http://host/path"),
                // TODO: Make sure this auto-generated app deep link URI is correct.
                Uri.parse("android-app://jp.coe.winklib/http/host/path")
        );
        AppIndex.AppIndexApi.start(client, viewAction);
    }

    @Override
    public void onStop() {
        super.onStop();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        Action viewAction = Action.newAction(
                Action.TYPE_VIEW, // TODO: choose an action type.
                "Main Page", // TODO: Define a title for the content shown.
                // TODO: If you have web page content that matches this app activity's content,
                // make sure this auto-generated web page URL is correct.
                // Otherwise, set the URL to null.
                Uri.parse("http://host/path"),
                // TODO: Make sure this auto-generated app deep link URI is correct.
                Uri.parse("android-app://jp.coe.winklib/http/host/path")
        );
        AppIndex.AppIndexApi.end(client, viewAction);
        client.disconnect();
    }

    //TODO:activityでonRequestPermissionsResultを実装するとWinkFragmentのonRequestPermissionsResultがこない
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        Log.w(TAG, "onRequestPermissionsResult");
        mWinkFragment.onRequestPermissionsResultForFragment(requestCode,permissions,grantResults);
//        MainActivityPermissionsDispatcher.onRequestPermissionsResult(this, requestCode, grantResults);
    }
//
//    @SuppressWarnings("unused")
//    @OnPermissionDenied(Manifest.permission.WRITE_EXTERNAL_STORAGE)
//    void deniedPermission() {
//        Log.w(TAG, "deniedPermission");
//        if (PermissionUtils.shouldShowRequestPermissionRationale(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
//            Toast.makeText(this, "電話をかけるのに失敗しました。", Toast.LENGTH_SHORT).show();
//        } else {
//            Toast.makeText(this, "あうあうあ。", Toast.LENGTH_SHORT).show();
//        }
//    }
//
//    @SuppressWarnings("unused")
//    @OnShowRationale(Manifest.permission.WRITE_EXTERNAL_STORAGE)
//    void showRationaleForCamera(final PermissionRequest request) {
//        Log.w(TAG, "showRationaleForCamera");
//        new AlertDialog.Builder(this)
//                .setMessage("")
//                .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
//                        request.cancel();
//                    }
//                })
//                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
//                        request.proceed();
//                    }
//                })
//                .show();
//    }
//
//    @OnNeverAskAgain(Manifest.permission.CAMERA)
//    void showNeverAskForCamera() {
//        Log.w(TAG, "showNeverAskForCamera");
//        Toast.makeText(this, android.R.string.VideoView_error_text_invalid_progressive_playback, Toast.LENGTH_SHORT).show();
//    }

}
