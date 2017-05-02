# Guanglei's Oracle Bare Metal Cloud Tool for Demo and for Fun #

**No file in this repo provides production warranty!! It's just for FUN.**

Configure **`~/.oraclebmc/config`** file as following:

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

+ Check Oracle official site for Bare Metal Cloud SDK download and install.
+ Modify `pom-sample.xml` and rename it to `pom.xml` to build and run.
