package pro.butovanton.mlkit;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Rect;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.TextureView;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.ml.vision.FirebaseVision;
import com.google.firebase.ml.vision.face.FirebaseVisionFace;
import com.google.firebase.ml.vision.face.FirebaseVisionFaceDetector;
import com.google.firebase.ml.vision.face.FirebaseVisionFaceDetectorOptions;
import com.google.firebase.ml.vision.objects.FirebaseVisionObject;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    private final int MY_REQUEST_CODE_FOR_CAMERA = 110;

    CameraService[] myCameras = null;
    int id;

    private CameraManager mCameraManager = null;
    private final int CAMERA1 = 0;
    private final int CAMERA2 = 1;
    private TextureView mTextureView = null;
    private TextView textView;
    private Button button;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mTextureView = findViewById(R.id.textureView);

        mCameraManager = (CameraManager) getSystemService(Context.CAMERA_SERVICE);
        try {
            // Получение списка камер с устройства
            myCameras = new CameraService[mCameraManager.getCameraIdList().length];
            for (String cameraID : mCameraManager.getCameraIdList()) {
                Log.i("DEBUG", "cameraID: " + cameraID);
                id = Integer.parseInt(cameraID);
                // создаем обработчик для камеры
            }
            myCameras[CAMERA1] = new CameraService(mCameraManager, "0", mTextureView);

            final FireBaseVision fireBaseVision = new FireBaseVision();
            textView = findViewById(R.id.textView);
           button = findViewById(R.id.button);
           button.setOnClickListener(new View.OnClickListener() {
               @Override
               public void onClick(View v) {
                   button.setEnabled(false);
                   textView.setText("");
                   //                 fireBaseVision.detecting(mTextureView.getBitmap()).addOnCompleteListener(new OnCompleteListener<List<FirebaseVisionFace>>() {
                   //                     @Override
                   //                     public void onComplete(@NonNull Task<List<FirebaseVisionFace>> task) {
                   //                         List<FirebaseVisionFace> result = task.getResult();
                   //                         for (FirebaseVisionFace face : result)
                   //                         textView.setText(textView.getText() + "Result: " + face.getTrackingId() + " ");
                   ///                         button.setEnabled(true);
                   //                     }
                   //                 });
                   //             }
                   //         });

                   fireBaseVision.objectDetecting(mTextureView.getBitmap()).addOnSuccessListener(
                           new OnSuccessListener<List<FirebaseVisionObject>>() {
                               @Override
                               public void onSuccess(List<FirebaseVisionObject> detectedObjects) {
                                   // The list of detected objects contains one item if multiple object detection wasn't enabled.
                                   for (FirebaseVisionObject obj : detectedObjects) {
                                       String str = "";
                                       // Integer id = obj.getTrackingId(); null in SINGLE_IMAGE_MODE
                                       Rect bounds = obj.getBoundingBox();
                                       str = str + "Bounds- " + bounds + "\n" + obj.getClassificationCategory();

                                           textView.setText(str + "\n");
                                       }
                               }
                           });
                          button.setEnabled(true);
               }
           });


        } catch (CameraAccessException e) {
            Log.e("DEBUG", e.getMessage());
            e.printStackTrace();
        }

    }

    @Override
    public void onResume() {
        super.onResume();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{Manifest.permission.CAMERA}, MY_REQUEST_CODE_FOR_CAMERA);
            }
        }
        openCamera();
    }

        @Override
        public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
            if (requestCode == MY_REQUEST_CODE_FOR_CAMERA) {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // startCameraActivity(); // запускаем активность с камерой (ну или фрагмент)
                    openCamera();
                } else {
                    Toast toast = Toast.makeText(getApplicationContext(), "no permition camera", Toast.LENGTH_SHORT);
                    toast.show();
                }
            }
        }


    private void openCamera() {
        myCameras[CAMERA1].openCamera(this);
    }

    @Override
    public void onPause() {
        super.onPause();
        if(myCameras[CAMERA1].isOpen()){myCameras[CAMERA1].closeCamera();}
    }


}