'use strict';

function errorCallback(error) {
	console.log('navigator.getUserMedia error: ', error);
}

var url = "ws://192.168.1.100:8080/CCTV/websocket";

var webSocket = null;
var connectonline = false;
if (webSocket == null) {
	webSocket = new WebSocket(url);
	webSocket.binaryType = 'arraybuffer';
}

webSocket.onerror = function(event) {
	console.log(event.data);
};

webSocket.onopen = function(event) {
	connectonline = true;
	console.log("Websocket is opened!");
};

window.onbeforeunload = function() {
	webSocket.onclose();
};

webSocket.onerror = function() {
	connectonline = false;
	console.log("WebSocket connect get error");
};

webSocket.onclose = function() {
	webSocket.onclose = function () {};
	webSocket.close();
	connectonline = false;
	console.log("WebSocket connect has been closed");
};


var mode = 0;

var status = "Logon";
var onetimecode;
var _username = "halaoshi";
var _userpassword = "123"
	
var videoElement = document.querySelector("video");

var mediaSource = new MediaSource();
var arrayOfBlobs = [];
var sourceBuffer = null;

function appendToSourceBuffer()
{
	console.log("MEDIASOURECE",mediaSource.readyState);
	console.log("MEDIASOURECE",mediaSource.readyState === "open");

    if (mediaSource.readyState === "open" && sourceBuffer && sourceBuffer.updating === false) {
    	console.log("!!!!!!!!!!!!!!!!!!!! VIDOE MOVE!!!!!!!!!!!!!!");
    	var mediadata = arrayOfBlobs.shift();
    	if(mediadata != null){
        	console.log(mediadata);
            sourceBuffer.appendBuffer(mediadata);
    	}
    }
}

var countermedia = true;

webSocket.onmessage = function(event) {
	
	console.log("Get Message - " + event.data);
	
	switch(status) {
	case "Logon" : {
		WebSocketSendMessage(ServerRequest_Logonreq());
		status = "Logonconf";
		break;
	}
	case "Logonconf" : {
		WebSocketSendMessage(ServerRequest_Logon(_username, _userpassword));
		status = "ChangeCore";
		break;
	}
	case "ChangeCore" : {
		WebSocketSendMessage(CoreServerRequest_Coreclose());
		status = "ChangeCore1";
		break;
	}
	case "ChangeCore1" : {
		WebSocketSendMessage(CoreServerRequest_Coreopen("halaoshi"));
		status = "Login_1";
		break;
	}
	
	case "Login_1" : {
		WebSocketSendMessage(ServerRequect_Login(_username));
		console.log("Send to server");
		status = "Login_2";
		break;
	}
	case "Login_2" : {
		var fromserver = JSON.parse(event.data);
		if(fromserver.message){
			onetimecode = fromserver.onetimecode;			
			WebSocketSendMessage(ServerRequest_Verification(onetimecode, _username, _userpassword));
			status = "verificationsuccessful";
		}
		break;
	}
	case "verificationsuccessful" : {
		var fromserver = JSON.parse(event.data);
		if(fromserver.verification_status){
			if(mode == 0){
				if(SetVideoMaster()){
					status = "PullMedia";
				}
			}
			else {
				status = "ReadMedia";
				WebSocketSendMessage(GetVideoSlave());
				videoElement.src = URL.createObjectURL(mediaSource);
				
				mediaSource.addEventListener("sourceopen", function() {
					var mediaSource = this;
					console.log("!!!!!!!!!!!!!!!!!!!! VIDOE Open!!!!!!!!!!!!!!");
					sourceBuffer = mediaSource.addSourceBuffer('video/webm; codecs="vp8"');
					sourceBuffer.addEventListener("updateend", appendToSourceBuffer);
				});
			}
		}
		break;
	}
	case "PullMedia" : {
		
		break;
	}
	case "ReadMedia" : {
		var mediadata = JSON.parse(event.data).mediadata;
		
		if(mediadata != null){
			
			arrayOfBlobs.push(stringToUint8Array(atob(mediadata)));
	        appendToSourceBuffer();

		}        
		break;
	}
	default : {
		console.log("finish");
	}
	}
};

