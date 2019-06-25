package Model;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;

import javax.imageio.ImageIO;

public class Pool {
/////////////////////////DATA STRUCTURES
    protected Population population;
    protected FileManager fm;
    protected FitnessEvaluator fe;
/////////////////////////VARIABLES FIXED IN ALL GENERATIONS
    protected int mainColorNum;
    protected int dimension;
    protected int pointSizeInPixels;
////////////////////////VARIABLES MAY BE CHANGED DURING EVOLUTION
    protected int totalGenerations;
    protected int populationSize;
    protected float crossoverProbability;
    protected float mutationProbability;
    protected int elitism;
    protected char fitnessEvaluationMethod;
    protected char selectionMethod;//W=Roulette Wheel Selection;R=Rank Selection
///////////////////////VARIABLES THAT UPDATE IN EACH GENERATION
    protected int currentGeneration;


    public Pool(int mainColorNum, int dimension, int pointSizeInPixels){
        this.population = new Population(this.populationSize, mainColorNum, dimension, pointSizeInPixels);
        this.fe = new FitnessEvaluator();

        this.mainColorNum = mainColorNum;
        this.dimension=dimension;
        this.pointSizeInPixels=pointSizeInPixels;
        
        this.currentGeneration=0;
    }

    public void setParameters(int totalGenerations, int populationSize, float crossoverProbability, float mutationProbability, int elitism, char fitnessEvaluationMethod, char selectionMethod){
        this.totalGenerations=totalGenerations;this.populationSize=populationSize;this.crossoverProbability=crossoverProbability; this.mutationProbability=mutationProbability; this.elitism=elitism; this.fitnessEvaluationMethod=fitnessEvaluationMethod; this.selectionMethod=selectionMethod;
        this.population.setSize(populationSize);
    }
    public void initialize_simpleRandom(){
        this.population.randomAssign();
    }

//------------------------------------------------------FITNESS
public float findFitness(Individual i){
    switch(fitnessEvaluationMethod){
        case 'A'://AUTO
        float fitness=0;
        BufferedImage bi1=FileManager.blurAndResize(i.getImage(), 2); 
        ArrayList<String> croppedList = fm.croppedFiles;
        for(String x: croppedList){
            try {
                BufferedImage bi2 = ImageIO.read(new File(x));
                fitness+=FitnessEvaluator.compareColorSimilarity(bi1, bi2);
            } catch (IOException e) {
            }
        }
        fitness/=croppedList.size();
        return fitness;
        case 'H'://HUMAN
        return -1;
        default:
        return -1;
    }
}
public void findFitnessAndSort(Population p){
    for(Individual i : p.getPopulationList()) {
        float score = findFitness(i);
        i.fitnessLabel=score;
    }
    p.sort();
}
public ArrayList<Individual> findTopOnes(int num){
    if(!population.sorted){
        findFitnessAndSort(population);
    }
    ArrayList<Individual> output = new ArrayList<Individual>(population.getPopulationList().subList(0, num));
    for(int i=0; i<output.size(); i++){
        output.set(i, output.get(i).makeCopy());
    }
    return output;
}    
//---------------------------------------------------------SELECT
    public Individual[] select(int num){

        switch(selectionMethod){
            case 'N':
            return select_Naive(num);
            case 'R':
            return select_Rank();
            default:
            return null;
        }
    }

    public Individual[] select_Naive(int num){
        Individual[] array = new Individual[num];
        for(int i =0; i<num; i++){
            array[i]=population.getPopulationList().get(i).makeCopy();
        }
        return array;
    }

