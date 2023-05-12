const BASE_URL = "http://localhost:8082"
// const BASE_URL = "https://suddenrun.com"
const PLAYLIST = "/api/v1/playlists"
const USER = "/api/v1/users"


function sendAsyncHttpRequest(httpMethod, url, callback) {
    const xmlHttp = new XMLHttpRequest();
    xmlHttp.onreadystatechange = function () {
        if (xmlHttp.readyState === 4 && xmlHttp.status === 200) {
            console.log("client received HTTP status: " + xmlHttp.status)
            callback(xmlHttp.responseText)
        }
        if (xmlHttp.readyState === 4 && xmlHttp.status === 404) {
            console.log("client received HTTP status: " + xmlHttp.status)
            callback(null)
        }
    }
    xmlHttp.open(httpMethod, url, true);
    xmlHttp.send(null);
}

function registerUser() {
    const request = sendRequest("POST", BASE_URL + USER, null);
    if (request.status === 200) {
        return JSON.parse(request.responseText);
    }
    return null;
}

function getUser() {
    const request = sendRequest("GET", BASE_URL + USER, null);
    if (request.status === 200) {
        return JSON.parse(request.responseText);
    }
    return null;
}

function createPlaylist() {
    const request = sendRequest("POST", BASE_URL + PLAYLIST, null);
    if (request.status === 200) {
        return JSON.parse(request.responseText);
    }
    return null;
}

function getPlaylist() {
    const request = sendRequest("GET", BASE_URL + PLAYLIST, null);
    if (request.status === 200) {
        return JSON.parse(request.responseText);
    }
    return null;
}

function updatePlaylist() {
    const request = sendRequest("PUT", BASE_URL + PLAYLIST, null);
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