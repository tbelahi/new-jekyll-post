(ns new-post.core
	(require [clj-time.local :as l])
	(require [clj-time.format :as f])
	(require [clojure.tools.cli :refer [parse-opts]])
  (:gen-class))

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
    :default true]
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
	[options]
	(str (getTime) "-" (:title options) ".md"))

(defn make-interactive-title
	[s]
	(str (getTime) "-" (remove-spaces s) ".md"))

(defn make-header
	"write a string corresponding to yaml frontmatter necessary for Jekyll to parse the file and publish the post "
	[options post-attributes]
	(str "---\n" "title: " (remove-dashes (:title options)) "\nlayout: " (:layout post-attributes) "\n" "tags:\npublished: " (:published options) "\n---\n"))

(defn make-interactive-header
	"write a string corresponding to yaml frontmatter necessary for Jekyll to parse the file and publish the post "
	[options post-attributes input]
	(str "---\n" "title: " input "\nlayout: " (:layout post-attributes) "\n" "tags:\npublished: " (:published options) "\n---\n"))

(defn -main
	"main function where the action and logic take place"
  [& args]
  ;parse options from the command line
  (let [{:keys [options arguments errors summary]} (parse-opts args cli-options)]
  	;define filename and headers for markdown file from command line options and default values of post-attributes
  	(if (:intervactive options)
      (do
        (println "Please enter the title for your post:")
  		  (let [input (clojure.string/trim (read-line))
              filename (make-interactive-title input)
  			      header   (make-interactive-header options post-attributes input)]
  			      (spit filename header)))
  	  (let [filename (make-filename options)
  	      	header   (make-header options post-attributes)]
  	      	;create the file
  	    	  (spit filename header)))))
