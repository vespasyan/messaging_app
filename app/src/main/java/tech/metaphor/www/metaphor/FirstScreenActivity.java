package tech.metaphor.www.metaphor;

import android.annotation.SuppressLint;
import android.app.Application;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.RelativeLayout;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;



public class FirstScreenActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private String screen;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.first_screen);
        mAuth = FirebaseAuth.getInstance();

        FirebaseUser currentUser = mAuth.getCurrentUser();

        if (currentUser != null){
            currentUser.getIdToken(true).isSuccessful();
            Intent navIntent = new Intent(FirstScreenActivity.this, WelcomeScreenActivity.class);
            navIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(navIntent);
            finish();
        }else{
            //currentUser.getUid();
            Intent nav2Intent = new Intent(FirstScreenActivity.this, PhoneNumberInputActivity.class);
            nav2Intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(nav2Intent);
        }

    }

}

