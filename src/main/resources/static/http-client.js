export function sendRequest(method, url) {
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
