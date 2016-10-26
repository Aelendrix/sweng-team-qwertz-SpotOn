package ch.epfl.sweng.spotOn.test;

import android.support.test.runner.AndroidJUnit4;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import org.junit.Test;
import org.junit.runner.RunWith;

import ch.epfl.sweng.spotOn.gui.ViewPagerAdapter;

/**
 * Created by Alexis Dewaele on 13/10/2016.
 *
 * JUnit test that tests if the ViewPagerAdapter class is correctly implemented
 */

/*
@RunWith(AndroidJUnit4.class)
public class ViewPagerAdapterTest extends AppCompatActivity {

    @Test
    public void getItem_Valid_Fragments_ReturnsGoodValues() {

        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(new Fragment(), "Frag 1");
        adapter.addFragment(new Fragment(), "Frag 2");
        adapter.addFragment(new Fragment(), "Frag 3");

        assert(adapter.getCount() == 3);
        assert(adapter.getPageTitle(0) == "Frag 1");
        assert(adapter.getPageTitle(1) == "Frag 2");
        assert(adapter.getPageTitle(2) == "Frag 3");

    }
}
*/