package com.T03G3.eLibtheBookManager;

import android.graphics.Bitmap;

public class imageTaskSaveParameters{
    String imagename;
    Bitmap bitmap;

    imageTaskSaveParameters(String imagename, Bitmap bitmap) {
        this.imagename = imagename;
        this.bitmap = bitmap;
    }
}
