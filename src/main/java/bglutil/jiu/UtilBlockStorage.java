package bglutil.jiu;

import java.util.List;

import com.oracle.bmc.core.Blockstorage;
import com.oracle.bmc.core.model.CreateVolumeDetails;
import com.oracle.bmc.core.model.Volume;
import com.oracle.bmc.core.requests.CreateVolumeRequest;
import com.oracle.bmc.core.requests.DeleteVolumeRequest;
import com.oracle.bmc.core.requests.GetVolumeRequest;
import com.oracle.bmc.core.requests.ListVolumesRequest;

import bglutil.jiu.common.UtilMain;

public class UtilBlockStorage extends UtilMain{
	
	public UtilBlockStorage() {
		super();
	}
	
	public List<Volume> getAllVolume(Blockstorage bs, String availabilityDomain, String compartmentId){
		return bs.listVolumes(ListVolumesRequest.builder().availabilityDomain(availabilityDomain).compartmentId(compartmentId).build()).getItems();
	}
	
	public Volume createVolume(Blockstorage bs, String name, String availabilityDomain, long sizeGB, String compartmentId){
		return bs.createVolume(CreateVolumeRequest.builder().createVolumeDetails(CreateVolumeDetails.builder()
				.availabilityDomain(availabilityDomain)
				.compartmentId(compartmentId)
				.displayName(name)
				.sizeInMBs(sizeGB*1024).build()
				).build()).getVolume();
	}
	
	public void killVolumeById(Blockstorage bs, String volId){
		bs.deleteVolume(DeleteVolumeRequest.builder().volumeId(volId).build());
	}
	
	public Volume getVolumeByName(Blockstorage bs, String name, String availabilityDomain, String compartmentId){
		List<Volume> vols = this.getAllVolume(bs, availabilityDomain, compartmentId);
		Volume vol = null;
		for(Volume v:vols){
			if(v.getDisplayName().equals(name)){
				vol = v;
				break;
			}
		}
		return vol;
	}
	
	public Volume getVolumeById(Blockstorage bs, String volumeId){
		return bs.getVolume(GetVolumeRequest.builder().volumeId(volumeId).build()).getVolume();
	}
	
}
