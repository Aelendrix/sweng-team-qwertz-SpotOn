package ch.epfl.sweng.spotOn.test.util;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import ch.epfl.sweng.spotOn.media.PhotoObject;


/** Provides several static methods constructing premade PhotoObjects
 *  Created by quentin on 26.10.16.
 */

public class PhotoObjectUtils {

    public final static int NB_PO_AVAILABLE = 3;


// USEFUL METHODS ON PHOTOBJECTS

    /* compares the PhotoOBjects field by field for equality */
    public static boolean areEquals(PhotoObject p1, PhotoObject p2){
        return p1.getThumbnail().sameAs(p2.getThumbnail()) &&
                p1.getPictureId() == p2.getPictureId() &&
                p1.getAuthorId() == p2.getAuthorId() &&
                p1.getLatitude() == p2.getLatitude() &&
                p1.getLongitude() == p2.getLongitude() &&
                p1.getRadius() == p2.getRadius();
    }



// FACTORY METHODS TO INSTANCIATE PREMADE PHOTOBJECTS

    public static PhotoObject getRandomPhotoObject(){
        return getOnePo(new Random().nextInt(NB_PO_AVAILABLE));
    }

    public static List<PhotoObject> getAllPO(){
        List<PhotoObject> l = new ArrayList<>(NB_PO_AVAILABLE);
        int i = 0;
        while(i<NB_PO_AVAILABLE){
            l.add(getOnePo(i));
            i++;
        }
        return l;
    }

    public static PhotoObject getOnePo(int i){
        if(i>= NB_PO_AVAILABLE){
            throw new AssertionError();
        }
        switch(i){
            case 0 : return iceDivingPO();
            case 1 : return germaynDeryckePO();
            case 2 : return paulVanDykPO();
            default : throw new AssertionError();
        }
    }


// PHOTOOBJECTS INDIVIDUAL CONSTRUCTORS

    public static PhotoObject paulVanDykPO(){
        Bitmap image =  getBitmapFromURL("https://upload.wikimedia.org/wikipedia/commons/8/89/Paul_van_Dyk_DJing.jpg");
        return new PhotoObject(image, "paul", "cc", new Timestamp(1), 1, 1, 1);
    }

    public static PhotoObject germaynDeryckePO(){
        Bitmap image = getBitmapFromURL("https://upload.wikimedia.org/wikipedia/commons/thumb/7/74/Germain_Derycke_%281954%29.jpg/450px-Germain_Derycke_%281954%29.jpg");
        return new PhotoObject(image, "author1", "photo1", new Timestamp(1), 1, 1, 1);
    }

    public static PhotoObject iceDivingPO(){
        Bitmap image = getBitmapFromURL("https://upload.wikimedia.org/wikipedia/commons/4/4e/Ice_Diving_2.jpg");
        return new PhotoObject(image, "saruman", "icediving", new Timestamp(1), 1, 1, 1);
    }




// HELPERS

    /* Retrieves a bitmap file from the internet since it's the easiest way to get one consistently across multiple computers */
    public static Bitmap getBitmapFromURL(String src){
        try {
            URL url = new URL(src);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoInput(true);
            connection.connect();
            InputStream input = connection.getInputStream();
            Bitmap myBitmap = BitmapFactory.decodeStream(input);
            return myBitmap;
        } catch (IOException e) {
            throw new Error("Couldn't fetch image from the internet");
        }
    }

}
