package eLib_theBookManager;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;

public class FirebaseStorageImages extends AsyncTask<String,Void,String> {
    private static final String TAG = "FirebaseStorageImages";
    public String getimage(String imagename) {
        final FirebaseStorage storage = FirebaseStorage.getInstance();
        final Bitmap[] my_image = new Bitmap[1];
        final StorageReference ref = storage.getReference().child(imagename);

        Log.v(TAG, String.valueOf(ref.getParent()));
        File directory = new File("/data/data/com.example.t03team3mad/app_imageDir");
        directory.mkdirs();
        //image saved name
        final String fileName = imagename;
        //creates the file
        final File filetosave = new File(directory, fileName);
        ref.getFile(filetosave).addOnSuccessListener(new OnSuccessListener< FileDownloadTask.TaskSnapshot >() {
            @Override
            public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                //my_image[0] = BitmapFactory.decodeByteArray(bytes,0,bytes.length);
                my_image[0] = BitmapFactory.decodeFile(filetosave.getAbsolutePath());
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

            }
        });
        return filetosave.getAbsolutePath();
    }

    @Override
    protected String doInBackground(String... strings) {
        return getimage(strings[0]);
    }
    protected void onPostExecute(String path) {
    }
}
