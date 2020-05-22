package com.columnhack.notekeeper;

import android.app.Service;
import android.app.job.JobParameters;
import android.app.job.JobService;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.IBinder;
import android.os.PersistableBundle;

public class NoteUploaderJobService extends JobService {
    public static final String EXTRA_DATA_URI = "com.columnhack.notekeeper.extras.DATA_URI";
    private NoteUploader mNoteUploader;

    public NoteUploaderJobService() {
    }

    @Override
    public boolean onStartJob(JobParameters params) {
        // This method is called on the main application thread
        // Dispatch work done here to a different Thread
        // Either use an AsyncTask or a Handler

        // JobParameters give configuration and Identification data about the job
        // the JobParameters include the extras that were passed into this job

        // To do a background work, we need a reference to JobParameters

        AsyncTask<JobParameters, Void, Void> task = new AsyncTask<JobParameters, Void, Void>() {
            @Override
            protected Void doInBackground(JobParameters... backgroundParams) {
                JobParameters jobParams = backgroundParams[0];

                String stringDataUri = jobParams.getExtras().getString(EXTRA_DATA_URI);
                Uri dataUri = Uri.parse(stringDataUri);
                mNoteUploader.doUpload(dataUri);

                if (!mNoteUploader.isCanceled()) {
                    jobFinished(jobParams, false);
                }
                return null;
            }
        };

        mNoteUploader = new NoteUploader(this);
        task.execute(params);

        // let the JobScheduler know we've started work in the background
        // By returning true, we let the JobScheduler know that
        // our process should be allowed to keep running until
        // our background work finishes
        return true;
    }

    @Override
    public boolean onStopJob(JobParameters params) {
        mNoteUploader.cancel();

        // Reschedule the work, because it was canceled in the onstartJob method
        return true;
    }
}
