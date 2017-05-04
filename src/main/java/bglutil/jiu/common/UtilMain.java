package bglutil.jiu.common;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Map;

import javax.annotation.Priority;
import javax.ws.rs.Priorities;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.ClientRequestContext;
import javax.ws.rs.client.ClientRequestFilter;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;

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
	public static String[] restGet(String apiVersion, String path, String resource, String profile) throws IOException{
		String[] ret = new String[2];
		RequestSigner requestSigner = DefaultRequestSigner.createRequestSigner(Config.getAuthProvider(profile));
		javax.ws.rs.client.Client client = ClientBuilder.newBuilder().build().register(new SigningFilter(requestSigner));
		WebTarget target = null;
		if(!resource.equals("null")){
			target = client.target("https://iaas.us-phoenix-1.oraclecloud.com")
            .path(apiVersion)
            .path(path)
            .path(UrlEscapers.urlPathSegmentEscaper().escape(resource));
		}
		else{
			target = client.target("https://iaas.us-phoenix-1.oraclecloud.com")
            .path(apiVersion)
            .path(path);
		}
                        
		Invocation.Builder ib = target.request();
        ib.accept(MediaType.APPLICATION_JSON);
        Response response = ib.get();
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
	
}
