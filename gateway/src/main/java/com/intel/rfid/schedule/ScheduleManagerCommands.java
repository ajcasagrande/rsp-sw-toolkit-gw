/*
 * Copyright (C) 2018 Intel Corporation
 * SPDX-License-Identifier: BSD-3-Clause
 */
package com.intel.rfid.schedule;

import com.intel.rfid.console.ArgumentIterator;
import com.intel.rfid.exception.GatewayException;
import com.intel.rfid.helpers.PrettyPrinter;
import jline.console.completer.AggregateCompleter;
import jline.console.completer.ArgumentCompleter;
import jline.console.completer.Completer;
import jline.console.completer.FileNameCompleter;
import jline.console.completer.NullCompleter;
import jline.console.completer.StringsCompleter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import static com.intel.rfid.console.CLICommander.INFO;
import static com.intel.rfid.console.CLICommander.SHOW;
import static com.intel.rfid.console.CLICommander.Support;

public class ScheduleManagerCommands implements Support {

    protected Logger log = LoggerFactory.getLogger(getClass());

    private ScheduleManager scheduleMgr;

    public ScheduleManagerCommands(ScheduleManager _scheduleMgr) {
        scheduleMgr = _scheduleMgr;
    }

    public static final String CMD_ID = "scheduler";

    public static final String ACTIVATE_ALL_ON = "activate.all.on";
    public static final String ACTIVATE_ALL_SEQ = "activate.all.sequenced";
    public static final String ACTIVATE_FROM_CFG = "activate.from.config.file";
    public static final String DEACTIVATE = "deactivate";

    @Override
    public String getCommandId() {
        return CMD_ID;
    }

    @Override
    public void getCompleters(List<Completer> _comps) {

        _comps.add(
            new AggregateCompleter(
                new ArgumentCompleter(
                    new StringsCompleter(CMD_ID),
                    new StringsCompleter(SHOW,
                                         ACTIVATE_ALL_ON,
                                         ACTIVATE_ALL_SEQ,
                                         DEACTIVATE),
                    new NullCompleter()
                ),
                new ArgumentCompleter(
                    new StringsCompleter(CMD_ID),
                    new StringsCompleter(ACTIVATE_FROM_CFG),
                    new FileNameCompleter(),
                    new NullCompleter()
                )
            )
                  );

    }

    public void usage(PrettyPrinter _out) {
        _out.indent(0, "USAGE");
        _out.blank();
        _out.indent(0, "> " + CMD_ID + " " + INFO + " <topic>");
        _out.indent(1, "Displays info about the topic");
        _out.blank();
        _out.indent(0, "> " + CMD_ID + " " + SHOW);
        _out.indent(1, "Displays info about current state of the schedule manager");
        _out.blank();
        _out.indent(0, "> " + CMD_ID + " " + ACTIVATE_ALL_ON);
        _out.indent(1, "Transitions to all sensors reading tags at the same time");
        _out.blank();
        _out.indent(0, "> " + CMD_ID + " " + ACTIVATE_ALL_SEQ);
        _out.indent(1, "Transitions to each sensor reading tags one at a time");
        _out.blank();
        _out.indent(0, "> " + CMD_ID + " " + ACTIVATE_FROM_CFG + " <file_path>");
        _out.indent(1, "Load a schedule from the specified JSON file path");
        _out.blank();
        _out.indent(0, "> " + CMD_ID + " " + DEACTIVATE);
        _out.indent(1, "Deactivates any scheduling activities and causes the sensors to stop reading");
        _out.blank();
    }

    @Override
    public void doAction(String _action, ArgumentIterator _argIter, PrettyPrinter _out)
        throws IOException, GatewayException {

        switch (_action) {

            case ACTIVATE_ALL_ON:
            case ACTIVATE_ALL_SEQ:
                doActivate(_action, _out);
                break;
            case ACTIVATE_FROM_CFG:
                doActivateFromConfig(_argIter, _out);
                break;
            case DEACTIVATE:
                doDeactivate(_out);
                break;
            case SHOW:
                scheduleMgr.show(_out);
                break;
            default:
                usage(_out);
        }
    }

    private void doActivate(String _action, PrettyPrinter _out)
        throws IOException, GatewayException {

        switch (_action) {
            case ACTIVATE_ALL_ON:
                scheduleMgr.activate(ScheduleManager.RunState.ALL_ON, null);
                break;
            case ACTIVATE_ALL_SEQ:
                scheduleMgr.activate(ScheduleManager.RunState.ALL_SEQUENCED, null);
                break;
        }
        _out.line("completed");
    }

    private void doActivateFromConfig(ArgumentIterator _argIter, PrettyPrinter _out)
        throws IOException, GatewayException {


        Path p = Paths.get(new File(_argIter.next()).getCanonicalPath());

        scheduleMgr.activate(ScheduleManager.RunState.FROM_CONFIG, p);
        _out.line("completed");
    }

    private void doDeactivate(PrettyPrinter _out) {
        scheduleMgr.deactivate();
        _out.line("completed");
    }


}
