package net.geforcemods.safeguard.lists;

/**
 * The list of mods that requires a library
 */
public enum DependantsList {
	
	DECOCRAFT("com.mia.props.Props"),
	NEI("codechicken.nei.NotEnoughItems");
	
	/**
	 * The path to the mod's "main" class (the class annotated with @Mod)
	 */
	public final String classPath;
	
	private DependantsList(String mainClassPath) {
		classPath = mainClassPath;
	}

}
