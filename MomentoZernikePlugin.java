package plugins;/*Exemplo plugin para k-nearest
   Prof. Joaquim Felipe */

import java.io.*;
import java.util.ArrayList;

import ij.*;
import ij.io.*;
import ij.ImagePlus;
import ij.process.*;
import ij.gui.*;
import ij.plugin.filter.*;


public class _MomentoZernikePlugin implements PlugInFilter {
    ImagePlus reference;        // Reference image
    int k;                      // Number of nearest neighbors
    int level;                  // Wavelet decoposition level

    public int setup(String arg, ImagePlus imp) {
        reference = imp;
        ImageConverter ic = new ImageConverter(imp);
        ic.convertToGray8();
        return DOES_ALL;
    }

    public void run(ImageProcessor img) {

        ImageAccess[] imagemBase;
        double nDist;
        double[][] distancias;
        ImagePlus reference;
        int k;
        int n;
        String[] nomes;


        GenericDialog gd = new GenericDialog("Momentos de Zernike", IJ.getInstance());
        gd.addNumericField("Número 'k' de vizinhos a serem buscados: ", 5, 0);
        gd.addNumericField("Grau máximo 'n' do polinômio a ser utilizado: ", 5, 0);
        gd.addChoice("Função de distancia usada: ", new String[] { "Manhattan ", "Euclidiana", "Chebyshev"}, "Euclidiana");
        gd.showDialog();
        if (gd.wasCanceled())
            return;
        k = (int) gd.getNextNumber();
        n = (int) gd.getNextNumber();
        String distancia = gd.getNextChoice();

        DistanceCalculator distanceCalculator;
        switch (distancia) {
            case "Euclidiana":
                distanceCalculator = new DistanciaEuclidiana();
                break;
            case "Manhattan":
                distanceCalculator = new DistanciaManhattan();
                break;
            case "Chebyshev":
                distanceCalculator = new DistanciaChebyshev();
                break;
        }

        SaveDialog sd = new SaveDialog("Escolha seu diretório", "Algum arquivo (necessário)", "");
        if (sd.getFileName()==null) return;
        String dir = sd.getDirectory();


        ArrayList<double[]> caracteristicas;
        try {
            caracteristicas = caracteristicasDiretorio(dir, n);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public ArrayList<double[]> caracteristicasDiretorio(String dir, int n) throws Exception{
        IJ.log("");
        IJ.log("Searching images");

        if (!dir.endsWith(File.separator))
            dir += File.separator;

        String[] list = new File(dir).list();  /* lista de arquivos */
        if (list == null) throw new Exception("Erro: diretorio vazio.");

        ArrayList<double[]> vetoresCaracteristicas = new ArrayList<>();

        for (int i=0; i<list.length; i++) {
            IJ.showStatus(i+"/"+list.length+": "+list[i]);   /* mostra na interface */
            IJ.showProgress((double)i / list.length);  /* barra de progresso */
            File f = new File(dir+list[i]);
            if (!f.isDirectory()) {
                ImagePlus image = new Opener().openImage(dir, list[i]); /* abre imagem image */
                if (image != null) {

                    // CODIGO para inverter a imagem:
                    ImageAccess input = new ImageAccess(image.getProcessor());
                    int nx = input.getWidth();
                    int ny = input.getHeight();
                    vetoresCaracteristicas.add(Zernike.getFeatures(n, nx, ny, input));
                }
            }
        }
        IJ.showProgress(1.0);
        IJ.showStatus("");

        return vetoresCaracteristicas;
    }

    private class ImageData {
        private double[] data;
        private String imageName;

        public ImageData(double[] data, String imageNAme) {

        }

        public double[] getData() {
            return data;
        }
    }

    private void saveToCsv(ArrayList<double[]> data, String filename) {
        try (PrintWriter writer = new PrintWriter(new FileWriter(filename)))
        {
            for (double[] row : data) {
                writer.print(imageNames[j] + ",");

                for (int i = 0; i < row.length; i++) {
                    writer.print(row[i]);
                    if (i < row.length - 1) writer.print(",");
                }
            }
            for (int j = 0; j < caracteristicas.length; j++) {
                double[] row = caracteristicas[j];

                for (int i = 0; i < row.length; i++) {
                    writer.print(row[i]);
                    if (i < row.length - 1) writer.print(",");
                }
                writer.println();
            }
            writer.close();
        }
        catch (IOException e)
        {
            IJ.log("\n" + e.getMessage());
        }
    }

    private interface DistanceCalculator {
        double distance(double[] p1, double[] p2);
    }

    private class DistanciaManhattan implements DistanceCalculator {
        @Override
        public double distance(double[] p1, double[] p2) {
            double dist = 0.0;
            for (int i=0;i<Math.min(p1.length,p2.length);i++) {
                dist += Math.abs(p1[i]-p2[i]);
            }
            return dist;
        }
    }

    private class DistanciaEuclidiana implements DistanceCalculator {
        @Override
        public double distance(double[] p1, double[] p2) {
            double dist = 0.0;
            for (int i=0;i<Math.min(p1.length,p2.length);i++) {
                dist += (p1[i]-p2[i])*(p1[i]-p2[i]);
            }
            return Math.sqrt(dist);
        }
    }
    private class DistanciaChebyshev implements DistanceCalculator {
        @Override
        public double distance(double[] p1, double[] p2) {
            double dist = 0.0;
            for (int i=0;i<Math.min(p1.length,p2.length);i++) {
                dist = Math.max(dist, Math.abs(p1[i]-p2[i]));
            }
            return dist;
        }
    }
}