package com.example.android.quakereport;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.net.ssl.HttpsURLConnection;

/**
 * Helper methods related to requesting and receiving earthquake data from USGS.
 */
public final class EarthquakeRepository {
    public static ExecutorService executorService = Executors.newFixedThreadPool(1);

    private static final String LOG_TAG = EarthquakeRepository.class.getSimpleName();

    private static final String QUAKE_QUERY_URL = "https://earthquake.usgs.gov/fdsnws/event/1/query?format=geojson&eventtype=earthquake&orderby=time&minmag=5&limit=100";

    public interface UpdateUICallback {
        void onCompleted(ArrayList<Earthquake> earthquakes);
    }

    /**
     * Create a private constructor because no one should ever create a {@link EarthquakeRepository} object.
     * This class is only meant to hold static variables and methods, which can be accessed
     * directly from the class name QueryUtils (and an object instance of QueryUtils is not needed).
     */
    private EarthquakeRepository() {
    }

    /**
     * Return a list of {@link Earthquake} objects that has been built up from
     * parsing a JSON response.
     */
    public static void extractEarthquakes(final UpdateUICallback callback) {

        // Create an empty ArrayList that we can start adding earthquakes to

        executorService.execute(new Runnable() {
            @Override
            public void run() {
                ArrayList<Earthquake> earthquakes = new ArrayList<>();

                try {
                    URL queryUrl = new URL(QUAKE_QUERY_URL);
                    JSONObject quakeJson = fetchJsonFromUrl(queryUrl);
                    if (quakeJson != null) {
                        JSONArray featuresArray = quakeJson.getJSONArray("features");

                        for (int i = 0; i < featuresArray.length(); i++) {
                            JSONObject properties = featuresArray.getJSONObject(i).getJSONObject("properties");
                            double magnitude = properties.getDouble("mag");
                            String place = properties.getString("place");
                            String time = properties.getString("time");
                            String url = properties.getString("url");

                            earthquakes.add(new Earthquake(magnitude, place, time, url));
                        }
                    }

                    callback.onCompleted(earthquakes);

                } catch (MalformedURLException | JSONException e) {
                    Log.e(LOG_TAG, "Problem parsing the earthquake JSON results", e);
                } catch (IOException e) {
                    Log.e(LOG_TAG, "Exception Occurred", e);
                }
            }
        });

    }

    private static JSONObject fetchJsonFromUrl(URL url) throws IOException, JSONException {
        if (url == null) {
            return null;
        }

        HttpsURLConnection urlConnection = null;
        InputStream inputStream = null;
        StringBuilder jsonStringBuilder = new StringBuilder();

        try {
            urlConnection = (HttpsURLConnection) url.openConnection();
            urlConnection.setConnectTimeout(10000); // 10s
            urlConnection.setReadTimeout(15000); // 15s
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            if (urlConnection.getResponseCode() == 200) {
                inputStream = urlConnection.getInputStream();
                InputStreamReader inputStreamReader = new InputStreamReader(inputStream, StandardCharsets.UTF_8);
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

                String line = bufferedReader.readLine();
                while (line != null) {
                    jsonStringBuilder.append(line);
                    line = bufferedReader.readLine();
                }

            } else {
                Log.e(LOG_TAG, "Connection Error: Unexpected response code: " + urlConnection.getResponseCode());
            }
        } catch (IOException e) {
            Log.e(LOG_TAG, "Exception Occurred", e);
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }

            if (inputStream != null) {
                inputStream.close();
            }
        }

        if (jsonStringBuilder.length() == 0) {
            return null;
        } else {
            return new JSONObject(jsonStringBuilder.toString());
        }
    }
}
