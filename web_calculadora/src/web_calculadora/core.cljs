(ns web_calculadora.core
  (:require [reagent.core :as r :refer [atom]]
            [calculadora.core :as k]))

(enable-console-print!)

;; define your app data so that it doesn't get over-written on reload
(defonce app-state (atom {}))
(defonce moeda (atom {}))
(defonce cache (atom {}))
(defonce moedas (atom ["PES" "USD" "EUR"]))


(defn input []
  [:input {:type "text" 
           :value (:reais @cache) 
           :on-change #(swap! cache assoc :reais (-> % .-target .-value))}])

(defn botao []
  [:button {:on-click (fn [e] (swap! moeda assoc :reais (:reais @cache)))} 
   (str "-->")])

(defn input-com-label [palavra]
  [:label (str palavra) [input palavra]])

(defn calculadora-window []  
  [:div 
   [input]
   [botao] 
   [:div [:label "Preço de compra câmbio"]]
   (for [moeda @moedas]
     [:div [input-com-label (str moeda " = ")]]
     )
   [:div [:label "Preços na loja"]]
   (for [moeda @moedas]
     [:div [input-com-label (str moeda ": ")]])
   ])

(r/render-component [calculadora-window]
                          (. js/document (getElementById "app")))

  ;; optionally touch your app-state to force rerendering depending on
  ;; your application
  ;; (swap! app-state update-in [:__figwheel_counter] inc)
(defn on-js-reload []
  (println (str "APP-STATE:" @app-state)))
