(ns mockster.core-test
  (:use [mockster.core]
        [clojure.test]
        [ring.mock.request]
        [clojure.data.json :only [read-json json-str]]))

(defn response-matching [response]
  (assoc response :body (json-str (:body response))))

(defn configuration-request-with [uri method response]
  (-> (request :post "/mockster-responses")
      (query-string {"uri" uri, "method" method})
      (body (json-str response))))

(defn delete-request-with [uri method]
  (-> (request :delete "/mockster-responses")
      (query-string {"uri" uri, "method" method})))

(defn api-request-with [uri method]
  (request (keyword method) uri))

(deftest mockster-test
  (let [the-uri "api"
        the-method "put"
        the-api-request (api-request-with the-uri the-method)]

    (testing "post creates new mock response"
      (let [the-response {:status 200, :body [{:id 1}, {:id 2}, {:id 3}]}
            the-configuration-request (configuration-request-with the-uri the-method the-response)]
        (reset! responses {})
        (app the-configuration-request)
        (is (= (app the-api-request) (response-matching the-response)))))

    (testing "multiple posts create consecutive responses"
      (let [the-first-response {:status 200, :body [{:foo "bar"}]}
            the-second-response {:status 501}
            the-first-configuration-request (configuration-request-with the-uri the-method the-first-response)
            the-second-configuration-request (configuration-request-with the-uri the-method the-second-response)]
        (reset! responses {})
        (app the-first-configuration-request)
        (app the-second-configuration-request)
        (is (= (app the-api-request) (response-matching the-first-response)))
        (is (= (app the-api-request) (response-matching the-second-response)))))

    (testing "return status 404 when uri and method combination has been depleted"
      (let [the-response {:status 200}
            the-configuration-request (configuration-request-with the-uri the-method the-response)]
        (reset! responses {})
        (app the-configuration-request)
        (app the-api-request)
        (is (= (app the-api-request) {:status 404}))))

    (testing "returns status 404 for unconfigured uri and method combination"
      (do
        (reset! responses {})
        (is (= (app the-api-request) {:status 404}))))

    (testing "delete removes configurations"
      (let [the-response {:status 200}
            the-configuration-request (configuration-request-with the-uri the-method the-response)
            the-delete-request (delete-request-with the-uri the-method)]
        (reset! responses {})
        (app the-configuration-request)
        (app the-delete-request)
        (is (= (app the-api-request) {:status 404}))))

    (testing "delete without uri resets configurations"
      (let [the-response {:status 200}
            the-configuration-request (configuration-request-with the-uri the-method the-response)
            the-delete-request (request :delete "/mockster-responses")]
        (reset! responses {})
        (app the-configuration-request)
        (app the-delete-request)
        (is (= (app the-api-request) {:status 404}))))

    (testing "configuration uri works without :context param"
      (with-redefs [mockster.core/configure (fn [request] "has been called")]
        (let [the-configuration-request {:uri "/mockster-responses" :request-method :post}]
          (is (= (app the-configuration-request) "has been called")))))

    (testing "configuration uri works with :context param"
      (with-redefs [mockster.core/configure (fn [request] "has been called")]
        (let [the-configuration-request {:uri "/contextpath/mockster-responses" :request-method :post :context "/contextpath"}]
          (is (= (app the-configuration-request) "has been called")))))

    (testing "deletion uri works without :context param"
      (with-redefs [mockster.core/delete (fn [request] "has been called")]
        (let [the-configuration-request {:uri "/mockster-responses" :request-method :delete}]
          (is (= (app the-configuration-request) "has been called")))))

    (testing "deletion uri works with :context param"
      (with-redefs [mockster.core/delete (fn [request] "has been called")]
        (let [the-configuration-request {:uri "/contextpath/mockster-responses" :request-method :delete :context "/contextpath"}]
          (is (= (app the-configuration-request) "has been called")))))))