package ch.epfl.sweng.spotOn.gui;

import android.content.Context;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.maps.android.clustering.Cluster;
import com.google.maps.android.clustering.ClusterManager;
import com.google.maps.android.clustering.view.DefaultClusterRenderer;
import com.google.maps.android.ui.IconGenerator;

import static com.facebook.FacebookSdk.getApplicationContext;

/**
 * Get the right colors on the pins (green or yellow) for the cluster manager
 * Created by olivi on 31.10.2016.
 */

public class ClusterRenderer extends DefaultClusterRenderer<Pin> {

    public ClusterRenderer(Context context, GoogleMap map,
                             ClusterManager<Pin> clusterManager) {
        super(context, map, clusterManager);
    }

    @Override
    protected void onBeforeClusterItemRendered(Pin pin,
                                               MarkerOptions markerOptions) {

        BitmapDescriptor markerDescriptor = pin.getColor();
        markerOptions.icon(markerDescriptor);
    }

    @Override
    protected boolean shouldRenderAsCluster(Cluster cluster) {
        return (cluster.getSize() > 3);
    }
}