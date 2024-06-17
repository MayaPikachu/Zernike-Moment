package plugins;

import ij.IJ;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;

public class CsvExporter {
    public static void exportToCsv(ArrayList<plugins.ImageData> images, String filename) {
        try (PrintWriter writer = new PrintWriter(new FileWriter(filename)))
        {
            for (plugins.ImageData image: images) {
                double[] row = image.getFeatures();

                writer.print(image.getImageName() + ",");
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
}
