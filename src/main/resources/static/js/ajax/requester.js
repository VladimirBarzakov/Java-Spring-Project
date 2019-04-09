/* 
 * Atia & Tiger technology 2019.
 */
let requester = (() => {

    function getCSRFToken() {
        let csrfValue = $($("input[name='_csrf']")[0]).attr('value');
        return csrfValue;
    }
    
    function getCSRFTheader() {
        let header = $('#_csrf_header').attr('content');
        return header;
    }

// Creates request object to kinvey
    function makeRequest(method, targeUrl, successFunc, errorFunc) {
        return req = {
            method,
            url: targeUrl,
            beforeSend: function (xhr) {
                xhr.setRequestHeader(getCSRFTheader(), getCSRFToken());
            },
            success: function (data) {
                successFunc(data);
            },
            error: function (data) {
                errorFunc(data);
            }
        };
    }

// Function to return GET promise
    function get(targetUrl, successFunc, errorFunc) {
        return $.ajax(makeRequest('GET', targetUrl, successFunc, errorFunc));
    }

// Function to return POST promise
    function post(targetUrl, successFunc, errorFunc, data) {
        let req = makeRequest('POST', targetUrl, successFunc, errorFunc);
        req.data = data;
        return $.ajax(req);
    }

// Function to return PUT promise
    function update(targetUrl, successFunc, errorFunc, data) {
        let req = makeRequest('PUT', targetUrl, successFunc, errorFunc);
        req.data = data;
        return $.ajax(req);
    }

// Function to return DELETE promise
    function remove(targetUrl, successFunc, errorFunc) {
        return $.ajax(makeRequest('DELETE', targetUrl, successFunc, errorFunc));
    }

    return {
        get,
        post,
        update,
        remove
    };
})();
