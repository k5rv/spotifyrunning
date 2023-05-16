// import {sendRequest} from "./http-client.js";

const BASE_URL = "https://suddenrun.com"
// const BASE_URL = "http://localhost:8082"
const API_V1_PLAYLISTS = "/api/v1/playlists"
const API_V1_USERS = "/api/v1/users"

function getUser() {
    console.log("Getting user")
    return sendRequest("GET", BASE_URL + API_V1_USERS).then((response) => {
        if (response.status === 404) {
            console.log("User not found")
            return null
        }
        if (response.status >= 200 && response.status < 300) {
            let user = JSON.parse(response.message)
            console.log("Found user [" + user.id + "]")
            return user
        }
        throw new Error("Unexpected HTTP status [" + response.status + "]")
    })
}

function registerUser() {
    console.log("Registering user")
    return sendRequest("POST", BASE_URL + API_V1_USERS).then((response) => {
        if (response.status === 409) {
            console.log("User already registered")
            return null
        }
        if (response.status >= 200 && response.status < 300) {
            let user = JSON.parse(response.message)
            console.log("Registered user [" + user.id + "]")
            return user
        }
        throw new Error("Unexpected HTTP status [" + response.status + "]")
    })
}

function getPlaylist() {
    console.log("Getting playlist")
    return sendRequest("GET", BASE_URL + API_V1_PLAYLISTS).then((response) => {
        if (response.status === 404) {
            console.log("Playlist doesn't exist")
            return null
        }
        if (response.status >= 200 && response.status < 300) {
            let playlist = JSON.parse(response.message)
            console.log("Found playlist [" + playlist.id + "]")
            return playlist
        }
        throw new Error("Unexpected HTTP status [" + response.status + "]")
    })
}

function createPlaylist() {
    console.log("Creating playlist")
    return sendRequest("POST", BASE_URL + API_V1_PLAYLISTS).then((response) => {
        if (response.status === 409) {
            console.log("Playlist already exist")
            return null
        }
        if (response.status >= 200 && response.status < 300) {
            let playlist = JSON.parse(response.message)
            console.log("Created playlist [" + playlist.id + "]")
            return playlist
        }
        throw new Error("Unexpected HTTP status [" + response.status + "]")
    })
}

function updatePlaylist() {
    console.log("Updating playlist")
    return sendRequest("PUT", BASE_URL + API_V1_PLAYLISTS).then((response) => {
        if (response.status === 404) {
            console.log("Playlist doesn't exist")
            return null
        }
        if (response.status >= 200 && response.status < 300) {
            let playlist = JSON.parse(response.message)
            console.log("Updated playlist [" + playlist.id + "]")
            return playlist
        }
        throw new Error("Unexpected HTTP status [" + response.status + "]")
    })
}

function sendRequest(method, url) {
    return new Promise(function (resolve, reject) {
        const xhr = new XMLHttpRequest();
        xhr.open(method, url);
        xhr.onload = function () {
            resolve({
                status: xhr.status,
                message: xhr.responseText
            });
        };
        xhr.onerror = function () {
            reject({
                status: xhr.status,
                message: xhr.responseText
            });
        };
        xhr.send();
    });
}

