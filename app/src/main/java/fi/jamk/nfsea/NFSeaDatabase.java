package fi.jamk.nfsea;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by h3409 on 8.11.2016.
 */

public class NFSeaDatabase extends SQLiteOpenHelper {

    private static final String DB_NAME = "NFSea_db";
    private final String DB_TABLE = "nfseaMessages";
    private final String MESSAGE_TITLE = "messageTitle";
    private final String MESSAGE_CONTENT = "messageContent";

    public NFSeaDatabase(Context context) {
        super(context, DB_NAME, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE "+DB_TABLE+" (_id INTEGER PRIMARY KEY AUTOINCREMENT, "+MESSAGE_TITLE+" TEXT, "+MESSAGE_CONTENT+" TEXT);");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS "+DB_TABLE);
        onCreate(db);
    }
}