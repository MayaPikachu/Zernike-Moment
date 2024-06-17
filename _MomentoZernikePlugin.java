package plugins;/*Exemplo plugin para k-nearest
   Prof. Joaquim Felipe */

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import ij.*;
import ij.io.*;
import ij.ImagePlus;
import ij.process.*;
import ij.gui.*;
import ij.plugin.filter.*;


public class _MomentoZernikePlugin implements PlugInFilter {
    ImagePlus reference;        // Reference image
    int k;                      // Number of nearest neighbors
    Map<String, ImageAccess> imagesByName;

    public int setup(String arg, ImagePlus imp) {
        reference = imp;
        ImageConverter ic = new ImageConverter(imp);
        ic.convertToGray8();
        return DOES_ALL;
    }

    public void run(ImageProcessor img) {

        int n;

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

        plugins.DistanceCalculator distanceCalculator;
        switch (distancia) {
            case "Euclidiana":
                distanceCalculator = new plugins.EuclideanDistance();
                break;
            case "Manhattan":
                distanceCalculator = new plugins.ManhattanDistance();
                break;
            case "Chebyshev":
                distanceCalculator = new plugins.ChebyshevDistance();
                break;
            default:
                distanceCalculator = new plugins.EuclideanDistance();
        }

        SaveDialog sd = new SaveDialog("Escolha seu diretório", "Algum arquivo (necessário)", "");
        if (sd.getFileName()==null) return;
        String dir = sd.getDirectory();


        ArrayList<plugins.ImageData> caracteristicas;
        List<plugins.ImageData> k_nearest;
        try {
            caracteristicas = caracteristicasDiretorio(dir, n);
            plugins.CsvExporter.exportToCsv(caracteristicas, "features.csv");
            plugins.KNN knn = new plugins.KNN(k, distanceCalculator);
            k_nearest = knn.getKNN(caracteristicas, reference.getTitle());
        }
        catch (Exception e) {
            throw new RuntimeException(e);
        }

        int i = 1;
        double precisao = 0;
        String targetClass = reference.getTitle().replaceAll("[^a-z]","");
        for(plugins.ImageData imag: k_nearest){
            imagesByName.get(imag.getImageName()).show("Imagem semelhante número: " + i);
            i++;
            IJ.log(imag.getImageName());

            String currentClass = imag.getImageName().replaceAll("[^a-z]","");
            if(currentClass.equals(targetClass)){
                precisao += 1./k;
            }
        }
        IJ.log("Precisão: " + String.format("%.3f", precisao));
    }

    public ArrayList<plugins.ImageData> caracteristicasDiretorio(String dir, int n) throws Exception{
        IJ.log("");
        IJ.log("Searching images");

        if (!dir.endsWith(File.separator))
            dir += File.separator;

        String[] list = new File(dir).list();  /* lista de arquivos */
        if (list == null) throw new Exception("Erro: diretorio vazio.");

        List<plugins.ImageData> vetoresCaracteristicas = new ArrayList<>();

        for (int i=0; i<list.length; i++) {
            IJ.showStatus(i+"/"+list.length+": "+list[i]);   /* mostra na interface */
            IJ.showProgress((double)i / list.length);  /* barra de progresso */
            File f = new File(dir+list[i]);
            if (!f.isDirectory()) {
                ImagePlus image = new Opener().openImage(dir, list[i]); /* abre imagem image */

                if (image != null) {
                    ImageAccess input = new ImageAccess(image.getProcessor());
                    imagesByName.put(image.getTitle(), input);
                    int nx = input.getWidth();
                    int ny = input.getHeight();
                    vetoresCaracteristicas.add(new plugins.ImageData(plugins.Zernike.getFeatures(n, nx, ny, input), image.getTitle()));
                }
            }
        }
        IJ.showProgress(1.0);
        IJ.showStatus("");

        return vetoresCaracteristicas;
    }
}