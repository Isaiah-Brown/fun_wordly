package awesome.zaza.cat;

import android.util.Log;
import android.util.Pair;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;

public class Graph {

    public class WordNode{

        String word;
        Integer priority;
        ArrayList <String> path = new ArrayList<String>(); // the list that contains all the words that led to this node
        ArrayList <String> successors = new ArrayList<String>(); // the list that contains all possible expansion

        WordNode(String w) {
            word = w;
            for (String some_word : dictionary) {
                if (check_matched_letters(word, some_word) == word.length()-1){ // find words that are 1 letter differnt
                    successors.add(some_word); // put all possible words in successors
                }
            }
        }
    }

    String fileName = "../../../../assets/words/words_test.txt";
    ArrayList <String> solution = new ArrayList<String>();
    ArrayList <String> dictionary; // maybe move this to main
    ArrayList <String> words_visited = new ArrayList<String>();



    LinkedList <WordNode> queue;

    public Graph(BufferedReader br) throws IOException {
        queue = new LinkedList<WordNode>();
        dictionary = generatePossibleWords(br);
    }


    public static ArrayList <String> generatePossibleWords(BufferedReader br) throws IOException {
            ArrayList <String> all_the_words = new ArrayList<String>();
            String word;
            while ((word = br.readLine()) != null){
                if (word.length() == 4){
                    all_the_words.add(word);
                    //Log.d("new word", word);
                    System.out.println(word);
                }
            }
            return all_the_words;
    }

    public static int check_matched_letters(String str1, String str2){ //used as heuristics, and for finding successors
        if (str1.length() == str2.length()){
            int matched = 0;
            for (int i = 0; i < str1.length(); i++){
                if(str1.charAt(i) == str2.charAt(i)){
                    matched += 1;
                }
            }
            return matched;
        }
        else{
            return -1;
        }
    }

    public boolean playGame(String startWord, String endWord){


        boolean pathExist = false; //

        WordNode currNode = new WordNode(startWord);
        queue.addFirst(currNode);

        while (!currNode.word.equals(endWord)){
            currNode = queue.pop();
            if (currNode.word.equals(endWord)){
                solution = currNode.path;
                pathExist = true;
                break;
            }
            if (!words_visited.contains(currNode.word)){
                for(int i = 0; i < currNode.successors.size(); i++){
                    String item = currNode.successors.get(i);
                    if (!words_visited.contains(item)){
                        WordNode newWN = new WordNode(item);
                        queue.addFirst(newWN);
                    }
                }
            }
            if (queue.size()==0){
                pathExist = false;
            } // probably not necessary
            words_visited.add(currNode.word);
        }

        if (pathExist){
            printArray(solution);
            return true;
        }
        else{
            printArray(solution);
            return false;
        }
    }


    public void printArray(ArrayList<String> a) {
        for(String s : a) {
            Log.d("solution", s);
        }
    }
}
