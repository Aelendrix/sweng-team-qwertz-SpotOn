package ch.epfl.sweng.spotOn.utils;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;

/**  Allows to send testMessages even outside of Activity/Fragments. Provides finer control over
 *   how several toasts are displayed simultaneously
 *   Downside : need to call update() when changing activity
 */
public class ToastProvider {

    public static int LONG = Toast.LENGTH_LONG;
    public static int SHORT = Toast.LENGTH_SHORT;

//    private static Context mCurrentContext = null;
    private static Activity currentActivity;
    private static Toast mCurrentlyDisplayedToast = null;

    private ToastProvider(){
        //empty default constructor
    }


// PUBLIC METHODS
    public static void update(Activity activity){
        if(activity==null){
            throw new IllegalArgumentException();
        }
//        mCurrentContext=c;
        currentActivity=activity;
    }

    public static void printOverCurrent(String message, int duration){
//        if(mCurrentContext==null){
        if(currentActivity==null){
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

    public static void printAfterCurrent(String message, int duration){
//        if(mCurrentContext==null){
        if(currentActivity==null){
            Log.d("ToastProvider", "ToastProvider has no current context");
            return;
        }
        if(!(duration==LONG || duration==SHORT)){
            throw new IllegalArgumentException("Invalid duration");
        }
        displayToast(message, duration);
    }

    public static void printIfNoCurrent(String message, int duration){
//        if(mCurrentContext==null){
        if(currentActivity==null){
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
    private static void displayToast(final String message, final int duration){
// Handler way
//        Handler mHandler = new Handler(Looper.getMainLooper()){
//            @Override
//            public void handleMessage(Message message) {
//                String text = (String) message.obj;
//                Toast newToast = Toast.makeText(mCurrentContext, text, duration);
//                mCurrentlyDisplayedToast = newToast;
//                newToast.show();
//            }
//        };

        currentActivity.runOnUiThread(new Runnable() {
            public void run() {
                Toast newToast = Toast.makeText(currentActivity, message, duration);
                mCurrentlyDisplayedToast = newToast;
                newToast.show();
            }
        });
    }



}
