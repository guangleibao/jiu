package bglutil.jiu.common;

import java.util.ArrayList;
import java.util.List;

import com.oracle.bmc.core.VirtualNetwork;
import com.oracle.bmc.core.model.DhcpOptions;
import com.oracle.bmc.core.model.RouteTable;
import com.oracle.bmc.core.model.SecurityList;
import com.oracle.bmc.core.model.Subnet;
import com.oracle.bmc.core.model.Vcn;
import com.oracle.bmc.core.requests.GetDhcpOptionsRequest;
import com.oracle.bmc.core.requests.GetRouteTableRequest;
import com.oracle.bmc.core.requests.GetSecurityListRequest;
import com.oracle.bmc.core.requests.GetSubnetRequest;
import com.oracle.bmc.core.requests.GetVcnRequest;
import com.oracle.bmc.identity.Identity;
import com.oracle.bmc.identity.model.AvailabilityDomain;
import com.oracle.bmc.identity.model.Compartment;
import com.oracle.bmc.identity.requests.GetCompartmentRequest;

/**
 * 
 * All information surround a subnet.
 * fact: sn.
 * demension: others.
 * 
 * 
 * @author guanglei
 *
 */
public class SubnetAsset {
	
	private Subnet sn;
	private Vcn vcn;
	private AvailabilityDomain ad;
	private DhcpOptions dhcp;
	private String dnsLabel;
	private RouteTable rt;
	private List<SecurityList> secLists;
	
	public SubnetAsset(){
		super();
	}
	
	public static SubnetAsset getInstance(VirtualNetwork vn, Identity id, String subnetId){
		SubnetAsset sa = new SubnetAsset();
		sa.sn = vn.getSubnet(GetSubnetRequest.builder().subnetId(subnetId).build()).getSubnet();
		sa.vcn = vn.getVcn(GetVcnRequest.builder().vcnId(sa.sn.getVcnId()).build()).getVcn();
		sa.ad = AvailabilityDomain.builder().name(sa.sn.getAvailabilityDomain()).build();
		sa.dhcp = vn.getDhcpOptions(GetDhcpOptionsRequest.builder().dhcpId(sa.sn.getDhcpOptionsId()).build()).getDhcpOptions();
		sa.rt = vn.getRouteTable(GetRouteTableRequest.builder().rtId(sa.sn.getRouteTableId()).build()).getRouteTable();
		sa.dnsLabel = sa.sn.getDnsLabel();
		sa.secLists = new ArrayList<SecurityList>();
		for(String lId:sa.sn.getSecurityListIds()){
			sa.secLists.add(vn.getSecurityList(GetSecurityListRequest.builder().securityListId(lId).build()).getSecurityList());
		}
		return sa;
	}
	
	/**
	 * Create a new subnet.
	 */
	public void create(){
		//TODO
	}
	
	public Vcn getVcn() {
		return vcn;
	}

	public void setVcn(Vcn vcn) {
		this.vcn = vcn;
	}
	
	public Subnet getSn() {
		return sn;
	}
	public void setSn(Subnet sn) {
		this.sn = sn;
	}
	public AvailabilityDomain getAd() {
		return ad;
	}
	public void setAd(AvailabilityDomain ad) {
		this.ad = ad;
	}
	public DhcpOptions getDhcp() {
		return dhcp;
	}
	public void setDhcp(DhcpOptions dhcp) {
		this.dhcp = dhcp;
	}
	public String getDnsLabel() {
		return dnsLabel;
	}
	public void setDnsLabel(String dnsLabel) {
		this.dnsLabel = dnsLabel;
	}
	public RouteTable getRt() {
		return rt;
	}
	public void setRt(RouteTable rt) {
		this.rt = rt;
	}
	public List<SecurityList> getSecLists() {
		return secLists;
	}
	public void setSecLists(List<SecurityList> secLists) {
		this.secLists = secLists;
	}
	public void addSecList(String securyListId){
		//TODO
	}
	public void removeSecList(String securityListId){
		//TODO
	}
}
