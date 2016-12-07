package ch.epfl.sweng.spotOn.gui;

import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.clustering.ClusterItem;

import ch.epfl.sweng.spotOn.media.PhotoObject;

/**
 * Created by olivi on 31.10.2016.
 */

public class Pin implements ClusterItem {

    private final LatLng mPosition;
    private final PhotoObject mPictureAssociated;
    private boolean mIsAccessible;
    private String mTitle;
    private float color;
    private float zDepth;

    public Pin(PhotoObject picture, boolean isAccessible) {
        mPosition = new LatLng(picture.getLatitude(), picture.getLongitude());
        mPictureAssociated = picture;
        this.mIsAccessible = isAccessible;
        //Title only useful for testing
        mTitle = picture.getPictureId();
        //Green Pin if it is accessible
        if(isAccessible) {
            color = BitmapDescriptorFactory.HUE_GREEN;
            zDepth = 30f;
        }
        //Yellow pin if not accessible
        else{
            color = BitmapDescriptorFactory.HUE_YELLOW;
            zDepth = 10f;
        }
    }

    @Override
    public LatLng getPosition() {
        return mPosition;
    }

    public PhotoObject getPhotoObject(){
        return mPictureAssociated;
    }

    public float getColor(){
        return color;
    }

    public float getZDepth() {
        return zDepth;
    }

    public boolean getAccessibility(){
        return mIsAccessible;
    }

    public String getTitle(){
        return mTitle;
    }

    public void setAccessibility(boolean newAccessibility){
        mIsAccessible = newAccessibility;
    }
}
