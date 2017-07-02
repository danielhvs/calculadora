(ns mobile-calculadora.android.core
  (:require [reagent.core :as r :refer [atom]]
            [re-frame.core :refer [subscribe dispatch dispatch-sync]]
            [cljs.pprint :as pprint]
            [mobile-calculadora.events]
            [mobile-calculadora.subs]))

(defn gen-key []
  (gensym "key-"))

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

(defonce moedas (atom {:peso {:nome "PES" :preco-cambio 0.33 :preco-loja 2} 
                       :dolar {:nome "USD" :preco-cambio 3.40 :preco-loja 25}
                       :euro {:nome "EUR" :preco-cambio 4.10 :preco-loja 3}
                       :real {:nome "R$" :preco-cambio 1 :preco-loja 14}}))
(def chaves [:peso :dolar :euro :real])

(def border-width 1)
(def styles {
             :input {:font-size 16 :flex 1 :keyboard-type "numeric"} 
             :titulo {:color "blue" :font-size 24 :text-align "center" :text-align-vertical "center"} 
             :estilo-tudo {:flex 1}
             :borda-red {:border-color "red" :border-width border-width} 
             :borda-blue {:border-color "blue" :border-width border-width} 
             :borda-green {:border-color "green" :border-width border-width} 
             :view-titulo {:flex 1 :align-items "center" :justify-content "center"} 
             :lista-esquerda {:flex 5 :align-items "center" :justify-content "flex-start"} 
             :fila {:flex 1 :flex-direction "row"} 
             :label {
 :width 110 :align-items "flex-end" :margin-right 5
}
             :texto-pequeno {:font-size 12}
             :label-container {:flexDirection "row" :margin-vertical 2}
             :default {
 :height 24 :border-width 0.0 :border-color "#0f0f0f" :flex 1 :font-size 14 :padding 4
}
})

(defn estilos [chaves]
  {:style (apply merge (map styles chaves))})

(defn calcula-reais-preco-loja [moeda]
  (* (:preco-loja moeda) (:preco-cambio moeda)))

(defn get-moedas []
  (for [chave chaves]
    (chave @moedas)))

(defn preco-em-reais [moeda]
  (* (:preco-loja moeda) (:preco-cambio moeda)))

(defn calcula-melhor []
  (let [ms (get-moedas) 
        ordenado (sort-by preco-em-reais ms)]
    (filter #(> (preco-em-reais %) 0) ordenado)))

(defn titulo [texto]
  [text (estilos [:titulo]) texto])

(defn update-estado! [chave-moeda chave dado]
  (swap! moedas assoc-in [chave-moeda chave] dado))

(defn formata [numero]
  (pprint/cl-format nil "~,2f" numero))

(defn with-label [texto componente]
  [view (estilos [:label-container])
   [view (estilos [:label])
    [text texto]]
   componente])

(defn entrada [valor chave-moeda chave]
  [text-input (assoc (estilos [:default]) 
                :on-end-editing #(update-estado! chave-moeda chave @valor) 
                :on-change-text #(reset! valor %)
                :value (str @valor))])

(defn app-root []
  (let [greeting (subscribe [:get-greeting])]
    (fn []
      [scroll-view 
       [view (estilos [:view-titulo])
        [view {:style {:flex 2}}
         [text (estilos [:titulo]) "Preço de câmbio"]]
        [view (estilos [:lista-esquerda])
         (doall
          (for [chave chaves] ^{:key (gen-key)}
               [with-label (str "1 " (:nome (chave @moedas)) " = R$ " ) 
                [entrada (atom (formata (:preco-cambio (chave @moedas)))) chave :preco-cambio]]))]]
       [view (estilos [:view-titulo])
        [view {:style {:flex 2}}
         [text (estilos [:titulo]) "Preço do produto"]]
        [view (estilos [:lista-esquerda])
         (doall
          (for [chave chaves] ^{:key (gen-key)}
               [with-label (str (:nome (chave @moedas)) "" ) 
                [entrada (atom (formata (:preco-loja (chave @moedas)))) chave :preco-loja]]))]]
       [view (estilos [:view-titulo])
        [view {:style {:flex 2}}
         [text (estilos [:titulo]) "Melhor pagar com"]]
        [view (estilos [:lista-esquerda])
         (doall
          (for [melhor (calcula-melhor)] ^{:key (gen-key)}
               [with-label (str (:nome melhor) " " (formata (:preco-loja melhor)) " = " ) 
                [text (str "R$ " (formata (preco-em-reais melhor)))]]))]]])))

(defn init []
      (dispatch-sync [:initialize-db])
      (.registerComponent app-registry "mobileCalculadora" #(r/reactify-component app-root)))
