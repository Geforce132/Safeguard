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
	
	/**
	 * Load all the classes from {@link DependantsList} and see if any are present
	 * in the /mods/ folder. If so, load the class and check its @Mod dependencies
	 */
	public void loadMainClasses(ArrayList<File> mods) {
		ArrayList<String> modClassMap = getMainClasses();
		
		for(File file : mods) {
			loadClass(file, modClassMap);
		}
	}

	/**
	 * Returns all the mods to check dependencies for
	 */
	public ArrayList<String> getMainClasses() {
		ArrayList<String> map = new ArrayList<String>();

		for(int i = 0; i < DependantsList.values().length; i++) {
			map.add(DependantsList.values()[i].classPath);
		}

		return map;
	}

	/**
	 * Loads a .jar file and checks to see if any of the classes in {@link DependantsList}
	 * are present in it
	 * 
	 * @param mod The file to check
	 * @param mainClasses The classes to check for
	 */
	private void loadClass(File mod, ArrayList<String> mainClasses) {
		for(int i = 0; i < mainClasses.size(); i++) {
			try {
			    URL fileURL = mod.toURI().toURL();
			    String jarURL = "jar:" + fileURL + "!/";
			    URL urls [] = { new URL(jarURL) };
			    URLClassLoader clsLoader = URLClassLoader.newInstance(urls);
			    Class<?> cls = clsLoader.loadClass(mainClasses.get(i));
			    
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

	/**
	 * Returns true if a class file has a @Mod annotation
	 */
	public static boolean isMod(Class<?> clazz) {
		if(getModAnnotationString(clazz) != null)
			return true;
		
		return false;
	}
	
	/**
	 * Gets a class's @Mod information in String form
	 */
	public static String getModAnnotationString(Class<?> clazz) {
		for(Annotation a : clazz.getDeclaredAnnotations()) {
			if(a.toString().startsWith("@net.minecraftforge.fml.common.Mod("))
				return a.toString();
		}
		
		return null;
	}

	/**
	 * Parses the @Mod information from a class and returns it in ModInfo form
	 */
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
