package com.boscotec.medmanager;

import android.os.AsyncTask;
import android.util.Log;

import com.firebase.jobdispatcher.JobParameters;
import com.firebase.jobdispatcher.JobService;


public class ReminderService extends JobService {

    private static final String TAG = ReminderService.class.getSimpleName();
    BackgroundTask mBackgroundTask;

    @Override
    public boolean onStartJob(JobParameters jobParameters) {
        Log.d(TAG, "Performing long running task in scheduled job");
        // Note: this is preformed on the main thread.
        mBackgroundTask.execute(jobParameters);
        return true;
    }

    // Stopping jobs if our job requires change.
    @Override
    public boolean onStopJob(JobParameters jobParameters) {
        // Note: return true to reschedule this job.
        Log.i("TAG", "onStopJob");
        if (mBackgroundTask != null) {
            mBackgroundTask.cancel(true);
        }
        /* true means, we're not done, please reschedule */
        return true;

        //return mBackgroundTask.stopJob(jobParameters);
    }


    private class BackgroundTask extends AsyncTask<JobParameters, Void, JobParameters[]> {

        @Override
        protected JobParameters[] doInBackground(JobParameters... params) {
            Log.d(TAG, "carrying out task in the background");
            // Do updating and stopping logical here.
            return params;
        }

        @Override
        protected void onPostExecute(JobParameters[] result) {
            for (JobParameters params : result) {
                if (!hasJobBeenStopped(params)) {
                    jobFinished(params, false);
                }else {
                    jobFinished(params, true);
                }
            }
        }

        private boolean hasJobBeenStopped(JobParameters params) {
            // Logic for checking stop.
            return false;
        }

        public boolean stopJob(JobParameters params) {
            // Logic for stopping a job. return true if job should be rescheduled.
            return false;
        }
    }

}