function stringToUint8Array(str){
	  var arr = [];
	  for (var i = 0, j = str.length; i < j; ++i) {
	    arr.push(str.charCodeAt(i));
	  }
	 
	  var tmpUint8Array = new Uint8Array(arr);
	  return tmpUint8Array
}

function GetVideoSlave(mediadata) {
	var server_request = JSON.parse("{}");
	server_request.server = "coreserver_startwatch";
	return CoreServerRequest(server_request);
}

function StartMedia() {
	WebSocketSendMessage(CoreServerRequest_Coreopen("admin"));
	status = "Logon";
	mode = 0;
}

function ReadMedia() {
	WebSocketSendMessage(CoreServerRequest_Coreopen("admin"));
	_username = "admin";
	_userpassword = "admin";
	status = "ChangeCore";
	mode = 1;
}

var mediaRecorder;
function SetVideoMaster() {
	var constraints = {
			audio : true,
			video : {width: 640, height: 480}
		};
	navigator.mediaDevices.getUserMedia(constraints)
	.then(function(stream) {
		
		console.log("JS Get MediaStream, Start Recording");
		
		videoElement.srcObject = stream;
		videoElement.onloadedmetadata = function(e) {
			videoElement.play();
		};

		var mediacode = {
				mimeType : 'video/webm;codecs="vp8"'
		};		
		console.log('Using ' + mediacode.mimeType);
		mediaRecorder = new MediaRecorder(stream, mediacode);
				
		mediaRecorder.ondataavailable = function(mediadata) {
			if(mediadata.data.size > 0 && connectonline){
				
				console.log("Data available...");
				
				console.log(mediadata.data);
				
				var reader = new FileReader();
				reader.addEventListener("loadend", function() {

					let buf = new Uint8Array(reader.result);
					try{						
						let oncodebuf = btoa(String.fromCharCode.apply(null, buf));
						//  var oncodebuf = btoa();

						var server_request = JSON.parse("{}");
						server_request.server = "coreserver_pushmedia";
						server_request.mediadata = oncodebuf;
						var toserver = CoreServerRequest(server_request);
						
						console.log(toserver);
						
						WebSocketSendMessage(toserver);
					} catch(InvalidCharacterError){
						console.log("???");
					}
				});
				reader.readAsArrayBuffer(mediadata.data);
			}
		};
		
		mediaRecorder.onerror = function(e) {
			console.log('Error: ', e);
		};
		mediaRecorder.onstart = function() {
			console.log('Started & state = ' + mediaRecorder.state);
		};
		mediaRecorder.onstop = function() {
			console.log('Stopped  & state = ' + mediaRecorder.state);
		};
		mediaRecorder.onpause = function() {
			console.log('Paused & state = ' + mediaRecorder.state);
		}
		mediaRecorder.onresume = function() {
			console.log('Resumed  & state = ' + mediaRecorder.state);
		}
		mediaRecorder.onwarning = function(e) {
			console.log('Warning: ' + e);
		};
		
		mediaRecorder.start(500);
	})
	.catch(function(err) {
		errorCallback(err.message);
		return false;
	});
	return true;
}

function WebSocketSendMessage(str) {
	if(str.length < 0) return;
	do{
		var sendstr;
		var endpacket = false;
		if(str.length > 1024) {
			sendstr = str.substring(0, 1024);
			str = str.substring(1024);
		} else {
			endpacket = true;
			sendstr = str;
			str = "";
		}
		var packet = JSON.parse("{}");
		packet.end = endpacket;
		packet.packetinfo = sendstr;		
		webSocket.send(JSON.stringify(packet));
	} while(str.length > 0)
}

function stringToUint8Array(str){
  var arr = [];
  for (var i = 0, j = str.length; i < j; ++i) {
    arr.push(str.charCodeAt(i));
  }
 
  var tmpUint8Array = new Uint8Array(arr);
  return tmpUint8Array;
}


function CoreServerRequest_Coreopen(corename) {
	var core_request = JSON.parse("{}");
	core_request.port_connectcore = "port_coreopen";
	core_request.port_ownername = corename;
	console.log(JSON.stringify(core_request));
	return JSON.stringify(core_request);
}

function CoreServerRequest_Coreclose() {
	var core_request = JSON.parse("{}");
	core_request.port_connectcore = "port_coreclose";
	console.log(JSON.stringify(core_request));
	return JSON.stringify(core_request);
}

