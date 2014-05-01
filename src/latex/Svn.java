/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package latex;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;
import static javax.swing.WindowConstants.DISPOSE_ON_CLOSE;
import latexGlobalGraphics.Collis;
import latexGlobalGraphics.Output;
import latexGraphicsForSVN.SVNMenu;

/**
 *
 * @author Dwight
 *
 * Description of SVN protocol:
 * 1) CHECKOUT from repository to local directory, That creates working copy that works as a workspace.
 * 2) User editing part: changing or deleting it's contents
 * 3) Return the file to it's repository COMMIT
 * 4) If more people are using the same file they can use UPDATE function to work with the newest version of given file
 */
public class Svn {
    
    private String _defaultUrlForSvnServer;
    private String _defaultDirectoryForBibs;
    private String _defaultDirectoryForPaperwork;
    private String _nameOfServer;
    private String _defaultEditor;
    private String[] _listOfAllBibFiles;
    private SvnFileReader _svnFR;
    private SVNMenu _svnGraphics;
    private Paperwork[] _paperworks;
    private ArrayList<String> _localStorage;
    private Output _localOutput;
    
    private String _locPaperN;
    private String _locBibN;
    public boolean contflag=true;
    private String _pathOfLastUpdate;
    
    public Svn(SVNMenu svnGraph,String name,String url,int out,String editor){
        _svnGraphics = svnGraph;
        _defaultUrlForSvnServer = url;
        _defaultDirectoryForBibs = "./LatexTool";
        _defaultDirectoryForPaperwork = "./Papers";
        _nameOfServer = name;
        _defaultEditor = editor;
        CheckExistenceOfLocalRep();
        CreateOrUpdateBibs();
        if(out==1)
            InitOutput();
        // Load Paperworks from info.txt file    
        _svnFR = new SvnFileReader();
        _paperworks = _svnFR.getThePaperworksList();
        detectCollisions();
        
    }
    
