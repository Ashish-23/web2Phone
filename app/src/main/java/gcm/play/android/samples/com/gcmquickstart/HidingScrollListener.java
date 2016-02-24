package gcm.play.android.samples.com.gcmquickstart;

import android.os.Handler;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Created by Ashish Agarwal on 25-08-2015.
 */
public abstract class HidingScrollListener extends RecyclerView.OnScrollListener {


    @Override
   public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
        super.onScrolled(recyclerView, dx, dy);


    }

}
