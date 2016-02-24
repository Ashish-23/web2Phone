/**
 * Copyright 2015 Google Inc. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package gcm.play.android.samples.com.gcmquickstart;

import android.app.ActivityManager;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.PowerManager;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.android.gms.gcm.GcmListenerService;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class MyGcmListenerService extends GcmListenerService {

    private static final String TAG = "MyGcmListenerService";
    String PackageName = "gcm.play.android.samples.com.gcmquickstart";
    String extension, s, message;

    Context context = this;

    /**
     * Called when message is received.
     *
     * @param from SenderID of the sender.
     * @param data Data bundle containing message data as key/value pairs.
     *             For Set of keys use data.keySet().
     */
    // [START receive_message]
    @Override
    public void onMessageReceived(String from, Bundle data) {
        message = data.getString("message");
        String sender = data.getString("senderId");

        Log.d(TAG, "From: " + from);
        Log.d(TAG, "Message: " + message);
        Log.d(TAG, "Sender: " + sender);

        //storing the message in the database
        MyDB enter = new MyDB(this);
        Long k = enter.createRecords(message, sender);
        int count = message.length();

        boolean img = false;

        if (count > 4) {
            extension = message.substring(count - 4, count);
            Log.v("Here", "Extension is" + extension);
            if (extension.equals(".png") || extension.equals(".jpg") || extension.equals(".gif")) {

                enter.editName(k.intValue(), extension);
                Log.v("MyGcm", extension);
                s = k.toString();
                img = true;
                new DownloadImageTask().execute(message);
                message = "Image";

            }
        }
        if (!img) {
            if (checkActivity()) {


                Intent intent = new Intent(this, MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK |
                        Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                startActivity(intent);


            } else {

                sendNotification(message);
            }
        }
    }
    // [END receive_message]


    private void sendNotification(String message) {

        // Open a new activity called GCMMessageView
        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        // Pass data to the new activity
        intent.putExtra("message", message);
        // Starts the activity on notification click
        PendingIntent pIntent = PendingIntent.getActivity(this, 0, intent,
                PendingIntent.FLAG_UPDATE_CURRENT);


        // PendingIntent pi = PendingIntent.getService(this,0,i,PendingIntent.FLAG_UPDATE_CURRENT);
        // Create the notification with a notification builder
        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        Intent i = new Intent(this, MyPhoneListener.class);
        Bundle b1 = new Bundle();

        b1.putString("msg", message);
        i.putExtras(b1);
        PendingIntent pi = PendingIntent.getBroadcast(this, 0, i, PendingIntent.FLAG_CANCEL_CURRENT);
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.drawable.ic_stat_ic_notification)
                .setContentTitle("GCM Message")
                .setContentText(message)
                .setAutoCancel(true)
                .setSound(defaultSoundUri)
                .setContentIntent(pIntent).addAction(R.drawable.ic_stat_ic_notification, "Copy", pi);


        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        notificationManager.notify(0, notificationBuilder.build());

        {
            // Wake Android Device when notification received
            PowerManager pm = (PowerManager) this
                    .getSystemService(Context.POWER_SERVICE);
            final PowerManager.WakeLock mWakelock = pm.newWakeLock(
                    PowerManager.FULL_WAKE_LOCK
                            | PowerManager.ACQUIRE_CAUSES_WAKEUP, "GCM_PUSH");
            mWakelock.acquire();

            // Timer before putting Android Device to sleep mode.
            Timer timer = new Timer();
            TimerTask task = new TimerTask() {
                public void run() {
                    mWakelock.release();
                }
            };
            timer.schedule(task, 5000);
        }

    }


    private boolean checkActivity() {
        // Get the Activity Manager
        ActivityManager manager = (ActivityManager) getSystemService(ACTIVITY_SERVICE);

        // Get a list of running tasks, we are only interested in the last one,
        // the top most so we give a 1 as parameter so we only get the topmost.
        List<ActivityManager.RunningTaskInfo> task = manager.getRunningTasks(1);

        // Get the info we need for comparison.
        ComponentName componentInfo = task.get(0).topActivity;

        // Check if it matches our package name.
        if (componentInfo.getPackageName().equals(PackageName))
            return true;

        // If not then our app is not on the foreground.
        return false;
    }

    private class DownloadImageTask extends AsyncTask<String, Integer, Integer> {

        @Override
        protected Integer doInBackground(String... params) {

            DownloadImage(params[0]);

            return 0;
        }

        @Override
        protected void onPostExecute(Integer integer) {

            if (checkActivity()) {


                Intent intent = new Intent(context, MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK |
                        Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                startActivity(intent);


            } else {
                sendNotification(message);
            }

        }
    }


    private InputStream OpenHttpConnection(final String urlString)
            throws IOException {

        InputStream in = null;
        try {
            int response = -1;

            URL url = new URL(urlString);
            URLConnection conn = url.openConnection();

            if (!(conn instanceof HttpURLConnection))
                throw new IOException("Not an HTTP connection");

            HttpURLConnection httpConn = (HttpURLConnection) conn;
            httpConn.setAllowUserInteraction(false);
            httpConn.setInstanceFollowRedirects(true);
            httpConn.setRequestMethod("GET");
            httpConn.connect();

            response = httpConn.getResponseCode();
            Log.v("Here", String.valueOf(response));
            if (response == HttpURLConnection.HTTP_OK) {
                in = httpConn.getInputStream();
            }


        } catch (IOException e) {
            e.printStackTrace();
        }

        return in;
    }

    private Bitmap DownloadImage(String URL) {

        Bitmap bitmap = null;
        InputStream in;
        try {
            in = OpenHttpConnection(URL);

            bitmap = BitmapFactory.decodeStream(in);

            // Find the SD Card path
            File filepath = Environment.getExternalStorageDirectory();

            // Create a new folder in SD Card
            File dir = new File(filepath.getAbsolutePath()
                    + "/web2phone/");
            dir.mkdirs();

            String out = "abcdef" + s + extension;
            // Create a name for the saved image
            File file = new File(dir, out);
            Log.v("Here", "Extension is" + extension);
            in.close();

            FileOutputStream output = new FileOutputStream(file);

            // Compress into png format image from 0% - 100%
            if (extension.equals(".jpg")) {
                if (bitmap.compress(Bitmap.CompressFormat.JPEG, 100, output)) {

                } else
                    Log.v(TAG, extension + "  " + "elllaa");
            } else if (extension.equals(".png")) {
                bitmap.compress(Bitmap.CompressFormat.PNG, 75, output);
            } else {
            }

            output.flush();
            output.close();

        } catch (IOException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }
        return bitmap;
    }


}
