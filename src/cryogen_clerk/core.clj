(ns cryogen-clerk.core
  (:require [cryogen-core.markup :refer [markup-registry rewrite-hrefs]]
            [clojure.string :as s])
  (:import cryogen_core.markup.Markup))

(defn rewrite-hrefs-transformer
  "A :replacement-transformer for use in markdown.core that will inject the
  given blog prefix in front of local links."
  [{:keys [blog-prefix]} text state]
  [(rewrite-hrefs blog-prefix text) state])

(defn clerk
  []
  (reify Markup
    (dir [this] "clj")
    (exts [this] #{".clj"})
    (render-fn [this]
      (fn [rdr config]
        (->> (java.io.BufferedReader. rdr)
             (line-seq)
             (s/join "\n"))))))

(defn init []
  (swap! markup-registry conj (clerk)))
