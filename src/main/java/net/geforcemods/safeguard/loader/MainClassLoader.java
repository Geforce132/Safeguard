package net.geforcemods.safeguard.loader;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.annotation.Annotation;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Scanner;

import org.apache.commons.lang3.StringUtils;

import com.google.gson.Gson;

import net.geforcemods.safeguard.Safeguard;
import net.geforcemods.safeguard.downloader.Downloader;
import net.geforcemods.safeguard.wrappers.CFMod;
import net.geforcemods.safeguard.wrappers.ModInfo;
import net.minecraftforge.fml.common.versioning.DependencyParser;
import net.minecraftforge.fml.relauncher.Side;

public class MainClassLoader {
	
	public void loadMainClasses(ArrayList<File> mods) {
		ArrayList<String> modClassMap = getModDependencies();
		
		for(File file : mods) {
			loadClass(file, modClassMap);
		}
	}
	
	private void loadClass(File mod, ArrayList<String> mainClasses) {
	    System.out.println("checking " + mod.getName());

		for(int i = 0; i < mainClasses.size(); i++) {
			try {
			    URL fileURL = mod.toURI().toURL();
			    String jarURL = "jar:" + fileURL + "!/";
			    URL urls [] = { new URL(jarURL) };
			    URLClassLoader clsLoader = URLClassLoader.newInstance(urls);
			    Class<?> cls = clsLoader.loadClass(mainClasses.get(i));
			    System.out.println("Found " + cls.getSimpleName());
			    
			    if(isMod(cls)) {
			    	ModInfo modInfo = getModInfo(cls);
			    	
			    	if(!modInfo.dependencies.isEmpty())
			    		handleDependency(modInfo.modid, modInfo.dependencies);
			    }

			    break;
			} 
			catch (MalformedURLException ex)
			{
			    ex.printStackTrace();
			}
			catch (IOException ex)
			{
				ex.printStackTrace();
			} 
			catch (ClassNotFoundException e) 
			{
				System.out.println(mainClasses.get(i) + " doesn't exist in " + mod.getName() + ", skipping");
			}
		}
	}

	private ArrayList<String> getModDependencies() {
		ArrayList<String> map = new ArrayList<String>();
		
		InputStream inputStream = MainClassLoader.class.getResourceAsStream("/assets/" + Safeguard.MODID + "/main_classes.txt");
		
		Scanner scanner = new Scanner(inputStream);
		
		while(scanner.hasNextLine()) {
			String classPath = scanner.nextLine();
			
			map.add(classPath);
		}
		
		scanner.close();
		return map;
	}
	
	private void handleDependency(String modid, String dependency) throws IOException {
		System.out.println(modid + " needs " + dependency);
		
		DependencyParser dependencyParser = new DependencyParser(modid, Side.CLIENT);
        DependencyParser.DependencyInfo info = dependencyParser.parseDependencies(dependency);

		if(Downloader.downloadedDependencies.containsKey(modid)) return;
		
		System.out.printf("%s\n%s\n", info.dependencies, info.requirements);
		
		URL url = new URL("https://api.cfwidget.com/minecraft/mc-mods/ptrlib?version=" + Safeguard.MCVERSION);
		HttpURLConnection con = (HttpURLConnection) url.openConnection();
		con.setRequestMethod("GET");
		
		CFMod result  = new Gson().fromJson(new InputStreamReader(con.getInputStream()), CFMod.class);

		con.disconnect();
		
		System.out.printf("%s\n%d\n", result.title, result.files.length);

		Downloader.downloadMod(result.download, ModDetector.modDir.getAbsolutePath() + "\\" + Safeguard.MCVERSION);
		Downloader.downloadedDependencies.put(modid, result);
	}
	
	public static boolean isMod(Class<?> clazz) {
		if(getModAnnotationString(clazz) != null)
			return true;
		
		return false;
	}
	
	public static String getModAnnotationString(Class<?> clazz) {
		for(Annotation a : clazz.getDeclaredAnnotations()) {
			if(a.toString().startsWith("@net.minecraftforge.fml.common.Mod("))
				return a.toString();
		}
		
		return null;
	}

	public static ModInfo getModInfo(Class<?> clazz) {
		if(!isMod(clazz)) return null;
		
		String annotation = getModAnnotationString(clazz);
		
		String modid = StringUtils.substringBetween(annotation, "modid=", ")");
		String name = StringUtils.substringBetween(annotation, "name=", ",");
		String version = StringUtils.substringBetween(annotation, "version=", ",");
		String dependencies = StringUtils.substringBetween(annotation, "dependencies=", ", ");

		return new ModInfo(modid, name, version, dependencies);
	}

}
