package com.example.jane.timskaapplication101.extra;

/**
 * Created by Jane on 7/22/2015.
 */

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.SoundPool;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.speech.RecognizerIntent;
import android.speech.tts.TextToSpeech;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.internal.widget.AdapterViewCompat;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.jane.timskaapplication101.MainActivity;
import com.example.jane.timskaapplication101.MainNavigation;
import com.example.jane.timskaapplication101.MyNetworkChecker;
import com.example.jane.timskaapplication101.Prefs;
import com.example.jane.timskaapplication101.R;
import com.memetix.mst.language.Language;
import com.memetix.mst.translate.Translate;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class FirstFrag extends MainNavigation.SectionFrag  {

    protected static final int RESULT_SPEECH = 1;
    private boolean toastBool;

    private ImageView clear;

    private String translatedText;


    Spinner spinnerLeft;
    Spinner spinnerRight;



    Spinner voiceLangauges;

    String [] spinnerL;
    String [] spinnerR;

    String [] spinnerVoice;

    Locale localeL;


    String momentLeftLangauge;
    String momentRightLangauge;

    String momentVoiceLangauge;

    Button change;

    int voiceSelectedItem;


    private ImageView btnSpeak;

    private EditText enterText;
    private TextView translated;
    private String localeLangauge;

    private boolean MobileDataUsageNotGood;

    public FirstFrag(){
        super();
    }
    public static FirstFrag newInstance(Context c, int section){
        FirstFrag ret = new FirstFrag();
        ret.setSection(section);
        return ret;
    }



    @Override
    public void afterCreateView(LayoutInflater inflater, ViewGroup container,
                                Bundle savedInstanceState) {

        super.afterCreateView(inflater, container, savedInstanceState); // not mandatory, but good to call super
        setContentView(R.layout.first_frag_layout);
        Log.i("JANE", "Test za prv frag");
        spinnerLeft = (Spinner) findViewById(R.id.left_langauge_spinner);
        spinnerRight = (Spinner) findViewById(R.id.right_langauge_spinner);

        voiceLangauges = (Spinner) findViewById(R.id.voice_langauge_spinner);

        enterText = (EditText) findViewById(R.id.entertexthere);
        enterText.setMovementMethod(new ScrollingMovementMethod());

        localeLangauge="en";

        btnSpeak = (ImageView) findViewById(R.id.speechbutton);


        SharedPreferences example = getActivity().getApplicationContext().
                getSharedPreferences(Prefs.PREFS, 0);

        toastBool = example.getBoolean(Prefs.ToastPrefs,
                false);



        boolean voiceTransBool = example.getBoolean(Prefs.VoiceTransPrefs,
                true);
        if(voiceTransBool) {
            btnSpeak.setVisibility(View.VISIBLE);
        } else {
            btnSpeak.setVisibility(View.GONE);
        }

        boolean WifiOnly = example.getBoolean(Prefs.WifiPrefs, false);
        if(WifiOnly) {
          boolean mobileDataActive = MyNetworkChecker.isMobileDataAvailable(
                    getActivity().getApplicationContext());
            if(mobileDataActive) {
                MobileDataUsageNotGood = true;
            }
        }


        enterText.addTextChangedListener(writingWatcher);

        clear = (ImageView) findViewById(R.id.clearbutton);
        clear.setVisibility(View.GONE);



        btnSpeak.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                if(translated.getText().toString().trim() != "") {
                    speakOut();
                }else {
                    Log.i("JANE", "Prazen textView");
                }
            }

        });


        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> adapter =
                ArrayAdapter.createFromResource(getActivity().getApplicationContext(),
                R.array.langauge_array , R.layout.spinner_custom_textview);

        ArrayAdapter<CharSequence> adapter2 =
                ArrayAdapter.createFromResource(getActivity().getApplicationContext(),
                        R.array.langauge_array , R.layout.spinner_custom_textview);

        ArrayAdapter<CharSequence> adaptervoice =
                ArrayAdapter.createFromResource(getActivity().getApplicationContext(),
                        R.array.langauge_array , R.layout.spinner_custom_textview);


        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(R.layout.spinner_custom_textview);
        adapter2.setDropDownViewResource(R.layout.spinner_custom_textview);
        adaptervoice.setDropDownViewResource(R.layout.spinner_custom_textview);

        // Apply the adapter to the spinner
        spinnerLeft.setAdapter(adapter);
        spinnerRight.setAdapter(adapter2);
        voiceLangauges.setAdapter(adaptervoice);

        voiceSelectedItem = 4;

        spinnerL = new String[38];
        spinnerL = getResources().getStringArray(R.array.langauge_array);

        spinnerR = new String[38];
        spinnerR = getResources().getStringArray(R.array.langauge_array);

        spinnerVoice = new String[38];
        spinnerVoice = getResources().getStringArray(R.array.langauge_array);

        momentVoiceLangauge = spinnerVoice[voiceSelectedItem];

        translated = (TextView) findViewById(R.id.translatedText);


        spinnerLeft.setSelection(adapter.getPosition("English"));
        voiceLangauges.setSelection(adapter.getPosition("English"));

        clear.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    enterText.setText("");
                }
        });


        /*

        so setOnItemSelectedListener ne znam zoshto mi se povikuva
        onCreate() metodo nanovo sho znaci nanovo mi gi prebaruva
        site fajlovi
        sho znaci to e losho:@@@@@@@@@@@@

        ali ok, na drugiov nacin si raboti zasega


        spinnerLeft.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                momentLeftLangauge = spinnerL[position];


            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        spinnerRight.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                momentRightLangauge = spinnerR[position];

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

*/

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);

        fab.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // za multi-threading - do 5 maksimum

                if (MobileDataUsageNotGood) {
                    if(toastBool) {
                       Toast.makeText(getActivity().getApplicationContext(),
                               "Please check the applications settings and try again",
                               Toast.LENGTH_SHORT).show();
                    } else {
                        MainActivity.tts.setLanguage(Locale.ENGLISH);
                        MainActivity.tts.speak(
                                "Please check the applications settings and try again",
                                TextToSpeech.QUEUE_FLUSH, null);
                    }

                } else {

                    if (enterText.getText().toString().trim().equals("")) {
                        if(!toastBool) {
                            MainActivity.tts.setLanguage(Locale.ENGLISH);
                            MainActivity.tts.speak(
                                    getString(R.string.messageFromTranslator),
                                    TextToSpeech.QUEUE_FLUSH, null);
                        } else {
                            Toast.makeText(getActivity().getApplicationContext(),
                                    getString(R.string.messageFromTranslator),
                                    Toast.LENGTH_SHORT).show();
                        }
                        enterText.setText("");
                    } else {
                        //da si go prevedi teksto vo pozadina
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
                            new TranslateFromBing().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                        } else {
                            new TranslateFromBing().execute();
                        }
                    }
                }
            }
        });

        FloatingActionButton fabvoice = (FloatingActionButton) findViewById(R.id.fabvoice);

        fabvoice.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {

                voiceLangauges.performClick();
                return true;
            }
        });


        fabvoice.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(
                        RecognizerIntent.ACTION_RECOGNIZE_SPEECH);

                voiceSelectedItem = voiceLangauges.getSelectedItemPosition();
                Log.i("JANE", voiceLangauges.getSelectedItemPosition() + "");

                momentVoiceLangauge = spinnerVoice[voiceSelectedItem];
                Log.i("JANE", momentVoiceLangauge);

                intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE,
                        convertorToLangaugeCode(momentVoiceLangauge));
                Log.i("JANE short", momentVoiceLangauge);

                try {
                    startActivityForResult(intent, RESULT_SPEECH);
                } catch (ActivityNotFoundException a) {
                    Toast t = Toast.makeText(getActivity().getApplicationContext(),
                            "Your device doesn't support Speech to Text",
                            Toast.LENGTH_SHORT);
                    t.show();
                }
            }
        });

        change = (Button) findViewById(R.id.changelangauge);
        change.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int lev = spinnerLeft.getSelectedItemPosition();
                int des = spinnerRight.getSelectedItemPosition();
                spinnerLeft.setSelection(des);
                spinnerRight.setSelection(lev);
            }
        });

    }



    private TextWatcher writingWatcher = new TextWatcher() {

        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            Log.i("JANE " , "sho majka ti before");
        }

        public void onTextChanged(CharSequence s, int start, int before, int count) {
            Log.i("JANE " , "sho majka ti ON");
                clear.setVisibility(View.VISIBLE);
        }

        public void afterTextChanged(Editable s) {
            if (s.length() == 0) {
                Log.i("JANE " , "sho majka ti after == 0");
                clear.setVisibility(View.GONE);
            } else{
                Log.i("JANE ", "sho majka ti after != 0");
                clear.setVisibility(View.VISIBLE);
            }
        }
    };


    public String convertorToLangaugeCode(String fullNameOfLangauge) {
        String langaugeCode;

        switch (fullNameOfLangauge) {

            case "Arabic" : langaugeCode = "ar";
                break;
            case "Czech" : langaugeCode = "cs";
                break;
            case "Danish" : langaugeCode = "da";
                break;
            case "German" : langaugeCode = "de";
                break;
            case "English" : langaugeCode = "en";
                break;
            case "Estonian" : langaugeCode = "et";
                break;
            case "Finnish" : langaugeCode = "fi";
                break;
            case "French" : langaugeCode = "fr";
                break;
            case "Dutch" : langaugeCode = "nl";
                break;
            case "Greek" : langaugeCode = "el";
                break;
            case "Hebrew" : langaugeCode = "he";
                break;
            case "Haitian Creole" : langaugeCode = "ht";
                break;
            case "Hungarian" : langaugeCode = "hu";
                break;
            case "Indonesian" : langaugeCode = "id";
                break;
            case "Italian" : langaugeCode = "it";
                break;
            case "Japanese" : langaugeCode = "ja";
                break;
            case "Korean" : langaugeCode = "ko";
                break;
            case "Lithuanian" : langaugeCode = "lt";
                break;
            case "Latvian" : langaugeCode = "lv";
                break;
            case "Norwegian" : langaugeCode = "no";
                break;
            case "Polish" : langaugeCode = "pl";
                break;
            case "Portuguese" : langaugeCode = "pt";
                break;
            case "Romanian" : langaugeCode = "ro";
                break;
            case "Spanish" : langaugeCode = "es";
                break;
            case "Russian" : langaugeCode = "ru";
                break;
            case "Slovak" : langaugeCode = "sk";
                break;
            case "Slovenian" : langaugeCode = "sl";
                break;
            case "Swedish" : langaugeCode = "sv";
                break;
            case "Thai" : langaugeCode = "th";
                break;
            case "Turkish" : langaugeCode = "tr";
                break;
            case "Ukrainian" : langaugeCode = "uk";
                break;
            case "Vietnamese" : langaugeCode = "vi";
                break;
            case "Simplified Chinese" : langaugeCode = "zh-CHS";
                break;
            case "Traditional Chinese" : langaugeCode = "zh-CHT";
                break;
            default: langaugeCode = "en";
                break;
        }

        return langaugeCode;
    }



    public String getLeftSpinnerLangauge() {
        String leftLangauge = convertorToLangaugeCode(momentLeftLangauge);
        return leftLangauge;
    }

    public String getRightSpinnerLangauge() {
        String rightLangauge = convertorToLangaugeCode(momentRightLangauge);
        return rightLangauge;
    }

    @Override
    protected void onRetryClicked() {}

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case RESULT_SPEECH: {
                if (resultCode == -1 && null != data) {

                    ArrayList<String> text = data
                            .getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                    String enterTextString = enterText.getText().toString();
                    enterTextString += " " + text.get(0);
                    enterText.setText(enterTextString);
                }
                break;
            }

        }
    }




    private void speakOut() {
        String text = translated.getText().toString();
        MainActivity.tts.speak(text, TextToSpeech.QUEUE_FLUSH, null);
    }


    // Async Task Class
    class TranslateFromBing extends AsyncTask<String, String, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            showLoading();
        }


        @Override
        protected String doInBackground(String... f_url) {

            try {

                //Log.i("KLARAL: ", spinnerLeft.getSelectedItemPosition() + "");
                //Log.i("KLARAR:", spinnerRight.getSelectedItemPosition() + "");

                momentRightLangauge = spinnerR[spinnerRight.getSelectedItemPosition()];
                momentLeftLangauge = spinnerL[spinnerLeft.getSelectedItemPosition()];

                Log.i("JANE", momentLeftLangauge + "-" + momentRightLangauge);

                localeLangauge = getRightSpinnerLangauge();

                translatedText = Translate.execute(enterText.getText().toString(),
                        Language.fromString(getLeftSpinnerLangauge()),
                        Language.fromString(getRightSpinnerLangauge()));

            } catch (Exception ex) {
                ex.printStackTrace();
                translatedText = "Check your internet connection";
            }
            return translatedText;
        }


        protected void onProgressUpdate(String... progress) {
            // Set progress percentage

        }

        @Override
        protected void onPostExecute(String file_url) {
            dismissLoading();
            if(translatedText != null) {
                if(translatedText.equals("Check your internet connection")) {
                    MainActivity.tts.setLanguage(Locale.ENGLISH);
                    MainActivity.tts.speak(translatedText, TextToSpeech.QUEUE_FLUSH, null);
                    translated.setText("");
                } else {
                    translated.setText(translatedText);
                }
            }
            // za da namestam jazik za speech za pobrzo da raboti
            // ne znam dali ovde  enajdobro mesto. Mozda
            // na krajo sepak vo btnSpeaak ce go stavam . hmmm ???
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
                new setLangaugeToSpeech().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
            } else {
                new setLangaugeToSpeech().execute();
            }

        }
    }


    // Async Task Class
    class setLangaugeToSpeech extends AsyncTask<String, String, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }


        @Override
        protected String doInBackground(String... f_url) {
            localeL = new Locale(localeLangauge);
            MainActivity.tts.setLanguage(localeL);
            return "";
        }


        protected void onProgressUpdate(String... progress) {
            // Set progress percentage

        }

        @Override
        protected void onPostExecute(String file_url) {

        }
    }


}