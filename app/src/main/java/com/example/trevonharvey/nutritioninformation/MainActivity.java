package com.example.trevonharvey.nutritioninformation;

import android.annotation.TargetApi;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;

import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.regex.Pattern;

public class MainActivity extends AppCompatActivity {

    private EditText searchEditText;
    private TextView searchTextView;
    private TextView queryTextView;
    private Button searchButton;
    private ListView resultsListView;
    private ProgressBar queryProgressBar;
    private CategoryItem[] categories;
    private List<CategoryItem> categoryList = new ArrayList<>();
    private CategoryItemAdapter adapter;
    private SensorManager sensorManager;
    private Sensor accelerometer;
    private android.support.v4.app.FragmentManager fm;
    private double mAccel;
    private double mAccelCurrent;
    private double mAccelLast;
    private String[] tips = {"Shoot for 3 liters of water a day",
            "Shoot for 6 small meals a day",
            "Anything green is your friend",
            "Always read the ingredients list",
            "Whole grains are great!",
            "Watch your sodium intake",
            "Sugar is not your friend",
            "Avoid processed foods",
            "Avoid soft-drinks",
            "Bulk up on fiber"};


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        searchEditText = (EditText)findViewById(R.id.searchEditText);
        searchTextView = (TextView)findViewById(R.id.searchTextView);
        searchTextView.setVisibility(View.INVISIBLE);
        queryTextView = (TextView)findViewById(R.id.quereyTextView);
        queryTextView.setVisibility(View.INVISIBLE);
        resultsListView = (ListView)findViewById(R.id.resultsListView);
        resultsListView.setOnItemClickListener(onItemClickedListener);
        searchButton = (Button)findViewById(R.id.searchButton);
        searchButton.setOnClickListener(onSearchButtonClick);
        queryProgressBar = (ProgressBar)findViewById(R.id.queryProgressBar);
        queryProgressBar.setVisibility(View.INVISIBLE);

        sensorManager = (SensorManager)getSystemService(Context.SENSOR_SERVICE);
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        mAccel = 0.0;
        mAccelCurrent = SensorManager.GRAVITY_EARTH;
        mAccelLast = SensorManager.GRAVITY_EARTH;
        fm = getSupportFragmentManager();

    }

    @Override
    protected void onResume() {
        super.onResume();
        enableAccelerationListening();
    }

    @Override
    protected void onPause() {
        super.onPause();
        disableAccelerationListening();
    }

    public void enableAccelerationListening(){
        sensorManager.registerListener(sensorEventListener,
                sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
                SensorManager.SENSOR_DELAY_NORMAL);
    }

    public void disableAccelerationListening(){
        sensorManager.unregisterListener(sensorEventListener,
                sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER));
    }

    private final SensorEventListener sensorEventListener =
            new SensorEventListener() {
                @Override
                public void onSensorChanged(SensorEvent event) {

                    double x = event.values[0];
                    double y = event.values[1];
                    double z = event.values[2];

                    mAccel = mAccelCurrent;
                    mAccelCurrent = (double)Math.sqrt((double)(x*x + y*y + z*z));
                    double delta = mAccelCurrent - mAccelLast;
                    mAccel = mAccel * 0.9 + delta;

                    int min = 0;
                    int max = tips.length-1;

                    Random r = new Random();
                    int index = r.nextInt(max - min + 1) + min;

                    if(mAccel > 35){
                        Toast toast = Toast.makeText(getApplicationContext(), tips[index], Toast.LENGTH_SHORT);
                        toast.show();
                    }

                }

                @Override
                public void onAccuracyChanged(Sensor sensor, int accuracy) {
                }
            };

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private AdapterView.OnItemClickListener onItemClickedListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            String itemNdbno = categoryList.get(position).getNdbno();
            Intent intent = new Intent(MainActivity.this, NutritionInfoActivity.class);
            intent.putExtra("com.example.trevonharvey.nutritioninformation.MESSAGE", itemNdbno);
            startActivity(intent);
        }
    };

    private Button.OnClickListener onSearchButtonClick = new Button.OnClickListener(){

        @Override
        public void onClick(View v) {

            String foodChoice = searchEditText.getText().toString().trim();

            if(isValid(foodChoice)){
                queryProgressBar.setVisibility(View.VISIBLE);
                searchTextView.setVisibility(View.VISIBLE);
                queryTextView.setVisibility(View.VISIBLE);

                URL url = createFoodURLSearch(foodChoice);

                if(url != null) {
                    dismissKeyboard(v);
                    queryTextView.setText("\t" + foodChoice);
                    GetFoodTask getFoodTask = new GetFoodTask();
                    getFoodTask.execute(url);
                    new GetFoodTask().execute(url);
                }
                else{
                    queryTextView.setText("Failed");
                }
            }
            else{
                searchEditText.setText("Invalid Input");
            }

        }
    };

    private boolean isValid(String input){
        String regex = "^[a-zA-Z]+(?:[\\s-][a-zA-Z]+)*$";
        boolean isValid = Pattern.matches(regex, input);
        if(isValid){
            return true;
        }
        return false;
    }


    private void dismissKeyboard(View view){
        InputMethodManager imm = (InputMethodManager)getSystemService(
                Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    private URL createFoodURLSearch(String food){
        String apiKey = "lIRAsCwnPnhiFERZWX0FzK6tICsm0r86EkWFSMVQ";
        String baseURL = "http://api.nal.usda.gov/ndb/search/?format=json&q=";

        try{
            String urlString = baseURL + URLEncoder.encode(food, "UTF-8") +
                    "&sort=n&max=25&offset=0&api_key=" + apiKey;
            return new URL(urlString);
        }
        catch(Exception e){
            e.printStackTrace();
        }

        return null;
    }

    // Thread class to retrieve categories and id number
    private class GetFoodTask extends AsyncTask<URL, Integer, JSONObject> {
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
        protected  void onProgressUpdate(Integer... progress){
            queryProgressBar.setProgress(progress[0]);
        }

        @Override
        protected void onPostExecute(JSONObject food){
            if(food != null) {
                convertJSONNdbno(food);
                //adapter = new ArrayAdapter<CategoryItem>(MainActivity.this, android.R.layout.simple_list_item_1, categories);
                adapter = new CategoryItemAdapter(MainActivity.this, categoryList);
                resultsListView.setAdapter(adapter);
            }
            else{
                Toast t = Toast.makeText(getApplicationContext(), "Sorry, no matches", Toast.LENGTH_SHORT);
                t.show();
                searchTextView.setVisibility(View.INVISIBLE);
                queryTextView.setVisibility(View.INVISIBLE);
            }
            queryProgressBar.setVisibility(View.INVISIBLE);

        }


        private void convertJSONNdbno(JSONObject search){
            categoryList.clear();
            try{

                JSONObject list = search.getJSONObject("list");
                JSONArray items = list.getJSONArray("item");
//                categories = new CategoryItem[items.length()];
//                for(int i = 0; i < items.length(); i++){
//                    JSONObject foodList = items.getJSONObject(i);
//                    categories[i] = new CategoryItem(foodList.getString("ndbno"), foodList.getString("group"), foodList.getString("name"));
//                }
                for(int i = 0; i < items.length(); i++){
                    JSONObject foodList = items.getJSONObject(i);
                    categoryList.add(new CategoryItem(foodList.getString("ndbno"), foodList.getString("group"), foodList.getString("name")));
                }

            }
            catch(JSONException e){
                e.printStackTrace();
            }
        }
    }

}
