build_all:
	export LOGINBUDDY_SIDECAR_LOCATION=http://loginbuddy-sidecar:8044
	#mvn clean install
	mvn -Dmaven.test.skip=true clean install