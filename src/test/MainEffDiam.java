package src.test;

import src.core.coreset.*;
import src.core.utils.*;

import java.util.*;
import java.io.*;

public class MainEffDiam
{
    static double INF = 10e20;
    static int stride = 100000;
    public static void main(String[] args) throws Exception
    {
        String in = args[0];
        String out = args[1];
        int DIM = Integer.parseInt(args[2]);
        double alpha = Double.parseDouble(args[3]), eta = Double.parseDouble(args[4]);
        int wSize = Integer.parseInt(args[5]);
        double beta = Double.parseDouble(args[6]), eps = Double.parseDouble(args[7]), lambda = Double.parseDouble(args[8]);
        double mD = Double.parseDouble(args[9]), MD = Double.parseDouble(args[10]);
        int doOurs = 1, doSeq = Integer.parseInt(args[11]);
        PrintWriter writer = new PrintWriter("out/diam/"+out);
        DatasetReader reader = new CustomReader("data/"+in, DIM);

        writer.println(Arrays.toString(args));
        long startTime, endTime;
        CoresetEffectiveDiameter coh = new CoresetEffectiveDiameter(beta, eps, alpha, eta, mD, MD, lambda, wSize);
        ArrayList<WeightedPoint> deb_win = new ArrayList<>();

        for(int tim = 0; tim < wSize+stride ; tim++){
            Point p = reader.nextPoint();
            if(tim%1000==0) System.out.println(tim);
            p.exitTime = tim + wSize;
            //debug window
            deb_win.add(new WeightedPoint(p, 1));
            if(deb_win.size() > wSize)
                deb_win.remove(0); //slow but it's for debug purposes only

            if(tim < wSize){ //only update
                coh.update(p);
                continue;
            }

            // ours
            if(doOurs == 1){
                startTime = System.nanoTime();
                coh.update(p);
                endTime = System.nanoTime();

                if(tim % 10000 != 0) continue;
                writer.print( (endTime - startTime) + " " );  // update

                startTime = System.nanoTime();
                double sol = coh.query();
                endTime = System.nanoTime();
                writer.print( (endTime - startTime) + " " );  // query


                int count = 0;
                int maxc = 0;
                for(HashMap<Point, ArrayList<Integer>> x : coh.cor.histoT)
                    for(ArrayList<Integer> xx : x.values()){ count += xx.size(); maxc = Math.max(maxc, xx.size());  }
                for(HashMap<Point, ArrayList<Integer>> x : coh.cor.histoC)
                    for(ArrayList<Integer> xx : x.values()) count += xx.size();
                for(TreeSet<Point> x : coh.cor.O) count += x.size()*DIM;
                for(TreeSet<Point> x : coh.cor.OV) count += x.size()*DIM;
                for(TreeMap<Point, Point> x : coh.cor.R) count += x.size()*2*DIM;
                for(TreeMap<Point, Point> x : coh.cor.RV) count += x.size()*2*DIM;
                writer.print( count + " ");  // memory tot

                writer.print(Utils.distanceBetweenSetsOutliersWeighted(deb_win, coh.cor.coreset(), 0) +" "); //radius
                writer.print(sol+"   "); //radius
            }

            // sequential
            if(doSeq == 1){
                writer.print( 0 + " " ); // query
                startTime = System.nanoTime();
                double sol = Utils.approxEffectiveDiameter(deb_win, alpha, 0.01, mD, MD);
                endTime = System.nanoTime();
                writer.print( (endTime - startTime) + " " );  // query
                writer.print( deb_win.size()*DIM+ " " ); // mem tot
                //writer.print( deb_win.size()+ " " ); // pts in mem
                //writer.print( deb_win.size()+ " " ); // coreset
                writer.print(sol+"   "); // radius
            }
            else{
                writer.print("0 0 0 0   ");
            }

            writer.println();
            writer.flush();

        }


        writer.close();

        reader.close();

        //*/
    }

}
