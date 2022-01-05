package src.test;

import src.core.coreset.*;
import src.core.utils.*;

import java.util.*;
import java.io.*;

public class Main
{
    static int stride = 100000;
    public static void main(String[] args) throws Exception
    {
        String in = args[0];
        String out = args[1];
        int DIM = Integer.parseInt(args[2]), k = Integer.parseInt(args[3]), z = Integer.parseInt(args[4]);
        int wSize = Integer.parseInt(args[5]);
        double beta = Double.parseDouble(args[6]), eps = Double.parseDouble(args[7]), lambda = Double.parseDouble(args[8]);
        double mD = Double.parseDouble(args[9]), MD = Double.parseDouble(args[10]), samp = Double.parseDouble(args[11]);
        int doOurs = 1, doChar = Integer.parseInt(args[12]), doSamp = 1, doGonz = 1;
        PrintWriter writer = new PrintWriter("out/k-cen/"+out);
        DatasetReader reader = new CustomReader("data/"+in, DIM);

        writer.println(Arrays.toString(args));
        long startTime, endTime;
        CoresetOutliers coh = new CoresetOutliers(beta, eps, wSize, k, z, mD, MD, lambda, 1);
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
                ArrayList<WeightedPoint>[] sol = coh.query();
                ArrayList<WeightedPoint> outl = sol[1];
                endTime = System.nanoTime();
                writer.print( (endTime - startTime) + " " );  // query

                int count = 0;
                int maxc = 0;
                for(HashMap<Point, ArrayList<Integer>> x : coh.histoT)
                    for(ArrayList<Integer> xx : x.values()){ count += xx.size(); maxc = Math.max(maxc, xx.size());  }
                for(HashMap<Point, ArrayList<Integer>> x : coh.histoC)
                    for(ArrayList<Integer> xx : x.values()) count += xx.size();
                for(TreeSet<Point> x : coh.O) count += x.size()*DIM;
                for(TreeSet<Point> x : coh.OV) count += x.size()*DIM;
                for(TreeMap<Point, Point> x : coh.R) count += x.size()*2*DIM;
                for(TreeMap<Point, Point> x : coh.RV) count += x.size()*2*DIM;
                writer.print( count + " ");  // memory tot

                count = 0;
                for(TreeSet<Point> x : coh.O) count += x.size();
                for(TreeSet<Point> x : coh.OV) count += x.size();
                for(TreeMap<Point, Point> x : coh.R) count += x.size()*2;
                for(TreeMap<Point, Point> x : coh.RV) count += x.size()*2;
                //writer.print( count + " " );  // pts in memory

                ArrayList<WeightedPoint> cors = coh.coreset();
                //writer.print( cors.size() + " " );  // coreset
                //writer.print(Utils.distanceBetweenSetsOutliersWeighted(deb_win, cors, 0) +" "); // max dist
                writer.print(Utils.distanceBetweenSetsOutliersWeighted(deb_win, sol[0], z)+"   ");  //radius
            }

            // charikar
            if(doChar == 1){
                writer.print( 0 + " " ); // query
                startTime = System.nanoTime();
                ArrayList<WeightedPoint>[] sol;
                ArrayList<WeightedPoint> outl;
                double[] Gamma = new double[(int)(Math.log(MD/mD)/Math.log(1+beta) + 1)];
                int i = 0;
                for(double gamma = mD; i < Gamma.length; gamma*=(1+beta)){
                    Gamma[i] = gamma;
                    i++;
                }
                // binary search
                int first = 0;
        		int last = Gamma.length;
        		while (first < last) {
                    i = first + ((last - first) >> 1);

                    sol = Utils.charikarOutliersSampled(deb_win, k, Gamma[i], 0, 1);
                    outl = sol[1];
                    int tot = 0;
                    for(WeightedPoint pp : outl){
                        tot += pp.w;
                    }
                    if(tot > z)
                        first = i + 1;
                    else
                        last = i;
                }
                sol = Utils.charikarOutliersSampled(deb_win, k, Gamma[first], 0, 1);
                endTime = System.nanoTime();
                writer.print( (endTime - startTime) + " " );  // query
                writer.print( deb_win.size()*DIM+ " " ); // mem tot
                //writer.print( deb_win.size()+ " " ); // pts in mem
                //writer.print( deb_win.size()+ " " ); // coreset
                writer.print(Utils.distanceBetweenSetsOutliersWeighted(deb_win, sol[0], z)+"   "); // radius
            }
            else{
                writer.print("0 0 0 0   ");
            }

            // sampled charikar
            if(doSamp == 1){
                writer.print( 0 + " " ); // query
                startTime = System.nanoTime();
                ArrayList<WeightedPoint>[] sol;
                ArrayList<WeightedPoint> outl;
                double[] Gamma = new double[(int)(Math.log(MD/mD)/Math.log(1+beta) + 1)];
                int i = 0;
                for(double gamma = mD; i < Gamma.length; gamma*=(1+beta)){
                    Gamma[i] = gamma;
                    i++;
                }
                // binary search
                int first = 0;
        		int last = Gamma.length;
        		while (first < last) {
                    i = first + ((last - first) >> 1);

                    sol = Utils.charikarOutliersSampled(deb_win, k, Gamma[i], 0, samp/wSize);
                    outl = sol[1];
                    int tot = 0;
                    for(WeightedPoint pp : outl){
                        tot += pp.w;
                    }
                    if(tot > z)
                        first = i + 1;
                    else
                        last = i;
                }
                sol = Utils.charikarOutliersSampled(deb_win, k, Gamma[first], 0, samp/wSize);
                endTime = System.nanoTime();
                writer.print( (endTime - startTime) + " " );  // query
                writer.print( deb_win.size()*DIM+ " " ); // mem tot
                //writer.print( deb_win.size()+ " " ); // pts in mem
                //writer.print( deb_win.size()+ " " ); // coreset
                writer.print(Utils.distanceBetweenSetsOutliersWeighted(deb_win, sol[0], z)+"   "); // radius
            }
            else{
                writer.print("0 0 0 0   ");
            }


            // gonzalez
            if(doGonz == 1){
                writer.print( 0 + " " ); // query
                startTime = System.nanoTime();
                ArrayList<WeightedPoint> sol = Utils.gonKCenterW(deb_win, k);
                endTime = System.nanoTime();
                writer.print( (endTime - startTime) + " " );  // query
                writer.print( deb_win.size()*DIM+ " " ); // mem tot
                writer.print(Utils.distanceBetweenSetsOutliersWeighted(deb_win, sol, z)+"   "); // radius
            }

            writer.println();
            writer.flush();

        }



        writer.close();
        //writerSeq.close();

        reader.close();

        //*/
    }

}
