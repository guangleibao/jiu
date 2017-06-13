package bglutil.jiu;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Date;
import java.util.Map;

import com.oracle.bmc.objectstorage.ObjectStorage;
import com.oracle.bmc.objectstorage.model.Bucket;
import com.oracle.bmc.objectstorage.model.BucketSummary;
import com.oracle.bmc.objectstorage.model.CreateBucketDetails;
import com.oracle.bmc.objectstorage.model.ObjectSummary;
import com.oracle.bmc.objectstorage.model.PreauthenticatedRequest;
import com.oracle.bmc.objectstorage.model.PreauthenticatedRequest.AccessType;
import com.oracle.bmc.objectstorage.model.PreauthenticatedRequestSummary;
import com.oracle.bmc.objectstorage.model.UpdateBucketDetails;
import com.oracle.bmc.objectstorage.model.CreateBucketDetails.PublicAccessType;
import com.oracle.bmc.objectstorage.model.CreatePreauthenticatedRequestDetails;
import com.oracle.bmc.objectstorage.requests.CreateBucketRequest;
import com.oracle.bmc.objectstorage.requests.CreatePreauthenticatedRequestRequest;
import com.oracle.bmc.objectstorage.requests.DeleteBucketRequest;
import com.oracle.bmc.objectstorage.requests.DeleteObjectRequest;
import com.oracle.bmc.objectstorage.requests.DeletePreauthenticatedRequestRequest;
import com.oracle.bmc.objectstorage.requests.GetNamespaceRequest;
import com.oracle.bmc.objectstorage.requests.GetObjectRequest;
import com.oracle.bmc.objectstorage.requests.ListBucketsRequest;
import com.oracle.bmc.objectstorage.responses.GetObjectResponse;
import com.oracle.bmc.objectstorage.responses.ListBucketsResponse;
import com.oracle.bmc.objectstorage.responses.ListObjectsResponse;
import com.oracle.bmc.objectstorage.responses.ListPreauthenticatedRequestsResponse;
import com.oracle.bmc.objectstorage.transfer.UploadConfiguration;
import com.oracle.bmc.objectstorage.transfer.UploadManager;
import com.oracle.bmc.objectstorage.transfer.UploadManager.UploadRequest;
import com.oracle.bmc.objectstorage.transfer.UploadManager.UploadResponse;
import com.oracle.bmc.objectstorage.requests.ListBucketsRequest.Builder;
import com.oracle.bmc.objectstorage.requests.ListObjectsRequest;
import com.oracle.bmc.objectstorage.requests.ListPreauthenticatedRequestsRequest;
import com.oracle.bmc.objectstorage.requests.PutObjectRequest;
import com.oracle.bmc.objectstorage.requests.UpdateBucketRequest;

import bglutil.jiu.common.UtilMain;

/**
 * Object storage utilities.
 * 
 * @author guanglei
 *
 */
public class UtilObjectStorage extends UtilMain {

	public UtilObjectStorage() {
		super();
	}

	/**
	 * Delete bucket..
	 * 
	 * @param os
	 * @param bucketName
	 */
	public void deleteBucket(ObjectStorage os, String bucketName) {
		os.deleteBucket(
				DeleteBucketRequest.builder().bucketName(bucketName).namespaceName(this.getNamespace(os)).build());
	}
	
	public void deletePar(ObjectStorage os, String bucketName, String parId){
		String namespaceName = this.getNamespace(os);
		os.deletePreauthenticatedRequest(DeletePreauthenticatedRequestRequest.builder().bucketName(bucketName).namespaceName(namespaceName)
				.parId(parId).build());
	}

	/**
	 * Delete object in bucket.
	 * 
	 * @param os
	 * @param bucketName
	 * @param objectName
	 */
	public void deleteObject(ObjectStorage os, String bucketName, String objectName) {
		os.deleteObject(DeleteObjectRequest.builder().bucketName(bucketName).namespaceName(this.getNamespace(os))
				.objectName(objectName).build());
	}

