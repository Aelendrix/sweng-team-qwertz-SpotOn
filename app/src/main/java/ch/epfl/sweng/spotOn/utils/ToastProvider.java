package ch.epfl.sweng.spotOn.utils;

import android.app.Application;
import android.content.Context;
import android.util.Log;
import android.widget.Toast;

/**  Allows to send testMessages even outside of Activity/Fragments. Provides finer control over
 *   how several toasts are displayed simultaneously
 *   Downside : need to call update() when changing activity
 */
public class ToastProvider extends Application {

    public static int LONG = Toast.LENGTH_LONG;
    public static int SHORT = Toast.LENGTH_SHORT;

    private static Context mCurrentContext = null;
    private static Toast mCurrentlyDisplayedToast = null;

    private ToastProvider(){
        //empty default constructor
    }


// PUBLIC METHODS
    public static void update(Context c){
        if(c==null){
            throw new IllegalArgumentException();
        }
        mCurrentContext=c;
    }

    public static void printOverCurrent(String message, int duration){
        if(mCurrentContext==null){
            Log.d("ToastProvider", "ToastProvider has no current context");
            return;
        }
        if(!(duration==LONG || duration==SHORT)){
            throw new IllegalArgumentException("Invalid duration");
        }
        if(toastBeingDisplayed()){
            mCurrentlyDisplayedToast.cancel();
        }
        displayToast(message, duration);
    }

// buggy, waiting for a fix
//    public static void printAfterCurrent(String message, int duration){
//        if(mCurrentContext==null){
//            Log.d("ToastProvider", "ToastProvider has no current context");
//            return;
//        }
//        if(!(duration==LONG || duration==SHORT)){
//            throw new IllegalArgumentException("Invalid duration");
//        }
//        displayToast(message, duration);
//    }

    public static void printIfNoCurrent(String message, int duration){
        if(mCurrentContext==null){
            Log.d("ToastProvider", "ToastProvider has no current context");
            return;
        }
        if(!(duration==LONG || duration==SHORT)){
            throw new IllegalArgumentException("Invalid duration");
        }
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


// PRIVATE HELPERS
    private static void displayToast(String message, int duration){
        Toast newToast = Toast.makeText(mCurrentContext,message,duration);
        mCurrentlyDisplayedToast = newToast;
        newToast.show();
    }



}
