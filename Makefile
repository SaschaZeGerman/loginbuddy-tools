define BUILD_DOCKER
	docker build --no-cache --tag saschazegerman/loginbuddy-tools:latest -f Dockerfile_builder .
endef

# Compile the code
# Run the target 'build_all' of the repository 'loginbuddy' before running this target
# https://github.com/SaschaZeGerman/loginbuddy
#
build_all:
	mvn clean install

# Compile the code and build docker images using the builder image
# Use this target if you do not have Java and Maven installed
# Run the target 'build_all_non_dev' of the repository 'loginbuddy' before running this target
# https://github.com/SaschaZeGerman/loginbuddy
#
build_all_non_dev:
	docker run -v `pwd`:/tmp saschazegerman/loginbuddy-builder:latest mvn -f "/tmp/pom.xml" clean install
	$(BUILD_DOCKER)
