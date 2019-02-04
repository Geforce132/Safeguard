package net.geforcemods.safeguard.loader;

import java.io.File;
import java.lang.reflect.Field;
import java.util.ArrayList;

import org.apache.commons.io.FilenameUtils;

import net.geforcemods.safeguard.Safeguard;
import net.minecraftforge.fml.relauncher.CoreModManager;

public class ModDetector {
	
	public static MainClassLoader classLoader;
	public static File modDir;
	
	/**
	 * Creates a new class loader and detects all of the mods for dependency checking
	 */
	public static void detectMods() {
		if(classLoader == null) classLoader = new MainClassLoader();

		ArrayList<File> mods = getModJars();
		classLoader.loadMainClasses(mods);
	}

	/**
	 * Returns all the files in the /mods/ and /mods/<i>mcversion</i>/ folders
	 * @return
	 */
	private static ArrayList<File> getModJars() {
		ArrayList<File> modJars = new ArrayList<File>();

		try {
			Field field = CoreModManager.class.getDeclaredField("mcDir");
			field.setAccessible(true);
			File dir = (File) field.get(null);
			field.setAccessible(false);
			File modsFolder = new File(dir, "\\mods\\");
			File versionSpecificModsFolder = new File(dir, "\\mods\\" + Safeguard.MCVERSION + "\\");
			modDir = modsFolder;
			
			if(modsFolder.exists()) {
				for(int i = 0; i < modsFolder.listFiles().length; i++) {
					File file = modsFolder.listFiles()[i];
					
					if(file.isDirectory()) continue;
					if(!FilenameUtils.getExtension(file.getName()).equals("jar")) continue;
					
					modJars.add(file);
				}
			}
			
			if(versionSpecificModsFolder.exists()) {
				for(int i = 0; i < versionSpecificModsFolder.listFiles().length; i++) {
					File file = versionSpecificModsFolder.listFiles()[i];

					if(file.isDirectory()) continue;
					if(!FilenameUtils.getExtension(file.getName()).equals("jar")) continue;
					
					modJars.add(file);
				}
			}
		}
		catch (NoSuchFieldException | SecurityException | IllegalArgumentException | IllegalAccessException e) {
			e.printStackTrace();
		}

		return modJars;
	}

}
