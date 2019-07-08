package Model;

import java.awt.image.BufferedImage;

public class Individual implements Comparable{

    private Chromosome chromosome;
    private int pointSizeInPixels;
    private int mainColorNum;
    private int dimension;
    protected double fitnessLabel;

    public Individual(int mainColorNum, int dimension, int pointSizeInPixels){
        this.chromosome=new Chromosome(mainColorNum, dimension);
        this.pointSizeInPixels=pointSizeInPixels;
        this.mainColorNum=mainColorNum;
        this.dimension=dimension;
        this.fitnessLabel=-1;
    }
    public Individual(Gene[] array, Individual species){
        this(species.mainColorNum,species.dimension,species.pointSizeInPixels);
        this.chromosome=new Chromosome(array.clone(), species.chromosome);
    }
    public Individual makeCopy(){
        return new Individual(this.chromosome.getGeneArray(), this);
    }

    public void randomAssign(){
        this.chromosome.simpleRandomAssign();
    }

    public BufferedImage getImage(){
        int widthInPixels = pointSizeInPixels*dimension;
        BufferedImage image = new BufferedImage(widthInPixels, widthInPixels, BufferedImage.TYPE_3BYTE_BGR);
        for(int i=0; i<dimension; i++){
            for(int j=0; j<dimension; j++){
                
                for(int m=0; m<pointSizeInPixels; m++){
                    for(int n=0; n<pointSizeInPixels; n++){

                        image.setRGB(i*pointSizeInPixels+m, j*pointSizeInPixels+n, this.chromosome.getColorOfPoint(i, j).getRGB());
                    }
                }

            }
        }
        return image;
    }


    public Chromosome getChromosome() {
        return chromosome;
    }
    public void setChromosome(Chromosome c){
        this.chromosome=c.makeCopy();
    }


    @Override
    public int compareTo(Object o) {
        Individual i=(Individual)o;
        return Double.compare(i.fitnessLabel, this.fitnessLabel);
    }

    public String toString(){
        return this.chromosome.toString();
    }

}