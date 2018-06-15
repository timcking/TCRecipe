package com.timothyking.tcrecipe;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;

public class MainActivity extends AppCompatActivity {
    private EditText editSearch;
    public  static final String TAG  = "TCRecipe";
    Button buttonSearch;

    public void searchRecipe (View view) {
        editSearch = (EditText) findViewById(R.id.editSearch);
        String strSearch =  (editSearch.getText().toString());
        DownloadTask task = new DownloadTask();

        // Using string resource
        String myURL = getString(R.string.urlSearch) + strSearch;

        task.execute(myURL);
        Log.i("***** Searched ***** ", strSearch);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Find the ListView resource.
        ListView mainListView = (ListView) findViewById( R.id.mainListView );
        buttonSearch = findViewById(R.id.buttonSearch);

        // Create and populate a List of planet names.
        String[] planets = new String[] { "Mercury", "Venus", "Earth", "Mars",
                "Jupiter", "Saturn", "Uranus", "Neptune"};
        ArrayList<String> planetList = new ArrayList<String>();
        planetList.addAll( Arrays.asList(planets) );

        // Create ArrayAdapter using the planet list.
        ListAdapter listAdapter = new ArrayAdapter<String>(this, R.layout.simplerow, planetList);

        // Add more planets. If you passed a String[] instead of a List<String>
        // into the ArrayAdapter constructor, you must not add more items.
        // Otherwise an exception will occur.
        planetList.add("Alpha");
        planetList.add("Beta");
        planetList.add("Gamma");
        planetList.add("Omega");
        planetList.add("Chi");

        // Set the ArrayAdapter as the ListView's adapter.
        mainListView.setAdapter( listAdapter );
    }

    public class DownloadTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... urls) {
            URL url;
            HttpURLConnection urlConnection = null;

            try {
                url = new URL(urls[0]);
                urlConnection = (HttpURLConnection) url.openConnection();

                InputStream in = urlConnection.getInputStream();
                BufferedReader reader = new BufferedReader(new InputStreamReader(in));
                StringBuilder result = new StringBuilder();
                String line;

                while((line = reader.readLine()) != null) {
                    result.append(line);
                }

                // TCK, never triggers postexecute
                return result.toString();

            } catch (MalformedURLException e) {
                Log.e(TAG, "Error while fetching recipe info (this should not happen really!)", e);
            } catch (IOException e) {
                Log.e(TAG, "Error while fetching recipe info", e);
            } catch (Exception e) {
                Log.e(TAG, "Unknown exception");
            } finally {
                if(urlConnection != null)
                    urlConnection.disconnect();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            try {
                JSONObject jsonObject = new JSONObject(result);
                String recipeHits = jsonObject.getString("hits");
                JSONArray arr = new JSONArray(recipeHits);

                for (int i = 0; i < arr.length(); i++) {
                    JSONObject jsonPart = arr.getJSONObject(i);
                    JSONObject recipe = jsonPart.getJSONObject("recipe");

                    String label = recipe.getString("label");
                    double calories = recipe.getDouble("calories");

                    Log.i(TAG, label);
                    // ToDo, convert to string 1,234
                    // Log.i(TAG, calories);
                }
            } catch(JSONException e) {
                Toast.makeText(MainActivity.this, "Not found, try another ingredient", Toast.LENGTH_LONG).show();
                Log.e(TAG, "error while fetching weather info", e);
            }
        }
    }
}
