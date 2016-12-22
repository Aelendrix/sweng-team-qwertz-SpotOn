package ch.epfl.sweng.spotOn.gui;

import android.content.Context;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;

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

    private boolean isVeryZoomed = false;
    //private boolean isVeryUnZoomed = false;
    private GoogleMap mMap;

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

        BitmapDescriptor markerDescriptor = BitmapDescriptorFactory.defaultMarker(pin.getColor());
        markerOptions.icon(markerDescriptor).zIndex(pin.getZDepth());
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

        //return isVeryUnZoomed || cluster.getSize() >=4  && !isVeryZoomed;
        return cluster.getSize() >=4  && !isVeryZoomed;
    }

    private void computeZoom(){
        float zoomLevel = mMap.getCameraPosition().zoom;
        isVeryZoomed = mMap.getMaxZoomLevel()-3 < zoomLevel;
        //isVeryUnZoomed = 12.5 >= zoomLevel;
    }
}