package bglutil.jiu;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;


import com.oracle.bmc.core.Compute;
import com.oracle.bmc.core.VirtualNetwork;
import com.oracle.bmc.core.model.AttachIScsiVolumeDetails;
import com.oracle.bmc.core.model.AttachVnicDetails;
import com.oracle.bmc.core.model.CreateImageDetails;
import com.oracle.bmc.core.model.CreateVnicDetails;
import com.oracle.bmc.core.model.IScsiVolumeAttachment;
import com.oracle.bmc.core.model.Image;
import com.oracle.bmc.core.model.Instance;
import com.oracle.bmc.core.model.LaunchInstanceDetails;
import com.oracle.bmc.core.model.Shape;
import com.oracle.bmc.core.model.Vnic;
import com.oracle.bmc.core.model.VnicAttachment;
import com.oracle.bmc.core.model.VolumeAttachment;
import com.oracle.bmc.core.requests.AttachVnicRequest;
import com.oracle.bmc.core.requests.AttachVolumeRequest;
import com.oracle.bmc.core.requests.CreateImageRequest;
import com.oracle.bmc.core.requests.DeleteImageRequest;
import com.oracle.bmc.core.requests.DetachVnicRequest;
import com.oracle.bmc.core.requests.DetachVolumeRequest;
import com.oracle.bmc.core.requests.GetImageRequest;
import com.oracle.bmc.core.requests.GetInstanceRequest;
import com.oracle.bmc.core.requests.GetVnicAttachmentRequest;
import com.oracle.bmc.core.requests.GetVnicRequest;
import com.oracle.bmc.core.requests.GetVolumeAttachmentRequest;
import com.oracle.bmc.core.requests.LaunchInstanceRequest;
import com.oracle.bmc.core.requests.ListImagesRequest;
import com.oracle.bmc.core.requests.ListInstancesRequest;
import com.oracle.bmc.core.requests.ListShapesRequest;
import com.oracle.bmc.core.requests.ListVnicAttachmentsRequest;
import com.oracle.bmc.core.requests.ListVolumeAttachmentsRequest;
import com.oracle.bmc.core.requests.TerminateInstanceRequest;
import com.oracle.bmc.core.responses.AttachVnicResponse;
import com.oracle.bmc.core.responses.GetInstanceResponse;
import com.oracle.bmc.core.responses.GetVnicAttachmentResponse;
import com.oracle.bmc.core.responses.LaunchInstanceResponse;

import bglutil.jiu.common.Helper;
import bglutil.jiu.common.UtilMain;

/**
 * Compute service utilities.
 * @author bgl
 *
 */
public class UtilCompute extends UtilMain{
	public UtilCompute() {
		super();
	}
	
	/**
	 * Create a custom image from existing instance.
	 * @param c
	 * @param name
	 * @param instanceId
	 * @param compartmentId
	 * @return
	 */
	public Image createImage(Compute c, String name, String instanceId, String compartmentId){
		Image img = c.createImage(CreateImageRequest.builder().createImageDetails(CreateImageDetails.builder()
				.compartmentId(compartmentId)
				.displayName(name)
				.instanceId(instanceId).build()
				).build()).getImage();
		return img;
	}
	
	/**
	 * Get volume attachment information in a compartment.
	 * @param c
	 * @param i
	 * @param compartmentId
	 * @return
	 */
	public List<VolumeAttachment> getVolumeAttachment(Compute c, Instance i, String compartmentId){
		return c.listVolumeAttachments(ListVolumeAttachmentsRequest.builder().instanceId(i.getId())
				.availabilityDomain(i.getAvailabilityDomain())
				.compartmentId(compartmentId).build()).getItems();
	}
	
	/**
	 * Get private ip addresses on VM instance.
	 * @param c
	 * @param vn
	 * @param instanceId
	 * @param compartmentId
	 * @return
	 */
	public List<String> getPrivateIpByInstanceId(Compute c, VirtualNetwork vn, String instanceId, String compartmentId){
		List<String> ips = new ArrayList<String>();
		List<Vnic> atchs = this.getVnicByInstanceId(c, vn, instanceId, compartmentId);
		for(Vnic v:atchs){
			ips.add(v.getPrivateIp());
		}
		return ips;
	}
	
