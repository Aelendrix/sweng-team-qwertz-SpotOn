// commented - todo - reenable
//
// package ch.epfl.sweng.spotOn.test.user;
//
//import android.support.test.runner.AndroidJUnit4;
//
//
//import org.junit.After;
//import org.junit.Assert;
//import org.junit.Test;
//import org.junit.runner.RunWith;
//
//import ch.epfl.sweng.spotOn.singletonReferences.DatabaseRef;
//import ch.epfl.sweng.spotOn.user.RealUser;
//import ch.epfl.sweng.spotOn.user.User;
//
//@RunWith(AndroidJUnit4.class)
//public class UserTest {
//
//    private User testUser = null;
//
//    @After
//    public void removeTestUser() {
//        DatabaseRef.deleteUserFromDB(testUser.getUserId());
//    }
//
//    @Test
//    public void testSetAndGetUser(){
//        testUser = new RealUser("firstname","lastname","mlb",null);
//        testUser.setKarma(500);
//
//        try {
//            Thread.sleep(3000);
//        } catch (InterruptedException e) {
//            System.err.print(e);
//        }
//
//        Assert.assertEquals(testUser.getFirstName(), "firstname");
//        Assert.assertEquals(testUser.getLastName(), "lastname");
//        Assert.assertEquals(testUser.getUserId(), "mlb");
//        Assert.assertEquals(testUser.getKarma(), 500);
//        Assert.assertEquals(testUser.computeRemainingPhotos(), RealUser.computeMaxPhotoInDay(500));
//        Assert.assertEquals(testUser.isLoggedIn(), false);
//    }
//}
