package bglutil.jiu.common;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Base64;


import com.oracle.bmc.core.Compute;
import com.oracle.bmc.core.ComputeWaiters;
import com.oracle.bmc.core.VirtualNetwork;
import com.oracle.bmc.core.VirtualNetworkWaiters;
import com.oracle.bmc.core.model.DhcpOptions;
import com.oracle.bmc.core.model.Instance;
import com.oracle.bmc.core.model.InternetGateway;
import com.oracle.bmc.core.model.RouteTable;
import com.oracle.bmc.core.model.SecurityList;
import com.oracle.bmc.core.model.Subnet;
import com.oracle.bmc.core.model.Vcn;
import com.oracle.bmc.core.requests.GetDhcpOptionsRequest;
import com.oracle.bmc.core.requests.GetInstanceRequest;
import com.oracle.bmc.core.requests.GetInternetGatewayRequest;
import com.oracle.bmc.core.requests.GetRouteTableRequest;
import com.oracle.bmc.core.requests.GetSecurityListRequest;
import com.oracle.bmc.core.requests.GetSubnetRequest;
import com.oracle.bmc.core.requests.GetVcnRequest;
import com.oracle.bmc.core.responses.GetDhcpOptionsResponse;
import com.oracle.bmc.core.responses.GetInstanceResponse;
import com.oracle.bmc.core.responses.GetInternetGatewayResponse;
import com.oracle.bmc.core.responses.GetRouteTableResponse;
import com.oracle.bmc.core.responses.GetSecurityListResponse;
import com.oracle.bmc.core.responses.GetSubnetResponse;
import com.oracle.bmc.core.responses.GetVcnResponse;
import com.oracle.bmc.identity.Identity;
import com.oracle.bmc.identity.IdentityWaiters;
import com.oracle.bmc.identity.model.Compartment;
import com.oracle.bmc.identity.requests.GetCompartmentRequest;
import com.oracle.bmc.identity.responses.GetCompartmentResponse;
import com.oracle.bmc.loadbalancer.LoadBalancer;
import com.oracle.bmc.loadbalancer.LoadBalancerWaiters;
import com.oracle.bmc.loadbalancer.requests.GetWorkRequestRequest;
import com.oracle.bmc.loadbalancer.responses.GetWorkRequestResponse;

import bglutil.jiu.Jiu;

/**
 * Helper center used by all other classes.
 * @author guanglei
 *
 */
public class Helper {
	
	private Speaker sk = new Speaker(Speaker.RenderType.CONSOLE);
	
	public static char BUILDING = Character.toChars(9749)[0];
	public static char REMOVING = Character.toChars(9762)[0];
	public static char STAR = Character.toChars(9733)[0];
	public static char FIST = Character.toChars(9994)[0];
	
	/* progressStop bean */
	private boolean progressStop;
	
	public boolean isProgressStop() {
		return progressStop;
	}
	
	public void setProgressStop(boolean progressStop) {
		this.progressStop = progressStop;
	}
	/* progressStop bean */

	private static String[] processingCharacters = new String[] { "\\", "|", "/", "-" };
	
	public String base64Encode(String plain){
		byte[] b = null;
		String base64 = null;
		/*
		try {
			b=plain.getBytes("utf-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}*/
		b=plain.getBytes();
		if(b!=null){
			base64 = Base64.getUrlEncoder().encodeToString(b);
		}
		return base64;
	}
	
