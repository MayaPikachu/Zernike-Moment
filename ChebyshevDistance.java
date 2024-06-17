package plugins;

public class ChebyshevDistance implements DistanceCalculator {
    @Override
    public double getDistance(double[] p1, double[] p2) {
        double distance = 0.0;
        for (int i=0;i<Math.min(p1.length,p2.length);i++) {
            distance = Math.max(distance, Math.abs(p1[i]-p2[i]));
        }
        return distance;
    }
}
