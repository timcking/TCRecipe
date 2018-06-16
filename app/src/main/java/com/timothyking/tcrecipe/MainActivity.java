package com.timothyking.tcrecipe;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
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
import java.io.Serializable;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    public static final String EXTRA_MESSAGE = "com.timothyking.tcrecipe.MESSAGE";
    private EditText editSearch;
    public  static final String TAG  = "TCRecipe";
    Button buttonSearch;
    ListView mainListView;
    ListAdapter listAdapter;
    HashMap<Integer, String> hmapCalories = new HashMap<Integer, String>();
    HashMap<Integer, String> hmapLabel = new HashMap<Integer, String>();
    HashMap<Integer, String> hmapImage = new HashMap<Integer, String>();
    HashMap<Integer, String> hmapUrl = new HashMap<Integer, String>();

    public void getRecipeItem(int position) {
        String calories = hmapCalories.get(position);
        String label = hmapLabel.get(position);
        String url = hmapUrl.get(position);
        String image = hmapImage.get(position);

        ArrayList<String> listStrings = new ArrayList<>();
        // List<String> listStrings = new ArrayList<String>();
        listStrings.add(label);
        listStrings.add(calories);
        listStrings.add(url);
        listStrings.add(image);

        // ToDo, call new activity
        Intent intent = new Intent(this, RecipeActivity.class);
        // List<String> message = listStrings;
        intent.putExtra(EXTRA_MESSAGE, listStrings);
        startActivity(intent);

        Toast.makeText(MainActivity.this, "URL: " + url, Toast.LENGTH_LONG).show();
    }

    public void searchRecipe (View view) {
        editSearch = (EditText) findViewById(R.id.editSearch);
        String strSearch =  (editSearch.getText().toString());
        DownloadTask task = new DownloadTask();

        // Using string resource
        String myURL = getString(R.string.urlSearch) + strSearch;

        task.execute(myURL);
        Log.i(TAG, strSearch);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Find the ListView resource.
        mainListView = (ListView) findViewById( R.id.mainListView );
        buttonSearch = findViewById(R.id.buttonSearch);

        mainListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                getRecipeItem(position);
            }
        });
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
            ArrayList<String> labelList = new ArrayList<String>();
            // Create ArrayAdapter using the label list
            ListAdapter listAdapter = new ArrayAdapter<String>(MainActivity.this, R.layout.simplerow, labelList);

            try {
                JSONObject jsonObject = new JSONObject(result);
                String recipeHits = jsonObject.getString("hits");
                JSONArray arr = new JSONArray(recipeHits);

                for (int i = 0; i < arr.length(); i++) {
                    JSONObject jsonPart = arr.getJSONObject(i);
                    JSONObject recipe = jsonPart.getJSONObject("recipe");

                    String label = recipe.getString("label");
                    String image = recipe.getString("image");
                    String url = recipe.getString("url");
                    labelList.add(i, label);

                    double calories = recipe.getDouble("calories");
                    DecimalFormat df = new DecimalFormat("##,###");
                    String formattedCal = df.format(calories);

                    hmapCalories.put(i, formattedCal);
                    hmapLabel.put(i, label);
                    hmapImage.put(i, image);
                    hmapUrl.put(i, url);
                }
            } catch(JSONException e) {
                Toast.makeText(MainActivity.this, "Not found, try another ingredient", Toast.LENGTH_LONG).show();
                Log.e(TAG, "error while fetching weather info", e);
            }

            // Set the ArrayAdapter as the ListView's adapter.
            mainListView.setAdapter( listAdapter );
        }
    }
}
