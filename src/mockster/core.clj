(ns mockster.core
  (:use [ring.middleware.params]
        [clojure.data.json :only [read-json json-str]]))

(def responses (atom {}))

(defn responder [{uri :uri, method :request-method}]
  (do
    (println "Inside responder:\nMethod: " method "\nURI: " uri)
    (if-let [response (@responses {:uri uri, :method method})]
      (assoc response :body (json-str (:body response)))
      {:status 404})))

(defn configurator [request]
  (let [params (:params request)
        method (keyword (get params "method"))
        uri (get params "uri")
        response-key {:uri uri, :method method}
        response-value (read-json (slurp (:body request)))]
    (println "In configurator:\nMethod: " method "\nURI: " uri "\nResponse:" response-key ": " response-value)
    (swap! responses assoc response-key response-value)
    {:status 200}))

(defn router [request]
  (let [uri (:uri request)]
    (println "Inside router")
    (if (= uri "/configure-mockster")
      (configurator request)
      (responder request))))

(defn echo [request]
  {:status 200
   :body (str request)})

(def app
  (-> router
      (wrap-params)))

