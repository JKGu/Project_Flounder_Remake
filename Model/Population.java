package Model;

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class Population {
    private int size;
    private int mainColorNum;
    private int dimension;
    private int pointSizeInPixels;
    private ArrayList<Individual> population;
    protected boolean sorted;

    public Population(int size, int mainColorNum, int dimension, int pointSizeInPixels){
        this.size=size; 
        this.mainColorNum=mainColorNum; 
        this.dimension=dimension; 
        this.pointSizeInPixels=pointSizeInPixels;
        this.population=new ArrayList<Individual>();
        this.sorted=false;
    }

    public void randomAssign(){
        for(int i=0; i<size; i++){
            Individual tmp = new Individual(mainColorNum,dimension,pointSizeInPixels);
            tmp.randomAssign();
            population.add(tmp);
        }
        this.sorted=false;
    }

    public void addIndividual(Individual i){
        population.add(i.makeCopy());
        this.sorted=false;
    }

    public void sort(){
        Collections.sort(this.population);
        this.sorted=true;
    }

    public void setSize(int x){
        this.size=x;
    }
    public int getSize(){
        return this.size;
    }
    public ArrayList<Individual> getPopulationList(){
        return this.population;
    }
    public void setIndividual(Individual i, int index){
        this.population.set(index, i.makeCopy());
    }

    public String toString(){
        String output="";
        for(Individual i:this.population){
            output+=i.toString()+" ";
        }
        return output;
    }
    
    public BufferedImage getImage(){

        int widthInPixels = pointSizeInPixels*dimension;

        BufferedImage image = new BufferedImage(widthInPixels*this.population.size(), widthInPixels, BufferedImage.TYPE_3BYTE_BGR);
        for(int x=this.population.size()-1; x>=0; x--){
            drawImage(this.population.get(x).getImage(), image, x*widthInPixels);
        }
        return image;
    }

    private static void drawImage (BufferedImage smaller, BufferedImage larger, int x) {
        larger.getGraphics().drawImage(smaller, x, 0, null);
    }
}