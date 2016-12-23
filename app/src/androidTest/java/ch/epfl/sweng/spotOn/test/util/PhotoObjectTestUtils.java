package ch.epfl.sweng.spotOn.test.util;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;

import ch.epfl.sweng.spotOn.utils.BitmapUtils;
import ch.epfl.sweng.spotOn.media.PhotoObject;
import ch.epfl.sweng.spotOn.media.PhotoObjectStoredInDatabase;


/** Provides several static methods constructing premade PhotoObjects
 *  Created by quentin on 26.10.16.
 *
 */

public class PhotoObjectTestUtils {

    public final static int NB_PO_AVAILABLE = 3;


// USEFUL METHODS ON PHOTOOBJECTS

    /* compares the PhotoObjects field by field for equality */
    public static boolean areEquals(PhotoObject p1, PhotoObject p2){
        boolean fullsizeImageOrLinkComparison = false;      // images have either a fullsizeimage or a link
        if(p1.hasFullSizeImage() && p2.hasFullSizeImage()){
            fullsizeImageOrLinkComparison = p1.getFullSizeImage().sameAs(p2.getFullSizeImage());
        }else{
            fullsizeImageOrLinkComparison = p1.getFullsizeImageLink().equals(p2.getFullsizeImageLink());
        }
        return fullsizeImageOrLinkComparison &&
                p1.getThumbnail().sameAs(p2.getThumbnail()) &&
                p1.getPictureId().equals(p2.getPictureId()) &&
                p1.getPhotoName().equals(p2.getPhotoName()) &&
                p1.getCreatedDate().equals(p2.getCreatedDate()) &&
                // expireDate can be mutated via votes
                p1.getLatitude() == p2.getLatitude() &&
                p1.getLongitude() == p2.getLongitude() &&
                // radius can be mutated via votes
                p1.getAuthorId().equals(p2.getAuthorId());
    }

    public static PhotoObjectStoredInDatabase convertToStoredInDatabase(PhotoObject po){
        return new PhotoObjectStoredInDatabase(po.getFullsizeImageLink(),
                BitmapUtils.encodeBitmapAsString(po.getThumbnail()), po.getPictureId(),
                po.getAuthorId(), po.getPhotoName(), po.getCreatedDate(), po.getExpireDate(),
                po.getLatitude(), po.getLongitude(), po.getUpvotes(), po.getDownvotes(),
                po.getReports(), po.getUpvotersList(), po.getDownvotersList(),
                po.getReportersList());
    }


// FACTORY METHODS TO INSTANTIATE PREMADE PHOTOOBJECTS

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
        return new PhotoObject(image, "Test", "cc", new Timestamp(new Date().getTime()), 0.52890355757777, 0.569420238493777);
    }

    public static PhotoObject germaynDeryckePO(){
        Bitmap image = getBitmapFromURL("https://upload.wikimedia.org/wikipedia/commons/thumb/7/74/Germain_Derycke_%281954%29.jpg/450px-Germain_Derycke_%281954%29.jpg");
        return new PhotoObject(image, "Test", "photo1", new Timestamp(new Date().getTime()), 0.52890355757888, 0.569420238493888);
    }

    public static PhotoObject iceDivingPO(){
        Bitmap image = getBitmapFromURL("https://upload.wikimedia.org/wikipedia/commons/4/4e/Ice_Diving_2.jpg");
        return new PhotoObject(image, "Test", "icediving", new Timestamp(new Date().getTime()), 0.52890355757999, 0.569420238493999);
    }

    public static PhotoObject veryOldTimestampPicture(){
        Bitmap image = getBitmapFromURL("https://upload.wikimedia.org/wikipedia/commons/4/4e/Ice_Diving_2.jpg");
        return new PhotoObject(image, "Test", "icediving", new Timestamp(0), 0.520013, 0.566721);

    }

    public static PhotoObject farAwayPicture(){
        Bitmap image = getBitmapFromURL("https://upload.wikimedia.org/wikipedia/commons/4/4e/Ice_Diving_2.jpg");
        return new PhotoObject(image, "Test", "doge", new Timestamp(new Date().getTime()), 0.53, 0.60);
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
            return BitmapFactory.decodeStream(input);
        } catch (IOException e) {
            throw new Error("Couldn't fetch image from the internet");
        }
    }

}