	/**
	 * Get VNICs on VM instance.
	 * @param c
	 * @param vn
	 * @param instanceId
	 * @param compartmentId
	 * @return
	 */
	public List<Vnic> getVnicByInstanceId(Compute c, VirtualNetwork vn, String instanceId, String compartmentId){
		List<VnicAttachment> atchs = c.listVnicAttachments(ListVnicAttachmentsRequest.builder().compartmentId(compartmentId).instanceId(instanceId).build()).getItems();
		List<Vnic> vnics = new ArrayList<Vnic>();
		for(VnicAttachment va: atchs){
			VnicAttachment.LifecycleState state = va.getLifecycleState();
			if(state.equals(VnicAttachment.LifecycleState.Attached)
					|| state.equals(VnicAttachment.LifecycleState.Attaching)){
				String vid = va.getVnicId();
				Vnic v = vn.getVnic(GetVnicRequest.builder().vnicId(vid).build()).getVnic();
				vnics.add(v);
			}
		}
		return vnics;
	}
	
	/**
	 * Get VNIC on VM instance, based on VNIC name.
	 * @param c
	 * @param vn
	 * @param instanceId
	 * @param vnicName
	 * @param compartmentId
	 * @return
	 */
	public Vnic getVnicByInstanceIdAndVnicName(Compute c, VirtualNetwork vn, String instanceId, String vnicName, String compartmentId){
		Vnic nic = null;
		List<Vnic> vnics = this.getVnicByInstanceId(c, vn, instanceId, compartmentId);
		for(Vnic v:vnics){
			if(v.getDisplayName().equals(vnicName)){
				nic = v;
				break;
			}
		}
		return nic;
	}
	
	/**
	 * Instance Name to Instance ID.
	 * @param c
	 * @param name
	 * @param compartmentId
	 * @return
	 */
	public String getInstanceIdByName(Compute c, String name, String compartmentId){
		List<Instance> instances = c.listInstances(ListInstancesRequest.builder().compartmentId(compartmentId).build()).getItems();
		String ocid = null;
		for(Instance i:instances){
			if(i.getDisplayName().equals(name) && !i.getLifecycleState().equals(Instance.LifecycleState.Terminated)){
				ocid = i.getId();
			}
		}
		return ocid;
	}
	
	/**
	 * Get all instances in a compartment.
	 * @param c
	 * @param compartmentId
	 * @return
	 */
	public List<Instance> getAllInstance(Compute c, String compartmentId){
		List<Instance> instances = c.listInstances(ListInstancesRequest.builder().compartmentId(compartmentId).build()).getItems();
		return instances;
	}
	
	/**
	 * Get instance java object by name searching.
	 * @param c
	 * @param name
	 * @param compartmentId
	 * @return
	 */
	public Instance getInstanceByName(Compute c, String name, String compartmentId){
		List<Instance> instances = c.listInstances(ListInstancesRequest.builder().compartmentId(compartmentId).build()).getItems();
		Instance inst = null;
		for(Instance i:instances){
			if(i.getDisplayName().equals(name) && !i.getLifecycleState().equals(Instance.LifecycleState.Terminated)){
				inst = i;
			}
		}
		return inst;
	}
	
	/**
	 * Get instance private/public ip address by name searching.
	 * @param c
	 * @param vn
	 * @param name
	 * @param privateOrPublic
	 * @param compartmentId
	 * @return
	 */
	public String getInstanceIpByName(Compute c, VirtualNetwork vn, String name, String privateOrPublic, String compartmentId){
		String instanceId = this.getInstanceIdByName(c, name, compartmentId);
		List<Vnic> vnics = this.getVnicByInstanceId(c, vn, instanceId, compartmentId);
		String ret = null;
		for(Vnic v:vnics){
			if(privateOrPublic.equals("private")){
				ret = v.getPrivateIp();
				break;
			}
			else if(privateOrPublic.equals("public")){
				ret = v.getPublicIp();
				break;
			}
		}
		return ret;
	}
	
