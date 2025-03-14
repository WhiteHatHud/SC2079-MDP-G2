/**
 * CustomError class extends the standard Error class to include additional content and 
 * a method to set the error message.
 */
export class CustomError extends Error {
    content;

    /**
     * Constructor for CustomError.
     * 
     * @param {Response} content - The response object from a failed fetch request.
     */
    constructor(content) {
        super();
        this.content = content;
    }

    /**
     * Sets the error message.
     * 
     * @param {string} s - The error message string.
     */
    msg(s) {
        this.message = s;
    }
}

/**
 * Enum-like object to define HTTP method types for ease of use in requests.
 */
export var methodType = { get: 'GET', post: 'POST', put: 'PUT', delete: 'DELETE' };

/**
 * BaseAPI class provides a static method for making JSON-based HTTP requests 
 * and handling responses and errors. This class is designed to interact with 
 * a FastAPI backend.
 */
export default class BaseAPI {	

    /**
     * Makes an HTTP request to a specified API endpoint and handles the response.
     * 
     * @param {string} api - The API endpoint (relative to the host).
     * @param {string} method - The HTTP method to use (GET, POST, PUT, DELETE).
     * @param {Object} headers - Additional headers to include in the request.
     * @param {Object} options - Additional fetch options (e.g., credentials, mode).
     * @param {Object} content - The JSON content to send in the body of the request (for POST and PUT).
     * 
     * @returns {Promise} - A promise that resolves with the response data or rejects with an error.
     */
    static JSONRequest(api, method, headers = {}, options = {}, content = null) {
        const host = "http://localhost:8000"; // Base URL for local development
        // const host = "http://10.91.242.113:8000"; 

        // Setup request options, including method, headers, and any additional options
        let requestOptions = {
            method: method,
            headers: { ...headers, 'Content-Type': 'application/json' },
            ...options
        };

        // If the method is POST or PUT, include the content in the request body as JSON
        if (method === methodType.post || method === methodType.put) {
            requestOptions.body = JSON.stringify(content);
        }

        // Return a promise that handles the fetch request and response processing
        return new Promise((resolve, reject) => {
            fetch(host + api, requestOptions)
                .then(response => {
                    if (!response.ok) { 
                        // If the response is not OK, throw a CustomError with the response content
                        throw new CustomError(response);
                    }

                    // Attempt to parse the JSON response
                    response.json()
                        .then(res => {
                            // If the response contains an error, reject the promise
                            if (res.detail || res.error) {  // FastAPI usually uses `detail` for errors
                                reject(JSON.stringify(res.detail || res.error));
                            } else {
                                // Otherwise, resolve the promise with the response data
                                resolve(res);  // Assuming FastAPI returns data directly
                            }
                        })
                        .catch(err => {
                            // If parsing fails, resolve with an empty object
                            resolve({});
                        });
                })
                .catch(async (err) => {
                    console.log(err);
                    if (err instanceof CustomError) {
                        // Handle the custom error, attempt to extract the error message from the JSON response
                        let errStr = await err.content.json()
                            .then(res => {
                                if (res.detail || res.errors) {
                                    return JSON.stringify(res.detail || res.errors);
                                }
                                return "";
                            })
                            .catch(() => {
                                return "";
                            });

                        // Set the error message in the CustomError instance and reject the promise
                        err.msg(errStr);
                        reject(err);
                    } else {
                        // If it's a different type of error, reject the promise with the error
                        reject(err);
                    }
                });
        });
    }
}
