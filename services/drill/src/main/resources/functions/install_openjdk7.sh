#
# Licensed to the Apache Software Foundation (ASF) under one or more
# contributor license agreements.  See the NOTICE file distributed with
# this work for additional information regarding copyright ownership.
# The ASF licenses this file to You under the Apache License, Version 2.0
# (the "License"); you may not use this file except in compliance with
# the License.  You may obtain a copy of the License at
#
#     http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# See the License for the specific language governing permissions and
# limitations under the License.
#
function install_openjdk7_deb() {
  retry_apt_get update
  retry_apt_get -y install openjdk-7-jdk
  
  # Try to set JAVA_HOME in a number of commonly used locations
  # Lifting JAVA_HOME detection from jclouds
  if [ -z "$JAVA_HOME" ]; then
      for CANDIDATE in `ls -d /usr/lib/jvm/java-1.7.0-openjdk-* /usr/lib/jvm/java-7-openjdk-* /usr/lib/jvm/java-7-openjdk 2>&-`; do
          if [ -n "$CANDIDATE" -a -x "$CANDIDATE/bin/java" ]; then
              export JAVA_HOME=$CANDIDATE
              break
          fi
      done
  fi

  if [ -f /etc/profile ]; then
    echo export JAVA_HOME=$JAVA_HOME >> /etc/profile
  fi
  if [ -f /etc/bashrc ]; then
    echo export JAVA_HOME=$JAVA_HOME >> /etc/bashrc
  fi
  if [ -f ~root/.bashrc ]; then
    echo export JAVA_HOME=$JAVA_HOME >> ~root/.bashrc
  fi
  if [ -f /etc/skel/.bashrc ]; then
    echo export JAVA_HOME=$JAVA_HOME >> /etc/skel/.bashrc
  fi
  if [ -f "$DEFAULT_HOME/$NEW_USER" ]; then
    echo export JAVA_HOME=$JAVA_HOME >> $DEFAULT_HOME/$NEW_USER
  fi

  update-alternatives --install /usr/bin/java java $JAVA_HOME/bin/java 17000
  update-alternatives --set java $JAVA_HOME/bin/java
  java -version
}

function install_openjdk7_rpm() {
  retry_yum -y install java-1.7.0-openjdk-devel
  
  # Try to set JAVA_HOME in a number of commonly used locations
  # Lifting JAVA_HOME detection from jclouds
  if [ -z "$JAVA_HOME" ]; then
      for CANDIDATE in `ls -d /usr/lib/jvm/java-1.6.0-openjdk-* /usr/lib/jvm/java-7-openjdk-* /usr/lib/jvm/java-7-openjdk 2>&-`; do
          if [ -n "$CANDIDATE" -a -x "$CANDIDATE/bin/java" ]; then
              export JAVA_HOME=$CANDIDATE
              break
          fi
      done
  fi
  if [ -f /etc/profile ]; then
    echo export JAVA_HOME=$JAVA_HOME >> /etc/profile
  fi
  if [ -f /etc/bashrc ]; then
    echo export JAVA_HOME=$JAVA_HOME >> /etc/bashrc
  fi
  if [ -f ~root/.bashrc ]; then
    echo export JAVA_HOME=$JAVA_HOME >> ~root/.bashrc
  fi
  if [ -f /etc/skel/.bashrc ]; then
    echo export JAVA_HOME=$JAVA_HOME >> /etc/skel/.bashrc
  fi
  if [ -f "$DEFAULT_HOME/$NEW_USER" ]; then
    echo export JAVA_HOME=$JAVA_HOME >> $DEFAULT_HOME/$NEW_USER
  fi

  alternatives --install /usr/bin/java java $JAVA_HOME/bin/java 17000
  alternatives --set java $JAVA_HOME/bin/java
  java -version
}

function install_openjdk7() {
  if which dpkg &> /dev/null; then
    install_openjdk7_deb
  elif which rpm &> /dev/null; then
    install_openjdk7_rpm
  fi
}
