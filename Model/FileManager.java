package Model;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

import javax.imageio.ImageIO;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class FileManager {
    protected String workingPath;
    protected ArrayList<String> originalFiles;
    protected ArrayList<String> croppedFiles;
    private FileWriter writer;
    
    public FileManager(String workingPath) {
        this.workingPath = workingPath;
        File dir1 = new File(workingPath + "/Original");
        dir1.mkdir();
        File dir2 = new File(workingPath + "/Cropped");
        dir2.mkdir();
        File dir3 = new File(workingPath + "/Generated");
        dir3.mkdir();
        File dir4 = new File(workingPath + "/Data");
        dir4.mkdir();
        this.originalFiles = new ArrayList<String>();
        this.croppedFiles = new ArrayList<String>();
        // -----------WRITING FITNESS DATA
        File fitnessData = new File(workingPath + "/Data/FitnessData.txt");
        try {
            fitnessData.createNewFile();
        } catch (IOException e) {
            System.out.println("ERROR:" + e.toString());
            e.printStackTrace();
        }
        try {
            writer = new FileWriter(workingPath + "/Data/FitnessData.txt");
        } catch (IOException e) {
            System.out.println("ERROR:" + e.toString());
            e.printStackTrace();
        }
    }

    public void writeFitnessData(double x){
        try {
            writer.write(x+"\n");
            writer.flush();
        } catch (IOException e) {
            System.out.println("ERROR:"+e.toString());e.printStackTrace();
        }
    }

    public void loadSamples(){
        File dir = new File(workingPath+"/Original");
        File[] directoryListing = dir.listFiles();
        for(File f: directoryListing){
            originalFiles.add(f.getAbsolutePath());
        }
    }

    public void loadCroppedFiles(){
        File dir = new File(workingPath+"/Cropped");
        File[] directoryListing = dir.listFiles();
        for(File f: directoryListing){
            croppedFiles.add(f.getAbsolutePath());
        }
    }

    public void generateCroppedFiles(int num, int w, int h){
        
        this.croppedFiles.add(workingPath+"/Cropped/0.bmp");
       // this.croppedFiles.add(workingPath+"/Cropped/1.bmp");
        /*
        num=num*originalFiles.size();
        for(int i=0; i<num; i++){
            int r = (int)(Math.random()*(originalFiles.size()));
            try {
                BufferedImage b = ImageIO.read(new File(originalFiles.get(r)));
                int x = (int)(Math.random()*b.getWidth());
                int y = (int)(Math.random()*b.getHeight());
                if(x+w<b.getWidth()-1&&y+h<b.getHeight()-1){
                    BufferedImage tmp = b.getSubimage(x, y, w, h);
                    //tmp = blurAndResize(tmp, 2);
                    try {
                        ImageIO.write(tmp, "bmp", new File(workingPath+"/Cropped/"+Integer.toString(i,16)+".bmp"));
                        this.croppedFiles.add(workingPath+"/Cropped/"+Integer.toString(i,16)+".bmp");
                    } catch (IOException e) {
                    }
                }else{
                    if(i!=0) i--;
                }
            } catch (IOException e) {
                if(i!=0) i--;
            }
        }
        */
    }

    public static BufferedImage blurAndResize(BufferedImage bi, int pixel){
        int newWidth = bi.getWidth()/pixel; int range =newWidth*pixel;
        BufferedImage output = new BufferedImage(newWidth, newWidth, BufferedImage.TYPE_3BYTE_BGR);
        int h=bi.getHeight(); int w=bi.getWidth();
        for(int m=0; m<range; m+=pixel){
            for(int n=0; n<range; n+=pixel){
                int red =0; int blue = 0; int green = 0;
                for(int i=0; i<pixel; i++){
                    for(int k=0; k<pixel; k++){
                        int rgb = bi.getRGB(m+i, n+k);
                        red+=(rgb & 0x00ff0000) >> 16;
                        green+=(rgb & 0x0000ff00) >> 8;
                        blue+=rgb & 0x000000ff;
                    }
                }
                int rgb = (red/pixel/pixel<<16)|(green/pixel/pixel<<8)|(blue/pixel/pixel);
                output.setRGB(m/pixel, n/pixel, rgb);
            }
        }
        return output;
    }

    public static void main (String[] args){
        FileManager test = new FileManager("C:/Users/steve/Desktop/New folder/Path");
        test.loadSamples();
        System.out.println("Samples loaded");
        test.generateCroppedFiles(1, 200, 200);
        System.out.println("Files generated");

    }



}