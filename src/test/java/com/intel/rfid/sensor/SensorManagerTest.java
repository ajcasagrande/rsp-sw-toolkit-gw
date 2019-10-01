/*
 * Copyright (C) 2018 Intel Corporation
 * SPDX-License-Identifier: BSD-3-Clause
 */
package com.intel.rfid.sensor;

import com.intel.rfid.api.data.BooleanResult;
import com.intel.rfid.api.data.Cluster;
import com.intel.rfid.api.data.ClusterConfig;
import com.intel.rfid.api.data.Personality;
import com.intel.rfid.api.sensor.ConnectRequest;
import com.intel.rfid.api.sensor.Platform;
import com.intel.rfid.cluster.ClusterManager;
import com.intel.rfid.cluster.MockClusterManager;
import com.intel.rfid.downstream.MockDownstreamManager;
import com.intel.rfid.exception.ConfigException;
import com.intel.rfid.gpio.MockGPIOManager;
import com.intel.rfid.helpers.EnvHelper;
import com.intel.rfid.helpers.StringHelper;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class SensorManagerTest {


    @BeforeClass
    public static void beforeClass() throws IOException {
        EnvHelper.beforeTests();
    }

    @AfterClass
    public static void afterClass() {
        EnvHelper.afterTests();
    }

    @Test
    public void testConvertCaseRSPId() {
        assertThat(StringHelper.convertCaseRSPId("ABCDE__RSP-1A2b3C")).isEqualTo("ABCDE__RSP-1a2b3c");
        assertThat(StringHelper.convertCaseRSPId("ABCDE__1A2b3C")).isEqualTo("ABCDE__1A2b3C");
        assertThat(StringHelper.convertCaseRSPId("RSP-AABBCC")).isEqualTo("RSP-aabbcc");
    }

    @Test
    public void testAliasing() {

        MockClusterManager clusterMgr = new MockClusterManager();
        MockSensorManager sensorMgr = new MockSensorManager(clusterMgr);

        String deviceId1 = "RSP-TEST01";
        String deviceId2 = "RSP-TEST02";

        SensorPlatform rsp01 = sensorMgr.establish(deviceId1);
        SensorPlatform rsp02 = sensorMgr.establish(deviceId2);

        // Check constructed with defaults
        for (int i = 0; i < SensorPlatform.NUM_ALIASES; i++) {
            assertThat(rsp01.getAlias(i)).isEqualTo(rsp01.getDefaultAlias(i));
        }

        // Check that connecting will affect the aliasing correctly
        ConnectRequest connectReq1 = new ConnectRequest();
        connectReq1.params.hostname = deviceId1;
        connectReq1.params.platform = Platform.H1000;
        rsp01.onConnect(connectReq1);

        ConnectRequest connectReq2 = new ConnectRequest();
        connectReq2.params.hostname = deviceId2;
        connectReq2.params.platform = Platform.H3000;
        rsp02.onConnect(connectReq2);

        for (int i = 0; i < SensorPlatform.NUM_ALIASES; i++) {
            assertThat(rsp01.getAlias(i)).isEqualTo(rsp01.getDefaultAlias(i));
            assertThat(rsp02.getAlias(i)).isEqualTo(deviceId2);
        }

        // Check ways to set a default
        rsp01.setAlias(0, null);
        rsp01.setAlias(1, "");
        rsp01.setAlias(3, SensorPlatform.ALIAS_KEY_DEFAULT);

        for (int i = 0; i < SensorPlatform.NUM_ALIASES; i++) {
            assertThat(rsp01.getAlias(i)).isEqualTo(rsp01.getDefaultAlias(i));
        }

        for (int i = 0; i < SensorPlatform.NUM_ALIASES; i++) {
            rsp01.setAlias(i, SensorPlatform.ALIAS_KEY_DEVICE_ID);
            assertThat(rsp01.getAlias(i)).isEqualTo(rsp01.getDeviceId());
        }

        assertThat(rsp01.getAlias(0)).isEqualTo(rsp01.getAlias(1));
        assertThat(rsp01.getAlias(2)).isEqualTo(rsp01.getAlias(1));
        assertThat(rsp01.getAlias(2)).isEqualTo(rsp01.getAlias(3));

        rsp01.setAlias(0, "alias-0");
        assertThat(rsp01.getAlias(0)).isEqualTo("alias-0");
        rsp01.setAlias(1, "alias-1");
        assertThat(rsp01.getAlias(1)).isEqualTo("alias-1");
        rsp01.setAlias(2, "alias-2");
        assertThat(rsp01.getAlias(2)).isEqualTo("alias-2");
        rsp01.setAlias(3, "alias-3");
        assertThat(rsp01.getAlias(3)).isEqualTo("alias-3");

    }

    @Test
    public void testPersistRestore() throws IOException {

        Files.deleteIfExists(SensorManager.CACHE_PATH);

        String dev01 = "RSP-000001";
        String facility01 = "facility-01";
        String token01 = "token-01";
        String alias01 = "alias-01";
        String alias03 = "alias-03";

        Personality personality = Personality.EXIT;

        ClusterManager clusterMgr = new ClusterManager();

        SensorManager sensorMgr = new SensorManager(clusterMgr);
        List<SensorPlatform> sensors = new ArrayList<>();
        SensorPlatform sensor;
        // try restoring first to check for any errors from nothing
        sensorMgr.restore();
        sensorMgr.getSensors(sensors);
        assertThat(sensors).isEmpty();

        sensor = sensorMgr.establish(dev01);
        sensor.setProvisionToken(token01);
        sensor.setFacilityId(facility01);
        sensor.setPersonality(personality);
        sensor.setAlias(1, alias01);
        sensor.setAlias(3, alias03);

        sensorMgr.establish("UnitTestRSP-112233");
        sensorMgr.establish("UnitTestRSP-DDEEFF");
        sensorMgr.persist();
        assertThat(Files.exists(SensorManager.CACHE_PATH)).isTrue();

        // lookups and establishes are case insensitive
        sensor = sensorMgr.getSensor("UnitTestRSP-DDEEFF");
        assertThat(sensor).isNotNull();
        assertThat(sensor.getDeviceId()).isEqualTo("UnitTestRSP-ddeeff");

        assertThat(sensorMgr.getSensor("UnitTestRSP-ddeeff")).isNotNull();

        sensor = sensorMgr.establish("UnitTestRSP-DDEEFF");
        assertThat(sensor).isNotNull();
        assertThat(sensor.getDeviceId()).isEqualTo("UnitTestRSP-ddeeff");


        sensorMgr = new SensorManager(clusterMgr);
        sensors.clear();
        sensorMgr.getSensors(sensors);
        assertThat(sensors).isEmpty();

        sensorMgr.restore();
        sensors.clear();
        sensorMgr.getSensors(sensors);
        assertThat(sensors).hasSize(3);

        sensors.clear();
        sensors.addAll(sensorMgr.findRSPs(dev01));

        assertThat(sensors).hasSize(1);
        sensor = sensors.get(0);
        assertThat(sensor.getDeviceId()).isEqualTo(dev01);
        assertThat(sensor.getProvisionToken()).isEqualTo(token01);
        assertThat(sensor.getFacilityId()).isEqualTo(facility01);
        assertThat(sensor.getAlias(1)).isEqualTo(alias01);
        assertThat(sensor.getAlias(3)).isEqualTo(alias03);
        assertThat(sensor.getAlias(0)).isEqualTo(sensor.getDefaultAlias(0));
        assertThat(sensor.getAlias(2)).isEqualTo(sensor.getDefaultAlias(2));
        assertThat(sensor.getAlias(4)).isEqualTo(sensor.getDefaultAlias(4));

        assertThat(sensor.getPersonality()).isEqualTo(personality);
    }


    @Test
    public void testCaseInsensitive() {
        MockClusterManager clusterMgr = new MockClusterManager();
        MockSensorManager sensorMgr = new MockSensorManager(clusterMgr);
        clusterMgr.setSensorManager(sensorMgr);

        String id01Upper = "RSP-15AB2C";
        String id01Lower = "RSP-15ab2c";

        // internal storage is UPPER case by default
        SensorPlatform rsp00 = sensorMgr.establish(id01Lower);
        HashSet<String> allIds = new HashSet<>();
        sensorMgr.getDeviceIds(allIds);
        assertThat(allIds).doesNotContain(id01Lower);
        assertThat(allIds).contains(id01Upper);

        // referencing / lookup is case insensitive
        SensorPlatform rsp01 = sensorMgr.getSensor(id01Upper);
        assertThat(rsp00).isEqualTo(rsp01);
        assertThat(rsp00.deviceId).isEqualTo(id01Upper);

    }

    @Test
    public void testRemove() throws IOException, InterruptedException, ConfigException {
        MockClusterManager clusterMgr = new MockClusterManager();
        MockSensorManager sensorMgr = new MockSensorManager(clusterMgr);
        clusterMgr.setSensorManager(sensorMgr);
        MockGPIOManager gpioMgr = new MockGPIOManager(sensorMgr);
        MockDownstreamManager downstreamMgr = new MockDownstreamManager(sensorMgr, gpioMgr);

        MockSensorPlatform rsp01 = sensorMgr.establish("RSP-TEST01");
        assertThat(rsp01.isConnected()).isFalse();
        assertThat(downstreamMgr.handlerExistsFor(rsp01)).isFalse();


        downstreamMgr.connectSequence(rsp01);
        assertThat(rsp01.isConnected()).isTrue();
        assertThat(downstreamMgr.handlerExistsFor(rsp01)).isTrue();

        BooleanResult removeResult = sensorMgr.remove(rsp01);
        assertThat(removeResult.success).isFalse();
        System.out.println(removeResult.message);

        ClusterConfig clusterCfg = new ClusterConfig();
        clusterCfg.id = "test_cluster_cfg";
        Cluster cluster = new Cluster();
        cluster.id = "test_cluster_one";
        cluster.behavior_id = "ClusterAllSeq_PORTS_1";
        cluster.facility_id = "test_facility";
        List<String> sensorGroup1 = new ArrayList<>();
        sensorGroup1.add(rsp01.getDeviceId());
        cluster.sensor_groups.add(sensorGroup1);
        clusterCfg.clusters.add(cluster);
        clusterMgr.loadConfig(clusterCfg);

        sensorMgr.disconnectAll();
        assertThat(rsp01.isConnected()).isFalse();

        removeResult = sensorMgr.remove(rsp01);
        assertThat(removeResult.success).isFalse();
        System.out.println(removeResult.message);

        clusterMgr.deleteConfig();
        removeResult = sensorMgr.remove(rsp01);
        assertThat(removeResult.success).isTrue();
        System.out.println(removeResult.message);
        assertThat(sensorMgr.getSensor("RSP-TEST01")).isNull();


        // THIS IS WHAT WAS CAUSING A BUG
        // removing a sensor that was disconnected, and then it would
        // reconnect, but not show up in the sensor manager because
        // the downstream manager wasn't getting rid of the previous message
        // handler object for the same device id.
        assertThat(downstreamMgr.handlerExistsFor(rsp01)).isFalse();

        downstreamMgr.connectSequence(rsp01);
        assertThat(downstreamMgr.handlerExistsFor(rsp01)).isTrue();

        SensorPlatform nextRSP = sensorMgr.getSensor("RSP-TEST01");
        assertThat(nextRSP).isNotNull();
        assertThat(nextRSP.isConnected()).isTrue();

    }

}
