package src.core.coreset;

import src.core.utils.*;
import java.util.*;

public class CoresetOutliers
{
    public CoresetOutliers(double _beta, double _eps, int _wSize, int _k, int _z, double _mD, double _MD, double _alpha, double _samp)
    {
        samp = _samp;
        beta = _beta;
		eps = _eps;
        delta = _eps/(1+beta);
        wSize = _wSize;
        alpha = _alpha;
        k = _k;
        z = _z;
        k_plus_z = k+z;
        minDist = _mD;
        maxDist = _MD;
        iters = (int)(Math.log(maxDist/minDist)/Math.log(1+beta) + 1);
        RV = new TreeMap[iters];
        OV = new TreeSet[iters];
        R = new TreeMap[iters];
        O = new TreeSet[iters];
        histoT = new HashMap[iters];
        histoC = new HashMap[iters];
        for(int i = 0; i < iters; i++){
            RV[i] = new TreeMap<>((aa, bb)-> aa.exitTime-bb.exitTime);
            OV[i] = new TreeSet<>((aa, bb)-> aa.exitTime-bb.exitTime);
            R[i] = new TreeMap<>((aa, bb)-> aa.exitTime-bb.exitTime);
            O[i] = new TreeSet<>((aa, bb)-> aa.exitTime-bb.exitTime);
            histoT[i] = new HashMap<>();
            histoC[i] = new HashMap<>();
        }
    }

    public ArrayList<WeightedPoint>[] query()
    {
        ArrayList<WeightedPoint> core = coreset();
        ArrayList<WeightedPoint>[] sol = null;
        double[] Gamma = new double[(int)(Math.log(maxDist/minDist)/Math.log(1+beta) + 1)];
        int i = 0;
        for(double gamma = minDist; i < Gamma.length; gamma*=(1+beta)){
            Gamma[i] = gamma;
            i++;
        }

        // binary search
        int first = 0;
		int last = iters;
		while (first < last) {
            i = first + ((last - first) >> 1);

            sol = Utils.charikarOutliersSampled(core, k, Gamma[i], eps, samp);
            ArrayList<WeightedPoint> outl = sol[1];
            int tot = 0;
            for(WeightedPoint pp : outl){
                tot += pp.w;
            }
            if(tot > z)
                first = i + 1;
            else
                last = i;
        }
        return Utils.charikarOutliersSampled(core, k, Gamma[first], eps, samp);
    }

    public ArrayList<WeightedPoint> coreset()
    {
        int i = 0;
        double[] Gamma = new double[iters];
        for(double gamma = minDist; i < iters; gamma*=(1+beta)){
            Gamma[i] = gamma;
            i++;
        }
        // binary search
        int first = 0;
		int last = iters;
		while (first < last) {
            i = first + ((last - first) >> 1);

            ArrayList<Point> C = new ArrayList<>();
            for(Point p : RV[i].keySet()){
                C.add(p);
            }
            for(Point p : OV[i])
            {
                if(C.size() > k_plus_z)
                    break;
                double mind = maxDist+1;
                for(Point q : C)
                    mind = Math.min(mind, p.distance(q));
                if(mind > 2*Gamma[i])
                    C.add(p);
            }
            for(Point p : RV[i].values())
            {
                if(C.size() > k_plus_z)
                    break;
                double mind = maxDist+1;
                for(Point q : C)
                    mind = Math.min(mind, p.distance(q));
                if(mind > 2*Gamma[i])
                    C.add(p);
            }

            if(C.size() > k_plus_z)
                first = i + 1;
            else
                last = i;

        }
        i = first;

        ArrayList<WeightedPoint> core = new ArrayList<>();
        for(Point p : R[i].values()){
            int j = 0;
            ArrayList<Integer> tmp = histoT[i].get(p);
            while(tmp.get(j) < step - wSize ) j++;
            int w = histoC[i].get(p).get(j);
            core.add(new WeightedPoint(p, w));
        }
        for(Point p : O[i]){
            int j = 0;
            ArrayList<Integer> tmp = histoT[i].get(p);
            while(tmp.get(j) < step - wSize ) j++;
            int w = histoC[i].get(p).get(j);
            core.add(new WeightedPoint(p, w));
        }
        return core;
    }

