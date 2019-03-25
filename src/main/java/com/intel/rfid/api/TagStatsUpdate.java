/*
 * Copyright (C) 2018 Intel Corporation
 * SPDX-License-Identifier: BSD-3-Clause
 */
package com.intel.rfid.api;

import com.intel.rfid.inventory.TagStats;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

public class TagStatsUpdate {

    public class Stat {
        public String device_id;
        public boolean is_location;
        public long last_read;
        public long n;
        public double mean;
        public double std_dev;
        public double min;
        public double max;
    }

    public Map<String, List<Stat>> epc_map = new HashMap<>();
    // this keeps track of all sensor ids in the update
    public Set<String> sensor_ids = new TreeSet<>();

    public TagStatsUpdate() { }

    public void addStat(String _epc, String _deviceId, boolean _isLocation, TagStats.Results _results) {
        Stat stat = new Stat();
        stat.device_id = _deviceId;
        stat.is_location = _isLocation;
        stat.last_read = _results.lastRead;
        stat.n = _results.n;
        stat.mean = _results.mean;
        stat.std_dev = _results.stdDev;
        stat.min = _results.min;
        stat.max = _results.max;

        epc_map.computeIfAbsent(_epc, k->new ArrayList<>()).add(stat);

        sensor_ids.add(_deviceId);
    }

}
