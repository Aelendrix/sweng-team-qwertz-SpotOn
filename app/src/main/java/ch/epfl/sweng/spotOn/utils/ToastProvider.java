package ch.epfl.sweng.spotOn.utils;

import android.app.Activity;
import android.util.Log;
import android.widget.Toast;

/**  Allows to send testMessages even outside of Activity/Fragments. Provides finer control over
 *   how several toasts are displayed simultaneously
 *   Downside : need to call update() when changing activity
 */
public class ToastProvider {

    //    private static Context mCurrentContext = null;
    private static Activity currentActivity = null;
    private static Toast mCurrentlyDisplayedToast = null;

    private ToastProvider(){
        //empty default constructor
    }


    // PUBLIC METHODS
    public static void update(Activity activity){
        if(activity==null){
            throw new IllegalArgumentException();
        }
        currentActivity=activity;
    }

    public static void printOverCurrent(String message, int duration){
        checkNonnullActivity();
        checkDuration(duration);
        if(toastBeingDisplayed()){
            mCurrentlyDisplayedToast.cancel();
        }
        displayToast(message, duration);
    }


// this methods has issues I need to solve later (todo)
//    public static void printAfterCurrent(String message, int duration){
//        if(currentActivity==null){
//            Log.d("ToastProvider", "ToastProvider has no current context");
//            return;
//        }
//        if(!(duration==LONG || duration==SHORT)){
//            throw new IllegalArgumentException("Invalid duration");
//        }
//        displayToast(message, duration);
//    }

    public static void printIfNoCurrent(String message, int duration){
        checkNonnullActivity();
        checkDuration(duration);
        if(!toastBeingDisplayed()){
            displayToast(message, duration);
        }
    }

    public static boolean toastBeingDisplayed(){
        if(mCurrentlyDisplayedToast==null){
            return false;
        }else{
            return mCurrentlyDisplayedToast.getView().isShown();
        }
    }

    public static void setDisplayedToast(Toast t){
        mCurrentlyDisplayedToast = t;
    }


    // PRIVATE HELPERS
    private static void displayToast(final String message, final int duration){
        currentActivity.runOnUiThread( new Runnable() {
            public void run() {
                Toast newToast = Toast.makeText(currentActivity, message, duration);
                ToastProvider.setDisplayedToast(newToast);
                newToast.show();
            }
        });
    }

    private static void checkDuration(int duration){
        if(!(duration==Toast.LENGTH_LONG || duration==Toast.LENGTH_SHORT)){
            throw new IllegalArgumentException("Invalid duration");
        }
    }

    private static void checkNonnullActivity(){
        if(currentActivity==null){
            Log.d("ToastProvider", "ToastProvider has no current context");
            return;
        }
    }



}
