package awesome.zaza.cat;


import androidx.appcompat.app.AppCompatActivity;


import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;

import android.view.View;

import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;

import java.io.InputStreamReader;
import java.util.ArrayList;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Main extends AppCompatActivity {

    ArrayList<String> words; // the words from left to right to be used in the game

    Graph g;
    boolean gameInProgress = false;
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

        updateEditText(); // sets edit text 1 to words[0] and edit text 2 to words[-1]

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
        String start = et1.getText().toString();
        String end = et2.getText().toString();
        if(!validInput(start, end)) {
            return;
        }

        if (start.equals(words.get(0)) && end.equals(words.get(words.size() - 1))) {
            Intent i = new Intent(this, Game.class);
            i.putStringArrayListExtra("words", words);
            startActivity(i);
        } else {
            if(gameInProgress) {
                return;
            } else {
                gameInProgress = true;
            }
            TextView tv = findViewById(R.id.loading);
            tv.setVisibility(View.VISIBLE);
            Animation a = AnimationUtils.loadAnimation(this, R.anim.spins);
            a.setRepeatCount(10);
            tv.setAnimation(a);
            tv.animate();

            LoadExecutor le = new LoadExecutor();
            le.load(start, end, lcb);
        }


    }

    LoadCallback lcb = new LoadCallback() {
        @Override
        public void onComplete(boolean valid) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (valid) {
                        if(g.solution != null && g.solution.size() > 2) {
                            words = g.solution;
                            Intent i = new Intent(getApplicationContext(), Game.class);
                            i.putStringArrayListExtra("words", words);
                            startActivity(i);
                        } else {
                            butterToast("Please enter different words");
                        }
                    } else {
                        butterToast("Please enter different words");
                    }
                    TextView tv = findViewById(R.id.loading);
                    tv.setVisibility(View.INVISIBLE);
                    gameInProgress = false;
                }
            });
        }
    };

    interface LoadCallback {
        void onComplete(boolean valid);
    }

    public class LoadExecutor {
        public void load(String start, String end, final LoadCallback lcb) {
            ExecutorService es = Executors.newFixedThreadPool(1);
            es.execute(new Runnable() {
                @Override
                public void run() {
                    boolean valid = g.playGame(start, end);
                    lcb.onComplete(valid);
                }
            });

        }
    }

    public Graph initGraph() {
        try{
            BufferedReader br = new BufferedReader(new InputStreamReader(getAssets().open("words/words_unix.txt")));
            Graph g = new Graph(br);
            return g;
        }catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public boolean validInput(String start, String end) {
        Log.d("Start and end", start + " " + end);
        Log.d("lengths", String.valueOf(start.length()) + " " + String.valueOf(end.length()));
        if(start.length() != 4 || end.length() != 4) {
            butterToast("words must be four letters long");
            return false;
        }
        for(int i = 0; i < start.length(); i++) {
            char startC = start.charAt(i);
            char endC = end.charAt(i);
            int sOrd = startC;
            int eOrd = endC;
            if(sOrd < 97 || sOrd > 122 || eOrd < 97 || eOrd > 122) {
                butterToast("words must be lowercase a-z");
                return false;
            }

        }
        return true;
    }


    public void butterToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }


}