	/**
	 * Instance ID to Instance Name.
	 * @param c
	 * @param instanceId
	 * @return
	 */
	public String getInstanceNameById(Compute c, String instanceId){
		return c.getInstance(GetInstanceRequest.builder().instanceId(instanceId).build()).getInstance().getDisplayName();
	}
	
	/**
	 * Image ID to Image Name.
	 * @param c
	 * @param imageId
	 * @return
	 */
	public String getImageNameById(Compute c, String imageId){
		return c.getImage(GetImageRequest.builder().imageId(imageId).build()).getImage().getDisplayName();
	}
	
	/**
	 * Image Name to Image ID.
	 * @param c
	 * @param imageName
	 * @param compartmentId
	 * @return
	 */
	public String getImageIdByName(Compute c, String imageName, String compartmentId){
		List<Image> images = this.getAllImage(c, compartmentId);
		String ret = null;
		for(Image i:images){
			if(i.getDisplayName().equals(imageName)){
				ret = i.getId();
				break;
			}
		}
		return ret;
	}
	
	/**
	 * Get all available images.
	 * @param c
	 * @param compartmentId
	 * @return
	 */
	public List<Image> getAllImage(Compute c, String compartmentId){
		return c.listImages(ListImagesRequest.builder().compartmentId(compartmentId).build()).getItems();
	}
	
