package com.timothyking.tcrecipe;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Toast;

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
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;

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
    HashMap<Integer, String> hmapIngredients = new HashMap<Integer, String>();
    CheckBox carb, protein, fat, bal;

    public void getRecipeItem(int position) {
        String calories = hmapCalories.get(position);
        String label = hmapLabel.get(position);
        String url = hmapUrl.get(position);
        String image = hmapImage.get(position);
        String ingredients = hmapIngredients.get(position);

        ArrayList<String> listStrings = new ArrayList<>();
        // List<String> listStrings = new ArrayList<String>();
        listStrings.add(label);
        listStrings.add(calories);
        listStrings.add(url);
        listStrings.add(image);
        listStrings.add(ingredients);

        // Call new activity
        Intent intent = new Intent(this, RecipeActivity.class);
        // List<String> message = listStrings;
        intent.putExtra(EXTRA_MESSAGE, listStrings);
        startActivity(intent);
    }

    public void searchRecipe (View view) {
        editSearch = (EditText) findViewById(R.id.editSearch);
        String strSearch =  (editSearch.getText().toString());

        // &q=butter&diet=low-carb&diet=high-protein

        StringBuilder result=new StringBuilder();

        if (carb.isChecked()) {
            result.append("&diet=low-carb");
        }
        if (protein.isChecked()) {
            result.append("&diet=high-protein");
        }
        if (fat.isChecked()) {
            result.append("&diet=low-fat");
        }
        if (bal.isChecked()) {
            result.append("&diet=balanced");
        }

        // Using string resource
        String myURL = getString(R.string.urlSearch) + strSearch + result;

        DownloadTask task = new DownloadTask();
        task.execute(myURL);
        Log.i(TAG, strSearch);
    }

    // ToDo, remove along with onCheckBoxClicked in xml
    public void onCheckboxClicked(View view) {
        // Is the view now checked?
        boolean checked = ((CheckBox) view).isChecked();

        // Check which checkbox was clicked
        switch(view.getId()) {
            case R.id.checkFat:
                if (checked)
                    bal.setChecked(false);
                break;
            case R.id.checkCarb:
                if (checked)
                    bal.setChecked(false);
                break;
            case R.id.checkBal:
                if (checked)
                    fat.setChecked(false);
                    carb.setChecked(false);
                    protein.setChecked(false);
                break;
            case R.id.checkProtein:
                if (checked)
                    bal.setChecked(false);
                break;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Find the ListView resource.
        mainListView = (ListView) findViewById( R.id.mainListView );
        buttonSearch = findViewById(R.id.buttonSearch);
        carb = findViewById(R.id.checkCarb);
        protein = findViewById(R.id.checkProtein);
        fat = findViewById(R.id.checkFat);
        bal = findViewById(R.id.checkBal);

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
            ListAdapter listAdapter = new ArrayAdapter<String>(MainActivity.this,
                    R.layout.simplerow, labelList) {

                // Alternate row colors
                @Override
                public View getView(int position, View listAdapter, ViewGroup parent) {
                    // Get the current item from ListView
                    View view = super.getView(position, listAdapter, parent);
                    if (position % 2 == 1) {
                        // Set a background color for ListView regular row/item
                        view.setBackgroundColor(Color.parseColor("#E6F2FF"));
                    } else {
                        // Set the background color for alternate row/item
                        // view.setBackgroundColor(Color.parseColor("#99CCFF"));
                        view.setBackgroundColor(Color.parseColor("#FFFFB3"));
                    }
                    return view;
                }
            };

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
                    String yield = recipe.getString("yield");

                    String ingredients = recipe.getString("ingredientLines");
                    // Remove brackets from ingredients
                    ingredients = ingredients.replaceAll("\\[", "").replaceAll("\\]","");
                    ingredients = ingredients.replaceAll("^\"", "");
                    ingredients = ingredients.replaceAll("\"$", "").replaceAll("\",\"", "\n");
                    ingredients = ingredients.replaceAll("\\\\", "");

                    labelList.add(i, label);

                    double calories = recipe.getDouble("calories");
                    double serving = recipe.getDouble("yield");
                    DecimalFormat df = new DecimalFormat("##,###");
                    String formattedCal = df.format(calories/serving);

                    hmapCalories.put(i, formattedCal);
                    hmapLabel.put(i, label);
                    hmapImage.put(i, image);
                    hmapUrl.put(i, url);
                    hmapIngredients.put(i, ingredients);
                }
            } catch(JSONException e) {
                Toast.makeText(MainActivity.this, "Not found, try another ingredient", Toast.LENGTH_LONG).show();
                Log.e(TAG, "error while fetching weather info", e);
            }

            // Set the ArrayAdapter as the ListView's adapter.
            mainListView.setAdapter( listAdapter );

            // Hide keyboard
            InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(editSearch.getWindowToken(), 0);


        }
    }
}
