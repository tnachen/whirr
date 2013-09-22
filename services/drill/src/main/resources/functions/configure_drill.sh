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
function configure_drill() {

  CLUSTER_NAME=$1
  ZOOKEEPER=$2
  USER_PORT=$3
  BIT_PORT=$4
  HOSTNAME=$PRIVATE_IP

  # Configure runtime.properties with Zookeeper address
  cat > /usr/local/drill-override.conf <<EOF

drill.exec: {
  cluster-id: "$CLUSTER_NAME"
  rpc: {
  	user.port : $USER_PORT,
  	bit.port : $BIT_PORT
  },
  operator: {
    packages += "org.apache.drill.exec.physical.config"
  },
  optimizer: {
    implementation: "org.apache.drill.exec.opt.IdentityOptimizer"
  },
  storage: {
	packages += "org.apache.drill.exec.store"
  }
  metrics : {
  	context: "drillbit"
  },
  zk: {
	connect: "$ZOOKEEPER",
	root: "/drill",
	refresh: 500,
	timeout: 5000,
	retry: {
	  count: 7200,
	  delay: 500
	}
  }

  network: {
    start: 35000
  }
}

EOF

}