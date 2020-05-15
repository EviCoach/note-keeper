package com.columnhack.notekeeper;

import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.ComponentName;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.StrictMode;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.google.android.material.navigation.NavigationView;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    public static final int NOTE_UPLOADER_JOB_ID = 1;
    private DrawerLayout mDrawerLayout;
    private Toolbar mToolbar;
    private NavigationView mNavigationView;
    private ActionBarDrawerToggle mActionBarDrawerToggle;
    private FragmentManager mFm;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        configureDrawerLayout();
        enableStrictMode(); // Enable strict mode
        loadDefaultFragment();
    }

    @Override
    protected void onResume(){
        super.onResume();

        openDrawer();
    }

    private void openDrawer() {
        // create a handler associated with the main thread
        Handler handler = new Handler(Looper.getMainLooper());
        // The work we send to the handler is placed in the message queue
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                DrawerLayout drawer = findViewById(R.id.drawer_layout);
                drawer.openDrawer(GravityCompat.START);
            }
        }, 1000);
    }

    private void enableStrictMode() {
        if(BuildConfig.DEBUG){
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
                    .detectDiskReads()
                    .detectDiskWrites()
                    .detectNetwork()
                    .penaltyLog()
                    .build();
            StrictMode.setThreadPolicy(policy);
        }
    }

    private void loadDefaultFragment() {
        mFm = getSupportFragmentManager();
        Fragment fragment = mFm.findFragmentById(R.id.fragment_container);
        if(fragment == null){
            mFm.beginTransaction()
                    .add(R.id.fragment_container, new NotesFragment())
                    .commit();
        } else {
            mFm.beginTransaction()
                    .replace(R.id.fragment_container, new NotesFragment())
                    .commit();
        }

        checkItemSelected(R.id.notes_menu);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.settings_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getItemId() == R.id.action_settings){
            startActivity(new Intent(this, SettingsActivity.class));
            return true;
        } else if(item.getItemId() == R.id.action_backup_notes){
            backupNotes();
        } else if(item.getItemId() == R.id.action_upload_notes){
            scheduleNoteUpload();
        }
        return super.onOptionsItemSelected(item);
    }

    private void scheduleNoteUpload() {
        // To schedule a job, we first need information
        // about the job, we need to use JobInfo
        // Description of the component that will handle the job
        // using ComponentName

        // componentName contains the description of the class
        // that will service our JobService component
        ComponentName componentName =
                new ComponentName(this, NoteUploaderJobService.class);
        JobInfo jobInfo = new JobInfo.Builder(NOTE_UPLOADER_JOB_ID, componentName)
                .setRequiredNetworkType(JobInfo.NETWORK_TYPE_ANY)
                .build();

        JobScheduler jobScheduler = (JobScheduler) getSystemService(JOB_SCHEDULER_SERVICE);
        jobScheduler.schedule(jobInfo);
    }

    private void backupNotes() {
        // The service is implemented by a NoteBackupService
        // so we pass in NoteBackupService.class as the second parameter
        Intent intent = new Intent(this, NoteBackupService.class);
        intent.putExtra(NoteBackupService.EXTRA_COURSE_ID, NoteBackup.ALL_COURSES);
        startService(intent);
    }

    private void configureDrawerLayout() {
        mDrawerLayout = findViewById(R.id.drawer_layout);
        mToolbar = findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);

        mNavigationView = findViewById(R.id.navigation_view);
        mNavigationView.setNavigationItemSelectedListener(this);
        mActionBarDrawerToggle = new ActionBarDrawerToggle(this,
                mDrawerLayout, mToolbar, R.string.open, R.string.close);
        mDrawerLayout.addDrawerListener(mActionBarDrawerToggle);
        mActionBarDrawerToggle.setDrawerIndicatorEnabled(true);
        mActionBarDrawerToggle.syncState();
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if(drawer.isDrawerOpen(GravityCompat.START)){
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        if(item.getItemId() == R.id.notes_menu){
            loadDefaultFragment();
            closeDrawer();
            checkItemSelected(R.id.notes_menu);
        } else if(item.getItemId() == R.id.courses_menu){
            loadCoursesFragment();
            closeDrawer();
            checkItemSelected(R.id.courses_menu);
        }
        return true;
    }

    private void checkItemSelected(int itemId) {
        NavigationView navigationView = findViewById(R.id.navigation_view);
        Menu menu = navigationView.getMenu();
        menu.findItem(itemId).setChecked(true);
    }

    private void closeDrawer() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if(drawer.isDrawerOpen(GravityCompat.START)){
            drawer.closeDrawer(GravityCompat.START);
        }
    }

    private void loadCoursesFragment() {
        mFm.beginTransaction()
                .replace(R.id.fragment_container, new CoursesFragment())
                .commit();
    }
}
