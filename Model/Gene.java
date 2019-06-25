package Model;

import java.awt.Color;


/*
    Each gene encodes a trait.
*/
public class Gene {
    private String traitName;
    private String encoding;

//////////////////////////////////////CONSTRUCTORS////////////////////////////////////////////////    
    public Gene(String traitName, String encoding){
        this.traitName=traitName; this.encoding=encoding;
    }
    public Gene(String traitName){//random color. future development: add switch on traitName
        String tempString;
                int r = (int)(Math.random()*256);int g = (int)(Math.random()*256);int b = (int)(Math.random()*256); 
                Color tmp = new Color(r, g, b);
                tempString = encodeColorToBinary(tmp);
        this.traitName=traitName; this.encoding=tempString;
    }
    public Gene(String traitName, int number){
        int t = (int)(Math.random()*number);
        this.traitName=traitName; 
        String s = String.format("%4s", Integer.toBinaryString(t));
        this.encoding = padWithZeros(s, 4);
    }


///////////////////////////METHODS////////////////////////////////////////////////////////////////
    public void setEncoding(String encoding){
        this.encoding=encoding;
    }
    public String getEncoding(){
        return encoding;
    }

    public String toString(){
        String output = "[(";
        switch(this.traitName){
            case "Color":
            output+=this.encoding+")(";
            Color c = decodeColor(this.encoding);
            output+=c.getRed()+","+c.getGreen()+","+c.getBlue()+")]";
            break;
            case "Pattern":
            output+=this.encoding+")("+Integer.parseInt(this.encoding, 2)+")]";
            break;
        }
        return output;
    }

///////////////////////////STATIC FUNCTIONS////////////////////////////////////////////////////////
    public static String encodeColorToBinary(Color c){
        String output = "";
        int r = c.getRed(); int g = c.getGreen(); int b = c.getBlue();
        float[] hsb = Color.RGBtoHSB(r, g, b, null);
        output+= padWithZeros(String.format("%9s", Integer.toBinaryString((int)(hsb[0]*360))) , 9)
                +padWithZeros(String.format("%7s", Integer.toBinaryString((int)(hsb[1]*100))) , 7)
                +padWithZeros(String.format("%7s", Integer.toBinaryString((int)(hsb[2]*100))) , 7);
        return output;
    }
    public static float decodeColorToFloat(String s, int i){//i : 0=h, 1=s, 2=b
        float output;
        if(i==0){
            output = ((float)Integer.parseInt("0"+s.substring(0, 9),2)) / 360;
        }else if(i==1){
            output =  ((float)Integer.parseInt("0"+s.substring(9, 16),2)) / 100;
        }else if(i==2){
            output = ((float)Integer.parseInt("0"+s.substring(16),2)) / 100;
        }else{
            System.out.println("Invalid parameter");
            return -1;
        }
        if(output<0) output=0; if(output>1) output=1;
        return output;
    }
    public static Color decodeColor(String s){
        return new Color(Color.HSBtoRGB(decodeColorToFloat(s,0), decodeColorToFloat(s,1), decodeColorToFloat(s,2)));
    }
    public static String encodeNumber(int n){
        return padWithZeros(String.format("%4s", Integer.toBinaryString(n)), 4);
    }
    public static int decodeNumer(String s){
        return Integer.parseInt(s, 2);
    }

    public static String padWithZeros(String s, int bits){
        char[] tmp = s.toCharArray();
        for(int i=0; i<bits; i++){
            if(tmp[i]!='0'&&tmp[i]!='1'){
                tmp[i]='0';
            }else{
                break;
            }
        }
        return String.valueOf(tmp);
    }
    //////////////////////////////////////////TEST FUNCTION///////////////////////////////////
    public static void main (String[] args) {
        System.out.println("Encoding color PINK: RGB="+Color.pink.getRed()+" "+Color.pink.getGreen()+" "+Color.pink.getBlue());
        Gene test1 = new Gene("Color", encodeColorToBinary(Color.PINK));
        System.out.println(test1.encoding);
        Color out = new Color(Color.HSBtoRGB(decodeColorToFloat(test1.encoding,0), decodeColorToFloat(test1.encoding,1), decodeColorToFloat(test1.encoding,2)));
        System.out.println("Decoding color PINK: RGB="+out.getRed()+" "+out.getGreen()+" "+out.getBlue());
        test1 = new Gene("Color");
        System.out.println(test1);
        test1 = new Gene("Pattern", 7);
        System.out.println(test1);
    }
}