	/**
	 * Get all available shapes.
	 * @param c
	 * @param compartmentId
	 * @return
	 */
	public Set<String> getAllShape(Compute c, String compartmentId){
		TreeSet<String> ts = new TreeSet<String>();
		for(Shape s:c.listShapes(ListShapesRequest.builder().compartmentId(compartmentId).build()).getItems()){
			String sh = s.getShape();
			String ss = null;
			if(sh.equals("BM.Standard1.36")){
				ss = "bm1003 "+sh+" [36 OCPU, 72 vCPU, 256 GB, 10 Gbps, 16 VNICs]";
			}
			else if(sh.equals("BM.HighIO1.36")){
				ss = "bm1002 "+sh+" [36 OCPU, 72 vCPU, 512 GB, 12.8 TB NVMe, 10 Gbps, 16 VNICs]";
			}
			else if(sh.equals("BM.DenseIO1.36")){
				ss = "bm1001 "+sh+" [36 OCPU, 72 vCPU, 512 GB, 28.8 TB NVMe, 10 Gbps, 16 VNICs]";
			}
			else if(sh.equals("VM.Standard1.1")){
				ss = "vm1008 " +sh+" [1 OCPU, 2 vCPU, 7 GB, Up to 600 Mbps, 2 VNICs]";
			}
			else if(sh.equals("VM.Standard1.2")){
				ss = "vm1007 "+sh+" [2 OCPU, 4 vCPU, 14 GB, Up to 1.2 Gbps, 2 VNICs]";
			}
			else if(sh.equals("VM.Standard1.4")){
				ss = "vm1006 "+sh+" [4 OCPU, 8 vCPU, 28 GB, 1.2 Gbps, 2 VNICs]";
			}
			else if(sh.equals("VM.Standard1.8")){
				ss = "vm1005 "+sh+" [8 OCPU, 16 vCPU, 56 GB, 2.4 Gbps, 4 VNICs]";
			}
			else if(sh.equals("VM.Standard1.16")){
				ss = "vm1004 "+sh+" [16 OCPU, 32 vCPU, 112 GB, 4.8 Gbps, 8 VNICs]";
			}
			else if(sh.equals("VM.DenseIO1.4")){
				ss = "vm1003 "+sh+" [4 OCPU, 8 vCPU, 60 GB, 3.2 TB NVMe, 1.2 Gbps, 2 VNICs]";
			}
			else if(sh.equals("VM.DenseIO1.8")){
				ss = "vm1002 "+sh+" [8 OCPU, 16 vCPU, 120 GB, 6.4 TB NVMe, 2.4 Gbps, 4 VNICs]";
			}
			else if(sh.equals("VM.DenseIO1.16")){
				ss = "vm1001 "+sh+" [16 OCPU, 32 vCPU, 240 GB, 12.8 TB NVMe, 4.8 Gbps, 8 VNICs]";
			}
			else if(sh.equals("VM.DenseIO2.24")){
				ss = "vm2001 "+sh+" [24 OCPU, 48 vCPU, 320 GB, 25.6 TB NVMe, 24.6 Gbps, 12 VNICs]";
			}
			else if(sh.equals("VM.DenseIO2.16")){
				ss = "vm2002 "+sh+" [16 OCPU, 32 vCPU, 240 GB, 12.8 TB NVMe, 16.4 Gbps, 8 VNICs]";
			}
			else if(sh.equals("VM.DenseIO2.8")){
				ss = "vm2003 "+sh+" [8 OCPU, 16 vCPU, 120 GB, 6.4 TB NVMe, 8.2 Gbps, 4 VNICs]";
			}
			else if(sh.equals("VM.Standard2.24")){
				ss = "vm2004 "+sh+" [24 OCPU, 48 vCPU, 320 GB, 24.6 Gbps, 12 VNICs]";
			}
			else if(sh.equals("VM.Standard2.16")){
				ss = "vm2005 "+sh+" [16 OCPU, 32 vCPU, 240 GB, 16.4 Gbps, 8 VNICs]";
			}
			else if(sh.equals("VM.Standard2.8")){
				ss = "vm2006 "+sh+" [8 OCPU, 16 vCPU, 120 GB, 8.2 Gbps, 4 VNICs]";
			}
			else if(sh.equals("VM.Standard2.4")){
				ss = "vm2007 "+sh+" [4 OCPU, 8 vCPU, 60 GB, 4.1 Gbps, 2 VNICs]";
			}
			else if(sh.equals("VM.Standard2.2")){
				ss = "vm2008 "+sh+" [2 OCPU, 4 vCPU, 30 GB, 2 Gbps, 2 VNICs]";
			}
			else if(sh.equals("VM.Standard2.1")){
				ss = "vm2009 "+sh+" [1 OCPU, 2 vCPU, 15 GB, 1 Gbps, 2 VNICs]";
			}
			else if(sh.equals("VM.Standard2.1")){
				ss = "vm2009 "+sh+" [1 OCPU, 2 vCPU, 15 GB, 1 Gbps, 2 VNICs]";
			}
			else if(sh.equals("BM.DenseIO2.52")){
				ss = "bm2001 "+sh+" [52 OCPU, 104 vCPU, 768 GB, 51.2 TB NVMe, 2 x 25 Gbps, 24 VNICs]";
			}
			else if(sh.equals("BM.Standard2.52")){
				ss = "bm2002 "+sh+" [52 OCPU, 104 vCPU, 768 GB, 2 x 25 Gbps, 24 VNICs]";
			}
			else if(sh.equals("BM.GPU2.2")){
				ss = "bm2003 "+sh+" [28 OCPU, 56 vCPU, 2 x P100 GPU, 192 GB, 2 x 25 Gbps, 24 VNICs]";
			}
			else{
				ss = sh;
			}
			ts.add(ss);
		}
		return ts;
	}
	
	/**
	 * Terminate Vm instance.
	 * @param c
	 * @param compartmentId
	 * @param instanceId
	 * @throws Exception
	 */
	public void killInstanceById(Compute c, String compartmentId, String instanceId) throws Exception{
		String name = this.getInstanceNameById(c, instanceId);
		c.terminateInstance(TerminateInstanceRequest.builder().instanceId(instanceId).build());
		h.waitForInstanceStatus(c, instanceId, Instance.LifecycleState.Terminating, "Terminating VM Instance "+name, true);
	}
	
