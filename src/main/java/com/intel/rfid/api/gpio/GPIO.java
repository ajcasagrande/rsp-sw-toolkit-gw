/*
 * Copyright (C) 2018 Intel Corporation
 * SPDX-License-Identifier: BSD-3-Clause
 */
package com.intel.rfid.api.gpio;

public class GPIO {

  public enum Direction {
      INPUT,
      OUTPUT;
  }

  public enum PinFunction {
      START_READING,
      STOP_READING,
      SENSOR_CONNECTED,
      SENSOR_DISCONNECTED,
      SENSOR_TRANSMITTING,
      SENSOR_READING_TAGS,
      NOT_ASSIGNED;
  }

  public enum State {
      ASSERTED,
      DEASSERTED,
      UNKNOWN;
  }
}
