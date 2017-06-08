package bglutil.jiu;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.SimpleTimeZone;
import java.util.TimeZone;
import java.util.TreeMap;
import java.util.TreeSet;

import com.oracle.bmc.audit.Audit;
import com.oracle.bmc.audit.AuditClient;
import com.oracle.bmc.core.Blockstorage;
import com.oracle.bmc.core.BlockstorageClient;
import com.oracle.bmc.core.Compute;
import com.oracle.bmc.core.ComputeClient;
import com.oracle.bmc.core.VirtualNetwork;
import com.oracle.bmc.core.VirtualNetworkClient;
import com.oracle.bmc.core.model.DhcpOption;
import com.oracle.bmc.core.model.DhcpOptions;
import com.oracle.bmc.core.model.Drg;
import com.oracle.bmc.core.model.EgressSecurityRule;
import com.oracle.bmc.core.model.IScsiVolumeAttachment;
import com.oracle.bmc.core.model.Image;
import com.oracle.bmc.core.model.IngressSecurityRule;
import com.oracle.bmc.core.model.Instance;
import com.oracle.bmc.core.model.InstanceCredentials;
import com.oracle.bmc.core.model.InternetGateway;
import com.oracle.bmc.core.model.RouteRule;
import com.oracle.bmc.core.model.RouteTable;
import com.oracle.bmc.core.model.SecurityList;
import com.oracle.bmc.core.model.Subnet;
import com.oracle.bmc.core.model.Vcn;
import com.oracle.bmc.core.model.Vnic;
import com.oracle.bmc.core.model.VnicAttachment;
import com.oracle.bmc.core.model.Volume;
import com.oracle.bmc.core.model.VolumeAttachment;
import com.oracle.bmc.core.requests.GetDrgRequest;
import com.oracle.bmc.core.requests.GetInternetGatewayRequest;
import com.oracle.bmc.core.requests.GetSecurityListRequest;
import com.oracle.bmc.core.requests.GetSubnetRequest;
import com.oracle.bmc.core.requests.GetVolumeAttachmentRequest;
import com.oracle.bmc.core.requests.GetWindowsInstanceInitialCredentialsRequest;
import com.oracle.bmc.core.requests.InstanceActionRequest;
import com.oracle.bmc.core.requests.ListInstancesRequest;
import com.oracle.bmc.core.requests.ListSubnetsRequest;
import com.oracle.bmc.core.requests.ListVnicAttachmentsRequest;
import com.oracle.bmc.core.responses.GetInstanceResponse;
import com.oracle.bmc.identity.Identity;
import com.oracle.bmc.identity.IdentityClient;
import com.oracle.bmc.identity.model.ApiKey;
import com.oracle.bmc.identity.model.AvailabilityDomain;
import com.oracle.bmc.identity.model.User;
import com.oracle.bmc.identity.requests.ListApiKeysRequest;
import com.oracle.bmc.identity.requests.ListAvailabilityDomainsRequest;
import com.oracle.bmc.identity.requests.ListUsersRequest;
import com.oracle.bmc.identity.responses.ListUsersResponse;
import com.oracle.bmc.loadbalancer.LoadBalancer;
import com.oracle.bmc.loadbalancer.LoadBalancerClient;
import com.oracle.bmc.loadbalancer.model.Backend;
import com.oracle.bmc.loadbalancer.model.LoadBalancerPolicy;
import com.oracle.bmc.loadbalancer.model.LoadBalancerShape;
import com.oracle.bmc.loadbalancer.model.WorkRequest;
import com.oracle.bmc.loadbalancer.model.WorkRequestError;
import com.oracle.bmc.loadbalancer.requests.GetLoadBalancerRequest;
import com.oracle.bmc.loadbalancer.requests.ListLoadBalancersRequest;
import com.oracle.bmc.objectstorage.ObjectStorage;
import com.oracle.bmc.objectstorage.ObjectStorageClient;
import com.oracle.bmc.objectstorage.transfer.UploadManager.UploadResponse;
import com.oracle.bmc.ClientConfiguration;
import com.oracle.bmc.Region;

import bglutil.jiu.common.Helper;
import bglutil.jiu.common.Speaker;
import bglutil.jiu.common.SubnetAsset;
import bglutil.jiu.common.UtilMain;
import bglutil.jiu.common.VcnAsset;

/**
 * Jiu Jiu~
 * Oracle Bare Metal Cloud demos for fun.
 */
public class Jiu {
	// Those methods will not be exposed by main routine.
	public static final ArrayList<String> SKIPPED_METHODS = new ArrayList<String>();
	
	// Static settings, certain.
	static {
		java.security.Security.setProperty("networkaddress.cache.ttl", "60");
		SKIPPED_METHODS.add("coreV2");
		SKIPPED_METHODS.add("main");
	}
	
	// Helper utilities.
	private static Speaker sk = Speaker.CONSOLE;
	private Helper h = new Helper();
	
	
	// OBJECT STORAGE //
	
	/**
	 * List all objects under a bucket.
	 * @param bucketName
	 * @param profile
	 * @throws Exception
	 */
	public void showObject(String bucketName, String profile) throws Exception{
		h.help(bucketName, "<bucket> <profile>");
		ObjectStorage os = Client.getObjectStorageClient(profile);
		UtilObjectStorage uos = new UtilObjectStorage();
		sk.printTitle(0, "All Objects in "+bucketName);
		uos.printAllObjectsInBucket(os, bucketName, profile);
		sk.printTitle(0, "End");
	}
	
	/**
	 * Upload a file to Object Storage
	 * @param bucketName
	 * @param objectName
	 * @param filePath
	 * @param profile
	 * @throws NumberFormatException
	 * @throws IOException
	 * @throws InterruptedException
	 */
	public void simpleUpload(String bucketName, String objectName, String filePath, String profile) throws NumberFormatException, IOException, InterruptedException{
		h.help(bucketName, "<bucket> <object> <file> <profile>");
		ObjectStorage os = Client.getObjectStorageClient(profile);
		UtilObjectStorage uos = new UtilObjectStorage();
		h.processingV2("Uploading ... ");
		UploadResponse ur = uos.upload(os, bucketName, objectName, new File(filePath), null, null, null, null);
		h.done(Helper.BUILDING);
		sk.printResult(0, true, "md5: "+ur.getContentMd5());
		sk.printResult(0, true, "ETag: "+ur.getETag());
	}
	
	/**
	 * Download a from Object Storage
	 * @param bucketName
	 * @param objectName
	 * @param filePath
	 * @param profile
	 * @throws NumberFormatException
	 * @throws IOException
	 * @throws InterruptedException
	 */
	public void simpleDownload(String bucketName, String objectName, String filePath, String profile) throws NumberFormatException, IOException, InterruptedException{
		h.help(bucketName, "<bucket> <object> <file> <profile>");
		ObjectStorage os = Client.getObjectStorageClient(profile);
		UtilObjectStorage uos = new UtilObjectStorage();
		h.processingV2("Downloading ... ");
		uos.download(os, bucketName, objectName, new File(filePath));
		h.done(Helper.BUILDING);
	}
	
	// SHOW //
	
	public void detachVolume(String volumeName, String instanceName, String profile) throws NumberFormatException, IOException{
		h.help(volumeName, "<volume-name> <instance-name> <profile>");
		sk.printTitle(0, "Detach "+volumeName+" from "+instanceName);
		Compute c = Client.getComputeClient(profile);
		Blockstorage bs = Client.getBlockstorageClient(profile);
		UtilCompute uc = new UtilCompute();
		UtilBlockStorage ubs = new UtilBlockStorage();
		String compartmentId = Config.getMyCompartmentId(profile);
		Instance i = uc.getInstanceByName(c, instanceName, compartmentId);
		List<VolumeAttachment> va = uc.getVolumeAttachment(c, i, compartmentId);
		for(VolumeAttachment v:va){
			if(!v.getLifecycleState().equals(VolumeAttachment.LifecycleState.Detached)){
				String volumeId = v.getVolumeId();
				String vName = ubs.getVolumeById(bs, volumeId).getDisplayName();
				if(vName.equals(volumeName)){
					sk.printResult(0, true, "Detaching...");
					uc.detachVolume(c, v.getId());
				}
			}
		}
		sk.printTitle(0, "End");
	}
	
	public void showVolumeAttachment(String profile) throws NumberFormatException, IOException{
		h.help(profile, "<profile>");
		sk.printTitle(0, "Instance and volume attachment in profile compartment");
		Compute c = Client.getComputeClient(profile);
		Blockstorage bs = Client.getBlockstorageClient(profile);
		UtilCompute uc = new UtilCompute();
		UtilBlockStorage ubs = new UtilBlockStorage();
		String compartmentId = Config.getMyCompartmentId(profile);
		List<Instance> instances = uc.getAllInstance(c, compartmentId);
		for(Instance i:instances){
			if(!i.getLifecycleState().equals(Instance.LifecycleState.Terminated)){
				List<VolumeAttachment> va = uc.getVolumeAttachment(c, i, compartmentId);
				sk.printResult(0,true,"Instance: "+i.getDisplayName()+", "+i.getAvailabilityDomain());
				for(VolumeAttachment v:va){
					if(!v.getLifecycleState().equals(VolumeAttachment.LifecycleState.Detached)){
						Volume vol = ubs.getVolumeById(bs, v.getVolumeId());
						sk.printResult(1, true, v.getDisplayName()+", "+vol.getDisplayName()+", "+vol.getSizeInMBs()/1024+"GB");
						IScsiVolumeAttachment iv = (IScsiVolumeAttachment) v;
						this.printISCSIInfo(iv);
					}
				}
			}
		}
		sk.printTitle(0, "End");
	}
	
