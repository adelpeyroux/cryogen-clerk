(ns cryogen-clerk.core
  (:require [cryogen-core.markup :refer [markup-registry rewrite-hrefs]]
            [nextjournal.clerk :as c]
            [clojure.string :as s])
  (:import cryogen_core.markup.Markup))

(defn rewrite-hrefs-transformer
  "A :replacement-transformer for use in markdown.core that will inject the
  given blog prefix in front of local links."
  [{:keys [blog-prefix]} text state]
  [(rewrite-hrefs blog-prefix text) state])

(def tmp-file-name "tmp-post.txt")
(def tmp-dir "./tmp-dir" )

(defn clerk
  []
  (reify Markup
    (dir [this] "clj")
    (exts [this] #{".clj"})
    (render-fn [this]
      (fn [rdr config]
        (let [clj-content (->> (java.io.BufferedReader. rdr)
                               (line-seq)
                               (s/join "\n"))
              _ (spit tmp-file-name clj-content)
              _ (c/build! {:index tmp-file-name :out-path tmp-dir :ssr true :compile-css true})
              html-content (slurp (str tmp-dir "/index.html"))]
          html-content)))))

(defn init []
  (swap! markup-registry conj (clerk)))
