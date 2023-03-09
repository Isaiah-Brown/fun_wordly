package awesome.zaza.cat;

import android.util.Log;
import android.util.Pair;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Random;

public class Graph {

    public class WordNode{

        String word;
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




    ArrayList <String> solution = new ArrayList<String>();
    ArrayList <String> dictionary; // maybe move this to main
    ArrayList <String> words_visited = new ArrayList<String>();
    Random rand = new Random();



    LinkedList <WordNode> queue;

    public Graph(BufferedReader br) throws IOException {
        queue = new LinkedList<WordNode>();
        dictionary = generatePossibleWords(br);
    }


    public static ArrayList <String> generatePossibleWords(BufferedReader br) throws IOException {
        ArrayList <String> all_the_words = new ArrayList<String>();
        String word;
        while ((word = br.readLine()) != null){
            if (valid(word)){
                all_the_words.add(word);
                //Log.d("new word", word);
            }
        }
        return all_the_words;
    }
    public static boolean valid(String s) {

        if(s.length() != 4) {
            return false;
        }

        for(int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);
            int cOrd = c;
            if(cOrd < 97 || cOrd > 122) {
                return false;
            }

        }
        return true;
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


    public ArrayList<String> generateGame(){

        boolean validGame = false;
        String randomStart;
        int upperbound = dictionary.size();
        int random_idx = rand.nextInt(upperbound);

        while(!validGame){
            randomStart = dictionary.get(random_idx);
            solution = makingSolutionFrom(randomStart);
            if (solution.size() == 4) {
                validGame = true;
            }
        }
        return solution;
    }

    public ArrayList<String> makingSolutionFrom(String word){
        words_visited = new ArrayList<String>();
        solution = new ArrayList<String>();

        ArrayList <String> game = new ArrayList<String>();
        game.add(word);
        words_visited.add(word);
        WordNode currWN = new WordNode(word);

        while (game.size()<4){
            System.out.println(game.toString());
            ArrayList<String> listOfSuccessors = currWN.successors;
            if (listOfSuccessors.size() != 0 && notVisitedAll(listOfSuccessors)){
                //check to see if there  is no more successors, or all of the successors have been visited

                int random_idx = rand.nextInt(listOfSuccessors.size());
                String nextWord = listOfSuccessors.get(random_idx);

                if (!words_visited.contains(nextWord)){
                    words_visited.add(nextWord);
                    game.add(nextWord);
                    currWN = new WordNode(nextWord);
                }
                else{
                    continue;
                }
            }
            else{
                return game; // finish running because the start word is not viable
            }
        }

        return game;

    }


    public ArrayList<String> randomGame() {
        ArrayList<String> wordly = new ArrayList<>();
        int random_idx = rand.nextInt(dictionary.size());
        WordNode curr = new WordNode(dictionary.get(random_idx));
        wordly.add(curr.word);
        int count = 0;
        while(wordly.size() < 4 && count < 100) {
            count += 1;
            if (!curr.successors.isEmpty()) {
                random_idx = rand.nextInt(curr.successors.size());
                WordNode tmp = new WordNode(curr.successors.get(random_idx));
                if (!wordly.contains(tmp.word)) {
                    curr = tmp;
                    wordly.add(curr.word);
                }
            }
        }
        printArray("wordly", wordly);
        Log.d("count", String.valueOf(count));
        return wordly;

    }



    private boolean notVisitedAll(ArrayList <String> los){
        for (int i = 0; i < los.size(); i ++){
            String word = los.get(i);
            if (!words_visited.contains(word)){
                return true;
            }
        }
        return false;
    }

    public boolean playGame(String startWord, String endWord){
            queue.clear();
            boolean pathExist = false; //
            words_visited = new ArrayList<String>();

            WordNode currNode = new WordNode(startWord);
            queue.addFirst(currNode);

            while (!currNode.word.equals(endWord)){
                currNode = queue.removeLast();
                if (currNode.word.equals(endWord)){
                    solution = currNode.path;
                    solution.add(endWord);
                    pathExist = true;
                    break;
                }
                if (!words_visited.contains(currNode.word)){
                    for(int i = 0; i < currNode.successors.size(); i++){
                        String item = currNode.successors.get(i);
                        if (!words_visited.contains(item)){
                            WordNode newWN = new WordNode(item);

                            for (int j = 0; j < currNode.path.size(); j++){
                                String name = currNode.path.get(j);
                                newWN.path.add(name);
                            }
                            newWN.path.add(currNode.word);
                            queue.addFirst(newWN);
                        }
                    }
                }
                if (queue.size()==0){
                    pathExist = false;
                    break;
                } // probably not necessary
                words_visited.add(currNode.word);
            }

            if (pathExist){

                printArray("solution", solution);
                return true;
            }
            else{
                printArray("solution", solution);
                return false;
            }

    }


    public void printArray(String message, ArrayList<String> a) {
        String s = "";
        for(String ss : a) {
            s += ss + " ";
        }
        Log.d(message, s);

    }
}