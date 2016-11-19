//package ch.epfl.sweng.spotOn.test;
//
//import android.support.test.runner.AndroidJUnit4;
//
//import org.junit.Test;
//import org.junit.runner.RunWith;
//
//import ch.epfl.sweng.spotOn.user.User;
//
//
//@RunWith(AndroidJUnit4.class)
//public class UsersTest {
//
//    @Test
//    public void testUserSetGet() throws Exception{
//        String firstName = "aaa";
//        String lastName = "bbb";
//        String userId = "123456789";
//        String firstName2 = "ccc";
//        String lastName2 = "ddd";
//        String userId2 = "987654321";
//        User u = new User(firstName, lastName,userId);
//        if(!u.getFirstName().equals(firstName)) {
//            throw new AssertionError("firstName getter Error");
//        }
//        if(!u.getLastName().equals(lastName)) {
//            throw new AssertionError("lastName getter Error");
//        }
//        if(!u.getUserId().equals(userId)) {
//            throw new AssertionError("userId getter Error");
//        }
//        u.setFirstName(firstName2);
//        u.setLastName(lastName2);
//        u.setUserId(userId2);
//        if(!u.getFirstName().equals(firstName2)) {
//            throw new AssertionError("firstName setter Error");
//        }
//        if(!u.getLastName().equals(lastName2)) {
//            throw new AssertionError("lastName setter Error");
//        }
//        if(!u.getUserId().equals(userId2)) {
//            throw new AssertionError("userId setter Error");
//        }
//    }
//}