	/**
	 * Show all volumes that vm can utilize in subnet scope.
	 * @param vcnName
	 * @param profile
	 * @throws NumberFormatException
	 * @throws IOException
	 */
	public void showVolume(String vcnName, String profile) throws NumberFormatException, IOException{
		h.help(vcnName, "<vcn-name> <profile>");
		sk.printTitle(0, "All volumes that subnets can use. Same volume can be showed MULTIPLE times if you have MULTIPLE subnets in ONE AD!");
		Compute c = Client.getComputeClient(profile);
		Blockstorage bs = Client.getBlockstorageClient(profile);
		VirtualNetwork vn = Client.getVirtualNetworkClient(profile);
		UtilCompute uc = new UtilCompute();
		UtilBlockStorage ubs = new UtilBlockStorage();
		UtilNetwork un = new UtilNetwork();
		String compartmentId = Config.getMyCompartmentId(profile);
		List<Subnet> subnets = un.getSubnetInVcn(vn, compartmentId, vcnName);
		for(Subnet s:subnets){
			System.out.println();
			sk.printResult(0,true,"Subnet: "+s.getDisplayName()+", "+s.getCidrBlock()+", "+s.getAvailabilityDomain());
			System.out.println();
			List<Volume> vols = ubs.getAllVolume(bs, s.getAvailabilityDomain(), compartmentId);
			for(Volume v:vols){
				if(!v.getLifecycleState().equals(Volume.LifecycleState.Terminated)){
					sk.printResult(1, true, v.getDisplayName()+", "+v.getSizeInMBs()/1024+"GB, "+v.getLifecycleState().toString());
				}
			}
		}
		sk.printTitle(0, "End");
	}
	
	private void showAction(Class clazz){
		List<String> skip = new ArrayList<String>();
		skip.add("close");skip.add("setEndpoint");skip.add("setRegion");skip.add("getWaiters");
		Set<String> methods = h.getPublicMethodName(clazz);
		int i=0;
		for(String m:methods){
			System.out.println(skip.contains(m)?Helper.STAR+" "+m:(++i)+": "+m);
		}
	}
	
	/**
	 * List all actions available for compute service.
	 */
	public void showActionCompute(){
		this.showAction(ComputeClient.class);
	}
	
	/**
	 * List all actions available for block storage service.
	 */
	public void showActionBlockstorage(){
		this.showAction(BlockstorageClient.class);
	}
	
	/**
	 * List all actions available for network service.
	 */
	public void showActionNetwork(){
		this.showAction(VirtualNetworkClient.class);
	}
	
	/**
	 * List all actions available for IAM service.
	 */
	public void showActionIam(){
		this.showAction(IdentityClient.class);
	}
	
	/**
	 * List all actions available for audit service.
	 */
	public void showActionAudit(){
		this.showAction(AuditClient.class);
	}
	
	/**
	 * List all actions available for object storage service.
	 */
	public void showActionObjectStorage(){
		this.showAction(ObjectStorageClient.class);
	}
	
	/**
	 * List all actions available for load balancing service.
	 */
	public void showActionLoadbalancer(){
		this.showAction(LoadBalancerClient.class);
	}
	
	/**
	 * Print out instanceId by name.
	 * @param name
	 * @param profile
	 * @throws NumberFormatException
	 * @throws IOException
	 */
	public void printInstanceIdByName(String name, String profile) throws NumberFormatException, IOException{
		h.help(name, "<instance-name> <profile>");
		Compute c = Client.getComputeClient(profile);
		UtilCompute uc = new UtilCompute();
		String compartmentId = Config.getMyCompartmentId(profile);
		System.out.println(uc.getInstanceIdByName(c, name, compartmentId));
	}
	
	/**
	 * Print out instance private or public IP address by name.
	 * @param name
	 * @param privateOrPublic
	 * @param profile
	 * @throws NumberFormatException
	 * @throws IOException
	 */
	public void printInstanceIpByName(String name, String privateOrPublic, String profile) throws NumberFormatException, IOException{
		h.help(name, "<instance-name> <type: private|public> <profile>");
		Compute c = Client.getComputeClient(profile);
		VirtualNetwork vn = Client.getVirtualNetworkClient(profile);
		UtilCompute uc = new UtilCompute();
		String compartmentId = Config.getMyCompartmentId(profile);
		System.out.println(uc.getInstanceIpByName(c, vn, name, privateOrPublic, compartmentId));
	}
	
	/**
	 * Show namespace for object storage.
	 * @param profile
	 * @throws NumberFormatException
	 * @throws IOException
	 */
	public void showNamespace(String profile) throws NumberFormatException, IOException{
		h.help(profile, "<profile>");
		ObjectStorage os = Client.getObjectStorageClient(profile);
		UtilObjectStorage uos = new UtilObjectStorage();
		sk.printTitle(0, "Namespace: "+uos.getNamespace(os));
	}
	
	/**
	 * Show all load balancers in a VCN.
	 * @param vcnName
	 * @param profile
	 * @throws NumberFormatException
	 * @throws IOException
	 */
	public void showLbInVcn(String vcnName, String profile) throws NumberFormatException, IOException{
		h.help(vcnName, "<vcn-name> <profile>");
		sk.printTitle(0, "Show all load balancers in VCN "+vcnName);
		UtilLB ulb = new UtilLB();
		LoadBalancer lb = Client.getLoadBalancerClient(profile);
		VirtualNetwork vn = Client.getVirtualNetworkClient(profile);
		UtilNetwork un = new UtilNetwork();
		String compartmentId = Config.getMyCompartmentId(profile);
		String vcnId = un.getVcnIdByName(vn, vcnName, compartmentId);
		List<com.oracle.bmc.loadbalancer.model.LoadBalancer> ls = lb.listLoadBalancers(ListLoadBalancersRequest.builder().compartmentId(compartmentId).build()).getItems();
		for(com.oracle.bmc.loadbalancer.model.LoadBalancer l:ls){
			for(String subnetId:l.getSubnetIds()){
				Subnet s = vn.getSubnet(GetSubnetRequest.builder().subnetId(subnetId).build()).getSubnet();
				if(s.getVcnId().equals(vcnId)){
					List<String> sids = l.getSubnetIds();
					List<Subnet> sns = new ArrayList<Subnet>();
					for(String sid:sids){
						sns.add(vn.getSubnet(GetSubnetRequest.builder().subnetId(sid).build()).getSubnet());
					}
					sk.printResult(1,true, Helper.STAR+" "+Helper.STAR+" LoadBalancer: "+l.getDisplayName()+", "+l.getIpAddresses().get(0)+", "+l.getShapeName()+" @"+sns.get(0).getCidrBlock()+","+sns.get(1).getCidrBlock());
					for(String lName:l.getListeners().keySet()){
						sk.printResult(2,true, l.getListeners().get(lName));
					}
					for(String backendSetName:l.getBackendSets().keySet()){
						sk.printResult(2, true, "BackendSet: "+l.getBackendSets().get(backendSetName).getName()+", "+l.getBackendSets().get(backendSetName).getPolicy());
						List<Backend> ends = l.getBackendSets().get(backendSetName).getBackends();
						for(Backend be:ends){
							sk.printResult(3, true, "Backend: "+be.getName()+", "+be.getIpAddress()+", "+be.getPort()+", "+be.getWeight()+", "+(be.getOffline()?"offline":"online"+", "+(be.getBackup()?"backup":"primary")+", "+(be.getDrain()?"drain":"active")));
						}
					}
					break;
				}
			}
		}
		sk.printTitle(0, "End");
	}
	
	/**
	 * Show AD.
	 * @param profile
	 * @throws IOException
	 */
	public void showAd(String profile) throws IOException{
		sk.printTitle(0, "Bare Metal AD in "+Config.getConfigFileReader(profile).get("region"));
		Identity id = Client.getIamClient(profile);
		List<AvailabilityDomain> ads = id.listAvailabilityDomains(ListAvailabilityDomainsRequest.builder().compartmentId(Config.getMyCompartmentId(profile)).build()).getItems();
		for(AvailabilityDomain ad:ads){
			sk.printResult(0, true, ad.getName());
		}
		sk.printTitle(0, "End");
	}
	
	/**
	 * Encode plain text base64.
	 * @param plainText
	 */
	public void base64Encode(String plainText){
		h.help(plainText, "<plain-text>");
		System.out.println(h.base64Encode(plainText));
	}
	
	/**
	 * Decode plain text from base64.
	 * @param base64
	 */
	public void base64Decode(String base64){
		h.help(base64, "<base64>");
		System.out.println(h.base64Decode(base64));
	}
	
	/**
	 * Encode file content to base64.
	 * @param path
	 */
	public void base64EncodeFromFile(String path){
		h.help(path, "<path>");
		h.base64EncodeFromFile(path);
	}
	
	/**
	 * Show available load balancer policies.
	 * @param profile
	 * @throws NumberFormatException
	 * @throws IOException
	 */
	public void showLbPolicy(String profile) throws NumberFormatException, IOException{
		h.help(profile, "<profile>");
		UtilLB ulb = new UtilLB();
		LoadBalancer lb = Client.getLoadBalancerClient(profile);
		List<LoadBalancerPolicy> policies = ulb.getAllLoadBalancerPolicy(lb, Config.getMyCompartmentId(profile));
		sk.printTitle(0, "All Load Balancer Policies");
		for(LoadBalancerPolicy p:policies){
			sk.printResult(0, true, p.getName());
		}
		sk.printTitle(0, "End");
	}
	
	/**
	 * Show available load balancer shapes.
	 * @param profile
	 * @throws NumberFormatException
	 * @throws IOException
	 */
	public void showLbShape(String profile) throws NumberFormatException, IOException{
		h.help(profile, "<profile>");
		UtilLB ulb = new UtilLB();
		LoadBalancer lb = Client.getLoadBalancerClient(profile);
		List<LoadBalancerShape> shapes = ulb.getAllLoadBalancerShapeName(lb, Config.getMyCompartmentId(profile));
		sk.printTitle(0, "All Load Balancer Shapes");
		for(LoadBalancerShape s:shapes){
			sk.printResult(0, true, s.getName());
		}
		sk.printTitle(0, "End");
	}
	
