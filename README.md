# urlmatcher

A Clojure library designed to recognize and destruct URL by 
host domain, path parts, splitted with "/",
queryparam: name/value pairs of query (see examples below)

Each string that is started from "?" is a "bind"

## Usage

    (def twitter (pattern "host(twitter.com); path(?user/status/?id);"))
    (recognize twitter "http://twitter.com/bradfitz/status/562360748727611392")
    ;; => [[:id 562360748727611392] [:user "bradfitz"]]

    (def dribbble (pattern "host(dribbble.com); path(shots/?id); queryparam(offset=?offset);")
    (recognize dribbble "https://dribbble.com/shots/1905065-Travel-Icons-pack?list=users&offset=1")
    ;; => [[:id "1905065-Travel-Icons-pack"] [:offset "1"]]
    (recognize dribbble "https://twitter.com/shots/1905065-Travel-Icons-pack?list=users&offset=1")
    ;; => nil ;; host mismatch
    (recognize dribbble "https://dribbble.com/shots/1905065-Travel-Icons-pack?list=users")
    ;; => nil ;; offset queryparam missing

    (def dribbble2 (pattern "host(dribbble.com); path(shots/?id); queryparam(offset=?offset); queryparam(list=?type);")

## License

Copyright © 2016 FIXME

Distributed under the Eclipse Public License either version 1.0 or (at
your option) any later version.
