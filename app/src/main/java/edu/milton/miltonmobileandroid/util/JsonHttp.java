package edu.milton.miltonmobileandroid.util;

import android.content.Context;
import android.util.Log;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.apache.http.client.CookieStore;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Iterator;

public class JsonHttp {
    public static String LOG_TAG = JsonHttp.class.getName();
    //full client

    public static void request(
            final String url,
            final String method,
            final RequestParams params,
            final CookieStore cookies,
            final JSONArray headers,
            final JsonHttpResponseHandler handler) {
        request(url,method,params,cookies,headers,handler,null);
    }

    public static void request(
            final String url,
            final String method,
            final RequestParams params,
            final CookieStore cookies,
            final JSONArray headers,
            final JsonHttpResponseHandler handler,
            final Context context) {
        AsyncHttpClient client = new AsyncHttpClient();
        if (cookies != null) {
            client.setCookieStore(cookies);
        }
        //client.setEnableRedirects(true);
        if (headers != null) {
            for (int i = 0; i < headers.length(); i++) {
                try {
                    JSONArray hArray = headers.getJSONArray(i);
                    client.addHeader((String) hArray.get(0),(String) hArray.get(1));

                } catch (JSONException e) {
                    try {
                        JSONObject hObject = headers.getJSONObject(i);
                        for (Iterator<String> iter = hObject.keys(); hObject.keys().hasNext(); ) {
                            String key = iter.next();
                            client.addHeader(key, (String) headers.getJSONObject(i).get(key));
                        }
                    }
                    catch (JSONException x) {
                        Log.v(LOG_TAG,"Bad JSON Headers");
                    }
                }
            }
        }
        client.setUserAgent("Mozilla/5.0 (Macintosh; Intel Mac OS X 10_10_5) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/45.0.2454.99 Safari/537.36");
        if (method.equalsIgnoreCase("POST")) {
            if (context != null) {
                client.post(context,url,params,handler);
                return;
            }
            client.post(url,params,handler);
            return;
        }
        if (method.equalsIgnoreCase("PUT")) {
            if (context != null) {
                client.put(context,url,params,handler);
                return;
            }
            client.put(url,params,handler);
            return;
        }
        if (method.equalsIgnoreCase("DELETE")) {
            if (context != null) {
                client.delete(context,url,handler);
                return;
            }
            client.delete(url,handler);
            return;
        }
        if (context != null) {
            client.get(context,url,params,handler);
            return;
        }
        client.get(url,params,handler);

    }

    //basic client
    public static void request(final String url, final JsonHttpResponseHandler handler) {
        request(url, handler, null);
    }
    public static void request(final String url, final JsonHttpResponseHandler handler, Context context) {
        request(url, "GET", null, null, null, handler, context);
    }
}
