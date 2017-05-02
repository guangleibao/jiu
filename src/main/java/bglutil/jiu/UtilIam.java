package bglutil.jiu;

import java.io.IOException;
import java.util.List;

import com.oracle.bmc.identity.Identity;
import com.oracle.bmc.identity.model.Compartment;
import com.oracle.bmc.identity.model.CreateCompartmentDetails;
import com.oracle.bmc.identity.model.User;
import com.oracle.bmc.identity.requests.CreateCompartmentRequest;
import com.oracle.bmc.identity.requests.GetCompartmentRequest;
import com.oracle.bmc.identity.requests.GetUserRequest;
import com.oracle.bmc.identity.requests.ListApiKeysRequest;
import com.oracle.bmc.identity.requests.ListCompartmentsRequest;
import com.oracle.bmc.identity.requests.ListUsersRequest;
import com.oracle.bmc.identity.responses.CreateCompartmentResponse;
import com.oracle.bmc.identity.responses.GetCompartmentResponse;
import com.oracle.bmc.identity.responses.GetUserResponse;
import com.oracle.bmc.identity.responses.ListUsersResponse;

import bglutil.jiu.common.UtilMain;

/**
 * IAM utilities.
 * @author guanglei
 *
 */
public class UtilIam extends UtilMain{
	
	public UtilIam(){
		super();
	}
	
	// GETTER //
	
	/**
	 * Convert IAM user OCID to user name.
	 * @param id
	 * @param ocid
	 * @return
	 */
	public String getUsernameByOcid(Identity id, String ocid){
		GetUserResponse gur = null;
		try {
			gur = id.getUser(GetUserRequest.builder().userId(ocid).build());
		}
		catch(IllegalArgumentException ex){
			ex.printStackTrace();
			return null;
		}
		return gur.getUser().getName();
	}
	
	/**
	 * Convert IAM compartment OCID to compartment name.
	 * @param id
	 * @param ocid
	 * @return
	 */
	public String getCompartmentNameByOcid(Identity id, String ocid){
		GetCompartmentResponse gcr = null;
		try {
			gcr = id.getCompartment(GetCompartmentRequest.builder().compartmentId(ocid).build());
		}
		catch(IllegalArgumentException ex){
			ex.printStackTrace();
			return null;
		}
		return gcr.getCompartment().getName();
	}
	
	/**
	 * Convert IAM compartment name to OCID.
	 * @param id
	 * @param name
	 * @param tenancyId
	 * @return
	 */
	public String getCompartmentOcidByName(Identity id, String name, String tenancyId){
		List<Compartment> compartments = id.listCompartments(ListCompartmentsRequest.builder().compartmentId(tenancyId).build()).getItems();
		String ocid = null;
		for(Compartment c:compartments){
			if(c.getName().equals(name)){
				ocid = c.getId();
			}
		}
		return ocid;
	}
	
	// CREATOR //
	
	/**
	 * Create a new compartment.
	 * @param id
	 * @param name
	 * @param description
	 * @return
	 * @throws Exception
	 */
	public Compartment createCompartment(Identity id, String name, String description) throws Exception{
		CreateCompartmentResponse ccr = id.createCompartment(
				CreateCompartmentRequest.builder()
				.createCompartmentDetails(CreateCompartmentDetails.builder()
											.compartmentId(Config.getConfigFileReader("default").get("tenancy")) // Not compartmentId, tenancy id.
											.name(name)
											.description(description)
											.build())
				.build());
		GetCompartmentResponse res = h.waitForCompartmentStatus(id, ccr.getCompartment().getId(), Compartment.LifecycleState.Active, "Creating compartment "+name, false);
		return res.getCompartment();
	}
	
	// TERMINATOR //
	
}
