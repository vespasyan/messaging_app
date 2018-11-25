package tech.metaphor.www.metaphor;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import android.widget.Toolbar;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class StatusActivity extends AppCompatActivity {


    private android.support.v7.widget.Toolbar tool_bar;
    private Button statusChangeButton;
    private EditText statusChangeText;
    private ProgressDialog progressDialog;

    private DatabaseReference databaseReference_change;
    private FirebaseAuth firebaseAuth_auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_status);

        tool_bar = findViewById(R.id.status_app_bar);
        setSupportActionBar(tool_bar);
        getSupportActionBar().setTitle("Change Status");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        statusChangeButton = findViewById(R.id.status_change_button);
        statusChangeText = findViewById(R.id.change_status_text);
        progressDialog = new ProgressDialog(this);

        firebaseAuth_auth = FirebaseAuth.getInstance();
        String user_id = firebaseAuth_auth.getCurrentUser().getUid();
        databaseReference_change = FirebaseDatabase.getInstance().getReference().child("Users").child(user_id);

        statusChangeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String default_status = statusChangeText.getText().toString();

                ChangeProfileStatus(default_status);
            }
        });

    }

    private void ChangeProfileStatus(String default_status) {
        if (TextUtils.isEmpty(default_status)){
            Toast.makeText(StatusActivity.this, "Please Write Status State..",Toast.LENGTH_LONG).show();

        }else {
            progressDialog.setMessage("Your Status Updating...");

            databaseReference_change.child("user_status").setValue(default_status).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()){
                        progressDialog.dismiss();
                        statusChangeButton.onEditorAction(EditorInfo.IME_ACTION_DONE);

                        //Intent statusIntent = new Intent(StatusActivity.this, UserSettingsActivity.class);
                        //startActivity(statusIntent);


                        Toast.makeText(StatusActivity.this,"Status changed Successfully", Toast.LENGTH_SHORT).show();
                    }else {
                        Toast.makeText(StatusActivity.this,"Error...", Toast.LENGTH_SHORT).show();
                    }
                }
            });

        }
    }


}
