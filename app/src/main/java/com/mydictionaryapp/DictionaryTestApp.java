
package com.mydictionaryapp;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.mydictionaryapp.model.Dictionary;
import com.mydictionaryapp.utils.AppUtils;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class DictionaryTestApp extends Activity implements OnClickListener, OnItemSelectedListener,
        HttpRequestCallback, TextToSpeech.OnInitListener {

    private final int REQ_CODE_SPEECH_INPUT = 100;
    Map<String, String> hmLanguages;
    String translateURL = "https://translate.yandex.net/api/v1.5/tr.json/translate"; // ?key=trnsl.1.1.20140304T071909Z.b9e89fae4d489cf1.6486d3ae866bfe9c6a389b84cc61e5d860cf0112&lang=en-tr&text=dog";
    String detectURL = "https://translate.yandex.net/api/v1.5/tr.json/detect";
    String apiKey = "trnsl.1.1.20140304T071909Z.b9e89fae4d489cf1.6486d3ae866bfe9c6a389b84cc61e5d860cf0112";
    String httpOutput;
    HttpRequest httpRequest;
    Map<String, String> params = new HashMap<String, String>();
    private Dictionary dictionary;
    private EditText etInput;
    private TextView tvOutput;
    private Button btnSend;
    private Gson gson;
    private Spinner spinnerFrom, spinnerTo;
    // private String item = "";
    private ImageButton ibMic;
    private TextToSpeech tts;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dictionary);

        tts = new TextToSpeech(this, this);

        etInput = (EditText) findViewById(R.id.etInput);
        tvOutput = (TextView) findViewById(R.id.tvOutput);
        btnSend = (Button) findViewById(R.id.btnTranslate);
        btnSend.setOnClickListener(this);
        httpRequest = new HttpRequest(DictionaryTestApp.this);

        RelativeLayout layoutDictionaryActivity = (RelativeLayout) findViewById(R.id.rlDictionaryActivity);
        layoutDictionaryActivity.setOnClickListener(this);

        spinnerFrom = (Spinner) findViewById(R.id.languageFrom);
        spinnerTo = (Spinner) findViewById(R.id.languageTo);

        ibMic = (ImageButton) findViewById(R.id.ibMic);
        ibMic.setOnClickListener(this);

        AdView adView = (AdView) this.findViewById(R.id.adMob);
        // request TEST ads to avoid being disabled for clicking your own ads
        AdRequest adRequest = new AdRequest.Builder()
                .addTestDevice(AdRequest.DEVICE_ID_EMULATOR)// This is for
                                                            // emulators
                // test mode on DEVICE (this example code must be replaced with
                // your device uniquq ID)
                .addTestDevice("") // Galaxy S2
                .build();
        adView.loadAd(adRequest);

        List<String> languages = new ArrayList<String>();
        languages.add("Select");
        languages.add("Russian");
        languages.add("Polish");
        languages.add("English");
        languages.add("German");
        languages.add("French");
        languages.add("Spanish");
        languages.add("Italy");
        languages.add("Turkish");

        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this, R.layout.spinner_layout,
                languages);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerFrom.setAdapter(dataAdapter);
        spinnerTo.setAdapter(dataAdapter);

        hmLanguages = new HashMap<String, String>();
        hmLanguages.put("Select", "null");
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
        switch (view.getId()) {
            case R.id.btnTranslate:
                if (hmLanguages.get(spinnerFrom.getSelectedItem()).equals("null")) {
                    detectLanguageRequest(etInput.getText().toString());
                } else {
                    translateRequest(hmLanguages.get(spinnerFrom.getSelectedItem()),
                            hmLanguages.get(spinnerTo.getSelectedItem()));
                }

                break;
            case R.id.rlDictionaryActivity:
                AppUtils.hideKeyboard(DictionaryTestApp.this);
                break;

            case R.id.ibMic:
                promptSpeechInput();
                break;
            default:
                break;
        }
    }

    public void detectLanguageRequest(String text) {

        params.put("key", apiKey);
        params.put("text", text);
        if (!AppUtils.checkNetworkConnection(DictionaryTestApp.this)) {
            AppUtils.showToast(DictionaryTestApp.this, "Check your internet connection");
        } else {
            httpRequest.makeHttpPostWithVolley(detectURL, params, false);
        }
    }

    public void translateRequest(String langCodeFrom, String langCodeTo) {
        params.put("key", apiKey);
        params.put("lang", langCodeFrom + "-" + langCodeTo);
        Log.d("Translate Request : FROM > TO >>>", langCodeFrom + ">" + langCodeTo);
        params.put("text", etInput.getText().toString());
        if (!AppUtils.checkNetworkConnection(DictionaryTestApp.this)) {
            AppUtils.showToast(DictionaryTestApp.this, "Check your internet connection");
        } else {
            httpRequest.makeHttpPostWithVolley(translateURL, params, true);
        }
    }

    @Override
    public void callback(String jsonResponse) {
        gson = new Gson();
        Log.d("Response", jsonResponse.toString());
        dictionary = gson.fromJson(jsonResponse, Dictionary.class);
        if (dictionary.getLang().length() < 3) {
            String langCodeFrom = dictionary.getLang().trim();
            translateRequest(langCodeFrom, hmLanguages.get(spinnerTo.getSelectedItem()));
        } else {
            dictionary = gson.fromJson(jsonResponse, Dictionary.class);
            tvOutput.setText(dictionary.getText().get(0));
            speakOut();
        }
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
        // item = adapterView.getSelectedItem().toString();
        Log.d("ITEM>>> ", adapterView.getSelectedItem().toString());
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {
        // item = "null";
    }

    /**
     * Receiving speech input
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case REQ_CODE_SPEECH_INPUT: {
                if (resultCode == RESULT_OK && null != data) {

                    ArrayList<String> result = data
                            .getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                    etInput.setText(result.get(0));
                }
                break;
            }

        }
    }

    /**
     * Showing google speech input dialog
     */
    private void promptSpeechInput() {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT,
                getString(R.string.speech_prompt));
        try {
            startActivityForResult(intent, REQ_CODE_SPEECH_INPUT);
        } catch (ActivityNotFoundException a) {
            Toast.makeText(getApplicationContext(),
                    getString(R.string.speech_not_supported),
                    Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onDestroy() {
        // Don't forget to shutdown tts!
        if (tts != null) {
            tts.stop();
            tts.shutdown();
        }
        super.onDestroy();
    }

    @Override
    public void onInit(int status) {
        if (status == TextToSpeech.SUCCESS) {

            int result = tts.setLanguage(Locale.US);

            if (result == TextToSpeech.LANG_MISSING_DATA
                    || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                Log.e("TTS", "This Language is not supported");
            } else {
                // btnSpeak.setEnabled(true);
                speakOut();
            }

        } else {
            Log.e("TTS", "Initilization Failed!");
        }
    }

    private void speakOut() {

        String text = tvOutput.getText().toString();

        tts.speak(text, TextToSpeech.QUEUE_FLUSH, null);
    }
}
