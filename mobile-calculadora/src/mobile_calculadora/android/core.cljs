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
(def scroll-view (r/adapt-react-class (.-ScrollView ReactNative)))
(def image (r/adapt-react-class (.-Image ReactNative)))
(def touchable-highlight (r/adapt-react-class (.-TouchableHighlight ReactNative)))

(def logo-img (js/require "./images/cljs.png"))

(defn alert [title]
      (.alert (.-Alert ReactNative) title))

(defonce moedas (atom {:peso {:nome "PES" :preco-cambio 0.33 :preco-loja 2 :carteira 10 :reais-carteira 3.30} 
                       :dolar {:nome "USD" :preco-cambio 3.40 :preco-loja 25 :carteira 5 :reais-carteira 17}
                       :euro {:nome "EUR" :preco-cambio 4.10 :preco-loja 23 :carteira 6 :reais-carteira 16.60}
                       :real {:nome "R$" :preco-cambio 1 :preco-loja 14 :carteira 3 :reais-carteira 3}}))
(def chaves [:peso :dolar :euro :real])

(def border-width 2)
(def styles {
             :input {:font-size 8 :flex 1} 
             :titulo {:color "blue" :font-size 10 :text-align "center" :text-align-vertical "center"} 
             :estilo-tudo {:flex 1}
             :borda-red {:border-color "red" :border-width border-width} 
             :borda-blue {:border-color "blue" :border-width border-width} 
             :borda-green {:border-color "green" :border-width border-width} 
             :view-titulo {:flex 1 :align-items "center" :justify-content "center"} 
             :lista-esquerda {:flex 3 :align-items "stretch" :justify-content "flex-start"} 
             :fila {:flex 1 :flex-direction "row" :align-items "center" :justify-content "flex-start"} 
             :input-preco {:flex-direction "row" :align-items "center" :justify-content "space-around"}
             :label {:align-items "flex-start"}
             :texto-pequeno {:font-size 8}
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
  [text (estilos [:titulo]) texto])

(defn input-preco [texto componente]
  [view (estilos [:fila :borda-blue])
   [view (estilos [:label :borda-green])
    [text (estilos [:texto-pequeno]) texto]]
   componente])

(defn app-root []
  (let [greeting (subscribe [:get-greeting])]
    (fn []
      [scroll-view (estilos [:estilo-tudo :borda-red])
       [view (estilos [:borda-red])
        [view (estilos [:view-titulo :borda-green]) 
         (titulo "Preco de cambio")]
        [view (estilos [:lista-esquerda :borda-green])
         (for [chave chaves]
           (input-preco (str "Comprei cada " (:nome (chave @moedas)) " por R$ " ) [text-input (estilos [:input]) (str (:preco-cambio (chave @moedas)))]))]]
       [view (estilos [:borda-red])
        [view (estilos [:view-titulo :borda-blue]) 
         (titulo "Dinheiro na carteira")]
        [view (estilos [:lista-esquerda :borda-green])
         (for [chave chaves]
           (input-preco (str "" (:nome (chave @moedas)) ": " ) [text-input (estilos [:input]) (str (:carteira (chave @moedas)))]))]]
       [view (estilos [:borda-red])
        [view (estilos [:view-titulo :borda-green]) 
         (titulo "Preco na loja")]
        [view (estilos [:lista-esquerda :borda-green])
         (for [chave chaves]
           (input-preco (str "" (:nome (chave @moedas)) ": " ) [text-input (estilos [:input]) (str (:preco-loja (chave @moedas)))]))]]
       [view (estilos [:borda-red])
        [view (estilos [:view-titulo :borda-green]) 
         (titulo "Melhor pagar com")]
        [view (estilos [:lista-esquerda :borda-green])
         (for [melhor (calcula-melhor)]
           (input-preco (str "Melhor: " (:nome melhor) ": " ) [text (estilos [:text-pequeno]) (:preco-loja melhor)]))]]      
       ])))

(defn init []
      (dispatch-sync [:initialize-db])
      (.registerComponent app-registry "mobileCalculadora" #(r/reactify-component app-root)))
