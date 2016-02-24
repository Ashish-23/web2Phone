package gcm.play.android.samples.com.gcmquickstart;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.util.Collections;
import java.util.List;

/**
 * Created by Ashish Agarwal on 23-08-2015.
 */
public class CustomAdapterImage extends RecyclerView.Adapter<CustomAdapterImage.MyViewHolder> {

    private Context context;
    private LayoutInflater inflator;
    List<InformationImage> data = Collections.emptyList();

    public CustomAdapterImage(Context context, List<InformationImage> data) {

        this.context = context;
        inflator = (LayoutInflater) context.getSystemService(context.LAYOUT_INFLATER_SERVICE);
        this.data = data;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {

        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.list_item_fetch_card_iamge, viewGroup, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MyViewHolder viewHolder, int i) {
        InformationImage current = data.get(i);

        String mesage = current.msg;
        int count = mesage.length();
        String str = " ", end = " ";
        if (count > 10) {
            str = mesage.substring(0, 6);
            end = mesage.substring(count - 4, count);

        }
        Log.v("TAG", String.valueOf(count) + str + end);
        if (str.equals("abcdef")) {
            if (end.equals(".png") || end.equals(".jpg") || end.equals(".gif")) {
                viewHolder.icon.setVisibility(View.VISIBLE);
                Log.v("TAG", mesage);

                // Find the SD Card path
                File filepath = Environment.getExternalStorageDirectory();

                // Create a new folder in SD Card
                String f1 = filepath.getAbsolutePath()
                        + "/web2phone/" + mesage;
                File f = new File(f1);
                Log.v("TAG", f1);
                Bitmap bmp = BitmapFactory.decodeFile(f.getAbsolutePath());
                viewHolder.icon.setImageBitmap(bmp);
                viewHolder.tvDate.setText(current.date);
                viewHolder.title.setVisibility(View.GONE);

            }
        } else {
            Log.v("Here", current.date);
            viewHolder.title.setText(current.msg);
            viewHolder.tvDate.setText(current.date);
        }

    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {

        TextView title, tvDate;
        ImageView icon;


        public MyViewHolder(View itemView) {
            super(itemView);
            title = (TextView) itemView.findViewById(R.id.list_item1);
            icon = (ImageView) itemView.findViewById(R.id.list_item_image3);
            tvDate = (TextView) itemView.findViewById(R.id.tvDateCard1);

           title.setOnClickListener(this);
           title.setOnLongClickListener(this);
           icon.setOnClickListener(this);
           icon.setOnLongClickListener(this);
        }

        @Override
        public void onClick(View v) {

            InformationImage current = data.get(getPosition());

            String value = current.msg;

            String ext = value;
            if (value.length() > 5)
                ext = value.substring(value.length() - 4, value.length());

            if (ext.equals(".jpg") || ext.equals(".png")) {

                loadPhoto(icon, 100, 100);
            } else {
                int sdk_Version = android.os.Build.VERSION.SDK_INT;
                if (sdk_Version < android.os.Build.VERSION_CODES.HONEYCOMB) {
                    android.text.ClipboardManager clipboard = (android.text.ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
                    clipboard.setText(value);   // Assuming that you are copying the text from a TextView
                    Toast.makeText(context, "Copied to Clipboard!", Toast.LENGTH_SHORT).show();
                } else {

                    android.content.ClipboardManager clipboard = (android.content.ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
                    android.content.ClipData clip = android.content.ClipData.newPlainText("Text Label", value);
                    clipboard.setPrimaryClip(clip);
                    Toast.makeText(context, "Copied to Clipboard!", Toast.LENGTH_SHORT).show();
                }
            }
        }

        @Override
        public boolean onLongClick(View v) {
            InformationImage current = data.get(getPosition());

            final String value = current.msg;
            PopupMenu popup = new PopupMenu(context, v);

            final MyDB enter = new MyDB(context);

            //Inflating the Popup using xml file
            popup.getMenuInflater().inflate(R.menu.popup, popup.getMenu());

            //registering popup with OnMenuItemClickListener
            popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                public boolean onMenuItemClick(MenuItem item) {
                    Toast.makeText(context, "You Clicked : " + value, Toast.LENGTH_SHORT).show();

                    if (item.getItemId() == R.id.delete) {

                        showConfirmationBox(value);
                        enter.removeRecord(value);

                        removeAt(getPosition());

                    }
                    if (item.getItemId() == R.id.Share) {
                        Intent sendIntent = new Intent();
                        sendIntent.setAction(Intent.ACTION_SEND);
                        sendIntent.putExtra(Intent.EXTRA_TEXT, value);
                        sendIntent.setType("text/plain");
                        context.startActivity(sendIntent);
                    }
                    return true;
                }
            });
            popup.show();//showing popup menu
            return true;
        }

        public void removeAt(int position) {
            data.remove(position);
            notifyItemRemoved(position);
            notifyItemRangeChanged(getPosition(), data.size());
        }

        private void loadPhoto(ImageView imageView, int width, int height) {

            ImageView tempImageView = imageView;


            AlertDialog.Builder imageDialog = new AlertDialog.Builder(context);
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(context.LAYOUT_INFLATER_SERVICE);

            View layout = inflater.inflate(R.layout.custom_fullimage_dialog,
                    (ViewGroup) itemView.findViewById(R.id.layout_root));
            ImageView image = (ImageView) layout.findViewById(R.id.fullimage);
            image.setImageDrawable(tempImageView.getDrawable());
            imageDialog.setView(layout);
            imageDialog.setPositiveButton("Ok", new DialogInterface.OnClickListener(){

                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }

            });


            imageDialog.create();
            imageDialog.show();
        }
        private void showConfirmationBox(final String value) {
            final CharSequence[] items = {"Deleted From SD card"};
            final boolean[] states = {false, false, false};
            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            builder.setTitle("Delete");
            builder.setMultiChoiceItems(items, states, new DialogInterface.OnMultiChoiceClickListener() {
                public void onClick(DialogInterface dialogInterface, int item, boolean state) {
                }
            });
            builder.setPositiveButton("Okay", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    SparseBooleanArray CheCked = ((AlertDialog) dialog).getListView().getCheckedItemPositions();
                    if (CheCked.get(CheCked.keyAt(0)) == true) {

                        String ext = value;
                        if (value.length() > 5) {
                            ext = value.substring(value.length() - 4, value.length());

                            if (ext.equals(".jpg") || ext.equals(".png"))
                                deleteImage(value);
                        }
                        Toast.makeText(context, "Deleted", Toast.LENGTH_SHORT).show();
                    }
                }
            });
            builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    dialog.cancel();
                }
            });
            builder.create().show();
        }
        private void deleteImage(String value) {

            File file = new File(android.os.Environment.getExternalStorageDirectory() + "/web2phone/" + value);

            if (file.exists()) {
                Log.v("Here", "inside delete" + value);
                file.delete();
            }
        }
    }

}
