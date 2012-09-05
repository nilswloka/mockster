(ns mockster.core
  (:use [ring.middleware.params]
        [clojure.data.json :only [read-json json-str]]))

(def responses (atom {}))

(defn response-key-for [uri method]
  (let [method (keyword method)
        response-key {:uri uri, :method method}]
    response-key))

(defn configure [{{uri "uri" method "method"} :params :as request}]
  (let [params (:params request)
        response-key (response-key-for uri method)
        response-value (read-json (slurp (:body request)))]
    (swap! responses (partial merge-with (fn [former new] (concat former new))) {response-key (vector response-value)})
    {:status 200}))

(defn delete [{{uri "uri" method "method"} :params}]
  (if (nil? uri)
    (do
      (reset! responses {})
      {:status 200})
    (let [response-key (response-key-for uri method)]
      (swap! responses dissoc response-key)
      {:status 200})))

(defn respond-to [{uri :uri, method :request-method}]
  (let [all-responses @responses
        the-key {:uri uri, :method method}
        first-matching-response (first (all-responses the-key))
        rest-matching-responses (rest (all-responses the-key))]
    (if (not (nil? first-matching-response))
      (do
        (swap! responses assoc the-key rest-matching-responses)
        (assoc first-matching-response :body (json-str (:body first-matching-response))))
      {:status 404})))

(defn router [{uri :uri method :request-method context :context :as request}]
  (cond
   (and (= uri (str context "/mockster-responses")) (= method :post)) (configure request)
   (and (= uri (str context "/mockster-responses")) (= method :delete)) (delete request)
   :else (respond-to request)))

(def app (wrap-params router))

