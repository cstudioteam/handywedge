$(function() {
	var url1 = "ws://52.194.238.124:8080/hw-pushnotice/Ws/pushnotice/token000000000000000000000000002";
	var url2 = "ws://localhost:8080/hw-pushnotice/Ws/pushnotice/token000000000000000000000000002";
	var ws = new WebSocket(url2);

	ws.onmessage = function(receive) {
		$("#message").text(receive.data);
	};

	ws.onopen = function() {
		ws.send();
//		ws.send("{\"token\": \"token000000000000000000000000002\"}");
		console.log("送信しました");
	};
});