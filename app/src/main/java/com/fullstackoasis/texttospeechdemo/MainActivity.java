package com.fullstackoasis.texttospeechdemo;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.util.Locale;

/**
 * The only activity. Just a demo for text-to-speech functionality in Android.
 *
 * Be wary of "Activity has leaked ServiceConnection
 * android.speech.tts.TextToSpeech$Connection@44f91c5 that was originally bound here"
 * on configuration change.
 */
public class MainActivity extends AppCompatActivity implements TextToSpeech.OnInitListener {
    private static String TAG = MainActivity.class.getCanonicalName();
    private static String TEXT_TO_SPEECH = "TEXT_TO_SPEECH";
    private static int MY_DATA_CHECK_CODE = 921;
    private TextToSpeech textToSpeech;
    private TextView tvNotification;
    private EditText tvEdit;
    private Button btnSayIt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate");
        setContentView(R.layout.activity_main);
        tvNotification = findViewById(R.id.tv_notification);
        btnSayIt = findViewById(R.id.btn_say_it);
        tvEdit = findViewById(R.id.et_text_input);
        enableWidgets(false);

        checkAvailableTTS();
        btnSayIt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String content = tvEdit.getText().toString();
                Log.d(TAG, "Content: " + content);
                textToSpeech.speak(content, TextToSpeech.QUEUE_ADD, null);
            }
        });
        Log.d(TAG, "done");
    }

    void enableWidgets(boolean enabled) {
        tvEdit.setEnabled(enabled);
        btnSayIt.setEnabled(enabled);
        if (enabled) {
            tvNotification.setText(R.string.instructions);
        } else {
            tvNotification.setText(R.string.not_ready);
        }
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
                enableWidgets(true);
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
    protected void onDestroy() {
        super.onDestroy();
        if (textToSpeech != null) {
            textToSpeech.stop();
            textToSpeech.shutdown();
        }
        textToSpeech = null;
    }

    @Override
    public void onInit(int status) {
        Log.d(TAG, "onInit");
        if (textToSpeech.isLanguageAvailable(Locale.US) == TextToSpeech.LANG_COUNTRY_AVAILABLE) {
            tvNotification.setText(R.string.instructions);
            textToSpeech.setLanguage(Locale.US);
        } else {
            // TODO FIXME. give users other option.
            tvNotification.setText(R.string.error);
        }
    }
}
