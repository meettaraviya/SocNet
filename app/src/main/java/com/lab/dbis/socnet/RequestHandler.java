package com.lab.dbis.socnet;

import android.content.res.Resources;
import android.util.Log;

import com.lab.dbis.socnet.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;

import javax.net.ssl.HttpsURLConnection;

/**
 * Created by hell-raiser on 4/10/17.
 */

public class RequestHandler {

    private boolean storeCookie;
    private boolean sendSessionID;
    private HashMap<String, String> cookieMap;
    String SessionID;
    public RequestHandler(){
        storeCookie = false;
        sendSessionID = false;
        SessionID = null;
        cookieMap = null;
    }
    private String getQuery(HashMap<String,String> params) throws UnsupportedEncodingException {
        StringBuilder result = new StringBuilder();
        boolean first = true;

        for (HashMap.Entry<String,String> pair : params.entrySet())
        {
            if (first)
                first = false;
            else
                result.append("&");

            result.append(URLEncoder.encode(pair.getKey(), "UTF-8"));
            result.append("=");
            result.append(URLEncoder.encode(pair.getValue(), "UTF-8"));
        }

        return result.toString();
    }
    public void setSessionID(String SessionID) {
        sendSessionID = true;
        this.SessionID = SessionID;
    }
    public void doStoreCookie(boolean storeCookie) {
        this.storeCookie = storeCookie;
        this.cookieMap = new HashMap<String,String>();
    }
    private void storeCookieMap(HttpURLConnection conn) {
        if (conn == null)
            return;
        String headerName = null;
        for (int i=0; (headerName = conn.getHeaderFieldKey(i)) != null; i++) {
            if (headerName.equals("Set-Cookie")){
                String cookie = conn.getHeaderField(i);
                cookie = cookie.substring(0, cookie.indexOf(";"));
                String cookieName = cookie.substring(0, cookie.indexOf("="));
                String cookieValue = cookie.substring(cookie.indexOf("=") + 1, cookie.length());
                cookieMap.put(cookieName,cookieValue);

            }
        }

    }
    public String getCookie(String cookieName) {
        String cookieValue = cookieMap.get(cookieName);
        return cookieValue;
    }
    public JSONObject handle(String location, String method, HashMap<String,String> params){
        URL url = null;
        HttpURLConnection conn = null;
        JSONObject response = null;
        try {
            url = new URL(location);
            conn = (HttpURLConnection) url.openConnection();
            conn.setReadTimeout(1000);
            conn.setConnectTimeout(2000);
            conn.setRequestMethod(method);
            conn.setDoInput(true);
            if (sendSessionID) {
                Log.i("session-id",SessionID);
                conn.setRequestProperty("Cookie", "JSESSIONID="+SessionID);
            }
            if(method.equals("POST") && params!=null){
                conn.setDoOutput(true);
                OutputStream os = conn.getOutputStream();
                BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os, "UTF-8"));
                writer.write(getQuery(params));
                writer.flush();
                writer.close();
                os.close();
            }

            InputStream in = new BufferedInputStream(conn.getInputStream());
            BufferedReader r = new BufferedReader(new InputStreamReader(in));
            StringBuilder total = new StringBuilder();
            String line;
            while ((line = r.readLine()) != null) {
                total.append(line).append('\n');
            }
            if (storeCookie) {
                storeCookieMap(conn);
            }

            response = new JSONObject(total.toString());
            Log.i("Response", response.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        catch (java.io.IOException e){
            e.printStackTrace(); // TODO: Toast?
        }
        catch (JSONException e){
            e.printStackTrace();
        }
        finally {
            if(conn!=null)
                conn.disconnect();
        }
        return response;
    }
}