	/**
	 * Detach volume from instance.
	 * @param c
	 * @param volumeAttachmentId
	 */
	public void detachVolume(Compute c, String volumeAttachmentId){
		c.detachVolume(DetachVolumeRequest.builder().volumeAttachmentId(volumeAttachmentId).build());
		VolumeAttachment check = null;
		while(true){
			Helper.wait(1000);
			check = c.getVolumeAttachment(GetVolumeAttachmentRequest.builder().volumeAttachmentId(volumeAttachmentId).build()).getVolumeAttachment();
			sk.printResult(0,true,check.getLifecycleState().getValue());
			if(check.getLifecycleState().equals(VolumeAttachment.LifecycleState.Detached)){
				break;
			}
		}
	}
	
	/**
	 * Attach iSCSI volume to instance.
	 * @param c
	 * @param attachName
	 * @param instanceId
	 * @param volumeId
	 * @return
	 */
	public IScsiVolumeAttachment attachVolumeIscsi(Compute c, String attachName, String instanceId, String volumeId){
		IScsiVolumeAttachment va = (IScsiVolumeAttachment) c.attachVolume(AttachVolumeRequest.builder().attachVolumeDetails(AttachIScsiVolumeDetails.builder()
				.displayName(attachName)
				.instanceId(instanceId)
				.volumeId(volumeId).build()
				).build()).getVolumeAttachment();
		return va;
	}
	
	/**
	 * Launch a new instance.
	 * @param c
	 * @param compartmentId
	 * @param subnetId
	 * @param name
	 * @param imageId
	 * @param shapeId
	 * @param sshPublicKey
	 * @param ad
	 * @param userdataBase64
	 * @param targetState
	 * @return
	 * @throws Exception
	 */
	public GetInstanceResponse createInstance(Compute c, String compartmentId, String subnetId, String name, String imageId, String shapeId, String sshPublicKey, String ad, String userdataBase64, Instance.LifecycleState targetState) throws Exception{
		Map<String, String> metadata = new HashMap<String, String>();
        metadata.put("ssh_authorized_keys", sshPublicKey);
        if(userdataBase64!=null){
        	metadata.put("user_data", userdataBase64);
        }
        
        LaunchInstanceResponse response =
                c.launchInstance(
                        LaunchInstanceRequest.builder()
                                .launchInstanceDetails(
                                        LaunchInstanceDetails.builder()
                                                .availabilityDomain(ad)
                                                .compartmentId(compartmentId)
                                                .displayName(name)
                                                .imageId(imageId)
                                                .metadata(metadata)
                                                .shape(shapeId)
                                                .subnetId(subnetId)
                                                .build())
                                .build());
        String instanceId = response.getInstance().getId();
        GetInstanceResponse res = h.waitForInstanceStatus(c, instanceId, targetState, targetState.name()+" Instance - "+name, false);
        return res;
	}
	
	/**
	 * Launch a new instance with VNIC details.
	 * @param assignPublicIp
	 * @param hostnameLabel
	 * @param privateIp
	 * @param c
	 * @param compartmentId
	 * @param subnetId
	 * @param name
	 * @param imageId
	 * @param shapeId
	 * @param sshPublicKey
	 * @param ad
	 * @param userdataBase64
	 * @param targetState
	 * @return
	 * @throws Exception
	 */
	public GetInstanceResponse createInstanceWithVnicDetails(
			boolean assignPublicIp, String hostnameLabel, String privateIp,
			Compute c, String compartmentId, String subnetId, String name, String imageId, String shapeId, String sshPublicKey, String ad, String userdataBase64, Instance.LifecycleState targetState) throws Exception{
		
		Map<String, String> metadata = new HashMap<String, String>();
        metadata.put("ssh_authorized_keys", sshPublicKey);
        if(userdataBase64!=null){
        	metadata.put("user_data", userdataBase64);
        }
        
        LaunchInstanceResponse response =
                c.launchInstance(
                        LaunchInstanceRequest.builder()
                                .launchInstanceDetails(
                                        LaunchInstanceDetails.builder()
                                                .availabilityDomain(ad)
                                                .compartmentId(compartmentId)
                                                .imageId(imageId)
                                                .metadata(metadata)
                                                .shape(shapeId)
                                                .createVnicDetails(CreateVnicDetails.builder().assignPublicIp(assignPublicIp).hostnameLabel(hostnameLabel).privateIp(privateIp).subnetId(subnetId).displayName(name).build())
                                                .build())
                                .build());
        String instanceId = response.getInstance().getId();
        GetInstanceResponse res = h.waitForInstanceStatus(c, instanceId, targetState, targetState.name()+" Instance - "+name, false);
        return res;
	}
	
