package plugins;

public class ManhattanDistance implements DistanceCalculator {
    @Override
    public double getDistance(double[] p1, double[] p2) {
        double distance = 0.0;
        for (int i=0;i<Math.min(p1.length,p2.length);i++) {
            distance += Math.abs(p1[i]-p2[i]);
        }
        return distance;
    }
}
