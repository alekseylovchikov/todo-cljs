(ns todoapp.core
  (:require [om.core :as om :include-macros true]
            [om.dom :as dom :include-macros true]))

(enable-console-print!)

(defonce app-state (atom {:todos [
    {:desc "Buy Milk" :id 0}
    {:desc "Go to Shop" :id 1}
    {:desc "Buy Car" :id 2}
    {:desc "Learn Clojure" :id 3}
    {:desc "Feed Cat" :id 4}
    {:desc "Drink Vodka" :id 5}
  ]
}))

(defn form [text owner]
  (reify
    om/IRender
    (render [_]
      (dom/form nil (dom/input nil)))))

(defn todos []
  (om/ref-cursor (:todos (om/root-cursor app-state))))

(defn todo-item-view [todo owner]
  (reify
    om/IRender
    (render [_]
      (dom/div nil (dom/strong nil (:desc todo))
        (dom/p nil (dom/button #js {:onClick (fn [e] (let [cs (todos)] (om/update! cs (vec (remove #(= (:id todo) (:id %)) cs)))))} "delete"))))))

(defn todos-view [todos owner]
  (reify
    om/IRender
    (render [_]
        (dom/div nil
          (dom/h1 nil "ToDo List")
          (doall (om/build-all todo-item-view todos))))))

(defn app-view [state owner]
  (reify
    om/IRender
    (render [_]
      (dom/div nil
        (om/build form ())
        (om/build todos-view (:todos state))
        ))))

(om/root
 app-view
 app-state
 {:target (js/document.getElementById "app")})
