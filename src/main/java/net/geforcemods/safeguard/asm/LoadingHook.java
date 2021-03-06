package net.geforcemods.safeguard.asm;

import java.util.Map;
import java.util.logging.Level;

import net.geforcemods.safeguard.Safeguard;
import net.geforcemods.safeguard.downloader.Downloader;
import net.geforcemods.safeguard.loader.ModDetector;
import net.minecraftforge.fml.relauncher.IFMLLoadingPlugin;
import net.minecraftforge.fml.relauncher.IFMLLoadingPlugin.MCVersion;

/**
 * Using a coremod as a slight "hack" to get Safeguard's mod detection code running before
 * Forge does its dependency check
 */
@IFMLLoadingPlugin.TransformerExclusions({"net.geforcemods.safeguard.asm"})
@MCVersion(value=Safeguard.MCVERSION)
public class LoadingHook implements IFMLLoadingPlugin {

    public LoadingHook() {}

    @Override
    public String[] getASMTransformerClass()
    {
        return new String[0];
    }

    @Override
    public String getModContainerClass()
    {
        return null;
    }

    @Override
    public String getSetupClass()
    {
        return null;
    }

    @Override
    public void injectData(Map<String, Object> data) {}

    @Override
    public String getAccessTransformerClass()
    {
    	// Doing this here because it takes a few milliseconds for the property to be updated
        // Used when opening connections to downloads
		System.setProperty("http.agent", "Chrome");

    	if(!Downloader.isOnline()) {
    		Safeguard.LOGGER.log(Level.WARNING, "No internet connection detected, Safeguard functionality is disabled!");
    		return null;
    	}

    	ModDetector.detectMods();
        return null;
    }

}