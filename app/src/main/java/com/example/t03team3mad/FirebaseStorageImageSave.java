package com.example.t03team3mad;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;


public class FirebaseStorageImageSave extends AsyncTask<imageTaskSaveParameters,Void,Void> {
    private static final String TAG = "FirebaseStorageImageSave";
    @SuppressLint("LongLogTag")
    public Void putImage(imageTaskSaveParameters parameters) {
        final FirebaseStorage storage = FirebaseStorage.getInstance();
        final StorageReference ref = storage.getReference().child(parameters.imagename);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        Log.v(TAG,"Saving image at"+ref.getBucket());
        Log.v(TAG,"image name is ="+parameters.imagename);
        if(parameters.bitmap!=null) {
            parameters.bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
            byte[] data = baos.toByteArray();

            UploadTask uploadTask = ref.putBytes(data);
            uploadTask.addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception exception) {
                }
            }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                }
            });
        }
        return null;

    }

    @Override
    protected Void doInBackground(imageTaskSaveParameters... parameters) {
        putImage(parameters[0]);
        return null;
    }
    protected void onPostExecute(String path) {
    }
}