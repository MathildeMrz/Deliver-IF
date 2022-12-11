package xml;

import java.io.File;
import javafx.stage.FileChooser;

import javax.swing.JFileChooser;
//import javax.swing.JFileChooser;
import javax.swing.filechooser.*;

import javafx.stage.Stage;

public class XMLfileOpener extends FileFilter {// Singleton
	
	private static XMLfileOpener instance = null;
	private XMLfileOpener(){}
	protected static XMLfileOpener getInstance(){
		if (instance == null) instance = new XMLfileOpener();
		return instance;
	}

 	public File open(boolean read, Stage stage) throws ExceptionXML{
 		File file;
 		FileChooser fileChooserXML = new FileChooser();
// 		jFileChooserXML.setLayout();
//        fileChooserXML.setFileFilter(this);
//        fileChooserXML.setFileSelectionMode(FileChooser.);
        if (read)
         	file = fileChooserXML.showOpenDialog(stage);
        else
         	file = fileChooserXML.showSaveDialog(stage);
        //if (file == null) 
        	//throw new ExceptionXML("Problem when opening file");
        return file;
 	}
 	
 	public File open(boolean read) throws ExceptionXML{
 		int returnVal;
 		JFileChooser jFileChooserXML = new JFileChooser();
        jFileChooserXML.setFileFilter(this);
        jFileChooserXML.setFileSelectionMode(JFileChooser.FILES_ONLY);
        if (read)
         	returnVal = jFileChooserXML.showOpenDialog(null);
        else
         	returnVal = jFileChooserXML.showSaveDialog(null);
        if (returnVal != JFileChooser.APPROVE_OPTION) 
        	throw new ExceptionXML("Problem when opening file");
        return new File(jFileChooserXML.getSelectedFile().getAbsolutePath());
 	}
 	
 	@Override
    public boolean accept(File f) {
    	if (f == null) return false;
    	if (f.isDirectory()) return true;
    	String extension = getExtension(f);
    	if (extension == null) return false;
    	return extension.contentEquals("xml");
    }

	@Override
	public String getDescription() {
		return "XML file";
	}

    private String getExtension(File f) {
	    String filename = f.getName();
	    int i = filename.lastIndexOf('.');
	    if (i>0 && i<filename.length()-1) 
	    	return filename.substring(i+1).toLowerCase();
	    return null;
   }
}
