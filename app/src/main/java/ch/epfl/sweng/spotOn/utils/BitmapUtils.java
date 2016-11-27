package ch.epfl.sweng.spotOn.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.support.v4.content.FileProvider;
import android.util.Base64;
import android.util.Log;

import java.io.ByteArrayOutputStream;
import java.io.File;

import ch.epfl.sweng.spotOn.BuildConfig;
import ch.epfl.sweng.spotOn.media.PhotoObjectStoredInDatabase;

/**
 * Created by quentin on 17.11.16.
 */

public class BitmapUtils {

    /** encodes the passed bitmap into a string
     */
    public static String encodeBitmapAsString(Bitmap img){
        ByteArrayOutputStream byteArrayOS = new ByteArrayOutputStream();
        img.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOS);
        return Base64.encodeToString(byteArrayOS.toByteArray(), Base64.DEFAULT);
    }

    /** creates the thumbail from this object by reducing the resolution of the fullSizeImage
     */
    public static Bitmap createThumbnail(Bitmap fullSizeImage, int thumbnailsize){
        return ThumbnailUtils.extractThumbnail(fullSizeImage, thumbnailsize, thumbnailsize);
    }

    /**
     * Creates the file in the internal storage where the image will be stored
     * @param context the context of the activity where this method is called
     * @return the Uri of where is stored the file
     */
    public static Uri createFileForBitmapAndGetUri(Context context){
        Uri uriToReturn;
        File temporalStorage = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),
                "/SpotOn/TEMP_PICTURE.jpg");
        if(Build.VERSION.SDK_INT <= 23) {
            uriToReturn = Uri.fromFile(temporalStorage);
            Log.d("UriImageUpload", uriToReturn.toString());
        } else {
            //For API >= 24 (was the cause of the crash)
            uriToReturn = FileProvider.getUriForFile(context,
                    BuildConfig.APPLICATION_ID + ".provider", temporalStorage);
            Log.d("UriImageUpload", uriToReturn.toString());
        }
        return uriToReturn;
    }

}
