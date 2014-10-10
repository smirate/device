/**
 * 
 */
package com.evixar;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import android.os.Handler;
import android.os.Message;
import android.util.Log;

/**
 * @author Hisaharu SUZUKI
 *
 */
public class metaResolver {
	public static final void init()
	{
	    AsyncGet asyncGet = new AsyncGet(new AsyncCallback() {
            public void onPreExecute() {
                // do something
            }
            public void onProgressUpdate(int progress) {
                // do something
            }
            public void onPostExecute(String result) {
                // do something
            }
            public void onCancelled() {
                // do something
            }
        });
        asyncGet.execute("http://54.64.80.124/metaapi/default/index.json");
	}
	
    public static final void resolve(String stream, final AsyncCallback responseCallback)
    {   
    	String urlString = "http://54.64.80.124/metaapi/default/index.json?";
 	        
	    HashMap<String, String> getparameter = new HashMap<String, String>();
	    getparameter.put("stream",stream);
	        
	    for(String key : getparameter.keySet()) {
	        urlString+=String.format("%s=%s&",key, getparameter.get(key));
	    }
	    urlString=urlString.substring(0, urlString.length()-1);
	        
	    AsyncGet asyncGet = new AsyncGet(new AsyncCallback() {
            public void onPreExecute() {
                // do something
            	responseCallback.onPreExecute();
            }
            public void onProgressUpdate(int progress) {
                // do something
            	responseCallback.onProgressUpdate(progress);
            }
            public void onPostExecute(String response) {
                // do something
            	responseCallback.onPostExecute(response);
            }
            public void onCancelled() {
                // do something
            	responseCallback.onCancelled();
            }
        });
        asyncGet.execute(urlString);
    }    
}
