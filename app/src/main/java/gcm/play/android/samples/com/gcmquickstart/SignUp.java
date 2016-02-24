package gcm.play.android.samples.com.gcmquickstart;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.gcm.GoogleCloudMessaging;

import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Ashish Agarwal on 09-08-2015.
 */
public class SignUp extends Activity implements View.OnClickListener {

    GoogleCloudMessaging gcm;
    String PROJECT_NUMBER = "356501837922", regid, mail;
    public static final String MyPREFERENCES = "MyPrefs";
    EditText etEmail, etPassword;
    Button login;
    String prob;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.signup);


        etEmail = (EditText) findViewById(R.id.loginEmail);
        etPassword = (EditText) findViewById(R.id.loginPass);

        login = (Button) findViewById(R.id.btnLogin);
        login.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {

        if (isNetworkAvailable()) {
            mail = etEmail.getText().toString();
            String pass = etPassword.getText().toString();

            if (passcrct(pass)) {
                //storing the count for how much the user has logged in (or) incrementing the count
                SharedPreferences.Editor editor = getSharedPreferences(MyPREFERENCES, MODE_PRIVATE).edit();
                editor.putString("email", mail);
                editor.putBoolean("userCount", false);
                editor.putString("password", pass);
                editor.commit();

                getRegId();
                Toast.makeText(getApplicationContext(), "Successfully Signed In", Toast.LENGTH_LONG).show();

                Intent i;
                i = new Intent(SignUp.this, MainActivity.class);
                Log.v("user", mail + pass);
                startActivity(i);
            } else {
                Toast.makeText(getApplicationContext(), prob, Toast.LENGTH_LONG).show();
            }
        } else {
            Toast.makeText(getApplicationContext(), "No Intenrnet Connextion", Toast.LENGTH_LONG).show();
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

                Log.v("FetchFrag", p[i] + "=" + String.valueOf(ascii[i]+p.length));
                if (((ascii[i] < 90) && (ascii[i] > 65)) ||((ascii[i] < 122) && (ascii[i] > 97))) {
                    chara = true;
                } else if ((ascii[i] < 57) && (ascii[i] > 48)) {
                    num = true;
                } else if ((ascii[i] < 47) && (ascii[i] > 33) ||(ascii[i]<64) &&(ascii[i]>58)||(ascii[i]<96) &&(ascii[i]>91)||(ascii[i]<126) &&(ascii[i]>123)) {
                    spl = true;
                } else {
                    prob = "Please enter one character ,one number and a special character as password";
                    return false;
                }
            }
            if(num && chara &&spl){
                Log.v("true","All true");
                return true;
            }
        }
        return false;
    }

    public void getRegId() {
        new AsyncTask<Void, Void, String>() {
            @Override
            protected String doInBackground(Void... params) {

                String msg = "";
                try {
                    if (gcm == null) {
                        gcm = GoogleCloudMessaging.getInstance(getApplicationContext());
                    }
                    regid = gcm.register(PROJECT_NUMBER);
                    msg = "Device registered, registration ID=" + regid;
                    Log.i("GCM", msg);

                    List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
                    nameValuePairs.add(new BasicNameValuePair("ID", regid));
                    nameValuePairs.add(new BasicNameValuePair("email", mail));

                    Log.e("Log Tag", "BackClass Inserted" + mail);

                    try {
                        HttpClient httpClient = new DefaultHttpClient();
                        HttpPost httpPost = new HttpPost("http://receiver.host22.com/web2phone/send.php");
                        httpPost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
                        httpClient.execute(httpPost);
                        Log.e("abc", "Log_tag");
                    } catch (ClientProtocolException e) {
                        Log.e("ClientProcotol", "here");
                        e.printStackTrace();
                    } catch (IOException e) {
                        Log.e("Log Tag", "theere");
                        e.printStackTrace();
                    } catch (Exception e) {
                        //Toast.makeText(getApplicationContext(), "Caught Exception", Toast.LENGTH_SHORT).show();
                    }

                } catch (IOException ex) {
                    msg = "Error :" + ex.getMessage();

                }
                return msg;
            }

            @Override
            protected void onPostExecute(String msg) {

            }
        }.execute(null, null, null);
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(this.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

}
