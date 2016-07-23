(ns todoapp.core
  (:require-macros [cljs.core.async.macros :refer [go]])
  (:require [om.core :as om :include-macros true]
            [om.dom :as dom :include-macros true]
            [cljs.core.async :refer [put! chan <!]]
            [clojure.string :as string]))

(enable-console-print!)

(defonce app-state
  (atom
    {:todos
     [{:desc "Go to Shop and buy milk 3.2%"} {:desc "Learn Clojure for job"}]}))

(defn todo-view [todo owner]
  (reify
    om/IRenderState
    (render-state [this {:keys [delete]}]
      (dom/li nil
        (dom/span nil (:desc todo))
        (dom/button #js {:onClick (fn [e] (put! delete @todo))} "Delete")))))

(defn add-todo [data owner]
  (let [new-todo (-> (om/get-node owner "todo-from-input")
                        .-value
                        :todos data)]
                        (println new-todo)
    (when new-todo
      (om/transact! data :todos #(conj % new-todo))
      (om/set-state! owner :desc ""))))

(defn handle-change [e owner {:keys [text]}]
  (let [value (.. e -target -value)]
    (if-not (re-find #"[0-9]" value)
      (om/set-state! owner :desc value)
      (om/set-state! owner :desc text))))

(defn todos-view [data owner]
  (reify
    om/IInitState
    (init-state [_]
      {:delete (chan)
       :desc ""})
    om/IWillMount
    (will-mount [_]
      (let [delete (om/get-state owner :delete)]
        (go (loop []
              (let [todo (<! delete)]
                (om/transact! data :todos
                  (fn [xs] (vec (remove #(= todo %) xs))))
                (recur))))))
    om/IRenderState
    (render-state [this state]
      (dom/div nil
        (apply dom/ul nil
          (om/build-all todo-view (:todos data)
            {:init-state state}))
        (dom/div nil
          (dom/input
            #js {:type "text" :ref "todo-from-input" :value (:desc state)
                 :onChange #(handle-change % owner state)})
          (dom/button #js {:onClick #(add-todo data owner)} "Add"))))))

(om/root
  todos-view
  app-state
  {:target (. js/document (getElementById "app"))})
