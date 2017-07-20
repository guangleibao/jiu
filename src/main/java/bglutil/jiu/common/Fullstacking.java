package bglutil.jiu.common;

import java.util.Hashtable;
import java.util.TreeMap;

public class Fullstacking extends UtilMain{
	
	private TreeMap<String,OracleCloudProduct> nameTable = new TreeMap<String,OracleCloudProduct>();
	private TreeMap<String,OracleCloudProduct> catIndex = new TreeMap<String,OracleCloudProduct>();
	private TreeMap<String,OracleCloudProduct> aliasIndex = new TreeMap<String,OracleCloudProduct>();
	private Hashtable<String,OracleCloudProduct> descIndex = new Hashtable<String,OracleCloudProduct>();
	
	public Fullstacking(){
		this.makeTable();
	}
	
	private void buildIndex(){
		
	}
	
	private void makeTable(){
		//////////////////////////////// IAAS
		// IaaS-OPC-Compute
		this.addProduct("IaaS-OPC-Compute", 
				"General Purpose Compute",
				"N/A",
				"A fully scalable multi-tenant virtual compute environment to run applications with predictable, consistent performance and built-in resiliency."
				+"Rapidly provision virtual machines on Oracle Cloud with all the necessary storage and networking resources, manage and scale your virtual machine topology in the cloud easily, and migrate your Oracle and third-party applications to Oracle Cloud.",
				"https://cloud.oracle.com/en_US/compute/documentation");
		this.addProduct("IaaS-OPC-Compute", 
				"Dedicated Compute", 
				"N/A", 
				"Dedicated environment in the Oracle Cloud, consists of high performance x86 servers reserved for your use to run critical applications with predictable, "
				+"consistent performance and network isolation.", 
				"https://cloud.oracle.com/en_US/compute/documentation");
		this.addProduct("IaaS-OPC-Compute", 
				"Dedicated Compute – SPARC Model 300", 
				"N/A", 
				"Dedicated environment in the Oracle Cloud, consists of high performance x86 servers reserved for your use to run critical applications with predictable, "
				+"consistent performance and network isolation. "
				+"Dedicated compute running on the world’s fastest processor, the SPARC M7, Near-Zero-Overhead Virtualization.", 
				"https://cloud.oracle.com/en_US/compute/documentation");
		// Iaas-OPC-Storage
		this.addProduct("IaaS-OPC-Storage", 
				"Oracle Storage Cloud Service - Object Storage", 
				"N/A", 
				"Secure, resilient and infinitely elastic object storage in the cloud. Designed for enterprise-class data protection and sharing with easy access from anywhere on the internet.", 
				"https://cloud.oracle.com/opc/iaas/ebooks/Oracle_Storage_Cloud_Service.pdf");
		this.addProduct("IaaS-OPC-Storage", 
				"Oracle Storage Cloud Service - Archive Storage", 
				"N/A", 
				"The most cost-effective storage in the industry. Designed for infrequently accessed data with enterprise-grade security, resilience, and elastic scalability.", 
				"https://cloud.oracle.com/opc/iaas/ebooks/Oracle_Storage_Cloud_Service.pdf");
		this.addProduct("IaaS-OPC-Storage", 
				"Oracle Database Backup Service", 
				"N/A", 
				"Storage for All Your Oracle Database Backup Needs. A reliable and scalable object storage solution for storing and accessing your ever-growing Oracle Database backup data.",
				"https://cloud.oracle.com/opc/paas/ebooks/Oracle_Database_Backup_Cloud_Service.pdf, https://www.oracle.com/search/results?cat=otn&Ntk=S3&Ntt=rman+module, https://docs.oracle.com/en/cloud/paas/db-backup-cloud/index.html");
		this.addProduct("IaaS-OPC-Storage", 
				"Oracle Storage Cloud Software Appliance", 
				"N/A", 
				"An easy on-ramp to Oracle's storage cloud providing a secure, POSIX compliant, local NFS interface. Serves as a NAS gateway to the cloud so users, applications, and IT management can start using cloud storage today.",
				"https://cloud.oracle.com/opc/iaas/ebooks/Oracle_Storage_Cloud_Service.pdf");
		this.addProduct("IaaS-OPC-Storage", 
				"Oracle Public Cloud Data Transfer Services", 
				"N/A", 
				"An easy on-ramp to Oracle's storage cloud providing a secure, POSIX compliant, local NFS interface. Serves as a NAS gateway to the cloud so users, applications, and IT management can start using cloud storage today.",
				"https://cloud.oracle.com/opc/iaas/ebooks/Oracle_Storage_Cloud_Service.pdf");
		// Iaas-OPC-Network
		this.addProduct("IaaS-OPC-Network", 
				"VPN for Compute", 
				"N/A", 
				"Oracle’s Corente Cloud Services Exchange (Corente CSX) is a cloud-based software- defined networking (SDN) service that enables distributed enterprises to deliver trusted connectivity services to and from any location with less complexity, in significantly less time, and at a greatly reduced cost, when compared to more traditional approaches. The Corente Services Gateway (CSG) is a distributed virtual appliance located at the network edge that provides secure endpoints for virtual private networks over any IP networks with zero-touch installation. Available as a Service (VPNaaS) as well as complete self service deployment in the users account.",
				"https://cloud.oracle.com/opc/iaas/ebooks/Oracle_Compute_Cloud_Service.pdf");
		this.addProduct("IaaS-OPC-Network", 
				"VPN for Dedicated Compute", 
				"N/A", 
				"Oracle Network Cloud Service – Site to Site VPN is a secure, reliable, and cost-effective solution for expanding your private network. Enterprises can securely connect to Oracle’s Dedicated Compute zone over IPSec tunnels as part of their virtual private network. Oracle Network Cloud Service – Site to Site VPN eliminates the need to invest in leased lines for more security and peace of mind. Available as a Service (VPNaaS) on Dedicated Compute in Regional Cloud.",
				"https://cloud.oracle.com/opc/iaas/ebooks/Oracle_Compute_Cloud_Service.pdf");
		this.addProduct("IaaS-OPC-Network", 
				"FastConnect", 
				"N/A", 
				"FastConnect addresses one of the most important issues that affect migration to a cloud service: the unpredictable nature of the Internet. With FastConnect, you can create a high-speed, dedicated, and low-latency extension that allows you to reap the benefits of a true hybrid cloud setup. It also offers better security than exchanging your data over the Internet.",
				"https://cloud.oracle.com/opc/iaas/ebooks/Oracle_Compute_Cloud_Service.pdf");
		// IaaS-OPC-Container
		this.addProduct("IaaS-OPC-Container", 
				"Container", 
				"OCCS", 
				"Oracle Container Cloud Service provides an easy and quick way to create an enterprise-grade container infrastructure. It delivers comprehensive tooling to compose, deploy, orchestrate and manage Docker container-based applications on the Oracle Cloud for Dev, Dev/Test, DevOps, and Cloud Native use cases.",
				"https://cloud.oracle.com/iaas/ebooks/Oracle_Container_Cloud_Service.pdf");
		// IaaS-OPC-Ravello
		this.addProduct("IaaS-OPC-Ravello", 
				"Ravello", 
				"N/A", 
				"Seamlessly deploy your existing VMware or KVM based data center workloads on Oracle Public Cloud, AWS, or Google Cloud as-is, without any modification to the VMs, network, or storage. Get on-demand and cost-effective scale for agile dev/test processes.",
				"https://cloud.oracle.com/iaas/datasheets/Ravello_Data_Sheet.pdf");
		// IaaS-OPC-Cloud-Machine
		this.addProduct("IaaS-OPC-Cloud-Machine", 
				"Cloud Machine", 
				"OCM", 
				"Oracle Cloud Machine delivers Oracle Cloud services in your data center, fully managed by Oracle, so that you can take advantage of the agility, innovation and subscription-based pricing of Oracle Cloud while meeting data-residency requirements.",
				"http://docs.oracle.com/en/cloud/cloud-at-customer/index.html");
		// IaaS-BMC-Compute
		this.addProduct("IaaS-BMC-Compute", 
				"Bare Metal Instances", 
				"N/A", 
				"Industry-first fully dedicated bare metal servers on a software-defined network, combining the power of bare metal servers with a secure, isolated virtualized cloud network. Bare Metal Compute Service provides unrivaled raw performance, including servers with latest generation NVMe SSDs offering multi-million IOPS, ideal to run I/O intensive web applications or big data workloads.",
				"https://docs.us-phoenix-1.oraclecloud.com/Content/services.htm");
		this.addProduct("IaaS-BMC-Compute", 
				"Virtual Machines Instances", 
				"N/A", 
				"Managed Virtual Machine (VM) instances are available in Bare Metal Cloud for workloads not requiring dedicated physical servers or high-performance of bare metal instances. VM instances provide cost-savings and are offered in different shapes, catering to a variety of workloads.",
				"https://docs.us-phoenix-1.oraclecloud.com/Content/services.htm");
		this.addProduct("IaaS-BMC-Networking", 
				"Bare Metal Cloud Virtual Cloud Network", 
				"N/A", 
				"A Virtual Cloud Network (VCN) is a customizable and private network in Oracle’s Bare Metal Cloud Services. Just like a traditional data center network, the VCN provides you with complete control over your network environment. This includes assigning your own private IP address space, creating subnets, route tables and configuring stateful firewalls. A single tenant can have multiple VCNs, thereby providing grouping and isolation of related resources.",
				"https://docs.us-phoenix-1.oraclecloud.com/Content/services.htm");
		this.addProduct("IaaS-BMC-Networking", 
				"Bare Metal Cloud FastConnect Service", 
				"N/A", 
				"The Oracle Bare Metal Cloud FastConnect Service is a network connectivity alternative to using the public internet for connecting your network with Oracle’s Bare Metal Cloud Services. FastConnect provides an easy, elastic, and economical way to create a dedicated and private connection with higher bandwidth options, and a more reliable and consistent networking experience when compared to internet-based connections.",
				"https://docs.us-phoenix-1.oraclecloud.com/Content/services.htm");
		// IaaS-BMC-Storage
		this.addProduct("IaaS-BMC-Storage", 
				"Bare Metal Cloud Block Volume Service", 
				"N/A", 
				"Block Volume Service provides high-speed storage capacity with seamless data protection and recovery. Network-attached block volumes deliver low latency and tens of thousands of IOPS per compute instance.",
				"https://docs.us-phoenix-1.oraclecloud.com/Content/services.htm");
		this.addProduct("IaaS-BMC-Storage", 
				"Bare Metal Cloud Object Storage Service", 
				"N/A", 
				"Object Storage Service offers an unlimited amount of capacity, automatically replicating and healing data across multiple fault domains for high durability and data integrity.",
				"https://docs.us-phoenix-1.oraclecloud.com/Content/services.htm");
		// IaaS-BMC-Governance
		this.addProduct("IaaS-BMC-Governance", 
				"Bare Metal Cloud Audit Service", 
				"N/A", 
				"The Audit Service provides comprehensive visibility into your Bare Metal Cloud Services. Access all public API activities in your tenancy for 90 days at no additional cost.",
				"https://docs.us-phoenix-1.oraclecloud.com/Content/services.htm");
		this.addProduct("IaaS-BMC-Governance", 
				"Bare Metal Cloud Identity and Access Management Service", 
				"IAM", 
				"The Audit Service provides comprehensive visibility into your Bare Metal Cloud Services. Access all public API activities in your tenancy for 90 days at no additional cost.",
				"https://docs.us-phoenix-1.oraclecloud.com/Content/services.htm");
		// IaaS-BMC-Database-Cloud-Service
		this.addProduct("IaaS-BMC-Database-Cloud-Service", 
				"Bare Metal Cloud Database Cloud Service", 
				"DB Systems", 
				"Exadata, RAC, Single Node databases in the cloud. Deploy your Oracle databases on-demand with dedicated hardware performance, proven RAC reliability, in-depth security, and granular controls in a highly durable and available cloud environment.",
				"https://docs.us-phoenix-1.oraclecloud.com/Content/services.htm");
		// IaaS-BMC-Load-Balancing
		this.addProduct("IaaS-BMC-Load-Balancing-Service", 
				"Bare Metal Cloud Load Balancing Service", 
				"N/A", 
				"Public and private load balancing. Increase application availability and performance with highly-available and provisioned bandwidth load balancing.",
				"https://docs.us-phoenix-1.oraclecloud.com/Content/services.htm");
		////////////////////////////////PAAS
		// PaaS-OPC-Database
		this.addProduct("PaaS-OPC-Database-Cloud-Services", 
				"Oracle Database Cloud Service", 
				"DBCS", 
				"Database Cloud Service offers elastic database services for application development, test and production deployment. The service delivers an easy to use web console user interface and RESTful API to provision and administer Oracle Database on Oracle Compute Cloud Offerings.",
				"https://cloud.oracle.com/database/ebooks");
		this.addProduct("PaaS-OPC-Database-Cloud-Services", 
				"Oracle Database Cloud Service – Bare Metal", 
				"DBCS-BM", 
				"Cloud Database Service on bare metal offers on-demand, pay-per-use database services with the performance of dedicated hardware and local NVMe storage, and the reliability of RAC, on a low latency, highly configurable, and secure Virtual Cloud Network.",
				"https://cloud.oracle.com/database/ebooks");
		this.addProduct("PaaS-OPC-Database-Cloud-Services", 
				"Oracle Database Exadata Cloud Service", 
				"N/A", 
				"The Exadata Cloud Service brings the full power of Exadata to the Oracle Cloud. The service includes all the benefits of Exadata performance and 100% compatibility with existing business-critical applications and databases that are on premise, ensuring a smooth transition to the cloud.",
				"https://cloud.oracle.com/database/ebooks");
		this.addProduct("PaaS-OPC-Database-Cloud-Services", 
				"Oracle Database Exadata Cloud Machine", 
				"N/A", 
				"Oracle Database Exadata Cloud Machine delivers the world’s most advanced database cloud to customers who require their databases to be located on-premises.",
				"https://cloud.oracle.com/database/ebooks");
		this.addProduct("PaaS-OPC-Database-Cloud-Services", 
				"Oracle Database Exadata Express Cloud Service - Managed", 
				"N/A", 
				"Exadata Express provides your own Oracle Database Enterprise Edition running the latest database release on Exadata for a full Oracle experience. It’s a fully managed service packed with features for modern application development and great for small to medium sized data.",
				"https://cloud.oracle.com/database/ebooks");
		this.addProduct("PaaS-OPC-Database-Cloud-Services", 
				"Oracle Database Schema Cloud Service - Managed", 
				"N/A", 
				"Schema Service runs Application Express (APEX), giving customers a declarative development environment to rapidly create data-rich web apps.",
				"https://cloud.oracle.com/database/ebooks");
		// PaaS-OPC-Database-Backup
		this.addProduct("PaaS-OPC-Database-Backup", 
				"Database Backup", 
				"N/A", 
				"A reliable and scalable object storage solution for storing and accessing your ever-growing Oracle Database backup data.",
				"https://cloud.oracle.com/opc/paas/ebooks/Oracle_Database_Backup_Cloud_Service.pdf");
		// PaaS-OPC-MySQL
		this.addProduct("PaaS-OPC-MySQL", 
				"MySQL", 
				"N/A", 
				"MySQL EE in the cloud. Compute node accessible.",
				"https://cloud.oracle.com/en_US/mysql/documentation");
		// PaaS-OPC-NoSQL
		this.addProduct("PaaS-OPC-NoSQL-Database", 
				"NoSQL", 
				"N/A", 
				"Coming soon. A scale-out, low latency key-value database service including support for JSON and Table data types. Built-in high availability, transactions, parallel query, and more.",
				"https://cloud.oracle.com/opc/paas/ebooks/Oracle_NoSQL_Database.pdf");
		// PaaS-OPC-Big-Data
		this.addProduct("PaaS-OPC-Big-Data", 
				"Big Data Cloud Service", 
				"BDCS", 
				"Oracle Big Data Cloud Service is an automated service that provides a high-powered environment tailor-made for advancing businesses’ analytical capabilities. With automated lifecycle management and one-click security, Big Data Cloud Service is designed to optimally and securely run a wide variety of big data workloads and technologies while simplifying operations.",
				"https://cloud.oracle.com/en_US/big-data/documentation");
		this.addProduct("PaaS-OPC-Big-Data", 
				"Big Data Cloud Service", 
				"BDCS", 
				"Oracle Big Data Cloud Service is an automated service that provides a high-powered environment tailor-made for advancing businesses’ analytical capabilities. With automated lifecycle management and one-click security, Big Data Cloud Service is designed to optimally and securely run a wide variety of big data workloads and technologies while simplifying operations.",
				"https://cloud.oracle.com/en_US/big-data/documentation");
		this.addProduct("PaaS-OPC-Big-Data", 
				"Big Data SQL Cloud Service", 
				"BDSCS", 
				"Oracle Big Data SQL Cloud Service enables organizations to immediately analyze data across Apache Hadoop, NoSQL and Oracle Database leveraging their existing SQL skills, security policies and applications with extreme performance. From simplifying data science efforts to unlocking data lakes, Big Data SQL makes the benefits of Big Data available to the largest group of end users possible.",
				"https://cloud.oracle.com/en_US/big-data/documentation");
		this.addProduct("PaaS-OPC-Big-Data", 
				"Big Data Cloud Machine", 
				"BDCM", 
				"Oracle Big Data Cloud Machine delivers the complete value of Oracle Big Data Cloud Service to customers who require their Big Data platform to be located on-premises.",
				"https://cloud.oracle.com/en_US/big-data/documentation");
		// PaaS-OPC-Big-Data-Compute-Edition
		this.addProduct("PaaS-OPC-Big-Data-Compute-Edition", 
				"Big Data – Compute Edition", 
				"BDCSCE", 
				"Apache Hadoop and Apache Spark delivered as a managed, elastic, integrated platform – for streaming, batch and interactive analysis.",
				"https://cloud.oracle.com/en_US/big-data-compute-edition/documentation");
		// PaaS-OPC-Event-Hub
		this.addProduct("PaaS-OPC-Event-Hub", 
				"Event Hub", 
				"N/A", 
				"Managed Apache Kafka in the Cloud. Oracle Event Hub Cloud Service delivers the power of Kafka as a managed streaming data platform integrated with the rest of Oracle’s Cloud.",
				"https://cloud.oracle.com/en_US/event-hub/documentation");
		// PaaS-OPC-Java
		this.addProduct("PaaS-OPC-Java", 
				"Oracle Java Cloud Service", 
				"JCS", 
				"Java Cloud Service is ideal for development, testing, user acceptance testing, staging and production. Instantly create dedicated and isolated WebLogic Server environments (your choice of the 11g or 12c version) to run your entire cluster at scale.",
				"https://cloud.oracle.com/en_US/java/documentation");
		this.addProduct("PaaS-OPC-Java", 
				"Oracle Java Cloud Service - SaaS Extension", 
				"JCSSE", 
				"Java Cloud Service - SaaS Extension is ideal for extending your Oracle SaaS applications. Seamlessly embed new functionality in existing SaaS services' UI, and create standalone mashup applications using data feeds from SaaS services or external sources.",
				"https://cloud.oracle.com/en_US/java/documentation");
		// Paas-OPC-Mobile
		this.addProduct("PaaS-OPC-Mobile", 
				"Mobile", 
				"N/A", 
				"Make mobile app development and integration quick, secure, and easy to deploy.",
				"https://cloud.oracle.com/en_US/mobile/documentation");
		// PaaS-OPC-Messaging
		this.addProduct("PaaS-OPC-Messaging", 
				"Messaging", 
				"N/A", 
				"Standard-based, robust and secure messaging solution in the cloud. The Oracle Messaging Cloud Service is a communication backbone delivered as a cloud service that connects any internet-based applications and devices on cloud and on-premise in a flexible, reliable and secure way, ideal for an event-driven service oriented architecture (SOA).",
				"https://cloud.oracle.com/en_US/messaging/documentation");
		// PaaS-OPC-Application-Container-Cloud
		this.addProduct("PaaS-OPC-Application-Container-Cloud", 
				"Application Container Cloud", 
				"ACC", 
				"Develop cloud native, 12-factor applications on a modern polyglot platform with Java SE, Node.js, PHP, Python and more.",
				"https://cloud.oracle.com/en_US/application-container-cloud/documentation");
		// PaaS-OPC-Developer
		this.addProduct("PaaS-OPC-Developer", 
				"Developer", 
				"N/A", 
				"Streamline Team Development and Software Delivery. Hosted team development and delivery platform including issue tracking, code versioning, wiki, agile-development tools, continuous integration and delivery automation.",
				"https://cloud.oracle.com/en_US/developer-service/documentation");
		// PaaS-OPC-Application-Builder
		this.addProduct("PaaS-OPC-Application-Builder", 
				"Application Builder", 
				"N/A", 
				"Rapidly create and host engaging business applications with a visual development environment right from the comfort of your browser.",
				"https://cloud.oracle.com/en_US/application-builder/documentation");
		// PaaS-OPC-API-Catalog
		this.addProduct("PaaS-OPC-API-Catalog", 
				"API Catalog", 
				"N/A", 
				"The API Catalog Cloud Service is a collection of machine-readable Open API (formerly Swagger), representing some of Oracle's most popular SaaS and PaaS applications. Use these descriptions to generate code stubs and facilitate integration between your applications and others in the Oracle Public Cloud.",
				"https://cloud.oracle.com/en_US/api-catalog/documentation");
		// PaaS-OPC-Data-Integration
		this.addProduct("PaaS-OPC-Data-Integration", 
				"Oracle Data Integrator", 
				"N/A", 
				"Oracle Data Integrator Cloud Service provides pushdown data processing; high performance ETL with less data movement which is best for the Cloud. Oracle Data Integrator Cloud Service executes data transformations where the data lies without having to copy data unnecessarily to remote locations.",
				"https://cloud.oracle.com/en_US/data-integrator/documentation");
		// PaaS-OPC-Integration
		this.addProduct("PaaS-OPC-Integration", 
				"Oracle Data Integrator", 
				"N/A", 
				"Maximize the value of your investments in SaaS and on-premises applications through a simple and powerful integration platform in the cloud.",
				"https://cloud.oracle.com/en_US/integration/documentation");
		// PaaS-OPC-SOA
		this.addProduct("PaaS-OPC-SOA", 
				"SOA Cloud Service", 
				"SOACS", 
				"Oracle SOA Suite is a comprehensive, standards-based software suite to build, deploy and manage integration following the concepts of service-oriented architecture (SOA). The components of the suite benefit from consistent tooling, a single deployment and management model, end-to-end security and unified metadata management. Oracle SOA Suite helps businesses lower costs by allowing maximum re-use of existing IT investments and assets, regardless of the environment (OS, application server, etc.) they run in, or the technology they were built upon. It is easy-to-use, re-use focused, unified application development tooling and end-to-end lifecycle management support further reduces development and maintenance cost and complexity.",
				"https://cloud.oracle.com/en_US/soa/documentation");
		this.addProduct("PaaS-OPC-SOA", 
				"Managed File Transfer Cloud Service", 
				"MFTCS", 
				"Oracle Managed File Transfer Cloud Service (Oracle MFT CS) enables secure file exchange and management between the cloud and both SaaS or on premise enterprise applications. Oracle Public Cloud provides the necessary cloud platform and infrastructure to provision your MFT cloud environment. Together, they protect against inadvertent access to unsecured files at every step in the end-to-end transfer of files. The MFT Console is easy to use especially for non-technical staff so you can leverage more resources to manage the transfer of files. The extensive reporting capabilities allow you to get quick status of a file transfer and resubmit it as required.",
				"https://cloud.oracle.com/en_US/soa/documentation");
		this.addProduct("PaaS-OPC-SOA", 
				"API Manager Cloud Service", 
				"N/A", 
				"Business leaders expect new business solutions to come to market quickly while the complexity of these solutions increases dramatically. Not only have the scope and scale of these solutions expanded, significant portions of the solutions are increasingly implemented on the cloud, outside of the IT organization. Application Programming Interfaces (APIs) provide access to, and information about, back-end business processes. As the number of APIs an organization produces and uses increases, the management and visibility of these APIs becomes increasingly important.",
				"https://cloud.oracle.com/en_US/soa/documentation");
		// PaaS-OPC-GoldenGate
		this.addProduct("PaaS-OPC-GoldenGate", 
				"GoldenGate", 
				"OGGCS", 
				"GoldenGate Cloud Service is a cloud based real-time data integration and replication service, which provides seamless data movement from various on-premises relational databases to databases in the cloud with sub-second latency while maintaining data consistency and offering fault tolerance and resiliency.",
				"https://cloud.oracle.com/en_US/goldengate/documentation");
		// PaaS-OPC-IoT
		this.addProduct("PaaS-OPC-GoldenGate", 
				"IoT", 
				"IoT", 
				"Gain new data-driven insights and drive actions from IoT by connecting, analyzing and integrating device data into your business processes and applications, enabling your business to deliver innovative new services faster and with less risk.",
				"https://cloud.oracle.com/en_US/iot/documentation");
		// PaaS-OPC-API-Platform
		this.addProduct("PaaS-OPC-API-Platform", 
				"API Platform", 
				"N/A", 
				"A great API Management solution supports agile API development, and also makes it easy to keep an eye on KPIs covering every aspect of the API lifecycle. True hybrid API deployment – in the Cloud or on-premises – means that your API solution is modern and adaptable, all while employing the most up-to-date security protocols.",
				"https://cloud.oracle.com/en_US/api-platform/documentation");
		// PaaS-OPC-Process
		this.addProduct("PaaS-OPC-Process", 
				"Process", 
				"N/A", 
				"Bring agility to your business with an easy, visual, low-code platform that simplifies day to day tasks by getting employees, customers, and partners the services they need to work anywhere, anytime, and on any device.",
				"https://cloud.oracle.com/en_US/process/documentation");
		// PaaS-OPC-Application-Performance-Monitoring
		this.addProduct("PaaS-OPC-Application-Performance-Monitoring", 
				"Application Performance Monitoring", 
				"APM", 
				"Oracle Application Performance Monitoring Cloud Service provides development and operations teams with the information that they need to find and fix application issues fast. All your end-user and application performance information (with associated application logs) are brought together into Oracle Management Cloud's secure, unified big data platform.",
				"https://cloud.oracle.com/en_US/application-performance-monitoring/documentation");
		// PaaS-OPC-Infrastructure-Monitoring
		this.addProduct("PaaS-OPC-Application-Infrastructure-Monitoring", 
				"Application Infrastructure Monitoring", 
				"AIM", 
				"Oracle Infrastructure Monitoring Cloud Service monitors the status and health of your entire IT infrastructure - on-premises or on the cloud -- from a single platform. Proactive monitoring across tiers enables administrators to be alerted on issues, troubleshoot and resolve these before they impact end users. This service is built on Oracle Management Cloud's secure, unified big data platform.",
				"https://cloud.oracle.com/en_US/infrastructure-monitoring/documentation");
		// PaaS-OPC-Log-Analytics
		this.addProduct("PaaS-OPC-Log-Analytics", 
				"Log Analytics", 
				"N/A", 
				"Oracle Log Analytics Cloud Service monitors, aggregates, indexes, and analyzes all log data from your applications and infrastructure – enabling users to search, explore, and correlate this data to troubleshoot problems faster, derive operational insight, and make better decisions. This service is built on Oracle Management Cloud's secure, unified big data platform.",
				"https://cloud.oracle.com/en_US/log-analytics/documentation");
		// PaaS-OPC-Orchestration
		this.addProduct("PaaS-OPC-Orchestration", 
				"Orchestration", 
				"N/A", 
				"Oracle Orchestration Cloud Service executes tasks at hyper cloud scale automating any by calling REST, scripts, or 3rd party automation frameworks. Oracle Orchestration Cloud Service can apply automation on both on-premises and cloud infrastructure. This service is built on Oracle Management Cloud's secure, unified big data platform.",
				"https://cloud.oracle.com/en_US/orchestration/datasheets");
		// PaaS-OPC-IT-Analytics
		this.addProduct("PaaS-OPC-IT-Analytics", 
				"IT Analytics", 
				"N/A", 
				"Oracle IT Analytics Cloud Service provides 360-degree insight into the performance, availability, and capacity of applications and infrastructure investments, enabling line-of-business and IT executives, analysts, and administrators to make critical decisions about their IT estate. This service is built on Oracle Management Cloud's secure, unified big data platform.",
				"https://cloud.oracle.com/en_US/it-analytics/documentation");
		// PaaS-OPC-Content-and-Experience
		this.addProduct("PaaS-OPC-Content-and-Experience", 
				"Content and Experience", 
				"N/A", 
				"Oracle Content and Experience Cloud is a cloud-based content hub to drive omni-channel content management and accelerate experience delivery.",
				"https://cloud.oracle.com/en_US/content/documentation");
		// PaaS-OPC-WebCenter-Portal-Cloud
		this.addProduct("PaaS-OPC-WebCenter-Portal-Cloud", 
				"WebCenter Portal Cloud", 
				"WCP", 
				"Deliver seamless and consistent digital experience across multiple channels to your employees, partners, and customers. Provision Oracle WebCenter Portal Cloud on top of Oracle Java Cloud Service (JCS), with just a few clicks.",
				"https://cloud.oracle.com/en_US/wcpcloud/documentation");
		// PaaS-OPC-DIVA-Cloud
		this.addProduct("PaaS-OPC-DIVA-Cloud", 
				"DIVA Cloud", 
				"DIVA", 
				"Oracle DIVA Cloud provides innovative, media-centric cloud solutions based on Oracle’s industry-leading content storage management software. Protect and monetize your digital-media assets using the same software, services and infrastructure trusted to manage over one exabyte of the world’s media content.",
				"https://cloud.oracle.com/en_US/diva/datasheets");
		// PaaS-OPC-Analytics-Cloud
		this.addProduct("PaaS-OPC-Analytics-Cloud", 
				"Analytics Cloud", 
				"N/A", 
				"Oracle Analytics Cloud is a single platform that empowers your entire organization to ask any question of any data in any environment on any device.",
				"https://cloud.oracle.com/en_US/oac/documentation");
		// PaaS-OPC-Business-Intelligence
		this.addProduct("PaaS-OPC-Business-Intelligence", 
				"Business Intelligence", 
				"N/A", 
				"A proven platform for creating powerful business intelligence applications, enabling users from the workgroup to the enterprise.",
				"https://cloud.oracle.com/en_US/business-intelligence/documentation");
		// PaaS-OPC-Big-Data-Discovery
		this.addProduct("PaaS-OPC-Big-Data-Discovery", 
				"Big Data Discovery", 
				"BDD", 
				"The Visual Face of Big Data. A single, easy to use product for anyone to transform raw data into business insight in minutes, without the need to learn complex tools or rely only on highly specialized resources.",
				"https://cloud.oracle.com/en_US/big-data-discovery/documentation");
		// PaaS-OPC-Big-Data-Preparation
		this.addProduct("PaaS-OPC-Big-Data-Preparation", 
				"Big Data Preparation", 
				"BDP", 
				"Built natively in Hadoop and Spark for scale, Oracle Big Data Preparation Cloud Service provides a highly intuitive and interactive way for analysts to prepare unstructured, semi-structured and structured data for downstream processing.",
				"https://cloud.oracle.com/en_US/big-data-preparation/documentation");
		// PaaS-OPC-Big-Data-Data-Visualization
		this.addProduct("PaaS-OPC-Data-Visualization", 
				"Data Visualization", 
				"BDDV", 
				"Get instant clarity with stunningly visual analysis and self-service discovery. Fast, fluid insights for the entire organization.",
				"https://cloud.oracle.com/en_US/data-visualization/documentation");
		// PaaS-OPC-Essbase
		this.addProduct("PaaS-OPC-Essbase", 
				"Essbase", 
				"N/A", 
				"Push button simplicity to empower all analytics users with all of the rich functionality the enterprise expects. Now available as part of Oracle Analytics Cloud.",
				"https://cloud.oracle.com/en_US/essbase/policies");
		// PaaS-OPC-CASB
		this.addProduct("PaaS-OPC-CASB", 
				"CASB", 
				"CASB", 
				"The Oracle CASB Cloud Service is the only Cloud Access Security Broker (CASB) that gives you both visibility into your entire cloud stack and the security automation tool your IT team needs.",
				"https://cloud.oracle.com/en_US/casb/documentation");
		// PaaS-OPC-Identity
		this.addProduct("PaaS-OPC-Identity", 
				"Identity", 
				"N/A", 
				"The next generation comprehensive security and identity platform that is cloud-native and designed to be an integral part of the enterprise security fabric, providing modern identity for modern applications",
				"https://cloud.oracle.com/en_US/identity/documentation");
		// PaaS-OPC-Security-Monitoring-and-Analytics
		this.addProduct("PaaS-OPC-Security-Monitoring-and-Analytics", 
				"Security Monitoring and Analytics", 
				"SMA", 
				"Oracle Security Monitoring and Analytics (SMA) Cloud Service enables rapid detection, investigation and remediation of the broadest range of security threats across on-premises and cloud IT assets. Security Monitoring and Analytics provides integrated SIEM and UEBA capabilities built on machine learning, user session awareness, and up-to-date threat intelligence context. This service is built on Oracle Management Cloud's secure, unified big data platform.",
				"https://cloud.oracle.com/en_US/security-analytics/datasheets");
		// PaaS-OPC-Configuration-and-Compliance
		this.addProduct("PaaS-OPC-Configuration-and-Compliance", 
				"Configuration and Compliance", 
				"N/A", 
				"Oracle Configuration and Compliance Service enables the IT and Business Compliance function to assess, score and remediate violations using industry standard benchmarks in addition to your own custom rules. The Oracle Configuration and Compliance Service can assess both on premise and cloud infrastructure. This service is built on Oracle Management Cloud's secure, unified big data platform.",
				"https://cloud.oracle.com/en_US/compliance/datasheets");
	}
	
	private void addProduct(String cat, String name, String alias, String desc, String doc){
		OracleCloudProduct p = new OracleCloudProduct(cat, name,alias,desc,doc);
		this.nameTable.put(p.getName(), p);
	}
	
	public void printAllOracleCloudProduct(){
		sk.printTitle(0, "Oracle Cloud Map (IaaS & PaaS)");
		for(String name:nameTable.keySet()){
			sk.printResult(0, true, nameTable.get(name)+"\n");
		}
		sk.printTitle(0, "End");
	}
	
	class IndexMaker extends Thread{
		
	}
}
