package gcm.play.android.samples.com.gcmquickstart;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.LayoutAnimationController;
import android.view.animation.TranslateAnimation;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * Created by Ashish Agarwal on 18-08-2015.
 */
public class OtherPcFragment extends Fragment {
    public static final String MyPREFERENCES = "MyPrefs";
    MyDB enter;
    private static final int RESULT_SETTINGS = 1;
    Context context;
    String sender;
    CustomAdapterImage adapter;
    final static String TAG = "FetchFragemt";
    RecyclerView recyclerView;
    String[][] forecastArray = {{" "}, {" "}};

    public OtherPcFragment(Context c) {
        context = c;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        enter = new MyDB(context);
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.fragment_menu, menu);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {


        View rootView = inflater.inflate(R.layout.activity_main_recycler, container,
                false);

        //getting the count for how much the user has logged into app
        SharedPreferences prefs = context.getSharedPreferences(MyPREFERENCES, context.MODE_PRIVATE);
        sender = prefs.getString("email", " ");

        forecastArray = enter.getRecords(sender, 2);

        if (forecastArray != null) {
            forecastArray[1] = refineTime(forecastArray[1]);

            //row = new RowData(context, forecastArray[0], forecastArray[1]);
            adapter = new CustomAdapterImage(context, getData());

            recyclerView = (RecyclerView) rootView
                    .findViewById(R.id.drawerList);

            recyclerView.setHasFixedSize(true);
            LinearLayoutManager llm = new LinearLayoutManager(context);
            llm.setOrientation(LinearLayoutManager.VERTICAL);

            scrollMyListViewToBottom();

            //setting animation
            AnimationSet set = new AnimationSet(true);
            Animation animation = new AlphaAnimation(0.0f, 1.0f);
            animation.setDuration(50);
            set.addAnimation(animation);
            animation = new TranslateAnimation(
                    Animation.RELATIVE_TO_SELF, 0.0f, Animation.RELATIVE_TO_SELF, 0.0f,
                    Animation.RELATIVE_TO_SELF, -1.0f, Animation.RELATIVE_TO_SELF, 0.0f);

            animation.setDuration(200);
            set.addAnimation(animation);

            LayoutAnimationController controller = new LayoutAnimationController(set, 0.5f);
            recyclerView.setLayoutAnimation(controller);


            recyclerView.setAdapter(adapter);
            recyclerView.setLayoutManager(llm);
        }

        return rootView;
    }

    private String[] refineTime(String[] timeString) {

        Calendar cal = Calendar.getInstance();
        int currentDay = cal.get(Calendar.DATE);
        int currentMonth = cal.get(Calendar.MONTH) + 1;
        int currentYear = cal.get(Calendar.YEAR);
        Log.v(TAG, " " + currentDay + currentMonth + currentYear);


        for (int i = 0; i < timeString.length; i++) {

            int day = Integer.parseInt(timeString[i].substring(8, 10));
            int month = Integer.parseInt(timeString[i].substring(5, 7));
            int year = Integer.parseInt(timeString[i].substring(0, 4));

            String buffer = timeString[i];

            if (year == currentYear) {
                buffer = timeString[i].substring(5, timeString[i].length());

                if (day == currentDay) {
                    buffer = timeString[i].substring(11, timeString[i].length());
                }
            }
            int hour = Integer.parseInt(timeString[i].substring(11, 13));
            if (hour > 12) {

                buffer = buffer.replace(String.valueOf(hour) + ":", String.valueOf(hour - 12) + ":");
                buffer = buffer + " PM";
            }
            timeString[i] = buffer;
        }
        return timeString;
    }

    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();
        if (id == R.id.refresh) {
            refreshApp();
            return true;
        } else if (id == R.id.deleteAll) {
            enter.deleteAllRecords();
            refreshApp();
            return true;
        } else if (id == R.id.setiings) {
            Intent i = new Intent(context, UserSettingActivity.class);
            startActivityForResult(i, RESULT_SETTINGS);
            return true;
        } else {
            return super.onOptionsItemSelected(item);
        }
    }


    public void refreshApp() {
        Log.v(TAG, "Refreshing....");
        forecastArray = enter.getRecords(sender, 1);
        adapter = new CustomAdapterImage(context, getData());

        recyclerView.setAdapter(null);
        recyclerView.setAdapter(adapter);
    }

    private void scrollMyListViewToBottom() {
        recyclerView.post(new Runnable() {
            @Override
            public void run() {
                recyclerView.scrollToPosition(forecastArray[0].length - 1);
            }
        });


    }

    public List<InformationImage> getData() {
        List<InformationImage> data = new ArrayList<>();

        String[] text = forecastArray[0];
        String[] date = forecastArray[1];

        for (int i = 0; i < text.length; i++) {

            InformationImage current = new InformationImage();
            // current.itemId = icons[i];
            current.msg = text[i];
            current.date = date[i];
            data.add(current);
        }
        return data;
    }
}