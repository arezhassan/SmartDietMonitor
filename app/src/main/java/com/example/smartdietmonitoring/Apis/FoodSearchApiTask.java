package com.example.smartdietmonitoring.Apis;

import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class FoodSearchApiTask extends AsyncTask<String, Void, String> {

    private static final String TAG = "FoodSearchApiTask";
    private static final String API_KEY = "ae84076795f7c33efe3d3cbe0ff0aa72";
    private static final String BASE_URL = "https://api.edamam.com/api/food-database/v2/parser";

    private FoodSearchApiListener listener;

    public FoodSearchApiTask(FoodSearchApiListener listener) {
        this.listener = listener;
    }

    @Override
    protected String doInBackground(String... params) {
        String query = params[0];

        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;
        String resultJson = null;

        try {
            URL url = new URL(BASE_URL + "?ingr=" + query + "&app_id=be3b11dd&app_key=" + API_KEY);
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            InputStream inputStream = urlConnection.getInputStream();
            StringBuilder buffer = new StringBuilder();
            if (inputStream == null) {
                // Nothing to do.
                return null;
            }
            reader = new BufferedReader(new InputStreamReader(inputStream));

            String line;
            while ((line = reader.readLine()) != null) {
                buffer.append(line).append("\n");
            }

            if (buffer.length() == 0) {
                // Stream was empty. No point in parsing.
                return null;
            }
            resultJson = buffer.toString();
        } catch (IOException e) {
            Log.e(TAG, "Error ", e);
            // If the code didn't successfully get data, there's no point in attempting
            // to parse it.
            return null;
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (reader != null) {
                try {
                    reader.close();
                } catch (final IOException e) {
                    Log.e(TAG, "Error closing stream", e);
                }
            }
        }

        return resultJson;
    }

    @Override
    protected void onPostExecute(String resultJson) {
        if (resultJson != null) {
            try {
                JSONObject jsonObject = new JSONObject(resultJson);
                JSONArray parsed = jsonObject.getJSONArray("parsed");
                // Parse the JSON response and extract the relevant information
                // Here you can retrieve information such as food name, serving size, calories, etc.
                // Then you can pass this information to the listener
                listener.onFoodSearchApiResponse(parsed);
            } catch (JSONException e) {
                Log.e(TAG, "Error parsing JSON", e);
            }
        } else {
            // Handle the case where the API response is empty or invalid
        }
    }

    public interface FoodSearchApiListener {
        void onFoodSearchApiResponse(JSONArray response);
    }
}
