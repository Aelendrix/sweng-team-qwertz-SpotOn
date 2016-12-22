package ch.epfl.sweng.spotOn.gui;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.widget.Button;

import com.facebook.Profile;
import com.facebook.login.LoginManager;

import ch.epfl.sweng.spotOn.R;
import ch.epfl.sweng.spotOn.user.UserManager;

/**
 * Created by Alexis on 22/12/2016.
 */

public class FacebookLogOutDialog extends DialogFragment {

    /*public interface StrongActionDialogListener {
        public void onDialogPositiveClick(DialogFragment dialog);
        public void onDialogNegativeClick(DialogFragment dialog);
    }

    StrongActionDialogListener mListener;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        try {
            mListener = (StrongActionDialogListener) context;
        }catch(ClassCastException e){
            throw new ClassCastException(context.toString() + " must implement StrongActionDialogListener");
        }
    }*/

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage(R.string.log_out_question)
        .setPositiveButton(R.string.log_out, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                disconnectFacebook();
                UserManager user = UserManager.getInstance();
                user.destroyUser();
                Intent intent = new Intent(getActivity(), MainActivity.class);
                startActivity(intent);
            }
        })
        .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                FacebookLogOutDialog.this.dismiss();
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();

        Button positiveButton = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
        positiveButton.setTextColor(Color.parseColor("#4B3832"));
        Button negativeButton = dialog.getButton(AlertDialog.BUTTON_NEGATIVE);
        negativeButton.setTextColor(Color.parseColor("#4B3832"));

        return dialog;
    }

    private void disconnectFacebook() {
        Profile profile = Profile.getCurrentProfile();
        if (profile != null) {
            LoginManager.getInstance().logOut();
            //go to the mainActivity in the activity stack
        }
    }
}
