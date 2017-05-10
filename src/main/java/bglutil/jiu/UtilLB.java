package bglutil.jiu;

import java.util.ArrayList;
import java.util.List;

import com.oracle.bmc.loadbalancer.LoadBalancer;
import com.oracle.bmc.loadbalancer.model.CreateBackendDetails;
import com.oracle.bmc.loadbalancer.model.CreateBackendSetDetails;
import com.oracle.bmc.loadbalancer.model.CreateListenerDetails;
import com.oracle.bmc.loadbalancer.model.CreateLoadBalancerDetails;
import com.oracle.bmc.loadbalancer.model.HealthCheckerDetails;
import com.oracle.bmc.loadbalancer.model.LoadBalancerPolicy;
import com.oracle.bmc.loadbalancer.model.LoadBalancerShape;
import com.oracle.bmc.loadbalancer.model.WorkRequest;
import com.oracle.bmc.loadbalancer.requests.CreateBackendRequest;
import com.oracle.bmc.loadbalancer.requests.CreateBackendSetRequest;
import com.oracle.bmc.loadbalancer.requests.CreateListenerRequest;
import com.oracle.bmc.loadbalancer.requests.CreateLoadBalancerRequest;
import com.oracle.bmc.loadbalancer.requests.ListLoadBalancersRequest;
import com.oracle.bmc.loadbalancer.requests.ListPoliciesRequest;
import com.oracle.bmc.loadbalancer.requests.ListShapesRequest;

import bglutil.jiu.common.UtilMain;


public class UtilLB extends UtilMain{
	public UtilLB(){
		super();
	}
	
	// GETTER //
	
	public List<LoadBalancerShape> getAllLoadBalancerShapeName(LoadBalancer lb, String compartmentId){
		List<LoadBalancerShape> shapes = lb.listShapes(ListShapesRequest.builder().compartmentId(compartmentId).build()).getItems();
		return shapes;
	}
	
	public List<LoadBalancerPolicy> getAllLoadBalancerPolicy(LoadBalancer lb, String compartmentId){
		List<LoadBalancerPolicy> pol = lb.listPolicies(ListPoliciesRequest.builder().compartmentId(compartmentId).build()).getItems();
		return pol;
	}
	
	public String getLoadBalancerIdByName(LoadBalancer lb, String name, String compartmentId){
		List<com.oracle.bmc.loadbalancer.model.LoadBalancer> lbs = lb.listLoadBalancers(ListLoadBalancersRequest.builder().compartmentId(compartmentId)
				.build()).getItems();
		String id = null;
		for(com.oracle.bmc.loadbalancer.model.LoadBalancer l:lbs){
			if(l.getDisplayName().equals(name)){
				id=l.getId();
				break;
			}
		}
		return id;
	}
	
	// CREATOR //
	public WorkRequest addListenerForLoadBalancer(LoadBalancer lb, String name, String protocol, int port, String loadBalancerId, String defaultBackendSetName) throws Exception{
		String wrId = lb.createListener(CreateListenerRequest.builder().loadBalancerId(loadBalancerId)
				.createListenerDetails(CreateListenerDetails.builder()
						.name(name)
						.port(port)
						.protocol(protocol)
						.defaultBackendSetName(defaultBackendSetName)
						.build()).build()).getOpcWorkRequestId();
		return h.waitForWorkReqeustStatus(lb, wrId, "Adding Listener "+name+" to "+defaultBackendSetName, false).getWorkRequest();
	}
	
	public WorkRequest addBackendToBackendSet(LoadBalancer lb, String backendSetName, String lbId, String backendIpAddress, int port) throws Exception{
		String wrId = lb.createBackend(CreateBackendRequest.builder().loadBalancerId(lbId).backendSetName(backendSetName)
				.createBackendDetails(CreateBackendDetails.builder()
						.ipAddress(backendIpAddress).port(port)
						.build()).build()).getOpcWorkRequestId();
		return h.waitForWorkReqeustStatus(lb, wrId, "Adding Backend "+backendIpAddress+" to "+backendSetName, false).getWorkRequest();
	}
	
	public WorkRequest addBackendSetForLoadBalancer(LoadBalancer lb, String name, String lbId, String lbPolicy, String hcProtocol, int hcPort, String hcUrlPath) throws Exception{
		String wrId = lb.createBackendSet(CreateBackendSetRequest.builder()
				.loadBalancerId(lbId)
				.createBackendSetDetails(CreateBackendSetDetails.builder()
						.name(name)
						.healthChecker(HealthCheckerDetails.builder()
								.port(hcPort)
								.protocol(hcProtocol)
								.urlPath(hcUrlPath).build())
						.policy(lbPolicy)
						.build()).build()).getOpcWorkRequestId();
		return h.waitForWorkReqeustStatus(lb, wrId, "Creating Backend Set "+name, false).getWorkRequest();
	
	}
	
	public WorkRequest createLoadBalancer(LoadBalancer lb, String name, String shapeName, String subnet1Id, String subnet2Id, String compartmentId) throws Exception{
		
		List<String> subnetIds = new ArrayList<String>();
		subnetIds.add(subnet1Id); subnetIds.add(subnet2Id);
		
		String createLbReqId = lb.createLoadBalancer(CreateLoadBalancerRequest.builder()
				.createLoadBalancerDetails(CreateLoadBalancerDetails.builder()
						.compartmentId(compartmentId)
						.displayName(name)
						.shapeName(shapeName)
						.subnetIds(subnetIds)
						.build()).build()).getOpcWorkRequestId();
		return h.waitForWorkReqeustStatus(lb, createLbReqId, "Creating Load Balancer "+name, false).getWorkRequest();
	}
}
