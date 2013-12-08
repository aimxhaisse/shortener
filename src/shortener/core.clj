(ns shortener.core
  (:use [compojure.core]
        [hiccup.core]
        [ring.adapter.jetty]
        [ring.middleware reload stacktrace]))

; handlers
(defn handle-home []
  "this handler serves the home page"
  (str "Hello World"))

(defn handle-redirect [token]
  "this handler redirects the user to the URL pointed by token"
  (str "Your token is " token))

(defn handle-new-url [new-url]
  "this handler creates a new token for the given URL"
  (str "Your URL is " new-url))

; routes
(defroutes custom-routes
  (GET "/" [] (handle-home))
  (GET "/:token" [token] (handle-redirect token))
  (POST "/" [new-url] (handle-new-url new-url)))

; main
(defn boot []
  (-> custom-routes
      (wrap-reload '(shortener.core))
      (wrap-stacktrace)))

(defn -main [& args]
  (run-jetty (boot) {:port 8040 :join? false}))
