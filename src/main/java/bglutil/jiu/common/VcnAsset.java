package bglutil.jiu.common;

import java.util.Hashtable;
import java.util.Map;

import com.oracle.bmc.core.VirtualNetwork;
import com.oracle.bmc.core.model.DhcpOptions;
import com.oracle.bmc.core.model.DrgAttachment;
import com.oracle.bmc.core.model.InternetGateway;
import com.oracle.bmc.core.model.RouteTable;
import com.oracle.bmc.core.model.SecurityList;
import com.oracle.bmc.core.model.Subnet;
import com.oracle.bmc.core.model.Vcn;
import com.oracle.bmc.core.requests.GetDhcpOptionsRequest;
import com.oracle.bmc.core.requests.GetRouteTableRequest;
import com.oracle.bmc.core.requests.GetSecurityListRequest;
import com.oracle.bmc.core.requests.GetVcnRequest;
import com.oracle.bmc.core.requests.ListDhcpOptionsRequest;
import com.oracle.bmc.core.requests.ListDrgAttachmentsRequest;
import com.oracle.bmc.core.requests.ListInternetGatewaysRequest;
import com.oracle.bmc.core.requests.ListRouteTablesRequest;
import com.oracle.bmc.core.requests.ListSecurityListsRequest;
import com.oracle.bmc.core.requests.ListSubnetsRequest;
import com.oracle.bmc.identity.Identity;


/**
 * All assets within a VCN.
 * fact: VCN.
 * dimension: others.
 * 
 * @author guanglei
 *
 */
public class VcnAsset {
	
	public VcnAsset(){
		super();
	}
	
	/**
	 * Make a VCN representation.
	 * @param vn
	 * @param id
	 * @param vcnId
	 * @param compartmentId
	 * @return
	 */
	public static VcnAsset getInstance(VirtualNetwork vn, Identity id, String vcnId, String compartmentId){
		VcnAsset va = new VcnAsset();
		va.vcn = vn.getVcn(GetVcnRequest.builder().vcnId(vcnId).build()).getVcn();
		va.dnsLabel = va.vcn.getDnsLabel();
		va.domainName = va.vcn.getVcnDomainName();
		va.cidr = va.vcn.getCidrBlock();
		va.ocid = vcnId;
		va.name = va.vcn.getDisplayName();
		va.defaultDhcpOptions = vn.getDhcpOptions(GetDhcpOptionsRequest.builder().dhcpId(va.vcn.getDefaultDhcpOptionsId()).build()).getDhcpOptions();
		va.defaultRouteTable = vn.getRouteTable(GetRouteTableRequest.builder().rtId(va.vcn.getDefaultRouteTableId()).build()).getRouteTable();
		va.defaultSecList = vn.getSecurityList(GetSecurityListRequest.builder().securityListId(va.vcn.getDefaultSecurityListId()).build()).getSecurityList();
		va.subnets = new Hashtable<String,Subnet>();
				for(Subnet sn:vn.listSubnets(ListSubnetsRequest.builder().compartmentId(compartmentId).vcnId(vcnId).build()).getItems()){
					va.subnets.put(sn.getId(), sn);
				}
		va.routeTables = new Hashtable<String,RouteTable>();
				for(RouteTable rt:vn.listRouteTables(ListRouteTablesRequest.builder().compartmentId(compartmentId).vcnId( vcnId).build()).getItems()){
					va.routeTables.put(rt.getId(), rt);
				}
		va.igws = new Hashtable<String, InternetGateway>();
				for(InternetGateway igw:vn.listInternetGateways(ListInternetGatewaysRequest.builder().compartmentId(compartmentId).vcnId( vcnId).build()).getItems()){
					va.igws.put(igw.getId(), igw);
				}
		va.drgAttachments = new Hashtable<String, DrgAttachment>();
				for(DrgAttachment drga:vn.listDrgAttachments(ListDrgAttachmentsRequest.builder().compartmentId(compartmentId).vcnId( vcnId).build()).getItems()){
					va.drgAttachments.put(drga.getId(), drga);
				}
		va.secLists = new Hashtable<String, SecurityList>();
				for(SecurityList sl:vn.listSecurityLists(ListSecurityListsRequest.builder().compartmentId(compartmentId).vcnId(vcnId).build()).getItems()){
					va.secLists.put(sl.getId(),sl);
				}
		va.dhcpOptions = new Hashtable<String, DhcpOptions>();
				for(DhcpOptions dhcpo:vn.listDhcpOptions(ListDhcpOptionsRequest.builder().compartmentId(compartmentId).vcnId( vcnId).build()).getItems()){
					va.dhcpOptions.put(dhcpo.getId(), dhcpo);
				}
		return va;
	}
	
	private Vcn vcn;
	private String dnsLabel;
	private String domainName;
	private String cidr;
	private String ocid;
	private String name;
	private Map<String,Subnet> subnets;
	private RouteTable defaultRouteTable;
	private Map<String,RouteTable> routeTables;
	private Map<String,InternetGateway> igws;
	private Map<String,DrgAttachment> drgAttachments;
	private SecurityList defaultSecList;
	private Map<String,SecurityList> secLists;
	private DhcpOptions defaultDhcpOptions;
	private Map<String,DhcpOptions> dhcpOptions;
	
	public Vcn getVcn() {
		return vcn;
	}

	public String getDomainName() {
		return domainName;
	}

	public String getCidr() {
		return cidr;
	}

	public String getOcid() {
		return ocid;
	}

	public String getName() {
		return name;
	}

	public Map<String,Subnet> getSubnets() {
		return subnets;
	}

	public RouteTable getDefaultRouteTable() {
		return defaultRouteTable;
	}

	public Map<String,DrgAttachment> getDrgAttachments() {
		return drgAttachments;
	}

	public SecurityList getDefaultSecList() {
		return defaultSecList;
	}

	public DhcpOptions getDefaultDhcpOptions() {
		return defaultDhcpOptions;
	}

	public String getDnsLabel() {
		return dnsLabel;
	}
	public Map<String,RouteTable> getRouteTables() {
		return routeTables;
	}
	public Map<String,InternetGateway> getIgws() {
		return igws;
	}
	public Map<String,SecurityList> getSecLists() {
		return secLists;
	}
	public Map<String,DhcpOptions> getDhcpOptions() {
		return dhcpOptions;
	}
	
}
