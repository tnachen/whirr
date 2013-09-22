/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.whirr.service.drill;

import org.apache.whirr.Cluster;
import org.apache.whirr.Cluster.Instance;
import org.apache.whirr.service.ClusterActionEvent;
import org.apache.whirr.service.ClusterActionHandlerSupport;
import org.apache.whirr.service.FirewallManager;
import org.apache.whirr.service.zookeeper.ZooKeeperCluster;

import java.io.IOException;
import java.util.Set;

import static org.apache.whirr.RolePredicates.role;
import static org.apache.whirr.service.FirewallManager.Rule;
import static org.jclouds.scriptbuilder.domain.Statements.call;

public class DrillClusterActionHandler extends ClusterActionHandlerSupport {

  public static final String DRILL_ROLE = "drill";
  public static final int USER_PORT = 31010;
  public static final int BIT_PORT = 32011;

  public static final String MAJOR_VERSION = "whirr.drill.version.major";

  @Override
  public String getRole() {
    return DRILL_ROLE;
  }

  @Override
  protected void beforeBootstrap(ClusterActionEvent event) throws IOException {
  }

  @Override
  protected void beforeConfigure(final ClusterActionEvent event)
    throws IOException, InterruptedException {
    Cluster cluster = event.getCluster();
    Set<Instance> instances = cluster.getInstancesMatching(role(DRILL_ROLE));

    event.getFirewallManager().addRule(
      Rule.create()
        .destination(instances)
        .ports(USER_PORT, BIT_PORT)
    );

    // Open a port for the service
    event.getFirewallManager().addRule(
        FirewallManager.Rule.create().destination(role(getRole())).port(USER_PORT)
    );

    event.getFirewallManager().addRule(
        FirewallManager.Rule.create().destination(role(getRole())).port(BIT_PORT)
    );

    handleFirewallRules(event);

    String quorum = ZooKeeperCluster.getHosts(event.getCluster(), true);

    addStatement(event, call("retry_helpers"));
    addStatement(event, call("install_openjdk7"));
    addStatement(event, call("configure_hostnames"));
    addStatement(event, call("configure_drill",
        getRole(), quorum, Integer.toString(USER_PORT), Integer.toString(BIT_PORT)));


    //Configuration config = clusterSpec.getConfiguration();

    //String major = config.getString(MAJOR_VERSION, null);

    addStatement(event, call("install_drill"));
  }

  @Override
  protected void beforeStart(ClusterActionEvent event) {
    addStatement(event, call("start_drill"));
  }

  @Override
  protected void beforeStop(ClusterActionEvent event) {
    addStatement(event, call("stop_drill"));
  }

  @Override
  protected void beforeCleanup(ClusterActionEvent event) {
    addStatement(event, call("remove_service"));
    addStatement(event, call("cleanup_drill"));
  }
}
