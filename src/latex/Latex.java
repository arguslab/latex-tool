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
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;
import latexGraphicsForGIT.GITMenu;
import latexGlobalGraphics.Properties;
import latexGraphicsForSVN.SVNMenu;

/**
 *
 * @author Dwight
 */
public class Latex {

    private String _defaultProperties = "./settings.txt";
    
    private int _isSvn;
    private String _name;
    private String _Url;
    private int _output;
    private String _editor;
    
    
    private Properties _intrMenu;
    private SVNMenu _svnMenu;
    private GITMenu _gitMenu;
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
       Latex lat = new Latex();
       lat.localStart();
    }
    
    
    public void  changeProperties(){
      _intrMenu = new Properties();
      _intrMenu.setTitle("Create a new server");
      _intrMenu.setResizable(false);
      _intrMenu.setVisible(true);
      _intrMenu.setLocalReferenceOfMainMenu(this);
      _intrMenu.setProperties(_isSvn, _name, _Url, _output,_editor);
      _intrMenu.setLocation(_svnMenu.getLocation());
      _svnMenu.dispose();
      
    
    }
    
    private void localStart(){
       boolean decision=false;
        try{
            decision = ReadTheFile();
        }
        catch(IOException e){
           JOptionPane.showConfirmDialog(null,"Can not create settings.txt file.","Error", JOptionPane.PLAIN_MESSAGE); 
        }
        
        if(decision){
            if(_isSvn == 1)
                handlessSVNActions();
            else
                handlessGITActions();
        
        
        }
        else{
       _intrMenu = new Properties();
       _intrMenu.setTitle("Create a new server");
       _intrMenu.setResizable(false);
       _intrMenu.setVisible(true);
       _intrMenu.setLocalReferenceOfMainMenu(this);
        }
    }
    
    private void handlessSVNActions(){
        _svnMenu = new SVNMenu();
        _svnMenu.setTitle("SVN server synchronizer");
        _svnMenu.setResizable(false);
        _svnMenu.setSVNReference(new Svn(_svnMenu,_name,_Url,_output,_editor));
        _svnMenu.setLATReference(this);
        _svnMenu.setVisible(true);
    
    }
    private void handlessGITActions(){
        _gitMenu = new GITMenu();
        _gitMenu.setVisible(true);
        
    }
    
    // ---------------------------------------------------
    
    public void SetProperties(int svn,int output,String name,String url,String editor){
        _isSvn = svn;
        _output = output;
        _name = name;
        _Url = url;
        _editor = editor;
        
        // ----- run it
       WriteTheFile();
            if(_isSvn == 1)
                handlessSVNActions();
            else
                handlessGITActions();
    }
    
    private boolean isThereAnExistingServer(){
        File f = new File(_defaultProperties);
        if(f.exists()&& f.length() !=0)
            return true;
        return false;
    }
    
    private void InitTheFile(){
        File f = new File(_defaultProperties);
        try {
            f.createNewFile();
        } catch (IOException ex) {
            JOptionPane.showConfirmDialog(null,"Can not create settings.txt file.","Error", JOptionPane.PLAIN_MESSAGE);
        }
    
    }
    
    private boolean ReadTheFile()throws IOException{
        if(isThereAnExistingServer()){
            BufferedReader br = new BufferedReader(new FileReader(_defaultProperties));

            _isSvn = Integer.parseInt(br.readLine());
            _name = br.readLine();
            _Url = br.readLine();
            _output = Integer.parseInt(br.readLine());
            _editor = br.readLine();
            
            return true;
            
        }
        else{
            InitTheFile();
            return false;
        }
        
    
    }
        
    
    private void WriteTheFile(){
       if(!_Url.equals("") && _Url != null){
        File file = new File(_defaultProperties);
        file.delete();
    try {
        //file.createNewFile();
        BufferedWriter out = new BufferedWriter(new FileWriter(file));
        
        out.write(_isSvn+"\n"+_name+"\n"+_Url+"\n"+_output+"\n"+_editor+"\n");
        out.close();
        } 
    catch (IOException e) {
        System.out.println("There has been a problem with writing to a settings.txt document. ");
        }
    }
        else{
            File f = new File(_defaultProperties);
            f.delete();
        }
    }
    
    
    
}
