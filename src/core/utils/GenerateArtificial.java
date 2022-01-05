package src.core.utils;

import java.util.*;
import java.io.*;

public class GenerateArtificial
{
    static final int LIM = 1200000;
    public static void main(String[] args) throws Exception{
		String output = args[0];
        double prob = Double.parseDouble(args[1]);
		int R = Integer.parseInt(args[2]);
        int size = Integer.parseInt(args[3]);
        PrintWriter pw = new PrintWriter(output);
        //int size = 4;
        //double prob = 0.001;
        for(int ii=0; ii<LIM; ii++){
            double[] x = new double[size];
            for(int i=0; i<size; i++)
                x[i] = Math.random()*2 -1;
            double nr = 0;
            for(int i=0; i<size; i++)
                nr += x[i]*x[i];
            nr = Math.sqrt(nr);
            double r = Math.random();
            if(Math.random() < prob)
                r = R;
            for(int i=0; i<size; i++)
                x[i] = x[i]*r/nr;

            for(int i=0; i<size-1; i++)
                pw.print(x[i]+",");
            pw.println(x[size-1]);
        }
        
	}
	
}