<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">  
<html xmlns="http://www.w3.org/1999/xhtml">  
<head>  
<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />  
<title></title>  
</head>  
  </head>  
  <script type="text/javascript">  
  var socket;   
  //实际生产中，id可以从session里面拿用户id
  var id  = Math.random().toString(36).substr(2);
  if(!window.WebSocket){  
      window.WebSocket = window.MozWebSocket;  
  }  
   
  if(window.WebSocket){  
      socket = new WebSocket("ws://localhost:7397");  
        
      socket.onmessage = function(event){             
            appendln("receive:" + event.data);  
      };  
   
      socket.onopen = function(event){  
            appendln("WebSocket is opened");  
            login(); 
      };  
   
      socket.onclose = function(event){  
            appendln("WebSocket is closed");  
      };  
  }else{  
        alert("WebSocket is not support");  
  }  

    
  function appendln(text) {  
    var ta = document.getElementById('responseText');  
    ta.value += text + "\r\n";  
  }  
    
  function login(){
	  console.log("aaaaaa");
	  var date={"id":id,"type":"aa"};
	  var login = JSON.stringify(date);
	  socket.send(login);
	  
  
  }  
        
  </script>  
  <body>  
    <form onSubmit="return false;">  
        <input type = "text" name="message" value="hello"/>  
        <br/><br/>  
 
        <textarea id="responseText" style="width: 800px;height: 300px;"></textarea>  
    </form>  
  </body>  
</html> 