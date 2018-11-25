package tech.metaphor.www.metaphor;

import android.animation.Animator;
import android.animation.AnimatorInflater;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.io.Serializable;

import static tech.metaphor.www.metaphor.R.id.webView;

public class WelcomeScreenActivity extends AppCompatActivity  {



    private static int SPLASH_TIME_OUT = 4000;


    private Animation buttonAnimMain;

    private TextView textView;
    private FirebaseAuth mainAuth;

    @SuppressLint({"SetJavaScriptEnabled", "JavascriptInterface"})
    @TargetApi(Build.VERSION_CODES.O)
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.welcome_screen);
        //mainAuth = FirebaseAuth.getInstance();


        WebView webView = findViewById(R.id.webView);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.addJavascriptInterface(new WelcomeScreenActivity(), "injectedObject");
        webView.loadUrl("file:///android_asset/www/index.html");

        textView = findViewById(R.id.mytext);
        textView.setBackgroundResource(R.drawable.gradient_box);
        //textView.postDelayed((Runnable) this, 30000);
        //textView.getBackground().setVisible(false, true);


        ObjectAnimator objectAnimator = new ObjectAnimator().ofFloat(textView, View.ALPHA, 100f);
        objectAnimator.setStartDelay((long) 3200);
        objectAnimator.setRepeatCount(0);
        objectAnimator.setDuration(1000);

        //objectAnimator.setTarget(Animator.DURATION_INFINITE);


        //objectAnimator.setRepeatMode(ValueAnimator.RESTART);

        AnimatorSet setAnimation = new AnimatorSet();
        setAnimation.play(objectAnimator);

        //setupAnimation(textView, objectAnimator, R.id.mytext);
        Animation mAnim = AnimationUtils.loadAnimation(this, R.anim.alpha_anim);
        textView.startAnimation(mAnim);

        Thread timer = new Thread(){
            @Override
            public void run() {
                try {
                    sleep(3200);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }finally {
                    Intent intent = new Intent(WelcomeScreenActivity.this, MainScreenActivity.class);
                    startActivity(intent);
                    //intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK |Intent.FLAG_ACTIVITY_CLEAR_TASK);

                    finish();
                }
            }
        };
        timer.start();
    }


    /*
    private void setupAnimation(View view, final Animator animation, final int animationID){
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View animList) {
                Animator anim = AnimatorInflater.loadAnimator(WelcomeScreenActivity.this, animationID);
                anim.setTarget(animList);
                anim.start();
                //return;
            }

        });
        animation.start();
    }
    */
}
