#!/usr/bin/env boot

(set-env! :dependencies '[[org.clojure/clojure "1.6.0"]
                          [clj-time "0.9.0"] 
                          [org.clojure/tools.cli "0.3.1"]])

(require '[clj-time.local :as l])
(require '[clj-time.format :as f])
(require '[clojure.tools.cli :refer [parse-opts]])
(require '[boot.cli :as cli])

;set some default values for yaml frontmatter
(def post-attributes {:title "" :date "" :layout "post" :permalink "" :published "true" :tags ""})

;define the cli options for the program 
(def cli-options
  [
   ["-h" "--help"]
   ["-t" "--title TITLE" "Title"
    :default "please-set-a-PROPER-title"
    :parse-fn #(str %)]
   ["-pb" "--published" "Publication status"
    :default false]
   ["-i"  "Interactive Mode"
    :id :intervactive
    :default false]
   ])

(defn getTime []
	"returns the current date in format :year-month-day, e.g. 2015-01-27"
	(l/format-local-time (l/local-now) :year-month-day))

(defn remove-dashes [s]
	"replace dashes by spaces in string s"
	(clojure.string/replace s #"-" " "))

(defn remove-spaces[s]
	(clojure.string/replace s #" " "-"))

(defn make-filename
	"write a string in the format required by Jekyll for a blog post with the current date"
	[t pb]
	(str (getTime) "-" t ".md"))

(defn make-interactive-title
	[s]
	(str (getTime) "-" (remove-spaces s) ".md"))

(defn make-header
	"write a string corresponding to yaml frontmatter necessary for Jekyll to parse the file and publish the post "
	[t pb post-attributes]
	(str "---\n" "title: " (remove-dashes t) "\nlayout: " (:layout post-attributes) "\n" "tags:\npublished: " (if pb "true" "false") "\n---\n"))

(defn make-interactive-header
	"write a string corresponding to yaml frontmatter necessary for Jekyll to parse the file and publish the post "
	[pb post-attributes input]
	(str "---\n" "title: " input "\nlayout: " (:layout post-attributes) "\n" "tags:\npublished: " (if pb "true" "false") "\n---\n"))

(cli/defclifn -main
  [t title TITLE str "Title-of-the-post"
   i interactive bool "Set title of the post in interactive mode"
   p published bool "set the published tag to true, default to false"]
   (println title)
   (println interactive)
   (println published)
   (if interactive
     (do
       (println "Please enter the title for your post:")
       (let [input (clojure.string/trim (read-line))
             filename (make-interactive-title input)
             header   (make-interactive-header published post-attributes input)]
             (spit filename header)))
     (let [filename (make-filename title published)
           header   (make-header title published post-attributes)]
           ;create the file
           (spit filename header))))