package amir.face.detection.utils.interfaces;

import amir.face.detection.utils.models.RectModel;

public interface FaceDetectStatus {
    void onFaceLocated(RectModel rectModel);
    void onFaceNotLocated() ;
}
