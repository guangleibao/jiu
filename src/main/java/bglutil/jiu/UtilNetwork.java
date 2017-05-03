package bglutil.jiu;

import java.util.ArrayList;
import java.util.List;

import com.oracle.bmc.core.VirtualNetwork;
import com.oracle.bmc.core.model.CreateDhcpDetails;
import com.oracle.bmc.core.model.CreateInternetGatewayDetails;
import com.oracle.bmc.core.model.CreateRouteTableDetails;
import com.oracle.bmc.core.model.CreateSecurityListDetails;
import com.oracle.bmc.core.model.CreateSubnetDetails;
import com.oracle.bmc.core.model.CreateVcnDetails;
import com.oracle.bmc.core.model.DhcpOption;
import com.oracle.bmc.core.model.DhcpOptions;
import com.oracle.bmc.core.model.EgressSecurityRule;
import com.oracle.bmc.core.model.IngressSecurityRule;
import com.oracle.bmc.core.model.InternetGateway;
import com.oracle.bmc.core.model.PortRange;
import com.oracle.bmc.core.model.RouteRule;
import com.oracle.bmc.core.model.RouteTable;
import com.oracle.bmc.core.model.SecurityList;
import com.oracle.bmc.core.model.Subnet;
import com.oracle.bmc.core.model.TcpOptions;
import com.oracle.bmc.core.model.UdpOptions;
import com.oracle.bmc.core.model.UpdateSecurityListDetails;
import com.oracle.bmc.core.model.Vcn;
import com.oracle.bmc.core.requests.CreateDhcpOptionsRequest;
import com.oracle.bmc.core.requests.CreateInternetGatewayRequest;
import com.oracle.bmc.core.requests.CreateRouteTableRequest;
import com.oracle.bmc.core.requests.CreateSecurityListRequest;
import com.oracle.bmc.core.requests.CreateSubnetRequest;
import com.oracle.bmc.core.requests.CreateVcnRequest;
import com.oracle.bmc.core.requests.DeleteRouteTableRequest;
import com.oracle.bmc.core.requests.DeleteSecurityListRequest;
import com.oracle.bmc.core.requests.DeleteVcnRequest;
import com.oracle.bmc.core.requests.GetSecurityListRequest;
import com.oracle.bmc.core.requests.ListRouteTablesRequest;
import com.oracle.bmc.core.requests.ListSecurityListsRequest;
import com.oracle.bmc.core.requests.ListSubnetsRequest;
import com.oracle.bmc.core.requests.ListVcnsRequest;
import com.oracle.bmc.core.requests.UpdateSecurityListRequest;
import com.oracle.bmc.core.responses.CreateDhcpOptionsResponse;
import com.oracle.bmc.core.responses.CreateInternetGatewayResponse;
import com.oracle.bmc.core.responses.CreateRouteTableResponse;
import com.oracle.bmc.core.responses.CreateSecurityListResponse;
import com.oracle.bmc.core.responses.CreateSubnetResponse;
import com.oracle.bmc.core.responses.CreateVcnResponse;
import com.oracle.bmc.core.responses.GetDhcpOptionsResponse;
import com.oracle.bmc.core.responses.GetInternetGatewayResponse;
import com.oracle.bmc.core.responses.GetRouteTableResponse;
import com.oracle.bmc.core.responses.GetSecurityListResponse;
import com.oracle.bmc.core.responses.GetVcnResponse;
import com.oracle.bmc.core.responses.ListSubnetsResponse;
import com.oracle.bmc.core.responses.ListVcnsResponse;

import bglutil.jiu.common.UtilMain;

/**
 * VirtualNetwork utilities.
 * 
 * @author guanglei
 *
 */
public class UtilNetwork extends UtilMain {

	public UtilNetwork() {
		super();
	}

	// Facet //
	
	public List<IngressSecurityRule> getLinuxBastionIngressSecurityRules(){
		PortRange ssh = PortRange.builder().min(22).max(22).build();
		TcpOptions sshInbound = TcpOptions.builder().destinationPortRange(ssh).build();
		IngressSecurityRule isrBastionSsh = IngressSecurityRule.builder().source("0.0.0.0/0").protocol("6").tcpOptions(sshInbound).build();
		IngressSecurityRule isrBastionIcmp = IngressSecurityRule.builder().source("0.0.0.0/0").protocol("1").build();
		List<IngressSecurityRule> ir = new ArrayList<IngressSecurityRule>();
		ir.add(isrBastionSsh); ir.add(isrBastionIcmp);
		return ir;
	}
	
