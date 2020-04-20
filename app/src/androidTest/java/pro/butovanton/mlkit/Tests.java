package pro.butovanton.mlkit;

import android.content.ContentResolver;
import android.content.Context;
import android.content.res.Resources;
import android.net.Uri;
import android.util.Log;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.arch.core.executor.testing.InstantTaskExecutorRule;
import androidx.lifecycle.Observer;
import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.ml.vision.common.FirebaseVisionImage;
import com.google.firebase.ml.vision.face.FirebaseVisionFace;
import com.google.firebase.ml.vision.objects.FirebaseVisionObject;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static org.junit.Assert.*;

@RunWith(AndroidJUnit4.class)
public class Tests {

    private Context appContext = InstrumentationRegistry.getInstrumentation().getTargetContext();

    @Rule
    public InstantTaskExecutorRule instantTaskExecutorRule = new InstantTaskExecutorRule();

    @Test
    public void nordTest() throws InterruptedException {
        testFaces(R.drawable.nord, 1);
    }

    @Test
    public void yahtTest() throws InterruptedException {
        testFaces(R.drawable.yaht, 0);
    }

    public void testFaces(int resurse, final int countFaces) throws InterruptedException {
        FireBaseVision fireBaseVision = new FireBaseVision();
        final CountDownLatch count = new CountDownLatch(1);
        Uri uri = uriFromResurce(resurse);
        FirebaseVisionImage image = fireBaseVision.imageFromUri(appContext, uri);
        fireBaseVision.detecting(image).addOnSuccessListener(new OnSuccessListener<List<FirebaseVisionFace>>() {
            @Override
            public void onSuccess(List<FirebaseVisionFace> firebaseVisionFaces) {
                assertTrue(firebaseVisionFaces.size() == countFaces);
                count.countDown();
            }
        });
        count.await(1, TimeUnit.MINUTES);
    }

    private Uri uriFromResurce(int resId) {
        Resources resources = appContext.getResources();
        return new Uri.Builder()
                    .scheme(ContentResolver.SCHEME_ANDROID_RESOURCE)
                    .authority(resources.getResourcePackageName(resId))
                    .appendPath(resources.getResourceTypeName(resId))
                    .appendPath(resources.getResourceEntryName(resId))
                    .build();
    }
}
