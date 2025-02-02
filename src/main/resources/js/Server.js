"use strict";

var server = {

    sendAsyncAjax: function(url, args, callback, owner) {
        var str = "";
        if (args) {
            for (var arg in args) {
                if (str.length > 0) str += "&";
                str += encodeURIComponent(arg) + "=" + encodeURIComponent(args[arg]);
            }
        }

        var debugInfo = "";
        if (config.DEBUG_AJAX) {
            debugInfo = "POST " + url + "(" + str + ")";
        }

        // create a request to the server
        var xmlHttp = new XMLHttpRequest();
        var serverResponse = new ServerResponse(xmlHttp, callback, owner, debugInfo);
        xmlHttp.onreadystatechange = serverResponse.onStateChange;

        try {
            xmlHttp.open("POST", url, true);
            xmlHttp.setRequestHeader('Content-Type', 'application/x-www-form-urlencoded');
            xmlHttp.send(str);

            if (config.DEBUG_AJAX) {
                console.log(debugInfo);
            }
        } catch (e) {
            alert("Can't connect to server:\n" + e.toString());
        }
    }
};