	/**
	 * Show available VM images.
	 * @param profile
	 * @throws NumberFormatException
	 * @throws IOException
	 */
	public void showImage(String profile) throws NumberFormatException, IOException{
		h.help(profile, "<profile>");
		Compute c = Client.getComputeClient(profile);
		UtilCompute uc = new UtilCompute();
		sk.printTitle(0, "All Images");
		for(Image i:uc.getAllImage(c, Config.getMyCompartmentId(profile))){
			sk.printResult(0, true, i.getDisplayName()+", "+i.getOperatingSystem()+", "+i.getOperatingSystemVersion());
			sk.printResult(1, true, i.getId());
		}
		sk.printTitle(0, "End");
	}
	
	/**
	 * Show available shapes.
	 * @param profile
	 * @throws NumberFormatException
	 * @throws IOException
	 */
	public void showShape(String profile) throws NumberFormatException, IOException{
		h.help(profile, "<profile>");
		Compute c = Client.getComputeClient(profile);
		UtilCompute uc = new UtilCompute();
		sk.printTitle(0, "All Shapes");
		for(String s:uc.getAllShape(c, Config.getMyCompartmentId(profile))){
			sk.printResult(0, true, s);
		}
		sk.printTitle(0, "End");
	}
	
	/**
	 * Show all buckets.
	 * @param profile
	 * @throws Exception
	 */
	public void showBucket(String profile) throws Exception{
		h.help(profile, "<profile>");
		ObjectStorage os = Client.getObjectStorageClient(profile);
		UtilObjectStorage uos = new UtilObjectStorage();
		sk.printTitle(0, "All Buckets by profile "+profile);
		uos.printAllBuckets(os, profile);
		sk.printTitle(0, "End");
	}
	
	/**
	 * Show rules for seclist.
	 * @param secListOcid
	 * @param profile
	 * @throws Exception
	 */
	public void showSecListById(String secListOcid, String profile) throws Exception{
		h.help(secListOcid, "<sec-list-ocid> <profile>");
		VirtualNetwork vn = Client.getVirtualNetworkClient(profile);
		UtilNetwork un = new UtilNetwork();
		SecurityList sl = vn.getSecurityList(GetSecurityListRequest.builder().securityListId(secListOcid).build()).getSecurityList();
		sk.printTitle(1,"Security List:");
		un.printSecListRules(sl);
		sk.printTitle(0, "End");
	}
	
	
	
	/**
	 * Show all IAM users.
	 * @param profile
	 * @throws Exception
	 */
	public void showUser(String profile) throws Exception{
		h.help(profile, "<profile-name>");
		Identity id = Client.getIamClient(profile);
		ListUsersResponse res = id.listUsers(
				ListUsersRequest.builder().compartmentId(Config.getConfigFileReader(profile).get("tenancy")).build()
				);
		sk.printTitle(0, "All IAM Users");
		for (User u:res.getItems()){
			sk.printResult(0, true, u.getName()+", "+u.getLifecycleState().getValue()+", "+u.getId());
			List<ApiKey> ak = id.listApiKeys(ListApiKeysRequest.builder().userId(u.getId()).build()).getItems();
			for(ApiKey k:ak){
				sk.printResult(1, true, k.getKeyValue());
				
			}
		}
		sk.printTitle(0, "End");
	}
	
	/**
	 * Show all VCN details by profile compartment.
	 * @param profile
	 * @throws Exception
	 */
	public void showVcn(String name, String profile) throws Exception{
		h.help(name, "<vcn-name: ? means all> <profile-name>");
		VirtualNetwork vn = Client.getVirtualNetworkClient(profile);
		Identity id = Client.getIamClient(profile);
		Compute c = Client.getComputeClient(profile);
		UtilNetwork un = new UtilNetwork();
		UtilIam ui = new UtilIam();
		UtilCompute uc = new UtilCompute();
		String compartmentId = Config.getMyCompartmentId(profile);
		String compartmentName = ui.getCompartmentNameByOcid(id, compartmentId);
		sk.printTitle(0, (name.equals("?")?"All":name)+" VCN in Compartment - "+compartmentName);
		boolean wildhunt = false;
		if(name.equals("?")){
			wildhunt = true;
		}
		for (Vcn v:un.getAllVcn(vn, compartmentId)){
			if(!wildhunt && !v.getDisplayName().toLowerCase().startsWith(name.toLowerCase())){
				continue;
			}
			VcnAsset va = VcnAsset.getInstance(vn, id, v.getId(), compartmentId);
			sk.printResult(0, true, "VCN: "+h.objectName(va.getName())+", "+va.getCidr()+", "+va.getDomainName()+", "+va.getVcn().getLifecycleState().getValue()+", "+va.getVcn().getTimeCreated().toString());
			sk.printResult(1, true, "OCID: "+va.getOcid());
			sk.printResult(1, true, "ALL INTERNET GATEWAY:");
			for(InternetGateway igw:va.getIgws().values()){
				sk.printResult(2, true, h.objectName(igw.getDisplayName())+", enabled-"+igw.getIsEnabled());
				sk.printResult(3, true, "OCID: "+igw.getId());
			}
			sk.printResult(1, true, "DEFAULT ROUTE TABLE: "+h.objectName(va.getDefaultRouteTable().getDisplayName()));
			sk.printResult(1, true, "ALL ROUTE TABLE:");
			for(RouteTable rt:va.getRouteTables().values()){
				sk.printResult(2, true, h.objectName(rt.getDisplayName()));
				sk.printResult(3, true, "OCID: "+rt.getId());
				for(RouteRule rr:rt.getRouteRules()){
					String nOcid = rr.getNetworkEntityId();
					if(nOcid.contains("internetgateway")){
						InternetGateway igw = vn.getInternetGateway(GetInternetGatewayRequest.builder().igId(nOcid).build()).getInternetGateway();
						sk.printResult(3, true, rr.getCidrBlock()+" => "+h.objectName(igw.getDisplayName()));
					}
					else if(nOcid.contains("dynamic")){ //TODO To be confirmed.
						Drg drg = vn.getDrg(GetDrgRequest.builder().drgId(nOcid).build()).getDrg();
						sk.printResult(3, true, rr.getCidrBlock()+" => "+h.objectName(drg.getDisplayName()));
					}
				}
			}
			sk.printResult(1, true, "DEFAULT DHCP OPTIONS: "+h.objectName(va.getDefaultDhcpOptions().getDisplayName()));
			sk.printResult(1, true, "ALL DHCP OPTIONS:");
			for(DhcpOptions dhcpo:va.getDhcpOptions().values()){
				sk.printResult(2, true, h.objectName(dhcpo.getDisplayName()));
				sk.printResult(3, true, "OCID: "+dhcpo.getId());
				for(DhcpOption dhcp:dhcpo.getOptions()){
					sk.printResult(3, true, dhcp.toString());
				}
			}
			sk.printResult(1, true, "DEFAULT SECURITY LIST: "+h.objectName(va.getDefaultSecList().getDisplayName()));	
			sk.printResult(1, true, "ALL SECURITY LIST:");
			for(SecurityList sl:va.getSecLists().values()){
				un.printSecListRules(sl);
			}
			Collection<Subnet> subnets = va.getSubnets().values();
			for(Subnet s:subnets){
				//SubnetAsset sa = SubnetAsset.getInstance(vn, id, s.getId());
				sk.printResult(1, true, "SUBNET: "+h.objectName(s.getDisplayName())+", "+s.getCidrBlock()+", "+s.getSubnetDomainName()+", "+s.getAvailabilityDomain()+", "+(s.getProhibitPublicIpOnVnic()?"Private":"Public"));
				sk.printResult(2, true, "OCID: "+s.getId());
				sk.printResult(2, true, "DHCP Options: "+h.objectName(va.getDhcpOptions().get(s.getDhcpOptionsId()).getDisplayName()));
				sk.printResult(2, true, "Route Table: "+h.objectName(va.getRouteTables().get(s.getRouteTableId()).getDisplayName()));
				int sli=0;
				for(String slId:s.getSecurityListIds()){
					sk.printResult(2, true, "SecList: ("+(++sli)+") "+h.objectName(va.getSecLists().get(slId).getDisplayName()));
				}
				// Instance detail
				Map<Vnic,Instance> instances = un.getInstanceBySubnet(vn, c, s, Instance.LifecycleState.Running);
				for(Vnic nic:instances.keySet()){
					String dn = instances.get(nic).getDisplayName();
					sk.printResult(2, true, (dn.toLowerCase().contains("bastion")?Helper.FIST:Helper.STAR)+"  Machine: "
							+dn+", "
							+nic.getPrivateIp()+", "
							+nic.getPublicIp()+", "
							+instances.get(nic).getShape()+", "
							+uc.getImageNameById(c,instances.get(nic).getImageId())+", "
							+instances.get(nic).getLifecycleState().getValue());
				}
			}
			LoadBalancer lb = Client.getLoadBalancerClient(profile);
			List<com.oracle.bmc.loadbalancer.model.LoadBalancer> ls = lb.listLoadBalancers(ListLoadBalancersRequest.builder().compartmentId(compartmentId).build()).getItems();
			for(com.oracle.bmc.loadbalancer.model.LoadBalancer l:ls){
				for(String subnetId:l.getSubnetIds()){
					Subnet s = vn.getSubnet(GetSubnetRequest.builder().subnetId(subnetId).build()).getSubnet();
					if(s.getVcnId().equals(v.getId())){
						List<String> sids = l.getSubnetIds();
						List<Subnet> sns = new ArrayList<Subnet>();
						for(String sid:sids){
							sns.add(vn.getSubnet(GetSubnetRequest.builder().subnetId(sid).build()).getSubnet());
						}
						sk.printResult(1,true, Helper.STAR+" "+Helper.STAR+" LoadBalancer: "+l.getDisplayName()+", "+l.getIpAddresses().get(0)+", "+l.getShapeName()+" @"+sns.get(0).getCidrBlock()+","+sns.get(1).getCidrBlock());
						for(String lName:l.getListeners().keySet()){
							sk.printResult(2,true, l.getListeners().get(lName));
						}
						break;
					}
				}
			}
		}
		sk.printTitle(0, "End");
	}
	
