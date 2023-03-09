package awesome.zaza.cat;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;

import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.Random;

import javax.net.ssl.HttpsURLConnection;


public class Game extends AppCompatActivity implements RecyclerViewInterface {
    ArrayList<String> words;
    ArrayList<Integer> visibility;
    RecyclerView rv;

    private View decorView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        decorView = getWindow().getDecorView();
        decorView.setOnSystemUiVisibilityChangeListener(new View.OnSystemUiVisibilityChangeListener() {
            @Override
            public void onSystemUiVisibilityChange(int i) {
                if (i == 0) {
                    decorView.setSystemUiVisibility(hideSystemBars());
                }
            }
        });

        Intent intent = getIntent();
        words = intent.getStringArrayListExtra("words");

        if(savedInstanceState != null) {
            visibility = savedInstanceState.getIntegerArrayList("visibility");
        } else{
            visibility = new ArrayList<Integer>(Collections.nCopies(words.size(), 0));
            visibility.set(0, 1);
            visibility.set(visibility.size()-1, 1);
        }
        initRecyclerView();

        Button hint = findViewById(R.id.hintButton);
        hint.setOnClickListener(view -> giveHint());

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                loopImages();
            }
        }, 1000);

        if (gameIsOver()){
            endGame();
        }

    }
    @Override
    protected void onSaveInstanceState(Bundle outState) { //https://www.youtube.com/watch?v=TcTgbVudLyQ
        super.onSaveInstanceState(outState);
        outState.putIntegerArrayList("visibility", visibility);
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus){
        super.onWindowFocusChanged(hasFocus);
        if(hasFocus){
            decorView.setSystemUiVisibility(hideSystemBars());
        }
    }

    private int hideSystemBars() { //https://www.youtube.com/watch?v=zC2CgDExCeI
        return View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_FULLSCREEN
                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION;
    }


    public void initRecyclerView() {
        LinearLayoutManager llm = new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.HORIZONTAL, false);
        rv = findViewById(R.id.recycle);
        rv.setLayoutManager(llm);
        RecyclerViewAdapter rva = new RecyclerViewAdapter(this, words, visibility, this);
        rv.setAdapter(rva);

    }

    public void updateRecyclerView(int idx) {
        visibility.set(idx, 1);
        RecyclerViewAdapter rva = new RecyclerViewAdapter(this, words, visibility, this);
        rv.setAdapter(rva);
    }

    public void makeAltertDialog(int pos) {
        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        final EditText et = new EditText(this);
        alert.setTitle("Guess a word");
        alert.setView(et);
        alert.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                String word = et.getText().toString();
                checkIfValid(word, pos);
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
    public void onItemClick(int position) {
        makeAltertDialog(position);
    }

    public void checkIfValid(String word, int pos) {

        if (word.equals(words.get(pos))) {
            updateRecyclerView(pos);
            if (gameIsOver()) {
                endGame();
            }
            return;
        }

        if (word.length() != 4) {
            butterToast("Word must be four letters!!");
        } else {
            butterToast("That's not the word I had in mind!!   ;)");
        }
    }

    public boolean gameIsOver() {
        int sum = 0;
        for(int num : visibility) {
            sum += num;
        }
        return sum == visibility.size();
    }

    public void endGame() {   //do whatever needs to be done at end of game
        ImageView hintPhotos = findViewById(R.id.hintImage);
        ImageView heart = findViewById(R.id.heart);
        hintPhotos.setVisibility(View.INVISIBLE);

        Animation a = AnimationUtils.loadAnimation(this, R.anim.fade_in); //https://www.youtube.com/watch?v=1CllXl9n7iY
        heart.setAnimation(a);
        heart.animate();
        butterToast("You WON!!!");

        View v = findViewById(R.id.game_layout);
        v.setOnClickListener(view -> {
            Intent i = new Intent(getApplicationContext(), Main.class);
            startActivity(i);
        });
    }

    public void butterToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    public void giveHint() {

        int idx = visibility.indexOf(0);
        String last = words.get(idx - 1);
        String curr = words.get(idx);
        for (int j = 0; j < last.length(); j++) {
            if (last.charAt(j) != curr.charAt(j)) {
                butterToast(String.valueOf(curr.charAt(j)));
                return;
            }
        }
    }


    ImageCallback ic = new ImageCallback() {
        @Override
        public void onComplete(Bitmap img) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    ImageView iv = findViewById(R.id.hintImage);
                    if (img != null) {
                        iv.setImageBitmap(img);
                    } else {
                        iv.setImageResource(R.drawable.wordly_transparent_background);
                    }
                    iv.setVisibility(View.VISIBLE);
                }
            });
        }
    };

    interface ImageCallback {
        void onComplete(Bitmap img);
    }


    public class ImageExecutor {
        public void showImage(final ImageCallback ic) {
            ExecutorService es = Executors.newFixedThreadPool(1);
            es.execute(new Runnable() {
                @Override
                public void run() {
                    Bitmap img = getImage();
                    ic.onComplete(img);
                }
            });
        }
    }


    public Bitmap getImage() {  //https://pixabay.com/api/docs/

        URL apiURL;
        String imageStr;
        HttpsURLConnection con = null;
        Bitmap img = null;
        StringBuffer data;

        String word = words.get(visibility.indexOf(0));
        if (isConnected()) {
            try {
                apiURL = new URL("https://pixabay.com/api/?key=34180268-bc5019e2f3cc27cb9ce36eb82&q=" + word + "&image_type=photo");
                con = (HttpsURLConnection) apiURL.openConnection();
                con.setRequestMethod("GET");
                con.connect();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

            try {
                BufferedReader in = new BufferedReader(new InputStreamReader(apiURL.openStream()));
                data = new StringBuffer();
                String curLine;
                while ((curLine = in.readLine()) != null) {
                    data.append(curLine);
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

            try {
                JSONObject jsonAPI = new JSONObject((data.toString()));
                JSONArray images;
                try {
                    images = jsonAPI.getJSONArray("hits");
                    Log.d("length", String.valueOf(images.length()));
                    if (images.length() < 1) {
                        return null;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    return null;
                }
                Random r = new Random();
                int idx = r.nextInt(10);
                while (idx > images.length()) {
                    idx = r.nextInt(10);
                }
                JSONObject image = images.getJSONObject(idx);
                imageStr = image.getString("webformatURL");
            } catch (JSONException e) {
                return null;
            }

            try {
                URL imageURL = new URL(imageStr);
                InputStream in = new BufferedInputStream(imageURL.openStream());
                ByteArrayOutputStream out = new ByteArrayOutputStream();
                byte[] buf = new byte[1024];
                int n = 0;
                while (-1 != (n = in.read(buf))) {
                    out.write(buf, 0, n);
                }
                out.close();
                in.close();

                byte[] response = out.toByteArray();
                img = BitmapFactory.decodeByteArray(response, 0, response.length);

            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            con = null;
            return img;
        } else {
            return null;
        }

    }

    public void loopImages() {  //https://stackoverflow.com/questions/22860546/android-changing-image-with-time-interval
        ImageView iv = findViewById(R.id.hintImage);
        new Runnable() {
            public void run() {
                if(!gameIsOver()) {
                    ImageExecutor ie = new ImageExecutor();
                    ie.showImage(ic);
                    iv.postDelayed(this, 4000);
                }
            }
        }.run();
    }

    public boolean isConnected(){  //https://stackoverflow.com/questions/5474089/how-to-check-currently-internet-connection-is-available-or-not-in-android
        ConnectivityManager connectivityManager = (ConnectivityManager)getSystemService(CONNECTIVITY_SERVICE);
        return (connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() == NetworkInfo.State.CONNECTED || connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() == NetworkInfo.State.CONNECTED);

    }
}




