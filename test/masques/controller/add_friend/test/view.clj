(ns masques.controller.add-friend.test.view
  (:require [test.init :as test-init])
  (:require [fixtures.identity :as identity-fixture]
            [masques.model.friend :as friend-model]
            [masques.test.util :as test-util]
            [seesaw.core :as seesaw-core])
  (:use clojure.test
        masques.controller.add-friend.view))

(test-util/use-combined-login-fixture identity-fixture/fixture-map)

(def test-friend-xml
  "<?xml version=\"1.0\" encoding=\"UTF-8\"?>
<friend>
  <user name=\"test\" publicKey=\"MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQCK+z0Vpy12GSCrEbHgs65xIksuhXmCRsC7bC9iWKdCH3rxrRAi86V5VJCVGTSIZuHZd2dHz7HS/sSQTJDq40TPwt+aoGFL7Agp6DHCW95fGdBYy5W7ljYPoU8z7C1IUy9NCCdFR/Tn00I6zwWCz7g3JIJRp8B/zm6VEpX2IFaVVQIDAQAB\" publicKeyAlgorithm=\"RSA\"/>
  <destination>rVPi1MYulYsXwM79N5tCXwuu4dPYIDvvHJPByZdzGRk5KjtNC4hqVCx74ilVVHc59fozQm230iOWL4lZtuRlCjsfXjqrTaFFRnQ8YIUfQ~HgbcBJiKsxZuIWmoQ4ojnBXVwvcg~OG78vVODb~sale1dJiDXmwatUnHWrcUYEJSICaa-IMM2KGDE0axty7QYSf4oJ-m9yela0YqrYNOPggtb9YWoL2vYiuVCkH90btubFsuY0F-U-99xcPG0hHw4YOGxTVC-~FeuqURF5uJXMG4a4RfmIyVnq7bVy08sKI8fbyKGUEcVAwmUweLd64195Xs0iuEB2pfQWgle~4WYMSDLTTjisfONyGMSnqrB2HV9q6borapavfCCGcJkHhrAvzm7UmM22XmeQSFOlnE1GwWkQI0rWsKzf4W5DeIZOlNg-KSeZAL2knBNGPsHRBWFX6Dv-fQA-efuKsRPNIb7uXy8aeKQ2JOJZc-nnfI8iQV70wEoMDkQokc61ey1oJeVNAAAA</destination>
</friend>")

(deftest test-create
  (is (= (count (friend-model/all-friends)) 0)) 
  (let [frame (show nil)]
    (is frame)
    (is (.isShowing frame))
    (.doClick (seesaw-core/select frame ["#add-button"]))
    (is (.isShowing frame))
    (is (= (count (friend-model/all-friends)) 0))
    (friend-text frame test-friend-xml)
    (.doClick (seesaw-core/select frame ["#add-button"]))
    (let [all-friends (friend-model/all-friends)]
      (is (= (count all-friends) 1))
      (doseq [friend all-friends]
        (friend-model/destroy-record friend)))
    (is (not (.isShowing frame)))))

(deftest test-cancel
  (let [frame (show nil)]
    (is frame)
    (is (.isShowing frame))
    (.doClick (seesaw-core/select frame ["#cancel-button"]))
    (is (not (.isShowing frame)))))