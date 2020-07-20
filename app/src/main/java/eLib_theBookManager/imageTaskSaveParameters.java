package eLib_theBookManager;

import android.graphics.Bitmap;

public class imageTaskSaveParameters{
    String imagename;
    Bitmap bitmap;

    imageTaskSaveParameters(String imagename, Bitmap bitmap) {
        this.imagename = imagename;
        this.bitmap = bitmap;
    }
}
