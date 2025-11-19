// Simple WebSocket signaling server for WebRTC (open-source, no SaaS)
const WebSocket = require('ws');
const wss = new WebSocket.Server({ port: 3001 });

const rooms = {};

wss.on('connection', function connection(ws) {
  ws.on('message', function incoming(message) {
    let data;
    try {
      data = JSON.parse(message);
    } catch (e) {
      ws.send(JSON.stringify({ type: 'error', message: 'Invalid JSON' }));
      return;
    }
    const { type, room, payload } = data;
    if (!room) return;
    if (!rooms[room]) rooms[room] = [];
    if (!rooms[room].includes(ws)) rooms[room].push(ws);
    // Broadcast to all other clients in the room
    rooms[room].forEach(client => {
      if (client !== ws && client.readyState === WebSocket.OPEN) {
        client.send(JSON.stringify({ type, payload }));
      }
    });
  });
  ws.on('close', function() {
    Object.values(rooms).forEach(clients => {
      const idx = clients.indexOf(ws);
      if (idx !== -1) clients.splice(idx, 1);
    });
  });
});

console.log('WebRTC signaling server running on ws://localhost:3001');
