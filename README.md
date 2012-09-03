# mockster

A simple, configurable data backend mock.

## Usage

Post your configuration to /configure-mockster, using the following syntax:
- Use query parameters `uri` and `method` to configure the request that should be responded to.
- Provide a JSON representation of the ring response map as request body.

### Example

1. `lein ring server-headless`
2. `curl -X POST -H "Content-Type: application/json" 
   -d '{"status": 200, "body": [{"id": 1}, {"id": 2}, {"id": 3}]}' 
   "localhost:3000/configure-mockster?uri=/api/things&method=get"`
3. `curl http://localhost:3000/api/things`

## License

Copyright © 2012 Nils Wloka
Distributed under the Eclipse Public License, the same as Clojure.
