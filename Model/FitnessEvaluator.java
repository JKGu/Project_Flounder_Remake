package Model;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import javax.imageio.ImageIO;

public class FitnessEvaluator {


    private static double findHSBDistance(int rgb1, int rgb2){
        float[] c1= findCoordinateInColorSpace(rgb1);   
        float[] c2= findCoordinateInColorSpace(rgb2); 

        double distance = Math.sqrt(Math.pow(c1[0]-c2[0], 2) + Math.pow(c1[1]- c2[1], 2) + Math.pow(c1[2] - c2[2], 2));
        return distance;
    }
    private static float[] findCoordinateInColorSpace(int rgb){
        float[] c=Color.RGBtoHSB((rgb & 0x00ff0000) >> 16,(rgb & 0x0000ff00) >> 8,rgb & 0x000000ff, null);  
        float x,y,z,r; 
        z=c[2];
        if(z>0.5){
            r=2*c[1]-2*c[1]*c[2];
        }else{
            r=2*c[1]*c[2];
        }
        x= (float) (r * Math.cos(c[0]));
        y= (float) (r * Math.sin(c[0]));
        return new float[] {x,y,z};
    }


    public static double iterateImagesAndFindFitness(BufferedImage bi1, BufferedImage bi2){
        int width = bi1.getWidth();
        int bondary = width-1;
        double colorSimilarityScore=0;
        double contrastScore=0;
        double largeBlockScore=0;
        double blendInScore=0;
        for(int i=1; i<bondary; i++){
            for(int j=1; j<bondary;j++){
                int rgb1=bi1.getRGB(i, j); int rgb2=bi2.getRGB(i, j);
                //colorSimilarityScore+=compareColorSimilarity(rgb1, rgb2);
                //contrastScore+=Math.abs(getContrast(bi1, i, j)-getContrast(bi2, i, j));
                blendInScore+=blendIn(bi1, bi2, i, j);
            }
        }
        //colorSimilarityScore/=(width-2)*(width-2);
        //contrastScore/=(width-2)*(width-2);
        blendInScore/=(width-2)*(width-2);
        return blendInScore;//*contrastScore;
    }

    public static float awardLargeBlock(BufferedImage bi, int num){
        int width = bi.getWidth();
        float score=0;
        for(int i=0; i<width; i+=num){
            for(int j=0; j<width;j+=num){
                int rgbReference=bi.getRGB(i, j);
                int count = 0;
                for(int m=0; m<num; m++){
                    for(int n=0; n<num; n++){
                        if(bi.getRGB(i+m, j+n)==rgbReference){
                            count++;
                        }
                    }
                }
                score+=count-1;
            }
        }
        return (float) (0.99*(score / (width*width))+0.01);
    }

    private static double compareColorSimilarity(int rgb1, int rgb2){
        return 2/(findHSBDistance(rgb1, rgb2)+1)-1;
    }

    private static double getContrast(BufferedImage bi, int i, int j){
                double c=  findHSBDistance(bi.getRGB(i, j), bi.getRGB(i + 1, j))
                + findHSBDistance(bi.getRGB(i, j), bi.getRGB(i - 1, j))
                + findHSBDistance(bi.getRGB(i, j), bi.getRGB(i, j + 1))
                + findHSBDistance(bi.getRGB(i, j), bi.getRGB(i, j - 1));
        return c/4;
    }
    
    private static double blendIn(BufferedImage bi1, BufferedImage bi2, int i, int j){
        double min = 0;
        double distance = 0;
        int rgb = bi1.getRGB(i, j);
        distance = findHSBDistance(rgb, bi2.getRGB(i-1, j-1));
        if(distance<min) min = distance;
        distance = findHSBDistance(rgb, bi2.getRGB(i-1, j));
        if(distance<min) min = distance;
        distance = findHSBDistance(rgb, bi2.getRGB(i-1, j+1));
        if(distance<min) min = distance;
        distance = findHSBDistance(rgb, bi2.getRGB(i, j-1));
        if(distance<min) min = distance;
        distance = findHSBDistance(rgb, bi2.getRGB(i, j));
        if(distance<min) min = distance;
        distance = findHSBDistance(rgb, bi2.getRGB(i, j+1));
        if(distance<min) min = distance;
        distance = findHSBDistance(rgb, bi2.getRGB(i+1, j-1));
        if(distance<min) min = distance;
        distance = findHSBDistance(rgb, bi2.getRGB(i+1, j));
        if(distance<min) min = distance;
        distance = findHSBDistance(rgb, bi2.getRGB(i+1, j+1));
        if(distance<min) min = distance;
        return 1-distance;
    }

    private static BufferedImage patternImage(BufferedImage bi){
        int width = bi.getWidth();
        int rgb = Color.getHSBColor((float)0.5, (float)0.5, (float)0.5).getRGB();
        for(int i=0; i<width; i++){
            for(int j=0; j<width; j++){
                //float distance = (float)findHSBDistance(bi.getRGB(i, j), rgb);
                float[] coordinate = findCoordinateInColorSpace(bi.getRGB(i, j));
                Color output = new Color(coordinate[0],coordinate[1], coordinate[2]);
                bi.setRGB(i, j, output.getRGB());
            }
        }
        return bi;
    }
public static void main (String[] args){
    try {
        BufferedImage bi1 = ImageIO.read(new File("C://Users/steve/Desktop/New folder/Path2/Cropped/0.bmp"));
        bi1 = patternImage(bi1);
        ImageIO.write(bi1, "bmp", new File("C://Users/steve/Desktop/New folder/Path2/Cropped/0OUTPUT.bmp"));
    } catch (IOException e) {
        System.out.println("ERROR");
    }
}

    
}