	/**
	 * Get the URL
	 * 
	 * @param os
	 * @param bucketName
	 * @param objectName
	 * @param profile
	 * @return
	 * @throws IOException
	 */
	public String getUrl(ObjectStorage os, String bucketName, String objectName, String profile) throws IOException {
		return "https://objectstorage." + Config.getConfigFileReader(profile).get("region") + ".oraclecloud.com/n/"
				+ this.getNamespace(os) + "/b/" + bucketName + "/o/" + objectName;
	}

	// PAR //
	/**
	 * Generate PAR.
	 * 
	 * @param os
	 * @param bucketName
	 * @param objectName
	 * @param parName
	 * @param at
	 * @param hours
	 * @return
	 */
	// closed issue submitted to bmcs-java-sdk github.
	// https://github.com/oracle/bmcs-java-sdk/issues?q=is%3Aissue+is%3Aclosed
	public PreauthenticatedRequest generatePreAuthenticatedReqeust(ObjectStorage os, String bucketName,
			String objectName, String parName, CreatePreauthenticatedRequestDetails.AccessType at, long hours) {
		String ns = this.getNamespace(os);
		Date expiration = new Date();
		long milliSeconds = expiration.getTime();
		milliSeconds += hours * 1000 * 60 * 60;
		expiration.setTime(milliSeconds);
		PreauthenticatedRequest par = os
				.createPreauthenticatedRequest(CreatePreauthenticatedRequestRequest.builder().bucketName(bucketName)
						.namespaceName(ns)
						.createPreauthenticatedRequestDetails(CreatePreauthenticatedRequestDetails.builder()
								.accessType(at).name(parName).objectName(objectName).timeExpires(expiration).build())
						.build())
				.getPreauthenticatedRequest();
		return par;
	}

	// CHANGE //

	public Bucket changeBucketPublicAccess(ObjectStorage os, String bucketName,
			UpdateBucketDetails.PublicAccessType pat, String compartmentId) {
		String ns = this.getNamespace(os);
		Bucket b = os.updateBucket(UpdateBucketRequest.builder().bucketName(bucketName).namespaceName(ns)
				.updateBucketDetails(
						UpdateBucketDetails.builder().name(bucketName).namespace(ns).publicAccessType(pat).build())
				.build()).getBucket();
		return b;
	}

	// CREATOR //
	public Bucket createBucket(ObjectStorage os, String bucketName, String compartmentId) {
		String ns = this.getNamespace(os);
		Bucket b = os.createBucket(CreateBucketRequest.builder()
				.namespaceName(ns).createBucketDetails(CreateBucketDetails.builder().compartmentId(compartmentId)
						.name(bucketName).publicAccessType(PublicAccessType.NoPublicAccess).build())
				.build()).getBucket();
		return b;
	}

	// GETTER //

	/**
	 * Get the namespace for object storage.
	 * 
	 * @param os
	 * @return
	 */
	public String getNamespace(ObjectStorage os) {
		return os.getNamespace(GetNamespaceRequest.builder().build()).getValue();
	}

	/**
	 * Print out all buckets.
	 * 
	 * @param os
	 * @param profile
	 * @throws Exception
	 */
	public void printAllBuckets(ObjectStorage os, String profile) throws Exception {
		String namespaceName = this.getNamespace(os);

		Builder listBucketsBuilder = ListBucketsRequest.builder().namespaceName(namespaceName)
				.compartmentId(Config.getConfigFileReader(profile).get("compartment"));

		String nextToken = null;
		do {
			listBucketsBuilder.page(nextToken);
			ListBucketsResponse listBucketsResponse = os.listBuckets(listBucketsBuilder.build());
			for (BucketSummary bucket : listBucketsResponse.getItems()) {
				sk.printResult(0, true, bucket.getName());
			}
			nextToken = listBucketsResponse.getOpcNextPage();
		} while (nextToken != null);

		os.close();
	}

