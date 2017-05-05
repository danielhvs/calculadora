(ns calculadora.core-spec
  (:require [speclj.core :refer :all]
            [calculadora.core :refer :all]))

(describe "um cenario"
  (before
    (def entities 1))

  (it "soma 2"
    (let [r (incrementa 2)]
      (should= 3 r)))

  (it "soma 3"
    (let [r (incrementa 3)]
      (should= 4 r))))
