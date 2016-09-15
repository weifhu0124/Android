package com.weifeng.android.callmemaybe;

import android.net.Uri;
import android.text.format.Time;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.UUID;

/**
 * Class that holds information of a specfic task
 * Created by weifenghu on 8/8/2016.
 */
public class Tasks {

    private static final String JSON_ID = "id";
    private static final String JSON_NAME = "name";
    private static final String JSON_CONTACT = "contact_Way";
    private static final String JSON_NUMBER = "contact_number";
    private static final String JSON_EMAIL = "contact_email";
    private static final String JSON_YEAR = "year";
    private static final String JSON_MONTH = "month";
    private static final String JSON_DAY = "day";
    private static final String JSON_HOUR = "hour";
    private static final String JSON_MINUTE = "minute";
    private static final String JSON_NOTE = "notes";
    private static final String JSON_READY = "ready";

    private UUID mId;
    // components for tasks
    private String mName;
    private String mNotes;
    private String mNumber;
    private int mContackWay;
    private Calendar mCalendar;
    private String mEmailAddr;
    private boolean mReady;

    public Tasks(){
        mId = UUID.randomUUID();
        mReady = false;
        mContackWay = -1;
    }

    public Tasks(JSONObject json) throws JSONException{
        mId = UUID.fromString(json.getString(JSON_ID));
        if (json.has(JSON_NAME)){
            mName = json.getString(JSON_NAME);
        }
        mContackWay = json.getInt(JSON_CONTACT);
        if (json.has(JSON_NUMBER))
            mNumber = json.getString(JSON_NUMBER);
        if (json.has(JSON_EMAIL))
            mEmailAddr = json.getString(JSON_EMAIL);
        mCalendar = Calendar.getInstance();
        mCalendar.set(json.getInt(JSON_YEAR), json.getInt(JSON_MONTH), json.getInt(JSON_DAY),
                json.getInt(JSON_HOUR), json.getInt(JSON_MINUTE));
        if (json.has(JSON_NOTE))
            mNotes = json.getString(JSON_NOTE);
        mReady = json.getBoolean(JSON_READY);
    }

    public JSONObject toJSON() throws JSONException{
        JSONObject json = new JSONObject();
        json.put(JSON_ID, mId.toString());
        json.put(JSON_NAME, mName);
        json.put(JSON_CONTACT, mContackWay);
        json.put(JSON_NUMBER, mNumber);
        json.put(JSON_EMAIL, mEmailAddr);
        json.put(JSON_YEAR, mCalendar.get(Calendar.YEAR));
        json.put(JSON_MONTH, mCalendar.get(Calendar.MONTH));
        json.put(JSON_DAY, mCalendar.get(Calendar.DAY_OF_MONTH));
        json.put(JSON_HOUR, mCalendar.get(Calendar.HOUR_OF_DAY));
        json.put(JSON_MINUTE, mCalendar.get(Calendar.MINUTE));
        json.put(JSON_NOTE, mNotes);
        json.put(JSON_READY, mReady);
        return json;
    }

    // setters and getters for private variables
    public UUID getId() {
        return mId;
    }

    public void setId(UUID Id) {
        this.mId = Id;
    }

    public String getName() {
        return mName;
    }

    public void setName(String Name) {
        this.mName = Name;
    }

    public String getNotes() {
        return mNotes;
    }

    public String getNumber() {
        return mNumber;
    }

    public void setmNumber(String Number) {
        this.mNumber = Number;
    }

    public int getContackWay() {
        return mContackWay;
    }

    public void setContackWay(int ContackWay) {
        this.mContackWay = ContackWay;
    }

    public Calendar getCalendar() {
        return mCalendar;
    }

    public String getCalendarTime(){
        SimpleDateFormat format = new SimpleDateFormat("EEEE, MMM.dd 'at' H:mm a");
        return format.format(mCalendar.getTime());
    }

    public void setCalendar(Calendar calendar) {
        this.mCalendar = calendar;
    }

    public void setNotes(String notes){
        mNotes = notes;
    }

    public void setEmailAddr(String emailAddr){
        mEmailAddr = emailAddr;
    }

    public String getEmailAddr(){
        return mEmailAddr;
    }

    public boolean isReady() {
        return mReady;
    }

    public void setReady(boolean ready) {
        this.mReady = ready;
    }

    //override to string method
    @Override
    public String toString(){
        return mName;
    }
}
