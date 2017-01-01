(ns urlmatcher.core-test
  (:require [clojure.test :refer :all]
            [urlmatcher.core :refer :all]))

(deftest url-match-test
  (is (= ["https://" "google.com" ["path" "to" "file"] {"param1" "val1" "param2" "val2"}]
         (url-match "https://google.com/path/to/file?param1=val1&param2=val2"))))

(deftest pattern-constructor-test
  (testing "w/o query params"
    (is (= (->Pattern "twitter.com" ["?user" "status" "?id"] {})
           (pattern "host(twitter.com); path(?user/status/?id);"))))
  (testing "with query params"
    (is (= (->Pattern "twitter.com" ["?user" "status" "?id"] {"offset" "?offset" "list" "?type"})
           (pattern "host(twitter.com); path(?user/status/?id); queryparam(offset=?offset); queryparam(list=?type);")))))

(deftest pattern-matching-test
  (let [p (pattern "host(dribbble.com); path(shots/?id); queryparam(offset=?offset);")]
    (testing "with query params"
      (is (= [[:id "1905065-Travel-Icons-pack"] [:offset "1"]]
             (recognize p "https://dribbble.com/shots/1905065-Travel-Icons-pack?list=users&offset=1"))))
    (testing "host mismatch"
      (is (= nil
             (recognize p "https://twitter.com/shots/1905065-Travel-Icons-pack?list=users&offset=1"))))
    (testing "offset queryparam missing"
      (is (= nil
             (recognize p "https://dribbble.com/shots/1905065-Travel-Icons-pack?list=users"))))
    ))
