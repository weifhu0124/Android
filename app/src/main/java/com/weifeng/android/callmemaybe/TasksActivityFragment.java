package com.weifeng.android.callmemaybe;

import android.app.Activity;
import android.app.FragmentManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.app.Fragment;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AlertDialog;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.TimePicker;

import org.w3c.dom.Text;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.UUID;

/**
 * A fragment that holds specific information of a task
 * A placeholder fragment containing a simple view.
 */
public class TasksActivityFragment extends Fragment {
    private TextView mNameField;
    private TextView mContact;
    private TextView mEmail;
    private Button mTimePick;
    private RadioGroup mForm;
    private TextView mNote;
    private Button mSave;
    private Button mDelete;
    private Button mStartContact;
    private Button mExistingContact;
    public static final String EXTRA_TASK_ID = "com.weifeng.android.CallMeMaybe.task_id";
    private static final String DIALOG_TIME = "time";
    private Tasks mTask;
    private int mIndication;
    private static final int NO_NAME = 0;
    private static final int NO_CONTACT = 1;
    private static final int NO_TIME = 3;
    private static final int REQUEST_TIME = 0;
    private static final int REQUEST_CONTACT = 1;

    public TasksActivityFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        UUID taskId = (UUID)getActivity().getIntent().getSerializableExtra(EXTRA_TASK_ID);
        mTask = TaskLab.get(getActivity()).getTask(taskId);
        setHasOptionsMenu(true);
        setRetainInstance(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_tasks, container, false);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB){
            if (getActivity().getActionBar() != null) {
                if (NavUtils.getParentActivityName(getActivity()) != null)
                    getActivity().getActionBar().setDisplayHomeAsUpEnabled(true);
            }
        }
        // wiring up all the widgets
        mNameField = (TextView) v.findViewById(R.id.task_name);
        mNameField.setText(mTask.getName());

        mExistingContact = (Button) v.findViewById(R.id.choose_existing_contact);
        mExistingContact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI);
                startActivityForResult(i, REQUEST_CONTACT);
            }
        });
        mContact = (TextView) v.findViewById(R.id.task_contact);
        mEmail = (TextView) v.findViewById(R.id.task_contact_email);

        mForm = (RadioGroup) v.findViewById(R.id.task_form);
        RadioButton mEmailButton=(RadioButton) v.findViewById(R.id.task_email);
        mEmailButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mExistingContact.setEnabled(false);
                mContact.setVisibility(View.GONE);
                mEmail.setVisibility(View.VISIBLE);
                mEmail.setText(mTask.getEmailAddr());
            }
        });

        RadioButton mPhoneButton = (RadioButton) v.findViewById(R.id.task_phone);
        mPhoneButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mExistingContact.setEnabled(true);
                mEmail.setVisibility(View.GONE);
                mContact.setVisibility(View.VISIBLE);
                mContact.setText(mTask.getNumber());
            }
        });
