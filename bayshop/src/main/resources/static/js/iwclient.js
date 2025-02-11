/**
 * WebSocket API, which only works once initialized
 */
const ws = {		

	/**
	 * Number of retries if connection fails
	 */
	retries: 3,
		
	/**
	 * Default action when message is received. 
	 */
	receive: (messageObj) => {
		console.log(messageObj);

		if(messageObj.type && messageObj.type == "message"){
			// if(_ws_notifyMessage && typeof _ws_notifyMessage === 'function'){
			// 	_ws_notifyMessage(messageObj); // BayShop.js
			// }
			var _table = $('body.messages table.all-user-messages tbody');

			if(_table.length > 0){
				var alert = $(`<div class="slide-alert success">Has recibido un mensaje</div>`);
				$('body').append(alert);
				$(alert).animate({ opacity : '0' }, 3000, null, function(){ $(alert).remove(); });

				_table.prepend(`
				<tr class="msg" data-id="${messageObj.id}" data-type="inbox">
					<td>${messageObj.sender}</td>
					<td>${messageObj.receiver}</td>
					<td>${messageObj.date}</td>
					<td>No</td>
					<td>${messageObj.msg}</td>
					<td>
						<div class="btn btn-light view-message"><i class="fa fa-eye"></i></div>
						<div class="btn btn-light delete-message"><i class="fa fa-trash"></i></div>
					</td>
				</tr>
				`);
			}
		}
	},
	
	headers: {'X-CSRF-TOKEN' : config.csrf.value},
	
	/**
	 * Attempts to establish communication with the specified
	 * web-socket endpoint. If successfull, will call 
	 */
	initialize: (endpoint, subs = []) => {
		try {
			ws.stompClient = Stomp.client(endpoint);
			ws.stompClient.reconnect_delay = 2000;
			// only works on modified stomp.js, not on original from mantainer's site
			ws.stompClient.reconnect_callback = () => ws.retries -- > 0;
			ws.stompClient.connect(ws.headers, () => {
		        ws.connected = true;
		        console.log('Connected to ', endpoint, ' - subscribing...');		        
		        while (subs.length != 0) {
		        	ws.subscribe(subs.pop())
		        }
		    });			
			console.log("Connected to WS '" + endpoint + "'")
		} catch (e) {
			console.log("Error, connection to WS '" + endpoint + "' FAILED: ", e);
		}
	},
	
	subscribe: (sub) => {
        try {
	        ws.stompClient.subscribe(sub, 
	        		(m) => ws.receive(JSON.parse(m.body))); 	// falla si no recibe JSON!
        	console.log("Hopefully subscribed to " + sub);
        } catch (e) {
        	console.log("Error, could not subscribe to " + sub);
        }
	}
} 

/**
 * Sends an ajax request using fetch
 */
//envía json, espera json de vuelta; lanza error si status != 200
function go(url, method, data = {}) {
  let params = {
    method: method, // POST, GET, POST, PUT, DELETE, etc.
    headers: {
      "Content-Type": "application/json; charset=utf-8",
    },
    body: JSON.stringify(data)
  };
  if (method === "GET") {
	  delete params.body;
  } else {
      params.headers["X-CSRF-TOKEN"] = config.csrf.value; 
  }  
  console.log("sending", url, params)
  return fetch(url, params)
  	.then(response => {
	    if (response.ok) {
	        return response.json(); // esto lo recibes con then(d => ...)
	    } else {
	    	throw response.text();  // esto lo recibes con catch(d => ...)
	    }
  	})
}

/**
 * Actions to perform once the page is fully loaded
 */
document.addEventListener("DOMContentLoaded", () => {
	if (config.socketUrl) {
		let subs = config.admin ? 
				["/topic/admin", "/user/queue/updates"] : ["/user/queue/updates"]
		ws.initialize(config.socketUrl, subs);
	}
	
	// add your after-page-loaded JS code here; or even better, call 
	// 	 document.addEventListener("DOMContentLoaded", () => { /* your-code-here */ });
	//   (assuming you do not care about order-of-execution, all such handlers will be called correctly)
});
