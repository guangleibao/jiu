package bglutil.jiu;

import java.io.IOException;

import com.oracle.bmc.Region;
import com.oracle.bmc.audit.Audit;
import com.oracle.bmc.audit.AuditClient;
import com.oracle.bmc.core.VirtualNetwork;
import com.oracle.bmc.core.VirtualNetworkClient;
import com.oracle.bmc.identity.Identity;
import com.oracle.bmc.identity.IdentityClient;
import com.oracle.bmc.objectstorage.ObjectStorage;
import com.oracle.bmc.objectstorage.ObjectStorageClient;

/**
 * Client builder. Region determined by default configuration file.
 * @author guanglei
 *
 */
public class Client {
	
	public static ObjectStorage getObjectStorageClient(String profile) throws NumberFormatException, IOException{
		ObjectStorage os = new ObjectStorageClient(Config.getAuthProvider(profile.toUpperCase()), Config.getClientConfig(profile));
		os.setRegion(Region.fromRegionId(Config.getConfigFileReader(profile.toUpperCase()).get("region")));
		return os;
	}
	
	/**
	 * Audit service client.
	 * @param profile
	 * @return
	 * @throws IOException
	 */
	public static Audit getAuditClient(String profile) throws IOException{
		Audit auditService = new AuditClient(Config.getAuthProvider(profile.toUpperCase()), Config.getClientConfig(profile));
		auditService.setRegion(Region.fromRegionId(Config.getConfigFileReader(profile.toUpperCase()).get("region")));
		return auditService;
	}
	
	/**
	 * IAM service client.
	 * @param profile
	 * @return
	 * @throws IOException
	 */
	public static Identity getIamClient(String profile) throws IOException {
		Identity idService = new IdentityClient(Config.getAuthProvider(profile.toUpperCase()), Config.getClientConfig(profile));
		idService.setRegion(Region.fromRegionId(Config.getConfigFileReader(profile.toUpperCase()).get("region")));
		return idService;
	}
	
	/**
	 * Network service client.
	 * @param profile
	 * @return
	 * @throws IOException
	 */
	public static VirtualNetwork getVirtualNetworkClient(String profile) throws IOException {
		VirtualNetwork vnService = new VirtualNetworkClient(Config.getAuthProvider(profile.toUpperCase()), Config.getClientConfig(profile));
		vnService.setRegion(Region.fromRegionId(Config.getConfigFileReader(profile.toUpperCase()).get("region")));
		return vnService;
	}
}
