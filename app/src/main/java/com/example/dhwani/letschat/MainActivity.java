package com.example.dhwani.letschat;

import android.content.Intent;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private Toolbar mtoolbar;
    private ViewPager mviewPager;
    private SectionsPageAdapter msectionPager;
    private TabLayout mtabLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mAuth = FirebaseAuth.getInstance();
        mtoolbar = (Toolbar)findViewById(R.id.main_page_toolbar);
        setSupportActionBar(mtoolbar);
        getSupportActionBar().setTitle("Let's Chat");

        mviewPager = (ViewPager) findViewById(R.id.tab_pager);
        msectionPager = new SectionsPageAdapter(getSupportFragmentManager());
        mtabLayout = findViewById(R.id.main_tab);
        mtabLayout.setupWithViewPager(mviewPager);
        mviewPager.setAdapter(msectionPager);
    }

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();

        if(currentUser == null){
            sendToStart();
        }

    }

    private void sendToStart(){
        Intent startIntent = new Intent(MainActivity.this,StartActivity.class);
        startActivity(startIntent);
        finish();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.menu,menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);

        if(item.getItemId() == R.id.logout){
            mAuth.signOut();
            sendToStart();
        }
        if(item.getItemId() == R.id.acc_settings){
            Intent s_intent = new Intent(MainActivity.this,AccountSettingsActivity.class);
            startActivity(s_intent);
        }

        if(item.getItemId() == R.id.user){
            Intent s_intent = new Intent(MainActivity.this,AllUserActivity.class);
            startActivity(s_intent);
        }

        return true;
    }
}
