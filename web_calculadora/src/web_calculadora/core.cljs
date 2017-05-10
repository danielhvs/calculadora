(ns web_calculadora.core
  (:require [reagent.core :as r :refer [atom]]
            [calculadora.core :as k]))

(enable-console-print!)

;; define your app data so that it doesn't get over-written on reload
(defonce moedas (atom {:peso {:nome "PES" :preco-cambio 0.33 :preco-loja 0 :carteira 1000} 
                       :dolar {:nome "USD" :preco-cambio 3.40 :preco-loja 0 :carteira 75}
                       :euro {:nome "EUR" :preco-cambio 4.10 :preco-loja 0 :carteira 35}}
))

(defn calcula-reais [moeda]
  (* (:carteira moeda) (:preco-cambio moeda)))

(defn botao []
  [:button {:on-click (fn [e] e)} 
   (str "-->")])

(defn input-valor [seq-moeda chave]
  (let [moeda (get seq-moeda 1)
        chave-moeda (get seq-moeda 0)]
  [:input {:type "text" 
           :value (chave moeda) 
           :on-change #(swap! moedas assoc-in [chave-moeda chave] (-> % .-target .-value))}]))

(defn input-preco-cambio [seq-moeda chave]
  (let [moeda (get seq-moeda 1)
        chave-moeda (get seq-moeda 0)]
    [:div
     [:label "Preço pago por " (:nome moeda) " = R$ " ]
     (input-valor seq-moeda chave)]))

(defn input-preco-loja [seq-moeda chave]
  (let [moeda (get seq-moeda 1)
        chave-moeda (get seq-moeda 0)]
    [:div
     [:label "Produto custa " (:nome moeda) " "]
     (input-valor seq-moeda chave)]))

(defn input-carteira [seq-moeda chave]
  (let [moeda (get seq-moeda 1)
        chave-moeda (get seq-moeda 0)]
    [:div [:label "Dinheiro na carteira: "] (input-valor seq-moeda chave) [:label (:nome moeda) " = R$ " (calcula-reais moeda)]
]))

(defn calculadora-window []  
  [:div 
   [:div [:label "Preço de compra câmbio"]]
   (for [moeda @moedas]
     [:div [input-preco-cambio moeda :preco-cambio]])
   [:div [:label "Quanto tem na carteira"]]
   (for [moeda @moedas]
     [:div [input-carteira moeda :carteira]])
   [:div [:label "Quanto custa na loja"]]
   (for [moeda @moedas]
     [:div [input-preco-loja moeda :preco-loja]])
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
