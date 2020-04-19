package pro.butovanton.mlkit;

import android.content.ContentResolver;
import android.content.Context;
import android.content.res.Resources;
import android.net.Uri;
import android.util.Log;
import android.widget.ImageView;

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;
import androidx.lifecycle.Observer;
import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.google.firebase.ml.vision.common.FirebaseVisionImage;
import com.google.firebase.ml.vision.face.FirebaseVisionFace;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static org.junit.Assert.*;

/**
 * Instrumented test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
public class Tests {

    private FireBaseVision fireBaseVision = new FireBaseVision();
    private Context appContext = InstrumentationRegistry.getInstrumentation().getTargetContext();

    @Rule
    public InstantTaskExecutorRule instantTaskExecutorRule = new InstantTaskExecutorRule();

    @Test
    public void getImageFace() throws InterruptedException {
        final CountDownLatch count = new CountDownLatch(1);
        Resources resources = appContext.getResources();
        Uri uri = new Uri.Builder()
                .scheme(ContentResolver.SCHEME_ANDROID_RESOURCE)
                .authority(resources.getResourcePackageName(R.drawable.yaht))
                .appendPath(resources.getResourceTypeName(R.drawable.yaht))
                .appendPath(resources.getResourceEntryName(R.drawable.yaht))
                .build();
        FirebaseVisionImage image = fireBaseVision.imageFromUri(appContext, uri);
        fireBaseVision.detecting(image).observeForever(new Observer<List<FirebaseVisionFace>>() {
            @Override
            public void onChanged(List<FirebaseVisionFace> firebaseVisionFaces) {
                if (firebaseVisionFaces == null) Log.d("DEBUG", "Faces not");
                else Log.d("DEBUG", "Faces: " + firebaseVisionFaces.size());
                count.countDown();
            }
        });
        count.await(3, TimeUnit.MINUTES);
    }

}