    public void update(Point p)
    {
        int i = 0;

        for(double gamma = minDist; gamma < maxDist; gamma*=(1+beta))
        {
            // REMOVE OLD POINTS
            ArrayList<Point> ptsToDel = new ArrayList<>();

            if(!RV[i].isEmpty() && RV[i].firstKey().exitTime <= step){
                OV[i].add(RV[i].get(RV[i].firstKey()));
                RV[i].remove(RV[i].firstKey());
            }
            if(!R[i].isEmpty() && R[i].firstKey().exitTime <= step){
                O[i].add(R[i].get(R[i].firstKey()));
                R[i].remove(R[i].firstKey());
            }
            while(!OV[i].isEmpty() && OV[i].first().exitTime <= step){
                OV[i].remove(OV[i].first());
            }
            while(!O[i].isEmpty() && O[i].first().exitTime <= step){
                histoT[i].remove(O[i].first());
                histoC[i].remove(O[i].first());
                O[i].remove(O[i].first());
            }

            // INSERT NEW POINT p
            ArrayList<Point> D = new ArrayList<>(); // within radius of an attraction pt
            ArrayList<Point> E = new ArrayList<>(); // within radius of a validation pt
            for(Point q : RV[i].keySet()){
                if( p.distance(q) < gamma*2 ){
                    E.add(q);
                }
            }
            if(E.isEmpty()){
                RV[i].put(p, p);
                if(RV[i].size() > k_plus_z+1){ // keep size <= k+1
                    Point vOld = RV[i].firstKey();
                    OV[i].add(RV[i].get(vOld));
                    RV[i].remove(vOld);

                }
                if(RV[i].size() > k_plus_z){ // surely can't find a k cluster, so delete
                    int tOld = RV[i].firstKey().exitTime;

                    while(!OV[i].isEmpty() && OV[i].first().exitTime <= tOld)
                        OV[i].remove(OV[i].first());

                    ptsToDel = new ArrayList<>();
                    for(Point q : R[i].keySet()){
                        if(q.exitTime <= tOld){
                            O[i].add(R[i].get(q));
                            ptsToDel.add(q);
                        }
                        else
                            break;
                    }
                    for(Point q : ptsToDel) R[i].remove(q);
                    ptsToDel = null;
                    while(!O[i].isEmpty() && O[i].first().exitTime <= tOld){
                        histoT[i].remove(O[i].first());
                        histoC[i].remove(O[i].first());
                        O[i].remove(O[i].first());
                    }
                 }
            }
            else {
                RV[i].put(E.get(0), p);
            }
            for(Point q : R[i].keySet()){
                if( p.distance(q) < delta*gamma/2 ){
                    D.add(q);
                }
            }
            if(D.isEmpty()){
                // update histograms
                ArrayList<Integer> tp = new ArrayList<>(1);
                ArrayList<Integer> cp = new ArrayList<>(1);
                tp.add(step);
                cp.add(1);
                histoT[i].put(p, tp);
                histoC[i].put(p, cp);

                R[i].put(p, p);
            } else {
                Point attr = D.get(0);;
                double min_d = Double.POSITIVE_INFINITY;
                for(Point pd : D){
                    double tmpd = p.distance(pd);
                    if(tmpd < min_d){
                        attr = pd;
                        min_d = tmpd;
                    }
                }
                //attr = D.get(0);
                Point oldRep = R[i].get(attr);

                // move histograms and cleanup
                ArrayList<Integer> tp = histoT[i].get(oldRep);
                ArrayList<Integer> cp = histoC[i].get(oldRep);
                //System.out.println("Old: "+oldRep+"  "+cp.toString());
                for(int j=0; j<cp.size(); j++)
                    cp.set(j, cp.get(j) + 1);  // add 1 to all counts
                tp.add(step);
                cp.add(1);
                ArrayList<Integer> new_tp = new ArrayList<>();
                ArrayList<Integer> new_cp = new ArrayList<>();
                int lastj = 0;
                while(tp.get(lastj) < step-wSize+1) lastj++; // discard info older than start of window
                new_tp.add(tp.get(lastj));
                new_cp.add(cp.get(lastj));
                for(int j=lastj+1; j<cp.size()-1; j++){
                    if( cp.get(lastj) <= (1.0+alpha)*cp.get(j+1) )
                        continue;
                    new_tp.add(tp.get(j));
                    new_cp.add(cp.get(j));
                    lastj = j;
                }
                new_tp.add(tp.get(cp.size()-1));
                new_cp.add(cp.get(cp.size()-1));
                histoT[i].remove(oldRep);
                histoC[i].remove(oldRep);
                histoT[i].put(p, new_tp);
                histoC[i].put(p, new_cp);


                R[i].put(attr, p); // only one attractor!
            }

            // ENDED INSERTING


            i++; // next gamma
        }
        step++; //next step
    }


    public TreeSet<Point>[] O, OV;
    public TreeMap<Point, Point>[] R, RV;
    public HashMap<Point, ArrayList<Integer>>[] histoT;
    public HashMap<Point, ArrayList<Integer>>[] histoC;
    int step = 0, wSize, k, z, k_plus_z;
    public double beta, delta, alpha, eps, samp;
    public double minDist, maxDist;
    public int iters;
}
