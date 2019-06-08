/*
 * Copyright (C) 2018 Intel Corporation
 * SPDX-License-Identifier: BSD-3-Clause
 */

package com.intel.rfid.api.sensor;

public enum EnabledState {
    Disabled((byte) 0x00),
    Enabled((byte) 0x01),
    // to prevent having to deal with nulls
    UNKNOWN((byte) 0xFF);

    public final byte val;

    EnabledState(byte _val) { val = _val; }

}

