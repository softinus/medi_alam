package com.plusline.medialarm.management;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.HashSet;
import java.util.Set;

/**
 *  Option abstract class
 */
public abstract class Option {

    public interface OnChangedOptionListener {
        void onChanged();   // 어떤 옵션이 변경되도 호출됨.
    }


    protected Context context;

    private SharedPreferences preferences;
    private SharedPreferences.Editor editor;
    private Set<OnChangedOptionListener> changedOptionListeners = new HashSet<>();

    //
    //  basic constructor
    //
    protected Option() {

    }


    public void init(Context context, String optionName) {
        this.context = context;
        preferences = context.getSharedPreferences(optionName, 0);
    }


    protected boolean getBoolean(String optName, boolean defaultValue) {
        return preferences.getBoolean(optName, defaultValue);
    }

    protected void setBooleanValue(String optionName, boolean value) {
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean(optionName, value);
        editor.apply();
    }


    protected String getString(String optName, String defaultValue) {
        return preferences.getString(optName, defaultValue);
    }

    protected void setStringValue(String optName, String value) {
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(optName, value);
        editor.apply();
    }



    protected int getInt(String optName, int defaultValue) {
        return preferences.getInt(optName, defaultValue);
    }

    protected void setIntValue(String optionName, int value) {
        SharedPreferences.Editor editor = preferences.edit();
        editor.putInt(optionName, value);
        editor.apply();
    }


    public void addOnChangedOptionListener(OnChangedOptionListener listener) {
        changedOptionListeners.add(listener);
    }

    public void removeOnChangedOptionListener(OnChangedOptionListener listener) {
        changedOptionListeners.remove(listener);
    }

    public void clearListeners() {
        changedOptionListeners.clear();
    }

    protected void dispatchChangedMessage() {
        for(OnChangedOptionListener listener : changedOptionListeners) {
            listener.onChanged();
        }
    }
}
