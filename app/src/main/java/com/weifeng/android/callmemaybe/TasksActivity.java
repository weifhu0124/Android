package com.weifeng.android.callmemaybe;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.app.Fragment;
import android.app.FragmentManager;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

public class TasksActivity extends AppCompatActivity {
    private static final String DIALOG_REMIND = "reminder";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tasks);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setLogo(R.mipmap.ic_launcher);
            getSupportActionBar().setDisplayUseLogoEnabled(true);
        }
        FragmentManager fm = getFragmentManager();
        Fragment mFragment = fm.findFragmentById(R.id.specific_task);

        if (mFragment == null){
            mFragment = new TasksActivityFragment();
            fm.beginTransaction()
                    .add(R.id.fragment, mFragment)
                    .commit();
        }
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        menu.clear();
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_tasks, menu);
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
        if (id == android.R.id.home) {
            if (NavUtils.getParentActivityName(this) != null){
                // use dialog to remind users to save the progress
                AlertDialog remindSave = new AlertDialog.Builder(this)
                                            .setTitle(R.string.reminder_save)
                                            .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialog, int which) {
                                                    // exit without saving
                                                    dialog.dismiss();
                                                    NavUtils.navigateUpFromSameTask(TasksActivity.this);
                                                }
                                            })
                                            .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialog, int which) {
                                                    // do not exit
                                                    dialog.dismiss();
                                                }
                                            })
                                            .create();
                remindSave.show();
            }
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


}
