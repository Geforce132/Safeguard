package net.geforcemods.safeguard.downloader;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.logging.Level;

import org.apache.commons.io.FileUtils;

import net.geforcemods.safeguard.Safeguard;
import net.geforcemods.safeguard.lists.DependenciesList;
import net.geforcemods.safeguard.wrappers.CFFile;

public class Downloader {
	
	public static ArrayList<DependenciesList> downloadedDependencies = new ArrayList<DependenciesList>();
	
	public static void downloadMod(CFFile modToDownload, String path) throws IOException{
		Safeguard.LOGGER.log(Level.INFO, "Attempting to download " + modToDownload.name + "...");

		BufferedInputStream in = null;
		FileOutputStream out = null;
		
		String fileName = modToDownload.name;
		
		try{
			URL website = new URL(modToDownload.url + "/file");
            
            File fileDirectory = new File(path);
            File fileToCreate = new File(fileDirectory, fileName);

		    if(!fileDirectory.exists()){
			    fileDirectory.mkdir();	
		    }
		
		    if(!fileToCreate.exists()){
			    fileToCreate.createNewFile();
			    fileToCreate.setWritable(true);

			    FileUtils.copyURLToFile(website, fileToCreate);
			    Safeguard.LOGGER.log(Level.INFO, "Downloaded " + modToDownload.name);
		    }
		    else {
		    	Safeguard.LOGGER.log(Level.INFO, "A file named " + modToDownload.name + " already exists, skipping...");
		    }
		}
		catch(Exception e) {
			e.printStackTrace();
		}
		finally {
			if(in != null){
				in.close();
			}
			
			if(out != null){
				out.close();
			}
		}
	}

	public static boolean isOnline() {
		try { 
            URL url = new URL("https://www.google.com/"); 
            URLConnection connection = url.openConnection(); 
            connection.connect(); 

            return true;
        } 
        catch (Exception e) { 
            return false;
        } 
	}
}
