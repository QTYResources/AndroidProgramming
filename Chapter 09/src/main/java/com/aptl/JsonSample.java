package com.aptl;

import android.util.JsonReader;
import android.util.JsonToken;
import android.util.JsonWriter;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.*;

/**
 * @author Erik Hellman
 */
public class JsonSample {

    public JSONArray readTasksFromInputStream(InputStream stream) {
        InputStreamReader reader = new InputStreamReader(stream);
        JsonReader jsonReader = new JsonReader(reader);
        JSONArray jsonArray = new JSONArray();
        try {
            jsonReader.beginArray();
            while (jsonReader.hasNext()) {
                JSONObject jsonObject
                        = readSingleTask(jsonReader);
                jsonArray.put(jsonObject);
            }
            jsonReader.endArray();
        } catch (IOException e) {
            // Ignore for brevity
        } catch (JSONException e) {
            // Ignore for brevity
        }

        return jsonArray;
    }

    private JSONObject readSingleTask(JsonReader jsonReader)
            throws IOException, JSONException {
        JSONObject jsonObject = new JSONObject();
        jsonReader.beginObject();
        JsonToken token;
        do {
            String name = jsonReader.nextName();
            if ("name".equals(name)) {
                jsonObject.put("name", jsonReader.nextString());
            } else if ("created".equals(name)) {
                jsonObject.put("created", jsonReader.nextLong());
            } else if ("owner".equals(name)) {
                jsonObject.put("owner", jsonReader.nextString());
            } else if ("priority".equals(name)) {
                jsonObject.put("priority", jsonReader.nextInt());
            } else if ("status".equals(name)) {
                jsonObject.put("status", jsonReader.nextInt());
            }

            token = jsonReader.peek();
        } while (token != null && !token.equals(JsonToken.END_OBJECT));
        jsonReader.endObject();
        return jsonObject;
    }


    public void writeJsonToStream(JSONArray array, OutputStream stream)
            throws JSONException, IOException {
        OutputStreamWriter writer = new OutputStreamWriter(stream);
        JsonWriter jsonWriter = new JsonWriter(writer);

        int arrayLength = array.length();
        jsonWriter.beginArray();
        for(int i = 0; i < arrayLength; i++) {
            JSONObject object = array.getJSONObject(i);
            jsonWriter.beginObject();
            jsonWriter.name("name").
                    value(object.getString("name"));
            jsonWriter.name("created").
                    value(object.getLong("created"));
            jsonWriter.name("priority").
                    value(object.getInt("priority"));
            jsonWriter.name("status").
            value(object.getInt("status"));
            jsonWriter.name("owner").
                    value(object.getString("owner"));
            jsonWriter.endObject();
        }
        jsonWriter.endArray();
        jsonWriter.close();
    }

}
