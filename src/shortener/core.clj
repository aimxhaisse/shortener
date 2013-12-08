(ns shortener.core
  (:use [compojure.core]
        [clojure.pprint]
        [hiccup core page]
        [ring.adapter.jetty]
        [ring.middleware reload stacktrace params])
  (:require [compojure.route :as route]
            [taoensso.carmine :as car :refer (wcar)]))

; misc
(defn random-token []
  "returns a random token"
  (str
   (Integer/toString (rand 2000000000) 36)))

(defn http-redir [where]
  {:status 302
   :headers {"Location" where}})

(defn get-hostname []
  "localhost:8040")

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
       [:a {:href (str "http://" (get-hostname) "/")} (get-hostname)]]]
     [:div content]
     [:div {:class "clearfix"}]
     [:div {:class "footer"}
      [:h4 {:class "text-right"} [:small "Powered by "
                                  [:a {:href "http://sbrk.org/"} "sbrk"]]]]]]))

; redis
(defmacro wcar* [& body] `(car/wcar redis-conn ~@body))
(def redis-conn {:pool {} :spec {}})

(defn get-url [k]
  "retrieves a URL from the redis db"
  (wcar*
   (car/get k)))

(defn insert-url [url]
  "creates a random token and inserts the value in the redis db"
  (let [token (random-token)]
    (if (not (nil? (get-url token)))
      (insert-url url)
      (do (wcar* (car/set token url))
          (str token)))))

; handlers
(defn handle-home []
  "this handler serves the home page"
  (layout [:div {:class "jumbotron"}
           [:h1 "URL Shortener"]
           [:p (str "Yet another URL shortener. Paste your link below and eat "
                    "a cookie while our crews are figuring out a way to shorten it.")]]
          [:form {:method "POST" :class "form-inline" :action "/" :name="New URL"}
           [:input {:class "col-xs-9 input-lg" :type "text" :placeholder "Enter your URL" :name "url"}]
           [:button {:class "col-xs-3 btn btn-primary btn-lg" :type "submit"} "Shorten!"]]))

(defn handle-redirect [token]
  "this handler redirects the user to the URL pointed by token"
  (if-let [url (get-url token)]
    (http-redir url)
    (http-redir "/")))

(defn handle-new-url [url]
  "this handler creates a new token for the given URL"
  (let [link (str "http://" (get-hostname) "/" (insert-url url))]
    (layout [:h1 "Here you go!"]
            [:div {:class "jumbotron"}
             [:h2 [:a {:href link} link]]])))

; routes
(defroutes custom-routes
  (GET "/" [] (handle-home))
  (POST "/" [url] (handle-new-url url))
  (GET "/:token" [token] (handle-redirect token))
  (route/resources "/"))

; main
(defn boot []
  (-> custom-routes
      (wrap-params)
      (wrap-reload '(shortener.core))
      (wrap-stacktrace)))

(defn -main [& args]
  (run-jetty (boot) {:port 8040 :join? false}))
