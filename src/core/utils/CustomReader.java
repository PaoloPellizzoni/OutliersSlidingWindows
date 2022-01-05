package src.core.utils;

public class CustomReader implements DatasetReader
{
    public CustomReader(String file, int siz){
        io = new IO(file);
        size = siz;
    }

    public void close(){
        io.close();
    }

    public Point nextPoint(){
        double[] x = new double[size];
        for(int i=0; i<size; i++)
            x[i] = io.getDouble();
        return new Point(x);
    }

    private IO io;
    int size;
}
