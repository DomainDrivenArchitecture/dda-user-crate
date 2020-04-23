FROM ubuntu:18.04

RUN apt-get update > /dev/null \
    && apt-get -qqy upgrade > /dev/null \
    && apt-get -qqy install openjdk-11-jre > /dev/null

RUN apt-get -qqy install curl apt-utils sudo gnupg openssh-server > /dev/null

# prepare install user
RUN useradd install-user
RUN adduser install-user sudo
COPY integration/docker/image_as_sudoer/install-user.sudoers /etc/sudoers.d/install-user

# enable ssh service
RUN mkdir /var/run/sshd
RUN echo 'install-user:insecure' | chpasswd
RUN sed -i 's/PermitRootLogin prohibit-password/PermitRootLogin yes/' /etc/ssh/sshd_config
RUN sed 's@session\s*required\s*pam_loginuid.so@session optional pam_loginuid.so@g' -i /etc/pam.d/sshd
ENV NOTVISIBLE "in users profile"
RUN echo "export VISIBLE=now" >> /etc/profile
EXPOSE 22
CMD ["/usr/sbin/sshd", "-D"]
