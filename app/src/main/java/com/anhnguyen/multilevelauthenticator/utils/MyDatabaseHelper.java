package com.anhnguyen.multilevelauthenticator.utils;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import android.widget.Toast;

import com.anhnguyen.multilevelauthenticator.model.Account;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;


public class MyDatabaseHelper extends SQLiteOpenHelper {

    public static final int TYPE_TEXT = 100;
    public static final int TYPE_PATTERN = 200;

    private SQLiteDatabase database;

    private static final int DB_VERSION = 1;
    private static final String DB_NAME = "dbAuthenticator.db";

    private static final String DB_PATH_SUFFIX = "/databases/";

    // Table: Authenticator
    private static final String TABLE_AUTHENTICATOR = "Authenticator";

    private static final String COLUMN_ID = "id";
    private static final String COLUMN_TEXTPASS = "textPass";
    private static final String COLUMN_PATTERN = "pattern";

    private final Context myContext;

    public MyDatabaseHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
        this.myContext = context;
        processCopy();
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
//        String script = "CREATE TABLE " + TABLE_AUTHENTICATOR + "("
//                + COLUMN_ID + " TEXT PRIMARY KEY,"
//                + COLUMN_TEXTPASS + " TEXT"  // ","
////                + COLUMN_PATTERN + " TEXT"
//                + ")";
//
//        db.execSQL(script);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
//        db.execSQL("DROP TABLE IF EXISTS " + TABLE_AUTHENTICATOR);
//        onCreate(db);
    }


    private void processCopy() {

        //private app
        File dbFile = myContext.getDatabasePath(DB_NAME);

        if (!dbFile.exists()) {
            try {
                CopyDataBaseFromAsset();
                Toast.makeText(myContext, "Copying success from Assets folder", Toast.LENGTH_LONG).show();
            } catch (Exception e) {
                Log.e("database", e.toString());
            }
        }
    }

    @Override
    public synchronized void close() {
        if (database != null)
            database.close();

        super.close();
    }

    private void openDatabase() throws SQLException {
        try {
            database = SQLiteDatabase.openDatabase(getDatabasePath(), null,
                    SQLiteDatabase.OPEN_READWRITE + SQLiteDatabase.NO_LOCALIZED_COLLATORS);
        } catch (Exception e) {

        }
    }

    private String getDatabasePath() {
        return myContext.getApplicationInfo().dataDir + DB_PATH_SUFFIX + DB_NAME;
    }

    private void CopyDataBaseFromAsset() {
        try {
            InputStream myInput;

            myInput = myContext.getAssets().open(DB_NAME);

            // Path to the just created empty db
            String outFileName = getDatabasePath();

            // if the path doesn't exist first, create it
            File f = new File(myContext.getApplicationInfo().dataDir + DB_PATH_SUFFIX);
            if (!f.exists())
                f.mkdir();

            // Open the empty db as the output stream
            OutputStream myOutput = new FileOutputStream(outFileName);

            // transfer bytes from the inputfile to the outputfile
            byte[] buffer = new byte[1024];
            int length;
            while ((length = myInput.read(buffer)) > 0) {
                myOutput.write(buffer, 0, length);
            }

            // Close the streams
            myOutput.flush();
            myOutput.close();
            myInput.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /*
     * add new user and password
     */
    public void addNewUser(Account account, int type) {
        // new user
        openDatabase();
        ContentValues values = new ContentValues();

        switch (type) {
            case TYPE_TEXT:
                values.put(COLUMN_ID, account.getId());
                values.put(COLUMN_TEXTPASS, account.getTextPass());

                database.insert(TABLE_AUTHENTICATOR, null, values);
                database.close();
                break;
            case TYPE_PATTERN:
                values.put(COLUMN_ID, account.getId());
                values.put(COLUMN_PATTERN, account.getPattern());

                database.insert(TABLE_AUTHENTICATOR, null, values);
                database.close();
                break;
        }

    }

    public boolean checkPasswordExistUser(String id, int type) {
        // user exists, this password type not exists
        if ((getPassword(id, type) == null) || getPassword(id,type).equals("")) {
            return false;
        }
        // user and this password type exist
        else {
//            Toast.makeText(myContext.getApplicationContext(), "user and password for this type already exist!", Toast.LENGTH_LONG).show();
            return true;
        }
    }

    public String getPassword(String accountID, int type) {
        openDatabase();
        Cursor cursor = database.query(
                TABLE_AUTHENTICATOR,
                null,
                COLUMN_ID + "=?", new String[]{String.valueOf(accountID)},
                null, null, null);
        if (cursor != null)
            cursor.moveToFirst();
        switch (type) {
            case TYPE_TEXT:
                database.close();
                return cursor.getString(1);
            case TYPE_PATTERN:
                database.close();
                return cursor.getString(2);
        }
        return null;
    }

    // change or add new type of password for a user
    // reset password: set pass = ""
    public void updatePassword(String id, String pass, int type) {
        openDatabase();
        ContentValues values = new ContentValues();

        switch (type) {
            case TYPE_TEXT:
                values.put(COLUMN_TEXTPASS, pass);
                database.update(TABLE_AUTHENTICATOR,
                        values,
                        COLUMN_ID + "=?",
                        new String[]{String.valueOf(id)});
                database.close();
                break;
            case TYPE_PATTERN:
                values.put(COLUMN_PATTERN, pass);
                database.update(TABLE_AUTHENTICATOR,
                        values,
                        COLUMN_ID + "=?",
                        new String[]{String.valueOf(id)});
                database.close();
                break;
        }
    }

    //delete user by id
    public void deleteUser(String id) {
        openDatabase();
        database.delete(TABLE_AUTHENTICATOR, COLUMN_ID + "=?", new String[]{id});
        database.close();
    }

    // check if id has existed
    public boolean checkID(String id) {
        openDatabase();

        String query = "SELECT * FROM " + TABLE_AUTHENTICATOR + " WHERE " + COLUMN_ID + " =?";
        Cursor cursor = database.rawQuery(query, new String[]{id});

        if (cursor.moveToFirst()) {
            Log.d("database", "column 0/1/2: "
                    + String.valueOf(cursor.getString(0)) + "/"
                    + (cursor.getString(1)) + "/"
                    + (cursor.getString(2))
            );
            cursor.close();
            database.close();
            return true;
        } else {
            Log.d("database", "not found");
            cursor.close();
            database.close();
            return false;
        }
    }

}
