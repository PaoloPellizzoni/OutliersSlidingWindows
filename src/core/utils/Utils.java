package src.core.utils;

import java.util.*;

public class Utils
{
    static final int INF = 1000000000;

    public static ArrayList<Point> gonKCenter(ArrayList<Point> points, int k){
        int n = points.size();
        ArrayList<Point> sol = new ArrayList<>();
        double[] distances = new double[n];
        Arrays.fill(distances, INF+1);
        int maxi = 0;
        for(int i=0; i < k; i++){
            sol.add(points.get(maxi));
            double max = 0;
            for(int j=0; j < n; j++){
                distances[j] = Math.min(distances[j], sol.get(i).distance(points.get(j)));
                if(distances[j] > max){
                    max = distances[j];
                    maxi = j;
                }
            }
        }
        return sol;
    }

    public static ArrayList<WeightedPoint> gonKCenterW(ArrayList<WeightedPoint> points, int k){
        int n = points.size();
        ArrayList<WeightedPoint> sol = new ArrayList<>();
        double[] distances = new double[n];
        Arrays.fill(distances, INF);
        int maxi = 0;
        for(int i=0; i < k; i++){
            sol.add(points.get(maxi));
            double max = 0;
            for(int j=0; j < n; j++){
                distances[j] = Math.min(distances[j], sol.get(i).p.distance(points.get(j).p));
                if(distances[j] > max){
                    max = distances[j];
                    maxi = j;
                }
            }
        }
        return sol;
    }

    public static ArrayList<WeightedPoint>[] charikarOutliers(ArrayList<WeightedPoint> points, int k, double r, double eps){
        int n = points.size();
        int cnt = n;
        int[] Tf = new int[n];
        ArrayList<WeightedPoint> X = new ArrayList<WeightedPoint>();
        double thr_1 = (1 + 2*eps)*r;
        double thr_2 = (3 + 4*eps)*r;
        while(X.size() < k && cnt > 0){
            int besti = 0;
            int best = 0;
            for(int i=0; i<n; i++){
                int tmp = 0;
                for(int j=0; j<n; j++){
                    if(Tf[j]==0 && points.get(i).p.distance(points.get(j).p) <= thr_1)
                        tmp += points.get(i).w;
                        //tmp++;
                }
                if(tmp > best){
                    best = tmp;
                    besti = i;
                }
            }
            X.add(points.get(besti));
            for(int j=0; j<n; j++){
                if(Tf[j]==0 && points.get(besti).p.distance(points.get(j).p) <= thr_2){
                    Tf[j] = 1;
                    cnt--;
                }
            }
        }
        ArrayList<WeightedPoint>[] sol = new ArrayList[2];
        ArrayList<WeightedPoint> Tff = new ArrayList<>();
        for(int i=0; i<n; i++){
            if(Tf[i]==0) Tff.add(points.get(i));
        }
        sol[0] = X;
        sol[1] = Tff;
        return sol;
    }

    public static ArrayList<WeightedPoint>[] charikarOutliersSampled(ArrayList<WeightedPoint> points, int k, double r, double eps, double prob){
        int n = points.size();
        int cnt = n;
        int[] Tf = new int[n];
        ArrayList<WeightedPoint> X = new ArrayList<WeightedPoint>();
        double thr_1 = (1 + 2*eps)*r;
        double thr_2 = (3 + 4*eps)*r;
        while(X.size() < k && cnt > 0){
            int besti = 0;
            int best = 0;
            for(int i=0; i<n; i++){
                if(Math.random() > prob) continue;
                int tmp = 0;
                for(int j=0; j<n; j++){
                    if(Tf[j]==0 && points.get(i).p.distance(points.get(j).p) <= thr_1)
                        tmp += points.get(i).w;
                        //tmp++;
                }
                if(tmp > best){
                    best = tmp;
                    besti = i;
                }
            }
            X.add(points.get(besti));
            for(int j=0; j<n; j++){
                if(Tf[j]==0 && points.get(besti).p.distance(points.get(j).p) <= thr_2){
                    Tf[j] = 1;
                    cnt--;
                }
            }
        }
        ArrayList<WeightedPoint>[] sol = new ArrayList[2];
        ArrayList<WeightedPoint> Tff = new ArrayList<>();
        for(int i=0; i<n; i++){
            if(Tf[i]==0) Tff.add(points.get(i));
        }
        sol[0] = X;
        sol[1] = Tff;
        return sol;
    }


    public static ArrayList<Point> completeSearchDiameter(List<Point> l){
        double best = 0;
        ArrayList<Point> ans = new ArrayList<>(2);
        ans.add(null); ans.add(null);
        for(Point p1 : l){
            for(Point p2 : l){
                double d = p1.distance(p2);
                if(d > best){
                    best = d;
                    ans.set(0, p1);
                    ans.set(1, p2);
                }
            }
        }
        return ans;
    }

