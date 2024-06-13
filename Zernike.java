import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class Zernike {

    public Zernike() {
    }

	// Function to calculate the radial component of the Zernike polynomial
	private static double rnl(int n, int l, double rho) {
		double sum = 0.0;
			for (int s = 0; s <= (n - Math.abs(l)) / 2; s++) {
				sum += Math.pow(-1, s) * fatorial(n - s)
						/ (fatorial(s) * fatorial((n + Math.abs(l)) / 2 - s)
						* fatorial((n - Math.abs(l)) / 2 - s))
						* Math.pow(rho, n - 2 * s);
			}
		return sum;
	}

	// Function to calculate the factorial of a number
	private static double fatorial(int n) {
		double result = 1;
		for (int i = 2; i <= n; i++) {
			result *= i;
		}
		return result;
	}

	// Function to calculate the angular component of the Zernike polynomial
	private static double cosZernike(int l, double theta) {
		return Math.cos(l * theta);
	}

	private static double sinZernike(int l, double theta) {
		return Math.sin(l * theta);
	}

	private static double vnl(double theta, int l, int n, double radius){
        return Math.sqrt((Math.pow(rnl(n, l, radius)*cosZernike(l, theta), 2)) + (Math.pow(rnl(n, l, radius)*sinZernike(l, theta), 2)));
	}

	// Function to convert Cartesian coordinates to polar coordinates
	private static double[] cartesianToPolar(int x, int y, int centerX, int centerY) {
		double rho = Math.sqrt(Math.pow(x - centerX, 2) + Math.pow(y - centerY, 2));
		double theta = Math.atan2(y - centerY, x - centerX);
		return new double[]{rho, theta};
	}

	// Function to calculate the Zernike polynomial value at a given pixel
	private static double znl(int n, int width, int height, int centerX, int centerY, BufferedImage image) {
		double sum = 0.0;
		int l;
			for (int y = 0; y < height; y++) {
				for (int x = 0; x < width; x++) {
                    double[] polar = cartesianToPolar(x, y, centerX, centerY);
                    double radius = polar[0] / Math.min(width, height); // Normalizing the radius
                    double theta = polar[1];
                    for (int i = n; i >= -n; i -= 2){
						l = i;
                        sum += ((n + 1) / Math.PI) * vnl(theta, l, n, radius)* image.getRGB(x, y);
                    }
                }
			}
		return sum;
	}

	public static void main(String[] args) {
		try {
			BufferedImage image = ImageIO.read(new File("input_image.jpg"));
			int width = image.getWidth();
			int height = image.getHeight();
			int centerX = width / 2;
			int centerY = height / 2;
			int n = 5;
			double zernikeValue = znl(n, width, height, centerX, centerY, image);
			System.out.println("Zernike Polynomial Value: " + zernikeValue);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
