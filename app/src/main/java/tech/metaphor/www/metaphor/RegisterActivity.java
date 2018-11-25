package tech.metaphor.www.metaphor;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;

import java.io.IOException;

import de.hdodenhof.circleimageview.CircleImageView;

public class RegisterActivity extends AppCompatActivity {
    private static final String TAG = "AddToDatabase";



    private Button mAddToDB;

    private EditText mNewName;

    //add Firebase Database stuff
    private FirebaseDatabase mFirebaseDatabase;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private DatabaseReference myRef;
    private StorageReference storageReference_reg;



    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        //declare variables in oncreate
        mAddToDB = findViewById(R.id.BtnName);
        mNewName = findViewById(R.id.editName);

        storageReference_reg = FirebaseStorage.getInstance().getReference().child("Profile_images");


        //declare the database reference object. This is what we use to access the database.
        //NOTE: Unless you are signed in, this will not be useable.
        mAuth = FirebaseAuth.getInstance();
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        myRef = mFirebaseDatabase.getReference();
        // Read from the database
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    // User is signed in
                    Log.d(TAG, "onAuthStateChanged:signed_in:" + user.getUid());
                    toastMessage("Successfully signed in with: " + user.getPhoneNumber());


                } else {
                    // User is signed out
                    Log.d(TAG, "onAuthStateChanged:signed_out");
                    toastMessage("Successfully signed out.");
                }
                // ...
            }
        };
        mAddToDB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mNewName.onEditorAction(EditorInfo.IME_ACTION_DONE);
                Log.d(TAG, "onClick: Attempting to add object to database.");
                String newName = mNewName.getText().toString();
                if (!newName.equals("")) {
                    FirebaseUser user = mAuth.getCurrentUser();
                    String userID = user.getUid();

                    myRef = FirebaseDatabase.getInstance().getReference().child("Users").child(userID);
                    myRef.child("user_name").setValue(newName);
                    myRef.child("user_status").setValue("Come to Metaphor Guys...");
                    myRef.child("user_image").setValue("default_profile");
                    myRef.child("user_thumb_image").setValue("default_image");

                    final Intent registerIntent = new Intent(RegisterActivity.this, WelcomeScreenActivity.class);
                    Thread timer3 = new Thread() {
                        @Override
                        public void run() {
                            //super.run();
                            try {
                                sleep(3000);
                            } catch (InterruptedException e2) {
                                e2.printStackTrace();
                            } finally {
                                startActivity(registerIntent);
                                finish();
                            }
                        }
                    };
                    timer3.start();
                }

            }
        });

    }
    @Override
    public void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);

    }

    @Override
    public void onStop() {
        super.onStop();
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }

    //add a toast to show when successfully signed in
    /**
     * customizable toast
     * @param message
     */
    private void toastMessage(String message){
        Toast.makeText(this,message,Toast.LENGTH_SHORT).show();
    }
}


