package ch.epfl.sweng.spotOn.gui;

import android.content.Context;

import android.os.Handler;
import android.os.Looper;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.maps.android.clustering.Cluster;
import com.google.maps.android.clustering.ClusterManager;
import com.google.maps.android.clustering.view.DefaultClusterRenderer;

/**
 * Created by Olivier on 31.10.2016.
 * Generate a cluster of Pin with custom condition of when it should render cluster and/or pins
 */
public class ClusterRenderer extends DefaultClusterRenderer<Pin> {

    boolean zoom = false;
    GoogleMap mMap;

    /**
     * Custom clusterRenderer
     * @param context the context of the call
     * @param map the map on which the clusterRenderer works
     * @param clusterManager the manager of all the clusters
     */
    public ClusterRenderer(Context context, GoogleMap map,
                             ClusterManager<Pin> clusterManager) {
        super(context, map, clusterManager);
        mMap = map;
    }

    /**
     *
     * {@inheritDoc}
     */
    @Override
    protected void onBeforeClusterItemRendered(Pin pin,
                                               MarkerOptions markerOptions) {

        markerOptions.title(pin.getTitle());
        BitmapDescriptor markerDescriptor = BitmapDescriptorFactory.defaultMarker(pin.getColor());
        markerOptions.icon(markerDescriptor);
        markerOptions.zIndex(pin.getZDepth());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected boolean shouldRenderAsCluster(Cluster cluster) {
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                computeZoom();
            }
        });
        return cluster.getSize() >=4  && !zoom;
    }

    private void computeZoom(){
        zoom = mMap.getMaxZoomLevel()-3 < mMap.getCameraPosition().zoom;
    }
}