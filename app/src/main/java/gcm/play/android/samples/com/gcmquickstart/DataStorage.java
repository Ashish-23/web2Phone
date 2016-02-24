package gcm.play.android.samples.com.gcmquickstart;


import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;



/**
 * Created by Ashish Agarwal on 02-08-2015.
 */
public class DataStorage  extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 4;
    private static String DB_NAME = "mydb.db";
    private static String DB_PATH ;
    public final static String EMP_TABLE="Messages"; // name of table
    public final static String EMP_TIME = "created";//time and date
    public final static String EMP_ID="_id"; // id value for employee
    public final static String EMP_NAME="value";  // name of employee
    public final static String SENDER="sender";  // sender
    private final Context mContext;
    private SQLiteDatabase myDataBase;   final static String TAG = "FetchFragemt";

    public DataStorage(Context context){
        super(context, DB_NAME,null, 1);
        mContext = context;

    }


    @Override
    public void onCreate(SQLiteDatabase db) {
        Log.v("created table","Done not yet");
        String CREATE_CONTACTS_TABLE = "CREATE TABLE " + EMP_TABLE + "("
                +EMP_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," + EMP_NAME + " TEXT,"+EMP_TIME+" VARCHAR(25), "+SENDER+" TEXT);" ;
        db.execSQL(CREATE_CONTACTS_TABLE);
        Log.v("created table","Done");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) { Log.v("Fetch Frag created table","Done upgarding");

        //db.execSQL("DROP TABLE IF EXITS "+EMP_TABLE);
        //onCreate(db);
    }

}

