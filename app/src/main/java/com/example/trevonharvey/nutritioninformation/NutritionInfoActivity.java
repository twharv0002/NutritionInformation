package com.example.trevonharvey.nutritioninformation;

import android.annotation.TargetApi;
import android.app.FragmentManager;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.ProgressBar;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;

public class NutritionInfoActivity extends AppCompatActivity {

    private Food currentFood;
    private String ndbno;
    private TextView nameTextView;
    private TextView fatTextView;
    private TextView proteinTextView;
    private TextView carbTextView;
    private TextView caloriesTextView;
    private TextView sugarTextView;
    private TextView sodiumTextView;
    private TextView potassiumTextView;
    private TextView fiberTextView;
    private ProgressBar progressBar;
    private String names;
    private android.support.v4.app.FragmentManager fm;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nutrition_info);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        nameTextView = (TextView)findViewById(R.id.nameTextView);
        fatTextView = (TextView)findViewById(R.id.fatTextView);
        proteinTextView = (TextView)findViewById(R.id.proteinTextView);
        carbTextView = (TextView)findViewById(R.id.carbTextView);
        caloriesTextView = (TextView)findViewById(R.id.caloriesTextView);
        sugarTextView = (TextView)findViewById(R.id.sugarTextView);
        sodiumTextView = (TextView)findViewById(R.id.sodiumTextView);
        fiberTextView = (TextView)findViewById(R.id.fiberTextView);
        potassiumTextView = (TextView)findViewById(R.id.potassiumTextView);
        progressBar = (ProgressBar)findViewById(R.id.progressBar);

        fm = getSupportFragmentManager();

        Intent intent = getIntent();
        ndbno = intent.getStringExtra("com.example.trevonharvey.nutritioninformation.MESSAGE");

        URL url = createFoodURL(ndbno);

        if(url != null){
            GetFoodTaskSearch getFoodTaskSearch = new GetFoodTaskSearch();
            getFoodTaskSearch.execute(url);
        }
        else{
            nameTextView.setText("Failed");
        }

    }

    private URL createFoodURL(String ndbno){
        String apiKey = "lIRAsCwnPnhiFERZWX0FzK6tICsm0r86EkWFSMVQ";
        String baseURL = "http://api.nal.usda.gov/ndb/reports/?ndbno=";

        try{
            String urlString = baseURL + URLEncoder.encode(ndbno, "UTF-8") +
                    "&type=b&format=json&api_key=" + apiKey;
            return new URL(urlString);
        }
        catch(Exception e){
            e.printStackTrace();
        }

        return null;
    }

    private class GetFoodTaskSearch extends AsyncTask<URL, Integer, JSONObject> {
        @TargetApi(Build.VERSION_CODES.KITKAT)
        @Override
        protected  JSONObject doInBackground(URL...params){
            HttpURLConnection connection = null;

            try{
                connection = (HttpURLConnection)params[0].openConnection();
                int response = connection.getResponseCode();

                if(response == HttpURLConnection.HTTP_OK){
                    StringBuilder builder = new StringBuilder();

                    try(BufferedReader reader = new BufferedReader(
                            new InputStreamReader(connection.getInputStream())
                    )){
                        String line;

                        while ((line = reader.readLine()) != null) {
                            builder.append(line);
                        }
                    }
                    catch(IOException e){
                        e.printStackTrace();
                    }

                    return new JSONObject(builder.toString());
                }
                else{

                }
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
            finally {
                connection.disconnect();
            }

            return  null;
        }

        @Override
        protected void onProgressUpdate(Integer... progress){
            progressBar.setProgress(progress[0]);
        }

        @Override
        protected void onPostExecute(JSONObject food){

            try{
                convertJSONResults(food);

                nameTextView.setText(currentFood.getName());
                caloriesTextView.setText(currentFood.getCalories());
                fatTextView.setText(currentFood.getFat() + "g");
                proteinTextView.setText(currentFood.getProtein() + "g" );
                carbTextView.setText(currentFood.getCarb() + "g");
                sugarTextView.setText(currentFood.getSugar() + "g");
                sodiumTextView.setText(currentFood.getSodium() + "mg");
                fiberTextView.setText(currentFood.getFiber() + "g");
                potassiumTextView.setText(currentFood.getPotassium() + "mg");
            }
            catch(Exception e){
                e.printStackTrace();
                ErrorDialogFragment errorDialogFragment = new ErrorDialogFragment();
                errorDialogFragment.show(fm, "Error");
                nameTextView.setText("Sorry, something went wrong!");
            }

            progressBar.setVisibility(View.INVISIBLE);
        }

        private void convertJSONResults(JSONObject results){

            try{
                Log.d("NutritionTag", results.toString());
                JSONObject list = results.getJSONObject("report");
                JSONObject items = list.getJSONObject("food");
                JSONArray nutrients = items.getJSONArray("nutrients");
                JSONObject protein = nutrients.getJSONObject(1);
                JSONObject fat = nutrients.getJSONObject(2);
                JSONObject carb = nutrients.getJSONObject(3);
                JSONObject calories = nutrients.getJSONObject(0);
                JSONObject sodium = nutrients.getJSONObject(9);
                JSONObject potassium = nutrients.getJSONObject(8);
                JSONObject fiber = nutrients.getJSONObject(4);
                JSONObject sugar = nutrients.getJSONObject(5);

                String name = items.getString("name").replaceAll(",\\sUPC:\\s\\d*" ,"");
                currentFood = new Food(name,
                        carb.getDouble("value"),
                        protein.getDouble("value"),
                        fat.getDouble("value"),
                        sugar.getDouble("value"),
                        sodium.getDouble("value"),
                        fiber.getDouble("value"),
                        potassium.getDouble("value"),
                        calories.getDouble("value"));

            }
            catch(JSONException e){
                e.printStackTrace();
            }
        }

    }

}