    // ------------------------ MAIN BUTTON FUNCTIONS --------------------------
    public void AddBib(int version,String name,String message){
        _svnGraphics.createLoadingBar("Adding bib FILE");
        String paperName = _svnGraphics.getPaperName();
        if(!paperName.equals("None")){
        if(paperName.equals("ALL")){
            for (Paperwork p : _paperworks)
                if(p != null)
                    AddBib(version,name,p.getTheName(),message);
        }
        else
           AddBib(version,name,paperName,message);
        }
        _svnGraphics.closeLoadingBar();
    }
    private void AddBib(int version,String name,String paperName,String message){
        //Version :  0 - create a new bib file      1 - add just a refference
        String pathToPaper = GetPathForGivenPaper(paperName);
        String pathToBib="" ;
        boolean change = false;
        if(version == 0){
            if(!doesPaperHasThisBibFile(paperName, name)){
                change = true;
                pathToBib = pathToPaper +"/"+ name + ".bib";
                CreateFile(pathToBib);
                
        }
            else
                JOptionPane.showConfirmDialog(null,"Error(Addbib): Bib file "+name+" already exists in Paperwork: " + paperName,"Error", JOptionPane.PLAIN_MESSAGE);
        }
        
        else{
            pathToBib = name;
            String bibNameWithSuffix = SubstracNameFromPath(name);
            if(bibNameWithSuffix.endsWith("bib")){
            String bibName = bibNameWithSuffix.substring(0, bibNameWithSuffix.length()-4);
            if(!doesPaperHasThisBibFile(paperName, bibName)){
                change = true;
                File from = new File(pathToBib);
                File to = new File(pathToPaper+"/"+bibNameWithSuffix);
                CopyFile(from, to);
                name = bibName;
            }
            else
                JOptionPane.showConfirmDialog(null,"Error(Addbib): Bib file "+bibName+" already exists in Paperwork: " + paperName,"Error", JOptionPane.PLAIN_MESSAGE);
            }
            else
              JOptionPane.showConfirmDialog(null,"Error(Addbib): Given path does not lead to a ****.bib file.","Error", JOptionPane.PLAIN_MESSAGE);  
               
        }

        // adding to the list
        if(change){
            File bibPath = new File(pathToBib);
            // -------------------------- SVN COMMAND FOR ADDING FILE -------------
            CheckIfMasterContainsBib(bibPath,message); // THIS IS THE SVN PART
            String whereUp = _defaultDirectoryForPaperwork+"/"+paperName+"/"+name+".bib";
            ExecuteUpdate(false, whereUp);
            
            
            Paperwork temp = GetPaperworkForgivenPaper(paperName);
            temp.enlargeBibFileList(name);
        
        // still have to create the link to repository !!
            _svnFR.setThePaperList(_paperworks);
            _svnFR.WriteTheFile();
            
        // make the changes on the svn server
             // ExecuteAdd(pathToBibAtLocRepository, message);
        }
        }
    public void DeleteBibFile(){
        String paperName =_svnGraphics.getPaperName();
        String bibName = _svnGraphics.getBibName();
        if(!paperName.equals("None") && !bibName.equals("None")){
            if(paperName.equals("ALL")){
                if(bibName.equals("ALL")){
                    for(Paperwork p : _paperworks){
                     for(String s : p.getTheBibFiles()){
                        DeleteBibFile(p.getTheName(), s);
                    }
                }
            }
            else{
                for(Paperwork p : _paperworks)
                    DeleteBibFile(p.getTheName(), bibName);
            }
        
        }
        else{
            if(bibName.equals("ALL")){
                Paperwork tempP = GetPaperworkForgivenPaper(paperName);
                for(String s : tempP.getTheBibFiles())
                        DeleteBibFile(paperName, s);
            }
            else
                DeleteBibFile(paperName, bibName);
                
            }
    }
    }
    private void DeleteBibFile(String paperName,String bibName){
        //decision 0"YES - Delete contents  ;    1"NO" - DO NOT DELETE
        if(doesPaperHasThisBibFile(paperName,bibName)){
            Paperwork tempP = GetPaperworkForgivenPaper(paperName);
            tempP.decreaseBibFileList(bibName);
            
            String path = _defaultDirectoryForPaperwork + "/" + paperName + "/"+bibName + ".bib";
            DeleteDirOrFile(path);
            
            _svnFR.setThePaperList(_paperworks);
            _svnFR.WriteTheFile();
        }
    }
    public void AddPaper(String path){
        _svnGraphics.createLoadingBar("Adding Paperwork");
       // 0 - file      1 - refference
       String paperName = SubstracNameFromPath(path);
       if(DoesPaperworkExist(paperName))
        JOptionPane.showConfirmDialog(null,"ERROR(AddPaper):Paperwork with the same name is already being used.","Error", JOptionPane.PLAIN_MESSAGE);
       
       else{
           
           File from = new File(path);
           String to = _defaultDirectoryForPaperwork+"/"+paperName;
           
        if(from.exists()){
            CreateDirectory(to);
            ExecuteCheckout(to); 
           _localStorage = new ArrayList<>();
           SearchDirectory(from);    
           Paperwork tempNew = new Paperwork(paperName,path,0);
           EnlargePaperList(tempNew);
           
           // Check this part
           boolean isNewBib;
           for(String str : _localStorage){
            tempNew.enlargeBibFileList(str);
            File fromBib = new File(path+"/"+str+".bib");
            isNewBib = CheckIfMasterContainsBib(fromBib, "Adding a new bib file");
            String whereUp = to+"/"+str+".bib";
            ExecuteUpdate(false, whereUp);
            File locRep = new File(whereUp);
            WaitGivenTime(100);
            if (!isNewBib)
                CopyFile(locRep,fromBib);
           }
           
           _svnFR.setThePaperList(_paperworks);
           _svnFR.WriteTheFile();
           
           // make the changes on the svn server
             // ExecuteAdd(pathToLocalRepository, message);
           }
         else
             JOptionPane.showConfirmDialog(null,"ERROR(AddPaper):There is no paperwork at given path.","Error", JOptionPane.PLAIN_MESSAGE);  
       
        }
       _svnGraphics.closeLoadingBar();
    }
    public void DeletePaperwork(){
        String paperName =_svnGraphics.getPaperName();
        if(!paperName.equals("None")){
            if(paperName.equals("ALL")){
                for(Paperwork p : _paperworks)
                    DeletePaperwork( p.getTheName());
            }
            else
                DeletePaperwork(paperName);
        }
    }
    private void DeletePaperwork(String paperName){
        //decision 0"YES - Delete contents  ;    1"NO" - DO NOT DELETE
        Paperwork paper = GetPaperworkForgivenPaper(paperName);
        // Delete paper
        String path = _defaultDirectoryForPaperwork+"/"+paperName;
        File f = new File(path);
        DeleteDir(f);
        
        path = paper.getThePath();
        
        DecreasePaperList(paper);
        _svnFR.setThePaperList(_paperworks);
        _svnFR.WriteTheFile();
    }
    
// ------------------------------------------------------------------------------------------------------------------------------
// ------------------------------------------------------------------------------------------------------------------------------
// ------------------------------------------------------------------------------------------------------------------------------
    
    
    public void Synchronize(boolean option){
        String paperName =_svnGraphics.getPaperName();
        String bibName = _svnGraphics.getBibName();
        
        
        
        if(!paperName.equals("None") && !bibName.equals("None")){
            if(paperName.equals("ALL")){
                if(bibName.equals("ALL")){
                    for(Paperwork paper : _paperworks){                         // PAPER : ALL         BIB : ALL
                        for(String bib : paper.getTheBibFiles()){
                            Synchronize(paper,bib,option);
                    
                        }
                    }
                }
                else{                                                           // PAPER : ALL         BIB : Concrete
                    for(Paperwork paper : _paperworks)
                        if(paper.isTheBibFileTaken(bibName))
                            Synchronize(paper,bibName,option);
                }
            }
            else{
                Paperwork tempPaper = GetPaperworkForgivenPaper(paperName);
                if(bibName.equals("ALL")){                                      // PAPER : Concrete         BIB : ALL
                    for(String bib : tempPaper.getTheBibFiles())
                        Synchronize(tempPaper,bib,option);
                }
                else                                                            // PAPER : Concrete         BIB : Concrete
                    Synchronize(tempPaper,bibName,option);
            }
        }
        else
            JOptionPane.showConfirmDialog(null,"ERROR(SVNCheckout):Can not work with 'None' parametr.","Error", JOptionPane.PLAIN_MESSAGE);
    }
    public void Synchronize(Paperwork paper,String bibName,boolean option){
        // option says if I just sync file up to SVN SERVER : TRUE or if I am done with sync part and I want to distrubute the newest versioon to files :FALSE
        String paperName = paper.getTheName();
        String nameWithSuffix = bibName+".bib";
        _locPaperN = paperName;
        _locBibN = nameWithSuffix;
        String pathFrom="";
        String pathTo="";
        //System.out.println("paper: "+paperName+"   bibFile: "+nameWithSuffix);
      if(option){
        pathFrom = paper.getThePath() +"/"+nameWithSuffix;
        pathTo = _defaultDirectoryForPaperwork+"/"+paperName+"/"+nameWithSuffix;
        File from = new File(pathFrom);
        File to = new File(pathTo); 
        // 1)
        CopyFile(from, to);
        // 2)
        _pathOfLastUpdate = pathTo;
        ExecuteUpdate(true, pathTo);
        WaitGivenTime(100);
        String message = "sync File "+bibName;
        ExecuteCommit(message, pathTo, true);
        WaitGivenTime(100);
        // 4)
        //CopyFile(to, from);
        // 5)
        ExecuteUpdate(false, null);
          WaitGivenTime(100);
        }
      else{
          WaitGivenTime(100);
          String from = _defaultDirectoryForBibs+"/"+nameWithSuffix;
          String toFolder = paper.getThePath() +"/"+nameWithSuffix; 
          String toLocRep = _defaultDirectoryForPaperwork+"/"+paperName+"/"+nameWithSuffix;
          File fromF = new File(from);
          File toFolderF = new File(toFolder);
          File toLocRepF = new File(toLocRep);
          // COPY
          WaitGivenTime(50);
          CopyFile(fromF, toFolderF);
          WaitGivenTime(50);
          CopyFile(fromF, toLocRepF);
        }
      
    }

