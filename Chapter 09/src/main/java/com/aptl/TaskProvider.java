package com.aptl;

import android.content.*;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.os.ParcelFileDescriptor;
import android.provider.BaseColumns;
import android.util.Log;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;

/**
 * @author Erik Hellman
 */
public class TaskProvider extends ContentProvider {
    public static final String AUTHORITY = "com.aptl.code.provider";
    public static final int ALL_TASKS = 10;
    public static final int SINGLE_TASK = 20;
    public static final String TASK_TABLE = "task";
    public static final String[] ALL_COLUMNS =
            new String[]{TaskColumns._ID, TaskColumns.NAME,
                    TaskColumns.CREATED, TaskColumns.PRIORITY,
                    TaskColumns.STATUS, TaskColumns.OWNER};
    public static final String DATABASE_NAME = "TaskProvider";
    public static final int DATABASE_VERSION = 2;
    public static final String TAG = "TaskProvider";
    public static final String CREATE_SQL = "CREATE TABLE "
            + TASK_TABLE + " ("
            + TaskColumns._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + TaskColumns.NAME + " TEXT NOT NULL, "
            + TaskColumns.CREATED + " INTEGER DEFAULT NOW, "
            + TaskColumns.PRIORITY + " INTEGER DEFAULT 0, "
            + TaskColumns.STATUS + " INTEGER DEFAULT 0, "
            + TaskColumns.OWNER + " TEXT, "
            + TaskColumns.DATA + " TEXT);";
    public static final String CREATED_INDEX_SQL = "CREATE INDEX "
            + TaskColumns.CREATED + "_idx ON " + TASK_TABLE + " ("
            + TaskColumns.CREATED + " ASC);";
    public static final String OWNER_INDEX_SQL = "CREATE INDEX "
            + TaskColumns.OWNER + "_idx ON " + TASK_TABLE + " ("
            + TaskColumns.CREATED + " ASC);";
    private static final String FILE_PREFIX = "com.aptl_";
    private static final String FILE_SUFFIX = ".png";
    public static UriMatcher sUriMatcher
            = new UriMatcher(UriMatcher.NO_MATCH);
    public MyDatabaseHelper mOpenHelper;

    static {
        sUriMatcher.addURI(AUTHORITY, "task", ALL_TASKS);
        sUriMatcher.addURI(AUTHORITY, "task/#", SINGLE_TASK);
    }

    public static Bitmap readBitmapFromProvider(int taskId, ContentResolver resolver)
            throws FileNotFoundException {
        Uri uri = Uri.parse("content://" + TaskProvider.AUTHORITY
                + "/" + TaskProvider.TASK_TABLE + "/" + taskId);
        return BitmapFactory.decodeStream(resolver.openInputStream(uri));
    }

    public static String[] fixSelectionArgs(String[] selectionArgs,
                                            String taskId) {
        if (selectionArgs == null) {
            selectionArgs = new String[]{taskId};
        } else {
            String[] newSelectionArg =
                    new String[selectionArgs.length + 1];
            newSelectionArg[0] = taskId;
            System.arraycopy(selectionArgs, 0,
                    newSelectionArg, 1, selectionArgs.length);
        }
        return selectionArgs;
    }

    public static String fixSelectionString(String selection) {
        selection = selection == null ? TaskColumns._ID + " = ?" :
                TaskColumns._ID + " = ? AND (" + selection + ")";
        return selection;
    }

    @Override
    public boolean onCreate() {
        mOpenHelper = new MyDatabaseHelper(getContext());
        return true;
    }

    @Override
    public Cursor query(Uri uri, String[] projection,
                        String selection, String[] selectionArgs,
                        String sortOrder) {
        projection = projection == null ? ALL_COLUMNS : projection;
        sortOrder = sortOrder == null ? TaskColumns.PRIORITY : sortOrder;
        SQLiteDatabase database = mOpenHelper.getReadableDatabase();

        if (database != null) {
            switch (sUriMatcher.match(uri)) {
                case ALL_TASKS:
                    return database.query(TASK_TABLE, projection,
                            selection, selectionArgs,
                            null, null, sortOrder);
                case SINGLE_TASK:
                    String taskId = uri.getLastPathSegment();
                    selection = fixSelectionString(selection);
                    selectionArgs = fixSelectionArgs(selectionArgs, taskId);
                    return database.query(TASK_TABLE, projection,
                            selection, selectionArgs,
                            null, null, sortOrder);
                default:
                    throw new IllegalArgumentException("Invalid Uri: " + uri);
            }
        } else {
            throw new RuntimeException("Unable to open database!");
        }
    }

