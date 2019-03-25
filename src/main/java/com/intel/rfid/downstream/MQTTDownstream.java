/*
 * Copyright (C) 2018 Intel Corporation
 * SPDX-License-Identifier: BSD-3-Clause
 */
package com.intel.rfid.downstream;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.intel.rfid.api.GatewayStatusUpdate;
import com.intel.rfid.api.MQTTSummary;
import com.intel.rfid.exception.GatewayException;
import com.intel.rfid.gateway.ConfigManager;
import com.intel.rfid.helpers.Jackson;
import com.intel.rfid.mqtt.MQTT;
import org.eclipse.paho.client.mqttv3.MqttMessage;

public class MQTTDownstream extends MQTT {

    public static final String TOPIC_PREFIX = "rfid/rsp";

    public static final String COMMAND_TOPIC = TOPIC_PREFIX + "/command";
    public static final String CONNECT_TOPIC = TOPIC_PREFIX + "/connect";
    public static final String DATA_TOPIC = TOPIC_PREFIX + "/data";
    public static final String GW_STATUS_TOPIC = TOPIC_PREFIX + "/gw_status";
    public static final String RESPONSE_TOPIC = TOPIC_PREFIX + "/response";
    public static final String RSP_STATUS_TOPIC = TOPIC_PREFIX + "/rsp_status";

    protected Dispatch dispatch;
    protected ObjectMapper mapper = Jackson.getMapper();

    public MQTTDownstream(Dispatch _dispatch) {
        ConfigManager cm = ConfigManager.instance;
        credentials = cm.getMQTTDownstreamCredentials();
        brokerURI = cm.getMQTTDownstreamURI();
        dispatch = _dispatch;
    }

    @Override
    public void start() {
        subscribe(CONNECT_TOPIC);
        subscribe(RESPONSE_TOPIC + "/#");
        subscribe(RSP_STATUS_TOPIC + "/#");
        subscribe(DATA_TOPIC + "/#");

        super.start();
    }

    protected void onConnect() {
        super.onConnect();
        try {
            String deviceId = ConfigManager.instance.getGatewayDeviceId();
            GatewayStatusUpdate gsu = new GatewayStatusUpdate(deviceId, GatewayStatusUpdate.READY);
            publishGWStatus(mapper.writeValueAsBytes(gsu));
            log.info("Published GatewayStatusUpdate.READY");
        } catch (Exception e) {
            log.warn("Error publish GatewayStatusUpdate.READY:", e);
        }
    }

    public interface Dispatch {
        void onMessage(final String _topic, final MqttMessage _msg);
    }

    @Override
    public void messageArrived(final String _topic, final MqttMessage _msg) throws Exception {

        if (_msg.isRetained()) {
            log.info("Discarding retained message");
            return;
        }
        if (_msg.isDuplicate()) {
            log.info("Discarding duplicate message");
            return;
        }

        dispatch.onMessage(_topic, _msg);
    }

    public void publishConnectResponse(String _deviceId, byte[] _msg) throws GatewayException {
        String topic = CONNECT_TOPIC + "/" + _deviceId;
        publish(topic, _msg, DEFAULT_QOS);
    }

    public void publishCommand(String _deviceId, byte[] _msg) throws GatewayException {
        String topic = COMMAND_TOPIC + "/" + _deviceId;
        publish(topic, _msg, DEFAULT_QOS);
    }

    public void publishGWStatus(byte[] _msg) throws GatewayException {
        publish(GW_STATUS_TOPIC, _msg, DEFAULT_QOS);
    }

    public MQTTSummary getSummary() {
        MQTTSummary summary = new MQTTSummary();
        summary.run_state = runState;
        summary.broker_uri = brokerURI;
        summary.subscribes.addAll(subscriptions);
        summary.publishes.add(COMMAND_TOPIC);
        summary.publishes.add(GW_STATUS_TOPIC);
        return summary;
    }


}
