package bglutil.jiu;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;

import com.oracle.bmc.audit.Audit;
import com.oracle.bmc.audit.model.AuditEvent;
import com.oracle.bmc.audit.requests.ListEventsRequest;
import com.oracle.bmc.audit.responses.ListEventsResponse;
import com.oracle.bmc.identity.Identity;

import bglutil.jiu.common.UtilMain;

/**
 * Audit utilities.
 * @author guanglei
 *
 */
public class UtilAudit extends UtilMain {

	public UtilAudit() {
		super();
	}
	
	// GETTER //
	
	/**
	 * Audit log printing to console.
	 * @param auditService
	 * @param compartmentId
	 * @param startDate
	 * @param endDate
	 * @param format
	 * @param except !EventSource
	 * @param idService
	 * @throws Exception
	 */
	public void printAuditEventByDateRange(Audit auditService, String compartmentId, Date startDate, Date endDate,
			String format, String except, Identity idService) throws Exception {
		UtilIam ui = new UtilIam();
		String nextPageToken = null;
		sk.printTitle(0, "Audit events between " + startDate.toString() + " and " + endDate.toString());
		if (format.equals("raw")) {
			do {
				ListEventsResponse response = auditService.listEvents(ListEventsRequest.builder().compartmentId(compartmentId)
						.startTime(startDate).endTime(endDate).page(nextPageToken).build());

				for (AuditEvent event : response.getItems()) {
					if (except==null || !event.getRequestResource().toLowerCase().contains(except)) {
						sk.printResult(0, true, event);
					}
				}
				nextPageToken = response.getOpcNextPage();
			} while (nextPageToken != null);
		} else if (format.equals("simple")) {
			do {
				ListEventsResponse response = auditService.listEvents(ListEventsRequest.builder().compartmentId(compartmentId)
						.startTime(startDate).endTime(endDate).page(nextPageToken).build());
				for (AuditEvent event : response.getItems()) {
					if (except==null || !event.getRequestResource().toLowerCase().contains(except)) {
						String ocid = event.getPrincipalId(); // ocid length zero means NULL.
						sk.printResult(0, true, "(WHO): " + (ocid.length()==0?"None":ui.getUsernameByOcid(idService, ocid)+", "+ocid));
						if(event.getResponseHeaders()!=null && event.getResponseHeaders().get("Date")!=null){
							sk.printResult(1, true, "(WHEN): Event: " + event.getEventTime()+" | Response: "+event.getResponseHeaders().get("Date").get(0));
						}
						sk.printResult(1, true,
								"(WHAT): " + event.getRequestAction() + " " + event.getRequestResource());
						Map<String, List<String>> parameters = event.getRequestParameters();
						for (String key : parameters.keySet()) {
							sk.printResult(2, true, key + "=" + Arrays.toString(parameters.get(key).toArray()) + ";");
						}
					}
				}
				nextPageToken = response.getOpcNextPage();
			} while (nextPageToken != null);
		}
		auditService.close();
	}
}