	/**
	 * Show client config.
	 * @throws IOException 
	 * @throws NumberFormatException 
	 */
	public void showClientConfig(String profile) throws NumberFormatException, IOException{
		h.help(profile, "<profile-name>");
		ClientConfiguration cc = Config.getClientConfig(profile);
		sk.printTitle(0, "Current Client Config");
		sk.printResult(0, true, "Connection Timeout (ms): "+cc.getConnectionTimeoutMillis());
		sk.printResult(0, true, "Read Timeout (ms): "+cc.getReadTimeoutMillis());
		sk.printResult(0, true, "Max Async Threads: "+cc.getMaxAsyncThreads());
		sk.printTitle(0, "End");
	}
	
	/**
	 * Show all regions.
	 */
	public void showRegionCode(){
		sk.printTitle(0, "Bare Metal Regions");
		int i=0;
		for(Region r:Region.values()){
			sk.printResult(0, true, "("+(++i)+") "+r.getRegionId());
		}
		sk.printTitle(0, "End");
	}
	
	/**
	 * Show profile if works.
	 * @param profile
	 * @throws IOException
	 */
	public void showProfile(String profile) throws IOException {
		h.help(profile, "<profile-name>");
		sk.printTitle(0, "Profile " + profile);
		sk.printResult(0, true, Config.getAuthProvider(profile).getKeyId());
		sk.printTitle(0, "End");
	}
	
	/**
	 * Show all timezone.
	 */
	public void showTimezone(){
		TimeZone tz = null;
		for(String id:TimeZone.getAvailableIDs()){
			if(id.length()==3 && id.endsWith("T")){
				continue;
			}
			tz = SimpleTimeZone.getTimeZone(id);
			System.out.println(id+", ["+tz.getRawOffset()/(1000*60*60.0)+"]");
		}
		sk.printTitle(0, "End");
	}
	
	/**
	 * Show recent audit log within N minutes.
	 * @param lastMinutes
	 * @param format
	 * @param mirror
	 * @param profile
	 * @throws Exception
	 */
	public void showAuditEvent(String lastMinutes, String format, String mirror, String profile) throws Exception{
		h.help(lastMinutes, "<last-minutes> <format: raw|simple> <mirror: yes|no> <profile-name>");
		Audit a = Client.getAuditClient(profile);
		Identity i = Client.getIamClient(profile);
		UtilAudit ua = new UtilAudit();
		String except = null;
		if(mirror.equals("no")){
			except = "auditEvents";
		}
		ua.printAuditEventByDateRange(a, Config.getMyCompartmentId(profile), new Date(System.currentTimeMillis() - Long.parseLong(lastMinutes)*60000), new Date(), format, except==null?null:except.toLowerCase(), i);
		sk.printTitle(0, "End");
	}
	
	/**
	 * Show Java system properties.
	 */
	public void showSystemProperty() {
		Properties p = System.getProperties();
		for (Object k : p.keySet()) {
			System.out.println("[ " + k + " ] => ( " + p.getProperty((String) k) + " )");
		}
		sk.printTitle(0, "End");
	}
	
	/**
	 * Print 1000 characters by codepoint after start point.
	 * @param startCodepoint
	 */
	public void showChar(String startCodepoint){
		h.help(startCodepoint, "<start-codepoint>: To print next 1000 char");
		for(int i=Integer.valueOf(startCodepoint);i<(Integer.valueOf(startCodepoint)+1000);i++){
			char[] chars = Character.toChars(i);
			sk.printResult(0, false, i+" => ");
			for(char c:chars){
				System.out.println(c);
			}
		}
		sk.printTitle(0, "End");
	}
	
	// CREATOR //
	
	public void createVolumeInSubnet(String name, String vcnName, String subnetName, String sizeGB, String profile) throws Exception{
		h.help(name, "<name> <vcn-name> <subnet-name> <size-in-GB> <profile>");
		Blockstorage bs = Client.getBlockstorageClient(profile);
		VirtualNetwork vn = Client.getVirtualNetworkClient(profile);
		UtilBlockStorage ubs = new UtilBlockStorage();
		UtilNetwork un = new UtilNetwork();
		String compartmentId = Config.getMyCompartmentId(profile);
		Subnet sn = un.getSubnetByName(vn, subnetName, un.getVcnIdByName(vn, vcnName, compartmentId), compartmentId);
		Volume v = ubs.createVolume(bs, name, sn.getAvailabilityDomain(), Long.valueOf(sizeGB), compartmentId);
		h.waitForVolumeStatus(bs, v.getId(), Volume.LifecycleState.Available, "Creating volume", false);
		
		this.showVolume(vcnName, profile);
	}
	
	/**
	 * Launch a new VM instance.
	 * @param name
	 * @param vcnName
	 * @param subnetName
	 * @param imageName
	 * @param shapeName
	 * @param userdataFilePath
	 * @param profile
	 * @throws Exception
	 */
	public void createVmInstance(String name, String vcnName, String subnetName, String imageName, String shapeName, String userdataFilePath, String profile) throws Exception{
		h.help(name, "<name> <vcn-name> <subnet-name> <image-name> <shape-name> <user-data-file-path> <profile>");
		sk.printTitle(0, "Creating VM");
		Compute c = Client.getComputeClient(profile);
		UtilCompute uc = new UtilCompute();
		VirtualNetwork vn = Client.getVirtualNetworkClient(profile);
		UtilNetwork un = new UtilNetwork();
		String compartmentId = Config.getMyCompartmentId(profile);
		String subnetId = un.getSubnetIdByName(vn, subnetName, un.getVcnIdByName(vn, vcnName, compartmentId), compartmentId);
		String imageId = uc.getImageIdByName(c, imageName, compartmentId);
		GetInstanceResponse gis = uc.createVmInstance(c, compartmentId, subnetId, name, imageId, shapeName, Config.publicKeyToString(profile), un.getAdBySubnetId(vn, subnetId), 
				userdataFilePath.equalsIgnoreCase("null")?null:h.base64EncodeFromFile(userdataFilePath), 
				Instance.LifecycleState.Running);
		sk.printResult(0,true,gis.getInstance());
		if(imageName.toLowerCase().contains("windows")){
			InstanceCredentials ic = c.getWindowsInstanceInitialCredentials(GetWindowsInstanceInitialCredentialsRequest.builder().instanceId(gis.getInstance().getId()).build()).getInstanceCredentials();
			sk.printResult(0, true, "Windows Login: "+ic.getUsername()+"/"+ic.getPassword());
		}
		sk.printTitle(0, "End");
	}
	
	/**
	 * Add a listener to existing load balancer.
	 * @param name
	 * @param protocol
	 * @param port
	 * @param lbName
	 * @param backendSetName
	 * @param profile
	 * @throws Exception
	 */
	public void addLbListener(String name, String protocol, String port, String lbName, String backendSetName, String profile) throws Exception{
		h.help(name, "<listener-name> <protocol> <port> <load-balancer-name> <backend-set-name> <profile>");
		sk.printTitle(0, "Add Listener "+name+" to Load Balancer "+lbName+" BackendSet "+backendSetName);
		String compartmentId = Config.getMyCompartmentId(profile);
		LoadBalancer lb = Client.getLoadBalancerClient(profile);
		UtilLB ulb = new UtilLB();
		String lbId = ulb.getLoadBalancerIdByName(lb, lbName, compartmentId);
		ulb.addListenerForLoadBalancer(lb, lbName, protocol, Integer.valueOf(port), lbId, backendSetName);
		sk.printTitle(0, "End");
	}
	
	/**
	 * Add a backend server to existing load balancer.
	 * @param instanceName
	 * @param instancePort
	 * @param lbName
	 * @param backendSetName
	 * @param profile
	 * @throws Exception
	 */
	public void addLbBackend(String instanceName, String instancePort, String lbName, String backendSetName, String profile) throws Exception{
		h.help(instanceName, "<backend-instance-name> <load-balancer-name> <backendset-name> <profile>");
		sk.printTitle(0, "Add Instance "+instanceName+" to Load Balancer "+lbName+" BackendSet "+backendSetName);
		String compartmentId = Config.getMyCompartmentId(profile);
		LoadBalancer lb = Client.getLoadBalancerClient(profile);
		UtilLB ulb = new UtilLB();
		Compute c = Client.getComputeClient(profile);
		UtilCompute uc = new UtilCompute();
		VirtualNetwork vn = Client.getVirtualNetworkClient(profile);
		String instanceId = uc.getInstanceIdByName(c, instanceName, compartmentId);
		String backendIpAddress = uc.getPrivateIpByInstanceId(c, vn, instanceId, compartmentId).get(0);
		String lbId = ulb.getLoadBalancerIdByName(lb, lbName, compartmentId);
		ulb.addBackendToBackendSet(lb, backendSetName, lbId, backendIpAddress, Integer.valueOf(instancePort));
		sk.printTitle(0, "End");
	}
	
	/**
	 * Add a backend set to existing load balancer.
	 * @param name
	 * @param vcnName
	 * @param lbName
	 * @param lbPolicy
	 * @param hcProtocol
	 * @param hcPort
	 * @param hcUrlPath
	 * @param profile
	 * @throws Exception
	 */
	public void addLbBackendSet(String name, String vcnName, String lbName, String lbPolicy, String hcProtocol, String hcPort, String hcUrlPath, String profile) throws Exception{
		h.help(name, "<name> <vcn-name> <load-balancer-name> <policy: ROUND_ROBIN|LEAST_CONNECTIONS|IP_HASH> <health-check-protocol> <health-check-port> <health-check-url-path> <profile>");
		sk.printTitle(0, "Create Load Balancer Backend Set - "+name);
		LoadBalancer lb = Client.getLoadBalancerClient(profile);
		UtilLB ulb = new UtilLB();
		String compartmentId = Config.getMyCompartmentId(profile);
		ulb.addBackendSetForLoadBalancer(lb, name, ulb.getLoadBalancerIdByName(lb, lbName, compartmentId), 
				lbPolicy, hcProtocol, Integer.valueOf(hcPort), hcUrlPath);
		sk.printTitle(0, "End");
	}
	
