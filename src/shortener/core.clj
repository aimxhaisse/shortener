(ns shortener.core
  (:use [compojure.core]
        [hiccup.core]
        [ring.adapter.jetty]
        [ring.middleware reload stacktrace]))

; layouts
(defn layout [& content]
  (html
   [:head
    [:meta {:http-equiv "Content-Type"
            :content "text/html; charset=utf-8"}]
    [:title "Yet Another URL Shortener | 302.sbrk.org"]
    [:body content]]))

; handlers
(defn handle-home []
  "this handler serves the home page"
  (layout "Hello World"))

(defn handle-redirect [token]
  "this handler redirects the user to the URL pointed by token"
  (layout "Your token is " token))

(defn handle-new-url [new-url]
  "this handler creates a new token for the given URL"
  (layout "Your URL is " new-url))

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
