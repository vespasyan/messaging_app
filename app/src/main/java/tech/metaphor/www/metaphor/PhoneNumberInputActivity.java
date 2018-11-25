package tech.metaphor.www.metaphor;

import android.annotation.SuppressLint;
import android.app.MediaRouteButton;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.auth.ui.ProgressDialogHolder;
import com.firebase.ui.auth.ui.phone.CountryListSpinner;
import com.google.android.gms.common.internal.safeparcel.SafeParcelable;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.FirebaseTooManyRequestsException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.hbb20.CountryCodePicker;

import java.io.Serializable;
import java.util.Objects;
import java.util.concurrent.TimeUnit;


public class PhoneNumberInputActivity extends AppCompatActivity implements Serializable{



    private EditText mPhoneNumber;
    private EditText editName;
    private TextView mdForgetView;
    TextView pleaseWait;
    private Button mSendButton;
    String mVerificationId;

    private TextView mErrorText;
    private TextView welcomeText;
    private  String getEditText;
    private String getEditText2;
    private FirebaseAuth mAuth;
    private ProgressDialog loadBar;
    String sendCode;
    CountryCodePicker ccp;





    PhoneAuthProvider.ForceResendingToken mResendToken;
    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks;


    //@SuppressLint("ResourceType")
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.phone_number_input);
        mSendButton = findViewById(R.id.Btn1);
        mSendButton.setAlpha(1f);
        mAuth = FirebaseAuth.getInstance();

        ccp = findViewById(R.id.ccp_id);
        //ccp.registerCarrierNumberEditText(mPhoneNumber);
        //ccp.ggetFormattedFullNumber();
        ccp.getFullNumberWithPlus();

        loadBar = new ProgressDialog(this);


        //welcomeText = findViewById(R.id.welcomeText);
        //welcomeText.setText("Welcome to Verification \n and \n Subscription Screen");
        //welcomeText.setAlpha(0.4f);


        //Intent intent = new Intent(PhoneNumberInputActivity.this, MainActivity.class);
        //startActivity(intent);
        //editName = findViewById(R.id.editName);


        mPhoneNumber = findViewById(R.id.editPhoneNumber);
        //editName = findViewById(R.id.editName);
        mSendButton = findViewById(R.id.Btn1);
        mErrorText = findViewById(R.id.errorView);
        pleaseWait = findViewById(R.id.textWait);

        //mdForgetView = findViewById(R.id.dForgetView);
        //mdForgetView.setText("Do not forget to add \n COUNTRY CODE \n like this +1.....");
        //mdForgetView.setAlpha(0.5f);
            mSendButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mPhoneNumber.onEditorAction(EditorInfo.IME_ACTION_DONE);

                    getEditText = mPhoneNumber.getText().toString();
                    //getEditText2 = editName.getText().toString();
                    ccp.registerCarrierNumberEditText(mPhoneNumber);
                    ccp.detectSIMCountry(true);

                    if (TextUtils.isEmpty(getEditText)) {
                        Toast.makeText(PhoneNumberInputActivity.this, "Enter your Phone Number", Toast.LENGTH_LONG).show();


                    } //else if (TextUtils.isEmpty(getEditText2)) {Toast.makeText(PhoneNumberInputActivity.this, "Enter your Name", Toast.LENGTH_LONG).show();
                    else {
                        //mPhoneNumber.setEnabled(false);
                        //mSendButton.setEnabled(false);


                        String phoneNumber = ccp.getFullNumberWithPlus();
                        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                                phoneNumber,        // Phone number to verify
                                60,                 // Timeout duration
                                TimeUnit.SECONDS,   // Unit of timeout
                                PhoneNumberInputActivity.this,
                                mCallbacks);
                    }
                }
            }
            );
            //PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verificationId, code);


            mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                @Override
                public void onVerificationCompleted(PhoneAuthCredential phoneAuthCredential) {

                    signInWithPhoneAuthCredential(phoneAuthCredential);

                }

                //@RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
                @Override
                public void onVerificationFailed(FirebaseException e) {
                    mErrorText.setText("There was an error to Verification. \n Please Try AGAIN");
                    //Toast.makeText(PhoneNumberInputActivity.this, "There was an error to Verification. \n Please Try AGAIN", Toast.LENGTH_LONG).show();
                    mErrorText.setVisibility(View.VISIBLE);
                    mErrorText.setBackgroundColor(Color.RED);
                    final Intent PhoneIntent = new Intent(PhoneNumberInputActivity.this, PhoneNumberInputActivity.class);
                    Thread timer2 = new Thread() {
                        @Override
                        public void run() {
                            //super.run();
                            try {
                                sleep(3000);
                            } catch (InterruptedException e1) {
                                e1.printStackTrace();
                            } finally {
                                startActivity(PhoneIntent);
                                finish();
                            }
                        }
                    };
                    timer2.start();
                }
                @Override
                public void onCodeSent(String verificationId, PhoneAuthProvider.ForceResendingToken token) {

                    ///////////////////////////////// Save verification ID and resending token so we can use them later
                    mVerificationId = verificationId;
                    mResendToken = token;
                    mSendButton.setText("VERIFYING......");
                    //loadBar.setTitle("marco polo");
                    //loadBar.setMessage("kdsmfflnjvkhgyryduyg");



                    // ...
                }
            };

    }

    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(PhoneNumberInputActivity.this, new OnCompleteListener<AuthResult>() {
                    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            //FirebaseUser user = task.getResult().getUser();

                            Intent veryIntent = new Intent(PhoneNumberInputActivity.this, RegisterActivity.class);
                            startActivity(veryIntent);

                        } else {
                            // Sign in failed, display a message and update the UI
                            //Log.w(TAG, "signInWithCredential:failure", task.getException());
                            mErrorText.setText("There was an error to Login");
                            mErrorText.setVisibility(View.VISIBLE);
                            /*if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                                // The verification code entered was invalid
                            }*/
                        }
                    }
                });
    }



}
