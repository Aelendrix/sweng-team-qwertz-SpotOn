package ch.epfl.sweng.spotOn;

import android.graphics.Bitmap;
import android.media.ThumbnailUtils;
import android.util.Base64;

import java.io.ByteArrayOutputStream;

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

}
