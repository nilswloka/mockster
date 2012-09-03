# mockster

A simple, configurable data backend mock.

## Usage

Create a new mock response by sending a POST request with your configuration to `/mockster-responses`, using the following syntax:
- Use query parameters `uri` and `method` to configure the request that should be responded to.
- Provide a JSON representation of the ring response map as request body.

Consecutive calls with the same URI and method can be responded to by sending multiple configuration requests. Responses will be sent in FIFO order.

Remove all mock responses for a given uri and method by sending a DELETE request to `/mockster-responses`, using the following syntax:
- Use query parameters `uri` and `method` to identify the responses that should no longer be responded to.

### Example

1. `lein ring server-headless`
2. `curl -X POST -H "Content-Type: application/json"      
   -d '{"status": 200, "body": [{"id": 1}, {"id": 2}, {"id": 3}]}'     
   "localhost:3000/mockster-responses?uri=/api/things&method=get"`
3. `curl http://localhost:3000/api/things`

## License

Copyright © 2012 Nils Wloka
Distributed under the Eclipse Public License, the same as Clojure.
