package src.test;

import src.core.coreset.*;
import src.core.utils.*;

import java.util.*;
import java.io.*;

public class MainLambda
{
    static int stride = 100000;
    public static void main(String[] args) throws Exception
    {
        String in = args[0];
        String out = args[1];
        int DIM = Integer.parseInt(args[2]), k = Integer.parseInt(args[3]), z = Integer.parseInt(args[4]);
        int wSize = Integer.parseInt(args[5]);
        double beta = Double.parseDouble(args[6]), eps = Double.parseDouble(args[7]), lambda = Double.parseDouble(args[8]);
        double mD = Double.parseDouble(args[9]), MD = Double.parseDouble(args[10]);
        int doSlow = Integer.parseInt(args[11]);
        PrintWriter writer = new PrintWriter("out/lam/"+out);
        DatasetReader reader = new CustomReader("data/"+in, DIM);

        writer.println(Arrays.toString(args));
        long startTime, endTime;
        CoresetOutliers cor1 = new CoresetOutliers(beta, eps, wSize, k, z, mD, MD, 0, 1);
        CoresetOutliers cor2 = new CoresetOutliers(beta, eps, wSize, k, z, mD, MD, 0.1, 1);
        CoresetOutliers cor3 = new CoresetOutliers(beta, eps, wSize, k, z, mD, MD, 0.5, 1);
        CoresetOutliers cor4 = new CoresetOutliers(beta, eps, wSize, k, z, mD, MD, 1, 1);
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
                if(doSlow==1) cor1.update(p);
                cor2.update(p);
                cor3.update(p);
                cor4.update(p);
                continue;
            }

            // ours
            if(doSlow == 1){
                startTime = System.nanoTime();
                cor1.update(p);
                endTime = System.nanoTime();

                if(tim % 10000 != 0){
                    cor2.update(p);
                    cor3.update(p);
                    cor4.update(p);
                    continue;
                }
                writer.print( (endTime - startTime) + " " );  // update

                startTime = System.nanoTime();
                ArrayList<WeightedPoint>[] sol = cor1.query();
                ArrayList<WeightedPoint> outl = sol[1];
                endTime = System.nanoTime();
                writer.print( (endTime - startTime) + " " );  // query

                int count = 0;
                int maxc = 0;
                for(HashMap<Point, ArrayList<Integer>> x : cor1.histoT)
                    for(ArrayList<Integer> xx : x.values()){ count += xx.size(); maxc = Math.max(maxc, xx.size());  }
                for(HashMap<Point, ArrayList<Integer>> x : cor1.histoC)
                    for(ArrayList<Integer> xx : x.values()) count += xx.size();
                writer.print( count + " ");  // memory tot
                for(TreeSet<Point> x : cor1.O) count += x.size()*DIM;
                for(TreeSet<Point> x : cor1.OV) count += x.size()*DIM;
                for(TreeMap<Point, Point> x : cor1.R) count += x.size()*2*DIM;
                for(TreeMap<Point, Point> x : cor1.RV) count += x.size()*2*DIM;
                writer.print( count + " ");  // memory tot

                count = 0;
                for(TreeSet<Point> x : cor1.O) count += x.size();
                for(TreeSet<Point> x : cor1.OV) count += x.size();
                for(TreeMap<Point, Point> x : cor1.R) count += x.size()*2;
                for(TreeMap<Point, Point> x : cor1.RV) count += x.size()*2;
                //writer.print( count + " " );  // pts in memory

                ArrayList<WeightedPoint> cors = cor1.coreset();
                //writer.print( cors.size() + " " );  // coreset
                //writer.print(Utils.distanceBetweenSetsOutliersWeighted(deb_win, cors, 0) +" "); // max dist
                writer.print(Utils.distanceBetweenSetsOutliersWeighted(deb_win, sol[0], z)+"   ");  //radius
            }
            else{
                if(tim%10000 == 0)
                    writer.print("0 0 0 0 0   ");
            }


