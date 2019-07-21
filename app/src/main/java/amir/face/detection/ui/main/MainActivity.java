// Copyright 2018 Google LLC
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//      http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.
package amir.face.detection.ui.main;

import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat.OnRequestPermissionsResultCallback;
import androidx.core.content.ContextCompat;

import com.google.android.gms.common.annotation.KeepName;
import com.google.firebase.ml.vision.face.FirebaseVisionFace;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import amir.face.detection.R;
import amir.face.detection.ui.photo_viewer.PhotoViewerActivity;
import amir.face.detection.utils.base.BaseActivity;
import amir.face.detection.utils.common.CameraSource;
import amir.face.detection.utils.common.CameraSourcePreview;
import amir.face.detection.utils.interfaces.FaceDetectStatus;
import amir.face.detection.utils.common.FrameMetadata;
import amir.face.detection.utils.interfaces.FrameReturn;
import amir.face.detection.utils.common.GraphicOverlay;
import amir.face.detection.utils.base.PublicMethods;
import amir.face.detection.utils.models.RectModel;
import amir.face.detection.utils.visions.FaceDetectionProcessor;


@KeepName
public final class MainActivity extends BaseActivity
        implements OnRequestPermissionsResultCallback, FrameReturn, FaceDetectStatus {
    private static final String FACE_DETECTION = "Face Detection";
    private static final String TAG = "MLKitTAG";

    private CameraSource cameraSource = null;
    private CameraSourcePreview preview;
    private GraphicOverlay graphicOverlay;
    private ImageView faceFrame;
    private ImageView test;
    private Button takePhoto;
    private Bitmap croppedImage = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        test = findViewById(R.id.test);
        preview = findViewById(R.id.firePreview);
        takePhoto = findViewById(R.id.takePhoto);
        faceFrame = findViewById(R.id.faceFrame);
        graphicOverlay = findViewById(R.id.fireFaceOverlay);

        if (PublicMethods.allPermissionsGranted(this)) {
            createCameraSource();
        } else {
            PublicMethods.getRuntimePermissions(this);
        }

        takePhoto.setOnClickListener(v -> {
            takePhoto();
        });
    }


    private void createCameraSource() {
        if (cameraSource == null) {
            cameraSource = new CameraSource(this, graphicOverlay);
        }
        try {
            FaceDetectionProcessor processor = new FaceDetectionProcessor(getResources());
            processor.frameHandler = this;
            processor.faceDetectStatus = this;
            cameraSource.setMachineLearningFrameProcessor(processor);
        } catch (Exception e) {
            Log.e(TAG, "Can not create image processor: " + FACE_DETECTION, e);
            Toast.makeText(
                    getApplicationContext(),
                    "Can not create image processor: " + e.getMessage(),
                    Toast.LENGTH_LONG)
                    .show();
        }
    }


    private void startCameraSource() {
        if (cameraSource != null) {
            try {
                if (preview == null) {
                    Log.d(TAG, "resume: Preview is null");
                }
                if (graphicOverlay == null) {
                    Log.d(TAG, "resume: graphOverlay is null");
                }
                preview.start(cameraSource, graphicOverlay);
            } catch (IOException e) {
                Log.e(TAG, "Unable to start camera source.", e);
                cameraSource.release();
                cameraSource = null;
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        startCameraSource();
    }

    @Override
    protected void onPause() {
        super.onPause();
        preview.stop();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (cameraSource != null) {
            cameraSource.release();
        }
    }

    @Override
    public void onRequestPermissionsResult(
            int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (PublicMethods.allPermissionsGranted(this)) {
            createCameraSource();
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    //calls with each frame which includes by face

    Bitmap originalImage = null;

    @Override
    public void onFrame(Bitmap image, FirebaseVisionFace face, FrameMetadata frameMetadata, GraphicOverlay graphicOverlay) {
        originalImage = image;
    }

    @Override
    public void onFaceLocated(RectModel rectModel) {
        faceFrame.setColorFilter(ContextCompat.getColor(this, R.color.green));
        takePhoto.setEnabled(true);

        float scaledWidth = PublicMethods.getScreenWidth(this) / CameraSource.requestedPreviewWidth;
        float scaledHeight = PublicMethods.getScreenHeight(this) / CameraSource.requestedPreviewHeight;

        float left = rectModel.getLeft() / scaledWidth;
        float right = rectModel.getRight() / scaledWidth;
        float newWidth = right - left;
        float top = rectModel.getTop();

        croppedImage =
                Bitmap.createBitmap(originalImage,
                        ((int) (left)),
                        (int) (top / scaledHeight),
                        ((int) (newWidth)),
                        (int) (originalImage.getHeight() - top / 3));

        test.setImageBitmap(croppedImage);
    }

    private void takePhoto() {
        if (croppedImage != null) {
            String path = saveToInternalStorage(croppedImage) ;

            startActivity(new Intent(mActivity, PhotoViewerActivity.class)
                    .putExtra("image", path));
        }
    }

    private String saveToInternalStorage(Bitmap bitmapImage){
        ContextWrapper cw = new ContextWrapper(getApplicationContext());
        // path to /data/data/yourapp/app_data/imageDir
        File directory = cw.getDir("imageDir", Context.MODE_PRIVATE);
        // Create imageDir
        File mypath=new File(directory,"profile.jpg");

        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(mypath);
            // Use the compress method on the BitMap object to write image to the OutputStream
            bitmapImage.compress(Bitmap.CompressFormat.PNG, 100, fos);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                fos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return directory.getAbsolutePath();
    }

    @Override
    public void onFaceNotLocated() {
        faceFrame.setColorFilter(ContextCompat.getColor(this, R.color.red));
        takePhoto.setEnabled(false);
    }
}