package com.example.taruc.instacity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class feed extends AppCompatActivity{

    feedFragment feedFrag;
    activityFragment activityFrag;
    create_interfaceFragment createIntFrag;
    favouriteFragment favouriteFrag;
    profileFragment profileFrag;
    BottomNavigationView bnv;
    ViewPager vp;
    MenuItem prevMenuItem;

    private FirebaseAuth mAuth;
    private DatabaseReference usersRef;


    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {


            switch (item.getItemId()) {
                case R.id.navigation_feed:
                    vp.setCurrentItem(0);
                    break;
                case R.id.navigation_activities:
                    vp.setCurrentItem(1);
                    break;

                case R.id.navigation_create:
                    vp.setCurrentItem(2);
                    return true;
                case R.id.navigation_favourite:
                    vp.setCurrentItem(3);
                    return true;
                case R.id.navigation_profile:
                    vp.setCurrentItem(4);
                    return true;
            }
            return false;

        }
    };

    @Override

    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feed);

        mAuth = FirebaseAuth.getInstance();
        usersRef = FirebaseDatabase.getInstance().getReference().child("Users");

        bnv = (BottomNavigationView) findViewById(R.id.navigation);
        vp = (ViewPager) findViewById(R.id.pager);
        bnv.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        vp.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                if (prevMenuItem != null) {
                    prevMenuItem.setChecked(false);
                } else {
                    bnv.getMenu().getItem(0).setChecked(false);
                }
                Log.d("page", "onPageSelected: " + position);
                bnv.getMenu().getItem(position).setChecked(true);
                prevMenuItem = bnv.getMenu().getItem(position);

            }


            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });


        setupViewPager(vp);





    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if(currentUser == null){
            SendUserToLoginActivity();
        }else if(currentUser!=null){
            CheckUserExistence();
        }
    }




    private void CheckUserExistence() {
        final String current_user_id = mAuth.getCurrentUser().getUid();

        usersRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Log.d("hihi", "dataSnapshot.hasChild(current_user_id)");
                if(!dataSnapshot.hasChild(current_user_id)){

                    SendUserToSetupActivity();

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void SendUserToSetupActivity() {
        Intent setupIntent = new Intent(feed.this,SetupActivity.class);
        setupIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(setupIntent);
        finish();
    }

    private void SendUserToLoginActivity() {
        Intent loginIntent = new Intent(feed.this,LoginActivity.class);
        loginIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(loginIntent);
        finish();
    }

    private void setupViewPager(ViewPager viewPager) {
        PagerAdapter viewPagerAdapter = new PagerAdapter(getSupportFragmentManager());
        feedFrag = new feedFragment();
        activityFrag = new activityFragment();
        createIntFrag = new create_interfaceFragment();
        favouriteFrag = new favouriteFragment();
        profileFrag = new profileFragment();
        viewPagerAdapter.addFragment(feedFrag);
        viewPagerAdapter.addFragment(activityFrag);
        viewPagerAdapter.addFragment(createIntFrag);
        viewPagerAdapter.addFragment(favouriteFrag);
        viewPagerAdapter.addFragment(profileFrag);
        viewPager.setAdapter(viewPagerAdapter);
    }


    public void logout_listener(View view) {
        Intent logoutIntent = new Intent(feed.this,LoginActivity.class);
        logoutIntent.putExtra("logout",true);
        logoutIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(logoutIntent);
        FirebaseAuth.getInstance().signOut();
        finish();

    }

    public void settingListener(View view) {

    }
}
