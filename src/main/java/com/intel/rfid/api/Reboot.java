/*
 * Copyright (C) 2018 Intel Corporation
 * SPDX-License-Identifier: BSD-3-Clause
 */
package com.intel.rfid.api;

public class Reboot extends JsonRequest {

    public static final String METHOD_NAME = "reboot";

    public Reboot() { method = METHOD_NAME; }

}
