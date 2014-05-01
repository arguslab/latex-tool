/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package latex;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import javax.swing.JOptionPane;

/**
 *
 * @author Dwight
 */
public class SvnFileReader {
    private Paperwork[] _listOfPapers;
   
    
    public SvnFileReader(){
        try {
        readTheFile();
        }
        catch (IOException e){
            System.out.println(e);
        }            
    }
    
    public boolean doesTheFileExists(String str){
    File file = new File("./"+str);
    if(file.exists() && file.length() !=0)
        return true;
    return false;
    }
    
    
    public void readTheFile()throws IOException{
        if(doesTheFileExists("info.txt")){
            BufferedReader br = new BufferedReader(new FileReader("./info.txt"));
            int numberOfPapers = Integer.parseInt(br.readLine());
            _listOfPapers = new Paperwork[numberOfPapers];
            for(int i=0;i<numberOfPapers;i++){
                String name = br.readLine();
                String path = br.readLine();
                int numberOfBibs = Integer.parseInt(br.readLine());
                _listOfPapers[i] = new Paperwork(name,path,numberOfBibs);
                for(int a = 0; a<numberOfBibs; a++)
                    _listOfPapers[i].setThebibFile(br.readLine(),a);
            }
        }
        else
            initTheFile();
    
    }
    
    
    
    
    public void WriteTheFile(){
        if(getTheListlength() > 0){
        File file = new File("./info.txt");
        file.delete();
    try {
        //file.createNewFile();
        BufferedWriter out = new BufferedWriter(new FileWriter(file));
        out.write(_listOfPapers.length+"\n");
        for(int p=0;p<_listOfPapers.length;p++)
            writePaperStructure(_listOfPapers[p], out);
        out.close();
        } 
    catch (IOException e) {
        System.out.println("There has been a problem with writing to a info.txt document. ");
        }
    }
        else{
            File f = new File("./info.txt");
            f.delete();
        }
            
    }
    
    
    private void writePaperStructure(Paperwork paper,BufferedWriter out)throws IOException{
        if(paper != null && paper.getTheName()!=""){
            out.write(paper.getTheName()+"\n");
            out.write(paper.getThePath()+"\n"); 
            out.write(paper.getBibListLenght()+"\n");
            for(int i = 0 ; i < paper.getBibListLenght(); i++)
                if(paper.getThebibFile(i)!="")
                    out.write(paper.getThebibFile(i)+"\n");
        }
    }
    
    
    private void initTheFile(){
        try{
        File f = new File ("./info.txt");
        f.createNewFile();
        }
        catch(IOException e){
            System.out.println(e);
        }
    }
    
    public boolean isTheNamePaperworkTaken(String name){
        for(int p=0;p<_listOfPapers.length;p++){
            if(_listOfPapers[p].getTheName() == name)
                return true;
        }
        return false;
    }
    
    public boolean isTheNameBibFileTaken(int index,String name){
        return _listOfPapers[index].isTheBibFileTaken(name);
    }

   public Paperwork[] getThePaperworksList() {
     return _listOfPapers;
    }
   
   public void setThePaperList(Paperwork[] list){
       _listOfPapers = list;
   }
    
   private int getTheListlength(){
       if(_listOfPapers == null)
           return 0;
       return _listOfPapers.length;
   } 
    
}
