package gcm.play.android.samples.com.gcmquickstart;

import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

/**
 * Created by Ashish Agarwal on 07-08-2015.
 */
public class MyPhoneListener extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {

        Bundle extras = intent.getExtras();
        String value1 = extras.getString("msg");

        int sdk_Version = android.os.Build.VERSION.SDK_INT;
        if (sdk_Version < android.os.Build.VERSION_CODES.HONEYCOMB) {
            android.text.ClipboardManager clipboard = (android.text.ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
            clipboard.setText(value1);   // Assuming that you are copying the text from a TextView
            Toast.makeText(context, "Copied to Clipboard!", Toast.LENGTH_SHORT).show();
        } else {

            android.content.ClipboardManager clipboard = (android.content.ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
            android.content.ClipData clip = android.content.ClipData.newPlainText("Text Label", value1);
            clipboard.setPrimaryClip(clip);
            Toast.makeText(context, "Copied to Clipboard!", Toast.LENGTH_SHORT).show();
        }


        String ns = Context.NOTIFICATION_SERVICE;
        NotificationManager notificationManager =
                (NotificationManager) context.getApplicationContext().getSystemService(ns);
        // notificationManager.cancel(0);  Log.v("FetchFrag", "this recieve function");
    }
}
