/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */


import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;
import Jama.Matrix;
import Jama.SingularValueDecomposition;
import java.util.ArrayList;

/**
 *
 * @author penguin
 */
public class LSA {
    private Matrix A;
    private Matrix D;
    private Matrix M;
    private SingularValueDecomposition svd; 
    
    LSA(TreeMap<String,TreeMap<Integer,Double>> tfidf,ArrayList<String> docid){
        A = new Matrix(tfidf.size(),docid.size(),0.0D);
        Iterator outerIt = tfidf.entrySet().iterator();
        
        for(int i = 0;i<tfidf.size();i++){
            Map.Entry termVector = (Map.Entry)outerIt.next();
            TreeMap<Integer,Double> docs = (TreeMap)termVector.getValue();
            for(int j = 0; j< docid.size();j++){
                if(docs.containsKey(j))
                    A.set(i, j, docs.get(j));
            }
        }  
        svd = A.svd();
        
        M = svd.getS().inverse().times(svd.getU().transpose());
        D = M.times(A);
    }
    public SingularValueDecomposition getsvd(){return svd;}
    public Matrix getDocConMatrix(){return D;}
    public Matrix getTransformMatrix(){return M;}
    double cosineSimularity(Matrix Vd, Matrix Vq){return Vd.arrayTimes(Vq).norm1()/(Vd.normF()*Vq.normF());}
    
    Map<Integer,Double> generateSimMap(Matrix Vq){
        TreeMap<Integer,Double> map = new TreeMap<>();
        for(int i = 0; i < A.getRowDimension(); i++){
            for(int j = 0 ; j < A.getColumnDimension(); j++){
                Matrix Vd = D.getMatrix(0, A.getRowDimension()-1, j, j); // This the  document concept vector
                double simularity = cosineSimularity(Vd,Vq); // score for the doc
                if(simularity > 0.0){
                    map.put(j,simularity);
                }
            }
        }
        return map;
    }
}
