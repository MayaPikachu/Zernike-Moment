

public class ImageData {
    private double[] features;
    private String imageName;

    public ImageData(double[] features, String imageName) {
        this.features = features;
        this.imageName = imageName;
    }

    public double[] getFeatures() {
        return features;
    }

    public String getImageName() {
        return imageName;
    }
}
