package ch.epfl.sweng.spotOn.test;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.test.runner.AndroidJUnit4;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.sql.Timestamp;
import java.util.NoSuchElementException;

import ch.epfl.sweng.spotOn.media.PhotoObject;
import ch.epfl.sweng.spotOn.media.PhotoObjectStoredInDatabase;

/** test the database behavious with
 *  @author quentin
 */
@RunWith(AndroidJUnit4.class)
public class DatabaseIOTest {

    // a special directory in the database to avoid interfering with real data
    private final String PATH_TO_TEST_MEDIA_DIRECTORY = "MediaDirectory_Tests";


    /* Retrieves a bitmap file from the internet since it's the easiest way to get one consistently across multiple computers */
    private static Bitmap getBitmapFromURL(String src) throws Exception {
        try {
            URL url = new URL(src);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoInput(true);
            connection.connect();
            InputStream input = connection.getInputStream();
            Bitmap myBitmap = BitmapFactory.decodeStream(input);
            return myBitmap;
        } catch (IOException e) {
            throw new Exception("Couldn't fetch image from the internet");
        }
    }

    /* compares the PhotoOBjects for equality */
    private boolean areEquals(PhotoObject p1, PhotoObject p2){
        return p1.getThumbnail().sameAs(p2.getThumbnail()) &&
                p1.getPictureId() == p2.getPictureId() &&
                p1.getAuthorId() == p2.getAuthorId() &&
                p1.getLatitude() == p2.getLatitude() &&
                p1.getLongitude() == p2.getLongitude() &&
                p1.getRadius() == p2.getRadius();
    }

    @Test
    public void objectIsSendAndReceivedCorrectly() throws Exception {
        DatabaseReference DBref = FirebaseDatabase.getInstance().getReference(PATH_TO_TEST_MEDIA_DIRECTORY);
        final PhotoObject testOBject1 = initTestOBject1(DBref);
        final String testObjectId = testOBject1.getPictureId();

        testOBject1.upload();

        DBref.child(testObjectId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(!dataSnapshot.exists()){
                    throw new NoSuchElementException("datasnapshot doesn't exist");
                }
                PhotoObject receivedPhotoOBject = dataSnapshot.getValue(PhotoObjectStoredInDatabase.class).convertToPhotoObject();
                if(!areEquals(receivedPhotoOBject, testOBject1)){
                    throw new AssertionError("the send and received objects are different \n"
                            +testOBject1.toString()+"\n"+receivedPhotoOBject.toString());
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                throw new Error();
            }
        });
    }

    @Test
    public void createdObjectReturnGoodGetter() throws Exception{
        String imageLink = "bad_link";
        String pictureId = "key1";
        String author = "author1";
        String photoName = "name1";
        long createdDate = 1;
        long expireDate = 1000;
        double latitude = 4.4;
        double longitude =  6.6;
        int radius = 99;
        PhotoObject photo1 = new PhotoObject(imageLink, null, "key1", author, photoName, createdDate,
        expireDate, latitude, longitude, radius);
        if(photo1.getLongitude() != longitude) {
            throw new AssertionError("longitude wrongly get");
        }
        if(photo1.getLatitude() != latitude) {
            throw new AssertionError("latitude wrongly get");
        }
        if(photo1.getCreatedDate().getTime() != createdDate) {
            throw new AssertionError("created date wrongly get");
        }
        if(photo1.getExpireDate().getTime() != expireDate) {
            throw new AssertionError("expire date wrongly get");
        }
        if(photo1.getRadius() != radius) {
            throw new AssertionError("radius wrongly get");
        }
        if(!photo1.getAuthorId().equals(author)) {
            throw new AssertionError("author wrongly get");
        }
        if(!photo1.getPhotoName().equals(photoName)) {
            throw new AssertionError("photo name wrongly get");
        }
        if(!photo1.getFullsizeImageLink().equals(imageLink)){
            throw new AssertionError("image link wrongly get");
        }
        if(!photo1.getPictureId().equals(pictureId)){
            throw new AssertionError("image id wrongly get");
        }
    }

// CONSTRUCTORS FOR A VARIETY OF PhotoObjects

    private PhotoObject initTestOBject1(DatabaseReference dbref) throws Exception {
        Bitmap image = null;
        try {
            image = getBitmapFromURL("https://upload.wikimedia.org/wikipedia/commons/8/89/Paul_van_Dyk_DJing.jpg");
        } catch (Exception e) {
            throw new Exception("Problem instanciating PhotoOBject : image not retrieved from the interned");
        }
        String newPictureId = dbref.push().getKey();
        return new PhotoObject(image, "author1", "photo1", new Timestamp(1), 1, 1, 1);
    }

    private PhotoObject initTestOBject1_withWrongImage(DatabaseReference dbref) throws Exception {
        Bitmap image = null;
        try {
            image = getBitmapFromURL("https://upload.wikimedia.org/wikipedia/commons/thumb/7/74/Germain_Derycke_%281954%29.jpg/450px-Germain_Derycke_%281954%29.jpg");
        } catch (Exception e) {
            throw new Exception("Problem instanciating PhotoOBject : image not retrieved from the interned");
        }
        String newPictureId = dbref.push().getKey();
        return new PhotoObject(image, "author1", "photo1", new Timestamp(1), 1, 1, 1);
    }

}
