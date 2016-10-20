package ch.epfl.sweng.project;

import android.os.Bundle;

import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.TextView;

import java.io.File;
import java.util.Arrays;
import java.util.List;

public class SeePicturesActivity extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        View view = inflater.inflate(R.layout.activity_see_pictures, container, false);
        /*Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);*/


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

}