   // ------------------ END OF MAIN BUTTON FUNCTIONS -------------------------- 
    
    
    
   // ------------------------ SVN FUNCTIONS -----------------------------------
  
    private void ExecuteAdd(String name,String message){
        String addCommand = "svn add";
        try{
            // ---- ADD PART ----
            Runtime rt = Runtime.getRuntime();
            Process p;
            String command =addCommand+" "+_defaultDirectoryForBibs+"/"+name;
            p = rt.exec(command);
            ReadAndPrintOutputOfSVNCommand(p,true);
           // WaitGivenTime(100);
            ExecuteCommit(message,name,false);
        }
        catch (IOException e){
            JOptionPane.showConfirmDialog(null,"ERROR(ExecuteAdd):There has been a problem with svn add.","Error", JOptionPane.PLAIN_MESSAGE);
        }
        }
    
    private void ExecuteCommit(String message,String name,boolean option){
        try{
           String command = "svn commit ";
           if(option)
               command += name;
           else
           command+=_defaultDirectoryForBibs+"/"+name;
           
           
           if(message != null){ 
               String[] messWithout = message.split(" ");
            message ="";
            
            for(int i=0;i<messWithout.length;i++){
                message +=messWithout[i]+".";
           }
                //command +=" -m \"" +message+ "\"";
               command+=" -m "+(char)34 +message+(char)34;
           }
           else
               command +=" -m \"" +""+ "\"";
           Process p = Runtime.getRuntime().exec(command);
           ReadAndPrintOutputOfSVNCommand(p,true); 
        }
        catch(IOException e){
            JOptionPane.showConfirmDialog(null,"ERROR(ExecuteCommit):There has been a problem with svn commit.","Error", JOptionPane.PLAIN_MESSAGE);
            }
    }
    
