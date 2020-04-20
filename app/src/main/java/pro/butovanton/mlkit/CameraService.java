package pro.butovanton.mlkit;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Rect;
import android.graphics.SurfaceTexture;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCaptureSession;
import android.hardware.camera2.CameraDevice;
import android.hardware.camera2.CameraManager;
import android.hardware.camera2.CaptureRequest;
import android.os.Build;
import android.util.Log;
import android.view.Surface;
import android.view.TextureView;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.ml.vision.face.FirebaseVisionFace;

import java.util.Arrays;
import java.util.List;

public class CameraService {
    private String mCameraID;
    private CameraDevice mCameraDevice = null;
    private CameraCaptureSession mCaptureSession;
    private CameraManager mCameraManager;
    private TextureView mTextureView;
    private FireBaseVision fireBaseVision;
    private enum Process { WAIT , DETECTING }
    private Process process;
    private MutableLiveData<String> rect = new MutableLiveData<>();

    public CameraService(CameraManager cameraManager, String cameraID, TextureView textureVew) {
        mCameraManager = cameraManager;
        mCameraID = cameraID;
        mTextureView = textureVew;
        fireBaseVision = new FireBaseVision();
        process = Process.WAIT;
    }

    private CameraDevice.StateCallback mCameraCallback = new CameraDevice.StateCallback() {
        @Override
        public void onOpened(CameraDevice camera) {
            mCameraDevice = camera;
            Log.i("DEBUG", "Open camera  with id:"+mCameraDevice.getId());
            createCameraPreviewSession();
        }

        @Override
        public void onDisconnected(CameraDevice camera) {
            mCameraDevice.close();
            Log.i("DEBUG", "disconnect camera  with id:"+mCameraDevice.getId());
            mCameraDevice = null;
        }

        @Override
        public void onError(CameraDevice camera, int error) {
            Log.i("DEBUG", "error! camera id:"+camera.getId()+" error:"+error);
        }
    };


    private void createCameraPreviewSession() {
       if(mTextureView.isAvailable()) setSurface();
       mTextureView.setSurfaceTextureListener(new TextureView.SurfaceTextureListener() {
       @Override
       public void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height) {
           setSurface();
       }

       @Override
       public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) {
       }

       @Override
       public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
           return false;
       }

       @Override
       public void onSurfaceTextureUpdated(SurfaceTexture surface) {
           if (process == process.WAIT) {
               process = process.DETECTING;
               getFace(mTextureView.getBitmap());
           }
       }
   });
    }

    LiveData<String> getRect() {
        return rect;
    }

    void getFace(Bitmap bitmap) {
        fireBaseVision.detecting(bitmap).addOnSuccessListener(new OnSuccessListener<List<FirebaseVisionFace>>() {
            @Override
            public void onSuccess(List<FirebaseVisionFace> faces) {
                if (faces.size() == 1) {
                    rect.setValue(faces.get(0).getBoundingBox().toString());
                }
                else rect.setValue("");
                process = process.WAIT;
            }
        });
    }

      private void setSurface() {
            Surface surface = new Surface(mTextureView.getSurfaceTexture());
            try {
                final CaptureRequest.Builder builder =
                        mCameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW);
                builder.addTarget(surface);

                mCameraDevice.createCaptureSession(Arrays.asList(surface),
                        new CameraCaptureSession.StateCallback() {
                            @Override
                            public void onConfigured(CameraCaptureSession session) {
                                mCaptureSession = session;
                                try {
                                    mCaptureSession.setRepeatingRequest(builder.build(),null,null);
                                } catch (CameraAccessException e) {
                                    e.printStackTrace();
                                }
                            }

                            @Override
                            public void onConfigureFailed(CameraCaptureSession session) { }}, null );
            } catch (CameraAccessException e) {
                e.printStackTrace();
            }
        }


    public boolean isOpen() {
        if (mCameraDevice == null) {
            return false;
        } else {
            return true;
        }
    }

    public void openCamera(Activity activity) throws CameraAccessException {
           if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (activity.checkSelfPermission(Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
                    mCameraManager.openCamera(mCameraID,mCameraCallback,null);
                }
            } else mCameraManager.openCamera(mCameraID,mCameraCallback,null);
    }

    public void closeCamera() {
        if (mCameraDevice != null) {
            mCameraDevice.close();
            mCameraDevice = null;
        }
    }

}

