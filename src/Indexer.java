/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */


import Jama.Matrix;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;


public class Indexer implements java.io.Serializable {
    private long corpusTermCount;
    private static HashSet<String> terms;
    private int corpus;
    private static File input; 
    private static ArrayList<String> docids;// Use indexes as index for docs
    private static TreeMap<String,TreeMap<Integer,Double>> tfidf; // Term -> docID: tf-idf Score 
    private static Matrix DocsConcepts;
    
    public Indexer(){
        corpusTermCount = 0;
        corpus = 0;
        docids = new ArrayList<>();
        terms = new HashSet<>();
        tfidf = new TreeMap<>();
    }
   
    public ArrayList<String> getDocids(){return docids;}
    public TreeMap<String,TreeMap<Integer,Double>> getTfidf(){return tfidf;}
    public HashSet<String> getTerms(){return terms;}
    
    
    public double idf(int docsIncluded){return Math.log10(corpus/1+docsIncluded);}
 
    // Look through all the files and begin to generate term ids, and doc ids.
    public void genIndex(File directory) throws FileNotFoundException, IOException{
        System.out.println("Generating Index.. This may take a while...");
        HashMap<String,Integer> docWordMap;
        File[] directoryFiles = directory.listFiles();
        
        if(directoryFiles !=null){
            for(File child:directoryFiles){
                //Build Ids
                ++corpus;
                System.out.println(corpus+"/"+directoryFiles.length);
                docids.add(Utilities.getLinkName(child));
                ArrayList<String> tokens = Utilities.tokenizeFile(child);
                corpusTermCount += tokens.size();
                terms.addAll(tokens);
                //Build Index
                docWordMap = Utilities.countWords(tokens); // count the document terms
            
                for(String s: docWordMap.keySet()){                   
                    double count = docWordMap.get(s); // Count. Will generate tf-idf
                    int docIndex = corpus; // get a doc index
                    
                    if(!tfidf.containsKey(s)){ // Starts off as a count map. Need all docs to calculate tfidfs
                        TreeMap<Integer,Double> docIdTfidf = new TreeMap<>();
                        docIdTfidf.put(docIndex, count);
                        tfidf.put(s, docIdTfidf);
                    }
                    //term index exists inside mapping already
                    else{
                        TreeMap<Integer,Double> docIdTfidf = tfidf.get(s);
                        docIdTfidf.put(docIndex,count);
                        tfidf.put(s, docIdTfidf);
                    }
                }
            } 
        }
        else
            System.out.println("Directory files are empty");
    }
    
    //Creates the tf-idf scores for each entry
    public void generateScores(){
        Iterator outterIterator = tfidf.entrySet().iterator();
        while(outterIterator.hasNext()){
            Map.Entry e = (Map.Entry)outterIterator.next();
          
            Iterator innerIterator = tfidf.get(e.getKey()).entrySet().iterator();
            while(innerIterator.hasNext()){     
                Map.Entry ie = (Map.Entry)innerIterator.next();
                double tfidfScore = (double)ie.getValue()*idf(tfidf.get(e.getKey()).size());
                
                System.out.println(e.getKey()+" "+ie.getKey()+" "+ie.getValue());
            }
        }
    }
 
    /*
    public void indexToFile() throws IOException{
        System.out.println("Writing to file...");
        PrintWriter fwriter = new PrintWriter(new FileWriter("indices.txt"));
        Iterator mapIt = tfidf.entrySet().iterator();
        while(mapIt.hasNext()){
            Map.Entry entry = (Map.Entry)mapIt.next();
            Iterator innerIT = tfidf.get(entry.getKey()).entrySet().iterator();
            System.out.println("Entry: "+entry.getKey());
            while(innerIT.hasNext()){
                Map.Entry innerEntry = (Map.Entry)innerIT.next();
                //prints out the term id(entry.getKey() + docId (innerEntry.getKey()) + tfidf value (innerEntry.getValue())
                fwriter.println(entry.getKey()+" "+innerEntry.getKey()+" "+innerEntry.getValue());
            }
        }
    }
    */
}
