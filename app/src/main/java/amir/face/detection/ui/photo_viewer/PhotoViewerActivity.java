package amir.face.detection.ui.photo_viewer;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.widget.ImageView;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

import amir.face.detection.R;
import amir.face.detection.utils.base.BaseActivity;

public class PhotoViewerActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo_viewer);
        if (getIntent().hasExtra("image")) {
            ImageView imageView = findViewById(R.id.image) ;
            String imagePath = getIntent().getStringExtra("image") ;

            try {
                File f=new File(imagePath, "profile.jpg");
                Bitmap b = BitmapFactory.decodeStream(new FileInputStream(f));
                imageView.setImageBitmap(b);
            }
            catch (FileNotFoundException e)
            {
                e.printStackTrace();
            }

        }
    }




}
