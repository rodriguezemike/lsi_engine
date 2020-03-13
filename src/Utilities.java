

import Jama.Matrix;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Scanner;
import java.util.Set;
/**
 * A collection of utility methods for text processing.
 */
public class Utilities {
    
    public static ArrayList<String> tokenizeFile(File input) throws FileNotFoundException{
            // Alg: Use scanner to read line , using regular expressions to normalize data
            // add normalied data to arraylist word by word via space split 
            Scanner scanner;
            ArrayList<String> tokens = new ArrayList<>();
            scanner = new Scanner(input);
            String buffer = scanner.nextLine();

            while(scanner.hasNextLine()){
                    buffer = buffer.toLowerCase(); // Normalize to lower
                    buffer = buffer.replaceAll("[^a-z\\s]"," "); // replace all non alpha characters with ""
                    String[] temp = buffer.split("\\s+"); // Split by all whitespace including tabs
                    for(String s:temp)
                        if (!s.isEmpty()&& s.length()<45 && s.length()>1) 
                                tokens.add(s); // if s is not "" from replaceAll then add to arrayList

                    buffer = scanner.nextLine();
            }
            scanner.close();
            return tokens;
    }
    
    public static HashMap<String,Integer> countWords(ArrayList<String> tokens){
        HashMap<String,Integer> wordMap = new HashMap<>();
        if(tokens == null)
                return wordMap;
            // Count words
            for(String s: tokens)
                wordMap.compute(s, (k,v) -> v == null ? 1: v+1); // if not in map already put new value. if in map, add value by 1
        return wordMap;
    }

    public static Matrix qVector(String[] qTokens,Indexer index){
        int termid;
        Matrix Q = new Matrix(index.getTerms().size(),1,0.0d); // M by 1 Matrix to mark where the terms are
        HashSet<String> terms = index.getTerms();
        for(String s: qTokens){
            if(terms.contains(s)){
                 termid = Utilities.getIndex(terms, s);
                 double qScore = 1/qTokens.length;
                 Q.set(termid, 1, qScore); // set those term ids to 1
            }
        }
        return Q;
    }
    
    public static int getIndex(Set<? extends Object> input,Object toFind){
        int i = 0;
        for(Object entry:input){
            if(entry.equals(toFind))
                return i;
            i++;
        }
        return -1; 
    }
    
    public static String getLinkName(File inFile) throws FileNotFoundException, IOException{
        String link;
        String buffer;
        
        BufferedReader reader = new BufferedReader(new FileReader(inFile));
        buffer = reader.readLine();
        link = reader.readLine(); // links in the second line thanks to the crawler.
        reader.close();
        
        return link;
    }
}