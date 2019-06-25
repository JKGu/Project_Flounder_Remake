package Model;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import javax.imageio.ImageIO;

public class FitnessEvaluator {


    public static float compareColorSimilarity(BufferedImage bi1, BufferedImage bi2){
        float score =0;
        int width = bi1.getWidth();
        for(int i=0; i<width; i++){
            for(int j=0; j<width;j++){
                int rgb1=bi1.getRGB(i, j); int rgb2=bi2.getRGB(i, j);
                float[] c1=Color.RGBtoHSB((rgb1 & 0x00ff0000) >> 16,(rgb1 & 0x0000ff00) >> 8,rgb1 & 0x000000ff, null);    
                float[] c2=Color.RGBtoHSB((rgb2 & 0x00ff0000) >> 16,(rgb2 & 0x0000ff00) >> 8,rgb2 & 0x000000ff, null);       
                score+=Math.sqrt( Math.pow(c1[2]-c2[2], 2)+Math.pow(c1[1]*Math.cos(c1[0])-c2[1]*Math.cos(c2[0]), 2)+Math.pow(c1[1]*Math.sin(c1[0])-c2[1]*Math.sin(c2[0]), 2));
            }
        }
        return 1-score/(width*width);
    }
    

public static void main (String[] args){
    try {
        BufferedImage bi1 = ImageIO.read(new File("C://Users/steve/Desktop/New folder/Path2/Cropped/0K.jpg"));
        BufferedImage bi2 = ImageIO.read(new File("C://Users/steve/Desktop/New folder/Path2/Cropped/3K.jpg"));
        System.out.println(FitnessEvaluator.compareColorSimilarity(bi1, bi2));
    } catch (IOException e) {
    }
}

    
}