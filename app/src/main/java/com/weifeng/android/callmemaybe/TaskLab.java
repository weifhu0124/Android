package com.weifeng.android.callmemaybe;

import android.content.Context;

import java.util.ArrayList;
import java.util.UUID;

/**
 * Holds a singleton of the array list of tasks
 * Created by weifenghu on 8/8/2016.
 */
public class TaskLab {
    private ArrayList<Tasks> mTasks;
    private Context mAppContext;
    private static TaskLab sTaskLab;
    private static final String TAG = "TaskLab";
    private static final String FILENAME = "tasks.json";

    private TaskJSONSerializer mSerializer;

    private TaskLab (Context appContext){
        mAppContext = appContext;
        mSerializer = new TaskJSONSerializer(mAppContext, FILENAME);
        try{
            mTasks = mSerializer.loadTasks();
        } catch(Exception ex){
            mTasks = new ArrayList<Tasks>();
        }
    }

    public static TaskLab get(Context c){
        if(sTaskLab == null){
            sTaskLab = new TaskLab(c.getApplicationContext());
        }
        return sTaskLab;
    }

    // add a task to the list
    public void addTask(Tasks t){
        mTasks.add(t);
    }

    // remove a task from the list
    public void removeTask(Tasks t){
        mTasks.remove(t);
    }

    //getter for array list
    public ArrayList<Tasks> getTasks(){
        return mTasks;
    }

    // get the specific task
    public Tasks getTask(UUID id){
        for (Tasks t : mTasks){
            if(t.getId().equals(id))
                return t;
        }
        return null;
    }

    public boolean saveTasks(){
        try{
            mSerializer.saveTasks(mTasks);
            return true;
        } catch(Exception e){
            return false;
        }
    }
}
