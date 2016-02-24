package gcm.play.android.samples.com.gcmquickstart;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;

/**
 * Created by Ashish Agarwal on 09-08-2015.
 */
public class Splash extends Activity {

    public static final String MyPREFERENCES = "MyPrefs";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash_screen);

        //getting the count for how much the user has logged into app
        SharedPreferences prefs = getSharedPreferences(MyPREFERENCES, MODE_PRIVATE);
        final boolean frstTime = prefs.getBoolean("userCount", true); //0 is the default value.
            Log.v("user", String.valueOf(frstTime));
        Thread t = new Thread(){
            public void run(){
                try{
                    sleep(1000);
                }catch(InterruptedException e){
                    e.printStackTrace();
                }finally{
                   if(frstTime){
                        Intent i;
                        i = new Intent(Splash.this,SignUp.class);
                        Log.v("user", String.valueOf(frstTime));
                        startActivity(i);
                    }else{
                        Intent i;
                        i = new Intent(Splash.this,MainActivity.class);
                        Log.v("user", String.valueOf(frstTime));
                        startActivity(i);
                    }
                }
            }
        };
        t.start();
    }
}
