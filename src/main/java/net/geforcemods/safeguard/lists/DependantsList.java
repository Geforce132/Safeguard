package net.geforcemods.safeguard.lists;

public enum DependantsList {
	
	DECOCRAFT("com.mia.props.Props"),
	NEI("codechicken.nei.NotEnoughItems");
	
	public final String classPath;
	
	private DependantsList(String mainClassPath) {
		classPath = mainClassPath;
	}

}
