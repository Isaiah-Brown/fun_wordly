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

        words = new ArrayList<>(Arrays.asList("hack", "lack", "lace", "lake"));
        g = initGraph();
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

    private void showStartPage(){

        Intent intent = new Intent(getApplicationContext(), wordly_explained.class);
        startActivity(intent);

        SharedPreferences prefs = getSharedPreferences("prefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putBoolean("firstStart", false);
        editor.apply();
    }

    public void updateEditText() {
        EditText et1 = findViewById(R.id.start_text);
        EditText et2 = findViewById(R.id.end_text);
        et1.setText(words.get(0));
        et2.setText(words.get(3));
    }

    public void makeNewPuzzle() {
        words = new ArrayList<>(Arrays.asList("lake", "lace", "lack", "hack"));
        updateEditText();
    }

    public void startGame() {
        EditText et1 = findViewById(R.id.start_text);
        EditText et2 = findViewById(R.id.end_text);
        boolean valid = g.playGame(et1.getText().toString(), et2.getText().toString());
        if (valid) {
            Intent i = new Intent(this, Game.class);
            //Log.d("main", g.solution.get(0));
            i.putStringArrayListExtra("words", g.solution);
            startActivity(i);
        }

    }

    public void testGraph() {
        try{
            BufferedReader br = new BufferedReader(new InputStreamReader(getAssets().open("words/words_test.txt")));
            Graph g = new Graph(br);
        }catch (Exception e) {
            e.printStackTrace();
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