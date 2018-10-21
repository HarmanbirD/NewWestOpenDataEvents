package ca.bcit.myapplication;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DataBaseHelper extends SQLiteOpenHelper
{

    private static final int DATABASE_VERSION           = 1;

    private static final String DATABASE_NAME           = "UserManager.db";

    private static final String TABLE_USER              = "user";

    private static final String COLUMN_USER_ID          = "user_id";
    private static final String COLUMN_USER_NAME        = "user_name";
    private static final String COLUMN_USER_EMAIL       = "user_email";
    private static final String COLUMN_USER_PASSWORD    = "user_password";

    private String CREATE_USER_TABLE = "CREATE TABLE " + TABLE_USER + "("
            + COLUMN_USER_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
            + COLUMN_USER_NAME + " TEXT," + COLUMN_USER_EMAIL + " TEXT," + COLUMN_USER_PASSWORD
            + " TEXT" + ")";

    private String DROP_USER_TABLE = "DROP TABLE IF EXISTS " + TABLE_USER;

    public DataBaseHelper(Context context)
    {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db)
    {
        db.execSQL(CREATE_USER_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
    {
        db.execSQL(DROP_USER_TABLE);

        onCreate(db);
    }

    public void addUser(User user)
    {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues contentValues = new ContentValues();

        contentValues.put(COLUMN_USER_NAME,     user.getName());
        contentValues.put(COLUMN_USER_EMAIL,    user.getEmail());
        contentValues.put(COLUMN_USER_PASSWORD, user.getPassword());

        db.insert(TABLE_USER, null, contentValues);
        db.close();
    }

    public void updateUser(User user)
    {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues contentValues = new ContentValues();

        contentValues.put(COLUMN_USER_NAME,     user.getName());
        contentValues.put(COLUMN_USER_EMAIL,    user.getEmail());
        contentValues.put(COLUMN_USER_PASSWORD, user.getPassword());

        db.update(TABLE_USER, contentValues, COLUMN_USER_ID + " = ?",
                new String[]{String.valueOf(user.getId())});
        db.close();
    }

    public void deleteUser(User user)
    {
        SQLiteDatabase db = this.getWritableDatabase();

        db.delete(TABLE_USER, COLUMN_USER_ID + " = ?",
                new String[]{String.valueOf(user.getId())});
        db.close();
    }

    public boolean checkUser(String email)
    {
        String[] columns        = {COLUMN_USER_ID};

        SQLiteDatabase db       = this.getReadableDatabase();

        String select           = COLUMN_USER_EMAIL + " = ?";

        String[] selectionArgs  = {email};

        Cursor cursor = db.query(TABLE_USER,
                columns,
                select,
                selectionArgs,
                null,
                null,
                null);
        int cursorNumbs = cursor.getCount();
        cursor.close();
        db.close();

        if (cursorNumbs > 0)
        {
            return true;
        }

        return false;
    }

    public boolean checkUser(String email, String password)
    {
        String[] columns        = {COLUMN_USER_ID};

        SQLiteDatabase db       = this.getReadableDatabase();

        String select           = COLUMN_USER_EMAIL + " = ?" + " AND " + COLUMN_USER_PASSWORD + " = ?";

        String[] selectionArgs  = {email, password};

        Cursor cursor = db.query(TABLE_USER,
                columns,
                select,
                selectionArgs,
                null,
                null,
                null);
        int cursorNumbs = cursor.getCount();
        cursor.close();
        db.close();

        if (cursorNumbs > 0)
        {
            return true;
        }

        return false;
    }






}
