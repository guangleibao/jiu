# Oracle Bare Metal Cloud Tool for Demo Purpose #

**No file in this repo provides production warranty!! It's just for fun and for demo.**

+ First time user please check [Oracle official site](https://docs.us-phoenix-1.oraclecloud.com/Content/API/SDKDocs/javasdk.htm) for Bare Metal Cloud Java SDK download, configure and install.
+ Current SDK [download page](https://github.com/oracle/bmcs-java-sdk/releases).

Sample config file **`~/.oraclebmc/config`** to run `Jiu`:

		[DEFAULT]
		tenancy=<tenancy_ocid>
		user=<iam_user_ocid>
		fingerprint=<signing_key_fingerprint>
		key_file=<signing_key_location>
		region=<bmc_region_code>
		compartment=<compartment_ocid>
		namespace=<object_storage_namespace>
		bastionseclist=<bastion_seclist_ocid>
		connectiontimeout=6000
		readtimeout=240000
		public_key_file=<public_key_file_location_for_vm>
		bastion_user_data=<file_location>
		webserver_user_data=<file_location>
		webserver0_user_data=<file_location>
		webserver1_user_data=<file_location>
		webserver2_user_data=<file_location>

		[PHOENIX]
		...

		[ASHBURN]
		...

+ [Download Maven](https://maven.apache.org/download.cgi).

+ [Install Maven](https://maven.apache.org/install.html).  

+ Edit `pom-sample.xml` to correct the `<bmc.jar.location>` property and rename it to `pom.xml`.

+ `mvn pacakge`

+ Replace `PATH_TO` portions in sample run script `runJiu.sh`.

+ Run `runJiu.sh` to show all utilities.

+ Run `runJiu.sh [utility_name] -h` to show help messasge.

