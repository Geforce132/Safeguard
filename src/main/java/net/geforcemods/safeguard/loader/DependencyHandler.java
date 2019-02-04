package net.geforcemods.safeguard.loader;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.logging.Level;

import javax.net.ssl.HttpsURLConnection;

import org.apache.commons.lang3.StringUtils;

import com.google.gson.Gson;

import net.geforcemods.safeguard.Safeguard;
import net.geforcemods.safeguard.downloader.Downloader;
import net.geforcemods.safeguard.lists.DependenciesList;
import net.geforcemods.safeguard.wrappers.CFMod;
import net.geforcemods.safeguard.wrappers.ModInfo;
import net.minecraftforge.fml.common.versioning.DependencyParser;
import net.minecraftforge.fml.relauncher.Side;

public class DependencyHandler {

	/**
	 * Handles checking for and downloading any dependency that a mod may require
	 * @param modInfo the dependant's info
	 * @throws IOException
	 */
	public static void handleDependency(ModInfo modInfo) throws IOException {		
		DependencyParser dependencyParser = new DependencyParser(modInfo.modid, Side.CLIENT);
        DependencyParser.DependencyInfo info = dependencyParser.parseDependencies(modInfo.dependencies);

		Safeguard.LOGGER.log(Level.INFO, modInfo.modid + " needs " + modInfo.dependencies);

		ArrayList<ModInfo> dependencies = getDependenciesFromString(info.requirements.toString());
		
		for(ModInfo dependency : dependencies) {
			// Ignore the dependency if it is Forge itself
			if(dependency.modid.equals("forge")) continue;

			DependenciesList cfDependency = getDependencyFromID(dependency.modid);
			
			if(cfDependency == null) {
				Safeguard.LOGGER.log(Level.WARNING, modInfo.modid + " requires the mod '" + dependency.modid + "' but Safeguard could not resolve this mod. Skipping...");
				continue;
			}

			if(Downloader.downloadedDependencies.contains(cfDependency)) continue;
			
			URL url = new URL("https://api.cfwidget.com/minecraft/mc-mods/" + cfDependency.cfProjectName + "?version=" + Safeguard.MCVERSION);
			HttpsURLConnection con = (HttpsURLConnection) url.openConnection();
			con.setRequestMethod("GET");
			
			CFMod result  = new Gson().fromJson(new InputStreamReader(con.getInputStream()), CFMod.class);
	
			con.disconnect();

			Downloader.downloadMod(result.download, ModDetector.modDir.getAbsolutePath() + "\\" + Safeguard.MCVERSION);
			Downloader.downloadedDependencies.add(cfDependency);
		}
	}
	
	/**
	 * Parse each individual mod (and its version) from a @Mod.dependencies() string
	 * @param string The string to parse
	 */
	public static ArrayList<ModInfo> getDependenciesFromString(String string) {
		ArrayList<ModInfo> dependencies = new ArrayList<ModInfo>();
		String[] dependency = string.split(", ");

		for(int i = 0; i < dependency.length; i++) {
			dependencies.add(new ModInfo(i > 0 ? StringUtils.substringBefore(dependency[i], "@[") : StringUtils.substringBetween(string, "[", "@["), null, StringUtils.substringBetween(dependency[i], "@[", ","), null));
		}

		return dependencies;
	}
	
	/**
	 * Lookup a supported dependency by its mod ID
	 * @param modid The mod ID of the dependency
	 */
	public static DependenciesList getDependencyFromID(String modid) {
		for(int i = 0; i < DependenciesList.values().length; i++) {
			if(modid.equals(DependenciesList.values()[i].modID)) {
				return DependenciesList.values()[i];
			}
		}
		
		return null;
	}

}
