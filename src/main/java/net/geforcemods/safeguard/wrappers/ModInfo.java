package net.geforcemods.safeguard.wrappers;

/**
 * Wrapper class for a loaded mod's main info
 */
public class ModInfo {
	
	public final String modid;
	public final String name;
	public final String version;
	public final String dependencies;
	
	public ModInfo(String id, String modName, String modVersion, String modDependencies) {
		modid = id;
		name = modName;
		version = modVersion;
		dependencies = modDependencies;
	}

}
