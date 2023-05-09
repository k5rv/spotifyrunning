const BASE_URL = "http://localhost:8082"
const PLAYLIST_API = "/api/v1/playlists"
const USER_API = "/api/v1/users"


function registerUser() {
    const request = sendRequest("POST", BASE_URL + USER_API, null);
    if (request.status === 200) {
        return JSON.parse(request.responseText);
    }
    return null;
}

function getUser() {
    const request = sendRequest("GET", BASE_URL + USER_API, null);
    if (request.status === 200) {
        return JSON.parse(request.responseText);
    }
    return null;
}

function createPlaylist() {
    const request = sendRequest("POST", BASE_URL + PLAYLIST_API, null);
    if (request.status === 200) {
        return JSON.parse(request.responseText);
    }
    return null;
}

function getPlaylist() {
    const request = sendRequest("GET", BASE_URL + PLAYLIST_API, null);
    if (request.status === 200) {
        return JSON.parse(request.responseText);
    }
    return null;
}

function updatePlaylist() {
    const request = sendRequest("PUT", BASE_URL + PLAYLIST_API, null);
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