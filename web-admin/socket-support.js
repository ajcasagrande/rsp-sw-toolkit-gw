function wsGetUrl() {
    var u = document.URL;
    var p;

    // We open the websocket encrypted if this page came on an
    // https:// url itself, otherwise unencrypted
    if (u.substring(0, 5) == "https") {
        p = "wss://";
        u = u.substr(8);
    } else {
        p = "ws://";
        if (u.substring(0, 4) == "http") {
            u = u.substr(7);
        }
    }

    u = u.split('/');

    return p + u[0] + "/web-admin-socket";
}

// this function is a "class" definition 
// that wraps the actual web socket with
// additional configuration and management
// capabilities
function ControllerWebSocket() {

    var self = this;
    self.socket = null;
    self.state = "closed";
    self.interval = null;
    self.stateListeners = [];
    self.dataListeners = [];
    self.outputBinary = false;

    self.addStateListener = function (callback) {
        self.stateListeners.push(callback);
    };

    self.notifyStateListeners = function () {
        for (var i = 0; i < self.stateListeners.length; i++) {
            try {
                self.stateListeners[i](self.state);
            } catch (err) {
                console.log(err);
            }
        }
    };

    self.addDataListener = function (callback) {
        self.dataListeners.push(callback);
    };

    self.notifyDataListeners = function (data) {
        for (var i = 0; i < self.dataListeners.length; i++) {
            try {
                self.dataListeners[i](data);
            } catch (err) {
                console.log(err);
            }
        }
    };

    self.init = function () {
        // update the state listeners to the initial state first
        self.notifyStateListeners();
        self.checkOpen();
    };

    self.setOutputToBinaryArray = function () {
        self.outputBinary = true;
    };

    self.checkOpen = function () {
        if (self.socket != null) { return; }

        try {
            self.socket = new WebSocket(wsGetUrl());
        } catch (exception) {
            console.log(exception);
            return;
        }

        try {

            if (self.outputBinary) {
                self.socket.binaryType = "arraybuffer";
            }

            self.socket.onopen = function () {
                self.stopInterval();
                self.state = "open";
                self.notifyStateListeners();
                console.log("ws opened: ");
                if (self.outputBinary) {
                    console.log("ws binary output: ");
                }
                if (self.topics != null) {
                    self.sendJsonRequest("subscribe", self.topics);
                }
            };

            self.socket.onmessage = function (msg) {
                //self.notifyDataListeners(msg.data);

                var rpc = JSON.parse(msg.data);
                if (typeof rpc.id !== 'undefined') {
                    if (typeof rpc["method"] !== 'undefined') {
                        self.inboundRequest(rpc);
                    } else {
                        if (self.pendingRequests[rpc.id] !== 'undefined') {
                            self.inboundResponse(rpc, self.pendingRequests[rpc.id].request);
                            delete self.pendingRequests[rpc.id];
                        } else {
                            console.log("missing corresponding request for response " + rpc.id);
                        }
                    }
                } else {
                    self.inboundNotification(rpc);
                }
            };

            self.socket.onclose = function () {
                // onclose gets called on connect failure
                // even if the socket was not opened
                if (self.state === "open") {
                    console.log("ws closed: ");
                    self.state = "closed";
                    self.notifyStateListeners();
                }
                delete self.socket;
                self.socket = null;
                self.startInterval();
            };

        } catch (exception) {
            alert('<p>Error' + exception);
        }

    };

    self.inboundRequest = function (jsonReq) {
        console.log("unhandled inboundRequest" + JSON.stringify(jsonReq));
    };

    self.inboundResponse = function (jsonRsp, jsonReq) {
        console.log("unhandled inboundResponse " + JSON.stringify(jsonRsp));
    };

    self.inboundNotification = function (jsonNot) {
        console.log("unhandled inboundNotification" + JSON.stringify(jsonNot));
    };


    self.startInterval = function () {
        if (self.interval == null) {
            self.interval = setInterval(self.checkOpen, 5000);
        }
    };

    self.stopInterval = function () {
        if (self.interval != null) {
            clearInterval(self.interval);
            self.interval = null;
        }
    };

    self.isOpen = function () {
        return self.state = "open";
    };

    self.send = function (message) {
        if (self.socket == null) {
            return false;
        }
        self.socket.send(message);
        return true;
    };

    self.topics = [];
    self.reqId = 0;
    self.pendingRequests = {};

    self.sendJsonRequest = function (method, params) {
        var req = {};
        req.jsonrpc = "2.0";
        req.id = self.reqId++;
        req.method = method;
        req.params = params;

        self.pendingRequests[req.id] = {request: req};
        //alert("send: " + JSON.stringify(req));
        self.send(JSON.stringify(req));

        return req.id;
    };

    self.sendPassword = function (password) {
        self.send("password " + password + "\n");
    };

}

