

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class KNN {
    int k;
    DistanceCalculator distanceCalculator;

    public KNN(int k, DistanceCalculator distanceCalculator) {
        this.k = k;
        this.distanceCalculator = distanceCalculator;
    }

    private static class ImageWithDistance {
        private final ImageData img;
        private final double distance;
        public ImageWithDistance(ImageData img, double distance) {
            this.img = img;
            this.distance = distance;
        }

        public ImageData getImg() {
            return img;
        }

        public double getDistance() {
            return distance;
        }
    }

    public List<ImageData> getKNN(List<ImageData> images, String imageName) {
        ImageData reference = images.stream().filter(img -> img.getImageName().equals(imageName)).collect(Collectors.toList()).get(0);
        return images.stream().filter(img -> !img.getImageName().equals(reference.getImageName())).map(img -> new ImageWithDistance(img, distanceCalculator.getDistance(img.getFeatures(), reference.getFeatures()))).sorted(Comparator.comparingDouble(ImageWithDistance::getDistance)).limit(k).map(ImageWithDistance::getImg).collect(Collectors.toList());
    }
}
