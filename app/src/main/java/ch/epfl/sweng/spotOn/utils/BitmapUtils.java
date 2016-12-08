package ch.epfl.sweng.spotOn.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Build;
import android.support.v4.content.FileProvider;
import android.util.Base64;
import android.util.Log;

import java.io.ByteArrayOutputStream;
import java.io.File;

import ch.epfl.sweng.spotOn.BuildConfig;

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

    /** creates the thumbnail from this object by reducing the resolution of the fullSizeImage
     */
    public static Bitmap createThumbnail(Bitmap fullSizeImage, int thumbnailSize){
        return ThumbnailUtils.extractThumbnail(fullSizeImage, thumbnailSize, thumbnailSize);
    }

    /**
     * Gets the uri from the file given depending on the API of the phone
     * @param context the context of the activity where this method is called
     * @param file the file for which we want the uri
     * @return the Uri of where is stored the file
     */
    public static Uri getUriFromFile(Context context, File file){
        Uri uriToReturn;
        if(Build.VERSION.SDK_INT <= 23) {
            uriToReturn = Uri.fromFile(file);
            Log.d("UriImageUpload", uriToReturn.toString());
        } else {
            //For API >= 24 (was the cause of the crash when opening the camera)
            uriToReturn = FileProvider.getUriForFile(context,
                    BuildConfig.APPLICATION_ID + ".provider", file);
            Log.d("UriImageUpload", uriToReturn.toString());
        }
        return uriToReturn;
    }

}
