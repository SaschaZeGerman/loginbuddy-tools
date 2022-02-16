build_sample:
	LOGINBUDDY_SIDECAR_LOCATION=http://loginbuddy-sidecar:8044
	mvn clean package
	docker build --no-cache --tag saschazegerman/loginbuddy-tools-sample:latest -f sample/Dockerfile ./sample