;
        final RadioButton mTextButton = (RadioButton) v.findViewById(R.id.task_text);
        mTextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mExistingContact.setEnabled(true);
                mEmail.setVisibility(View.GONE);
                mContact.setVisibility(View.VISIBLE);
                mContact.setText(mTask.getNumber());
            }
        });

        mForm.check(mTask.getContackWay());
        if (mForm.getCheckedRadioButtonId() == -1){
            // no button checked yet
            mForm.check(R.id.task_phone);
        }

        // determine which mode it is now
        if (mForm.getCheckedRadioButtonId() == R.id.task_email){
            mExistingContact.setEnabled(false);
            // email mode
            mContact.setVisibility(View.GONE);
            mEmail.setVisibility(View.VISIBLE);
            mEmail.setText(mTask.getEmailAddr());
        } else {
            mExistingContact.setEnabled(true);
            // call or text mode
            mEmail.setVisibility(View.GONE);
            mContact.setVisibility(View.VISIBLE);
            mContact.setText(mTask.getNumber());
        }

        mTimePick = (Button) v.findViewById(R.id.task_time);
        mTimePick.setText(mTask.getCalendarTime());
        mTimePick.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // start a time picker dialog
                FragmentManager fm = getActivity().getFragmentManager();
                TaskTimeFragment dialog = TaskTimeFragment.newInstance(mTask.getCalendar());
                dialog.setTargetFragment(TasksActivityFragment.this, REQUEST_TIME);
                dialog.show(fm, DIALOG_TIME);
            }
        });

        mNote = (TextView) v.findViewById(R.id.task_note);
        mNote.setText(mTask.getNotes());

        mSave = (Button) v.findViewById(R.id.task_save);
        mSave.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                if (getReady()) {
                    mTask.setName(mNameField.getText().toString());
                    mTask.setContackWay(mForm.getCheckedRadioButtonId());
                    if (mForm.getCheckedRadioButtonId() == R.id.task_email)
                        mTask.setEmailAddr(mEmail.getText().toString());
                    else
                        mTask.setmNumber(mContact.getText().toString());
                    mTask.setNotes(mNote.getText().toString());
                    getActivity().finish();
                }else{
                    // create error message dialog when can't be saved
                    AlertDialog error = new AlertDialog.Builder(getActivity())
                                            .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialog, int which) {
                                                    dialog.dismiss();
                                                }
                                            })
                                            .create();
                    switch (mIndication){
                        case NO_NAME:
                            error.setTitle("Please Input the Name of the Person You Want to Contact");
                            break;
                        case NO_CONTACT:
                            error.setTitle("Please Input the Contact Number or Email Address");
                            break;
                        case NO_TIME:
                            error.setTitle("Please Pick a Time to Contact Them");
                            break;
                        default:
                            error.setTitle("Unknown Error Occurred");
                            break;
                    }
                    error.show();
                }
            }
        });

        mDelete = (Button) v.findViewById(R.id.task_delete);
        mDelete.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                // use reminder dialog to give user a second chance
                AlertDialog reminder = new AlertDialog.Builder(getActivity())
                                            .setTitle(R.string.reminder_delete)
                                            .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialog, int which) {
                                                    // confirm deletion
                                                    TaskLab.get(getActivity()).removeTask(mTask);
                                                    dialog.dismiss();
                                                    getActivity().finish();
                                                }
                                            })
                                            .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialog, int which) {
                                                    // cancel deletion
                                                    dialog.dismiss();
                                                }
                                            })
                                            .create();
                reminder.show();
            }
        });

        mStartContact = (Button) v.findViewById(R.id.task_click_to_contact);
        mStartContact.setEnabled(false);
        enableContact();
        mStartContact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (timePassed(mTask.getCalendar())) {
                    contactNow();
                    TaskLab.get(getActivity()).removeTask(mTask);
                    getActivity().finish();
                } else{
                    // create warning
                    android.app.AlertDialog warns = new android.app.AlertDialog.Builder(getActivity())
                                                        .setTitle(R.string.warning_contact)
                                                        .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                                                            @Override
                                                            public void onClick(DialogInterface dialog, int which) {
                                                                contactNow();
                                                                TaskLab.get(getActivity()).removeTask(mTask);
                                                                dialog.dismiss();
                                                                getActivity().finish();
                                                            }
                                                        })
                                                        .setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                                                            @Override
                                                            public void onClick(DialogInterface dialog, int which) {
                                                                dialog.dismiss();
                                                            }
                                                        })
                                                        .create();
                    warns.show();
                }
            }
        });
        return v;
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

    // check if it is time to contact now
    private void enableContact(){
        if (!mTask.isReady())
            return;
       mStartContact.setEnabled(timePassed(mTask.getCalendar()));
    }

    // start contacting
    private void contactNow(){
        switch (mForm.getCheckedRadioButtonId()){
            case R.id.task_phone:
                // open phone app
                Intent phoneIntent = new Intent(Intent.ACTION_DIAL);
                phoneIntent.setData(Uri.parse("tel:" + mContact.getText().toString()));
                startActivity(phoneIntent);
                break;
            case R.id.task_text:
                // open message app
                Intent messageIntent = new Intent(Intent.ACTION_VIEW);
                messageIntent.setData(Uri.parse("sms:" + mContact.getText().toString()));
                messageIntent.putExtra("sms_body", mNote.getText().toString());
                startActivity(messageIntent);
                break;
            case R.id.task_email:
                // open email app
                Intent emailIntent = new Intent(Intent.ACTION_VIEW);
                Uri data = Uri.parse("mailto:"+ mEmail.getText().toString() + "?subject=&body=" + mNote.getText().toString());
                emailIntent.setData(data);
                startActivity(emailIntent);
                break;
        }
    }

    // check if all required field are filled
    private boolean getReady(){
        mTask.setReady(true);

        if (mNameField.getText().toString().equals("")) {
            // no name input
            mTask.setReady(false);
            mIndication = NO_NAME;
        } else {
            // check which radio button is clicked
            switch(mForm.getCheckedRadioButtonId()){
                case R.id.task_email:
                    if (mEmail.getText().toString().equals("")){
                        // no email address input
                        mTask.setReady(false);
                        mIndication = NO_CONTACT;
                    }
                    break;
                case R.id.task_phone:
                case R.id.task_text:
                    if (mContact.getText().toString().equals("")){
                        // no contact number input
                        mTask.setReady(false);
                        mIndication = NO_CONTACT;
                    }
                    break;
                default:
                    break;
            }
        }
        // check time selected
        return mTask.isReady();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data){
        if (resultCode != Activity.RESULT_OK) return;
        if (requestCode == REQUEST_TIME){
            Calendar calendar = (Calendar)data.getSerializableExtra(TaskTimeFragment.EXTRA_TIME);
            mTask.setCalendar(calendar);
            mTimePick.setText(mTask.getCalendarTime());
        } else if (requestCode == REQUEST_CONTACT){
            Uri contactUri = data.getData();
            // specify which fields you want to query to return values for
            String[] queryFields = new String[]{ContactsContract.Contacts._ID};

            // perform query
            Cursor c = getActivity().getContentResolver().query(contactUri, queryFields, null, null, null);
            if (c.getCount() == 0){
                c.close();
                return;
            }

            c.moveToFirst();
            String contactID = c.getString(0);
            String contactNumber = null;
            // Using the contact ID now we will get contact phone number
            Cursor cursorPhone = getActivity().getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                    new String[]{ContactsContract.CommonDataKinds.Phone.NUMBER},

                    ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ? AND " +
                            ContactsContract.CommonDataKinds.Phone.TYPE + " = " +
                            ContactsContract.CommonDataKinds.Phone.TYPE_MOBILE,

                    new String[]{contactID},
                    null);

            if (cursorPhone.moveToFirst()) {
                contactNumber = cursorPhone.getString(cursorPhone.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
            }

            cursorPhone.close();

            if (mForm.getCheckedRadioButtonId() == R.id.task_email){
                mEmail.setText(contactNumber);
                mTask.setEmailAddr(contactNumber);
            }else{
                mContact.setText(contactNumber);
                mTask.setmNumber(contactNumber);
            }
            c.close();
        }
    }

    @Override
    public void onPause(){
        TaskLab.get(getActivity()).saveTasks();
        super.onPause();
    }

}
