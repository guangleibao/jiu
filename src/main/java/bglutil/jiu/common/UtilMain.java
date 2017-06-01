package bglutil.jiu.common;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.util.GregorianCalendar;
import java.util.Map;

import javax.annotation.Priority;
import javax.ws.rs.Priorities;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.ClientRequestContext;
import javax.ws.rs.client.ClientRequestFilter;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;

import org.glassfish.jersey.jackson.JacksonFeature;

import com.google.common.net.UrlEscapers;
import com.oracle.bmc.http.signing.DefaultRequestSigner;
import com.oracle.bmc.http.signing.RequestSigner;

import bglutil.jiu.Config;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

/**
 * Parent class for all UtilXxx classes.
 * @author guanglei
 *
 */
public class UtilMain {
	
	protected Speaker sk;
	protected Helper h;
	
	public UtilMain(){
		this.sk = new Speaker(Speaker.RenderType.CONSOLE);
		this.h = new Helper();
	}
	
	@Priority(Priorities.AUTHENTICATION)
    @RequiredArgsConstructor
    private static class SigningFilter implements ClientRequestFilter {
		
		private SigningFilter(RequestSigner rs){
			this.requestSigner = rs;
		}
		
		private RequestSigner getRequestSigner(){
			return this.requestSigner;
		}
		
		private void setRequestSigner(RequestSigner rs){
			this.requestSigner = rs;
		}
		
        private RequestSigner requestSigner;

        @Override
        public void filter(@NonNull ClientRequestContext clientRequestContext) throws IOException {
            Map<String, String> authHeaders =
                    requestSigner.signRequest(
                            clientRequestContext.getUri(),
                            clientRequestContext.getMethod(),
                            clientRequestContext.getStringHeaders(),
                            clientRequestContext.getEntity());

            for (String key : authHeaders.keySet()) {
                clientRequestContext.getHeaders().putSingle(key, authHeaders.get(key));
            }
        }
    }
	
	// TODO parameters.
	public static String[] restCall(String method, String apiVersion, String[] slashPath, String profile) throws IOException{
		String[] ret = new String[2];
		RequestSigner requestSigner = DefaultRequestSigner.createRequestSigner(Config.getAuthProvider(profile));
		javax.ws.rs.client.Client client = ClientBuilder.newBuilder().build().register(new SigningFilter(requestSigner));
		WebTarget target = client.target("https://iaas.us-phoenix-1.oraclecloud.com")
	            .path(apiVersion);
		
		for(String sp:slashPath){
			target = target.path(UrlEscapers.urlPathSegmentEscaper().escape(sp));
		}
		      
		Invocation.Builder ib = target.request();
        ib.accept(MediaType.APPLICATION_JSON);
        Response response = null;
        if(method.equals("get")){
        	response = ib.get();
        }
        else{
        	response = ib.get();
        }
        MultivaluedMap<String, Object> responseHeaders = response.getHeaders();
        ret[0]=responseHeaders.toString();
        InputStream responseBody = (InputStream) response.getEntity();
        try (final BufferedReader reader =
                new BufferedReader(new InputStreamReader(responseBody))) {
            StringBuilder jsonBody = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                jsonBody.append(line);
            }
            ret[1] = jsonBody.toString();
            return ret;
        }
	}
	
	public static String[] restCallv2(String endponit, String method, String apiVersion, String[] slashPath, Map<String,String> paramKV, String profile) throws IOException{
		String[] ret = new String[2];
		String endpoint = "https://iaas.us-phoenix-1.oraclecloud.com";
		// JAX-RS 2.0 Client with Request signer
		RequestSigner requestSigner = DefaultRequestSigner.createRequestSigner(Config.getAuthProvider(profile));
		javax.ws.rs.client.Client client = ClientBuilder.newBuilder().build().register(new SigningFilter(requestSigner));
		
		WebTarget target = client.target(endpoint)
	            .path(apiVersion);
		
		for(String sp:slashPath){
			target = target.path(UrlEscapers.urlPathSegmentEscaper().escape(sp));
		}
		      
		Invocation.Builder ib = target.request();
        ib.accept(MediaType.APPLICATION_JSON);
  
        Response response = null;
        if(method.equals("get")){
        	response = ib.get();
        }
        else{
        	response = ib.get();
        }
 
        
        MultivaluedMap<String, Object> responseHeaders = response.getHeaders();
        ret[0]=responseHeaders.toString();
        InputStream responseBody = (InputStream) response.getEntity();
        try (final BufferedReader reader =
                new BufferedReader(new InputStreamReader(responseBody))) {
            StringBuilder jsonBody = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                jsonBody.append(line);
            }
            ret[1] = jsonBody.toString();
            return ret;
        }
	}
	
	public static String[] restCalltest(){
		String REST_SERVICE_URL = "http://localhost:8080/tutorial/rs/book";
		 
		String TITLE = "One big book";
		BigDecimal PRICE = new BigDecimal("20.0");
		GregorianCalendar PUBLISHED = new GregorianCalendar(2013, 12, 24);
		 
		Client client = ClientBuilder.newClient().register(JacksonFeature.class);
		
		
		
		return new String[]{"A","B"};
	}
}
