package src.core.coreset;

import src.core.utils.*;
import java.util.*;

public class CoresetEffectiveDiameter
{
    public CoresetEffectiveDiameter(double _beta, double _eps, double _alpha, double _eta, double _mD, double _MD, double _lambda, int _wSize){
        wSize = _wSize;
        alpha = _alpha;
        minDist = _mD;
        maxDist = _MD;
        cor = new CoresetOutliers(_beta, _eps*_eta/2, _wSize, 1, 0, _mD, _MD, _lambda, 1);
    }

    public void update(Point p){
        cor.update(p);
    }

    public double query(){
        ArrayList<WeightedPoint> coreset = cor.coreset();
        return Utils.coresetEffectiveDiameter(coreset, alpha, 0.01, minDist, maxDist);
    }

    public int wSize;
    public double minDist, maxDist, alpha;
    public CoresetOutliers cor;
}
