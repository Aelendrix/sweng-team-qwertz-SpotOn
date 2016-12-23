package ch.epfl.sweng.spotOn.test;


import org.junit.Test;
import ch.epfl.sweng.spotOn.utils.ServicesChecker;


public class NoServicesCheckerTests {

    @Test(expected = IllegalStateException.class)
    public void testNoServicesChecker(){
        ServicesChecker.initialize(null, null, null, null);
    }

    @Test(expected = IllegalStateException.class)
    public void testNoInstance(){
        ServicesChecker.getInstance();
    }
}
