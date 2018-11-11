(ns web_calculadora.core
  (:require [reagent.core :as r :refer [atom]]
            [calculadora.core :as k]
            [re-frame.core :as rf]))

;; Events

(rf/reg-event-db
 :initialize-db
 (fn [_ [_ _]]
   {:moedas {:peso {:nome "PES" :preco-cambio 0.33 :preco-loja 0 :carteira 0 :reais-carteira 0} 
             :dolar {:nome "USD" :preco-cambio 3.40 :preco-loja 0 :carteira 0 :reais-carteira 0}
             :euro {:nome "EUR" :preco-cambio 4.10 :preco-loja 0 :carteira 0 :reais-carteira 0}
             :real {:nome "R$" :preco-cambio 1 :preco-loja 0 :carteira 0 :reais-carteira 0}}}))

(rf/reg-event-db
 :atributo-mudou
 (fn [db [_ nome-moeda atributo valor]]
   (assoc-in db [:moedas nome-moeda atributo] valor)))

;; Subs
(rf/reg-sub :debug (fn [db _] db)) 
(rf/reg-sub :chaves (fn [db _] (keys (:moedas db)))) 
(rf/reg-sub :moedas (fn [db _] (:moedas db))) 

(defn calcula-reais-preco-loja [moeda]
  (* (:preco-loja moeda) (:preco-cambio moeda)))

(defn calcula-reais [moeda]
  (* (:carteira moeda) (:preco-cambio moeda)))

(defn preco-em-reais [moeda]
  (* (:preco-loja moeda) (:preco-cambio moeda)))

(defn get-moedas [chaves moedas]
  (for [chave chaves]
    (chave moedas)))

(defn calcula-melhor [chaves moedas]
  (let [ms (get-moedas chaves moedas) 
        ordenado (sort-by preco-em-reais ms)]
    (filter #(> (preco-em-reais %) 0) ordenado)))

(defn texto-input [texto chave moedas atributo]
  [:div
   [:label texto]
   [:input {:type "text" 
            :value (atributo (chave @moedas))
            :on-change #(rf/dispatch [:atributo-mudou chave atributo (-> % .-target .-value)])}]]
)

(defn calculadora-window []  
  [:div 
   [:h2 "Preço de compra câmbio"]
   (let [chaves (rf/subscribe [:chaves])
         moedas (rf/subscribe [:moedas])]
     (for [chave @chaves]
       [texto-input (str "Preco pago por " (:nome (chave @moedas)) " = R$ ") chave moedas :preco-cambio])) 
   [:h2 "Quanto custa na loja"]
   (let [chaves (rf/subscribe [:chaves])
         moedas (rf/subscribe [:moedas])]
     (for [chave @chaves]
       [texto-input (str "Produto custa " (:nome (chave @moedas)) " ") chave moedas :preco-loja]))
   (let [moedas (rf/subscribe [:moedas])
         chaves (rf/subscribe [:chaves])] 
     (for [melhor (calcula-melhor @chaves @moedas)]
       [:div
        [:label (str "Melhor pagar com "  (:nome melhor) " " (:preco-loja melhor) " = R$ " (calcula-reais-preco-loja melhor))]]))
   #_[:div (str @(rf/subscribe [:debug]))]
   ])

;; init
(r/render-component [calculadora-window]
                          (. js/document (getElementById "app")))
(rf/dispatch [:initialize-db])
(enable-console-print!)
