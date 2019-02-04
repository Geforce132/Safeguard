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

	public static void handleDependency(ModInfo modInfo) throws IOException {		
		DependencyParser dependencyParser = new DependencyParser(modInfo.modid, Side.CLIENT);
        DependencyParser.DependencyInfo info = dependencyParser.parseDependencies(modInfo.dependencies);

		System.out.println(modInfo.modid + " needs " + modInfo.dependencies);

		ArrayList<ModInfo> dependencies = getDependenciesFromString(info.requirements.toString());
		
		for(ModInfo dependency : dependencies) {
			if(dependency.modid.equals("forge")) continue;

			DependenciesList cfDependency = getDependencyFromID(dependency.modid);
			
			if(cfDependency == null) {
				Safeguard.LOGGER.log(Level.WARNING, modInfo.modid + " requires the mod '" + dependency.modid + "' but Safeguard could not resolve this mod. Skipping...");
				continue;
			}

			if(Downloader.downloadedDependencies.contains(cfDependency)) continue;
			
			/*try {
				SSLContext ctx = SSLContext.getInstance("TLS");
				ctx.init(new KeyManager[0], new TrustManager[] {new DefaultTrustManager()}, new SecureRandom());
				SSLContext.setDefault(ctx);
			}
			catch(Exception e){
				
			}*/
			
			URL url = new URL("https://api.cfwidget.com/minecraft/mc-mods/" + cfDependency.cfProjectName + "?version=" + Safeguard.MCVERSION);
			HttpsURLConnection con = (HttpsURLConnection) url.openConnection();
			/*con.setHostnameVerifier(new HostnameVerifier() {
	            @Override
	            public boolean verify(String arg0, SSLSession arg1) {
	                return true;
	            }
	        });*/
			con.setRequestMethod("GET");
			
			CFMod result  = new Gson().fromJson(new InputStreamReader(con.getInputStream()), CFMod.class);
	
			con.disconnect();

			Downloader.downloadMod(result.download, ModDetector.modDir.getAbsolutePath() + "\\" + Safeguard.MCVERSION);
			Downloader.downloadedDependencies.add(cfDependency);
		}
	}
	
	public static ArrayList<ModInfo> getDependenciesFromString(String string) {
		ArrayList<ModInfo> dependencies = new ArrayList<ModInfo>();
		String[] dependency = string.split(", ");

		for(int i = 0; i < dependency.length; i++) {
			dependencies.add(new ModInfo(i > 0 ? StringUtils.substringBefore(dependency[i], "@[") : StringUtils.substringBetween(string, "[", "@["), null, StringUtils.substringBetween(dependency[i], "@[", ","), null));
		}

		return dependencies;
	}
	
	public static DependenciesList getDependencyFromID(String modid) {
		for(int i = 0; i < DependenciesList.values().length; i++) {
			if(modid.equals(DependenciesList.values()[i].modID)) {
				return DependenciesList.values()[i];
			}
		}
		
		return null;
	}
	
	/*private static class DefaultTrustManager implements X509TrustManager {

        @Override
        public void checkClientTrusted(X509Certificate[] arg0, String arg1) throws CertificateException {}

        @Override
        public void checkServerTrusted(X509Certificate[] arg0, String arg1) throws CertificateException {}

        @Override
        public X509Certificate[] getAcceptedIssuers() {
            return null;
        }
    }*/

}
