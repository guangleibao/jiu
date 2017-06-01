package bglutil.jiu;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;

import com.google.common.base.Supplier;
import com.oracle.bmc.ClientConfiguration;
import com.oracle.bmc.ConfigFileReader;
import com.oracle.bmc.auth.SimpleAuthenticationDetailsProvider;
import com.oracle.bmc.auth.SimplePrivateKeySupplier;

/**
 * Static configuration settings.
 * @author guanglei
 *
 */
public class Config {
	
	/**
	 * Read public key to string.
	 * @param profile
	 * @return
	 */
	public static String publicKeyToString(String profile){
		StringBuffer sb = new StringBuffer();
		String line = null;
		try {
			BufferedReader br = new BufferedReader(new FileReader(Config.getConfigFileReader(profile).get("public_key_file")));
			line = br.readLine();
			while(line!=null){
				sb.append(line); // No newline appending.
				line = br.readLine();
			}
			br.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return new String(sb);
	}
	
	/**
	 * Get auth provider from default configuration file. Profile sensitive.
	 * @param profile
	 * @return
	 * @throws IOException
	 */
	public static SimpleAuthenticationDetailsProvider getAuthProvider(String profile) throws IOException{
		
		ConfigFileReader.ConfigFile cf = ConfigFileReader.parse("~/.oraclebmc/config", profile.toUpperCase());
		Supplier<InputStream> ks = (Supplier<InputStream>) new SimplePrivateKeySupplier(cf.get("key_file"));
		return SimpleAuthenticationDetailsProvider.builder()
				.tenantId(cf.get("tenancy"))
				.userId(cf.get("user"))
				.fingerprint(cf.get("fingerprint"))
				.privateKeySupplier(ks).build();
	}
	
	/**
	 * Get configuration file reader for default configuration file location.
	 * @param profile
	 * @return
	 * @throws IOException
	 */
	public static ConfigFileReader.ConfigFile getConfigFileReader(String profile) throws IOException{
		return ConfigFileReader.parse("~/.oraclebmc/config", profile.toUpperCase());
	}
	
	/**
	 * A chance to change the default client configuration.
	 * @return
	 * @throws IOException 
	 * @throws NumberFormatException 
	 */
	public static ClientConfiguration getClientConfig(String profile) throws NumberFormatException, IOException{
		int ct = Integer.valueOf(Config.getConfigFileReader(profile).get("connectiontimeout"));
		int rt = Integer.valueOf(Config.getConfigFileReader(profile).get("readtimeout"));
		return (ClientConfiguration.builder().connectionTimeoutMillis(ct)
		.readTimeoutMillis(rt).build());
	}
	
	/**
	 * Extract default compartmentId from default configuration file. The key must be 'compartment'.
	 * @param profile
	 * @return
	 * @throws IOException
	 */
	public static String getMyCompartmentId(String profile) throws IOException{
		ConfigFileReader.ConfigFile cf = ConfigFileReader.parse("~/.oraclebmc/config", profile.toUpperCase());
		return cf.get("compartment");
	}

}
