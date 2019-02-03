package net.geforcemods.safeguard.loader;

import java.io.File;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;

import org.apache.commons.lang3.StringUtils;

import net.geforcemods.safeguard.lists.DependantsList;
import net.geforcemods.safeguard.wrappers.ModInfo;

public class MainClassLoader {
	
	public void loadMainClasses(ArrayList<File> mods) {
		ArrayList<String> modClassMap = getMainClasses();
		
		for(File file : mods) {
			loadClass(file, modClassMap);
		}
	}

	public ArrayList<String> getMainClasses() {
		ArrayList<String> map = new ArrayList<String>();

		for(int i = 0; i < DependantsList.values().length; i++) {
			map.add(DependantsList.values()[i].classPath);
		}

		return map;
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
			    		DependencyHandler.handleDependency(modInfo);
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
			catch (ClassNotFoundException e) {}
		}
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
