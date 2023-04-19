package com.shockwawe.sodvapp;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.blikoon.qrcodescanner.QrCodeActivity;

import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private static final int REQUEST_CODE_QR_SCAN = 101;
    public static String LGT = "LOG";
    private TextToSpeech TTS;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Button button_st = (Button) findViewById(R.id.button_st);
        button_st.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick (View v){
                Intent i = new Intent(MainActivity.this, QrCodeActivity.class);
                startActivityForResult(i, REQUEST_CODE_QR_SCAN);
            }
        });
        TTS = new TextToSpeech(this, new TextToSpeech.OnInitListener() {
            @Override public void onInit(int initStatus) {
                Locale locale = new Locale("RU");
                if (initStatus == TextToSpeech.SUCCESS) {

                        TTS.setLanguage(locale);
                    int result = TTS.setLanguage(locale);
                    if (result == TextToSpeech.LANG_MISSING_DATA
                            || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                        Log.e("TTS", "Извините, этот язык не поддерживается");
                    }
                    TTS.setPitch(1.3f);
                    TTS.setSpeechRate(0.7f);
                    TTS.speak("Приложение СОДВ запущено. Добрый день.", TextToSpeech.QUEUE_FLUSH, null);
                } else if (initStatus == TextToSpeech.ERROR) {
                    Toast.makeText(MainActivity.this, "Error: RU language is not supported by your device", Toast.LENGTH_LONG).show();
                }

            }

        });

    }

        @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

            super.onActivityResult(requestCode, resultCode, data);
            if (resultCode != Activity.RESULT_OK) {
                Log.d(LGT, "COULD NOT GET A GOOD RESULT.");
                if (data == null)
                    return;
                //Getting the passed result
                String result = data.getStringExtra("com.blikoon.qrcodescanner.error_decoding_image");
                if (result != null) {
                    qr_proceed(result, 0);
                }
                return;

            }
            if (requestCode == REQUEST_CODE_QR_SCAN) {
                if (data == null)
                    return;
                //Getting the passed result
                String result = data.getStringExtra("com.blikoon.qrcodescanner.got_qr_scan_relult");
                Log.d(LGT, "Have scan result in your app activity :" + result);
                qr_proceed(result, 1);
            }
        }

    @Override
    public void onDestroy() {
        // Don't forget to shutdown mTTS!
        if (TTS != null) {
            TTS.stop();
            TTS.shutdown();
        }
        super.onDestroy();
    }

    public void qr_proceed(String result, int states){
        Log.d(LGT, "Is it working???");
        final Handler handler = new Handler();
        final Runnable r = new Runnable() {
            public void run() {

                switch(states){
                    case 0: //Код плохо считался, надо заново считать
                        TTS.speak("Код не распознан. Стойте спокойно, чтобы распознать его.", TextToSpeech.QUEUE_FLUSH, null);
                        Log.i("info","result");
                        break;
                    case 1: //Код хорошо считался, произносим его
                        TTS.speak(result, TextToSpeech.QUEUE_FLUSH, null);
                        Log.i("info","result");
                        break;
                    default: /*Если результат не 1 и не 0, произошел АБРЫГАЛВ.
            Перезапускаем считывание, хотя в этом случае лучше вообще
            рестартить приложение. Но работа с говноUI типа MIUI показала, что
            на многих телефонах автозапуск расценивается как вирус. Кринж.*/
                        TTS.speak("Код не распознан. Стойте спокойно, чтобы распознать его.", TextToSpeech.QUEUE_FLUSH, null);
                        Log.i("info","result");
                        break;
                }
            }
        };
        final Runnable r2 = new Runnable() {
            public void run() {
                Intent i = new Intent(MainActivity.this, QrCodeActivity.class);
                startActivityForResult(i, REQUEST_CODE_QR_SCAN);
            }
        };
        handler.postDelayed(r, 1000);
        handler.postDelayed(r2, 10000);

    }

}

