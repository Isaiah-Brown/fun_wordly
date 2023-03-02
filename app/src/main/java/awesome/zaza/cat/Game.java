package awesome.zaza.cat;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.util.ArrayList;
import java.util.Collections;

public class Game extends AppCompatActivity implements RecyclerViewInterface {
    ArrayList<String> words;
    ArrayList<Integer> visibility;
    RecyclerView rv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        Intent i = getIntent();
        words = i.getStringArrayListExtra("words");
        initListView();

    }

    public void initListView() {
        visibility = new ArrayList<Integer>(Collections.nCopies(words.size(), 0));
        visibility.set(0, 1);
        visibility.set(visibility.size()-1, 1);
        LinearLayoutManager llm = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        rv = findViewById(R.id.recycle);
        rv.setLayoutManager(llm);
        RecyclerViewAdapter rva = new RecyclerViewAdapter(this, words, visibility, this);
        rv.setAdapter(rva);
        Log.d("size", String.valueOf(words.size()));

    }

    public void updateRecyclerView(int idx) {
        visibility.set(idx, 1);
        RecyclerViewAdapter rva = new RecyclerViewAdapter(this, words, visibility, this);
        rv.setAdapter(rva);
    }



    public void makeAltertDialog() {
        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        final EditText et = new EditText(this);
        alert.setTitle("Guess a word");
        alert.setView(et);
        alert.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                String word = et.getText().toString();
                for(int i = 0; i < words.size(); i++) {
                    if (word.equals(words.get(i))) {
                        updateRecyclerView(i);
                        if(gameIsOver()) {
                            endGame();
                        }
                    }
                }
                dialog.cancel();
            }
        });
        alert.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                dialog.cancel();
            }
        });
        alert.show();
    }
    @Override
    public void onItemClick() {
        makeAltertDialog();
    }

    public boolean gameIsOver() {
        int sum = 0;
        for(int num : visibility) {
            sum += num;
        }
        return sum == visibility.size();
    }

    public void endGame() {   //do whatever needs to be done at end of game
        butterToast("You WON!!!");
    }

    public void butterToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

}