	/**
	 * Print out all obejcts under a bucket.
	 * 
	 * @param os
	 * @param bucketName
	 * @param profile
	 * @throws Exception
	 */
	public void printAllObjectsInBucket(ObjectStorage os, String bucketName, String profile) throws Exception {
		com.oracle.bmc.objectstorage.requests.ListObjectsRequest.Builder listObjectsBuilder = ListObjectsRequest
				.builder().namespaceName(this.getNamespace(os)).bucketName(bucketName);
		String nextToken = null;
		do {
			listObjectsBuilder.start(nextToken);
			ListObjectsResponse listObjectsResponse = os.listObjects(listObjectsBuilder.build());

			for (ObjectSummary object : listObjectsResponse.getListObjects().getObjects()) {
				sk.printResult(0, true,
						object.getName() + ", " + this.getUrl(os, bucketName, object.getName(), profile));
			}
			nextToken = listObjectsResponse.getListObjects().getNextStartWith();
		} while (nextToken != null);
		os.close();
	}

	public void printAllPARsInBucket(ObjectStorage os, String bucketName, String profile) throws Exception {
		com.oracle.bmc.objectstorage.requests.ListPreauthenticatedRequestsRequest.Builder listPARsBuilder = ListPreauthenticatedRequestsRequest
				.builder().namespaceName(this.getNamespace(os)).bucketName(bucketName);
		ListPreauthenticatedRequestsResponse listPreauthenticatedRequestsResponse = os
				.listPreauthenticatedRequests(listPARsBuilder.build());
		for (PreauthenticatedRequestSummary summary : listPreauthenticatedRequestsResponse.getItems()) {
			sk.printResult(0, true, summary.getName() + ", " + summary.getAccessType().getValue() + ", "
					+ summary.getObjectName()+", "+summary.getTimeExpires());
			sk.printResult(1, true, summary.getId());
		}
		os.close();
	}

	// UPLOADER //

	/**
	 * Upload a file using upload manager.
	 * 
	 * @param os
	 * @param bucketName
	 * @param objectName
	 * @param file
	 * @param contentType
	 * @param contentEncoding
	 * @param contentLanguage
	 * @param metadata
	 * @return
	 */
	public UploadResponse upload(ObjectStorage os, String bucketName, String objectName, File file, String contentType,
			String contentEncoding, String contentLanguage, Map<String, String> metadata) {
		UploadConfiguration uploadConfiguration = UploadConfiguration.builder().allowMultipartUploads(true)
				.allowParallelUploads(true).build();
		UploadManager uploadManager = new UploadManager(os, uploadConfiguration);
		PutObjectRequest request = PutObjectRequest.builder().bucketName(bucketName)
				.namespaceName(this.getNamespace(os)).objectName(objectName).contentType(contentType)
				.contentLanguage(contentLanguage).contentEncoding(contentEncoding).opcMeta(metadata).build();
		UploadRequest uploadDetails = UploadRequest.builder(file).allowOverwrite(true).build(request);
		UploadResponse response = uploadManager.upload(uploadDetails);
		return response;

	}

	// DOWNLOADER //

	/**
	 * Download and save file.
	 * 
	 * @param os
	 * @param bucketName
	 * @param objectName
	 * @param file
	 * @throws IOException
	 */
	public void download(ObjectStorage os, String bucketName, String objectName, File file) throws IOException {
		GetObjectResponse getResponse = os.getObject(GetObjectRequest.builder().namespaceName(this.getNamespace(os))
				.bucketName(bucketName).objectName(objectName).build());
		InputStream is = getResponse.getInputStream();
		byte[] buffer = new byte[128];
		try {
			FileOutputStream fw = new FileOutputStream(file);
			try {
				int len = is.read(buffer);
				while (len != -1) {
					fw.write(buffer, 0, len); // to specify offset, otherwise
												// the file downloaded will be
												// corrupted.
					len = is.read(buffer);
				}
			} catch (IOException e) {
				e.printStackTrace();
			} finally {
				fw.close();
			}
		} catch (IOException e1) {
			e1.printStackTrace();
		} finally {
			is.close();
		}
	}
}
