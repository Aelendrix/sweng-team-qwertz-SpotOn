package ch.epfl.sweng.spotOn.gui;

import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.clustering.ClusterItem;

import ch.epfl.sweng.spotOn.media.PhotoObject;

/**
 * Created by olivi on 31.10.2016.
 */

public class Pin implements ClusterItem {

    private final LatLng mPosition;
    private final PhotoObject mPictureAssociated;
    private boolean isAccessible;
    private BitmapDescriptor color;

    public Pin(PhotoObject picture, BitmapDescriptor color, boolean isAccessible){
        mPosition = new LatLng(picture.getLatitude(), picture.getLongitude());
        mPictureAssociated = picture;
        this.isAccessible = isAccessible;
        this.color = color;
    }

    @Override
    public LatLng getPosition() {
        return mPosition;
    }

    public PhotoObject getPhotoObject(){
        return mPictureAssociated;
    }

    public BitmapDescriptor getColor(){
        return color;
    }

    public boolean getAccessibility(){
        return isAccessible;
    }

    public void setAccessibility(boolean newAccessibility){
        isAccessible = newAccessibility;
    }
}
