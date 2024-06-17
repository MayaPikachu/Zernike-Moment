package plugins;

import javax.imageio.ImageIO;
import java.io.File;
import java.io.IOException;

public class Zernike {

	// Function to calculate the radial component of the Zernike polynomial
	private static double radial(int n, int l, double rho) {
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

	// Function to calculate the Zernike polynomial value for an image at a given n and a given l
	private static double znl(int n, int l, int width, int height, ImageAccess image) {
		double zr = 0.0;
		double zi = 0.0;
		int count = 0;
		int pixelTotal = width*height;
		double radial;
		double radius;
		double theta;
		for(int y = 0; y < pixelTotal - 1; y++){
			for(int x = 0; x <pixelTotal - 1; x++){
				radius = Math.sqrt(Math.pow(2*x - pixelTotal + 1, 2) + Math.pow(pixelTotal - 1 - 2*y, 2))/pixelTotal;
				if(radius <= 1){
					radial = radial(n, l, radius);
					theta = Math.atan(((double) pixelTotal - 1 - 2 * y) /(2*x - (double) pixelTotal + 1));
					zr += image.getPixel(x, y)*radial*Math.cos(l*theta);
					zi += image.getPixel(x, y)*radial*Math.sin(l*theta);
					count++;
				}
			}
		}
		return (n + 1)*Math.sqrt(zr*zr + zi*zi)/count;
	}



	//function to create an array containing the moment of the image at a given n
	private static double[] znls(int n, int width, int height, ImageAccess image){
		double[] arr = new double[n+1];
		int i = 0;
		for(int l = -n; l <= n; l+=2){
			arr[i] = znl(n, l, width, height, image);
			i++;
		}
		return arr;
	}

	//function to create an array containing all the moments of an image, given a maximum n
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
