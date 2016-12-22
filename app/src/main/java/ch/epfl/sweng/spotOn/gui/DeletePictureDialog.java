package ch.epfl.sweng.spotOn.gui;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.widget.Button;

import ch.epfl.sweng.spotOn.R;
import ch.epfl.sweng.spotOn.localObjects.LocalDatabase;
import ch.epfl.sweng.spotOn.singletonReferences.DatabaseRef;
import ch.epfl.sweng.spotOn.singletonReferences.StorageRef;
import ch.epfl.sweng.spotOn.user.UserManager;

/**
 * Created by Alexis on 22/12/2016.
 */

public class DeletePictureDialog extends DialogFragment {
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage(R.string.delete_picture_question)
        .setPositiveButton(R.string.userProfileListPictureDeleteButton, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                String pictureID = DeletePictureDialog.this.getArguments().getString("pictureID");
                LocalDatabase.getInstance().removePhotoObject(pictureID);
                DatabaseRef.deletePhotoObjectFromDB(pictureID);
                StorageRef.deletePictureFromStorage(pictureID);
                UserManager.getInstance().getUser().removePhoto(pictureID);
            }
        })
        .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dismiss();
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
}