function ServerRequest_Logonreq() {
	var server_request = JSON.parse("{}");
	server_request.server = "serverlogon";
	return CoreServerRequest(server_request);
}

function ServerRequest_Logon(username, password) {
	var logonmessage = JSON.parse("{}");
	logonmessage.username = username;
	logonmessage.userpassword = password;
	
	var server_request = JSON.parse("{}");
	server_request.server = "coreserver_logonconfirm";
	server_request.message = JSON.stringify(logonmessage);
	
	//  TODO RSA(server_request);
	return CoreServerRequest(server_request);
}

function ServerRequect_Login(username) {
	var server_request = JSON.parse("{}");
	server_request.server = "coreserver_login";
	server_request.username = username;
	return CoreServerRequest(server_request);
}

function ServerRequest_Verification(onetimecode, username, password) {
	var verificationcode = username + onetimecode + password;
	
	// TODO SHA1(verificationcode);
	
	var server_request = JSON.parse("{}");
	server_request.server = "coreserver_verification";
	server_request.message = verificationcode;
	return CoreServerRequest(server_request);
}

function CoreServerRequest(coremessage){
	var core_request = JSON.parse("{}");
	core_request.port_connectcore = "port_corestable";
	core_request.port_coremessage = JSON.stringify(coremessage);
	
	console.log(JSON.stringify(core_request));
	
	return JSON.stringify(core_request);
}

function getBrowser() {
	var nVer = navigator.appVersion;
	var nAgt = navigator.userAgent;
	var browserName = navigator.appName;
	var fullVersion = '' + parseFloat(navigator.appVersion);
	var majorVersion = parseInt(navigator.appVersion, 10);
	var nameOffset, verOffset, ix;

	// In Opera, the true version is after "Opera" or after "Version"
	if ((verOffset = nAgt.indexOf("Opera")) != -1) {
		browserName = "Opera";
		fullVersion = nAgt.substring(verOffset + 6);
		if ((verOffset = nAgt.indexOf("Version")) != -1)
			fullVersion = nAgt.substring(verOffset + 8);
	}
	// In MSIE, the true version is after "MSIE" in userAgent
	else if ((verOffset = nAgt.indexOf("MSIE")) != -1) {
		browserName = "Microsoft Internet Explorer";
		fullVersion = nAgt.substring(verOffset + 5);
	}
	// In Chrome, the true version is after "Chrome"
	else if ((verOffset = nAgt.indexOf("Chrome")) != -1) {
		browserName = "Chrome";
		fullVersion = nAgt.substring(verOffset + 7);
	}
	// In Safari, the true version is after "Safari" or after "Version"
	else if ((verOffset = nAgt.indexOf("Safari")) != -1) {
		browserName = "Safari";
		fullVersion = nAgt.substring(verOffset + 7);
		if ((verOffset = nAgt.indexOf("Version")) != -1)
			fullVersion = nAgt.substring(verOffset + 8);
	}
	// In Firefox, the true version is after "Firefox"
	else if ((verOffset = nAgt.indexOf("Firefox")) != -1) {
		browserName = "Firefox";
		fullVersion = nAgt.substring(verOffset + 8);
	}
	// In most other browsers, "name/version" is at the end of userAgent
	else if ((nameOffset = nAgt.lastIndexOf(' ') + 1) < (verOffset = nAgt.lastIndexOf('/'))) {
		browserName = nAgt.substring(nameOffset, verOffset);
		fullVersion = nAgt.substring(verOffset + 1);
		if (browserName.toLowerCase() == browserName.toUpperCase()) {
			browserName = navigator.appName;
		}
	}
	// trim the fullVersion string at semicolon/space if present
	if ((ix = fullVersion.indexOf(";")) != -1)
		fullVersion = fullVersion.substring(0, ix);
	if ((ix = fullVersion.indexOf(" ")) != -1)
		fullVersion = fullVersion.substring(0, ix);
	majorVersion = parseInt('' + fullVersion, 10);
	if (isNaN(majorVersion)) {
		fullVersion = '' + parseFloat(navigator.appVersion);
		majorVersion = parseInt(navigator.appVersion, 10);
	}
	return browserName;
}
