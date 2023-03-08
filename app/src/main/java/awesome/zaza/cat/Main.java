package awesome.zaza.cat;


import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;

public class Main extends AppCompatActivity {

    //commentq

    ArrayList<String> words; // the four words from left to right to be used in the game

    Graph g;
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        g = initGraph();
        if(savedInstanceState != null) {
            words = savedInstanceState.getStringArrayList("words");
        } else {
            makeNewPuzzle();
        }
        //comment

        updateEditText(); // sets edit text 1 to words[0] and edit text 2 to words[3]

        Button puzzle = findViewById(R.id.puzzle_button);
        puzzle.setOnClickListener(view -> makeNewPuzzle()); // changes words to a new set of 4 words

        Button play = findViewById(R.id.play_button);
        play.setOnClickListener(view -> startGame()); // starts the game


        //implementing the fresh intall pop up thingy
        SharedPreferences prefs = getSharedPreferences("prefs", MODE_PRIVATE);
        boolean firstStart = prefs.getBoolean("firstStart", true); //check to see if this is the first start or not

        if(firstStart){
            showStartPage();
        }

    }

    @Override
    protected void onSaveInstanceState(Bundle outState) { //https://www.youtube.com/watch?v=TcTgbVudLyQ
        super.onSaveInstanceState(outState);
        outState.putStringArrayList("words", words);
    }

    private void showStartPage(){

        Intent intent = new Intent(getApplicationContext(), wordly_explained.class);
        startActivity(intent);

        SharedPreferences prefs = getSharedPreferences("prefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putBoolean("firstStart", false);
        editor.apply();
    }

    public void updateEditText() {
        if(words != null) {
            EditText et1 = findViewById(R.id.start_text);
            EditText et2 = findViewById(R.id.end_text);
            et1.setText(words.get(0));
            et2.setText(words.get(words.size() - 1));
        }
    }

    public void makeNewPuzzle() {
        try {
            words = new ArrayList<>(g.randomGame());
            while(words.size() != 4) {
                words = new ArrayList<>(g.randomGame());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        updateEditText();

    }

    public void startGame() {
        EditText et1 = findViewById(R.id.start_text);
        EditText et2 = findViewById(R.id.end_text);
        Intent i = new Intent(this, Game.class);
        if (!et1.getText().toString().equals(words.get(0)) && !et2.getText().toString().equals(words.get(words.size() - 1))) {
            boolean valid = g.playGame(et1.getText().toString(), et2.getText().toString());
            if (valid) {
                //Log.d("main", g.solution.get(0));
                if(g.solution != null) {
                    i.putStringArrayListExtra("words", g.solution);
                    startActivity(i);
                }
            } else {
                butterToast("Please enter different words");
            }
        } else {
            i.putStringArrayListExtra("words", words);
            startActivity(i);
        }


    }


    public Graph initGraph() {
        try{
            BufferedReader br = new BufferedReader(new InputStreamReader(getAssets().open("words/words_simple.txt")));
            Graph g = new Graph(br);
            return g;
        }catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }


    public void butterToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
}