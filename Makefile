build_sample:
	mvn clean package
	docker build --no-cache --tag saschazegerman/loginbuddy-tools-sample:latest -f sample/Dockerfile ./sample