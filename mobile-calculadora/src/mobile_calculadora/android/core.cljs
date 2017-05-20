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

(def border-width 2)
(def styles {
             :input {:font-size 10 :flex 1} 
             :titulo {:font-size 12 :border-width 1 :text-align "center" :text-align-vertical "center"} 
             :estilo-tudo {:flex 1} 
             :borda-red {:border-color "red" :border-width border-width} 
             :borda-blue {:border-color "blue" :border-width border-width} 
             :borda-green {:border-color "green" :border-width border-width} 
             :lista-titulo {:flex 1 :align-items "center" :justify-content "flex-start"} 
             :lista-esquerda {:flex 10 :align-items "stretch" :justify-content "flex-start"} 
             :fila {:flex 1 :flex-direction "row" :align-items "center" :justify-content "flex-start"} 
             :input-preco {:flex-direction "row" :align-items "center" :justify-content "space-around"}
             :label-container {:flex-direction "row" :align-items "center" :justify-content "flex-end" :margin-vertical 2 :flex 1 }
             :label {:align-items "flex-start" :padding-top 2}
             :texto-pequeno {:font-size 10}
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
  [text (estilos [:titulo :borda-blue]) texto])

(defn input-preco [texto componente]
  [view (estilos [:fila :borda-red])
   [view (estilos [:label :borda-blue])
    [text (estilos [:texto-pequeno]) texto]]
   componente])

(defn app-root []
  (let [greeting (subscribe [:get-greeting])]
    (fn []
      [view (estilos [:estilo-tudo :borda-red])
       [view (estilos [:lista-titulo :borda-green]) 
        (titulo "Preco de cambio")]
       [view (estilos [:lista-esquerda :borda-green])
        (input-preco "R$->" [text-input (estilos [:input]) "oi"])
        (input-preco "R$->" [text-input (estilos [:input]) "oi"])]
      [view (estilos [:lista-titulo :borda-green]) 
        (titulo "Preco de cambio")]
       [view (estilos [:lista-esquerda :borda-green])
        (input-preco "R$->  " [text-input (estilos [:input]) "oi"])
        (input-preco "R$->" [text-input (estilos [:input]) "oi"])]
       ])))

(defn init []
      (dispatch-sync [:initialize-db])
      (.registerComponent app-registry "mobileCalculadora" #(r/reactify-component app-root)))
