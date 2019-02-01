package net.geforcemods.safeguard.downloader;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;

import org.apache.commons.io.FileUtils;

import net.geforcemods.safeguard.wrappers.CFFile;
import net.geforcemods.safeguard.wrappers.CFMod;

public class Downloader {
	
	public static HashMap<String, CFMod> downloadedDependencies = new HashMap<String, CFMod>();
	
	public static void downloadMod(CFFile modToDownload, String path) throws IOException{
		BufferedInputStream in = null;
		FileOutputStream out = null;
		
		String fileName = modToDownload.name;
		
		try{
			URL website = new URL(modToDownload.url + "/file");
			HttpURLConnection conn = (HttpURLConnection) website.openConnection();
			conn.connect();
            
            File fileDirectory = new File(path);
            File fileToCreate = new File(fileDirectory, fileName);

		    if(!fileDirectory.exists()){
			    fileDirectory.mkdir();	
		    }
		
		    if(!fileToCreate.exists()){
			    fileToCreate.createNewFile();
			    fileToCreate.setWritable(true);
		    }
		    
		    FileUtils.copyURLToFile(website, fileToCreate);
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

}
