/*
 * Copyright (C) 2018 Intel Corporation
 * SPDX-License-Identifier: BSD-3-Clause
 */
package com.intel.rfid.api.data;

public enum DeviceAlertType {
    Unknown(0),
    RfModuleError(100),
    HighAmbientTemp(101),
    HighCpuTemp(102),
    HighCpuUsage(103),
    HighMemoryUsage(104),
    LowVoltageError(105),
    DeviceMoved(151);

    public final int id;

    DeviceAlertType(int _id) { id = _id; }

}
