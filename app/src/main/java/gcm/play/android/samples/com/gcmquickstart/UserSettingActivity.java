package gcm.play.android.samples.com.gcmquickstart;

import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Ashish Agarwal on 08-08-2015.
 */
public class UserSettingActivity extends PreferenceActivity implements Preference.OnPreferenceClickListener {

    public static final String MyPREFERENCES = "MyPrefs";
    String prob;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        addPreferencesFromResource(R.xml.settings);
        findPreference("key").setOnPreferenceClickListener(this);
        findPreference("password").setOnPreferenceClickListener(this);
    }

    @Override
    public boolean onPreferenceClick(Preference preference) {
        Log.v("FetchFrag", "prefrence " + preference.getKey());
        if (preference.getKey().equals("key")) {

            showMyDialog();
        }
        if (preference.getKey().equals("password")) {

            showMyDialogPassword();
        }
        return true;
    }

    private void showMyDialogPassword() {
        Log.v("FetchFrag", "prefrence and button");
        LayoutInflater factory = LayoutInflater.from(this);
        final View textEntryView = factory.inflate(R.layout.pref_pass, null);

        AlertDialog.Builder alert = new AlertDialog.Builder(this);

        alert.setTitle("Changing password");
        //alert.setMessage("Enter your email and password");
        // Set an EditText view to get user input
        alert.setView(textEntryView);
        AlertDialog loginPrompt = alert.create();

        final EditText input1 = (EditText) textEntryView.findViewById(R.id.old_key);
        final EditText input2 = (EditText) textEntryView.findViewById(R.id.new_key);


        alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                Log.v("FetchFrag", "prefrence and button onclick");
                String old = input1.getText().toString().trim();
                String newKey = input2.getText().toString().trim();

                Log.v("FetchFrag", "prefrence and button onclick" + newKey);
                changeMyKey(old, newKey);

            }
        });
        alert.setNegativeButton("Cancel",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        dialog.cancel();
                    }
                });

        alert.show();
    }

    private void changeMyKey(String old, String newKey) {

        if (passcrct(newKey)) {

            SharedPreferences.Editor editor = getSharedPreferences(MyPREFERENCES, MODE_PRIVATE).edit();
            editor.putString("password", newKey);
            editor.commit();
            prob = "Changed Password";
        }


        Toast.makeText(getApplicationContext(), prob,
                Toast.LENGTH_LONG).show();

    }

    private void showMyDialog() {
        Log.v("FetchFrag", "prefrence and button");
        LayoutInflater factory = LayoutInflater.from(this);
        final View textEntryView = factory.inflate(R.layout.userpasslayout, null);

        AlertDialog.Builder alert = new AlertDialog.Builder(this);

        alert.setTitle("Changing Email");
        //alert.setMessage("Enter your email and password");
        // Set an EditText view to get user input
        alert.setView(textEntryView);
        AlertDialog loginPrompt = alert.create();

        final EditText input1 = (EditText) textEntryView.findViewById(R.id.username);
        final EditText input2 = (EditText) textEntryView.findViewById(R.id.password);


        alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                Log.v("FetchFrag", "prefrence and button onclick");
                String value = input1.getText().toString().trim();
                String value1 = input2.getText().toString().trim();

                Log.v("FetchFrag", "prefrence and button onclick" + value);
                changeMyEmail(value, value1);

            }
        });
        alert.setNegativeButton("Cancel",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        dialog.cancel();
                    }
                });

        alert.show();
    }

    private void changeMyEmail(final String prev, final String email) {


        //making POST request.
        try {
            Thread thread = new Thread(new Runnable() {
                @Override
                public void run() {
                    try {

                        //Your code goes here
                        HttpClient httpClient = new DefaultHttpClient();
                        // replace with your url
                        HttpPost httpPost = new HttpPost("http://receiver.host22.com/web2phone/change_mail.php");


                        //Post Data
                        List<NameValuePair> nameValuePair = new ArrayList<NameValuePair>(2);
                        nameValuePair.add(new BasicNameValuePair("pref", email));
                        nameValuePair.add(new BasicNameValuePair("pref_prev", prev));
                        //Encoding POST data
                        httpPost.setEntity(new UrlEncodedFormEntity(nameValuePair));
                        HttpResponse response = httpClient.execute(httpPost);
                        // write response to log
                        Log.d("Http Post Response:", response.toString());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });

            thread.start();

            Toast.makeText(getApplicationContext(), "Changed Email",
                    Toast.LENGTH_LONG).show();

        } finally {

            SharedPreferences.Editor editor = getSharedPreferences(MyPREFERENCES, MODE_PRIVATE).edit();
            editor.putString("email", email);
            editor.commit();
        }
    }


    private boolean passcrct(String pass) {

        boolean num = false, chara = false, spl = false;
        if (pass.length() > 3) {
            prob = "password should be of 3 characters ";
            return false;
        } else if (pass.length() == 0) {
            return false;
        } else if (pass.matches("")) {
            prob = "You did not enter a username";
            return false;
        } else {
            char[] p = pass.toCharArray();
            int[] ascii = new int[p.length];

            for (int i = 0; i < p.length; i++) {
                ascii[i] = (int) p[i];

                Log.v("FetchFrag", p[i] + "=" + String.valueOf(ascii[i] + p.length));
                if (((ascii[i] <= 90) && (ascii[i] >= 65)) || ((ascii[i] <= 122) && (ascii[i] >= 97))) {
                    chara = true;
                } else if ((ascii[i] <= 57) && (ascii[i] >= 48)) {
                    num = true;
                } else if ((ascii[i] <= 47) && (ascii[i] >= 33) || (ascii[i] <= 64) && (ascii[i] >= 58) || (ascii[i] <= 96) && (ascii[i] >= 91) || (ascii[i] <= 126) && (ascii[i] >= 123)) {
                    spl = true;
                } else {
                    prob = "Please enter one character ,one number and a special character as password";
                    return false;
                }
            }
            if (num && chara && spl) {
                Log.v("true", "All true");
                return true;
            }
        }
        return false;
    }

}
