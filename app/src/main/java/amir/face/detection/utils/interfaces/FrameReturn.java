package amir.face.detection.utils.interfaces;

import android.graphics.Bitmap;

import com.google.firebase.ml.vision.face.FirebaseVisionFace;

import amir.face.detection.utils.common.FrameMetadata;
import amir.face.detection.utils.common.GraphicOverlay;

public interface FrameReturn{
    void onFrame(
            Bitmap image ,
            FirebaseVisionFace face ,
            FrameMetadata frameMetadata,
            GraphicOverlay graphicOverlay
    );
}