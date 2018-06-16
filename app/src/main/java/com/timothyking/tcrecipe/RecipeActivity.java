package com.timothyking.tcrecipe;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

public class RecipeActivity extends AppCompatActivity {
    TextView textFood;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe);

        Intent intent = getIntent();
        ArrayList<String> message = (ArrayList<String>) intent.getSerializableExtra(MainActivity.EXTRA_MESSAGE);
        textFood = (TextView) findViewById( R.id.textFood);
        textFood.setText(message.get(0));

        Log.i("Temp", message.get(0));
    }
}
