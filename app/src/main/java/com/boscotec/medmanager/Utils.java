package com.boscotec.medmanager;

import android.app.Activity;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import com.firebase.jobdispatcher.Constraint;
import com.firebase.jobdispatcher.Driver;
import com.firebase.jobdispatcher.FirebaseJobDispatcher;
import com.firebase.jobdispatcher.GooglePlayDriver;
import com.firebase.jobdispatcher.Job;
import com.firebase.jobdispatcher.Lifetime;
import com.firebase.jobdispatcher.RetryStrategy;
import com.firebase.jobdispatcher.Trigger;

public class Utils {
    // Constant Intent String
    public static final String EXTRA_ID = "ID";

    public static void hideKeyboard(Context context) {
        InputMethodManager inputMethodManager = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        if (context instanceof Activity) {
            View view = ((Activity) context).getCurrentFocus();
            if (view != null) {
                inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
            }
        }
    }
    public static AlertDialog showDialog(final Context context, String title, String content) {
        return new AlertDialog.Builder(context)
                .setTitle(title)
                .setMessage(content)
                .setPositiveButton(context.getResources().getString(android.R.string.ok), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {}
                })
                .create();
    }

    public static void showOkDialogToDismiss(String title, String message, Activity context) {
        try {
            new AlertDialog.Builder(context).setTitle(title).setMessage(message).setPositiveButton("OK", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int whichButton) {
                    dialog.dismiss();
                }
            }).create().show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void showOkDialog(final Activity activity, String title, String message) {
        try {
            new AlertDialog.Builder(activity).setTitle(title).setMessage(message).setCancelable(false).setPositiveButton("OK", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int whichButton) {
                    dialog.dismiss();
                    activity.finish();
                }
            }).create().show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void showNotification(int notId, Context context, String title, String content, int smallIconResourceId) {
        //   Intent intent = new Intent(this, MainActivity.class);
//        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
  //      PendingIntent pendingIntent = PendingIntent.getActivity(this, 0 /* Request code */, intent,
    //            PendingIntent.FLAG_ONE_SHOT);
        PendingIntent contentIntent = PendingIntent.getActivity(context, notId, context.getPackageManager().getLaunchIntentForPackage(context.getPackageName()), PendingIntent.FLAG_CANCEL_CURRENT);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context)
                .setContentIntent(contentIntent)
                .setContentTitle(title)
                .setContentText(content)
                .setStyle(new NotificationCompat.BigTextStyle().bigText(content))
                .setSmallIcon(smallIconResourceId)
                .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
                .setOnlyAlertOnce(true)
                .setAutoCancel(true);

        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(notId, builder.build());
    }

    private static boolean sInitialized;
    synchronized public static void scheduleReminder(@NonNull final Context context){
        if(sInitialized) return;
        Driver driver = new GooglePlayDriver(context);
        FirebaseJobDispatcher dispatcher = new FirebaseJobDispatcher(driver);
        Job constraintUpdateJob = dispatcher.newJobBuilder()
                //the jobService that will be called
                .setService(ReminderService.class)
                //uniquely identifies the job
                .setTag("Reminder")
                //start between 0 and 15 minutes(900 seconds)
                .setTrigger(Trigger.executionWindow(0, 900))

                //one off job
                //.setRecurring(false)
                //don't persist past a device reboot
                //.setLifetime(Lifetime.UNTIL_NEXT_BOOT)

                .setRecurring(true)
                //persist even after reboot
                .setLifetime(Lifetime.FOREVER)

                //overwrite an existing job with the same tag
                .setReplaceCurrent(true)
                //retry with exponential backoff
                //.setRetryStrategy(RetryStrategy.DEFAULT_EXPONENTIAL)
                //constraints that need to be satisfied for the job to run
                //.setConstraints(
                        //only run when charging
                        //  Constraint.DEVICE_CHARGING,
                        //only run on an unmetered network
                //        Constraint.ON_UNMETERED_NETWORK,
                        //run on any network
                //        Constraint.ON_ANY_NETWORK
                //)
                .build();

        dispatcher.schedule(constraintUpdateJob);
        //dispatcher.mustSchedule(constraintUpdateJob);
        sInitialized = true;
    }

    public static final String ACTION_NOTIFICATION = "notification";
    public static void executeTask(Context context, String action){
        if(ACTION_NOTIFICATION.equals(action)) showNotification(0, context, "Notification", "Reminder", R.mipmap.ic_launcher_round);
    }
}