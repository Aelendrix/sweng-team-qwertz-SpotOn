package ch.epfl.sweng.spotOn.test;

import android.support.test.runner.AndroidJUnit4;

import org.junit.Test;
import org.junit.runner.RunWith;

import ch.epfl.sweng.spotOn.gui.Pin;
import ch.epfl.sweng.spotOn.media.PhotoObject;
import ch.epfl.sweng.spotOn.test.util.PhotoObjectTestUtils;

/**
 * Created by olivi on 05.11.2016.
 */

@RunWith(AndroidJUnit4.class)
public class PinTest {

    private PhotoObject photo1 = PhotoObjectTestUtils.paulVanDykPO();
    private PhotoObject photo2 = PhotoObjectTestUtils.iceDivingPO();

    private Pin pin1 = new Pin(photo1, true);
    private Pin pin2 = new Pin(photo2, false);

    @Test
    public void equalPinsTest() throws Exception {
        Pin likePin1 = new Pin(photo1, true);
        if (!areEquals(pin1, likePin1)){
            throw new AssertionError("Pins should be the same");
        }
        if(areEquals(pin2, likePin1)){
            throw new AssertionError("Pins should not be the same");
        }
    }

    public static boolean areEquals(Pin onePin, Pin anotherPin){
        return onePin.getAccessibility() == anotherPin.getAccessibility() &&
                PhotoObjectTestUtils.areEquals(onePin.getPhotoObject(), anotherPin.getPhotoObject());
    }
}