    public void ExecuteUpdate(boolean option,String where){
        String command = "svn update ";
        if(where == null)
        command+= _defaultDirectoryForBibs;     
        else
            command+=where;
        try{
            // ---- UPDATE PART ----
            Runtime rt = Runtime.getRuntime();
            Process p;
            p = rt.exec(command);
            if(option)
                ReadAndPrintOutputOfSVNCommand(rt,p,option);
        }
        catch (IOException e){
            JOptionPane.showConfirmDialog(null,"ERROR(ExecuteUpdate):There has been a problem with svn update.","Error", JOptionPane.PLAIN_MESSAGE);
        }
        catch (InterruptedException e){
            System.out.println("problem with solving the collision");
        }
        }
    
    private void ExecuteCheckout(String where){
        
        String command= "svn checkout "+_defaultUrlForSvnServer;
        
        if (where != null)
            command += " "+where+" --depth empty";
            
        try{
            Runtime rt = Runtime.getRuntime();
            Process p;
            p = rt.exec(command);
            ReadAndPrintOutputOfSVNCommand(p,true);
        }
        catch (IOException e){
            JOptionPane.showConfirmDialog(null,"ERROR(ExecuteCheckout):There has been a problem with svn checkout.","Error", JOptionPane.PLAIN_MESSAGE);
        }
        }
    
    private synchronized void ReadAndPrintOutputOfSVNCommand(Runtime rt,Process p,boolean option) throws IOException, InterruptedException{
        String line = "";
        String message = "";
        WaitGivenTime(500);
        BufferedReader buf = new BufferedReader( new InputStreamReader(p.getInputStream()));
        BufferedReader in = new BufferedReader(new InputStreamReader(p.getErrorStream()));
        if(in.read()>0){
            Collis c = new Collis(_svnGraphics,true);
            c.setAlwaysOnTop(true);
            c.handleThisCollision(p, this,_locPaperN,_locBibN);
            c.setVisible(true);
            if(contflag)
                ExecuteUpdate(true, _pathOfLastUpdate);
        }
        
        if(option && !message.equals("") && _localOutput!=null){
            _localOutput.AddText(message);
            _localOutput.setVisible(false);
            _localOutput.setVisible(true);
        }
    }
    
    private void ReadAndPrintOutputOfSVNCommand(Process p,boolean option)throws IOException{
        String message = "";
        BufferedReader buf = new BufferedReader( new InputStreamReader(p.getInputStream()));
        String s;
        while((s=buf.readLine())!= null)
            message+="\n"+s;
        if(option && !message.equals("") && _localOutput!=null){
            _localOutput.AddText(message);
            _localOutput.setVisible(false);
            _localOutput.setVisible(true);
        }
    }
   // ----------------------- END OF SVN FUNCTIONS -----------------------------
    
    
    
    
    
   // ------------------------- SIDE FUNCTIONS ---------------------------------
    
