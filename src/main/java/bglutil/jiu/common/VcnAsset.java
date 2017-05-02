package bglutil.jiu.common;

import java.util.List;

import com.oracle.bmc.core.VirtualNetwork;
import com.oracle.bmc.core.model.DhcpOptions;
import com.oracle.bmc.core.model.DrgAttachment;
import com.oracle.bmc.core.model.InternetGateway;
import com.oracle.bmc.core.model.RouteTable;
import com.oracle.bmc.core.model.SecurityList;
import com.oracle.bmc.core.model.Subnet;
import com.oracle.bmc.core.model.Vcn;
import com.oracle.bmc.core.model.Vcn.LifecycleState;
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
import com.oracle.bmc.identity.model.Compartment;
import com.oracle.bmc.identity.requests.GetCompartmentRequest;


/**
 * All assets within a VCN.
 * fact: VCN.
 * demension: others.
 * 
 * @author guanglei
 *
 */
public class VcnAsset {
	
	public VcnAsset(){
		super();
	}
	
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
		va.subnets = vn.listSubnets(ListSubnetsRequest.builder().compartmentId(compartmentId).vcnId(vcnId).build()).getItems();
		va.routeTables = vn.listRouteTables(ListRouteTablesRequest.builder().compartmentId(compartmentId).vcnId( vcnId).build()).getItems();
		va.igws = vn.listInternetGateways(ListInternetGatewaysRequest.builder().compartmentId(compartmentId).vcnId( vcnId).build()).getItems();
		va.drgAttachments = vn.listDrgAttachments(ListDrgAttachmentsRequest.builder().compartmentId(compartmentId).vcnId( vcnId).build()).getItems();
		va.secLists = vn.listSecurityLists(ListSecurityListsRequest.builder().compartmentId(compartmentId).vcnId(vcnId).build()).getItems();	
		va.dhcpOptions = vn.listDhcpOptions(ListDhcpOptionsRequest.builder().compartmentId(compartmentId).vcnId( vcnId).build()).getItems();
		return va;
	}
	
	private Vcn vcn;
	private String dnsLabel;
	private String domainName;
	private String cidr;
	private String ocid;
	private String name;
	private List<Subnet> subnets;
	private RouteTable defaultRouteTable;
	private List<RouteTable> routeTables;
	private List<InternetGateway> igws;
	private List<DrgAttachment> drgAttachments;
	private SecurityList defaultSecList;
	private List<SecurityList> secLists;
	private DhcpOptions defaultDhcpOptions;
	private List<DhcpOptions> dhcpOptions;
	
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

	public List<Subnet> getSubnets() {
		return subnets;
	}

	public RouteTable getDefaultRouteTable() {
		return defaultRouteTable;
	}

	public List<DrgAttachment> getDrgAttachments() {
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
	public List<RouteTable> getRouteTables() {
		return routeTables;
	}
	public List<InternetGateway> getIgws() {
		return igws;
	}
	public List<SecurityList> getSecLists() {
		return secLists;
	}
	public List<DhcpOptions> getDhcpOptions() {
		return dhcpOptions;
	}
	
}
