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
            
            
            <input type = "button" id = "Test Push Media" value = "LOGON" onclick = "StartMedia()"/>
            <input type = "button" id = "Test Pull Media" value = "LOGIN" onclick = "ReadMedia()"/>            
            
            <br>
            <video controls autoplay width="640" height="480" autoplay>
            </video>
            <br>
        </div>
        <p id="data"></p>
        <script src="Javascript/Main.js"></script>
    </body>
</html>
