function registerUser() {
    const request = sendRequest('POST', 'http://localhost:8082/api/v1/users', null);
    if (request.status === 200) {
        return JSON.parse(request.responseText);
    }
    return null;
}

function getUser() {
    const request = sendRequest('GET', 'http://localhost:8082/api/v1/users', null);
    if (request.status === 200) {
        return JSON.parse(request.responseText);
    }
    return null;
}

function createPlaylist() {
    const request = sendRequest('POST', 'http://localhost:8082/api/v1/playlists', null);
    if (request.status === 200) {
        return JSON.parse(request.responseText);
    }
    return null;
}

function getPlaylist() {
    const request = sendRequest('GET', 'http://localhost:8082/api/v1/playlists', null);
    if (request.status === 200) {
        return JSON.parse(request.responseText);
    }
    return null;
}

function updatePlaylist() {
    const request = sendRequest('PUT', 'http://localhost:8082/api/v1/playlists', null);
    if (request.status === 200) {
        return JSON.parse(request.responseText);
    }
    return null;
}

function sendRequest(type, url, body) {
    const request = new XMLHttpRequest();
    request.open(type, url, false);
    request.send(body);
    return request;
}