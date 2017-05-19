(ns mobile-calculadora.android.core
  (:require [reagent.core :as r :refer [atom]]
            [re-frame.core :refer [subscribe dispatch dispatch-sync]]
            [mobile-calculadora.events]
            [mobile-calculadora.subs]))

(def ReactNative (js/require "react-native"))

(def app-registry (.-AppRegistry ReactNative))
(def text (r/adapt-react-class (.-Text ReactNative)))
(def text-input (r/adapt-react-class (.-TextInput ReactNative)))
(def view (r/adapt-react-class (.-View ReactNative)))
(def image (r/adapt-react-class (.-Image ReactNative)))
(def touchable-highlight (r/adapt-react-class (.-TouchableHighlight ReactNative)))

(def logo-img (js/require "./images/cljs.png"))

(defn alert [title]
      (.alert (.-Alert ReactNative) title))

(defonce moedas (atom {:peso {:nome "PES" :preco-cambio 0.33 :preco-loja 0 :carteira 10 :reais-carteira 0} 
                       :dolar {:nome "USD" :preco-cambio 3.40 :preco-loja 0 :carteira 5 :reais-carteira 0}
                       :euro {:nome "EUR" :preco-cambio 4.10 :preco-loja 0 :carteira 0 :reais-carteira 0}
                       :real {:nome "R$" :preco-cambio 1 :preco-loja 0 :carteira 0 :reais-carteira 0}}))
(def chaves [:peso :dolar])

(def styles {
             :estilo-input {:font-size 10 :height 35 :border-width 1 :border-color "green" :margin-bottom 1 } 
             :estilo-titulo {:font-size 12 :border-width 1 :text-align "center" :text-align-vertical "center"} 
             :estilo-tudo {:flex 1} 
             :borda-red {:border-color "red" :border-width 2} 
             :borda-blue {:border-color "blue" :border-width 2} 
             :borda-green {:border-color "green" :border-width 2} 
             :estilo-lista {:flex 1 :align-items "center" :justify-content "flex-start"} 
             :estilo-texto-input {:justify-content "center"}
})

(defn estilos [chaves]
  {:style (apply merge (map styles chaves))})

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

(defn input-carteira [chave-moeda chave]
  (let [moeda (chave-moeda @moedas)]
    [view 
     [text (str "Dinheiro na carteira: ")] 
     [text-input (str (chave moeda))]
     [text (str (:nome moeda) " = R$ " (calcula-reais moeda))]
]))

(defn input-preco-cambio [chave-moeda chave]
  (let [moeda (chave-moeda @moedas)]
    [view 
     [text (str "Preço pago por " (:nome moeda) " = R$ ") ]
     [text-input (str (chave moeda))]]))

(defn titulo [texto]
  [text (estilos [:estilo-titulo :borda-blue]) texto])

(defn app-root []
  (let [greeting (subscribe [:get-greeting])]
    (fn []
      [view (estilos [:estilo-tudo :borda-red])
       [view (estilos [:estilo-lista :borda-green]) (titulo "Preco de cambio")]
       [view (estilos [:estilo-lista :borda-green]) (titulo "Quanto tem na carteira")]
       [view (estilos [:estilo-lista :borda-green]) (titulo "Quanto custa na loja?")]
       ])))

(defn init []
      (dispatch-sync [:initialize-db])
      (.registerComponent app-registry "mobileCalculadora" #(r/reactify-component app-root)))
