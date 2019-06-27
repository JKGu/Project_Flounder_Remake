package Model;

import java.awt.Color;

/*
    Genes build up a chromosome
*/
public class Chromosome {
    private Gene [] geneArray;
    private int mainColorNum;
    private int dimension;

    public Chromosome(int mainColorNum, int dimension){
        this.mainColorNum=mainColorNum;
        this.dimension=dimension;
        this.geneArray=new Gene[mainColorNum+dimension*dimension];
    }
    public Chromosome(Gene[] array, Chromosome species){
        this(species.mainColorNum,species.dimension);
        this.geneArray=array.clone();
    }
    public Chromosome makeCopy(){
        Chromosome output = new Chromosome(this.getGeneArray(), this);
        return output;
    }

    public Color getColorOfPoint(int x, int y){
        Gene point = geneArray[mainColorNum+y*dimension+x];
        int num = Gene.decodeNumer(point.getEncoding());
        Color c = Gene.decodeColor(geneArray[num].getEncoding());
        return c;
    }

    public void simpleRandomAssign(){
        for(int i=0; i<mainColorNum; i++){
            geneArray[i]=new Gene("Color");
        }
        int length = mainColorNum+dimension*dimension;
        for(int i=mainColorNum; i<length; i++){
            geneArray[i]=new Gene("Pattern", mainColorNum);
        }
    }

    public Gene[] getGeneArray(){
        int x=this.geneArray.length;
        Gene[] output = new Gene[x];
        for(int i=0; i<x; i++){
            output[i]=this.geneArray[i].makeCopy();
        }
        return output;
    }

    public void setGeneArray(Gene[] g){
        this.geneArray=g.clone();
    }


    public String toString(){
        String output = "[";
        for(int i=0; i<geneArray.length; i++){
            output+=geneArray[i];
        }
        return output+"]";
    }


    public static void main (String[] args){
        Chromosome t = new Chromosome(7, 5);
        t.simpleRandomAssign();
        System.out.println(t);
    }
}