package net.geforcemods.safeguard.lists;

public enum DependenciesList {
	
	PTRLIB("ptrmodellib", "ptrlib"),
	LLIBRARY("llibrary", "llibrary"),
	CODECHICKENLIB("codechickenlib", "codechicken-lib-1-8"),
	JEI("jei", "jei");
	
	public final String modID;
	public final String cfProjectName;
	
	private DependenciesList(String id, String name) {
		modID = id;
		cfProjectName = name;
	}

}