	/**
	 * Add secondary VNIC to an existing instance.
	 * @param c
	 * @param name
	 * @param instanceId
	 * @param subnetId
	 * @param assignPublicIp
	 * @param displayName
	 * @param hostnameLabel
	 * @param privateIp
	 * @return
	 * @throws Exception
	 */
	public GetVnicAttachmentResponse attachSecondaryVnic(Compute c, String name, String instanceId, String subnetId, boolean assignPublicIp, String displayName, String hostnameLabel, String privateIp) throws Exception{
		AttachVnicResponse response = null;
		if(privateIp!=null){
		response = c.attachVnic(AttachVnicRequest.builder()
				.attachVnicDetails(AttachVnicDetails.builder().instanceId(instanceId).displayName(name)
						.createVnicDetails(CreateVnicDetails.builder().assignPublicIp(assignPublicIp).hostnameLabel(hostnameLabel).privateIp(privateIp).subnetId(subnetId).displayName(displayName).build())
						.build())
				.build());
		}
		else{
			response = c.attachVnic(AttachVnicRequest.builder()
					.attachVnicDetails(AttachVnicDetails.builder().instanceId(instanceId).displayName(name)
							.createVnicDetails(CreateVnicDetails.builder().assignPublicIp(assignPublicIp).hostnameLabel(hostnameLabel).subnetId(subnetId).displayName(displayName).build())
							.build())
					.build());
		}
		GetVnicAttachmentResponse res = h.waitForVnicAttachmentStatus(c, response.getVnicAttachment().getId(), VnicAttachment.LifecycleState.Attached, " Attaching Vnic to Instance - "+name, false);
		return res;
	}
	
	public Instance getInstanceById(Compute c, String instanceId){
		return c.getInstance(GetInstanceRequest.builder().instanceId(instanceId).build()).getInstance();
	}
	
	
	
	public void detachSecondaryVnic(Compute c, VirtualNetwork vn, String name, String instanceId) throws Exception{
		Instance i = this.getInstanceById(c, instanceId);
		List<Vnic> vnics = this.getVnicByInstanceId(c, vn, instanceId, i.getCompartmentId());
		String vnicId = null;
		for(Vnic v:vnics){
			if(v.getDisplayName().equals(name)){
				vnicId = v.getId();
				break;
			}
		}
		System.out.println("RemoveMe: "+vnicId);
		List<VnicAttachment> vas = c.listVnicAttachments(ListVnicAttachmentsRequest.builder().compartmentId(i.getCompartmentId()).instanceId(instanceId).vnicId(vnicId).build()).getItems();
		String vnicAttachmentId = null;
		for(VnicAttachment va:vas){
			if(va.getVnicId().equals(vnicId)){
				vnicAttachmentId = va.getId();
				break;
			}
		}
		c.detachVnic(DetachVnicRequest.builder().vnicAttachmentId(vnicAttachmentId).build());
		h.waitForVnicAttachmentStatus(c, vnicAttachmentId, VnicAttachment.LifecycleState.Detached, "Detaching "+name, true);
	}
	
	
	public void deleteImageByName(Compute c, String imageName, String compartmentId) throws Exception{
		List<Image> li = c.listImages(ListImagesRequest.builder().displayName(imageName).compartmentId(compartmentId).build()).getItems();
		for(Image i:li){
			if(i.getLifecycleState().equals(Image.LifecycleState.Available)){
				c.deleteImage(DeleteImageRequest.builder().imageId(i.getId()).build());
				h.waitForImageStatus(c, i.getId(), Image.LifecycleState.Deleted, "Deleting image "+imageName, true);
			}
		}
		
	}
	

}
