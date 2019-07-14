package Model;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.*;
import javax.imageio.*;
import java.util.*;

public class FitnessEvaluator {

    public static double iterateImagesAndFindFitness(BufferedImage bi1, BufferedImage bi2){
        int width = bi1.getWidth();
        //double score=0;
        ArrayList<Double> distanceList = new ArrayList<Double>();
        for(int i=0; i<width; i++){
            for(int j=0; j<width;j++){
                int rgb1=bi1.getRGB(i, j); int rgb2=bi2.getRGB(i, j);
                //score+=findColorDifference(rgb1, rgb2);
                distanceList.add(findColorDifference(rgb1, rgb2));
            }
        }
        //score/=width*width;
        Collections.sort(distanceList);
        distanceList=new ArrayList<Double>(distanceList.subList(0,(int)(distanceList.size()*0.9) ));
        double distance=0;
        for(Double x : distanceList){
            distance+=x;
        }
        distance/=distanceList.size();
        return curve2(distance);
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

    private static double findHSBDistance(int rgb1, int rgb2){
        float[] c1= findCoordinateInColorSpace(rgb1);   
        float[] c2= findCoordinateInColorSpace(rgb2); 
        double distance = Math.sqrt(Math.pow(c1[0]-c2[0], 2) + Math.pow(c1[1]- c2[1], 2) + Math.pow(c1[2] - c2[2], 2));
        return distance;
    }

    private static double findColorDifference(int rgb1, int rgb2){//https://www.compuphase.com/cmetric.htm
        float[] c1 = {(rgb1 & 0x00ff0000) >> 16,(rgb1 & 0x0000ff00) >> 8,rgb1 & 0x000000ff};
        float[] c2 = {(rgb2 & 0x00ff0000) >> 16,(rgb2 & 0x0000ff00) >> 8,rgb2 & 0x000000ff};
        float r = (c1[0]+c2[0])/2;
        float dR = c1[0]-c2[0];
        float dG = c1[1]-c2[1];
        float dB = c1[2]-c2[2];
        double output = Math.sqrt((2+r/256)*(dR*dR)+4*(dG*dG)+(2+(255-r)/256)*(dB*dB));
        output=output/765;
        return output;
    }

    private static double curve(double x){
        double output =  -125913*(x-1)*(x-1)*(2209*x*x-2538*x-1271)/160000000;
        if(output<0)output=0;if(output>1)output=1;
        return output;
    }

    private static double curve2(double x){
        return 1-x;
        //return -Math.pow(x, 0.7)+1;
    }

public static void main (String[] args){
    
    try {
        BufferedImage bi1 = ImageIO.read(new File("C://Users/steve/Desktop/New folder/Path2/Cropped/0.bmp"));
        BufferedImage bi2 = ImageIO.read(new File("C://Users/steve/Desktop/New folder/Path2/Cropped/3.bmp"));
        System.out.println(iterateImagesAndFindFitness(bi1, bi2));
    } catch (IOException e) {
        System.out.println("ERROR");
    }
    
}

    
}