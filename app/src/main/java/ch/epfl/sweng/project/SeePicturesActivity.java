package ch.epfl.sweng.project;

import android.content.Intent;
import android.os.Bundle;

import android.os.Handler;
import android.os.Looper;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.util.Arrays;
import java.util.List;

import static ch.epfl.sweng.project.FullsizeImageViewActivity.WANTED_IMAGE_PICTUREID;

public class SeePicturesActivity extends Fragment {

    /*
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        View view = inflater.inflate(R.layout.activity_see_pictures, container, false);

        //displays the files available
        String textToDisplay = "There are no files saved";
        File folder = new File("/storage/emulated/0/Pictures/SpotOn/Pictures");

        if(folder.listFiles() != null) {

            List<File> listOfFiles = Arrays.asList(folder.listFiles());
            if(!listOfFiles.isEmpty()) {
                textToDisplay = "The files saved are: \n";
                for (File file : listOfFiles) {
                    if (file.isFile()) {
                        textToDisplay += file.getName() + "\n";
                    }
                }
            }
        }

        TextView textView = (TextView) view.findViewById(R.id.ListOfFiles);
        textView.setText(textToDisplay);

        return view;

    }
    */

    View mView;
    GridView mGridView;
    private ImageAdapter mImageAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.activity_see_pictures, container, false);
        mGridView = (GridView) mView.findViewById(R.id.gridview);
        mImageAdapter = new ImageAdapter(mView.getContext());
        mGridView.setAdapter(mImageAdapter);

        mGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v,
                                    int position, long id) {
                displayFullsizeImage(position);
                Log.d("Grid","matching pictureId : " + mImageAdapter.getIdAtPosition(position));
            }
        });
        return mView;
    }
    //refresh the Grid when called
    public void refreshGrid(){
        if(mGridView!=null&&mView!=null){
                    //create a new adapter and refresh the gridView
                    mImageAdapter= new ImageAdapter(mView.getContext());
                    mGridView.invalidateViews();
                    mGridView.setAdapter(mImageAdapter);

        }

    }

    /**  launches the fullSizeImageViewActivity and displays the thumbnail that has been clicked (method called by a OnClickListener in the gridview
     */
    public void displayFullsizeImage(int positionOfThumbnail){
        Intent displayFullsizeImageIntent = new Intent(this.getActivity(), FullsizeImageViewActivity.class);
        displayFullsizeImageIntent.putExtra(WANTED_IMAGE_PICTUREID, mImageAdapter.getIdAtPosition(positionOfThumbnail));
        startActivity(displayFullsizeImageIntent);
    }


}
