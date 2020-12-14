package com.markviews.hiltonroomcheck;

import androidx.appcompat.app.AppCompatActivity;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.speech.tts.TextToSpeech;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Locale;


public class MainActivity extends AppCompatActivity {

    TextToSpeech t1;
    EditText textInput;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        textInput = (EditText) findViewById(R.id.textSearchInput);
        setupTTS();

        Button voiceSearch = (Button) findViewById(R.id.voiceSearch);
        voiceSearch.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                displaySpeechRecognizer();
            }
        });

        Button textSearch = (Button) findViewById(R.id.textSearch);
        textSearch.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                parseCommand(textInput.getText().toString());
                hideKeyboard();
            }
        });

        textInput.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                textInput.setText("");
            }
        });

    }

    public void parseCommand(String text) {
        text = text.replace("for ", "4");
        String[] args = text.split(" ");
        for (String arg : args) {
            arg = arg.replace(":", "");
            if (arg.matches("\\d+")) {
                if (arg.length() == 3) {
                    String roomName = find(arg);
                    if (roomName == null) textInput.setText("");
                    else textInput.setText(arg);
                    speak(roomName);
                    return;
                }
            }
        }
        textInput.setText("");
        speak(null);
    }

    public void setupTTS() {
        t1=new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if(status != TextToSpeech.ERROR) {
                    t1.setLanguage(Locale.UK);
                }
            }
        });
    }

    public void speak(String text) {
        if (text == null) text = "Invalid Room Number";

        setImage(text);
        toast(text);

        if (text.contains("Dahlia")) text = text.replace("Dahlia", "dallie-uh");
        t1.speak(text, TextToSpeech.QUEUE_FLUSH, null);
    }

    public void toast(String text) {
        text = text.replace(",,", "");
        Toast.makeText(this, text,Toast.LENGTH_SHORT).show();
        TextView buildingName = (TextView) findViewById(R.id.buildingName);
        buildingName.setText(text);
    }

    public void setImage(String text) {
        ImageView img = (ImageView) findViewById(R.id.mapImage);
        if (text.contains("Gardenia")) img.setImageResource(R.drawable.gar);
        else if (text.contains("Camellia")) img.setImageResource(R.drawable.cam);
        else if (text.contains("Hibiscus")) img.setImageResource(R.drawable.hib);
        else if (text.contains("Bougainvillia")) img.setImageResource(R.drawable.bou);
        else if (text.contains("Azalea")) img.setImageResource(R.drawable.az);
        else if (text.contains("Dahlia")) img.setImageResource(R.drawable.dah);
        else if (text.contains("Eucalyptus")) img.setImageResource(R.drawable.eu);
        else if (text.contains("Fuchsia")) img.setImageResource(R.drawable.fuc);
        else if (text.contains("Invalid")) img.setImageResource(R.drawable.og);
    }

    //find room name from text file
    public String find(String room) {
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new InputStreamReader(getAssets().open("data.txt")));
            String mLine;
            while ((mLine = reader.readLine()) != null) {
                if (mLine.contains(room)) {
                    return mLine.replace(room + "\t", "");
                }
            }
            return null;
        } catch (IOException e) {
            Toast.makeText(this, "error",Toast.LENGTH_SHORT).show();
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    Toast.makeText(this, "error",Toast.LENGTH_SHORT).show();
                }
            }
        }
        return null;
    }

    public void hideKeyboard() {
        InputMethodManager imm = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);
    }

    private static final int SPEECH_REQUEST_CODE = 0;
    private void displaySpeechRecognizer() {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        startActivityForResult(intent, SPEECH_REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode,Intent data) {
        if (requestCode == SPEECH_REQUEST_CODE && resultCode == RESULT_OK)
            parseCommand(data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS).get(0));
        super.onActivityResult(requestCode, resultCode, data);
    }

}