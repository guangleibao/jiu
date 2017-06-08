package bglutil.jiu;

import java.io.IOException;

import com.oracle.bmc.Region;
import com.oracle.bmc.audit.Audit;
import com.oracle.bmc.audit.AuditClient;
import com.oracle.bmc.core.Blockstorage;
import com.oracle.bmc.core.BlockstorageClient;
import com.oracle.bmc.core.Compute;
import com.oracle.bmc.core.ComputeClient;
import com.oracle.bmc.core.VirtualNetwork;
import com.oracle.bmc.core.VirtualNetworkClient;
import com.oracle.bmc.identity.Identity;
import com.oracle.bmc.identity.IdentityClient;
import com.oracle.bmc.loadbalancer.LoadBalancer;
import com.oracle.bmc.loadbalancer.LoadBalancerClient;
import com.oracle.bmc.objectstorage.ObjectStorage;
import com.oracle.bmc.objectstorage.ObjectStorageClient;

/**
 * Client builder. Region determined by default configuration file.
 * @author guanglei
 *
 */
public class Client {
	
	/**
	 * Block storage client.
	 * @param profile
	 * @return
	 * @throws NumberFormatException
	 * @throws IOException
	 */
	public static Blockstorage getBlockstorageClient(String profile) throws NumberFormatException, IOException{
		Blockstorage bs = new BlockstorageClient(Config.getAuthProvider(profile.toUpperCase()), Config.getClientConfig(profile));
		bs.setRegion(Region.fromRegionId(Config.getConfigFileReader(profile.toUpperCase()).get("region")));
		return bs;
	}
	
	/**
	 * Load balancer client.
	 * @param profile
	 * @return
	 * @throws NumberFormatException
	 * @throws IOException
	 */
	public static LoadBalancer getLoadBalancerClient(String profile) throws NumberFormatException, IOException{
		LoadBalancer lb = new LoadBalancerClient(Config.getAuthProvider(profile.toUpperCase()), Config.getClientConfig(profile));
		lb.setRegion(Region.fromRegionId(Config.getConfigFileReader(profile.toUpperCase()).get("region")));
		return lb;
	}
	
	/**
	 * Compute service client.
	 * @param profile
	 * @return
	 * @throws NumberFormatException
	 * @throws IOException
	 */
	public static Compute getComputeClient(String profile) throws NumberFormatException, IOException{
		Compute c = new ComputeClient(Config.getAuthProvider(profile.toUpperCase()), Config.getClientConfig(profile));
		c.setRegion(Region.fromRegionId(Config.getConfigFileReader(profile.toUpperCase()).get("region")));
		return c;
	}
	
	/**
	 * ObjectStorage service client.
	 * @param profile
	 * @return
	 * @throws NumberFormatException
	 * @throws IOException
	 */
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
