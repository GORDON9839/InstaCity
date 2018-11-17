package com.example.taruc.instacity;

import android.content.Intent;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

import static java.lang.Thread.sleep;

public class mainSplash extends AppCompatActivity {


    private ImageView img;
    protected  void onCreate(Bundle savedInstanceState){

        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_splash);

        img = (ImageView) findViewById(R.id.img);
        final Intent intent = new Intent(this,LoginActivity.class);

        Animation anim = AnimationUtils.loadAnimation(this, R.anim.splash_transition);
        img.startAnimation(anim);
        Thread timer = new Thread() {
            public void run(){ try

            {
                sleep(3000);
            }catch(
                    InterruptedException e)

            {
                e.printStackTrace();
            }finally

            {
                startActivity(intent);
                finish();
            }}

        };
        timer.start();
    }


}
