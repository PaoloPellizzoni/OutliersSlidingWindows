package src.core.utils;

import java.util.*;
import java.io.*;

public class InjectOutliers
{
    static final int LIM = 1200000;
    public static void main(String[] args) throws Exception{
        String input = args[0];
        String output = args[1];
        double prob = Double.parseDouble(args[2]);
        double r = Double.parseDouble(args[3]);
        int size = Integer.parseInt(args[4]);
        IO io = new IO(input);
        PrintWriter pw = new PrintWriter(output);

        int cnt = 0;
        double[] mean = new double[size];
        double[] first = new double[size];
        double diam = 0;
        while(io.hasMoreTokens()){
            cnt++;
            double[] x = new double[size];
            for(int i=0; i<size; i++){
                x[i] = io.getDouble();
                mean[i] += x[i];
            }
            if(cnt==1)
                first = x;
            else{
                double d = 0;
                for(int i=0; i<size; i++) d += (first[i]-x[i])*(first[i]-x[i]);
                d = Math.sqrt(d);
                diam = Math.max(diam, d);
            }
            if(cnt == LIM)
                break;
        }
        for(int i=0; i<size; i++){
            mean[i] /= cnt;
        }
        //io.close();
        io = new IO(input);
        cnt = 0;
        int cnto = 0;
        System.out.println("Mean: "+Arrays.toString(mean)+"  Diam: "+diam);
        while(io.hasMoreTokens()){
            double[] x = new double[size];
            for(int i=0; i<size; i++)
                x[i] = io.getDouble();

            for(int i=0; i<size-1; i++)
                pw.print(x[i]+",");
            pw.println(x[size-1]);

            if(Math.random() < prob){
                for(int i=0; i<size; i++)
                    x[i] = Math.random();
                double nr = 0;
                for(int i=0; i<size; i++)
                    nr += x[i]*x[i];
                nr = Math.sqrt(nr);

                for(int i=0; i<size; i++)
                    x[i] = x[i]*diam*r/nr + mean[i];

                for(int i=0; i<size-1; i++)
                    pw.print(x[i]+",");
                pw.println(x[size-1]);

                cnto++;
            }
            cnt++;
            if(cnt == LIM)
                break;
        }
        System.out.println("Injected "+cnto+" outliers");
        //*/
        //  io.close();
        pw.flush();
        pw.close();


    }

    private IO io;
}
