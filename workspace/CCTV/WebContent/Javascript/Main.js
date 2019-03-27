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
	status = 'A';
	webSocket.send(CoreRequest_Coreopen("admin"));
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

var status = 'A';
var onetimecode;

webSocket.binaryType = "arraybuffer";
webSocket.onmessage = function(event) {
	
	console.log("Get Message - " + event.data);
	
	switch(status) {
	case 'A' : {
		webSocket.send(ServerRequect_Login("admin"));
		console.log("Send to server");
		status = 'B';
		break;
	}
	case 'B' : {
		var fromserver = JSON.parse(event.data);
		if(fromserver.message){
			onetimecode = fromserver.onetimecode;			
			webSocket.send(ServerRequest_Verification(onetimecode, "admin", "admin"));
			status = 'C';
		}
		break;
	}
	case 'C' : {
		var fromserver = JSON.parse(event.data);
		if(fromserver.verification_status){
			
			status = 'D';
		}
		break;
	}
	
	
	default : {
		console.log("finish");
	}
	}
};


function onBtnRecordClicked() {
	var a = document.getElementById("connectwho");
	a.innerHTML = 'What you are connecting now is ';
	a.innerHTML = a.innerHTML + 'Default';
}

var button0_changeflage = true;
var button0 = document.getElementById("change");
button0.onclick = function Changexx() {
	if(button0_changeflage) {
		this.value = 'changeconnect';
		document.getElementById("username").style.display = "none";
		document.getElementById("usernamelabel").style.display = "none";
		document.getElementById("password").style.display = "none";
		document.getElementById("passwordlabel").style.display = "none";
		document.getElementById("corename").style.display = "inline-block";
		document.getElementById("corenamelabel").style.display = "inline-block";
		document.getElementById("logon").style.display = "none";
		document.getElementById("login").style.display = "none";
		document.getElementById("changesumit").style.display = "inline-block";
		button0_changeflage = false;
	}
	else {
		this.value = 'registered';
		document.getElementById("username").style.display = "inline-block";
		document.getElementById("usernamelabel").style.display = "inline-block";
		document.getElementById("password").style.display = "inline-block";
		document.getElementById("passwordlabel").style.display = "inline-block";
		document.getElementById("corename").style.display = "none";
		document.getElementById("corenamelabel").style.display = "none";
		document.getElementById("logon").style.display = "inline-block";
		document.getElementById("login").style.display = "inline-block";
		document.getElementById("changesumit").style.display = "none";
		button0_changeflage = true;
	}
}

function CoreRequest_Coreopen(corename) {
	var core_request = JSON.parse("{}");
	core_request.port_connectcore = "port_coreopen";
	core_request.port_ownername = corename;
	console.log(JSON.stringify(core_request));
	return JSON.stringify(core_request);
}

function ServerRequect_Login(username) {
	var server_request = JSON.parse("{}");
	server_request.server = "coreserver_login";
	server_request.username = username;
	
	var core_request = JSON.parse("{}");
	core_request.port_connectcore = "port_corestable";
	core_request.port_coremessage = JSON.stringify(server_request);
	console.log(JSON.stringify(core_request));
	return JSON.stringify(core_request);
}

function ServerRequest_Verification(onetimecode, username, password) {
	var verificationcode = username + onetimecode + password;
	//  SHA1(verificationcode);
	
	var server_request = JSON.parse("{}");
	server_request.server = "coreserver_verification";
	server_request.message = verificationcode;
	
	var core_request = JSON.parse("{}");
	core_request.port_connectcore = "port_corestable";
	core_request.port_coremessage = JSON.stringify(server_request);
	console.log(JSON.stringify(core_request));
	return JSON.stringify(core_request);
}

