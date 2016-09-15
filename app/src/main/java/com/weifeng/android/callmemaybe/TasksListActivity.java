package com.weifeng.android.callmemaybe;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;

import java.util.Calendar;
import java.util.Date;

/**
 * holds list info
 * Created by weifenghu on 8/8/2016.
 */
public class TasksListActivity extends AppCompatActivity{
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.list_item_task);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setLogo(R.mipmap.ic_launcher);
            getSupportActionBar().setDisplayUseLogoEnabled(true);
        }

        FragmentManager fm = getSupportFragmentManager();
        Fragment fragment = fm.findFragmentById(R.id.fragment);

        if (fragment == null){
            fragment = new TaskListFragment();
            fm.beginTransaction()
                    .add(R.id.fragment, fragment)
                    .commit();
        }
    }


    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        menu.clear();
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_tasks_list, menu);
        super.onPrepareOptionsMenu(menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_adds) {
            Tasks t = new Tasks();
            t.setName("");
            t.setCalendar(Calendar.getInstance());
            TaskLab.get(this).addTask(t);
            Intent i = new Intent(this, TasksActivity.class);
            i.putExtra(TasksActivityFragment.EXTRA_TASK_ID, t.getId());
            startActivity(i);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