            if(1 == 1){
                startTime = System.nanoTime();
                cor2.update(p);
                endTime = System.nanoTime();

                if(tim % 10000 != 0) continue;
                writer.print( (endTime - startTime) + " " );  // update

                startTime = System.nanoTime();
                ArrayList<WeightedPoint>[] sol = cor2.query();
                ArrayList<WeightedPoint> outl = sol[1];
                endTime = System.nanoTime();
                writer.print( (endTime - startTime) + " " );  // query

                int count = 0;
                int maxc = 0;
                for(HashMap<Point, ArrayList<Integer>> x : cor2.histoT)
                    for(ArrayList<Integer> xx : x.values()){ count += xx.size(); maxc = Math.max(maxc, xx.size());  }
                for(HashMap<Point, ArrayList<Integer>> x : cor2.histoC)
                    for(ArrayList<Integer> xx : x.values()) count += xx.size();
                writer.print( count + " ");  // memory tot
                for(TreeSet<Point> x : cor2.O) count += x.size()*DIM;
                for(TreeSet<Point> x : cor2.OV) count += x.size()*DIM;
                for(TreeMap<Point, Point> x : cor2.R) count += x.size()*2*DIM;
                for(TreeMap<Point, Point> x : cor2.RV) count += x.size()*2*DIM;
                writer.print( count + " ");  // memory tot

                count = 0;
                for(TreeSet<Point> x : cor2.O) count += x.size();
                for(TreeSet<Point> x : cor2.OV) count += x.size();
                for(TreeMap<Point, Point> x : cor2.R) count += x.size()*2;
                for(TreeMap<Point, Point> x : cor2.RV) count += x.size()*2;
                //writer.print( count + " " );  // pts in memory

                ArrayList<WeightedPoint> cors = cor2.coreset();
                //writer.print( cors.size() + " " );  // coreset
                //writer.print(Utils.distanceBetweenSetsOutliersWeighted(deb_win, cors, 0) +" "); // max dist
                writer.print(Utils.distanceBetweenSetsOutliersWeighted(deb_win, sol[0], z)+"   ");  //radius
            }
            if(1 == 1){
                startTime = System.nanoTime();
                cor3.update(p);
                endTime = System.nanoTime();

                if(tim % 10000 != 0) continue;
                writer.print( (endTime - startTime) + " " );  // update

                startTime = System.nanoTime();
                ArrayList<WeightedPoint>[] sol = cor3.query();
                ArrayList<WeightedPoint> outl = sol[1];
                endTime = System.nanoTime();
                writer.print( (endTime - startTime) + " " );  // query

                int count = 0;
                int maxc = 0;
                for(HashMap<Point, ArrayList<Integer>> x : cor3.histoT)
                    for(ArrayList<Integer> xx : x.values()){ count += xx.size(); maxc = Math.max(maxc, xx.size());  }
                for(HashMap<Point, ArrayList<Integer>> x : cor3.histoC)
                    for(ArrayList<Integer> xx : x.values()) count += xx.size();
                writer.print( count + " ");  // memory tot
                for(TreeSet<Point> x : cor3.O) count += x.size()*DIM;
                for(TreeSet<Point> x : cor3.OV) count += x.size()*DIM;
                for(TreeMap<Point, Point> x : cor3.R) count += x.size()*2*DIM;
                for(TreeMap<Point, Point> x : cor3.RV) count += x.size()*2*DIM;
                writer.print( count + " ");  // memory tot

                count = 0;
                for(TreeSet<Point> x : cor3.O) count += x.size();
                for(TreeSet<Point> x : cor3.OV) count += x.size();
                for(TreeMap<Point, Point> x : cor3.R) count += x.size()*2;
                for(TreeMap<Point, Point> x : cor3.RV) count += x.size()*2;
                //writer.print( count + " " );  // pts in memory

                ArrayList<WeightedPoint> cors = cor3.coreset();
                //writer.print( cors.size() + " " );  // coreset
                //writer.print(Utils.distanceBetweenSetsOutliersWeighted(deb_win, cors, 0) +" "); // max dist
                writer.print(Utils.distanceBetweenSetsOutliersWeighted(deb_win, sol[0], z)+"   ");  //radius
            }
            if(1 == 1){
                startTime = System.nanoTime();
                cor4.update(p);
                endTime = System.nanoTime();

                if(tim % 10000 != 0) continue;
                writer.print( (endTime - startTime) + " " );  // update

                startTime = System.nanoTime();
                ArrayList<WeightedPoint>[] sol = cor4.query();
                ArrayList<WeightedPoint> outl = sol[1];
                endTime = System.nanoTime();
                writer.print( (endTime - startTime) + " " );  // query

                int count = 0;
                int maxc = 0;
                for(HashMap<Point, ArrayList<Integer>> x : cor4.histoT)
                    for(ArrayList<Integer> xx : x.values()){ count += xx.size(); maxc = Math.max(maxc, xx.size());  }
                for(HashMap<Point, ArrayList<Integer>> x : cor4.histoC)
                    for(ArrayList<Integer> xx : x.values()) count += xx.size();
                writer.print( count + " ");  // memory tot
                for(TreeSet<Point> x : cor4.O) count += x.size()*DIM;
                for(TreeSet<Point> x : cor4.OV) count += x.size()*DIM;
                for(TreeMap<Point, Point> x : cor4.R) count += x.size()*2*DIM;
                for(TreeMap<Point, Point> x : cor4.RV) count += x.size()*2*DIM;
                writer.print( count + " ");  // memory tot

                count = 0;
                for(TreeSet<Point> x : cor4.O) count += x.size();
                for(TreeSet<Point> x : cor4.OV) count += x.size();
                for(TreeMap<Point, Point> x : cor4.R) count += x.size()*2;
                for(TreeMap<Point, Point> x : cor4.RV) count += x.size()*2;
                //writer.print( count + " " );  // pts in memory

                ArrayList<WeightedPoint> cors = cor4.coreset();
                //writer.print( cors.size() + " " );  // coreset
                //writer.print(Utils.distanceBetweenSetsOutliersWeighted(deb_win, cors, 0) +" "); // max dist
                writer.print(Utils.distanceBetweenSetsOutliersWeighted(deb_win, sol[0], z)+"   ");  //radius
            }


            writer.println();
            writer.flush();

        }



        writer.close();

        reader.close();

        //*/
    }

}
