package jp.coe.winklib;

import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.vision.CameraSource;

import jp.coe.winkfragment.WinkFragment;

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
        if(tone.isPlaying()) {
            tone.stop();
        } else {
            tone.play();
        }
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
}
