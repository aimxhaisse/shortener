(ns shortener.core
  (:use [compojure.core]
        [hiccup core page]
        [ring.adapter.jetty]
        [ring.middleware reload stacktrace])
  (:require [compojure.route :as route]))

; layouts
(defn layout [& content]
  (html5
   [:head
    [:meta {:http-equiv "Content-Type"
            :content "text/html; charset=utf-8"}]
    [:meta {:name "viewport" :content "width=device-width, initial-scale=1.0"}]
    [:meta {:name "description" :content "Yet Another URL Shortener"}]
    [:meta {:name "author" :content "s. rannou <mxs@sbrk.org>"}]
    [:title "Yet Another URL Shortener | 302.sbrk.org"]
    (include-css "/css/bootstrap.min.css")
    (include-css "/css/shortener.css")]
   [:body
    [:div {:class "container"}
     [:div {:class "header"}
      [:h3
       [:a {:href "http://302.sbrk.org/"} "302.sbrk.org"]]]
     [:div content]
     [:div {:class "clearfix"}]
     [:div {:class "footer"}
      [:h4 {:class "text-right"} [:small "Powered by " [:a {:href "http://sbrk.org/"} "sbrk"]]]]]]))

; handlers
(defn handle-home []
  "this handler serves the home page"
  (layout [:div {:class "jumbotron"}
           [:h1 "URL Shortener"]
           [:p "Yet another URL shortener, paste your link below and eat a cookie while our crews are finding a way to shorten it."]]
          [:form {:method "post" :class "form-inline"}
           [:input {:class "col-xs-9 input-lg" :type "text" :placeholder "Enter your URL"}]
           [:button {:class "col-xs-3 btn btn-primary btn-lg" :type "submit"} "Shorten!"]]))

(defn handle-redirect [token]
  "this handler redirects the user to the URL pointed by token"
  (layout "Your token is " token))

(defn handle-new-url [new-url]
  "this handler creates a new token for the given URL"
  (layout "Your URL is " new-url))

; routes
(defroutes custom-routes
  (GET "/" [] (handle-home))
  (route/resources "/")
  (GET "/:token" [token] (handle-redirect token))
  (POST "/" [new-url] (handle-new-url new-url)))

; main
(defn boot []
  (-> custom-routes
      (wrap-reload '(shortener.core))
      (wrap-stacktrace)))

(defn -main [& args]
  (run-jetty (boot) {:port 8040 :join? false}))