    public Individual[] select_Rank(){
        return null;
    }
//--------------------------------------------------CROSSOVER
    public Individual crossover(Individual i1, Individual i2){
        Gene[] output = i1.getChromosome().getGeneArray();
        Gene[] g1=i1.getChromosome().getGeneArray(); Gene[] g2=i2.getChromosome().getGeneArray();
        int l=g1.length;
        for(int k=0; k<mainColorNum; k++){
            float outputHSB[] = new float[3];
            Color c1=Gene.decodeColor(g1[k].getEncoding());
            int rgb1=c1.getRGB();
            float[] hsb1=Color.RGBtoHSB((rgb1 & 0x00ff0000) >> 16,(rgb1 & 0x0000ff00) >> 8,rgb1 & 0x000000ff, null);  
            Color c2=Gene.decodeColor(g2[k].getEncoding());
            int rgb2=c2.getRGB();
            float[] hsb2=Color.RGBtoHSB((rgb2 & 0x00ff0000) >> 16,(rgb2 & 0x0000ff00) >> 8,rgb2 & 0x000000ff, null);  
            if(Math.random()<crossoverProbability) outputHSB[0]=hsb1[0]; else outputHSB[0]=hsb2[0];
            if(Math.random()<crossoverProbability) outputHSB[1]=hsb1[1]; else outputHSB[1]=hsb2[1];
            if(Math.random()<crossoverProbability) outputHSB[2]=hsb1[2]; else outputHSB[2]=hsb2[2];
            output[k].setEncoding(Gene.encodeColorToBinary(Color.getHSBColor(outputHSB[0], outputHSB[1], outputHSB[2])));
        }
        for(int k=mainColorNum; k<l; k++){
            if(Math.random()<crossoverProbability) output[k].setEncoding(g1[k].getEncoding()); else output[k].setEncoding(g2[k].getEncoding());;
        }
        Individual x = new Individual(output, i1);
        return x;
    }
//----------------------------------------------------MUTATION
    public Individual mutate(Individual i){
        Gene[] g = i.getChromosome().getGeneArray().clone();
        int length=g.length;
        for(int k=0; k<mainColorNum; k++){
            Color c=Gene.decodeColor(g[k].getEncoding());
            int rgb=c.getRGB();
            float[] hsb=Color.RGBtoHSB((rgb & 0x00ff0000) >> 16,(rgb & 0x0000ff00) >> 8,rgb & 0x000000ff, null);  
            if(Math.random()<mutationProbability){hsb[0] = (float)Math.random();}
            if(Math.random()<mutationProbability){hsb[1] = (float)Math.random();}
            if(Math.random()<mutationProbability){hsb[2] = (float)Math.random();}
            g[k].setEncoding(Gene.encodeColorToBinary(Color.getHSBColor(hsb[0], hsb[1], hsb[2])));
        }
        for(int k=mainColorNum; k<length; k++){
            if(Math.random()<mutationProbability){
                g[k].setEncoding(Gene.encodeNumber((int)(Math.random()*mainColorNum)));
            }
        }
        Individual output= new Individual(g, i);
        return output;
    }
//--------------------------------------------------EVOLVE
    public void evolve(){

        while(currentGeneration<totalGenerations){
            BufferedImage img = this.population.getImage();

            Population p = new Population(populationSize, mainColorNum, dimension, pointSizeInPixels);
            findFitnessAndSort(this.population);
            ArrayList<Individual>  elites = findTopOnes(elitism);


            img = this.population.getImage();
            try {
                ImageIO.write(img, "jpg", new File(fm.workingPath+"/Generated/"+(currentGeneration)+".jpg"));
            } catch (IOException e) {
            }

            //----------SELECT
            int offspringNum = populationSize-elitism;
            Individual[] parent1 = select(offspringNum);
            Individual[] parent2 = select(offspringNum);

            int counter =0;
            for(Individual i: elites){
                img=i.getImage();
                try {
                    ImageIO.write(img, "jpg", new File(fm.workingPath+"/Generated/"+(currentGeneration)+"["+counter+"]"+"X1.jpg"));
                } catch (IOException e) {
                }
                counter++;
            }

            //----------CROSSOVER & MUTATE
            for(int i=0; i<offspringNum; i++){
                Individual offspring = crossover(parent1[i], parent2[i]);
                img=offspring.getImage();
                try {
                    ImageIO.write(img, "jpg", new File(fm.workingPath+"/Generated/"+(currentGeneration)+"_"+i+"_"+"X2A.jpg"));
                } catch (IOException e) {
                }
                offspring = mutate(offspring);
                img=offspring.getImage();
                try {
                    ImageIO.write(img, "jpg", new File(fm.workingPath+"/Generated/"+(currentGeneration)+"_"+i+"_"+"X2B.jpg"));
                } catch (IOException e) {
                }
                p.addIndividual(offspring);
            }

            counter =0;
            for(Individual i: elites){
                img=i.getImage();
                try {
                    ImageIO.write(img, "jpg", new File(fm.workingPath+"/Generated/"+(currentGeneration)+"["+counter+"]"+"X3.jpg"));
                } catch (IOException e) {
                }
                counter++;
            }


            //-----------KEEP BEST ONES
            for(Individual i: elites){
                p.addIndividual(i);
            }

            //------------UPDATE

            this.currentGeneration++;
        }
    }

    public static void main (String[] args){
        Pool pool = new Pool(3, 10, 5);
        pool.fm = new FileManager("C://Users/steve/Desktop/New folder/Path2");
        pool.fm.loadSamples();
        pool.fm.generateCroppedFiles(5, pool.dimension*pool.pointSizeInPixels, pool.dimension*pool.pointSizeInPixels);
        pool.setParameters(100, 10, (float)0.1, (float) 1, 2, 'A', 'N');
        pool.initialize_simpleRandom();
        pool.evolve();
    }

}