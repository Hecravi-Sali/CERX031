'use strict';

function errorCallback(error) {
	console.log('navigator.getUserMedia error: ', error);
}

var url = "ws://localhost:8080/CCTV/websocket";

var webSocket = null;
if (webSocket == null) {
	webSocket = new WebSocket(url);
}

webSocket.onerror = function(event) {
	console.log(event.data);
};

webSocket.onopen = function(event) {
	console.log("Websocket is opened!");
	send();
};

window.onbeforeunload = function() {
	webSocket.onclose();
};

webSocket.onerror = function() {
	console.log("WebSocket connect get error");
	webSocket = new WebSocket(url);
};

webSocket.onclose = function() {
	console.log("WebSocket connect has been closed");
	webSocket = new WebSocket(url);
};

webSocket.binaryType = "arraybuffer";
webSocket.onmessage = function(event) {
	console.log("Get Message - " + event.data);
};

function send(){
	var text = '{"port_connectcore" : "port_coreopen", "port_ownername" : "admin"}';
	var obj = JSON.parse(text);
	console.log(obj);
	webSocket.send('{"port_connectcore" : "port_coreopen", "port_ownername" : "admin"}');
}
