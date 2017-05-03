package bglutil.jiu;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

import com.oracle.bmc.objectstorage.ObjectStorage;
import com.oracle.bmc.objectstorage.model.BucketSummary;
import com.oracle.bmc.objectstorage.model.ObjectSummary;
import com.oracle.bmc.objectstorage.requests.GetNamespaceRequest;
import com.oracle.bmc.objectstorage.requests.GetObjectRequest;
import com.oracle.bmc.objectstorage.requests.ListBucketsRequest;
import com.oracle.bmc.objectstorage.responses.GetNamespaceResponse;
import com.oracle.bmc.objectstorage.responses.GetObjectResponse;
import com.oracle.bmc.objectstorage.responses.ListBucketsResponse;
import com.oracle.bmc.objectstorage.responses.ListObjectsResponse;
import com.oracle.bmc.objectstorage.transfer.UploadConfiguration;
import com.oracle.bmc.objectstorage.transfer.UploadManager;
import com.oracle.bmc.objectstorage.transfer.UploadManager.UploadRequest;
import com.oracle.bmc.objectstorage.transfer.UploadManager.UploadResponse;
import com.oracle.bmc.objectstorage.requests.ListBucketsRequest.Builder;
import com.oracle.bmc.objectstorage.requests.ListObjectsRequest;
import com.oracle.bmc.objectstorage.requests.PutObjectRequest;

import bglutil.jiu.common.UtilMain;

/**
 * Object storage utilities.
 * @author guanglei
 *
 */
public class UtilObjectStorage extends UtilMain{
	
	public UtilObjectStorage(){
		super();
	}
	
	// GETTER //
	
	/**
	 * Print out all buckets.
	 * @param os
	 * @param profile
	 * @throws Exception
	 */
	public void printAllBuckets(ObjectStorage os, String profile) throws Exception{
		GetNamespaceResponse namespaceResponse =
                os.getNamespace(GetNamespaceRequest.builder().build());
        String namespaceName = namespaceResponse.getValue();

        Builder listBucketsBuilder =
                ListBucketsRequest.builder()
                        .namespaceName(namespaceName)
                        .compartmentId(Config.getConfigFileReader(profile).get("compartment"));

        String nextToken = null;
        do {
            listBucketsBuilder.page(nextToken);
            ListBucketsResponse listBucketsResponse =
                    os.listBuckets(listBucketsBuilder.build());
            for (BucketSummary bucket : listBucketsResponse.getItems()) {
                sk.printResult(0, true, bucket.getName());
            }
            nextToken = listBucketsResponse.getOpcNextPage();
        } while (nextToken != null);

        os.close();
	}
	
	/**
	 * Print out all obejcts under a bucket.
	 * @param os
	 * @param bucketName
	 * @param profile
	 * @throws Exception
	 */
	public void printAllObjectsInBucket(ObjectStorage os, String bucketName, String profile) throws Exception{
		com.oracle.bmc.objectstorage.requests.ListObjectsRequest.Builder listObjectsBuilder = ListObjectsRequest.builder().namespaceName(Config.getConfigFileReader(profile).get("namespace")).bucketName(bucketName);
		String nextToken = null;
        do {
            listObjectsBuilder.start(nextToken);
            ListObjectsResponse listObjectsResponse =
                    os.listObjects(listObjectsBuilder.build());
            
            for (ObjectSummary object : listObjectsResponse.getListObjects().getObjects()) {
                sk.printResult(0, true, object.getName());
            }
            nextToken = listObjectsResponse.getListObjects().getNextStartWith();
        } while (nextToken != null);

        os.close();
	}
	
	
	// UPLOADER //
	
	/**
	 * Upload a file using upload manager.
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
	public UploadResponse upload(ObjectStorage os, String bucketName, String objectName, File file, String contentType, String contentEncoding, String contentLanguage, Map<String,String> metadata){
		UploadConfiguration uploadConfiguration =
                UploadConfiguration.builder()
                        .allowMultipartUploads(true)
                        .allowParallelUploads(true)
                        .build();
		UploadManager uploadManager = new UploadManager(os, uploadConfiguration);
        PutObjectRequest request =
                PutObjectRequest.builder()
                        .bucketName(bucketName)
                        .namespaceName(os.getNamespace(GetNamespaceRequest.builder().build()).getValue())
                        .objectName(objectName)
                        .contentType(contentType)
                        .contentLanguage(contentLanguage)
                        .contentEncoding(contentEncoding)
                        .opcMeta(metadata)
                        .build();
        UploadRequest uploadDetails =
                UploadRequest.builder(file).allowOverwrite(true).build(request);
        UploadResponse response = uploadManager.upload(uploadDetails);
        return response;

	}

	// DOWNLOADER //
	
	/**
	 * Download and save file.
	 * @param os
	 * @param bucketName
	 * @param objectName
	 * @param file
	 * @throws IOException
	 */
	public void download(ObjectStorage os, String bucketName, String objectName, File file) throws IOException{
		GetObjectResponse getResponse =
                os.getObject(
                        GetObjectRequest.builder()
                                .namespaceName(os.getNamespace(GetNamespaceRequest.builder().build()).getValue())
                                .bucketName(bucketName)
                                .objectName(objectName)
                                .build());
		InputStream is = getResponse.getInputStream();
		byte[] buffer = new byte[128];
		try {
			FileOutputStream fw = new FileOutputStream(file);
			try {
				int len = is.read(buffer);
				while (len != -1) {
					fw.write(buffer, 0, len);   // to specify offset, otherwise the file downloaded will be corrupted.
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