	public String base64Decode(String base64){
		byte[] b = null;
		String plain = null;
		if(base64 != null){
			try {
				b = Base64.getUrlDecoder().decode(base64);
				plain = new String(b, "utf-8");
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return plain;
	}
	
	public String base64EncodeFromFile(String file){
		StringBuffer sb = new StringBuffer();
		String line = null;
		try {
			BufferedReader br = new BufferedReader(new FileReader(new File(file)));
			line = br.readLine();
			while(line!=null){
				sb.append(line+"\n");
				line = br.readLine();
			}
			br.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		String script = new String(sb);
		sk.printTitle(0,"File Content:");
		System.out.println(script);
		if(script.equals("")){
			throw new RuntimeException("File Content is NULL!!");
		}
		String base64 = this.base64Encode(script);
		return base64;
	}
	
	/**
	 * Enable the '-h' option for methods which call help.
	 * @param help
	 * @param helpMessage
	 */
	public void help(String help, String helpMessage) {
		if (help.equals("-h")) {
			StackTraceElement element = Thread.currentThread().getStackTrace()[2];
			System.out.println("\n j " + element.getMethodName() + " " + helpMessage + "\n");
			System.exit(0);
		}
	}

	/**
	 * Help those who cannot remember command names.
	 * @param commandPrefix
	 */
	public static void search(String commandPrefix) {
		Method[] allMethods = Jiu.class.getDeclaredMethods();
		for (Method m : allMethods) {
			if (Jiu.SKIPPED_METHODS.contains(m.getName())) {
				continue;
			}
			if (Modifier.isPublic(m.getModifiers()) && m.getName().startsWith(commandPrefix)) {
				System.out.println(m.getName());
			}
		}
	}
	
	/**
	 * Simple title printer. Use Speaker instead.
	 * @param title
	 */
	public void printSimgpleTitle(String title) {
		StringBuffer line = new StringBuffer("\n");
		for (int c = 0; c < title.length(); c++) {
			line.append('_');
		}
		System.out.println(line + "\\");
		System.out.println(title + ":\n");
	}
	
	/**
	 * Simple wait.
	 * @param msec
	 */
	public static void wait(int msec) {
		try {
			Thread.sleep(msec);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Used by waitForXxxStatus() to print a done marker to console.
	 * @param mark
	 * @throws InterruptedException
	 */
	public void done(char mark) throws InterruptedException{
		this.setProgressStop(true);
		Thread.sleep(1000);
		System.out.println(" DONE "+mark);
	}
	
	/* waitForXxxStatus */
	
	
	public GetWorkRequestResponse waitForWorkReqeustStatus(LoadBalancer lb, String workRequestId, String waitMessage, boolean tearDown) throws Exception{
		char mark = tearDown?Helper.REMOVING:Helper.BUILDING;
		LoadBalancerWaiters lbw = lb.getWaiters();
		this.processingV2(waitMessage+" ... ");
		GetWorkRequestResponse res = lbw.forWorkRequest(GetWorkRequestRequest.builder().workRequestId(workRequestId).build()).execute();
		this.done(mark);
		return res;
	}
	
	public GetInstanceResponse[] silentWaitForInstanceStatus(Compute c, String[] instanceIds, Instance.LifecycleState state, boolean tearDown) throws Exception{
		ComputeWaiters cw = c.getWaiters();
		GetInstanceResponse[] ress = new GetInstanceResponse[instanceIds.length]; 
		for(int i=0;i<instanceIds.length;i++){
			ress[i] = cw.forInstance(GetInstanceRequest.builder().instanceId(instanceIds[i]).build(), state).execute();
			
		}
		return ress;
	}
	
	public GetInstanceResponse waitForInstanceStatus(Compute c, String instanceId, Instance.LifecycleState state, String waitMessage, boolean tearDown) throws Exception{
		char mark = tearDown?Helper.REMOVING:Helper.BUILDING;
		ComputeWaiters cw = c.getWaiters();
		this.processingV2(waitMessage+" ... ");
		GetInstanceResponse res = cw.forInstance(GetInstanceRequest.builder().instanceId(instanceId).build(), state).execute();
		this.done(mark);
		return res;
	}
	
	public GetSubnetResponse waitForSubnetStatus(VirtualNetwork vn, String subnetId, Subnet.LifecycleState state, String waitMessage, boolean tearDown) throws Exception{
		char mark = tearDown?Helper.REMOVING:Helper.BUILDING;
		VirtualNetworkWaiters slw = vn.getWaiters();
		this.processingV2(waitMessage+" ... ");
		GetSubnetResponse res = slw.forSubnet(GetSubnetRequest.builder().subnetId(subnetId).build(), state).execute();
		this.done(mark);
		return res;
	}
	
	public GetDhcpOptionsResponse waitForDhcpOptionsStatus(VirtualNetwork vn, String dhcpId, DhcpOptions.LifecycleState state, String waitMessage, boolean tearDown) throws Exception{
		char mark = tearDown?Helper.REMOVING:Helper.BUILDING;
		VirtualNetworkWaiters slw = vn.getWaiters();
		this.processingV2(waitMessage+" ... ");
		GetDhcpOptionsResponse res = slw.forDhcpOptions(GetDhcpOptionsRequest.builder().dhcpId(dhcpId).build(), state).execute();
		this.done(mark);
		return res;
	}
	
	public GetRouteTableResponse waitForRouteTableStatus(VirtualNetwork vn, String rtId, RouteTable.LifecycleState state, String waitMessage, boolean tearDown) throws Exception{
		char mark = tearDown?Helper.REMOVING:Helper.BUILDING;
		VirtualNetworkWaiters slw = vn.getWaiters();
		this.processingV2(waitMessage+" ... ");
		GetRouteTableResponse res = slw.forRouteTable(GetRouteTableRequest.builder().rtId(rtId).build(), state).execute();
		this.done(mark);
		return res;
	}
	
	public GetInternetGatewayResponse waitForIgwStatus(VirtualNetwork vn, String igwId, InternetGateway.LifecycleState state, String waitMessage, boolean tearDown) throws Exception{
		char mark = tearDown?Helper.REMOVING:Helper.BUILDING;
		VirtualNetworkWaiters slw = vn.getWaiters();
		this.processingV2(waitMessage+" ... ");
		GetInternetGatewayResponse res = slw.forInternetGateway(GetInternetGatewayRequest.builder().igId(igwId).build(), state).execute();
		this.done(mark);
		return res;
	}
	
	public GetSecurityListResponse waitForSecurityListStatus(VirtualNetwork vn, String secListId, SecurityList.LifecycleState state, String waitMessage, boolean tearDown) throws Exception{
		char mark = tearDown?Helper.REMOVING:Helper.BUILDING;
		VirtualNetworkWaiters slw = vn.getWaiters();
		this.processingV2(waitMessage+" ... ");
		GetSecurityListResponse res = slw.forSecurityList(GetSecurityListRequest.builder().securityListId(secListId).build(),state).execute();
		this.done(mark);
		return res;
	}
	
	public GetVcnResponse waitForVcnStatus(VirtualNetwork vn, String vcnId, Vcn.LifecycleState state, String waitMessage, boolean tearDown) throws Exception{
		char mark = tearDown?Helper.REMOVING:Helper.BUILDING;
		VirtualNetworkWaiters vnw = vn.getWaiters();
		this.processingV2(waitMessage+" ... ");
		GetVcnResponse res = vnw.forVcn(GetVcnRequest.builder().vcnId(vcnId).build(), state).execute();
		this.done(mark);
		return res;
	}
	
	public GetCompartmentResponse waitForCompartmentStatus(Identity id, String compartmentId, Compartment.LifecycleState state, String waitMessage, boolean tearDown) throws Exception{
		char mark = tearDown?Helper.REMOVING:Helper.BUILDING;
		IdentityWaiters iw = id.getWaiters();
		this.processingV2(waitMessage+" ... ");
		GetCompartmentResponse res = iw.forCompartment(GetCompartmentRequest.builder().compartmentId(compartmentId).build(), state).execute();
		this.done(mark);
		return res;
	}
	
	/* waitForXxxStatus */
	
	
	/**
	 * Output in-progress icon and return the thread for further termination.
	 * @param s
	 * @return
	 */
	public Thread processingV2(String s) {
		InProgress ip = new InProgress(Character.toChars(9608)[0]+" "+s, 1);
		ip.start();
		return ip;
	}

	class InProgress extends Thread {

		private String s;
		private int m;
		private long startTime;
		private long endTime;

		public InProgress(String s, int m) {
			this.s = s;
			this.m = m;
		}

		@Override
		public void run() {
			this.startTime = System.currentTimeMillis();
			while (true) {
				StringBuffer bb = new StringBuffer("\b\b\b");
				for (int i = 0; i < s.length(); i++) {
					bb.append("\b");
				}
				System.out.print(new String(bb) + s + " " + processingCharacters[m % 4] + " ");
				m++;
				if (progressStop == true) {
					this.endTime = System.currentTimeMillis();
					//System.out.print(new String(bb) + s + " " + processingCharacters[m % 4] + " "+(this.endTime-this.startTime)+"(ms)");
					System.out.print(new String(bb) + s + " "+(this.endTime-this.startTime)+"(ms)");
					progressStop = false;
					break;
				}
				try {
					Thread.sleep(50);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
	}
}
