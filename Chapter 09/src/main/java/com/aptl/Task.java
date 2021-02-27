package com.aptl;

import android.content.ContentValues;
import android.database.Cursor;
import android.util.Log;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

import java.io.*;
import java.lang.reflect.Type;
import java.util.Collection;
import java.util.Date;
import java.util.List;

/**
 * Class demonstrating the use of the Gson API
 *
 * @author Erik Hellman
 */
public class Task {
    private String mName;
    private String mOwner;
    private Status mStatus;
    private int mPriority;
    private Date mCreated;

    public Task() {
    }

    public Task(String name, String owner,
                Status status, int priority, Date created) {
        mName = name;
        mOwner = owner;
        mStatus = status;
        mPriority = priority;
        mCreated = created;
    }

    public String getName() {
        return mName;
    }

    public void setName(String name) {
        mName = name;
    }

    public String getOwner() {
        return mOwner;
    }

    public void setOwner(String owner) {
        mOwner = owner;
    }

    public Status getStatus() {
        return mStatus;
    }

    public void setStatus(Status status) {
        mStatus = status;
    }

    public int getPriority() {
        return mPriority;
    }

    public void setPriority(int priority) {
        mPriority = priority;
    }

    public Date getCreated() {
        return mCreated;
    }

    public void setCreated(Date created) {
        mCreated = created;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Task task = (Task) o;
        return mCreated.equals(task.mCreated) && mName.equals(task.mName);
    }

    @Override
    public int hashCode() {
        int result = mName.hashCode();
        result = 31 * result + mCreated.hashCode();
        return result;
    }

    public enum Status {
        CREATED, ASSIGNED, ONGOING, CANCELLED, COMPLETED
    }

    public static Collection<Task> readTasksFromStream(InputStream stream) {
        InputStreamReader reader = new InputStreamReader(stream);
        JsonReader jsonReader = new JsonReader(reader);
        Gson gson =  new Gson();
        Type type = new TypeToken<Collection<Task>>(){}.getType();
        return gson.fromJson(jsonReader, type);
    }

    public static void writeTasksToStream(Collection<Task> tasks, OutputStream outputStream) {
        OutputStreamWriter writer = new OutputStreamWriter(outputStream);
        JsonWriter jsonWriter = new JsonWriter(writer);
        Gson gson = new Gson();
        Type type = new TypeToken<Collection<Task>>(){}.getType();
        gson.toJson(tasks, type, jsonWriter);
    }

    public ContentValues toContentValues() {
        ContentValues values = new ContentValues();
        values.put(TaskProvider.TaskColumns.NAME, mName);
        values.put(TaskProvider.TaskColumns.OWNER, mOwner);
        values.put(TaskProvider.TaskColumns.STATUS, mStatus.ordinal());
        values.put(TaskProvider.TaskColumns.PRIORITY, mPriority);
        values.put(TaskProvider.TaskColumns.CREATED, mCreated.getTime());
        return values;
    }

    public static Task fromCursor(Cursor cursor) {
        Task task = new Task();
        int nameColumnIdx
                = cursor.getColumnIndex(TaskProvider.TaskColumns.NAME);
        task.setName(cursor.getString(nameColumnIdx));
        int ownerColumnIdx
                = cursor.getColumnIndex(TaskProvider.TaskColumns.OWNER);
        task.setOwner(cursor.getString(ownerColumnIdx));
        int statusColumnIdx
                = cursor.getColumnIndex(TaskProvider.TaskColumns.STATUS);
        int statusValue = cursor.getInt(statusColumnIdx);
        for (Status status : Status.values()) {
            if(status.ordinal() == statusValue) {
                task.setStatus(status);
            }
        }
        int priorityColumnIdx
                = cursor.getColumnIndex(TaskProvider.TaskColumns.PRIORITY);
        task.setPriority(cursor.getInt(priorityColumnIdx));
        int createdColumnIdx
                = cursor.getColumnIndex(TaskProvider.TaskColumns.CREATED);
        task.setCreated(new Date(cursor.getLong(createdColumnIdx)));
        return task;
    }


    /**
     * TODO: You need to generate the protobuf classes and
     * include the Google Protobuf lite library for this method to work
     *
     * @param inputStream
     * @return
     * @throws IOException
     */
    public static TaskProtos.Task readBrotoBufFromStream(InputStream inputStream)
            throws IOException {
        TaskProtos.Task task = TaskProtos.Task.newBuilder()
                .mergeFrom(inputStream).build();
        Log.d("ProtobufDemo", "Read Task from stream: "
                + task.getName() + ", "
                + new Date(task.getCreated()) + ", "
                + (task.hasOwner() ?
                task.getOwner().getName() : "no owner") + ", "
                + task.getStatus().name() + ", "
                + task.getPriority()
                + task.getCommentsCount() + " comments.");
        return task;
    }

    public static TaskProtos.Task buildTask(String name, Date created,
                                     String ownerName, String ownerEmail,
                                     String ownerPhone,
                                     TaskProtos.Task.Status status,
                                     int priority,
                                     List<TaskProtos.Task.Comment> comments) {
        TaskProtos.Task.Builder builder = TaskProtos.Task.newBuilder();
        builder.setName(name);
        builder.setCreated(created.getTime());
        builder.setPriority(priority);
        builder.setStatus(status);
        if(ownerName != null) {
            TaskProtos.Task.Owner.Builder ownerBuilder
                    = TaskProtos.Task.Owner.newBuilder();
            ownerBuilder.setName(ownerName);
            if(ownerEmail != null) {
                ownerBuilder.setEmail(ownerEmail);
            }
            if(ownerPhone != null) {
                ownerBuilder.setPhone(ownerPhone);
            }
            builder.setOwner(ownerBuilder);
        }

        if (comments != null) {
            builder.addAllComments(comments);
        }

        return builder.build();
    }

    public static void writeTaskToStream(TaskProtos.Task task,
                                  OutputStream outputStream)
            throws IOException {
        task.writeTo(outputStream);
    }

}
