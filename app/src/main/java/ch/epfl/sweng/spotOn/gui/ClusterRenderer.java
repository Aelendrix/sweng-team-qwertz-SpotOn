package ch.epfl.sweng.spotOn.gui;

import android.content.Context;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.maps.android.clustering.Cluster;
import com.google.maps.android.clustering.ClusterManager;
import com.google.maps.android.clustering.view.DefaultClusterRenderer;

/**
 * Get the right colors on the pins (green or yellow) for the cluster manager
 * Created by olivi on 31.10.2016.
 */

public class ClusterRenderer extends DefaultClusterRenderer<Pin> {

    /*private Context mContext;
    private final IconGenerator mIconGenerator;
    private ShapeDrawable mColoredCircleBackground;
    private SparseArray<BitmapDescriptor> mIcons = new SparseArray<>();
    private final float mDensity;*/

    public ClusterRenderer(Context context, GoogleMap map,
                             ClusterManager<Pin> clusterManager) {
        super(context, map, clusterManager);
        /*mContext = context;
        mIconGenerator = new IconGenerator(context);
        mIconGenerator.setContentView(this.makeSquareTextView(context));
        mIconGenerator.setTextAppearance(
                com.google.maps.android.R.style.amu_ClusterIcon_TextAppearance);
        mIconGenerator.setBackground(this.makeClusterBackground());
        mColoredCircleBackground = new ShapeDrawable(new OvalShape());
        mDensity = context.getResources().getDisplayMetrics().density;*/

    }

    @Override
    protected void onBeforeClusterItemRendered(Pin pin,
                                               MarkerOptions markerOptions) {

        BitmapDescriptor markerDescriptor = BitmapDescriptorFactory.defaultMarker(pin.getColor());
        markerOptions.title(pin.getTitle());
        markerOptions.icon(markerDescriptor);
    }

    /*@Override
    protected void onBeforeClusterRendered(Cluster<Pin> cluster,
                                           MarkerOptions markerOptions) {
        int bucket = getBucket(cluster);
        BitmapDescriptor descriptor = this.mIcons.get(bucket);
        if(descriptor == null){
            descriptor = BitmapDescriptorFactory.fromBitmap(
                    mIconGenerator.makeIcon(getClusterText(bucket)));
            mIcons.put(bucket, descriptor);
        }
        markerOptions.icon(descriptor);
    }*/

    @Override
    protected boolean shouldRenderAsCluster(Cluster cluster) {
        return (cluster.getSize() >= 3);
    }

    /*private SquareTextView makeSquareTextView(Context context){
        SquareTextView squareTextView = new SquareTextView(context);
        ViewGroup.LayoutParams layoutParams = new ViewGroup.LayoutParams(-2, -2);
        squareTextView.setLayoutParams(layoutParams);
        squareTextView.setId(com.google.maps.android.R.id.amu_text);
        int dpi = (int)(12.0 * this.mDensity);
        squareTextView.setPadding(dpi, dpi, dpi, dpi);
        return squareTextView;
    }

    private LayerDrawable makeClusterBackground(){
        int clusterOutlineColor = ContextCompat.getColor(mContext, R.color.cardview_light_background);
        mColoredCircleBackground = new ShapeDrawable(new OvalShape());
        ShapeDrawable outline = new ShapeDrawable(new OvalShape());
        outline.getPaint().setColor(clusterOutlineColor);
        LayerDrawable background = new LayerDrawable(
                new Drawable[]{outline, mColoredCircleBackground});
        int strokeWidth = (int)(mDensity * 3.0);
        background.setLayerInset(1, strokeWidth, strokeWidth, strokeWidth, strokeWidth);
        return background;
    }*/
}