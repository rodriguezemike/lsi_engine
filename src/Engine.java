/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */


import Jama.Matrix;
import java.io.File;

import java.io.FileOutputStream;

import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import java.util.Iterator;
import java.util.Map;


public class Engine {
    public class Result{
        private String link;
        private double score;
        
        Result(String link, double score){
            this.score = score;
            this.link = link;
        }
 
    
    }
    
    private Indexer index;
    private LSA lsi;
    
    public Engine(){
        index = null;
        lsi = null;
    }

    public void printResults(ArrayList<Result> results, int k){
        for(int i = 0; i < k; i++)
            System.out.println( i +": "+ results.get(i).link + "[Score] = "+results.get(i).score);
    }
    
    public ArrayList<Result> search(String query){
        ArrayList<String> docids = new ArrayList<>();
        docids.addAll(index.getDocids());
        
        String[] queryTokens = query.split("\\s+");
        Matrix q = Utilities.qVector(queryTokens,index);
        Matrix Cd = lsi.getDocConMatrix(); //ConceptxDoc Matrix
        Matrix m = lsi.getTransformMatrix(); // ConceptxTerms 
        Matrix Cq = m.times(q);//TermsxConcepts (Query)
        Map<Integer,Double> idSimMap = lsi.generateSimMap(Cq);
        
        ArrayList<Result> results = new ArrayList<>();
        Iterator resultsIterator = idSimMap.entrySet().iterator();  
        
        while(resultsIterator.hasNext()){
            Map.Entry e = (Map.Entry)resultsIterator.next();
            String link = docids.get((Integer)e.getKey());
            Result r = new Result(link,(Double)e.getValue());
            results.add(r);
        }

        ResultsComparator rc = new ResultsComparator();
        Collections.sort(results,rc);
        return results;
    }
    public static void main(String [ ] args) {
        try{
            File inputs = new File("data"); //Used for an example
            Indexer index = new Indexer();
            index.genIndex(inputs);
            index.generateScores();
            LSA lsi = new LSA(index.getTfidf(),index.getDocids());

            System.out.println("Serializing objects...");
            FileOutputStream indexFile;
            FileOutputStream lsiFile;

            indexFile = new FileOutputStream("bin/index.ser");
            lsiFile = new FileOutputStream("bin/lsi.ser");

            ObjectOutputStream out = new ObjectOutputStream(indexFile);
            out.writeObject(index);
            out = new ObjectOutputStream(lsiFile);
            out.writeObject(lsi);
            indexFile.close();

            System.out.println("Saved to bin/index.ser");
        }
        catch (Exception e){
            System.out.println(e.getMessage());
        }
    }
    public class ResultsComparator implements Comparator<Result>{
            public int compare(Result r1, Result r2){
                if (r1.score>r2.score)
                    return -1;
                else if (r1.score<r2.score)
                    return 1;
                else
                    return 0;
            }
        }
}
