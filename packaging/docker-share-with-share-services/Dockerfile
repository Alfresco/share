FROM alfresco/alfresco-community-repo-base:${image.tag}

# Apply amps... - check acs-community-packaging for:
# 1. How to clone *github.com/Alfresco/alfresco-community-repo* & build its docker
# image local (with "-PcommunityDocker" or "-PenterpriseDocker" profiles)
# 2. How to download/copy/move dependency amps (in this module's target/dependency subdir)
# 3. How to apply the amps here (quite similar)
ARG TOMCAT_DIR=/usr/local/tomcat

# Copy Dockerfile to avoid an error if no AMPs exist
COPY target/dependency/*.amp $TOMCAT_DIR/amps_share/
RUN java -jar $TOMCAT_DIR/alfresco-mmt/alfresco-mmt*.jar install \
              $TOMCAT_DIR/amps_share $TOMCAT_DIR/webapps/share -directory -nobackup

EXPOSE 8000

