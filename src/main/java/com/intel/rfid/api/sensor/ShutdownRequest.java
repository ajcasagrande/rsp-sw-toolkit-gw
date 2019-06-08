/*
 * Copyright (C) 2018 Intel Corporation
 * SPDX-License-Identifier: BSD-3-Clause
 */

package com.intel.rfid.api.sensor;

import com.intel.rfid.api.JsonRequest;

public class ShutdownRequest extends JsonRequest {

    public static final String METHOD_NAME = "shutdown";

    public ShutdownRequest() { method = METHOD_NAME; }

}