	public List<EgressSecurityRule> getEgressAllowAllRules(){
		EgressSecurityRule esrBastionAll = EgressSecurityRule.builder().destination("0.0.0.0/0").protocol("all").build();
		List<EgressSecurityRule> er = new ArrayList<EgressSecurityRule>();
		er.add(esrBastionAll);
		return er;
	}
	
	// GETTER //
	/**
	 * Print rules for security list.
	 * @param sl
	 */
	public void printSecListRules(SecurityList sl) {
		sk.printResult(2, true, "<" + sl.getDisplayName() + ">");
		sk.printResult(3, true, "Ingress:");
		int rn = 0;
		for (IngressSecurityRule isr : sl.getIngressSecurityRules()) {
			sk.printResult(4, true, "("+(++rn)+") stateless-" + isr.getIsStateless() + ": protocol-" + isr.getProtocol() + " src:"
					+ isr.getSource());
			if (isr.getIcmpOptions() != null) {
				sk.printResult(5, true,
						"ICMP type-code: " + isr.getIcmpOptions().getType() + "-" + isr.getIcmpOptions().getCode());
			}
			TcpOptions iTcpOptions = isr.getTcpOptions();
			if (iTcpOptions != null) {
				PortRange iDestRange = iTcpOptions.getDestinationPortRange();
				PortRange iSrcRange = iTcpOptions.getSourcePortRange();
				if (iDestRange != null) {
					sk.printResult(5, true, "TCP dest port: " + iDestRange.getMin() + "~" + iDestRange.getMax());
				}
				if (iSrcRange != null) {
					sk.printResult(5, true, "TCP src port: " + iSrcRange.getMin() + "~" + iSrcRange.getMax());
				}
			}
			UdpOptions iUdpOptions = isr.getUdpOptions();
			if (iUdpOptions != null) {
				PortRange iDestRange = iUdpOptions.getDestinationPortRange();
				PortRange iSrcRange = iUdpOptions.getSourcePortRange();
				if (iDestRange != null) {
					sk.printResult(5, true, "UDP dest port: " + iDestRange.getMin() + "~" + iDestRange.getMax());
				}
				if (iSrcRange != null) {
					sk.printResult(5, true, "UDP src port: " + iSrcRange.getMin() + "~" + iSrcRange.getMax());
				}
			}
		}
		sk.printResult(3, true, "Egress:");
		int ern = 0;
		for (EgressSecurityRule esr : sl.getEgressSecurityRules()) {
			sk.printResult(4, true, "("+(++rn)+") stateless-" + esr.getIsStateless() + ": protocol-" + esr.getProtocol() + " dest:"
					+ esr.getDestination());
			if (esr.getIcmpOptions() != null) {
				sk.printResult(5, true,
						"ICMP type-code: " + esr.getIcmpOptions().getType() + "-" + esr.getIcmpOptions().getCode());
			}
			TcpOptions eTcpOptions = esr.getTcpOptions();
			if (eTcpOptions != null) {
				PortRange eDestRange = eTcpOptions.getDestinationPortRange();
				PortRange eSrcRange = eTcpOptions.getSourcePortRange();
				if (eDestRange != null) {
					sk.printResult(5, true, "TCP dest port: " + eDestRange.getMin() + "~" + eDestRange.getMax());
				}
				if (eSrcRange != null) {
					sk.printResult(5, true, "TCP src port: " + eSrcRange.getMin() + "~" + eSrcRange.getMax());
				}
			}
			UdpOptions eUdpOptions = esr.getUdpOptions();
			if (eUdpOptions != null) {
				PortRange eDestRange = eUdpOptions.getDestinationPortRange();
				PortRange eSrcRange = eUdpOptions.getSourcePortRange();
				if (eDestRange != null) {
					sk.printResult(5, true, "UDP dest port: " + eDestRange.getMin() + "~" + eDestRange.getMax());
				}
				if (eSrcRange != null) {
					sk.printResult(5, true, "UDP src port: " + eSrcRange.getMin() + "~" + eSrcRange.getMax());
				}
			}
		}
	}

	/**
	 * Get VCN OCID by VCN Name.
	 * 
	 * @param vn
	 * @param vcnName
	 * @param compartmentId
	 * @return
	 */
	public String getVcnIdByName(VirtualNetwork vn, String vcnName, String compartmentId) {
		List<Vcn> vcns = vn.listVcns(ListVcnsRequest.builder().compartmentId(compartmentId).build()).getItems();
		String ocid = null;
		for (Vcn v : vcns) {
			if (v.getDisplayName().equals(vcnName)) {
				ocid = v.getId();
			}
		}
		return ocid;
	}

