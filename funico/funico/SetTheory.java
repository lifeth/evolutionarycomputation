/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package funico;

import static funico.language.FunicoConstants.NATURAL;
import static funico.language.FunicoConstants.SUCCESSOR;
import funico.language.Term;
import java.util.Iterator;
import java.util.Random;
import java.util.Set;

/**
 *
 * @author Camiku
 */
public class SetTheory {
    
    private static String element;
    private static int ran, i;
    private static Term parent, child, leaf;
    private static StringBuilder sb = new StringBuilder();
    
    public static String getRandomElementSet(Set<String> set) {
        if (set.isEmpty()) {
            return null;
        } else {
            element = null;
            i = 0;
            Random r = new Random();
            ran = r.nextInt(set.size());
            for (Iterator<String> it = set.iterator(); it.hasNext();) {
                element = it.next();
                if (i == ran) {
                    return element;
                } else {
                    i++;
                }
            }
            return element;
        }
    }
    
    public static boolean isSubSet(Set<String> A, Set<String> B){
        for (Iterator<String> iterator = A.iterator(); iterator.hasNext();) {
            if(!B.contains(iterator.next())){
                return false;
            } 
        }
        return true;
    }
    
    public static Term generateTreeNumber(int n) {
        Term node = new Term();
        return generateTreeNumber(n, node);
    }
    
    public static Term generateTreeNumber(int n, Term root) {
        if (n == 0) {
            root.setType(NATURAL);
            root.setValue("0");
        } else {
            root.setType(SUCCESSOR);
            root.setValue("s" );
            child = new Term("0", NATURAL, null);
            for (i = 1; i < n; i++) {
                parent = new Term("s", SUCCESSOR, null);
                child.setParent(parent);
                parent.addChild(child);
                child = parent;
            }
            root.addChild(child);
            child.setParent(root);
        }
        return root;
    }
    
    public static String printArrayInt(int[] array) {
        sb.delete(0, sb.length());
        sb.append("[");
        for (i = 0; i < array.length; i++) {
            sb.append(array[i]);
            if(i < array.length - 1){
                sb.append(" ");
            }else{
                sb.append("]");
            }
        }
        return sb.toString();
    }
    
    public static String printArrayDouble(double[] array) {
        sb.delete(0, sb.length());
        sb.append("[");
        for (i = 0; i < array.length; i++) {
            sb.append(array[i]);
            if(i < array.length - 1){
                sb.append(" ");
            }else{
                sb.append("]");
            }
        }
        return sb.toString().replace('.', ',');
    }
    
    public static int sumArrayInt(int[] array) {
        int S = 0;
        for (i = 0; i < array.length; i++) {
            S += array[i];
        }
        return S;
    }
    
    public static double sumArrayDouble(int[] array) {
        double S = 0;
        for (i = 0; i < array.length; i++) {
            S += array[i];
        }
        return S;
    }
    
    public static double[] getPercents(int[] array, int sum){
        double[] result = new double[array.length];
        for (int i = 0; i < result.length; i++) {
            result[i] = 100.0 * array[i] / sum;
        }
        return result;
    }
    
}
