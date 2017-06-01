package bglutil.jiu;

import java.util.ArrayList;
import java.util.List;

import com.oracle.bmc.loadbalancer.LoadBalancer;
import com.oracle.bmc.loadbalancer.model.Backend;
import com.oracle.bmc.loadbalancer.model.CreateBackendDetails;
import com.oracle.bmc.loadbalancer.model.CreateBackendSetDetails;
import com.oracle.bmc.loadbalancer.model.CreateListenerDetails;
import com.oracle.bmc.loadbalancer.model.CreateLoadBalancerDetails;
import com.oracle.bmc.loadbalancer.model.HealthCheckerDetails;
import com.oracle.bmc.loadbalancer.model.LoadBalancerPolicy;
import com.oracle.bmc.loadbalancer.model.LoadBalancerShape;
import com.oracle.bmc.loadbalancer.model.UpdateBackendDetails;
import com.oracle.bmc.loadbalancer.model.UpdateLoadBalancerDetails;
import com.oracle.bmc.loadbalancer.model.WorkRequest;
import com.oracle.bmc.loadbalancer.requests.CreateBackendRequest;
import com.oracle.bmc.loadbalancer.requests.CreateBackendSetRequest;
import com.oracle.bmc.loadbalancer.requests.CreateListenerRequest;
import com.oracle.bmc.loadbalancer.requests.CreateLoadBalancerRequest;
import com.oracle.bmc.loadbalancer.requests.DeleteLoadBalancerRequest;
import com.oracle.bmc.loadbalancer.requests.GetBackendRequest;
import com.oracle.bmc.loadbalancer.requests.ListLoadBalancersRequest;
import com.oracle.bmc.loadbalancer.requests.ListPoliciesRequest;
import com.oracle.bmc.loadbalancer.requests.ListShapesRequest;
import com.oracle.bmc.loadbalancer.requests.UpdateBackendRequest;
import com.oracle.bmc.loadbalancer.requests.UpdateLoadBalancerRequest;

import bglutil.jiu.common.UtilMain;


public class UtilLB extends UtilMain{
	public UtilLB(){
		super();
	}
	
	// GETTER //
	
	/**
	 * Get available load balancer shapes.
	 * @param lb
	 * @param compartmentId
	 * @return
	 */
	public List<LoadBalancerShape> getAllLoadBalancerShapeName(LoadBalancer lb, String compartmentId){
		List<LoadBalancerShape> shapes = lb.listShapes(ListShapesRequest.builder().compartmentId(compartmentId).build()).getItems();
		return shapes;
	}
	
	/**
	 * Get available load balancer policies.
	 * @param lb
	 * @param compartmentId
	 * @return
	 */
	public List<LoadBalancerPolicy> getAllLoadBalancerPolicy(LoadBalancer lb, String compartmentId){
		List<LoadBalancerPolicy> pol = lb.listPolicies(ListPoliciesRequest.builder().compartmentId(compartmentId).build()).getItems();
		return pol;
	}
	
	/**
	 * Load balancer Name to ID.
	 * @param lb
	 * @param name
	 * @param compartmentId
	 * @return
	 */
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
	
	// CHANGER //
	
	/**
	 * Change the backend status.
	 * @param lb
	 * @param lbId
	 * @param backendSetName
	 * @param backendName
	 * @param w
	 * @param drain
	 * @param offline
	 * @param backup
	 * @return
	 * @throws Exception
	 */
	public WorkRequest changeBackendSetting(LoadBalancer lb, String lbId, String backendSetName, String backendName, Integer w, Boolean drain, Boolean offline, Boolean backup) throws Exception{
		Backend be = lb.getBackend(GetBackendRequest.builder().loadBalancerId(lbId).backendName(backendName).backendSetName(backendSetName).build()).getBackend();
		String wrId = lb.updateBackend(UpdateBackendRequest.builder().loadBalancerId(lbId).backendSetName(backendSetName).backendName(backendName)
				.updateBackendDetails(UpdateBackendDetails.builder()
						.weight(w!=null?w:be.getWeight())
						.drain(drain!=null?drain:be.getDrain())
						.offline(offline!=null?offline:be.getOffline())
						.backup(backup!=null?backup:be.getBackup())
						.build()).build()).getOpcWorkRequestId();
		return h.waitForWorkReqeustStatus(lb, wrId, "Changing "+backendName, false).getWorkRequest();
	}
	
	// CREATOR //
	/**
	 * Add listener to load balancer.
	 * @param lb
	 * @param name
	 * @param protocol
	 * @param port
	 * @param loadBalancerId
	 * @param defaultBackendSetName
	 * @return
	 * @throws Exception
	 */
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
	
	/**
	 * Add backend server to backendset.
	 * @param lb
	 * @param backendSetName
	 * @param lbId
	 * @param backendIpAddress
	 * @param port
	 * @return
	 * @throws Exception
	 */
	public WorkRequest addBackendToBackendSet(LoadBalancer lb, String backendSetName, String lbId, String backendIpAddress, int port) throws Exception{
		String wrId = lb.createBackend(CreateBackendRequest.builder().loadBalancerId(lbId).backendSetName(backendSetName)
				.createBackendDetails(CreateBackendDetails.builder()
						.ipAddress(backendIpAddress).port(port).weight(10)
						.build()).build()).getOpcWorkRequestId();
		return h.waitForWorkReqeustStatus(lb, wrId, "Adding Backend "+backendIpAddress+" to "+backendSetName, false).getWorkRequest();
	}
	
	/**
	 * Add backendset to load balancer.
	 * @param lb
	 * @param name
	 * @param lbId
	 * @param lbPolicy
	 * @param hcProtocol
	 * @param hcPort
	 * @param hcUrlPath
	 * @return
	 * @throws Exception
	 */
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
	
	/**
	 * Create a new load balancer.
	 * @param lb
	 * @param name
	 * @param shapeName
	 * @param subnet1Id
	 * @param subnet2Id
	 * @param compartmentId
	 * @return
	 * @throws Exception
	 */
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
	
	// DELETER
	
	/**
	 * Remove LB in compartment by name.
	 * @param lb
	 * @param lbName
	 * @param compartmentId
	 * @throws Exception
	 */
	public void deleteLoadBalancerByName(LoadBalancer lb, String lbName, String compartmentId) throws Exception{
		List<com.oracle.bmc.loadbalancer.model.LoadBalancer> lbs = lb.listLoadBalancers(ListLoadBalancersRequest.builder()
				.compartmentId(compartmentId)
				.build()).getItems();
		for(com.oracle.bmc.loadbalancer.model.LoadBalancer l:lbs){
			if(l.getDisplayName().equals(lbName)){
				String wrid = lb.deleteLoadBalancer(DeleteLoadBalancerRequest.builder()
						.loadBalancerId(l.getId())
						.build()).getOpcWorkRequestId();
				h.waitForWorkReqeustStatus(lb, wrid, "Deleting Load Balancer "+lbName, true).getWorkRequest();
			}
		}
	}
}
