package ch.epfl.sweng.spotOn.utils;

import android.app.Activity;
import android.util.Log;
import android.widget.Toast;

/**  Allows to send testMessages even outside of Activity/Fragments. Provides finer control over
 *   how several toasts are displayed simultaneously
 *   Downside : need to call update() when changing activity
 */
public class ToastProvider {

    private static ToastProvider mSingleInstance;

    private Activity currentActivity;
    private Toast mCurrentlyDisplayedToast;


// INIT METHODS AND CONSTRUCTOR
    public static void initialize(){
        if(mSingleInstance==null) {
            mSingleInstance = new ToastProvider();
        }else{
            Log.d("ToastProvider","WARNING : tried to initialized ToastProvider, but an instance already exists");
        }
    }

    private ToastProvider(){
        currentActivity = null;
        mCurrentlyDisplayedToast = null;
    }


// PUBLIC METHODS
    public static ToastProvider get(){
        if(mSingleInstance==null){
            throw new IllegalStateException("ToastProvider wasn't initialized");
        }
        return mSingleInstance;
    }

    public void update(Activity activity){
        if(activity==null){
            throw new IllegalArgumentException();
        }
        currentActivity=activity;
    }

    public void printOverCurrent(String message, int duration){
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

    public void printIfNoCurrent(String message, int duration){
        checkNonnullActivity();
        checkDuration(duration);
        if(!toastBeingDisplayed()){
            displayToast(message, duration);
        }
    }

    public boolean toastBeingDisplayed(){
        if(mCurrentlyDisplayedToast==null){
            return false;
        }else{
            return mCurrentlyDisplayedToast.getView().isShown();
        }
    }

    public void setDisplayedToast(Toast t){
        mCurrentlyDisplayedToast = t;
    }


    // PRIVATE HELPERS
    private void displayToast(final String message, final int duration){
        currentActivity.runOnUiThread( new Runnable() {
            public void run() {
                Toast newToast = Toast.makeText(currentActivity, message, duration);
                setDisplayedToast(newToast);
                newToast.show();
            }
        });
    }

    private void checkDuration(int duration){
        if(!(duration==Toast.LENGTH_LONG || duration==Toast.LENGTH_SHORT)){
            throw new IllegalArgumentException("Invalid duration");
        }
    }

    private void checkNonnullActivity(){
        if(currentActivity==null){
            Log.d("ToastProvider", "ToastProvider has no current context");
        }
    }



}
