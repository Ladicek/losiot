FROM openshift/jenkins-slave-maven-centos7:latest
MAINTAINER Ladislav Thon <lthon@redhat.com>

USER root

RUN yum install -y bzip2 xorg-x11-server-Xvfb gtk2 && \
    yum clean all -y && \
    curl -O http://ftp.mozilla.org/pub/firefox/releases/45.9.0esr/linux-x86_64/en-US/firefox-45.9.0esr.tar.bz2 && \
    tar xfj firefox-45.9.0esr.tar.bz2 && \
    rm firefox-45.9.0esr.tar.bz2

USER 1001
