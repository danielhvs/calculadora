(ns web_calculadora.core
  (:require [reagent.core :as r :refer [atom]]
            [calculadora.core :as k]))

(enable-console-print!)

;; define your app data so that it doesn't get over-written on reload
(defonce moedas (atom {:peso {:nome "PES" :preco-cambio 0.33 :preco-loja 0 :carteira 0 :reais-carteira 0} 
                       :dolar {:nome "USD" :preco-cambio 3.40 :preco-loja 0 :carteira 0 :reais-carteira 0}
                       :euro {:nome "EUR" :preco-cambio 4.10 :preco-loja 0 :carteira 0 :reais-carteira 0}
                       :real {:nome "R$" :preco-cambio 1 :preco-loja 0 :carteira 0 :reais-carteira 0}

}
))
(def chaves [:peso :dolar :euro :real])

(defn calcula-reais-preco-loja [moeda]
  (* (:preco-loja moeda) (:preco-cambio moeda)))

(defn calcula-reais [moeda]
  (* (:carteira moeda) (:preco-cambio moeda)))

(defn get-moedas []
  (for [chave chaves]
    (chave @moedas)))

(defn preco-em-reais [moeda]
  (* (:preco-loja moeda) (:preco-cambio moeda)))

(defn calcula-melhor []
  (let [ms (get-moedas) 
        ordenado (sort-by preco-em-reais ms)]
    (filter #(and (> (preco-em-reais %) 0) (>= (:reais-carteira %) (preco-em-reais %))) ordenado)))

(defn input-valor [chave-moeda chave]
  (let [moeda (chave-moeda @moedas)]
    [:input {:type "text" 
             :value (chave moeda) 
             :on-change #(do (swap! moedas assoc-in [chave-moeda chave] (-> % .-target .-value))
                             (swap! moedas assoc-in [chave-moeda :reais-carteira] (calcula-reais (chave-moeda @moedas)))) } ] ))

(defn input-preco-cambio [chave-moeda chave]
  (let [moeda (chave-moeda @moedas)]
    [:div
     [:label "Preço pago por " (:nome moeda) " = R$ " ]
     (input-valor chave-moeda chave)]))

(defn input-preco-loja [chave-moeda chave]
  (let [moeda (chave-moeda @moedas)]
    [:div
     [:label "Produto custa " (:nome moeda) " "]
     (input-valor chave-moeda chave)]))

(defn input-carteira [chave-moeda chave]
  (let [moeda (chave-moeda @moedas)]
    [:div [:label "Dinheiro na carteira: "] (input-valor chave-moeda chave) [:label (:nome moeda) " = R$ " (calcula-reais moeda)]
]))

(defn calculadora-window []  
  [:div 
   [:div [:label "Preço de compra câmbio"]]
   (for [chave chaves]
     [:div [input-preco-cambio chave :preco-cambio]])
   [:div [:label "Quanto tem na carteira"]]
   (for [chave chaves]
     [:div [input-carteira chave :carteira]])
   [:div [:label "Quanto custa na loja"]]
   (for [chave chaves]
     [:div [input-preco-loja chave :preco-loja]])
   (for [melhor (calcula-melhor)]
     [:div [:label (str "Melhor pagar com "  (:nome melhor) " " (:preco-loja melhor) " = R$ " (calcula-reais-preco-loja melhor))]])
])

(r/render-component [calculadora-window]
                          (. js/document (getElementById "app")))

  ;; optionally touch your app-state to force rerendering depending on
  ;; your application
  ;; (swap! app-state update-in [:__figwheel_counter] inc)
(defn on-js-reload []
  (println (str "@moedas = " @moedas)))



; React Native (re-natal):
(comment
  (def text-input (r/adapt-react-class (.-TextInput ReactNative)))
)

(comment 
  (defn app-root []
    (let [greeting (subscribe [:get-greeting])]
      (fn []
        [view 
         [text-input {:style {:height 40 :border-width 2 :border-color "green" :margin-bottom 1 }} (str "R$ " )]
         [text-input {:style {:height 40 :border-width 2 :border-color "green" :margin-bottom 1 }} (str "R$ " )]
         ])))

)
