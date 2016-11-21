package ch.epfl.sweng.spotOn.gui;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;

import ch.epfl.sweng.spotOn.R;

public class EditPictureActivity extends AppCompatActivity {

    private Bitmap mEditedBitmap;
    private ImageView mEditedImageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_picture);
        mEditedImageView = (ImageView) findViewById(R.id.image_view_edition);

        Intent intent = getIntent();
        Uri uri = (Uri) Uri.parse(intent.getExtras().getString("bitmapToEdit"));
        mEditedBitmap = TakePictureFragment.getBitmap(uri, this);
        mEditedImageView.setImageBitmap(mEditedBitmap);
    }
}
