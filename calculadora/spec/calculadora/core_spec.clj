(ns calculadora.core-spec
  (:require [speclj.core :refer :all]
            [calculadora.core :refer :all]))

(describe "um cenario"
  (before
    (def entities 1))

  (it "soma 2"
    (let [r (incrementa 2)]
      (should= 3 r)))

  (it "soma 4"
    (let [r (incrementa 4)]
      (should= 5 r))))
