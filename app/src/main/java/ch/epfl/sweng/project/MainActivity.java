package ch.epfl.sweng.project;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.widget.Toast;

//import android.support.design.widget.TabLayout;


/**
 * Your app's main activity.
 */
public final class MainActivity extends AppCompatActivity {

    /*
    private Toolbar toolbar;
    private TabLayout tabLayout;
    private ViewPager viewPager;
    */
    //gps permission tag
    private final int REQUEST_FINE_LOCALISATION = 9;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
/*
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        viewPager = (ViewPager) findViewById(R.id.viewpager);
        setupViewPager(viewPager);

        tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);
        */
    }

    @Override
    protected void onStart() {
        super.onStart();
        //check every time the MainActivity is started if we have the permission: ACCESS_FINE_LOCATION
        // and throw the user input in onRequestPermissionsResult
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Log.d("MainActivity","No Permission");
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_FINE_LOCALISATION);
        }

    }

    public void goToPictureActivity(View view){
        //launch the PictureActivity
        Intent pictureIntent = new Intent(this, PictureActivity.class);
        startActivity(pictureIntent);
    }

    private void setupViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(new MyStoriesFragment(), "My Stories");
        adapter.addFragment(new CameraFragment(), "Camera");
        adapter.addFragment(new StoriesAroundMeFragment(), "Stories around me");
        viewPager.setAdapter(adapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.options, menu);
        return true;
    }

    public void goToMapsActivity(View view){
        //launch the map Activity
        //TODO: migrate the MapsActivity inside the fragment manager of MainActivity
        Intent mapIntent = new Intent(this, MapsActivity.class);
        startActivity(mapIntent);
    }

    //read the result of the permission request, leave the app if we don't have the gps permission
    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case REQUEST_FINE_LOCALISATION: {
                // If request is cancelled, the result arrays are empty.
                // permission was granted
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    //TODO: When the PictureActivity and MapsActivity will be fragment inside MainActivity, create the LocationManager here

                }
                //permission denied
                else {
                    Toast.makeText(this, getString(R.string.gps_not_permitted), Toast.LENGTH_LONG).show();

                    // leave the app
                    Intent intent = new Intent(Intent.ACTION_MAIN);
                    intent.addCategory(Intent.CATEGORY_HOME);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                }
            }
        }
    }
}