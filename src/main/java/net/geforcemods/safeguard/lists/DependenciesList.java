package net.geforcemods.safeguard.lists;

/**
 * The list of Safeguard-supported dependencies. Any mod
 * on this list can be automatically downloaded from CurseForge.
 */
public enum DependenciesList {
	
	PTRLIB("ptrmodellib", "ptrlib"),
	LLIBRARY("llibrary", "llibrary"),
	CODECHICKENLIB("codechickenlib", "codechicken-lib-1-8"),
	JEI("jei", "jei");
	
	/**
	 * The ID of the mod
	 */
	public final String modID;
	
	/**
	 * The mod's CurseForge project ID
	 * 
	 * (e.g https://minecraft.curseforge.com/projects/<b>security-craft</b>)
	 */
	public final String cfProjectName;
	
	private DependenciesList(String id, String name) {
		modID = id;
		cfProjectName = name;
	}

}
