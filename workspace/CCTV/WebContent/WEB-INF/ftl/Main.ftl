<!DOCTYPE html>
<html>
    <head>
        <title>
            Media Recorder API Demo
        </title>
    </head>
    <body>
        <div id = "TITTLE">
        <div style = "text-align:center;">
            <h1>Closed-Circuit Television base on Network</h1>
            <h2>CCTV-N</h2>
        </div>
        
        <div id="container">
        <div style = "text-align:center;">
            
            <h1 id = "connectwho">
                You are connecting DefaultCore now
            </h1>
            
            <input type = "button" id = "change" value = "registered"/>
            <br>
            <div>
                <div>
                    <input type = "text" id = "username"/>
                    <label id = "usernamelabel"> username </label>
                </div>
                <div>
                    <input type = "text" id = "password"/>
                    <label id = "passwordlabel"> password </label>
                </div>
            </div>
            
            <div>
                <input type = "text" id = "corename" style = "display : none"/>
                <label id = "corenamelabel" style = "display : none"> corename </label>
            </div>
            
            <input type = "button" id = "logon" value = "LOGON"/>
            <input type = "button" id = "login" value = "LOGIN"/>
            <input type = "button" id = "changesumit" value = "CHANGE" style = "display : none"/>
            
            
            <br>
            <video controls autoplay>
            </video>
            <br>
            
            <button id="rec" onclick="onBtnRecordClicked()">
                Record
            </button>
        </div>
        <a id="downloadLink" download="mediarecorder.webm" name="mediarecorder.webm" href></a>
        <p id="data"></p>
        <script src="Javascript/Main.js"></script>
    </body>
</html>
