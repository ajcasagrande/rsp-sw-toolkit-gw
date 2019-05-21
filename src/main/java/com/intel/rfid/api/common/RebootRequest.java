/*
 * Copyright (C) 2018 Intel Corporation
 * SPDX-License-Identifier: BSD-3-Clause
 */
package com.intel.rfid.api.common;

public class RebootRequest extends JsonRequest {

    public static final String METHOD_NAME = "reboot";

    public RebootRequest() { method = METHOD_NAME; }

}