	/**
	 * Create a load balancer.
	 * @param name
	 * @param vcnName
	 * @param subnet1Name
	 * @param subnet2Name
	 * @param shape
	 * @param profile
	 * @throws Exception
	 */
	public void createLb(String name, String vcnName, String subnet1Name, String subnet2Name, String shape, String profile) throws Exception{
		h.help(name, "<name> <vcn-name> <subnet-1-name> <subnet-2-name> <shape: 100Mbps|400Mbps|8000Mbps> <profile>");
		sk.printTitle(0, "Create Load Balancer - "+name);
		LoadBalancer lb = Client.getLoadBalancerClient(profile);
		VirtualNetwork vn = Client.getVirtualNetworkClient(profile);
		UtilLB ulb = new UtilLB();
		UtilNetwork un = new UtilNetwork();
		String compartmentId = Config.getMyCompartmentId(profile);
		String vcnId = un.getVcnIdByName(vn, vcnName, compartmentId);
		String subnet1Id = un.getSubnetIdByName(vn, subnet1Name, vcnId, compartmentId);
		String subnet2Id = un.getSubnetIdByName(vn, subnet2Name, vcnId, compartmentId);
		ulb.createLoadBalancer(lb, name, shape, subnet1Id, subnet2Id,compartmentId);
		sk.printResult(0, true, "Load Balancer "+name+" created");
		sk.printTitle(0, "End");
	}
	
	/**
	 * Create a new VCN by CIDR.
	 * @param name
	 * @param cidr
	 * @param profile
	 * @throws Exception
	 */
	public void createVcn(String name, String cidr, String profile) throws Exception {
		h.help(name, "<name> <cidr> <profile>");
		sk.printTitle(0, "Create VCN - "+name);
		VirtualNetwork vn = Client.getVirtualNetworkClient(profile);
		UtilNetwork un = new UtilNetwork();
		Vcn vcn = un.createVcn(vn, Config.getMyCompartmentId(profile), name, cidr, "Jiu - "+name);
		sk.printResult(0, true, vcn.getCidrBlock()+", "+vcn.getVcnDomainName()+", "+vcn.getId()+" CREATED.");
		sk.printTitle(0, "End");
	}
	
	// SETUP //
	
	public void attachVolume(String volumeName, String instanceName, String profile) throws NumberFormatException, IOException{
		h.help(volumeName, "<volume-name> <instance-name> <profile>");
		sk.printTitle(0, "Attach volume "+volumeName+" to "+instanceName);
		Compute c = Client.getComputeClient(profile);
		Blockstorage bs = Client.getBlockstorageClient(profile);
		UtilCompute uc = new UtilCompute();
		UtilBlockStorage ubs = new UtilBlockStorage();
		String compartmentId = Config.getMyCompartmentId(profile);
		Instance i = uc.getInstanceByName(c, instanceName, compartmentId);
		Volume v = ubs.getVolumeByName(bs, volumeName, i.getAvailabilityDomain(), compartmentId);
		IScsiVolumeAttachment va = uc.attachVolumeIscsi(c, volumeName+"-"+instanceName, i.getId(), v.getId());
		String aid = va.getId();
		VolumeAttachment check = null;
		while(true){
			h.wait(1000);
			check = c.getVolumeAttachment(GetVolumeAttachmentRequest.builder().volumeAttachmentId(aid).build()).getVolumeAttachment();
			sk.printResult(0,true,check.getLifecycleState().getValue());
			if(check.getLifecycleState().equals(VolumeAttachment.LifecycleState.Attached)){
				break;
			}
		}
		sk.printTitle(0,"Run following scripts on instance "+instanceName+" to connect the volume");
		va = (IScsiVolumeAttachment) c.getVolumeAttachment(GetVolumeAttachmentRequest.builder().volumeAttachmentId(aid).build()).getVolumeAttachment();
		this.printISCSIInfo(va);
	}
	
	private void printISCSIInfo(IScsiVolumeAttachment va){
		String iqn = va.getIqn();
		String ipv4 = va.getIpv4();
		String port = va.getPort()==null?"3260":va.getPort().toString();
		StringBuffer script = new StringBuffer();
		script.append("sudo iscsiadm -m node -o new -T "+iqn+" -p "+ipv4+":"+port+"\n");
		script.append("sudo iscsiadm -m node -T "+iqn+" -o update -n node.startup -v automatic\n");
		script.append("sudo iscsiadm -m node -T "+iqn+" -p "+ipv4+":"+port+" -l\n");
		String output = new String(script);
		System.out.println(output);
	}
	
	
	/**
	 * Send actions to vm instance.
	 * @param name
	 * @param action
	 * @param profile
	 * @throws Exception
	 */
	public void vmInstanceActionByName(String name, String action, String profile) throws Exception{
		h.help(name, "<name> <action: start|stop|reset|softreset> <profile>");
		sk.printTitle(0, "Executing VM instance action - "+action+" for "+name);
		Compute c = Client.getComputeClient(profile);
		UtilCompute uc = new UtilCompute();
		String compartmentId = Config.getMyCompartmentId(profile);
		String instanceId = uc.getInstanceIdByName(c, name, compartmentId);
		c.instanceAction(InstanceActionRequest.builder()
				.instanceId(instanceId)
				.action(action).build()).getInstance();
		Instance.LifecycleState state = null;
		if(action.equals("start")||action.equals("reset")||action.equals("softreset")){
			state = Instance.LifecycleState.Running;
		}
		else if(action.equals("stop")){
			state = Instance.LifecycleState.Stopped;
		}
		h.waitForInstanceStatus(c, instanceId, state, "waiting", false);
		sk.printTitle(0, "End");
	}
	
	/**
	 * Change backend drain status for LB.
	 * @param lbName
	 * @param backendSetName
	 * @param backendName
	 * @param drain
	 * @param profile
	 * @throws Exception
	 */
	public void changeLbBackendDrain(String lbName, String backendSetName, String backendName, String drain, String profile) throws Exception{
		h.help(lbName, "<load-balancer-name> <backendset-name> <backend-name> <drain: true|false> <profile>");
		sk.printTitle(0, "Change "+backendName+" drain to "+drain);
		LoadBalancer lb = Client.getLoadBalancerClient(profile);
		UtilLB ulb = new UtilLB();
		String compartmentId = Config.getMyCompartmentId(profile);
		String lbId = ulb.getLoadBalancerIdByName(lb, lbName, compartmentId);
		ulb.changeBackendSetting(lb, lbId, backendSetName, backendName, null,drain.equals("true")?true:false,null,null);
		sk.printTitle(0, "End");
	}
	
	/**
	 * Change backend weight for LB.
	 * @param lbName
	 * @param backendSetName
	 * @param backendName
	 * @param newWeight
	 * @param profile
	 * @throws Exception
	 */
	public void changeLbBackendWeight(String lbName, String backendSetName, String backendName, String newWeight, String profile) throws Exception{
		h.help(lbName, "<load-balancer-name> <backendset-name> <backend-name> <new-weight> <profile>");
		sk.printTitle(0, "Change "+backendName+" to weight "+newWeight);
		LoadBalancer lb = Client.getLoadBalancerClient(profile);
		UtilLB ulb = new UtilLB();
		String compartmentId = Config.getMyCompartmentId(profile);
		String lbId = ulb.getLoadBalancerIdByName(lb, lbName, compartmentId);
		ulb.changeBackendSetting(lb, lbId, backendSetName, backendName, Integer.valueOf(newWeight),null,null,null);
		sk.printTitle(0, "End");
	}
	
	
	
	
	/**
	 * Create several public accessible vm instances in a new VCN. Shape: VM.Standard1.1
	 * @param namePrefix
	 * @param count
	 * @param profile
	 * @throws Exception
	 */
	public void demoCreateVmGroup(String namePrefix, String count, String profile) throws Exception{
		h.help(namePrefix, "<vm-name-prefix> <vm-count> <profile-name>");
		String prefix = namePrefix!=null?namePrefix:"bgltest";
		sk.printTitle(0, "Create VM group - "+prefix);
		Identity id = Client.getIamClient(profile);
		VirtualNetwork vn = Client.getVirtualNetworkClient(profile);
		UtilIam ui = new UtilIam();
		UtilNetwork un = new UtilNetwork();
		String compartmentId = Config.getMyCompartmentId(profile);
		// VCN
		Vcn vcn = un.createVcn(vn, compartmentId, prefix+"vcn", "10.10.0.0/16", "Jiu - "+prefix+"vcn");
		// IGW
		InternetGateway igw = un.createIgw(vn, compartmentId, vcn.getId(), prefix+"igw");
		// Public Route Table
		RouteRule rr = RouteRule.builder().cidrBlock("0.0.0.0/0").networkEntityId(igw.getId()).build();
		List<RouteRule> rrs = new ArrayList<RouteRule>(); rrs.add(rr);
		RouteTable publicRouteTable = un.createRouteTable(vn, compartmentId, vcn.getId(), prefix+"rt-public", rrs);
		
		List<EgressSecurityRule> erAllowAll = un.getEgressAllowAllRules();
		// Bastion SecList
		List<IngressSecurityRule> irBastion = un.getBastionIngressSecurityRules("10.10.0.0/16");
		SecurityList bastionSecList = un.createSecList(vn, compartmentId, vcn.getId(), prefix+"seclist-bastion-public", irBastion, erAllowAll);
		List<String> bastionSecLists = new ArrayList<String>();
		bastionSecLists.add(bastionSecList.getId());
		
		// Subnet
		List<AvailabilityDomain> ads = ui.getAllAd(id, profile);
		String snpub = prefix+"snpub";
		Subnet[] pubSubnets = new Subnet[1];
		int cnt = Integer.valueOf(count);
		for(int i=0;i<1;i++){
			pubSubnets[i] = un.createSubnet(vn, compartmentId, vcn.getId(), 
				snpub+i, snpub+i, 
				"10.10."+i+".0/24", ads.get(i).getName(), 
				vcn.getDefaultDhcpOptionsId(), publicRouteTable.getId(), bastionSecLists, "Jiu - "+snpub+i,
				false);
		}
		Compute c = Client.getComputeClient(profile);
		UtilCompute uc = new UtilCompute();
		Image vmImage = null;
		for(Image img:uc.getAllImage(c, compartmentId)){
			if(img.getOperatingSystem().equals("Oracle Linux") && img.getOperatingSystemVersion().equals("7.3") && img.getDisplayName().contains("2017.04.18-0")){
				vmImage = img;
				break;
			}
		}
		String[] instanceIds = new String[cnt];
		for(int i=0;i<cnt;i++){
			instanceIds[i] = uc.createVmInstance(c, compartmentId, pubSubnets[0].getId(), 
				prefix+"node"+i, vmImage.getId(), "VM.Standard1.1", 
				Config.publicKeyToString(profile), ads.get(0).getName(), h.base64EncodeFromFile(Config.getConfigFileReader(profile).get("bastion_user_data")), Instance.LifecycleState.Provisioning).getInstance().getId();
		}
		//this.showVcn(prefix+"vcn", profile);
		sk.printResult(0, true, "Waiting running status");
		h.silentWaitForInstanceStatus(c, instanceIds, Instance.LifecycleState.Running, false);
		sk.printTitle(0, "End");
	}
	
