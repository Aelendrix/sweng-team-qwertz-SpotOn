package ch.epfl.sweng.spotOn.gui;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.Marker;

/**
 * Created by olivi on 20.10.2016.
 */

/**
 * Class that will create an information window when clicking on a marker of the cluster manager on the map
 */
public class PhotoOnMarker implements GoogleMap.InfoWindowAdapter {

    private ImageView pictureView;
    private Pin mPin;

    public PhotoOnMarker(Context context, Pin pin){
        mPin = pin;
        pictureView = new ImageView(context);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        pictureView.setLayoutParams(layoutParams);
    }

    /**
     * Display an image view of the thumbnail associated to the marker
     * @param marker the marker the user clicked on
     * @return the thumbnail associated to the marker as an image view
     */
    @Override
    public View getInfoWindow(Marker marker){
        //the only marker with a title is the marker position
        if(mPin != null && mPin.getAccessibility() && marker.getTitle()==null){
            Bitmap associatedToMarker = mPin.getPhotoObject().getThumbnail();
            pictureView.setImageBitmap(associatedToMarker);
            return pictureView;
        } else {
            return null;
        }
    }

    //TODO: Create our own information window directly on google maps ?
    @Override
    public View getInfoContents(Marker marker){
        return null;
    }
}
