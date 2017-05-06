package bglutil.jiu;

import java.util.ArrayList;
import java.util.List;

import com.oracle.bmc.loadbalancer.LoadBalancer;
import com.oracle.bmc.loadbalancer.model.CreateLoadBalancerDetails;
import com.oracle.bmc.loadbalancer.model.LoadBalancerShape;
import com.oracle.bmc.loadbalancer.model.WorkRequest;
import com.oracle.bmc.loadbalancer.requests.CreateLoadBalancerRequest;
import com.oracle.bmc.loadbalancer.requests.GetWorkRequestRequest;
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
	
	// CREATOR //
	
	public void createLoadBalancer(LoadBalancer lb, String name, String shapeName, String subnet1Id, String subnet2Id, String compartmentId) throws Exception{
		
		List<String> subnetIds = new ArrayList<String>();
		subnetIds.add(subnet1Id); subnetIds.add(subnet2Id);
		
		String createLbReqId = lb.createLoadBalancer(CreateLoadBalancerRequest.builder()
				.createLoadBalancerDetails(CreateLoadBalancerDetails.builder()
						.compartmentId(compartmentId)
						.displayName(name)
						.shapeName(shapeName)
						.subnetIds(subnetIds)
						.build()).build()).getOpcWorkRequestId();
		WorkRequest wr = lb.getWorkRequest(GetWorkRequestRequest.builder().workRequestId(createLbReqId).build()).getWorkRequest();
		h.waitForWorkReqeustStatus(lb, createLbReqId, "Creating Load Balancer "+name, false);
	}
}