	/**
	 * Create a simple infra only includes 1 VCN, 1 Subnet, 1 Bastion.
	 * @param namePrefix
	 * @param profile
	 * @throws Exception
	 */
	public void demoCreateSingleBastionInfra(String namePrefix, String profile) throws Exception{
		h.help(namePrefix, "<resource-name-prefix> <profile-name>");
		String prefix = namePrefix!=null?namePrefix:"bgltest";
		sk.printTitle(0, "Create Infrastructure - "+prefix);
		Identity id = Client.getIamClient(profile);
		VirtualNetwork vn = Client.getVirtualNetworkClient(profile);
		UtilIam ui = new UtilIam();
		UtilNetwork un = new UtilNetwork();
		String compartmentId = Config.getMyCompartmentId(profile);
		// VCN
		Vcn vcn = un.createVcn(vn, compartmentId, prefix+"vcn", "10.8.0.0/16", "Jiu - "+prefix+"vcn");
		// IGW
		InternetGateway igw = un.createIgw(vn, compartmentId, vcn.getId(), prefix+"igw");
		// Public Route Table
		RouteRule rr = RouteRule.builder().cidrBlock("0.0.0.0/0").networkEntityId(igw.getId()).build();
		List<RouteRule> rrs = new ArrayList<RouteRule>(); rrs.add(rr);
		RouteTable publicRouteTable = un.createRouteTable(vn, compartmentId, vcn.getId(), prefix+"rt-public", rrs);
		
		List<EgressSecurityRule> erAllowAll = un.getEgressAllowAllRules();
		// Bastion SecList
		List<IngressSecurityRule> irBastion = un.getBastionIngressSecurityRules("10.8.0.0/16");
		SecurityList bastionSecList = un.createSecList(vn, compartmentId, vcn.getId(), prefix+"seclist-bastion-public", irBastion, erAllowAll);
		List<String> bastionSecLists = new ArrayList<String>();
		bastionSecLists.add(bastionSecList.getId());
		
		// Subnet
		List<AvailabilityDomain> ads = ui.getAllAd(id, profile);
		String snpub = prefix+"snpub";
		Subnet[] pubSubnets = new Subnet[1];
		for(int i=0;i<1;i++){
			pubSubnets[i] = un.createSubnet(vn, compartmentId, vcn.getId(), 
				snpub+i, snpub+i, 
				"10.8."+i+".0/24", ads.get(i).getName(), 
				vcn.getDefaultDhcpOptionsId(), publicRouteTable.getId(), bastionSecLists, "Jiu - "+snpub+i,
				false);
		}
		Compute c = Client.getComputeClient(profile);
		UtilCompute uc = new UtilCompute();
		Image vmImage = null;
		for(Image img:uc.getAllImage(c, compartmentId)){
			if(img.getOperatingSystem().equals("Oracle Linux") && img.getOperatingSystemVersion().equals("7.3") && img.getDisplayName().contains("2017.04.18-0")){
				vmImage = img;
				break;
			}
		}
		uc.createVmInstance(c, compartmentId, pubSubnets[0].getId(), 
				prefix+"bastion", vmImage.getId(), "VM.Standard1.1", 
				Config.publicKeyToString(profile), ads.get(0).getName(), h.base64EncodeFromFile(Config.getConfigFileReader(profile).get("bastion_user_data")), Instance.LifecycleState.Running);
		this.showVcn(prefix+"vcn", profile);
		sk.printTitle(0, "End");
	}
	
