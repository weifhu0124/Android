package com.weifeng.android.callmemaybe;

import android.app.Activity;
import android.content.Context;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONTokener;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.ArrayList;

/**
 * Saving data to JSON file
 * Created by weifenghu on 8/13/2016.
 */
public class TaskJSONSerializer extends Activity {
    private Context mContext;
    private String mFilename;

    public TaskJSONSerializer(Context c, String f){
        mContext = c;
        mFilename = f;
    }

    public void saveTasks(ArrayList<Tasks> tasks) throws JSONException, IOException{
        // build an array in JSON
        JSONArray array = new JSONArray();

        for (Tasks t : tasks){
            array.put(t.toJSON());
        }
        // write file to disk
        Writer writer = null;
        try{
            OutputStream out = mContext.openFileOutput(mFilename, Context.MODE_PRIVATE);
            writer = new OutputStreamWriter(out);
            writer.write(array.toString());
        } finally{
            if (writer != null)
                writer.close();
        }
    }

    public ArrayList<Tasks> loadTasks() throws IOException, JSONException{
        ArrayList<Tasks> tasks = new ArrayList<Tasks>();
        BufferedReader reader = null;
        try{
            // open and read the file into a string builder
            InputStream in = mContext.openFileInput(mFilename);
            reader = new BufferedReader(new InputStreamReader(in));
            StringBuilder jsonString = new StringBuilder();
            String line = null;
            while((line = reader.readLine()) != null){
                jsonString.append(line);
            }
            // parse JSON using JSONTokener
            JSONArray array = (JSONArray) new JSONTokener(jsonString.toString()).nextValue();
            // build the array from JSON objects
            for (int i = 0; i < array.length(); ++i){
                tasks.add(new Tasks(array.getJSONObject(i)));
            }
        } catch (FileNotFoundException fnfe){
            // ignore, happens at starting
        } finally {
            if (reader != null)
                reader.close();
        }
        return tasks;
    }
}
