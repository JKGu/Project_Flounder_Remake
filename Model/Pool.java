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
    protected int initialPopulationSize;
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
        this.totalGenerations=totalGenerations;this.initialPopulationSize=populationSize;this.populationSize=populationSize;this.crossoverProbability=crossoverProbability; this.mutationProbability=mutationProbability; this.elitism=elitism; this.fitnessEvaluationMethod=fitnessEvaluationMethod; this.selectionMethod=selectionMethod;
        this.population.setSize(populationSize);
    }
    public void initialize_simpleRandom(){
        this.population.randomAssign();
    }

//------------------------------------------------------FITNESS
public double findFitness(Individual i){
    switch(fitnessEvaluationMethod){
        case 'A'://AUTO
        double fitness=1;
        BufferedImage pattern=i.getImage();
        BufferedImage patternMacro=FileManager.blurAndResize(pattern, 3);
        File dirMain = new File(fm.workingPath+"/Cropped");
        File[] dirMainListing = dirMain.listFiles();
        for(File child: dirMainListing){
            File[] imageListing = child.listFiles();
            ArrayList<Double> scoreList = new ArrayList<Double>();
            for(File c: imageListing){
                try {
                    BufferedImage sample = ImageIO.read(c);
                    BufferedImage sampleMacro = FileManager.blurAndResize(sample, 3);
                    double f=FitnessEvaluator.iterateImagesAndFindFitness(pattern, sample);
                    double f2=FitnessEvaluator.iterateImagesAndFindFitness(patternMacro, sampleMacro);
                    double score = (f+f2)/2;
                    scoreList.add(score);
                } catch (IOException e) {
                    System.out.println(e.toString());
                }
            }
            Collections.sort(scoreList);
            int cutPoint = (int)(scoreList.size()*0.8); 
            scoreList=new ArrayList<Double>(scoreList.subList(cutPoint, scoreList.size()));
            double score=0;
            for(Double x : scoreList){score+=x;}
            score/=scoreList.size();
            fitness*=score;
        }
        double rt =1/(double)dirMainListing.length;
        fitness = Math.pow(fitness, rt);
        return fitness;
        case 'H'://HUMAN
        return -1;
        default:
        return -1;
    }
}
public void findFitnessAndSort(Population p){
    for(Individual i : p.getPopulationList()) {
        double score = findFitness(i);
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
            return select_Rank(num);
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

    public Individual[] select_Rank(int num){
        Individual[] array = new Individual[num];
        int populationListSize = this.population.getPopulationList().size();
        int sum = (1+populationListSize)*populationListSize/2;
        for(int i =0; i<num; i++){
            int random = (int)(Math.random()*(sum))+1;
            int rank=0; int accum=0;
            while(accum<random){
                rank++;
                accum+=rank;
            }
            array[i]=this.population.getPopulationList().get(populationListSize-rank).makeCopy();
        }
        
        return array;
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
        int numOfPoints = dimension*dimension;
        int numOfBlocks = (dimension/4)*(dimension/4);

        for(int k=mainColorNum; k<l; k+=4){
            if(Math.random()<crossoverProbability){
                for(int j=0; j<4; j++){
                    output[k+j].setEncoding(g1[k+j].getEncoding());
                }
            }  else {
                for(int j=0; j<4; j++){
                    output[k+j].setEncoding(g2[k+j].getEncoding());
                }
            }
        }
        Individual x = new Individual(output, i1.makeCopy());
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
        if(Math.random()<mutationProbability){
            int index1=(int)(Math.random()*mainColorNum);int index2=(int)(Math.random()*mainColorNum);
            Gene tmp = g[index1].makeCopy();
            g[index1]=g[index2].makeCopy();g[index2]=tmp;
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
            //float range = (float)(this.population.getPopulationList().get(0).fitnessLabel-this.population.getPopulationList().get(this.populationSize-1).fitnessLabel);
            //this.mutationProbability = (float)((1-range));
           // this.populationSize = (int)(0.5*standardPopulationSize+5*range*standardPopulationSize);
           // if(this.populationSize<elitism) this.populationSize = elitism;
            ArrayList<Individual>  elites = findTopOnes(elitism);
            fm.writeFitnessData(this.population.getPopulationList().get(0).fitnessLabel);

            if(currentGeneration%50==0){
                img = this.population.getImage();
                try {
                   ImageIO.write(img, "bmp", new File(fm.workingPath+"/Generated/"+(currentGeneration)+".bmp"));
                 } catch (IOException e) {
                }
            }
            System.out.println(this.population.getPopulationList().get(0).fitnessLabel);

            //----------SELECT
            int offspringNum = populationSize-elitism;
            Individual[] parent1 = select(offspringNum);
            Individual[] parent2 = select(offspringNum);

            //----------CROSSOVER & MUTATE
            for(int i=0; i<offspringNum; i++){
                Individual offspring = crossover(parent1[i], parent2[i]);
                offspring = mutate(offspring);
                p.addIndividual(offspring);

            }

            //-----------KEEP BEST ONES
            for(Individual i: elites){
                p.addIndividual(i);
            }
            //------------UPDATE
            this.population=p;
            this.currentGeneration++;

        }
        BufferedImage img = this.population.getImage();
        try {
            ImageIO.write(img, "bmp", new File(fm.workingPath+"/Generated/"+(totalGenerations)+".bmp"));
        } catch (IOException e) {
        }
    }


    public static void main (String[] args){
        Pool pool = new Pool(5, 30, 4);
        pool.fm = new FileManager("C://Users/steve/Desktop/New folder/Path2");
        pool.fm.loadSamples();

        pool.fm.generateCroppedFiles(20, pool.dimension*pool.pointSizeInPixels, pool.dimension*pool.pointSizeInPixels);
        pool.setParameters(100000,50, (float)0.3, (float) 0.0001, 5, 'A', 'R');
        pool.initialize_simpleRandom();
        System.out.println("START...");
        pool.evolve();
    }

}