	/**
	 * Create a new infrastructure of simple web servers for demo purpose.
	 * 
	 * [Bastion]-[LB0]--[LB1]	Public
	 *  |----------|------|  
	 * [ ]-------[Web0]-[Web1]	Web Internal
	 * [ ]-------[    ]-[    ]	NoSQL Internal
	 * 
	 * 1. Compartment read from configuration file.
	 * 2. One VCN.
	 * 3. One IGW.
	 * 4. One Public Route Table.
	 * 5. One Bastion security list with rules.
	 * 6. One Web Server security list with rules.
	 * 7. Nine Subnets.
	 * 	a. snpub0 => bastion seclist.
	 * 	b. snpub1 => public lb seclist.
	 *  c. supub2 => public lb seclist.
	 *  d. subpri0 => webserver seclist.
	 *  e. subpri1 => webservert seclist.
	 *  f. subpri2 => webservert seclist.
	 *  g. subnosql0 => nosql seclist.
	 *  h. subnosql1 => nosql seclist.
	 *  i. subnosql2 => nosql seclist.
	 * 8. One Oracle Linux 7.3 Bastion. Public 
	 * 9. Three Oracle Linux 7.3 NoSQL DB. Private
	 * 10. Two Oracle Linux 7.3 Web Server. Private.
	 * 11. One Load Balancer.
	 * 12. Registering three Web Servers to Load Balancer.
	 * @param profile
	 * @throws Exception
	 */
	public void demoCreateSimpleWebInfra(String namePrefix, String profile) throws Exception{
		h.help(namePrefix, "<resource-name-prefix> <profile-name>");
		String prefix = namePrefix!=null?namePrefix:"bgltest";
		sk.printTitle(0, "Create Infrastructure - "+prefix);
		Identity id = Client.getIamClient(profile);
		VirtualNetwork vn = Client.getVirtualNetworkClient(profile);
		UtilIam ui = new UtilIam();
		UtilNetwork un = new UtilNetwork();
		String compartmentId = Config.getMyCompartmentId(profile);
		// VCN
		Vcn vcn = un.createVcn(vn, compartmentId, prefix+"vcn", "10.7.0.0/16", "Jiu - "+prefix+"vcn");
		// IGW
		InternetGateway igw = un.createIgw(vn, compartmentId, vcn.getId(), prefix+"igw");
		// Public Route Table
		RouteRule rr = RouteRule.builder().cidrBlock("0.0.0.0/0").networkEntityId(igw.getId()).build();
		List<RouteRule> rrs = new ArrayList<RouteRule>(); rrs.add(rr);
		RouteTable publicRouteTable = un.createRouteTable(vn, compartmentId, vcn.getId(), prefix+"rt-public", rrs);
		
		List<EgressSecurityRule> erAllowAll = un.getEgressAllowAllRules();
		// Bastion SecList
		List<IngressSecurityRule> irBastion = un.getBastionIngressSecurityRules("10.7.0.0/16");
		SecurityList bastionSecList = un.createSecList(vn, compartmentId, vcn.getId(), prefix+"seclist-bastion-public", irBastion, erAllowAll);
		List<String> bastionSecLists = new ArrayList<String>();
		bastionSecLists.add(bastionSecList.getId());
		// Webserver SecList
		List<IngressSecurityRule> irInternalWebserver = un.getInternalWebserverIngressSecurityRules(vcn.getCidrBlock());
		SecurityList webserverSecList = un.createSecList(vn, compartmentId, vcn.getId(), prefix+"seclist-webserver-internal", irInternalWebserver, erAllowAll);
		List<String> webserverSecLists = new ArrayList<String>();
		webserverSecLists.add(webserverSecList.getId());
		// NoSQL SecList
		List<IngressSecurityRule> irInternalNoSql = un.getInternalNoSqlIngressSecurityRules(vcn.getCidrBlock());
		SecurityList nosqlSecList = un.createSecList(vn, compartmentId, vcn.getId(), prefix+"seclist-nosql-internal", irInternalNoSql, erAllowAll);
		List<String> nosqlSecLists = new ArrayList<String>();
		nosqlSecLists.add(nosqlSecList.getId());
		// Public LoadBalancer SecList
		List<IngressSecurityRule> irPublicLoadBalancer = un.getPublicLoadBalancerIngressSecurityRules(new int[]{80,443});
		SecurityList publicLoadBalancerSecList = un.createSecList(vn, compartmentId, vcn.getId(), prefix+"seclist-loadbalancer-public", irPublicLoadBalancer, erAllowAll);
		List<String> publicLoadBalancerSecLists = new ArrayList<String>();
		publicLoadBalancerSecLists.add(publicLoadBalancerSecList.getId());
		
		// 9 Subnets.
		List<AvailabilityDomain> ads = ui.getAllAd(id, profile);
		// Public
		String snpub = prefix+"pub";
		Subnet[] pubSubnets = new Subnet[3];
		for(int i=0;i<3;i++){
			pubSubnets[i] = un.createSubnet(vn, compartmentId, vcn.getId(), 
				snpub+i, snpub+i, 
				"10.7."+i+".0/24", ads.get(i).getName(), 
				vcn.getDefaultDhcpOptionsId(), publicRouteTable.getId(), i==0?bastionSecLists:publicLoadBalancerSecLists, "Jiu - "+snpub+i,
				false);
		}
		// Private Web
		String snpri = prefix+"webpri";
		Subnet[] priSubnets = new Subnet[3];
		for(int i=0;i<3;i++){
			priSubnets[i] = un.createSubnet(vn, compartmentId, vcn.getId(), 
				snpri+i, snpri+i, 
				"10.7."+(i+3)+".0/24", ads.get(i).getName(), 
				//vcn.getDefaultDhcpOptionsId(), vcn.getDefaultRouteTableId(), webserverSecLists, "Jiu - "+snpub+i); //TODO change to DefaultRouteTableId() when NAT is ready.
				vcn.getDefaultDhcpOptionsId(), publicRouteTable.getId(), webserverSecLists, "Jiu - "+snpri+i,
				false); // TODO change to true when NAT is ready.
		}
		// Private NoSQL
		String nosqlsnpri = prefix+"dbpri";
		Subnet[] nosqlpriSubnets = new Subnet[3];
		for(int i=0;i<3;i++){
			nosqlpriSubnets[i] = un.createSubnet(vn, compartmentId, vcn.getId(), 
				nosqlsnpri+i, nosqlsnpri+i, 
				"10.7."+(i+6)+".0/24", ads.get(i).getName(), 
				//vcn.getDefaultDhcpOptionsId(), vcn.getDefaultRouteTableId(), nosqlSecLists, "Jiu - "+nosqlsnpri+i); //TODO change to DefaultRouteTableId() when NAT is ready.
				vcn.getDefaultDhcpOptionsId(), publicRouteTable.getId(), nosqlSecLists, "Jiu - "+nosqlsnpri+i,
				false); // TODO change to true when NAT is ready.
		}
		// 1 Bastion.
		Compute c = Client.getComputeClient(profile);
		UtilCompute uc = new UtilCompute();
		Image vmImage = null;
		for(Image img:uc.getAllImage(c, compartmentId)){
			if(img.getOperatingSystem().equals("Oracle Linux") && img.getOperatingSystemVersion().equals("7.3") && img.getDisplayName().contains("2017.04.18-0")){
				vmImage = img;
				break;
			}
		}
		GetInstanceResponse 
			bastionGis = uc.createVmInstance(c, compartmentId, pubSubnets[0].getId(), 
				prefix+"bastion", vmImage.getId(), "VM.Standard1.1", 
				Config.publicKeyToString(profile), ads.get(0).getName(), h.base64EncodeFromFile(Config.getConfigFileReader(profile).get("bastion_user_data")), Instance.LifecycleState.Provisioning);
		// 2 Web Servers.
		GetInstanceResponse[] webGis = new GetInstanceResponse[2];
		for(int i=0;i<2;i++){
			webGis[i]= uc.createVmInstance(c, compartmentId, priSubnets[i+1].getId(), 
				prefix+"web"+i, vmImage.getId(), "VM.Standard1.1", 
				Config.publicKeyToString(profile), ads.get(i+1).getName(), h.base64EncodeFromFile(Config.getConfigFileReader(profile).get("webserver"+(i)+"_user_data")), Instance.LifecycleState.Provisioning);
				//Config.publicKeyToString(profile), ads.get(i).getName(), null, Instance.LifecycleState.Provisioning);	
		}
		// 1 Load Balancer.
		LoadBalancer lb = Client.getLoadBalancerClient(profile);
		UtilLB ulb = new UtilLB();
		String lbId = ulb.createLoadBalancer(lb, prefix+"lb", "100Mbps", pubSubnets[1].getId(), pubSubnets[2].getId(), compartmentId).getLoadBalancerId();
		String lbLocationInfo = pubSubnets[1].getCidrBlock()+"@"+pubSubnets[1].getAvailabilityDomain()+", "+pubSubnets[2].getCidrBlock()+"@"+pubSubnets[2].getAvailabilityDomain();
		String backendSetName = prefix+"lbbes";
		
		WorkRequest wr1 = null;
		do{
			wr1 = ulb.addBackendSetForLoadBalancer(lb, backendSetName, lbId, "ROUND_ROBIN", "HTTP", 80, "/index.html");
			List<WorkRequestError> errors = wr1.getErrorDetails();
			if(errors!=null && errors.size()>0){
				for(WorkRequestError error:errors){
					sk.printResult(3, true, error.getMessage());
				}
			}
		}while(wr1.getErrorDetails()!=null && wr1.getErrorDetails().size()>0);
		
		WorkRequest wr2 = null;
		do{
			wr2 = ulb.addListenerForLoadBalancer(lb, prefix+"lbl", "HTTP", 80, lbId, backendSetName);
			List<WorkRequestError> errors = wr2.getErrorDetails();
			if(errors!=null && errors.size()>0){
				for(WorkRequestError error:errors){
					sk.printResult(3, true, error.getMessage());
				}
			}
		}while(wr2.getErrorDetails()!=null && wr2.getErrorDetails().size()>0);
		
		String[] instanceIds = new String[2];
		for(int i=0;i<2;i++){
			GetInstanceResponse r = webGis[i];
			instanceIds[i]=r.getInstance().getId();
		}
		// Register 2 webservers to Load Balancer.
		h.silentWaitForInstanceStatus(c, instanceIds, Instance.LifecycleState.Running, false);
		for(int i=0;i<2;i++){
			ulb.addBackendToBackendSet(lb, backendSetName, lbId, uc.getPrivateIpByInstanceId(c, vn, instanceIds[i], compartmentId).get(0), 80);
		}
		// Output
		sk.printTitle(0,"Output");
		String regionId = Region.fromRegionId(Config.getConfigFileReader(profile.toUpperCase()).get("region")).getRegionId();
		com.oracle.bmc.loadbalancer.model.LoadBalancer l = lb.getLoadBalancer(GetLoadBalancerRequest.builder().loadBalancerId(lbId).build()).getLoadBalancer();
		sk.printResult(1, true, "Load Balancer@Region: "+regionId+", "+l.getIpAddresses().get(0)+", "+l.getDisplayName()+", "+lbLocationInfo);
		for(int i=0;i<2;i++){
			GetInstanceResponse g = webGis[i];
			List<Vnic> wvnics = uc.getVnicByInstanceId(c, vn, g.getInstance().getId(), compartmentId);
			for(Vnic v:wvnics){
				sk.printResult(1, true, "Webserver@AD: "+v.getAvailabilityDomain()+", HostName: "+v.getHostnameLabel()+", "+v.getPrivateIp()+", "+v.getPublicIp());
			}
		}
		String bastionId = bastionGis.getInstance().getId();
		h.silentWaitForInstanceStatus(c, new String[]{bastionId}, Instance.LifecycleState.Running, false);
		List<Vnic> vnics = uc.getVnicByInstanceId(c, vn, bastionId, compartmentId);
		for(Vnic v:vnics){
			sk.printResult(1, true, "Bastion@AD: "+v.getAvailabilityDomain()+", HostName: "+v.getHostnameLabel()+", "+v.getPrivateIp()+", "+v.getPublicIp());
		}
		//this.showVcn(prefix+"vcn", profile);
		sk.printTitle(0, "Infrastructure - "+prefix+" created.");
	}
	
	/**
	 * Open SSH for bastion seclist.
	 * @param profile
	 * @throws Exception
	 */
	public void openBastionSsh(String profile) throws Exception{
		h.help(profile, "<profile-name>");
		VirtualNetwork vn = Client.getVirtualNetworkClient(profile);
		UtilNetwork un =  new UtilNetwork();
		String ocid = Config.getConfigFileReader(profile).get("bastionseclist");
		un.secListAddIngressBastionTcpPort(vn, ocid,"22");
		sk.printResult(0, true, "SSH for Bastion seclist "+ocid+" opened.");
	}
	
	/**
	 * Close SSH for bastion seclist.
	 * @param profile
	 * @throws Exception
	 */
	public void closeBastionSsh(String profile) throws Exception{
		h.help(profile, "<profile-name>");
		VirtualNetwork vn = Client.getVirtualNetworkClient(profile);
		UtilNetwork un =  new UtilNetwork();
		String ocid = Config.getConfigFileReader(profile).get("bastionseclist");
		un.secListRemoveIngressBastionTcpPort(vn, ocid,"22");
		sk.printResult(0, true, "SSH for Bastion seclist "+ocid+" closed.");
	}
	
	// DESTROYER //
	
	public void destroyVolume(String volumeName, String availabilityDomain, String profile) throws Exception{
		h.help(volumeName, "<volume-name> <availability-domain> <profile>");
		sk.printTitle(0, "Delete volume named "+volumeName);
		String compartmentId = Config.getMyCompartmentId(profile);
		Blockstorage bs = Client.getBlockstorageClient(profile);
		UtilBlockStorage ubs = new UtilBlockStorage();
		Volume v = ubs.getVolumeByName(bs, volumeName, availabilityDomain, compartmentId);
		ubs.killVolumeById(bs, v.getId());
		h.waitForVolumeStatus(bs, v.getId(), Volume.LifecycleState.Terminated, "Removing volume", true);
		sk.printTitle(0, "End");
	}
	
	public void destroyLbByName(String lbName, String profile) throws Exception{
		h.help(lbName, "<load-balancer-name> <profile>");
		sk.printTitle(0, "Delete Load Balancer named "+lbName+" in profile compartment");
		UtilLB ulb = new UtilLB();
		LoadBalancer lb = Client.getLoadBalancerClient(profile);
		String compartmentId = Config.getMyCompartmentId(profile);
		ulb.deleteLoadBalancerByName(lb, lbName, compartmentId);
		sk.printTitle(0, "End");
	}
	
