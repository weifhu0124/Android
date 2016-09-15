package com.weifeng.android.callmemaybe;

import android.app.FragmentManager;
import android.content.ContentValues;
import android.content.Intent;
import android.graphics.Color;
import android.media.Image;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.text.Layout;
import android.view.ActionMode;
import android.view.ContextMenu;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

/**
 * A fragment that holds a list of tasks
 * Created by weifenghu on 8/8/2016.
 */
public class TaskListFragment extends ListFragment{
    private ArrayList<Tasks> mTasks;

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        getActivity().setTitle(R.string.tasks_title);
        setHasOptionsMenu(true);
        mTasks = TaskLab.get(getActivity()).getTasks();

        TaskAdapter adapter = new TaskAdapter(mTasks);
        setListAdapter(adapter);
        setRetainInstance(true);
    }

    // context menu
    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo){
        getActivity().getMenuInflater().inflate(R.menu.menu_task_list_context, menu);
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id){
        Tasks t = (Tasks)(getListAdapter()).getItem(position);

        // start TaskActivity
        Intent i = new Intent(getActivity(), TasksActivity.class);
        i.putExtra(TasksActivityFragment.EXTRA_TASK_ID, t.getId());
        startActivity(i);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState){
        View v = super.onCreateView(inflater, parent, savedInstanceState);

        ListView listView = (ListView)v.findViewById(android.R.id.list);
        // set empty view
        TextView addReminder = new TextView(getActivity());
        addReminder.setGravity(Gravity.CENTER);
        addReminder.setText(R.string.reminder);
        ((ViewGroup)listView.getParent()).addView(addReminder);
        listView.setEmptyView(addReminder);

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB)
            registerForContextMenu(listView);
        else{
            // use contextual action bar on honeycomb or higher
            listView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE_MODAL);
            listView.setMultiChoiceModeListener(new AbsListView.MultiChoiceModeListener() {
                @Override
                public void onItemCheckedStateChanged(ActionMode mode, int position, long id, boolean checked) {

                }

                @Override
                public boolean onCreateActionMode(ActionMode mode, Menu menu) {
                    MenuInflater inflater = mode.getMenuInflater();
                    inflater.inflate(R.menu.menu_task_list_context, menu);
                    return true;
                }

                @Override
                public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
                    return false;
                }

                @Override
                public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
                    if (item.getItemId() == R.id.menu_item_delete_task){
                        TaskAdapter adapter = (TaskAdapter)getListAdapter();
                        TaskLab taskLab = TaskLab.get(getActivity());
                        for (int i = adapter.getCount() - 1; i >= 0; --i){
                            if(getListView().isItemChecked(i))
                                taskLab.removeTask(adapter.getItem(i));
                        }
                        mode.finish();
                        TaskLab.get(getActivity()).saveTasks();
                        adapter.notifyDataSetChanged();
                        return true;
                    }
                    return false;
                }

                @Override
                public void onDestroyActionMode(ActionMode mode) {

                }
            });
        }
        return v;
    }

    private class TaskAdapter extends ArrayAdapter<Tasks>{
        public TaskAdapter(ArrayList<Tasks> tasks){
            super(getActivity(), 0, tasks);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = getActivity().getLayoutInflater().inflate(R.layout.list_item_task, null);
            }

            // configurate view for this task
            Tasks t = getItem(position);

            ImageView icons = (ImageView) convertView.findViewById(R.id.image_view);
            if (t.getContackWay() == R.id.task_email){
                icons.setImageResource(R.drawable.ic_email_icon);
            } else if (t.getContackWay() == R.id.task_text){
                icons.setImageResource(R.drawable.ic_imgres);
            } else{
                icons.setImageResource(R.drawable.ic_phone_icon);
            }


            TextView nameTextView = (TextView) convertView.findViewById(R.id.task_list_name);
            nameTextView.setText(t.getName());

            TextView timeTextView = (TextView) convertView.findViewById(R.id.task_list_time);
            timeTextView.setText(t.getCalendarTime());

            TextView notificationView = (TextView) convertView.findViewById(R.id.notification_box);

            // separate those that can start contacting
            if(timePassed(t.getCalendar())){
                notificationView.setBackgroundColor(Color.RED);
                notificationView.setText("Ready");
            } else{
                notificationView.setBackgroundColor(Color.TRANSPARENT);
                notificationView.setText("");
            }

            return convertView;
        }
    }

    // compare the time
    private boolean timePassed(Calendar setTime){
        Calendar current = Calendar.getInstance();
        int hourNow = current.get(Calendar.HOUR_OF_DAY);
        int minNow = current.get(Calendar.MINUTE);
        int hourSet = setTime.get(Calendar.HOUR_OF_DAY);
        int minSet = setTime.get(Calendar.MINUTE);
        if (hourNow > hourSet)
            return true;
        else if (hourNow == hourSet){
            if (minNow >= minSet)
                return true;
        }
        return false;
    }

    @Override
    public void onResume(){
        int count = getListAdapter().getCount() - 1;
        // remove the ones that do not have name
        while(count >= 0){
            Tasks t = ((TaskAdapter)getListAdapter()).getItem(count);
            if (!t.isReady()){
                ((TaskAdapter) getListAdapter()).remove(t);
            }
            --count;
        }
        ((TaskAdapter)getListAdapter()).sort(new Comparator<Tasks>() {
            @Override
            public int compare(Tasks lhs, Tasks rhs) {
                return lhs.getCalendar().compareTo(rhs.getCalendar());
            }
        });
        super.onResume();
        ((TaskAdapter)getListAdapter()).notifyDataSetChanged();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater){
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_tasks_list, menu);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item){
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo)item.getMenuInfo();
        int position = info.position;
        TaskAdapter adapter = (TaskAdapter)getListAdapter();
        Tasks t = adapter.getItem(position);

        if (item.getItemId() == R.id.menu_item_delete_task){
            TaskLab.get(getActivity()).removeTask(t);
            adapter.notifyDataSetChanged();
            return true;
        }
        return super.onContextItemSelected(item);
    }

}
