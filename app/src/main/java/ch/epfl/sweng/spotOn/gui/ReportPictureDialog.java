package ch.epfl.sweng.spotOn.gui;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.widget.Button;

import ch.epfl.sweng.spotOn.R;

/**
 * Created by Alexis on 22/12/2016.
 *
 */

public class ReportPictureDialog extends DialogFragment {

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage(R.string.report_question)
                .setPositiveButton(R.string.report, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        ViewFullSizeImageActivity activity = (ViewFullSizeImageActivity) getActivity();
                        activity.reportPicture();
                    }
                })
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        ReportPictureDialog.this.dismiss();
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