	/**
	 * Get all VCN in a compartment.
	 * 
	 * @param vn
	 * @param compartmentId
	 * @return
	 */
	public List<Vcn> getAllVcn(VirtualNetwork vn, String compartmentId) {
		ListVcnsResponse res = vn.listVcns(ListVcnsRequest.builder().compartmentId(compartmentId).build());
		return res.getItems();
	}

	/**
	 * Get all subnet in a VCN.
	 * 
	 * @param vn
	 * @param compartmentId
	 * @param vcnName
	 * @return
	 */
	public List<Subnet> getSubnetInVcn(VirtualNetwork vn, String compartmentId, String vcnName) {
		ListSubnetsResponse res = null;
		List<Vcn> vcns = this.getAllVcn(vn, compartmentId);
		for (Vcn v : vcns) {
			if (v.getDisplayName().equals(vcnName)) {
				res = vn.listSubnets(
						ListSubnetsRequest.builder().compartmentId(compartmentId).vcnId(v.getId()).build());
			}
		}
		return res.getItems();
	}

	// UPDATOR //
	
	/**
	 * Remove allows port from bastion 0.0.0.0/0 source.
	 * @param vn
	 * @param secListId
	 * @param tcpPort
	 */
	public void secListRemoveIngressBastionTcpPort(VirtualNetwork vn, String secListId, String tcpPort){
		SecurityList sl = vn.getSecurityList(GetSecurityListRequest.builder().securityListId(secListId).build()).getSecurityList();
		List<IngressSecurityRule> ir = sl.getIngressSecurityRules();
		for(IngressSecurityRule r:ir){
			String source = r.getSource();
			if(!source.equals("0.0.0.0/0")){
				continue;
			}
			if(r.getTcpOptions()==null 
					|| r.getTcpOptions().getDestinationPortRange()==null 
					|| r.getTcpOptions().getDestinationPortRange().getMax()==null){
				continue;
			}
			String port = r.getTcpOptions().getDestinationPortRange().getMin()+"~"+r.getTcpOptions().getDestinationPortRange().getMax();
			if(source.equals("0.0.0.0/0") && port.equals(tcpPort+"~"+tcpPort)){
				ir.remove(r);
				break;
			}
		}
		UpdateSecurityListDetails usld = UpdateSecurityListDetails.builder().ingressSecurityRules(ir).build();
		vn.updateSecurityList(UpdateSecurityListRequest.builder().securityListId(secListId).updateSecurityListDetails(usld).build());
	}
	
	/**
	 * Add allows port for bastion 0.0.0.0/0 source.
	 * @param vn
	 * @param secListId
	 * @param tcpPort
	 */
	public void secListAddIngressBastionTcpPort(VirtualNetwork vn, String secListId, String tcpPort){
		SecurityList sl = vn.getSecurityList(GetSecurityListRequest.builder().securityListId(secListId).build()).getSecurityList();
		List<IngressSecurityRule> ir = sl.getIngressSecurityRules();
		IngressSecurityRule r = IngressSecurityRule.builder().protocol("6").source("0.0.0.0/0").tcpOptions(
					TcpOptions.builder().destinationPortRange(PortRange.builder().min(22).max(22).build()).build()
				).build();
		ir.add(r);
		UpdateSecurityListDetails usld = UpdateSecurityListDetails.builder().ingressSecurityRules(ir).build();
		vn.updateSecurityList(UpdateSecurityListRequest.builder().securityListId(secListId).updateSecurityListDetails(usld).build());
	}
	
	// CREATOR //

	/**
	 * Create a new VCN.
	 * 
	 * @param vn
	 * @param compartmentId
	 * @param name
	 * @param cidr
	 * @param description
	 * @return Vcn interface
	 * @throws Exception
	 */
	public Vcn createVcn(VirtualNetwork vn, String compartmentId, String name, String cidr, String description)
			throws Exception {
		CreateVcnResponse cvr = vn
				.createVcn(
						CreateVcnRequest.builder()
								.createVcnDetails(CreateVcnDetails.builder().cidrBlock(cidr)
										.compartmentId(compartmentId).displayName(name).dnsLabel(name).build())
								.build());
		String vcnId = cvr.getVcn().getId();
		GetVcnResponse res = h.waitForVcnStatus(vn, vcnId, Vcn.LifecycleState.Available, "Creating VCN " + name, false);
		return res.getVcn();
	}

