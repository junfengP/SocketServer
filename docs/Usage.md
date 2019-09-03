# SocketServer - Java and C# Communication
Socket Server Address: 127.0.0.1:5656 (default port: 5656)

Sending String to the Socket Server, there are 2 command String. 
Other String will be ignored.

## Commands
1. register type.

~~**old method**(Not recommend)~~
```
{
    "type": value_str
}
```
- value_str : value_str can be "machine", "agent", "rfid"
	
**new method** (recommend)

client
```
{
    "action": "register",
    "value": value_str
}
```
- value_str : value_str can be "machine", "agent", "rfid"
              
             
server
```
{
    "result": "success", // "failed"
    "info": info_str
}
```
- result: should be "success" or "failed"
- info_str: extra information to tell the result of success or failed. Usually empty string ("").

2. send message to right place.
```
{
	"to": dest_str,
	"message": msg			
}
```
- dest_str : dest_str means the place your message will go. It can be "machine", "agent", "rfid".    
- msg : msg is the information you want to send.


3. check agent or machine is online

client
```
{
    "action": "check",
    "value": value_str
}
```
- value_str : value_str can be "machine", "agent", "rfid"
              

server
```
{
    "result": "success", // "failed"
    "info": info_str
}
```
- result: should be "success" or "failed", it means parsing the cmd success.
- info_str: extra information to tell the result of success or failed. Usually empty string "".
            In this case info_str should be "online" or "offline".
            



## Examples
1. register as Agent

old method
~~(not recommend)~~
```
{
    "type": "agent"
}
```

**new method**

client
```
{
    "action": "register",
    "value": "agent"
}
```

server
```
{
    "result": "success"
    "info": ""
}
```


2. register as Machine
```
{
    "type": "machine"
}
```

3. Machine sends a message to Agent
```
{
    "to": "agent", 
    "message": {"size1":100, "size2":210}  
}
```

4. check machine is online or not.

client
```
{
    "action": "check",
    "value": "machine"
}
```

server
```
{
    "result": "success",
    "info": "online"
}
```

## Warnning
1. Make sure your message contains a "\n" at the end. 
   Otherwise, your message wont be accepted until you send a "\n".
2. When your connect to the socket server, the first thing is to register the type. 
   Otherwise, all information will be ignored when the destination is not register yet.
3. Do not disconnect the server until your close your program.
4. All letters are lower case.
5. The message from Socket Server will be end with "\n" . 







