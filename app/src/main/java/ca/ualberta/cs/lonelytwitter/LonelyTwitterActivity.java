package ca.ualberta.cs.lonelytwitter;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Date;
import java.util.Observer;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

public class LonelyTwitterActivity extends Activity implements MyObserver {

    private static final String FILENAME = "file.sav"; // move to model
    private EditText bodyText; // move to view
    private ListView oldTweetsList; // move to model
    private ArrayList<Tweet> tweets; // move to view
    private ArrayAdapter<Tweet> adapter; // move to controller


    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.main); // goes in view (doesn't need to be moved)

        bodyText = (EditText) findViewById(R.id.body); // goes in view (doesn't need to be moved)
        Button saveButton = (Button) findViewById(R.id.save); // goes in view (doesn't need to be moved)
        oldTweetsList = (ListView) findViewById(R.id.oldTweetsList); // goes in view (doesn't need to be moved)

        saveButton.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                setResult(RESULT_OK);
                String text = bodyText.getText().toString(); // move to controller
                tweets.add(new NormalTweet(text)); // move to controller
                saveInFile();  // move to model
                adapter.notifyDataSetChanged(); // move to controller
            }
        });
    }

    @Override
    protected void onStart() {
        // TODO Auto-generated method stub
        super.onStart();
        loadFromFile(); // move to model
        if (tweets == null) {
            throw new RuntimeException();
        }
        adapter = new ArrayAdapter<Tweet>(this, R.layout.list_item, tweets); // move to controller
        oldTweetsList.setAdapter(adapter); // move to controller
    }

    // move loadFromFile to model
    private void loadFromFile() {
        try {
            FileInputStream fis = openFileInput(FILENAME);
            BufferedReader in = new BufferedReader(new InputStreamReader(fis));
            Gson gson = new Gson();
            // Following line based on https://google-gson.googlecode.com/svn/trunk/gson/docs/javadocs/com/google/gson/Gson.html retrieved 2015-09-21
            Type listType = new TypeToken<ArrayList<NormalTweet>>() {
            }.getType();
            tweets = gson.fromJson(in, listType);

        } catch (FileNotFoundException e) {
            tweets = new ArrayList<Tweet>();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    // move saveInFile to model
    private void saveInFile() {
        try {
            FileOutputStream fos = openFileOutput(FILENAME,
                    0);
            OutputStreamWriter writer = new OutputStreamWriter(fos);
            Gson gson = new Gson();
            gson.toJson(tweets, writer);
            writer.flush();
            fos.close();
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    // move myNotify to Controller
    public void myNotify(MyObservable observable) {
        adapter.notifyDataSetChanged();
    }
}