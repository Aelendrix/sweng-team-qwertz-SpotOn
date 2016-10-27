package ch.epfl.sweng.spotOn.gui;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Marker;

/**
 * Created by olivi on 20.10.2016.
 */

/**
 * Class that will create an information window when clicking on a marker on the map
 */
public class PhotoOnMarker implements GoogleMap.InfoWindowAdapter {

    private ImageView pictureView;

    public PhotoOnMarker(Context context){
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
        if(marker.getTag() != null){
            Bitmap associatedToMarker = (Bitmap) marker.getTag();
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