	/**
	 * Create a new Subnet with full parameters.
	 * 
	 * @param vn
	 * @param compartmentId
	 * @param vcnId
	 * @param name
	 * @param dnsLabel
	 * @param cidr
	 * @param availabilityDomain
	 * @param dhcpOptionsId
	 * @param routeTableId
	 * @param securityListIds
	 * @param description
	 * @return
	 */
	public Subnet createSubnet(VirtualNetwork vn, String compartmentId, String vcnId, String name, String dnsLabel,
			String cidr, String availabilityDomain, String dhcpOptionsId, String routeTableId,
			List<String> securityListIds, String description) {
		CreateSubnetResponse res = vn.createSubnet(CreateSubnetRequest.builder()
				.createSubnetDetails(CreateSubnetDetails.builder().availabilityDomain(availabilityDomain)
						.cidrBlock(cidr).compartmentId(compartmentId).dhcpOptionsId(dhcpOptionsId).displayName(name)
						.dnsLabel(dnsLabel).routeTableId(routeTableId).vcnId(vcnId).securityListIds(securityListIds)
						.build())
				.build());
		return res.getSubnet();
	}

	/**
	 * Create a new IGW.
	 * @param vn
	 * @param compartmentId
	 * @param vcnId
	 * @param name
	 * @return
	 * @throws Exception
	 */
	public InternetGateway createIgw(VirtualNetwork vn, String compartmentId, String vcnId, String name) throws Exception{
		CreateInternetGatewayResponse cigr = vn.createInternetGateway(CreateInternetGatewayRequest.builder()
				.createInternetGatewayDetails(CreateInternetGatewayDetails.builder()
							.compartmentId(compartmentId).isEnabled(true).vcnId(vcnId).displayName(name).build()
						)
				.build());
		String igwId = cigr.getInternetGateway().getId();
		GetInternetGatewayResponse res = h.waitForIgwStatus(vn, igwId, InternetGateway.LifecycleState.Available, "Creating IGW "+name, false);
		return res.getInternetGateway();
	}
	
	/**
	 * Create a new Route Table.
	 * @param vn
	 * @param compartmentId
	 * @param vcnId
	 * @param name
	 * @param rrs
	 * @return
	 * @throws Exception
	 */
	public RouteTable createRouteTable(VirtualNetwork vn, String compartmentId, String vcnId, String name, List<RouteRule> routeRules) throws Exception{
		
		CreateRouteTableResponse crtr = vn.createRouteTable(CreateRouteTableRequest.builder().createRouteTableDetails(
				CreateRouteTableDetails.builder().compartmentId(compartmentId).displayName(name).vcnId(vcnId)
				.routeRules(routeRules)
				.build()		
				).build()); 
		String rtId = crtr.getRouteTable().getId();
		GetRouteTableResponse res = h.waitForRouteTableStatus(vn, rtId, RouteTable.LifecycleState.Available, "Creating Route Table "+name, false);
		return res.getRouteTable();
	}
	
	/**
	 * Create a new DhcpOptions.
	 * @param vn
	 * @param compartmentId
	 * @param vcnId
	 * @param name
	 * @param options
	 * @return
	 * @throws Exception
	 */
	public DhcpOptions createDhcpOptions(VirtualNetwork vn, String compartmentId, String vcnId, String name, List<DhcpOption> options) throws Exception{
		
		CreateDhcpOptionsResponse cdos = vn.createDhcpOptions(CreateDhcpOptionsRequest.builder().createDhcpDetails(CreateDhcpDetails.builder()
					.compartmentId(compartmentId).displayName(name).vcnId(vcnId).options(options).build())
				.build());
		String dhcpId = cdos.getDhcpOptions().getId();
		GetDhcpOptionsResponse res = h.waitForDhcpOptionsStatus(vn, dhcpId, DhcpOptions.LifecycleState.Available, "Creating DHCP Options "+name, false);
		return res.getDhcpOptions();
	}
	
	public SecurityList createSecList(VirtualNetwork vn, String compartmentId, String vcnId, String name, List<IngressSecurityRule> ir, List<EgressSecurityRule> er) throws Exception{
		CreateSecurityListResponse cslr = vn.createSecurityList(CreateSecurityListRequest.builder().createSecurityListDetails(CreateSecurityListDetails
				.builder().compartmentId(compartmentId).vcnId(vcnId).displayName(name).ingressSecurityRules(ir).egressSecurityRules(er).build()
				).build());
		String secListId = cslr.getSecurityList().getId();
		GetSecurityListResponse res = h.waitForSecurityListStatus(vn, secListId, SecurityList.LifecycleState.Available, "Creating Security List "+name, false);
		return res.getSecurityList();
	}
	
