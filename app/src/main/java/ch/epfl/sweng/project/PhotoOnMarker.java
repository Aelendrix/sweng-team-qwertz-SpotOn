package ch.epfl.sweng.project;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.Gravity;
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
public class    PhotoOnMarker implements GoogleMap.InfoWindowAdapter {

    private ImageView pictureView;

    public PhotoOnMarker(Context context, Bitmap picture){
        pictureView = new ImageView(context);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        pictureView.setLayoutParams(layoutParams);
        pictureView.setImageBitmap(picture);
    }

    @Override
    public View getInfoWindow(Marker marker){
        return pictureView;
    }

    //TODO: Create our own information window directly on google maps
    @Override
    public View getInfoContents(Marker marker){
        return null;
    }
}