    @Override
    public String getType(Uri uri) {
        return null;
    }

    private Uri doInsert(Uri uri, ContentValues values,
                         SQLiteDatabase database) {
        Uri result = null;
        switch (sUriMatcher.match(uri)) {
            case ALL_TASKS:
                long id = database.insert(TASK_TABLE, "", values);
                if (id == -1) throw new SQLException("Error inserting data: "
                        + values.toString());
                result = Uri.withAppendedPath(uri, String.valueOf(id));

                // Update row with _data field pointing at a file...
                File dataFile = Environment.getExternalStoragePublicDirectory(
                        Environment.DIRECTORY_PICTURES);
                dataFile = new File(dataFile, FILE_PREFIX + id + FILE_SUFFIX);
                ContentValues valueForFile = new ContentValues();
                valueForFile.put("_data", dataFile.getAbsolutePath());
                update(result, values, null, null);
        }
        return result;
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        SQLiteDatabase database = mOpenHelper.getWritableDatabase();
        Uri result = doInsert(uri, values, database);
        return result;
    }

    @Override
    public int bulkInsert(Uri uri, ContentValues[] contentValueses) {
        SQLiteDatabase database = mOpenHelper.getWritableDatabase();
        int count = 0;
        try {
            database.beginTransaction();
            for (ContentValues values : contentValueses) {
                Uri resultUri = doInsert(uri, values, database);
                if (resultUri != null) {
                    count++;
                } else {
                    count = 0;
                    throw new RuntimeException("Error in bulk insert");
                }
            }
            database.setTransactionSuccessful();
        } finally {
            database.endTransaction();
        }
        return count;
    }

    @Override
    public ParcelFileDescriptor openFile(Uri uri, String mode) throws FileNotFoundException {
        if(sUriMatcher.match(uri) == SINGLE_TASK)
            return openFileHelper(uri, mode);
        else
            return super.openFile(uri, mode);
    }

    @Override
    public ContentProviderResult[] applyBatch(ArrayList<ContentProviderOperation> operations)
            throws OperationApplicationException {
        SQLiteDatabase database = mOpenHelper.getWritableDatabase();
        ContentProviderResult[] result
                = new ContentProviderResult[operations.size()];
        try {
            database.beginTransaction();
            for (int i = 0; i < operations.size(); i++) {
                ContentProviderOperation operation = operations.get(i);
                result[i] = operation.apply(this, result, i);
            }
            database.setTransactionSuccessful();
        } finally {
            database.endTransaction();
        }
        return result;
    }

    @Override
    public int delete(Uri uri, String s, String[] strings) {
        // TODO
        return 0;
    }

    @Override
    public int update(Uri uri, ContentValues contentValues, String s, String[] strings) {
        // TODO
        return 0;
    }

    public interface TaskColumns extends BaseColumns {
        public static final String NAME = "name";
        public static final String CREATED = "created";
        public static final String PRIORITY = "priority";
        public static final String STATUS = "status";
        public static final String OWNER = "owner";
        public static final String DATA = "_data";
    }

    private class MyDatabaseHelper extends SQLiteOpenHelper {

        public MyDatabaseHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase database) {
            Log.d(TAG, "Create SQL : " + CREATE_SQL);
            database.execSQL(CREATE_SQL);
            database.execSQL(CREATED_INDEX_SQL);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db,
                              int oldVersion, int newVersion) {
            if (oldVersion < 2) {
                db.execSQL("ALTER TABLE " + TASK_TABLE
                        + " ADD COLUMN " + TaskColumns.OWNER + " TEXT");
                db.execSQL("ALTER TABLE " + TASK_TABLE + " ADD COLUMN " + TaskColumns.DATA + "TEXT");
                db.execSQL(OWNER_INDEX_SQL);
            }
        }
    }
}
