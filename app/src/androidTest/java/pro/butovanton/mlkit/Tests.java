package pro.butovanton.mlkit;

import android.content.ContentResolver;
import android.content.Context;
import android.content.res.Resources;
import android.net.Uri;
import android.widget.ImageView;

import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.Test;
import org.junit.runner.RunWith;

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

    @Test
    public void getImageFace() {
        Resources resources = appContext.getResources();
        Uri uri = new Uri.Builder()
                .scheme(ContentResolver.SCHEME_ANDROID_RESOURCE)
                .authority(resources.getResourcePackageName(R.drawable.nord))
                .appendPath(resources.getResourceTypeName(R.drawable.nord))
                .appendPath(resources.getResourceEntryName(R.drawable.nord))
                .build();
        fireBaseVision.imageFromUri(appContext, uri);
    }
}
