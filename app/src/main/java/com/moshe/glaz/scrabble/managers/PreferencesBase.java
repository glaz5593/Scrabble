package com.moshe.glaz.scrabble.managers;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;

import com.moshe.glaz.scrabble.enteties.User;

public abstract class PreferencesBase {
    private String prefKey;
    private Context context;

    protected PreferencesBase(Context context, String prefKey) {
        this.context = context;
        this.prefKey = prefKey;
    }

    private SharedPreferences sharedPreferences = null;
    private SharedPreferences.Editor editor = null;

    protected SharedPreferences getSharedPreferences() {
        if (sharedPreferences == null) {
            sharedPreferences = context.getSharedPreferences(prefKey, 0);
        }

        return sharedPreferences;
    }

    protected SharedPreferences.Editor getEditor() {
        if (editor == null) {
            editor = getSharedPreferences().edit();
        }
        return editor;
    }

    public void clear() {
        getEditor().clear();
        getEditor().commit();
    }

    public String get(String key, String defaultValue) {
        String res = getSharedPreferences().getString(key, defaultValue);
        return res;
    }

    public void put(String key, String value) {
        getEditor().putString(key, value);
        getEditor().commit();
    }

    public void putAsync(String key, String value) {
        new AsyncPutTask().execute(key, value);
    }

    private class AsyncPutTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... params) {
            String key = params[0];
            String value = params[1];
            put(key, value);
            return "";
        }

        @Override
        protected void onPostExecute(String result) {

        }
    }
}
