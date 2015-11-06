package jp.coe.winklib;

import android.media.Ringtone;
import android.media.RingtoneManager;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import jp.coe.winkfragment.WinkFragment;

public class MainActivity extends AppCompatActivity implements WinkFragment.OnFragmentInteractionListener {

    private static final String TAG = "MainActivity";
    Ringtone mRingtoneClose;
    Ringtone mRingtoneLongClose;

    private Runnable mRunnable;
    private Handler mHandler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mRingtoneClose = RingtoneManager.getRingtone(getApplicationContext(), RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION));
        mRingtoneLongClose = RingtoneManager.getRingtone(getApplicationContext(), RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM));

    }

    @Override
    public void onClose() {
        if(mRingtoneClose.isPlaying()) {
            mRingtoneClose.stop();
        } else {
            mRingtoneClose.play();
        }

    }


    @Override
    public void onLongClose() {
        mRingtoneLongClose.play();
        mRunnable = new Runnable() {
            public void run() {
                Log.d(TAG, "mRingtoneLongClose stop");
                mRingtoneLongClose.stop();
            }
        };
        mHandler.postDelayed(mRunnable, 2000);

    }
}
