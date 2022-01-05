package src.core.utils;

public class WeightedPoint
{
    public WeightedPoint(Point _p, int _w)
    {
        p = _p;
        w = _w;
    }

    public String toString()
    {
        return p.toString();
    }

    public Point p;
    public int w;
}
