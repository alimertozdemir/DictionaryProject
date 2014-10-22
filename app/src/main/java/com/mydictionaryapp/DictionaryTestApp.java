package com.mydictionaryapp;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.view.View.OnClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.Toast;

import com.google.gson.Gson;
import com.mydictionaryapp.model.Dictionary;
import com.mydictionaryapp.utils.AppUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class DictionaryTestApp extends Activity implements OnClickListener, OnItemSelectedListener, HttpRequestCallback {

    private Dictionary dictionary;
    private EditText etInput;
    private TextView tvOutput;
    private Button btnSend;
    private Gson gson;
    private Spinner spinnerFrom, spinnerTo;
    private String item = "", langCodeFrom = "", langCodeTo = "";
    Map<String, String> hmLanguages;

    String baseURL = "https://translate.yandex.net/api/v1.5/tr.json/translate"; //?key=trnsl.1.1.20140304T071909Z.b9e89fae4d489cf1.6486d3ae866bfe9c6a389b84cc61e5d860cf0112&lang=en-tr&text=dog";
    String apiKey = "trnsl.1.1.20140304T071909Z.b9e89fae4d489cf1.6486d3ae866bfe9c6a389b84cc61e5d860cf0112";
    String httpOutput;
    HttpRequest httpRequest;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dictionary);

        etInput = (EditText) findViewById(R.id.etInput);
        tvOutput = (TextView) findViewById(R.id.tvOutput);
        btnSend = (Button) findViewById(R.id.btnTranslate);
        btnSend.setOnClickListener(this);
        httpRequest = new HttpRequest(DictionaryTestApp.this);

        RelativeLayout layoutDictionaryActivity = (RelativeLayout) findViewById(R.id.rlDictionaryActivity);
        layoutDictionaryActivity.setOnClickListener(this);

        spinnerFrom = (Spinner) findViewById(R.id.languageFrom);
        spinnerTo = (Spinner) findViewById(R.id.languageTo);


        List<String> languages = new ArrayList<String>();
        languages.add("Russian");
        languages.add("Polish");
        languages.add("English");
        languages.add("German");
        languages.add("French");
        languages.add("Spanish");
        languages.add("Italy");
        languages.add("Turkish");

        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this, R.layout.spinner_layout, languages);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerFrom.setAdapter(dataAdapter);
        spinnerTo.setAdapter(dataAdapter);

        hmLanguages = new HashMap<String, String>();
        hmLanguages.put("Russian", "ru");
        hmLanguages.put("Polish", "pl");
        hmLanguages.put("English", "en");
        hmLanguages.put("German", "de");
        hmLanguages.put("French", "fr");
        hmLanguages.put("Spanish", "es");
        hmLanguages.put("Italy", "it");
        hmLanguages.put("Turkish", "tr");

        spinnerFrom.setOnItemSelectedListener(this);
        spinnerTo.setOnItemSelectedListener(this);

    }

    @Override
    public void onClick(View view) {
            switch(view.getId()){
                case R.id.btnTranslate:
                    HashMap<String, String>  params = new HashMap<String, String>();
                    params.put("key", apiKey);
                    params.put("lang", hmLanguages.get(spinnerFrom.getSelectedItem())+"-"+hmLanguages.get(spinnerTo.getSelectedItem()));
                    Log.d("FROM > TO >>>", hmLanguages.get(spinnerFrom.getSelectedItem()) + ">" + hmLanguages.get(spinnerTo.getSelectedItem()));
                    params.put("text", etInput.getText().toString());
                    if (!AppUtils.checkNetworkConnection(DictionaryTestApp.this)) {
                       AppUtils.showToast(DictionaryTestApp.this, "Check your internet connection");
                    } else {
                        httpRequest.makeHttpPostWithVolley(baseURL, params);
                    }

                    break;
                case R.id.rlDictionaryActivity:
                    AppUtils.hideKeyboard(DictionaryTestApp.this);
                    break;
                default:
                    break;
            }
        }

    @Override
    public void callback(String jsonResponse) {
        gson = new Gson();
        Log.d("Response", jsonResponse.toString());
        dictionary = gson.fromJson(jsonResponse, Dictionary.class);
        tvOutput.setText(dictionary.getText().get(0));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.dictionary, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        item = adapterView.getSelectedItem().toString();
        Log.d("ITEM>>> ", item);
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }
}