    private void CreateBibFileAtLocRep(String bibName, String paperName){
        String path = _defaultDirectoryForPaperwork+"/"+paperName+"/"+bibName+".bib";
        File f = new File(path);
        CreateFile(path);
    }
    private void CheckExistenceOfLocalRep(){
        File f = new File(_defaultDirectoryForPaperwork);
        if(!f.exists()){
            CreateDirectory(_defaultDirectoryForPaperwork);
        } 
    }
    private void InitOutput(){
        _localOutput = new Output();
        int width = _svnGraphics.getWidth();
        int height = _svnGraphics.getHeight();
        int x =_svnGraphics.getX();
        int y =_svnGraphics.getY();
        _localOutput.setSize(width, height);
        _localOutput.setResizable(false);
        _localOutput.setLocation(x+width, y);
        _localOutput.setVisible(true);
        _localOutput.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
    }
    public void TerminateOutput(){
    if(_localOutput != null){
        _localOutput.dispose();
        _localOutput = null;
    }}
    
    private boolean CheckIfMasterContainsBib(File bibPath,String message){
        File master = new File(_defaultDirectoryForBibs);
        String[] list = master.list();
        String name = bibPath.getName();
        boolean isNewBib = true;
        if(list != null){
        for (String s : list)
            if(s.equals(name)){
                isNewBib = false;
                break;
            }
        if(isNewBib){
            master = new File(_defaultDirectoryForBibs+"/"+name);
                CopyFile(bibPath, master);
            ExecuteAdd(name, message);
        
        }}
        return isNewBib;    
    }
    public String[] GetListOfMasterBibs(){
        CreateOrUpdateBibs();
        return _listOfAllBibFiles;
    }
    public String GetPathForBibsInMasterDir(){
            return _defaultDirectoryForBibs;
}
    public void CreateOrUpdateBibs(){
        File f = new File(_defaultDirectoryForBibs);
        if(f.exists())
            ExecuteUpdate(true,null);
        else
            ExecuteCheckout(null);
        FilenameFilter filter = new FilenameFilter() {

            @Override
            public boolean accept(File dir, String name) {
                if(name.endsWith(".bib"))
                    return true;
                return false;
            }
        };
        _listOfAllBibFiles = f.list(filter);
    }
    public String[] getListOfAllBibFiles(){
        String [] list=null;
        ArrayList<String> array =  new ArrayList<>();
        if(_paperworks != null){
        for(Paperwork p : _paperworks){
            if(p.getTheBibFiles() != null){
            for(String sBIB : p.getTheBibFiles())
              if(!array.contains(sBIB))  
                array.add(sBIB);
            }
        }   
            
            
        list = new String[array.size()];
        int i=0;
        for(String s : array)
            list[i++]=s;
        }
        return list;
    
    }
    private boolean doesPaperHasThisBibFile(String paperName,String bibName){
        Paperwork p = GetPaperworkForgivenPaper(paperName);
        return p.isTheBibFileTaken(bibName);
    
    }
    private void CopyFile(File sourceLocation , File targetLocation) {
        String pathFrom="";
        String pathTo="";
        try{
        if(!sourceLocation.isDirectory()){
            pathTo =   targetLocation.getCanonicalPath();
            if(targetLocation.exists()){
                DeleteDirOrFile(pathTo);
                WaitGivenTime(100);
            }
            targetLocation.createNewFile();
            // Now copy the file
            pathFrom = sourceLocation.getCanonicalPath();
            
            
            InputStream in = new FileInputStream(sourceLocation);
            OutputStream out = new FileOutputStream(targetLocation);
    
        // Copy the bits from instream to outstream
            byte[] buf = new byte[1024];
            int len;
            while ((len = in.read(buf)) > 0) {
                out.write(buf, 0, len);
            }
            in.close();
            out.close();
        }
        }
         catch (IOException ex) {
             JOptionPane.showConfirmDialog(null,"ERROR(CopyFile):There has been an error while copying files."
                     + "\n FROM: "+pathFrom+""
                     + "\n TO: "+pathTo,"Error", JOptionPane.PLAIN_MESSAGE);
         }
        
    }
    private void CopyDirectory(File sourceLocation , File targetLocation){
    String pathFrom = "";
    String pathTo = "";
    if (sourceLocation.isDirectory()) {
        if (!targetLocation.exists()) {
            targetLocation.mkdir();
        }

        String[] children = sourceLocation.list();
        for (int i=0; i<children.length; i++) {
            CopyDirectory(new File(sourceLocation+"/"+children[i]),new File(targetLocation+"/"+children[i]));
        }
    } 
    else { // CONTROL THIS FIRST !
        String bibNameWithSuffix = sourceLocation.getName();
        boolean isBib = bibNameWithSuffix.endsWith("bib");
        String path = targetLocation.getPath();
        if(isBib){
            String bibName = bibNameWithSuffix.substring(0, bibNameWithSuffix.length()-4);
            _localStorage.add(bibName);
            String pathForDirectory = path.substring(0,path.length()-4);
            String p = pathForDirectory+bibNameWithSuffix;
            File destination = new  File(pathForDirectory+bibNameWithSuffix);
        // Now copy the file
            try{
            InputStream in = new FileInputStream(sourceLocation);
            OutputStream out = new FileOutputStream(destination);
            pathFrom = sourceLocation.getCanonicalPath();
            pathTo = destination.getCanonicalPath();
        // Copy the bits from instream to outstream
            byte[] buf = new byte[1024];
            int len;
            while ((len = in.read(buf)) > 0) {
                out.write(buf, 0, len);
            }
            in.close();
            out.close();
            }
            catch (IOException ex)
            {
                JOptionPane.showConfirmDialog(null,"ERROR(CopyDirectory):There has been an error while copying directory."
                     + "\n FROM: "+pathFrom+""
                     + "\n TO: "+pathTo,"Error", JOptionPane.PLAIN_MESSAGE);
            }
        }
    }
    }
    private void SearchDirectory(File sourceLocation){
    if (sourceLocation.isDirectory()) {

        String[] children = sourceLocation.list();
        for (int i=0; i<children.length; i++) {
            SearchDirectory(new File(sourceLocation+"/"+children[i]));
        }
    } 
    else { // CONTROL THIS FIRST !
        String bibNameWithSuffix = sourceLocation.getName();
        boolean isBib = bibNameWithSuffix.endsWith("bib");
        if(isBib){
             String bibName = bibNameWithSuffix.substring(0, bibNameWithSuffix.length()-4);
            _localStorage.add(bibName);
        }
    }   
    }
    private int GetPaperListLenght(){
        if(_paperworks == null)
            return 0;
        return _paperworks.length;
    }
    private void EnlargePaperList(Paperwork newItem){
        Paperwork[] tempField = new Paperwork[GetPaperListLenght()+1];
        int i;
        for(i=0;i<GetPaperListLenght();i++)
            tempField[i]=_paperworks[i];
        tempField[i]=newItem;
        _paperworks=tempField;
    }
    private void DecreasePaperList(Paperwork deleteItem){
        if(_paperworks.length == 1)
            _paperworks = null;
            
        else{
        Paperwork[] tempField = new Paperwork[_paperworks.length-1];
        int a=0;
        for(int i=0;i<_paperworks.length;i++)
            if(_paperworks[i] != deleteItem)
                tempField[a++] = _paperworks[i];
        _paperworks = tempField;
        }
    }
    private String SubstracNameFromPath(String path){
        String[] field = path.split("/");
        return field[field.length-1];    
    }
    private boolean DoesPaperworkExist(String name){
        if(_paperworks != null){
        for(Paperwork paper : _paperworks){
            String t = paper.getTheName();
        if(paper.getTheName().equals(name))
            return true;
        }
        }
        return false;
    }
    private Paperwork GetPaperworkForgivenPaper(String name){
        for(int i=0;i<_paperworks.length;i++)
            if(_paperworks[i].getTheName().equals(name))
                return _paperworks[i];
        return null;
    }
    private String GetPathForGivenPaper(String name){
        for(int i=0;i<_paperworks.length;i++){
            if(_paperworks[i].getTheName().equals(name))
                return _paperworks[i].getThePath();
        }
        return null;
    }
    public String[] GetBibFileForGivenPaper(String name){
        for(int i=0;i<_paperworks.length;i++){
            if(_paperworks[i].getTheName().equals(name))
                return _paperworks[i].getTheBibFiles();
        }
        return null;
    }
    public void SetSVNGraphic (SVNMenu svn){
        _svnGraphics = svn;
    }
    public void CreateDirectory(String path){
        File temp = new File(path);
        if(temp.isDirectory() == false)
        if(!temp.mkdir())
            JOptionPane.showConfirmDialog(null, "ERROR(CreateDirectory):There has been a problem with creating the directory for the new paperwork.","Error",JOptionPane.PLAIN_MESSAGE);
    }
    public void DeleteDirOrFile(String path){
        File temp = new File(path);
        if(temp.isFile())
            temp.delete();
        else
            DeleteDir(temp);
    }
    public boolean DeleteDir(File dir) {
    if (dir.isDirectory()) {
      String[] children = dir.list();
      for (int i = 0; i < children.length; i++) {
        boolean success = DeleteDir(new File(dir, children[i]));
        if (!success) {
          return false;
        }
      }
    }
    return dir.delete();
  } 
    public void CreateFile(String path){
        File temp = new File(path);
        try{
        temp.createNewFile();
        }
        catch(IOException e){
            JOptionPane.showConfirmDialog(_svnGraphics,"ERROR(CreateFile):There has been a problem with creating the bib file.","Error", JOptionPane.PLAIN_MESSAGE);   
        }
    }
    private void detectCollisions(){
        if(_paperworks != null)
        for (Paperwork paper : _paperworks)
            if(!testExistenceOfDirs(paper))
                for (String bibFile : paper.getTheBibFiles())
                    testExistenceOfbibFile(paper, bibFile);
    }
    private void testExistenceOfbibFile(Paperwork paper, String bibFile){
        boolean testOne;
        String paperName = paper.getTheName();
        String pathOne = paper.getThePath()+"/"+bibFile+".bib";
        File f = new File(pathOne);
        testOne = !f.exists();
        
        if(testOne){
            int a = JOptionPane.showConfirmDialog(null,"ERROR: There has been an collision while controlling the existence of Paperworks."
                    + "\n The program has detected a missing file at this path."
                    + "\n"+pathOne+"\n\n"
                    + "There are two possible ways to solve this problem."
                    + "\n1.(YES) This option terminates the program and let u to fix the problem yourself. "
                    + "\n2.(NO) This option tries to solve the problem with the restore function."
                    + "\n3.(CANCEL) This option starts the program, but not on your own risk."
                    + "","Error", JOptionPane.YES_NO_CANCEL_OPTION);
            // NO = 1, Yes = 0
            if(a == 0 ){
               System.exit(1); // 1 indicates abnormal exit
            }
            else if(a == 1){
                _svnGraphics.setBibName(bibFile);
                _svnGraphics.setPaperName(paperName);
                Synchronize(false);
            }
        }        
    
    
    
    }
    private boolean testExistenceOfDirs(Paperwork  paper){
        boolean statement = false;
        boolean testOne;
        String pathOne = paper.getThePath();
        File f = new File(pathOne);
        testOne = !f.exists(); 
        
        if(testOne){
            int a = JOptionPane.showConfirmDialog(null,"ERROR: There has been an collision while controlling the existence of Paperworks."
                    + "\n The program has detected a missing file at this path."
                    + "\n"+pathOne+"\n\n"
                    + "There are three possible ways to solve this problem."
                    + "\n1.(YES) This option terminates the program and let u to fix the problem yourself. "
                    + "\n2.(NO) This option tries to solve the problem with the restore function."
                    + "\n3.(CANCEL) This option starts the program, but not on your own risk."
                    + "","Error", JOptionPane.YES_NO_CANCEL_OPTION);
            // NO = 1, Yes = 0
            if(a == 0 ){
               System.exit(1); // 1 indicates abnormal exit
            }
            else if(a == 1){
                String paperName = paper.getTheName();
                _svnGraphics.setBibName("ALL");
                _svnGraphics.setPaperName(paperName);
                Synchronize(false);
            }
        }
        return statement;
    }
    public void WaitGivenTime(int time){
        try {
            Thread.sleep((int)time);
        } catch (InterruptedException ex) {
            Logger.getLogger(Svn.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        
    }
    
    public boolean getContFlag(){
        return contflag;
    }
    
    public void setContFlag(boolean flag){
        contflag = flag;
    }
    // ------------------------ END OF SIDE FUNCTIONS ---------------------------
    // --- GETTERS
    public int GetIsSVN(){
        return 1;
    }
    public String GetNameOfTheServer(){
        return _nameOfServer;
    }
    public String GetServersUrl(){
        return _defaultUrlForSvnServer;
    }
    public int GetOuputSetting(){
        
        if(_localOutput == null)
            return 0;
        else
            TerminateOutput();
        return 1;
    }

    public void resetPapers() {
        String info = "./info.txt";
        DeleteDirOrFile(info);
        DeleteDirOrFile(_defaultDirectoryForPaperwork);
        CheckExistenceOfLocalRep();
    }

   
    
}
