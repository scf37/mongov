FROM dockerfile/java:oracle-java8

ADD build/install/mongov /opt/mongov

WORKDIR /opt/mongov
CMD ["bin/mongov"]
