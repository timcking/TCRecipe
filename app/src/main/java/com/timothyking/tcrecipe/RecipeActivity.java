package com.timothyking.tcrecipe;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import com.squareup.picasso.Picasso;

import org.w3c.dom.Text;

import java.util.ArrayList;

public class RecipeActivity extends AppCompatActivity {
    TextView textFood;
    ImageView imageFood;
    TextView textCalories;
    TextView textUrl;
    TextView textIngredients;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe);
        textFood = (TextView) findViewById( R.id.textFood);
        imageFood = (ImageView) findViewById(R.id.imageFood);
        textCalories = (TextView) findViewById( R.id.textCalories);
        textUrl = (TextView) findViewById( R.id.textUrl);
        textIngredients = (TextView) findViewById(R.id.textIngredients);

        Intent intent = getIntent();
        ArrayList<String> message = (ArrayList<String>) intent.getSerializableExtra(MainActivity.EXTRA_MESSAGE);

        textFood.setText(message.get(0));
        Picasso.get().load(message.get(3)).into(imageFood);
        textCalories.setText(message.get(1) + " Calories");
        textUrl.setText(message.get(2));
        textIngredients.setText(message.get(4));
    }
}