    public static ArrayList<Point> approxDiameter(List<Point> l){
        double best = 0;
        ArrayList<Point> ans = new ArrayList<>(2);
        ans.add(null); ans.add(null);
        Point p1 = l.get(0);
        ans.set(0, p1);
        for(Point p2 : l){
            double d = p1.distance(p2);
            if(d > best){
                best = d;
                ans.set(1, p2);
            }
        }

        p1 = ans.get(1);
        for(Point p2 : l){
            double d = p1.distance(p2);
            if(d > best){
                best = d;
                ans.set(0, p2);
            }
        }
        return ans;
    }

    public static double completeSearchEffectiveDiameter(List<Point> l, double alpha){
        ArrayList<Double> dists = new ArrayList<>(0);
        for(Point p1 : l){
            for(Point p2 : l){
                dists.add(-p1.distance(p2));
            }
        }
        PriorityQueue<Double> q = new PriorityQueue<>(dists);
        int z = (int)(l.size()*l.size()*(1-alpha));
        for(int i=0; i<z; i++) q.poll();
        return -q.peek();
    }

    public static double approxEffectiveDiameter(List<WeightedPoint> l, double alpha, double eps, double _mD, double _MD){
        long[] cnt = new long[(int)(Math.log(_MD/_mD)/Math.log(1+eps))];
        for(WeightedPoint p1 : l){
            for(WeightedPoint p2 : l){
                double d = Math.log(p1.p.distance(p2.p)/_mD)/Math.log(1+eps);
                cnt[Math.max(0, (int)d)]++;
            }
        }
        /*
        long tmp = 0;
        for(int i=0; i<cnt.length; i++) System.out.println(i+": "+(tmp += cnt[i])+"  ");
        System.out.println();
        */
        long z = (long)((long)l.size()*(long)l.size()*(1-alpha));
        int i = cnt.length-1;
        while(z >= 0) z -= cnt[i--];
        //System.out.println("Sol: "+(i+1));
        return _mD*Math.pow(1+eps, i+1);
    }

    public static double coresetEffectiveDiameter(List<WeightedPoint> l, double alpha, double eps, double _mD, double _MD){
        long[] cnt = new long[(int)(Math.log(_MD/_mD)/Math.log(1+eps))];
        long sz = 0;
        for(WeightedPoint p1 : l) sz += p1.w;
        for(WeightedPoint p1 : l){
            for(WeightedPoint p2 : l){
                double d = Math.log(p1.p.distance(p2.p)/_mD)/Math.log(1+eps);
                cnt[Math.max(0, (int)d)] += p1.w*p2.w;
            }
        }
        /*
        long tmp = 0;
        for(int i=0; i<cnt.length; i++) System.out.println(i+": "+(tmp += cnt[i])+"  ");
        System.out.println();
        */
        long z = (long)(sz*sz*(1-alpha));
        int i = cnt.length-1;
        while(z >= 0) z -= cnt[i--];
        //System.out.println("Sol: "+(i+1));
        return _mD*Math.pow(1+eps, i+1);
    }


    public static double distanceBetweenSets(ArrayList<Point> set, ArrayList<Point> centers){
        double ans = 0;
        for(Point p : set){
            double dd = INF+1;
            for(Point q : centers){
                dd = Math.min(dd, p.distance(q));
            }
            ans = Math.max(dd, ans);
        }
        return ans;
    }

    public static double distanceBetweenSetsWeighted(ArrayList<Point> set, ArrayList<WeightedPoint> centers){
        double ans = 0;
        for(Point p : set){
            double dd = INF+1;
            for(WeightedPoint q : centers){
                dd = Math.min(dd, p.distance(q.p));
            }
            ans = Math.max(dd, ans);
        }
        return ans;
    }

    public static double distanceBetweenSetsOutliers(ArrayList<Point> set, ArrayList<WeightedPoint> centers, int z){
        double[] d = new double[set.size()];
        for(int i=0; i<set.size(); i++){
            Point p = set.get(i);
            double dd = INF+1;
            for(WeightedPoint q : centers){
                dd = Math.min(dd, p.distance(q.p));
            }
            d[i] = dd;
        }
        Arrays.sort(d);
        return d[set.size() - 1 -z];
    }

    public static double distanceBetweenSetsOutliersWeighted(ArrayList<WeightedPoint> set, ArrayList<WeightedPoint> centers, int z){
        double[] d = new double[set.size()];
        for(int i=0; i<set.size(); i++){
            WeightedPoint p = set.get(i);
            double dd = INF+1;
            for(WeightedPoint q : centers){
                dd = Math.min(dd, p.p.distance(q.p));
            }
            d[i] = dd;
        }
        Arrays.sort(d);
        return d[set.size() - 1 -z];
    }
}
