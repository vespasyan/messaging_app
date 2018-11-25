package tech.metaphor.www.metaphor;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;
import id.zelory.compressor.Compressor;

import static android.graphics.Color.argb;

public class UserSettingsActivity extends AppCompatActivity {

    private static final String TAG1 = "Update successfully";

    private CircleImageView circleImageView;
    private TextView displayName;
    private TextView displayStatus;
    private Button changeImage;
    private Button changeStatus;


    private FirebaseAuth setAuth;
    private StorageReference storageReference_user;
    private StorageReference storageReference_user_two;
    private DatabaseReference databaseReference_user;

    private static final String TAG2 = "MyUploadService";

    /** Intent Actions **/
    public static final String ACTION_UPLOAD = "action_upload";
    public static final String UPLOAD_COMPLETED = "upload_completed";
    public static final String UPLOAD_ERROR = "upload_error";

    /** Intent Extras **/
    public static final String EXTRA_FILE_URI = "extra_file_uri";
    public static final String EXTRA_DOWNLOAD_URL = "extra_download_url";

    // [START declare_ref]
    private StorageReference mStorageRef;
    // [END declare_ref]

    private final static int picForImage = 1;
    Uri imagePath = null;
    Bitmap bitmap = null;


    /* ...............add
     * transparent
     * background*/

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_settings);


        circleImageView = findViewById(R.id.set_image);
        displayName = findViewById(R.id.set_name);
        displayStatus = findViewById(R.id.set_status);
        changeImage = findViewById(R.id.change_img);
        changeStatus = findViewById(R.id.change_status);


        setAuth = FirebaseAuth.getInstance();
        String online_user_id = setAuth.getCurrentUser().getUid();

        databaseReference_user = FirebaseDatabase.getInstance().getReference().child("Users").child(online_user_id);
        storageReference_user = FirebaseStorage.getInstance().getReference().child("Profile_images");




        databaseReference_user.addValueEventListener(new ValueEventListener() {

             static final String TAG = "Warning";
            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot)
            {

                String image = dataSnapshot.child("user_image").getValue(String.class);
                String name = dataSnapshot.child("user_name").getValue(String.class);
                String status = dataSnapshot.child("user_status").getValue(String.class);
                String thumb_image = dataSnapshot.child("user_thumb_image").getValue(String.class);

                displayName.setText(name);
                displayStatus.setText(status);
                Picasso.get().load(String.valueOf(image)).into(circleImageView);
                //circleImageView.setImageURI(Uri.parse(image));
        }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                DatabaseError error = null;
                Log.w(TAG, "Failed to read value.", error.toException());

            }
        });

        changeImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent imgIntent = new Intent();
                imgIntent.setAction(Intent.ACTION_GET_CONTENT);
                imgIntent.setType("image/*");
                startActivityForResult(imgIntent, picForImage);

            }
        });
        changeStatus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent_status = new Intent(UserSettingsActivity.this, StatusActivity.class);
                startActivity(intent_status);
            }
        });
    }

/**This onActivityResult()
 * taken from ArthurHub...
 * for cropping images.....*/

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == picForImage && resultCode == RESULT_OK && data != null){
            Uri uri = data.getData();
            CropImage.activity(uri)
                    .setAspectRatio(1,1)
                    .start(this);
        }
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {

            CropImage.ActivityResult result = CropImage.getActivityResult(data);

            if (resultCode == RESULT_OK) {
                final Uri resultUri = result.getUri();
                String user_id = Objects.requireNonNull(setAuth.getCurrentUser()).getUid();
                File filePath_uri = new File(resultUri.getPath());

                try {

                    bitmap = new Compressor(this)
                            .setMaxWidth(200)
                            .setMaxHeight(200)
                            .setQuality(60)
                            .compressToBitmap(filePath_uri);

                }catch (IOException e){
                    e.printStackTrace();
                }
                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 60, byteArrayOutputStream);
                final byte[] thumb_byte = byteArrayOutputStream.toByteArray();



                final StorageReference filePath = storageReference_user.child(user_id + ".jpg");
                filePath.putFile(resultUri).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                        taskSnapshot.getBytesTransferred();
                        taskSnapshot.getTotalByteCount();

                    }
                }).continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                    @Override
                    public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {

                        if (!task.isSuccessful()){
                            throw task.getException();
                        }
                        return filePath.getDownloadUrl();


                    }
                }).addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        broadcastUploadFinished(uri, resultUri);
                        Toast.makeText(UserSettingsActivity.this,
                                "upload is finished successfully",Toast.LENGTH_SHORT).show();
                    }
                });
            }
            else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
            }
        }
    }

    private boolean broadcastUploadFinished(@Nullable Uri downloadUrl, @Nullable Uri fileUri){
        boolean success = downloadUrl != null;
        String action = success ? UPLOAD_COMPLETED : UPLOAD_ERROR;

        Intent broadcast = new Intent(action)
                .putExtra(EXTRA_DOWNLOAD_URL, downloadUrl)
                .putExtra(EXTRA_FILE_URI, fileUri);
        return LocalBroadcastManager.getInstance(getApplicationContext())
                .sendBroadcast(broadcast);



    }

}





