/*
 * Copyright (C) 2018 Intel Corporation
 * SPDX-License-Identifier: BSD-3-Clause
 */
package com.intel.rfid.api.upstream;

import com.intel.rfid.api.JsonNotification;

public class InventoryReadRateNotification extends JsonNotification {

    public static final String METHOD_NAME = "inventory_read_rate_per_second";

    public InventoryReadRateNotification() {
        method = METHOD_NAME;
    }

    public InventoryReadRateNotification(long _readRatePerSecond) {
        this();
        params.read_rate_per_second = _readRatePerSecond;
    }

    public Params params = new Params();

    public class Params {
        public long read_rate_per_second;
    }
}
