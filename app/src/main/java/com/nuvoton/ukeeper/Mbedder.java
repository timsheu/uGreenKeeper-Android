package com.nuvoton.ukeeper;

import android.content.Context;
import android.util.Base64;
import android.util.Log;

import com.goebl.david.Response;
import com.goebl.david.Webb;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by cchsu20 on 18/11/2016.
 */

public class Mbedder {
    public interface MbedderInterface {
        void payloadWithPollingText(Map<String, String> ret);
        void showToastMessage(String messenger);
        void isConnected(boolean option);
        void postAfterPut(String resource, String value);
    }
    public MbedderInterface mbedderInterface;
    private String[] keyArray = {
            "Yb0jTruOSpFrnWXAwaFf9qzoJWdWFDaDTDuX3ZOAYVYxS0LPcpD5HANbHe5COCJET5kNGrE8UwZ4rAv6ZTNM5Ymx3Nw0IevLGtzQ",
            "IIqReRdjBniF67b3Ht4k2NnG8XE3hACSeouWeJlvHgY5iqOyrbmHs56oBpehwy4PdKciUne9IQf1IWc4HKXojxkXRI7790zsibuj",
            "Bw2GI9DBxhcxLahcOQO4mWChfX6UIH4BH8y8cIqtTaUJN0wWHDqWkaovxNM47bQnizV2qgeZXMgb4Nb84txgsXAqcA2U7QM5nlX3"
    };
    private static String key = "Bearer Yb0jTruOSpFrnWXAwaFf9qzoJWdWFDaDTDuX3ZOAYVYxS0LPcpD5HANbHe5COCJET5kNGrE8UwZ4rAv6ZTNM5Ymx3Nw0IevLGtzQ";
    private static String basicURL = "https://api.connector.mbed.com";
    private static String endName = "";
    private final String TAG = "Mbedder";
    private static Mbedder mbedder = new Mbedder();
    private Mbedder() {}
    private static Webb webb;
    private static Context localContext;

    public static Mbedder getInstance(Context context){
        Mbedder.localContext = context;
        webb = Webb.create();
        webb.setBaseUri(basicURL);
        webb.setDefaultHeader("Authorization", key);
        return mbedder;
    }

    public void setupKey(int index){
        key = "Bearer " + keyArray[index];
        webb.setDefaultHeader("Authorization", key);
        Log.d(TAG, "setupKey: " + key);
    }

    public void getEndName(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                try{
                    Response<JSONArray> response = webb
                            .get("/endpoints")
                            .asJsonArray();
                    JSONArray result = response.getBody();
                    Log.d(TAG, "getEndName: " + result);
                    try{
                        JSONObject resultObject = result.getJSONObject(0);
                        String localEndName = resultObject.get("name").toString();
                        Log.d(TAG, "endName: " + localEndName);
                        getEndResources(localEndName);
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }catch (Exception e){
                    mbedderInterface.showToastMessage(localContext.getResources().getString(R.string.internet_not_avail));
                }

            }
        }).start();

    }

    public void getEndResources(final String localEndName){
        new Thread(new Runnable() {
            @Override
            public void run() {
                endName = localEndName;
                Response<JSONArray> response = webb
                        .get("/endpoints/" + endName)
                        .asJsonArray();

                JSONArray result = response.getBody();
                Log.d(TAG, "getBody: " + result);
                mbedderInterface.showToastMessage(localContext.getResources().getString(R.string.connected));
                mbedderInterface.isConnected(true);
            }
        }).start();

    }

    public void getNodeValue(final String resource){
        new Thread(new Runnable() {
            @Override
            public void run() {
                Response<JSONObject> response = webb
                        .get("/endpoints/" + endName + resource)
                        .asJsonObject();

                JSONObject result = response.getBody();
                Log.d(TAG, "getBody: " + result);
            }
        }).start();

    }

    public void openLongPolling(final String pollingText){
        new Thread(new Runnable() {
            @Override
            public void run() {
                String listNodes = "/notification/pull";
                webb.setDefaultHeader("Connection", "keep-alive");
                Response<JSONObject> response = webb
                        .get(listNodes)
                        .asJsonObject();

                JSONObject result = response.getBody();
                JSONArray resultArray;
                JSONObject asyncResult;
                Map<String, String> ret = new HashMap<>();
                String base64 = "0";
                try{
                    resultArray = (JSONArray) result.get("async-responses");
                    asyncResult = (JSONObject) resultArray.get(0);
                    base64 = asyncResult.get("payload").toString();
                    Log.d(TAG, "openLongPolling: base64: " + base64);
                    byte[] data = Base64.decode(base64, Base64.DEFAULT);
                    String payload = new String(data, "UTF-8");
                    Log.d(TAG, "openLongPolling: payload: " + payload);
                    ret.put("Payload", payload);
                }catch (Exception e){
                    try {
                        resultArray = (JSONArray) result.get("reg-updates");
                    }catch (Exception ee){
                        ee.printStackTrace();
                    }
                    ret.put("Payload", "-1");
                    e.printStackTrace();
                }
                ret.put("Category", pollingText);
                mbedderInterface.payloadWithPollingText(ret);
            }
        }).start();
    }

    public void putValueToResource(final String resource, final String value){
        Log.d(TAG, "putValueToResource: ");
        new Thread(new Runnable() {
            @Override
            public void run() {
                Response<JSONObject> response = webb
                        .put("/endpoints/" + endName + resource)
                        .body(value)
                        .asJsonObject();

                JSONObject result = response.getBody();
                Log.d(TAG, "getBody: " + result);
                try {
                    String id = result.getString("async-response-id");
                    mbedderInterface.postAfterPut(resource, value);
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        }).start();
    }

    public void postValueToResource(final String resource, final String value){
        Log.d(TAG, "postValueToResource: ");
        new Thread(new Runnable() {
            @Override
            public void run() {
                Response<JSONObject> response = webb
                        .post("/endpoints/" + endName + resource)
                        .body(value)
                        .asJsonObject();

                JSONObject result = response.getBody();
                Log.d(TAG, "getBody: " + result);
            }
        }).start();
    }
}
