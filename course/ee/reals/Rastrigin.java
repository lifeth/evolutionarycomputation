/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ee.reals;

import java.util.Random;

/**
 *
 * @author Lifeth
 */
public class Rastrigin {
    
    public static double rastrigin(double x){
        double a = 10.0;
        return x*x - a*Math.cos(2.0*Math.PI*x);
    }
    
    public static double rastrigin(int n, double[] x){
        double a = 10.0;
        double f = a*n;
        for(int i  = 0; i < n; i++){
            f += rastrigin(x[i]);
        }
        return f;
    }
    
    public static double[] uniformdist(int n){
        Random r = new Random();
        double low = -5.12;
        double high = 5.12;
        double ram [] = new double [n];
        
        for (int i = 0; i < n; i++) {
            ram [i] = (low + (high - low) * r.nextDouble());
        }
       return ram;
    }
    
    public static double[] normaldist(int n, double prob[]){
       Random r = new Random();
       double ram [] = new double [n];

       double rr =  r.nextDouble() * 1;
    	double acum = 0;
    	
    	for (int i = 0; i < n; i++) {
			
    		acum += prob[i];
			
			if(acum >= rr){
				ram[i] = prob[i];
			}
		}
    	
    	return ram;
    }
    
        /*  sumF = suma de todos los fitness
    		sumR = rand(0, 1)sumF
    		sumP = 0, j = 0
    		Repetir
    		j = j + 1
    		sumP = sumP + fitnessj
    		Hasta (sumP ≥ sumR)
    		Seleccionado= j*/
    
    public static double[] delta(int n){
        Random r = new Random();
         double d[] = new double[n];
         for (int i = 0; i < n; i++) {
             // -1 y 1 >>>> 
             d[i] = r.nextDouble()*2-1;
         }
        return d;
    }
    
    public static double[] hillClimbing(int n, int it){
        double x[] = uniformdist(n);        
        double f = rastrigin(n, x);
        double y[] = new double [n];
        
        for (int i = 0; i < it; i++) {
            
        	double d[] = delta(n);
            
            for (int j = 0; j < n; j++) {
                 y[j] = x[j] + d[j];
                
                 if(y[j] > 5.12)
                     y[j] = 5.12;
                 
                 if(y[j]<-5.12)
                      y[j] = -5.12;
            }
            
           double fy = rastrigin(n, y);
           System.out.println(f + " >> "+fy);
           
           if(fy <= f){
               x = y;
               f = fy;
           }
           
        }
        return x;
    }
    
    public static double normaldistri(int n, double prob[], int it){
        Random r = new Random();
        double rr =  r.nextDouble() * 1;
    	double acum;
    	double p =  0;
       // int count = 0;
        
        double[] sel =  new double[3];
        
        for (int i = 0; i < n; i++) {
        	acum = 0;
        	
        	for (int j = 0; j < prob.length; j++) {
        		
        		acum += prob[j];
    			
    			if(acum >= rr){
    				p = prob[j];
    				break;
    			}
    		}
        	
        	sel[i] = p;
		}
 
    	return p;
    }

    public static void main(String[] args) {
    
      //double x[] = {2.0, 0.0, -1.0};
      //System.out.println("Value: "+rastrigin(3, x));
      hillClimbing(10, 100000);
     System.out.println("Value: "+normaldist(3, new double[]{0.5, 0.3, 0.2}));
     
    // System.out.println(normaldistri(3, new double[]{0.5, 0.3, 0.2}, 1000));
  }
}
