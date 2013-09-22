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
package org.apache.whirr.service.drill.integration;

import org.apache.commons.configuration.CompositeConfiguration;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.apache.whirr.Cluster;
import org.apache.whirr.ClusterController;
import org.apache.whirr.ClusterSpec;
import org.apache.whirr.TestConstants;
import org.apache.whirr.service.drill.DrillClusterActionHandler;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.DatagramSocket;
import java.net.ServerSocket;

import static org.junit.Assert.assertTrue;

public class DrillServiceTest {
  private static final Logger LOG =
      LoggerFactory.getLogger(DrillClusterActionHandler.class);

  private ClusterSpec clusterSpec;
  private ClusterController controller;
  private Cluster cluster;

  @Before
  public void setUp() throws Exception {
    CompositeConfiguration config = new CompositeConfiguration();
    config.addConfiguration(new PropertiesConfiguration("whirr-drill-test.properties"));
    if (System.getProperty("config") != null) {
      config.addConfiguration(new PropertiesConfiguration(System.getProperty("config")));
    }
    clusterSpec = ClusterSpec.withTemporaryKeys(config);
    controller = new ClusterController();
    cluster = controller.launchCluster(clusterSpec);
  }

  @Test(timeout = TestConstants.ITEST_TIMEOUT)
  public void testClusterStart() {
    assertTrue(available(DrillClusterActionHandler.BIT_PORT));
  }

  public static boolean available(int port) {
    ServerSocket ss = null;
    DatagramSocket ds = null;
    try {
      ss = new ServerSocket(port);
      ss.setReuseAddress(true);
      ds = new DatagramSocket(port);
      ds.setReuseAddress(true);
      return true;
    } catch (IOException e) {
    } finally {
      if (ds != null) {
        ds.close();
      }

      if (ss != null) {
        try {
          ss.close();
        } catch (IOException e) {
                /* should not be thrown */
        }
      }
    }

    return false;
  }

  @After
  public void tearDown() throws IOException, InterruptedException {
    controller.destroyCluster(clusterSpec);
  }
}
