# Guanglei's Oracle Bare Metal Cloud Tool for Demo and for Fun #

**No file in this repo provides production warranty!! It's just for fun.**

+ Check [Oracle official site](https://docs.us-phoenix-1.oraclecloud.com/Content/API/SDKDocs/javasdk.htm) for Bare Metal Cloud Java SDK download, configure and install.

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
		connectiontimeout=
		readtimeout=
		public_key_file=<public_key_file_location_for_vm>


+ Edit `pom-sample.xml` to correct the `<bmc.jar.location>` property and rename it to `pom.xml` to build.

+ Main class is `bglutil.jiu.Jiu`, zero args execution shows the help message.
