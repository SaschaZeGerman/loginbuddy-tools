FROM tomcat:jdk11-openjdk

# We do not want to keep the default applications, remove them
#
RUN rm -rf /usr/local/tomcat/webapps/*

# add our own application as the one and only
#
# Default loginbuddy services
#
COPY target/sample-1.0.0/ /usr/local/tomcat/webapps/ROOT

COPY docker-build/add-ons/server/server.xml /usr/local/tomcat/conf/server.xml
COPY docker-build/add-ons/server/entrypoint.sh /opt/docker/entrypoint.sh

# Create directory for holding SSL keys
#
RUN mkdir /usr/local/tomcat/ssl

# create non-priviliged user (with its own group) to run tomcat
#
RUN addgroup --system tomcat
RUN adduser --system --no-create-home --ingroup tomcat tomcat
RUN chown -R tomcat:tomcat /usr/local/tomcat/

# Run the entrypoint script to run tomcat and create a private key for stting up SSL/TLS
#
CMD ["/opt/docker/entrypoint.sh"]