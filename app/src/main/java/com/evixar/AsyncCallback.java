package com.evixar;

public interface AsyncCallback {
    void onPreExecute();
    void onPostExecute(String result);
    void onProgressUpdate(int progress);
    void onCancelled();
}
