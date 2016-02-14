package jp.coe.winkfragment;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.support.v4.app.Fragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.vision.CameraSource;
import com.google.android.gms.vision.MultiProcessor;
import com.google.android.gms.vision.Tracker;
import com.google.android.gms.vision.face.Face;
import com.google.android.gms.vision.face.FaceDetector;

import java.io.IOException;
import java.util.Date;

import jp.coe.winkfragment.camera.CameraSourcePreview;


/**
 * WinkFragment class.
 * Activities that contain this fragment must implement the
 * {@link WinkFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link WinkFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class WinkFragment extends Fragment {

    private OnFragmentInteractionListener mListener;

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        /**
         * Eyes Close.
         */
        public void onClose();

        /**
         * Eyes long close.
         */
        public void onLongClose();

        /**
         * Left Eye close.
         */
        public void onLeftClose();

        /**
         * Right Eye close.
         */
        public void onRightClose();

    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     * @return A new instance of fragment WinkFragment.
     */
    public static WinkFragment newInstance(String param1, String param2) {
        WinkFragment fragment = new WinkFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    /**
     * new WinkFragment
     */
    public WinkFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_wink, container, false);
        mPreview = (CameraSourcePreview) view.findViewById(R.id.preview);

        return view;
    }

    @Override
    public void onStart() {

        super.onStart();
        // Check for the camera permission before accessing the camera.  If the
        // permission is not granted yet, request permission.
        int rc = ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.CAMERA);
        if (rc == PackageManager.PERMISSION_GRANTED) {
            createCameraSource();
        } else {
            requestCameraPermission();
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (OnFragmentInteractionListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onResume() {
        super.onResume();

        startCameraSource();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mCameraSource != null) {
            mCameraSource.release();
        }
    }


    private static final String TAG = "MainActivity";

    private CameraSource mCameraSource = null;

    private CameraSourcePreview mPreview;


    private static final int RC_HANDLE_GMS = 9001;
    private static final int RC_HANDLE_CAMERA_PERM = 2;

    private static final float THRESHOLD = 0.3f;



    /**
     * Factory for creating a face tracker to be associated with a new face.  The multiprocessor
     * uses this factory to create face trackers as needed -- one for each individual.
     */
    private class WinkFaceTrackerFactory implements MultiProcessor.Factory<Face> {
        @Override
        public Tracker<Face> create(Face face) {
            Log.d(TAG, "WinkFaceTrackerFactory instance");
            return new WinkFaceTracker(face);
        }
    }

    private boolean closeFlg = false;

    private Date firstRightEyeCloseDate = new Date(Long.MAX_VALUE);
    private Date firstLeftEyeCloseDate = new Date(Long.MAX_VALUE);

    private final int LONG_CLOSE_MILL = 2000;
    private final int SHORT_CLOSE_MILL = 500;


    /**
     * Face tracker for each detected individual. This maintains a face graphic within the app's
     * associated face overlay.
     */
    private class WinkFaceTracker extends Tracker<Face> {

        private Boolean mLongCloseFlg = false;

        private Boolean mLeftCloseFlg = false;
        private Boolean mRightCloseFlg = false;

        WinkFaceTracker(Face face) {
        }

        /**
         * Start tracking the detected face instance within the face overlay.
         */
        @Override
        public void onNewItem(int faceId, Face face) {
        }

        private boolean isLeftClose(Face face){
            return THRESHOLD > face.getIsLeftEyeOpenProbability() && face.getIsLeftEyeOpenProbability() > 0;
        }

        private boolean isRightClose(Face face){
            return THRESHOLD > face.getIsRightEyeOpenProbability() && face.getIsRightEyeOpenProbability() > 0;
        }

        private void reset(){

            firstLeftEyeCloseDate = new Date(Long.MAX_VALUE);
            firstRightEyeCloseDate = new Date(Long.MAX_VALUE);
        }

        /**
         * Update the position/characteristics of the face within the overlay.
         */
        @Override
        public void onUpdate(FaceDetector.Detections<Face> detectionResults, Face face) {

//            Log.d(TAG,""+face.getIsLeftEyeOpenProbability()+"face.getIsLeftEyeOpenProbability() ");
//            Log.d(TAG,""+face.getIsRightEyeOpenProbability()+ "face.getIsRightEyeOpenProbability() ");

            //どちらも正の値でなければリターン
            if(face.getIsLeftEyeOpenProbability() < 0 || face.getIsRightEyeOpenProbability() < 0) return;

            //初めて目を閉じた時間
            if(firstLeftEyeCloseDate.compareTo(new Date(Long.MAX_VALUE)) == 0 && isLeftClose(face)){
                firstLeftEyeCloseDate = new Date(System.currentTimeMillis());
            }

            if(firstRightEyeCloseDate.compareTo(new Date(Long.MAX_VALUE)) == 0 && isRightClose(face)){
                firstRightEyeCloseDate = new Date(System.currentTimeMillis());
            }

            //今両目を閉じているか
            if(isLeftClose(face) && isRightClose(face)) {
//                Log.d(TAG,"長く目を閉じるチェック");

                //長めに閉じているか
                final Date now = new Date(System.currentTimeMillis() - LONG_CLOSE_MILL);

                int diff = now.compareTo(firstLeftEyeCloseDate);
                int diff2 = now.compareTo(firstRightEyeCloseDate);

                if (diff > 0 && diff2 > 0) {
                    reset();
                    mListener.onLongClose();
                    mLongCloseFlg = true;
                    return;

                }

            }

            //短く目を閉じているか
            final Date now = new Date(System.currentTimeMillis() - SHORT_CLOSE_MILL);
            int diffLeft = now.compareTo(firstLeftEyeCloseDate);
            int diffRight = now.compareTo(firstRightEyeCloseDate);

            if (diffLeft > 0 && diffRight > 0) {
                if(!isRightClose(face) && !isLeftClose(face) ) {
                    if(mLongCloseFlg){
                        mLongCloseFlg = false;
                    } else {
                        mListener.onClose();
                    }
                    mLeftCloseFlg = false;
                    mRightCloseFlg = false;
                    reset();
                    return;
                }
            } else if (mLeftCloseFlg) {
                mListener.onLeftClose();
                mLeftCloseFlg = false;
                reset();
                return;
            } else if (mRightCloseFlg) {
                mRightCloseFlg = false;

                mListener.onRightClose();
                reset();
                return;
            }

            if (diffLeft > 0) {
                //大小比較
                if(
                        face.getIsLeftEyeOpenProbability() < face.getIsRightEyeOpenProbability()

                        ) {
                    mLeftCloseFlg = true;
                    return;
                }
            }

            if (diffRight > 0) {
                if(
                        face.getIsRightEyeOpenProbability() < face.getIsLeftEyeOpenProbability()
                        ) {
                    mRightCloseFlg = true;
                    return;
                }
            }

            if(!isLeftClose(face) && !isRightClose(face)) {
                reset();
            }



        }

        /**
         * Hide the graphic when the corresponding face was not detected.  This can happen for
         * intermediate frames temporarily (e.g., if the face was momentarily blocked from
         * view).
         */
        @Override
        public void onMissing(FaceDetector.Detections<Face> detectionResults) {
        }

        /**
         * Called when the face is assumed to be gone for good. Remove the graphic annotation from
         * the overlay.
         */
        @Override
        public void onDone() {
        }
    }

    /**
     * Handles the requesting of the camera permission.  This includes
     * showing a "Snackbar" message of why the permission is needed then
     * sending the request.
     */
    private void requestCameraPermission() {
        Log.w(TAG, "Camera permission is not granted. Requesting permission");
        final Activity thisActivity = getActivity();

        final String[] permissions = new String[]{Manifest.permission.CAMERA};

        if (!ActivityCompat.shouldShowRequestPermissionRationale(thisActivity,
                Manifest.permission.CAMERA)) {
//            Log.w(TAG, "not shouldShowRequestPermissionRationale");
            requestPermissions(permissions,RC_HANDLE_CAMERA_PERM);
            return;
        }

        //アラート
//        Log.w(TAG, "shouldShowRequestPermissionRationale");
        requestPermissions(permissions,RC_HANDLE_CAMERA_PERM);

    }

    /**
     * Creates and starts the camera.  Note that this uses a higher resolution in comparison
     * to other detection examples to enable the barcode detector to detect small barcodes
     * at long distances.
     */
    private void createCameraSource() {

        Context context = getActivity().getApplicationContext();
        FaceDetector detector = new FaceDetector.Builder(context)
                .setClassificationType(FaceDetector.ALL_CLASSIFICATIONS)
                .build();

        detector.setProcessor(
                new MultiProcessor.Builder<>(new WinkFaceTrackerFactory())
                        .build());

        if (!detector.isOperational()) {
            // Note: The first time that an app using face API is installed on a device, GMS will
            // download a native library to the device in order to do detection.  Usually this
            // completes before the app is run for the first time.  But if that download has not yet
            // completed, then the above call will not detect any faces.
            //
            // isOperational() can be used to check if the required native library is currently
            // available.  The detector will automatically become operational once the library
            // download completes on device.
            Log.w("TAG", "Face detector dependencies are not yet available.");
        }

        mCameraSource = new CameraSource.Builder(context, detector)
                .setRequestedPreviewSize(640, 480)
                .setFacing(CameraSource.CAMERA_FACING_FRONT)
                .setRequestedFps(30.0f)
                .build();
    }

    /**
     * Callback for the result from requesting permissions. This method
     * is invoked for every call on {@link #requestPermissions(String[], int)}.
     * <p>
     * <strong>Note:</strong> It is possible that the permissions request interaction
     * with the user is interrupted. In this case you will receive empty permissions
     * and results arrays which should be treated as a cancellation.
     * </p>
     *
     * @param requestCode  The request code passed in {@link #requestPermissions(String[], int)}.
     * @param permissions  The requested permissions. Never null.
     * @param grantResults The grant results for the corresponding permissions
     *                     which is either {@link PackageManager#PERMISSION_GRANTED}
     *                     or {@link PackageManager#PERMISSION_DENIED}. Never null.
     * @see #requestPermissions(String[], int)
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        Log.d(TAG,"onRequestPermissionsResult");
        switch(requestCode){
            case RC_HANDLE_CAMERA_PERM:
                if (grantResults.length != 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // we have permission, so create the camerasource
//                    Log.d(TAG,"createCameraSource");
                    createCameraSource();
                    return;
                } else {
                    getActivity().finish();
                }
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        }
    }

    /**
     * Starts or restarts the camera source, if it exists.  If the camera source doesn't exist yet
     * (e.g., because onResume was called before the camera source was created), this will be called
     * again when the camera source is created.
     */
    private void startCameraSource() {

        // check that the device has play services available.
        int code = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(
                getActivity().getApplicationContext());
        if (code != ConnectionResult.SUCCESS) {
            Dialog dlg =
                    GoogleApiAvailability.getInstance().getErrorDialog(getActivity(), code, RC_HANDLE_GMS);
            dlg.show();
        }

        //オーバーレイしないとWinkFaceTrackerが反応しない

        if (mCameraSource != null) {
            try {
                mPreview.start(mCameraSource);
            } catch (IOException e) {
                Log.e(TAG, "Unable to start camera source.", e);
                mCameraSource.release();
                mCameraSource = null;
            }
        }

    }

}
