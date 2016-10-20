package ch.epfl.sweng.project;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.TextView;

import java.io.File;
import java.util.Arrays;
import java.util.List;

public class SeePicturesActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_see_pictures);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

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
        TextView textView = (TextView) findViewById(R.id.ListOfFiles);
        textView.setText(textToDisplay);

    }

}
