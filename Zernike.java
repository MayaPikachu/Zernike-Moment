package plugins;

import javax.imageio.ImageIO;
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
	private static double znl(int n, int l, int width, int height, ImageAccess image) {
		double sum = 0.0;
		double radius = (double) Math.min(width, height) /2;
		for(double r = 0; r < radius; r++) {
			for(double theta = 0; theta < 2*Math.PI; theta+= 0.01){
				sum += ((n + 1) / Math.PI) * vnl(theta, l, n, (r/radius)) * Math.ceil(image.getPixel((int) (r*Math.cos(theta)), (int) (r*Math.sin(theta)))/255)*r*(0.01);
			}
		}
		return sum;
	}

	private static double[] znls(int n, int width, int height, ImageAccess image){
		double result = 0.0;
		double[] arr = new double[n+1];
		int i = 0;
		for(int l = -n; l <= n; l+=2){
			arr[i] = znl(n, l, width, height, image);
			i++;
		}
		return arr;
	}

	public static double[] getFeatures(int nMax, int width, int height, ImageAccess image){
		double[] arr = new double[(nMax+2)*(nMax+1)/2];
		int i = 0;
		for(int n = 0; n <= nMax; n++){
			double[] temp = znls(n, width, height, image);
			for(int j = 0; j <=n; j++){
				arr[i] = temp[j];
				i++;
			}
		}
		return arr;
	}

	public static void main(String[] args) {
		try {
			ImageAccess image = ImageIO.read(new File("input_image.jpg"));
			int width = image.getWidth();
			int height = image.getHeight();
			int n = 5;
			double[] vetorCaracterísticas = getFeatures(n, width, height, image);
			System.out.println("Zernike Polynomial Value: " + vetorCaracterísticas);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
