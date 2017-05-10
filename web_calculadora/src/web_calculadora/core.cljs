(ns web_calculadora.core
  (:require [reagent.core :as r :refer [atom]]
            [calculadora.core :as k]))

(enable-console-print!)

;; define your app data so that it doesn't get over-written on reload
(defonce moedas (atom {:peso {:nome "PES" :preco-cambio 0 :preco-loja 0} 
                       :dolar {:nome "USD" :preco-cambio 1 :preco-loja 0}}))

(defn botao []
  [:button {:on-click (fn [e] e)} 
   (str "-->")])

(defn input-preco-cambio [seq-moeda]
  (let [moeda (get seq-moeda 1)
        chave (get seq-moeda 0)]
    [:div
     [:label "Preço pago por " (:nome moeda) " = R$ " ] 
     [:input {:type "text" 
              :value (:preco-cambio moeda)
              :on-change #(swap! moedas assoc-in [chave :preco-cambio] (-> % .-target .-value))}]]))

(defn calculadora-window []  
  [:div 
   [:div [:label "Preço de compra câmbio"]]
   (for [moeda @moedas]
     [:div [input-preco-cambio moeda]])
   [botao]
   (for [moeda @moedas]
     [:div [:label "DEBUG " (str moeda)]])])

(r/render-component [calculadora-window]
                          (. js/document (getElementById "app")))

  ;; optionally touch your app-state to force rerendering depending on
  ;; your application
  ;; (swap! app-state update-in [:__figwheel_counter] inc)
(defn on-js-reload []
  (println (str "@moedas = " @moedas)))
