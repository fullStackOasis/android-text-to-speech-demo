package com.fullstackoasis.texttospeechdemo;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.widget.TextView;

import java.util.Locale;

/**
 * The only activity. Just a demo for text-to-speech functionality in Android.
 */
public class MainActivity extends AppCompatActivity implements TextToSpeech.OnInitListener {
    private static String TAG = MainActivity.class.getCanonicalName();
    private static int MY_DATA_CHECK_CODE = 921;
    private TextToSpeech textToSpeech;
    private TextView tvNotification;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        tvNotification = findViewById(R.id.tvNotification);
        checkAvailableTTS();
        Log.d(TAG, "done");
    }

    void checkAvailableTTS() {
        // https://android-developers.googleblog.com/2009/09/introduction-to-text-to-speech-in.html
        Intent checkIntent = new Intent();
        checkIntent.setAction(TextToSpeech.Engine.ACTION_CHECK_TTS_DATA);
        startActivityForResult(checkIntent, MY_DATA_CHECK_CODE);
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d(TAG, "onActivityResult");
        if (requestCode == MY_DATA_CHECK_CODE) {
            Log.d(TAG, "onActivityResult MY_DATA_CHECK_CODE");
            if (resultCode == TextToSpeech.Engine.CHECK_VOICE_DATA_PASS) {
                // success, create the TTS instance
                Log.d(TAG, "onActivityResult CHECK_VOICE_DATA_PASS");
                textToSpeech = new TextToSpeech(this, this);
            } else {
                Log.d(TAG, "onActivityResult ACTION_INSTALL_TTS_DATA");
                // missing data, install it
                Intent installIntent = new Intent();
                installIntent.setAction(
                        TextToSpeech.Engine.ACTION_INSTALL_TTS_DATA);
                startActivity(installIntent);
            }
        }
    }

    @Override
    public void onInit(int status) {
        Log.d(TAG, "onInit");
        if (textToSpeech.isLanguageAvailable(Locale.US) == TextToSpeech.LANG_COUNTRY_AVAILABLE) {
            tvNotification.setText("Text to Speech is ready. Demo US English.");
            textToSpeech.setLanguage(Locale.US);
        } else {
            // TODO FIXME. give users other option.
            tvNotification.setText("Cannot demo Text to Speech");
        }

    }
}