	// TERMINATOR //
	/**
	 * Delete all route tables with matching display name prefix.
	 * @param vn
	 * @param compartmentId
	 * @param vcnId
	 * @param namePrefix
	 * @throws Exception
	 */
	public void deleteRouteTableByNamePrefix(VirtualNetwork vn, String compartmentId, String vcnId, String namePrefix) throws Exception{
		List<RouteTable> rts = vn
				.listRouteTables(ListRouteTablesRequest.builder().compartmentId(compartmentId).vcnId(vcnId).build())
				.getItems();
		for(RouteTable rt:rts){
			if(rt.getDisplayName().startsWith(namePrefix)){
				vn.deleteRouteTable(DeleteRouteTableRequest.builder().rtId(rt.getId()).build());
				h.waitForRouteTableStatus(vn, rt.getId(), RouteTable.LifecycleState.Terminated, "Deleting Route Table "+rt.getDisplayName(), true);
			}
		}
	}
	
	/**
	 * Delete all SecList with matching display name prefix.
	 * 
	 * @param vn
	 * @param compartmentId
	 * @param vcnId
	 * @param secListNamePrefix
	 * @throws Exception
	 */
	public void deleteSecurityListByNamePrefix(VirtualNetwork vn, String compartmentId, String vcnId,
			String secListNamePrefix) throws Exception {
		List<SecurityList> secListsInVcn = vn
				.listSecurityLists(ListSecurityListsRequest.builder().compartmentId(compartmentId).vcnId(vcnId).build())
				.getItems();
		for (SecurityList sl : secListsInVcn) {
			if (sl.getDisplayName().startsWith(secListNamePrefix)) {
				vn.deleteSecurityList(DeleteSecurityListRequest.builder().securityListId(sl.getId()).build());
				h.waitForSecurityListStatus(vn, sl.getId(), SecurityList.LifecycleState.Terminated,
						"Deleting SEC LIST " + sl.getDisplayName(), true);
			}
		}
	}

	/**
	 * Delete all VCN with matching display name prefix.
	 * 
	 * @param vn
	 * @param compartmentId
	 * @param vcnNamePrefix
	 * @throws Exception
	 */
	public void deleteVcnByNamePrefix(VirtualNetwork vn, String compartmentId, String vcnNamePrefix) throws Exception {
		ListVcnsResponse res = vn.listVcns(ListVcnsRequest.builder().compartmentId(compartmentId).build());
		for (Vcn v : res.getItems()) {
			if (v.getDisplayName().startsWith(vcnNamePrefix)) {
				// Check defaults.
				String defaultRouteTableId = v.getDefaultRouteTableId();
				String defaultDhcpId = v.getDefaultDhcpOptionsId();
				String defaultSecList = v.getDefaultSecurityListId();
				// Remove route tables.
				List<RouteTable> rts = vn
						.listRouteTables(ListRouteTablesRequest.builder().compartmentId(compartmentId).vcnId(v.getId()).build())
						.getItems();
				for(RouteTable rt:rts){
					if(rt.getId().equals(defaultRouteTableId)){
						continue;
					}
					vn.deleteRouteTable(DeleteRouteTableRequest.builder().rtId(rt.getId()).build());
					h.waitForRouteTableStatus(vn, rt.getId(), RouteTable.LifecycleState.Terminated, "Deleting Route Table "+rt.getDisplayName(), true);
				}
				// Remove seclists.
				List<SecurityList> sls = vn
						.listSecurityLists(ListSecurityListsRequest.builder().compartmentId(compartmentId).vcnId(v.getId()).build())
						.getItems();
				for(SecurityList sl:sls){
					if(sl.getId().equals(defaultSecList)){
						continue;
					}
					vn.deleteSecurityList(DeleteSecurityListRequest.builder().securityListId(sl.getId()).build());
					h.waitForSecurityListStatus(vn, sl.getId(), SecurityList.LifecycleState.Terminated, "Deleting Security List "+sl.getDisplayName(), true);
				}
				// Remove VCN.
				vn.deleteVcn(DeleteVcnRequest.builder().vcnId(v.getId()).build());
				h.waitForVcnStatus(vn, v.getId(), Vcn.LifecycleState.Terminated, "Deleting VCN " + v.getDisplayName(),
						true);
			}
		}
	}
}
