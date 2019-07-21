package amir.face.detection.ui.photo_viewer;

import android.os.Bundle;
import android.widget.ImageView;

import amir.face.detection.R;
import amir.face.detection.utils.base.BaseActivity;
import amir.face.detection.utils.base.PublicMethods;

import static amir.face.detection.utils.base.Cons.IMG_EXTRA_KEY;
import static amir.face.detection.utils.base.Cons.IMG_FILE;

public class PhotoViewerActivity extends BaseActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo_viewer);
        if (getIntent().hasExtra(IMG_EXTRA_KEY)) {
            ImageView imageView = findViewById(R.id.image);
            String imagePath = getIntent().getStringExtra(IMG_EXTRA_KEY);
            imageView.setImageBitmap(PublicMethods.getBitmapByPath(imagePath, IMG_FILE));
        }
    }
}
