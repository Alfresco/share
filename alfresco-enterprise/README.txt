root/enterpriseprojects/overlays/alfresco

This maven project is for the alfresco enterprise platform with the share extensions AMP.

build the war file with
 
    mvn install -Penterprise
 
    the war file will be available in the "target" directory.
 
Run an alfresco repository with the share extensions amp for development purposes
 
    use mvn install -Prun -DskipTests
    the repository will be started with a temporary database, and solr 4 server.


