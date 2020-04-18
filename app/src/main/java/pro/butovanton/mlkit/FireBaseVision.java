package pro.butovanton.mlkit;

import android.content.Context;
import android.net.Uri;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.ml.vision.FirebaseVision;
import com.google.firebase.ml.vision.common.FirebaseVisionImage;
import com.google.firebase.ml.vision.face.FirebaseVisionFace;
import com.google.firebase.ml.vision.face.FirebaseVisionFaceDetector;
import com.google.firebase.ml.vision.face.FirebaseVisionFaceDetectorOptions;

import java.io.IOException;
import java.util.List;

public class FireBaseVision {
    FirebaseVisionFaceDetectorOptions highAccuracyOpts;
    FirebaseVisionFaceDetector detector;
    MutableLiveData<List<FirebaseVisionFace>> firebaseVisionFace = new MutableLiveData<>();

    FireBaseVision() {
    highAccuracyOpts = setHighAccuracyOptsBuild();
    detector = FirebaseVision.getInstance()
                .getVisionFaceDetector(highAccuracyOpts);
    }

    private FirebaseVisionFaceDetectorOptions setHighAccuracyOptsBuild() {
        // High-accuracy landmark detection and face classification
        FirebaseVisionFaceDetectorOptions highAccuracyOpts =
                new FirebaseVisionFaceDetectorOptions.Builder()
                        .setPerformanceMode(FirebaseVisionFaceDetectorOptions.ACCURATE)
                        .setLandmarkMode(FirebaseVisionFaceDetectorOptions.ALL_LANDMARKS)
                        .setClassificationMode(FirebaseVisionFaceDetectorOptions.ALL_CLASSIFICATIONS)
                        .build();
    return highAccuracyOpts;
    }

    // Real-time contour detection of multiple faces
    FirebaseVisionFaceDetectorOptions realTimeOpts =
            new FirebaseVisionFaceDetectorOptions.Builder()
                    .setContourMode(FirebaseVisionFaceDetectorOptions.ALL_CONTOURS)
                    .build();

    public LiveData<List<FirebaseVisionFace>> detecting(FirebaseVisionImage image) {
        Task<List<FirebaseVisionFace>> result = detector.detectInImage(image)
                .addOnSuccessListener(
                        new OnSuccessListener<List<FirebaseVisionFace>>() {
                            @Override
                            public void onSuccess(List<FirebaseVisionFace> faces) {
                                firebaseVisionFace.setValue(faces);
                            }
                        })
                .addOnFailureListener(
                        new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                // Task failed with an exception
                                // ...
                            }
                        });
        return firebaseVisionFace;
    }

    public FirebaseVisionImage imageFromUri(Context context, Uri uri) {
        FirebaseVisionImage image = null;
        try {
            image = FirebaseVisionImage.fromFilePath(context,uri);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return image;
    }
}







