package com.timothyking.tcrecipe;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.widget.ImageView;
import android.widget.TextView;
import com.squareup.picasso.Picasso;

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
        textIngredients = (TextView) findViewById(R.id.textIngredients);

        Intent intent = getIntent();
        ArrayList<String> message = (ArrayList<String>) intent.getSerializableExtra(MainActivity.EXTRA_MESSAGE);

        Picasso.get().load(message.get(3)).into(imageFood);
        textCalories.setText(message.get(1) + " Calories per Serving");
        textIngredients.setText(message.get(4));

        String food_name = message.get(0);
        String url = message.get(2);
        String linkText = "<a href=\"" + url + "\">" + food_name + "</a>";
        textFood.setText(Html.fromHtml(linkText));
        textFood.setMovementMethod(LinkMovementMethod.getInstance());
    }
}
