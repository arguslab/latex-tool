/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package latex;

/**
 *
 * @author Dwight
 */
public class Paperwork {
    private String _name;
    private String _pathToRealDirectory;
    private String[] _bibFiles;
    
    public Paperwork(String name,String path,int size){
       this._name = name;
       this._pathToRealDirectory = path;
       _bibFiles = new String[size];
       
    }

    
    public String getTheName(){
        return _name;
    }
    
    public String getThePath(){
        return _pathToRealDirectory;
    }
    
    public String getThebibFile(int index){
        return _bibFiles[index];
    }
    
    public void enlargeBibFileList(String name){
        String[] newBibList = new String[getBibListLenght()+1];
        int i;
        for(i=0;i<getBibListLenght();i++)
            newBibList[i]=_bibFiles[i];
        newBibList[i]=name;
        _bibFiles = newBibList;
    }
    
    public void decreaseBibFileList(String name){
       if(getBibListLenght() <= 1)
           _bibFiles = null;
       else{
       String[] newList = new String[_bibFiles.length-1];
       int a = 0;
       for(int i=0;i<_bibFiles.length;i++)
        if(!_bibFiles[i].equals(name))
            newList[a++]=_bibFiles[i];
        _bibFiles = newList;
        }
    }
    
    public int getBibListLenght(){
        if(_bibFiles == null)
            return 0;
        return _bibFiles.length;
    }
    
    public String[] getTheBibFiles(){
        return _bibFiles;
    }
    
    public void setThebibFile(String name,int index){
        _bibFiles[index]=name;
    }
    
    public boolean isTheBibFileTaken(String name){
        if(_bibFiles != null){
        for (int b=0;b<_bibFiles.length;b++){
            if(_bibFiles[b].equals(name))
                return true;
        }}
        return false;
    }
    
    
    
}
