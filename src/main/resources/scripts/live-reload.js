const socketUrl = 'ws://localhost:8080/alive';

const socket = new WebSocket(socketUrl);

socket.addEventListener('open',
    () => {
        socket.send("[refresh-on-reload client] client wants to connect")
        console.info("[refresh-on-reload client] WS-connection established with server");
    }
)

socket.addEventListener('message', (e) => console.log(e.data))

socket.addEventListener('close', (e) => refreshOrTimeOut(e));

async function refreshOrTimeOut(e) {
    console.info("[refresh-on-reload client] connection closed - reconnecting");
    const msBetweenAttempts = 500;
    const msUntilTimeout = 10000;

    // this bit is really important: the ktor-server will crash/not fully reload if it receives a request
    // during reloading. we therefore HAVE to wait a little for the server to be reachable again.
    await sleep(500)

    const probingIntervalId = setInterval(() => {
        probeServer(socketUrl);
    }, msBetweenAttempts);

    // should this even time out?
    // setTimeout(() => {
    //     console.error("[refresh-on-reload client] too many attempts - closing WS-connection");
    //     socket.close()
    //     clearInterval(probingIntervalId);
    // }, msUntilTimeout)
}

function probeServer(socketUrl) {
    let socket = new WebSocket(socketUrl);

    socket.addEventListener('error', function () {
        console.info("[refresh-on-reload client] error on reconnection - retrying");
        socket.close();
        socket = null;
    });

    socket.addEventListener('open', () => {
        console.info("[refresh-on-reload client] connection re-established - refreshing page and closing")
        socket.close();
        socket = null;
        location.reload();
    });
}

function sleep(ms) {
    return new Promise(resolve => setTimeout(resolve, ms));
}

// setInterval(() => location.reload(), 120000);
export {};