	/**
	 * Delete instances in selected VCN by name prefix.
	 * @param namePrefix
	 * @param vcnName
	 * @param profile
	 * @throws Exception
	 */
	public void destroyInstanceInVcn(String namePrefix, String vcnName, String profile) throws Exception{
		h.help(namePrefix, "<instance-name-refix> <vcn-name> <profile>");
		sk.printTitle(0, "Delete Instances with Name Prefix "+namePrefix+" in VCN "+vcnName);
		VirtualNetwork vn = Client.getVirtualNetworkClient(profile);
		UtilNetwork un = new UtilNetwork();
		String compartmentId = Config.getMyCompartmentId(profile);
		String vcnId = un.getVcnIdByName(vn, vcnName, compartmentId);
		Compute c = Client.getComputeClient(profile);
		UtilCompute uc = new UtilCompute();
		List<Instance> instances = c.listInstances(ListInstancesRequest.builder().compartmentId(compartmentId).build()).getItems();
		TreeMap<String,String> snToInstance = new TreeMap<String,String>(); 
		for(Instance vm:instances){
			if(vm.getDisplayName().startsWith(namePrefix) && !vm.getLifecycleState().getValue().equals(Instance.LifecycleState.Terminated.getValue())){
				List<VnicAttachment> vnicAttachments = c.listVnicAttachments(ListVnicAttachmentsRequest.builder().compartmentId(compartmentId).instanceId(vm.getId()).build()).getItems();
				for(VnicAttachment va:vnicAttachments){
					snToInstance.put(va.getSubnetId(), vm.getId());
				}
			}
		}
		List<Subnet> sns = vn
				.listSubnets(ListSubnetsRequest.builder().compartmentId(compartmentId).vcnId(vcnId).build())
				.getItems();
		for(Subnet sn:sns){
			for(String snId:snToInstance.keySet()){
				if(snId.equals(sn.getId())){
					uc.killInstanceById(c, compartmentId, snToInstance.get(snId));
				}
			}
		}
		sk.printTitle(0, "End");
	}
	
	/**
	 * Delete route tables in selected VCN by name prefix.
	 * @param namePrefix
	 * @param vcnName
	 * @param profile
	 * @throws Exception
	 */
	public void destroyRouteTableInVcn(String namePrefix, String vcnName, String profile) throws Exception{
		h.help(namePrefix, "<route-table-name-refix> <vcn-name> <profile>");
		sk.printTitle(0, "Delete Route Table with Name Prefix "+namePrefix+" in VCN "+vcnName);
		VirtualNetwork vn = Client.getVirtualNetworkClient(profile);
		UtilNetwork un = new UtilNetwork();
		String compId = Config.getMyCompartmentId(profile);
		String vcnId = un.getVcnIdByName(vn, vcnName, compId);
		un.deleteRouteTableByNamePrefix(vn, compId, vcnId, namePrefix);
		sk.printTitle(0, "Route Table with name prefix "+namePrefix+" DESTROYED.");
	}
	
	/**
	 * Delete all SECLIST if matches the name prefix in profile compartment and specified VCN.
	 * @param namePrefix
	 * @param vcnName
	 * @param profile
	 * @throws Exception
	 */
	public void destroySecListInVcn(String namePrefix, String vcnName, String profile) throws Exception{
		h.help(namePrefix, "<seclist-name-refix> <vcn-name> <profile>");
		sk.printTitle(0, "Delete Security List with Name Prefix "+namePrefix+" in VCN "+vcnName);
		VirtualNetwork vn = Client.getVirtualNetworkClient(profile);
		UtilNetwork un = new UtilNetwork();
		String compId = Config.getMyCompartmentId(profile);
		String vcnId = un.getVcnIdByName(vn, vcnName, compId);
		un.deleteSecurityListByNamePrefix(vn, compId, vcnId, namePrefix);
		sk.printTitle(0, "SecList with name prefix "+namePrefix+" DESTROYED.");
	}
	
	/**
	 * Delete all VCN if matches the name prefix in profile compartment. Check README.MD for profile setting.
	 * @param namePrefix
	 * @param profile
	 * @throws Exception
	 */
	public void nukeVcn(String namePrefix, String profile) throws Exception{
		h.help(namePrefix, "<vcn-name-refix> <profile>");
		sk.printTitle(0, "Delete VCN with Name Prefix "+namePrefix);
		this.nullrayVcn(namePrefix, profile);
		VirtualNetwork vn = Client.getVirtualNetworkClient(profile);
		LoadBalancer lb = Client.getLoadBalancerClient(profile);
		UtilNetwork un = new UtilNetwork();
		Compute c = Client.getComputeClient(profile);
		un.deleteVcnByNamePrefix(lb, vn, c, Config.getMyCompartmentId(profile), namePrefix);
		sk.printTitle(0, "VCN with name prefix "+namePrefix+" DESTROYED.");
	}
	
	/**
	 * 
	 * @param namePrefix
	 * @param profile
	 * @throws Exception
	 */
	public void nullrayVcn(String namePrefix, String profile) throws Exception{
		h.help(namePrefix, "<vcn-name-refix> <profile>");
		sk.printTitle(0, "Nullray VCN vm instances with VCN Name Prefix "+namePrefix);
		VirtualNetwork vn = Client.getVirtualNetworkClient(profile);
		LoadBalancer lb = Client.getLoadBalancerClient(profile);
		UtilNetwork un = new UtilNetwork();
		Compute c = Client.getComputeClient(profile);
		un.deleteVcnVmInstanceByVcnNamePrefix(lb, vn, c, Config.getMyCompartmentId(profile), namePrefix);
		sk.printTitle(0, "VCN with name prefix "+namePrefix+" nullrayfied.");
	}
	
	/**
	 * REST call.
	 * @param method, lowercase
	 * @param apiVersion
	 * @param path
	 * @param resource
	 * @param parameter {p1:v1,p2:v2,p3:v3}
	 * @param profile
	 * @throws IOException
	 */
	//TODO
	public void restCall(String method, String apiVersion, String slashPath, String profile) throws IOException{
		h.help(method, "<method> <api-version> <slash-separated-path> <profile>");
		sk.printTitle(0, "/"+apiVersion+"/"+slashPath);
		String[] ret = UtilMain.restCall(method, apiVersion, slashPath.split("/"), profile);
		sk.printResult(0, true, "Response Headers:");
		System.out.println(ret[0]);
		sk.printResult(0, true, "Response Body:");
		System.out.println(ret[1]);
	}
	
	/**
	 * The real program.
	 * @param args
	 * @throws Exception
	 */
	public static void coreV2(String[] args) throws Exception {

		if (args == null || args.length == 0) {
			sk.printTitle(0,
					"Use -h option with following sub-command to get help, [] count shows how many parameters the sub-command requires to run:");
			sk.printResult(0, true, "SUB-COMMANDS:");
			TreeSet<String> ts = new TreeSet<String>();
			StringBuffer sb = null;
			Method[] allMethods = Jiu.class.getDeclaredMethods();
			ArrayList<Method> methods = new ArrayList<Method>();
			for (Method m : allMethods) {
				if (Modifier.isPublic(m.getModifiers()) && !m.getName().equals("test")) {
					methods.add(m);
				}
			}
			String mn = null;
			for (Method m : methods) {
				sb = new StringBuffer();
				mn = m.getName();
				if (SKIPPED_METHODS.contains(mn)) {
					continue;
				}
				sb.append(mn + ": ");
				int parameterCount = m.getParameterTypes().length;
				for (int i = 0; i < parameterCount; i++) {
					sb.append("[]");
				}
				ts.add(new String(sb));
			}
			for (String s : ts) {
				sk.printResult(1, true, s);
			}
			sk.printTitle(0,"End");
			return;
		}

		Jiu u = new Jiu();
		Class<?> clazz = u.getClass();
		Method[] allMethods = clazz.getDeclaredMethods();
		ArrayList<Method> methods = new ArrayList<Method>();
		for (Method m : allMethods) {
			if (Modifier.isPublic(m.getModifiers())) {
				methods.add(m);
			}
		}
		if (SKIPPED_METHODS.contains(args[0])) {
			// System.out.println("options unkown.");
			return;
		}
		for (Method m : methods) {
			if (m.getName().equals(args[0])) {
				// System.out.println(m.getName()+" :: "+args[0]);
				// Pass through #1: putItemToDdb, #2: deleteItemFromDdb
				if (args[0].equals("ddbDeleteItemString")) {
					String[] parameters = Arrays.copyOfRange(args, 1, args.length);
					if (parameters[0].equals("-h")) {
						System.out.println("<profile> <table-name> <pk-attr-name> <pk-attr-value> ...");
						return;
					}
					// u.ddbDeleteItemString(parameters);
					return;
				}
				if (args[0].equals("ddbPutItemString")) {
					String[] parameters = Arrays.copyOfRange(args, 1, args.length);
					if (parameters[0].equals("-h")) {
						System.out.println("<profile> <table-name> <attr-key-name> <attr-value> ...");
						return;
					}
					// u.ddbPutItemString(parameters);
					return;
				}
				if (args[0].equals("commandsToAmiAsgSoftBeijing")) {
					String[] parameters = Arrays.copyOfRange(args, 1, args.length);
					if (parameters[0].equals("-h")) {
						System.out.println(
								"<asg-name> <wait-mins-during-swapping> <file:///path-to-script-file> | \\\"<command1>\\\", \\\"<command2>\\\", \\\"<...>\\\"");
						return;
					}
					// commandsToAmiAsgSoftBeijing(parameters);
					return;
				}
				// Options filter
				Class<?>[] paramTypes = m.getParameterTypes();
				if (paramTypes == null || paramTypes.length == 0
						|| paramTypes[0] != (new String[] { "XXX" }.getClass())) {
					int paramCount = paramTypes.length;
					String[] paramValues = new String[paramCount];
					// Take '-h' help into consideration.
					for (int i = 0; i < paramValues.length; i++) {
						if (args[1].equals("-h")) {
							paramValues[0] = args[1];
							for (int j = 1; j < paramValues.length; j++) {
								paramValues[j] = null;
							}
							break;
						} else {
							paramValues[i] = args[i + 1];
						}
					}
					m.invoke(u, (Object[]) paramValues);
				} else {
					String[] mParameters = Arrays.copyOfRange(args, 1, args.length);
					System.out.println(m.getName() + ": " + Arrays.toString(mParameters));
					m.invoke(u, (Object[]) (mParameters));
				}
				return;
			}
		}
		Helper.search(args[0]);
		return;
	}

	public static void main(String[] args) throws Exception {
		coreV2(args);
		/*
		Jiu j = new Jiu();
		j.createVmInstance("test1", "Test-plus", "Public Subnet Lgmh:PHX-AD-1", "fm_optimizer_vm",
				"VM.Standard1.1", "/Users/guanglei/git/fuf/bootstrap-bastion.sh", "test");*/
		
	}
}