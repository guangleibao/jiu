package bglutil.jiu;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.SimpleTimeZone;
import java.util.TimeZone;
import java.util.TreeSet;

import com.oracle.bmc.audit.Audit;
import com.oracle.bmc.core.Compute;
import com.oracle.bmc.core.VirtualNetwork;
import com.oracle.bmc.core.model.DhcpDnsOption;
import com.oracle.bmc.core.model.DhcpOption;
import com.oracle.bmc.core.model.DhcpOptions;
import com.oracle.bmc.core.model.Drg;
import com.oracle.bmc.core.model.EgressSecurityRule;
import com.oracle.bmc.core.model.IcmpOptions;
import com.oracle.bmc.core.model.Image;
import com.oracle.bmc.core.model.IngressSecurityRule;
import com.oracle.bmc.core.model.Instance;
import com.oracle.bmc.core.model.InternetGateway;
import com.oracle.bmc.core.model.PortRange;
import com.oracle.bmc.core.model.RouteRule;
import com.oracle.bmc.core.model.RouteTable;
import com.oracle.bmc.core.model.SecurityList;
import com.oracle.bmc.core.model.Shape;
import com.oracle.bmc.core.model.Subnet;
import com.oracle.bmc.core.model.TcpOptions;
import com.oracle.bmc.core.model.UdpOptions;
import com.oracle.bmc.core.model.Vcn;
import com.oracle.bmc.core.model.Vnic;
import com.oracle.bmc.core.requests.GetDrgRequest;
import com.oracle.bmc.core.requests.GetInstanceRequest;
import com.oracle.bmc.core.requests.GetInternetGatewayRequest;
import com.oracle.bmc.core.requests.GetSecurityListRequest;
import com.oracle.bmc.core.requests.GetVnicRequest;
import com.oracle.bmc.core.requests.ListInstancesRequest;
import com.oracle.bmc.core.responses.GetInstanceResponse;
import com.oracle.bmc.identity.Identity;
import com.oracle.bmc.identity.model.ApiKey;
import com.oracle.bmc.identity.model.AvailabilityDomain;
import com.oracle.bmc.identity.model.User;
import com.oracle.bmc.identity.requests.ListApiKeysRequest;
import com.oracle.bmc.identity.requests.ListAvailabilityDomainsRequest;
import com.oracle.bmc.identity.requests.ListUsersRequest;
import com.oracle.bmc.identity.responses.ListUsersResponse;
import com.oracle.bmc.objectstorage.ObjectStorage;
import com.oracle.bmc.objectstorage.responses.PutObjectResponse;
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

	public static final ArrayList<String> SKIPPED_METHODS = new ArrayList<String>();

	static {
		java.security.Security.setProperty("networkaddress.cache.ttl", "60");
		SKIPPED_METHODS.add("coreV2");
		SKIPPED_METHODS.add("main");
	}

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
		h.done(h.BUILDING);
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
		h.done(h.BUILDING);
	}
	
	// SHOW //
	
	public void showImage(String profile) throws NumberFormatException, IOException{
		h.help(profile, "<profile>");
		Compute c = Client.getComputeClient(profile);
		UtilCompute uc = new UtilCompute();
		sk.printTitle(0, "All Images");
		for(Image i:uc.getAllImage(c, Config.getConfigFileReader(profile).get("compartment"))){
			sk.printResult(0, true, i.getDisplayName()+", "+i.getOperatingSystem()+", "+i.getOperatingSystemVersion());
			sk.printResult(1, true, i.getId());
		}
	}
	
	public void showShape(String profile) throws NumberFormatException, IOException{
		h.help(profile, "<profile>");
		Compute c = Client.getComputeClient(profile);
		UtilCompute uc = new UtilCompute();
		sk.printTitle(0, "All Shapes");
		for(String s:uc.getAllShape(c, Config.getConfigFileReader(profile).get("compartment"))){
			sk.printResult(0, true, s);
		}
	}
	
	public void showBucket(String profile) throws Exception{
		h.help(profile, "<profile>");
		ObjectStorage os = Client.getObjectStorageClient(profile);
		UtilObjectStorage uos = new UtilObjectStorage();
		sk.printTitle(0, "All Buckets by profile "+profile);
		uos.printAllBuckets(os, profile);
	}
	
	/**
	 * Show rules for sec list.
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
		UtilNetwork un = new UtilNetwork();
		UtilIam ui = new UtilIam();
		String compartmentId = Config.getConfigFileReader(profile).get("compartment");
		String compartmentName = ui.getCompartmentNameByOcid(id, compartmentId);
		sk.printTitle(0, (name.equals("?")?"All":name)+" VCN in Compartment - "+compartmentName);
		boolean wildhunt = false;
		if(name.equals("?")){
			wildhunt = true;
		}
		for (Vcn v:un.getAllVcn(vn, compartmentId)){
			if(!wildhunt && !name.toLowerCase().equals(v.getDisplayName().toLowerCase())){
				continue;
			}
			VcnAsset va = VcnAsset.getInstance(vn, id, v.getId(), compartmentId);
			sk.printResult(0, true, "VCN: <"+va.getName()+">, "+va.getCidr()+", "+va.getDomainName()+", "+va.getVcn().getLifecycleState().getValue()+", "+va.getVcn().getTimeCreated().toString());
			sk.printResult(1, true, "OCID: "+va.getOcid());
			sk.printResult(1, true, "ALL INTERNET GATEWAY:");
			for(InternetGateway igw:va.getIgws()){
				sk.printResult(2, true, "<"+igw.getDisplayName()+">, enabled-"+igw.getIsEnabled());
			}
			sk.printResult(1, true, "DEFAULT ROUTE TABLE: <"+va.getDefaultRouteTable().getDisplayName()+">");
			sk.printResult(1, true, "ALL ROUTE TABLE:");
			for(RouteTable rt:va.getRouteTables()){
				sk.printResult(2, true, "<"+rt.getDisplayName()+">");
				for(RouteRule rr:rt.getRouteRules()){
					String nOcid = rr.getNetworkEntityId();
					if(nOcid.contains("internetgateway")){
						InternetGateway igw = vn.getInternetGateway(GetInternetGatewayRequest.builder().igId(nOcid).build()).getInternetGateway();
						sk.printResult(3, true, rr.getCidrBlock()+" => <"+igw.getDisplayName()+">");
					}
					else if(nOcid.contains("dynamic")){ //TODO To be confirmed.
						Drg drg = vn.getDrg(GetDrgRequest.builder().drgId(nOcid).build()).getDrg();
						sk.printResult(3, true, rr.getCidrBlock()+" => <"+drg.getDisplayName()+">");
					}
				}
			}
			sk.printResult(1, true, "DEFAULT DHCP OPTIONS: <"+va.getDefaultDhcpOptions().getDisplayName()+">");
			sk.printResult(1, true, "ALL DHCP OPTIONS:");
			for(DhcpOptions dhcpo:va.getDhcpOptions()){
				sk.printResult(2, true, "<"+dhcpo.getDisplayName()+">");
				for(DhcpOption dhcp:dhcpo.getOptions()){
					sk.printResult(3, true, dhcp.toString());
				}
			}
			sk.printResult(1, true, "DEFAULT SECURITY LIST: <"+va.getDefaultSecList().getDisplayName()+">");	
			sk.printResult(1, true, "ALL SECURITY LIST:");
			for(SecurityList sl:va.getSecLists()){
				un.printSecListRules(sl);
			}
			List<Subnet> subnets = va.getSubnets();
			for(Subnet s:subnets){
				SubnetAsset sa = SubnetAsset.getInstance(vn, id, s.getId());
				sk.printResult(1, true, "SUBNET: <"+sa.getSn().getDisplayName()+">, "+sa.getSn().getCidrBlock()+", "+sa.getSn().getSubnetDomainName()+", "+sa.getAd().getName());
				sk.printResult(2, true, "DHCP Options: <"+sa.getDhcp().getDisplayName()+">");
				sk.printResult(2, true, "Route Table: <"+sa.getRt().getDisplayName()+">");
				int sli=0;
				for(SecurityList sl:sa.getSecLists()){
					sk.printResult(2, true, "SecList: ("+(++sli)+") <"+sl.getDisplayName()+">");
				}
			}
		}
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
	}
	
	/**
	 * Show Java system properties.
	 */
	public void showSystemProperty() {
		Properties p = System.getProperties();
		for (Object k : p.keySet()) {
			System.out.println("[ " + k + " ] => ( " + p.getProperty((String) k) + " )");
		}
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
	}
	
	// CREATOR //
	
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
		Vcn vcn = un.createVcn(vn, Config.getConfigFileReader(profile).get("compartment"), name, cidr, "Jiu - "+name);
		sk.printResult(0, true, vcn.getCidrBlock()+", "+vcn.getVcnDomainName()+", "+vcn.getId()+" CREATED.");
	}
	
	// SETUP //
	
	/**
	 * Create a new infrastructure for demo purpose.
	 * 1. Compartment read from configuration file.
	 * 2. One VCN.
	 * 3. One IGW.
	 * 4. One Public Route Table.
	 * 5. One Bastion security list with rules.
	 * 6. One Web Server security list with rules.
	 * 7. Six Subnets.
	 * 8. One Oracle Linux 7.3 Bastion. Public
	 * 9. Three Oracle Linux 7.3 Web Server. Private.
	 * @param profile
	 * @throws Exception
	 */
	public void createDemoInfra(String namePrefix, String profile) throws Exception{
		h.help(namePrefix, "<resource-name-prefix> <profile-name>");
		String prefix = namePrefix!=null?namePrefix:"bgltest";
		sk.printTitle(0, "Create Infrastructure - "+prefix);
		Identity id = Client.getIamClient(profile);
		VirtualNetwork vn = Client.getVirtualNetworkClient(profile);
		UtilIam ui = new UtilIam();
		UtilNetwork un = new UtilNetwork();
		String compartmentId = Config.getConfigFileReader(profile).get("compartment");
		// VCN
		Vcn vcn = un.createVcn(vn, compartmentId, prefix+"vcn", "10.7.0.0/16", "Jiu - "+prefix+"vcn");
		// IGW
		InternetGateway igw = un.createIgw(vn, compartmentId, vcn.getId(), prefix+"igw");
		// Public Route Table
		RouteRule rr = RouteRule.builder().cidrBlock("0.0.0.0/0").networkEntityId(igw.getId()).build();
		List<RouteRule> rrs = new ArrayList<RouteRule>(); rrs.add(rr);
		RouteTable publicRouteTable = un.createRouteTable(vn, compartmentId, vcn.getId(), prefix+"rt-public", rrs);
		// Bastion SecList
		List<IngressSecurityRule> irBastion = un.getLinuxBastionIngressSecurityRules();
		List<EgressSecurityRule> erAllowAll = un.getEgressAllowAllRules();
		List<IngressSecurityRule> irWebserver = un.getInternalWebserverIngressSecurityRules(vcn.getCidrBlock());
		SecurityList bastionSecList = un.createSecList(vn, compartmentId, vcn.getId(), prefix+"seclist-bastion", irBastion, erAllowAll);
		List<String> bastionSecLists = new ArrayList<String>();
		bastionSecLists.add(bastionSecList.getId());
		SecurityList webserverSecList = un.createSecList(vn, compartmentId, vcn.getId(), prefix+"seclist-webserver", irWebserver, erAllowAll);
		List<String> webserverSecLists = new ArrayList<String>();
		webserverSecLists.add(webserverSecList.getId());
		// 6 Subnets.
		List<AvailabilityDomain> ads = ui.getAllAd(id, profile);
		String snpub = prefix+"snpub";
		Subnet[] pubSubnets = new Subnet[3];
		for(int i=0;i<3;i++){
			pubSubnets[i] = un.createSubnet(vn, compartmentId, vcn.getId(), 
				snpub+i, snpub+i, 
				"10.7."+i+".0/24", ads.get(i).getName(), 
				vcn.getDefaultDhcpOptionsId(), publicRouteTable.getId(), bastionSecLists, "Jiu - "+snpub+i);
		}
		String snpri = prefix+"snpri";
		Subnet[] priSubnets = new Subnet[3];
		for(int i=0;i<3;i++){
			priSubnets[i] = un.createSubnet(vn, compartmentId, vcn.getId(), 
				snpri+i, snpri+i, 
				"10.7."+(i+3)+".0/24", ads.get(i).getName(), 
				vcn.getDefaultDhcpOptionsId(), vcn.getDefaultRouteTableId(), webserverSecLists, "Jiu - "+snpub+i);
		}
		// 1 Bastion.
		Compute c = Client.getComputeClient(profile);
		UtilCompute uc = new UtilCompute();
		Image vmImage = null;
		for(Image img:uc.getAllImage(c, compartmentId)){
			if(img.getOperatingSystem().equals("Oracle Linux") && img.getOperatingSystemVersion().equals("7.3")){
				vmImage = img;
				break;
			}
		}
		GetInstanceResponse bastionGis = uc.createVmInstance(c, compartmentId, pubSubnets[0].getId(), 
				prefix+"bastion", vmImage.getId(), "VM.Standard1.2", 
				Config.publicKeyToString(profile), ads.get(0).getName(), Instance.LifecycleState.Running);
		String bastionId = bastionGis.getInstance().getId();
		List<Vnic> vnics = uc.getVnicByInstanceId(c, vn, bastionId, compartmentId);
		for(Vnic v:vnics){
			sk.printResult(1, true, "@AD: "+v.getAvailabilityDomain()+", HostName: "+v.getHostnameLabel()+", "+v.getPrivateIp()+", "+v.getPublicIp());
		}
		// 3 Web Servers.
		GetInstanceResponse[] webGis = new GetInstanceResponse[3];
		for(int i=0;i<3;i++){
			webGis[i]= uc.createVmInstance(c, compartmentId, priSubnets[i].getId(), 
				prefix+"web"+i, vmImage.getId(), "VM.Standard1.1", 
				Config.publicKeyToString(profile), ads.get(i).getName(), Instance.LifecycleState.Provisioning);
		}
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
		String compId = Config.getConfigFileReader(profile).get("compartment");
		String vcnId = un.getVcnIdByName(vn, vcnName, compId);
		un.deleteSecurityListByNamePrefix(vn, compId, vcnId, namePrefix);
		sk.printResult(0, true, "SecList with name prefix "+namePrefix+" DESTROYED.");
	}
	
	/**
	 * Delete all VCN if matches the name prefix in profile compartment. Check README.MD for profile setting.
	 * @param namePrefix
	 * @param profile
	 * @throws Exception
	 */
	public void destroyVcn(String namePrefix, String profile) throws Exception{
		h.help(namePrefix, "<vcn-name-refix> <profile>");
		sk.printTitle(0, "Delete VCN with Name Prefix "+namePrefix);
		VirtualNetwork vn = Client.getVirtualNetworkClient(profile);
		UtilNetwork un = new UtilNetwork();
		Compute c = Client.getComputeClient(profile);
		un.deleteVcnByNamePrefix(vn, c, Config.getConfigFileReader(profile).get("compartment"), namePrefix);
		sk.printTitle(0, "VCN with name prefix "+namePrefix+" DESTROYED.");
	}
	
	public void restGet(String apiVersion, String path, String resource, String profile) throws IOException{
		h.help(apiVersion, "<api-version> <path> <resource> <profile>");
		sk.printTitle(0, "/"+apiVersion+"/"+path+"/"+resource);
		String[] ret = UtilMain.restGet(apiVersion, path, resource, profile);
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
	}
}