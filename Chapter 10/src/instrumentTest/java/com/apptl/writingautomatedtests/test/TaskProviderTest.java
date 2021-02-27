package com.apptl.writingautomatedtests.test;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.SQLException;
import android.net.Uri;
import android.os.SystemClock;
import android.test.ProviderTestCase2;
import android.test.mock.MockContentResolver;
import com.apptl.writingautomatedtests.TaskProvider;

import java.util.Arrays;

/**
 * @author Erik Hellman
 */
public class TaskProviderTest extends ProviderTestCase2<TaskProvider> {
    private Uri ALL_TASKS_URI
            = Uri.parse("content://com.aptl.code.provider/task");
    private MockContentResolver mResolver;

    public TaskProviderTest() {
        super(TaskProvider.class, TaskProvider.AUTHORITY);
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        mResolver = getMockContentResolver();
    }

    public void testDatabaseCreated() {
        Cursor cursor = null;
        try {
            cursor = mResolver.
                    query(ALL_TASKS_URI, null, null, null, null);
            // Database should be empty
            assertNotNull(cursor);
            assertFalse(cursor.moveToNext());

            // Verify that we got all the columns
            String[] allColumnsSorted
                    = new String[TaskProvider.ALL_COLUMNS.length];
            System.arraycopy(TaskProvider.ALL_COLUMNS, 0,
                    allColumnsSorted, 0, allColumnsSorted.length);
            Arrays.sort(allColumnsSorted);
            String[] columnNames = cursor.getColumnNames();
            Arrays.sort(columnNames);
            assertTrue(Arrays.equals(allColumnsSorted, columnNames));
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }

    public void testCreateTaskWithDefaults() {
        ContentValues values = new ContentValues();
        values.put(TaskProvider.TaskColumns.NAME, "Do laundry");
        values.put(TaskProvider.TaskColumns.OWNER, "Erik");
        Uri insertedUri = mResolver.insert(ALL_TASKS_URI, values);
        assertNotNull(insertedUri);

        Cursor cursor = mResolver.query(insertedUri,
                null, null, null, null);
        assertNotNull(cursor);
        assertTrue(cursor.moveToNext());

        int nameColumnIdx
                = cursor.getColumnIndex(TaskProvider.TaskColumns.NAME);
        assertEquals(cursor.getString(nameColumnIdx), "Do laundry");

        int ownerColumnIdx
                = cursor.getColumnIndex(TaskProvider.TaskColumns.OWNER);
        assertEquals(cursor.getString(ownerColumnIdx), "Erik");

        int statusColumnIdx
                = cursor.getColumnIndex(TaskProvider.TaskColumns.STATUS);
        assertEquals(cursor.getInt(statusColumnIdx), 0);

        int priorityColumnIdx
                = cursor.getColumnIndex(TaskProvider.TaskColumns.PRIORITY);
        assertEquals(cursor.getInt(priorityColumnIdx), 0);

        int createdColumnIdx
                = cursor.getColumnIndex(TaskProvider.TaskColumns.CREATED);
        SystemClock.sleep(500);
        assertTrue(cursor.getLong(createdColumnIdx)
                < System.currentTimeMillis());
    }

    public void testInsertUpdateDelete() {
        ContentValues values = new ContentValues();
        values.put(TaskProvider.TaskColumns.NAME, "Do laundry");
        values.put(TaskProvider.TaskColumns.OWNER, "Erik");
        Uri insertedUri = mResolver.insert(ALL_TASKS_URI, values);
        assertNotNull(insertedUri);

        values.put(TaskProvider.TaskColumns.PRIORITY, 5);
        values.put(TaskProvider.TaskColumns.STATUS, 1);
        int updated = mResolver.update(insertedUri, values, null, null);
        assertEquals(updated, 1);

        Cursor cursor = null;
        try {
            cursor = mResolver.query(insertedUri, null, null, null, null);
            assertNotNull(cursor);
            assertTrue(cursor.moveToNext());
            int statusColumnIdx
                    = cursor.getColumnIndex(TaskProvider.TaskColumns.STATUS);
            assertEquals(cursor.getInt(statusColumnIdx), 1);
            int priorityColumnIdx
                    = cursor.getColumnIndex(TaskProvider.TaskColumns.PRIORITY);
            assertEquals(cursor.getInt(priorityColumnIdx), 5);
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }

        try {
            int deleted = mResolver.delete(insertedUri, null, null);
            assertEquals(deleted, 1);

            cursor = mResolver.query(insertedUri, null, null, null, null);
            assertNotNull(cursor);
            assertFalse(cursor.moveToNext());
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }

    public void testInsertInvalidColumn() {
        try {
            ContentValues values = new ContentValues();
            values.put(TaskProvider.TaskColumns.NAME, "Do laundry");
            values.put(TaskProvider.TaskColumns.OWNER, "Erik");
            values.put("nonExistingColumn", "someData");
            Uri uri = mResolver.insert(ALL_TASKS_URI, values);
            fail("Should throw SQLException on wrong column name.");
        } catch (Exception e) {
            assertTrue(e instanceof SQLException);
        }
    }

    public void testInvalidUri() {
        try {
            Cursor cursor = mResolver.
                    query(Uri.parse("content://"
                            + TaskProvider.AUTHORITY + "/wrongPath"),
                            null, null, null, null);
            fail("Expected IllegalArgumentException!");
        } catch (Exception e) {
            assertTrue(e instanceof IllegalArgumentException);
        }
    }
}
