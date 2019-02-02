package net.geforcemods.safeguard.loader;

public enum ModList {
	
	PTRLIB("ptrmodellib", "ptrlib");
	
	public final String modID;
	public final String cfProjectName;
	
	private ModList(String id, String name) {
		modID = id;
		cfProjectName = name;
	}

}
