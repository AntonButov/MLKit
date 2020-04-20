package pro.butovanton.mlkit;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import androidx.lifecycle.MutableLiveData;

import com.google.android.gms.tasks.Task;
import com.google.firebase.ml.vision.FirebaseVision;
import com.google.firebase.ml.vision.common.FirebaseVisionImage;
import com.google.firebase.ml.vision.face.FirebaseVisionFace;
import com.google.firebase.ml.vision.face.FirebaseVisionFaceDetector;
import com.google.firebase.ml.vision.face.FirebaseVisionFaceDetectorOptions;

import java.io.IOException;
import java.util.List;

public class FireBaseVision {
   private FirebaseVisionFaceDetectorOptions highAccuracyOpts;
   private FirebaseVisionFaceDetector detector;
   private MutableLiveData<List<FirebaseVisionFace>> firebaseVisionFace = new MutableLiveData<>();

   public FireBaseVision() {
    highAccuracyOpts = setHighAccuracyOptsBuild();
    detector = FirebaseVision.getInstance()
            .getVisionFaceDetector(highAccuracyOpts);
    }

    private FirebaseVisionFaceDetectorOptions setHighAccuracyOptsBuild() {
        // High-accuracy landmark detection and face classification
        FirebaseVisionFaceDetectorOptions highAccuracyOpts =
                new FirebaseVisionFaceDetectorOptions.Builder()
                        .setPerformanceMode(FirebaseVisionFaceDetectorOptions.FAST)
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

    public Task<List<FirebaseVisionFace>> detecting(FirebaseVisionImage image) {
        Task<List<FirebaseVisionFace>> result = detector.detectInImage(image);
        return result;
    }

    public Task<List<FirebaseVisionFace>> detecting(Bitmap bitmap) {
        FirebaseVisionImage image = FirebaseVisionImage.fromBitmap(bitmap);
        Task<List<FirebaseVisionFace>> result = detector.detectInImage(image);
        return result;
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







