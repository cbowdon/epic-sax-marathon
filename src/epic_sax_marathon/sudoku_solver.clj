(ns epic-sax-marathon.sudoku-solver
  (:require [clojure.set :as set]))

(def board identity)
(def block-size
  "Size of a block in the sudoku board, i.e. the x and y dimension."
  3)

(def all-values (set (range 1 (+ (* block-size block-size) 1))))

(defn value-at
  "Return the value at coordinates in sudoku board."
  [board coordinates]
  (get-in board coordinates))

(defn has-value?
  "Predicate for whether the board has a value at coordinates (zero represents not having a value)."
  [board coordinates]
  (not (= 0 (value-at board coordinates))))

(defn row-values
  "Return the set of values in the row on the board at coordinates."
  [board [x y]]
  (set (get board x)))

(defn col-values
  "Return the set of values in the column on the board at coordinates."
  [board [x y]]
  (->> (range 9)
       (map #(value-at board [% y]))
       set))

(defn coord-pairs
  "Return all coordinate pairs (2d vector) where row and column are from coord-sequence."
  [coord-sequence]
  (for [x coord-sequence
        y coord-sequence]
    [x y]))

(defn block-origin
  "Return the origin coordinates of the block in board at given coordinates."
  [board coordinates]
  (map #(* block-size (quot % block-size)) coordinates))

(defn block-coords
  "Return all the coordinate pairs in the block in board at given coordinates."
  [board coordinates]
  (let [[x0 y0] (block-origin board coordinates)
        xn (+ block-size x0)
        yn (+ block-size y0)]
    (for [x (range x0 xn)
          y (range y0 yn)]
      [x y])))

(defn block-values
  "Return the set of all numbers in the block of coordinates."
  [board coordinates]
  (->> (block-coords board coordinates)
       (map #(value-at board %))
       set))

(defn valid-values-for
  "Return all valid (not yet used in block or row/col) numbers for coordinates in board.
  Return empty set if block is already filled."
  [board coordinates]
  (if (has-value? board coordinates)
    #{}
    (set/difference all-values
                    (set/union
                     (block-values board coordinates)
                     (row-values board coordinates)
                     (col-values board coordinates)))))

(defn filled?
  "Predicate that is true if the board has no empty squares (zeros)."
  [board]
  (every? #(has-value? board %)
          (coord-pairs (range 1 (+ 1 block-size)))))

(defn rows
  "Return the set of numbers in every row of board."
  [board]
  (map set board))

(defn cols
  "Return the set of numbers in every column of board."
  [board]
  (let [board-len (* block-size block-size)
        vals (range board-len)]
    (map (fn [y] (set (map #(get-in board [% y]) vals)))
         vals)))

(defn block-origins
  "Return origins for every block in a board of given size."
  [size]
  (for [x (range size)
        y (range size)]
    [(* x size)
     (* y size)]))

(defn blocks
  "Return the set of numbers in every block of board."
  [board]
  (let [origins (block-origins block-size)]
    (map #(block-values board %) origins)))

(defn valid-rows?
  "Predicate for whether every row in board is valid."
  [board]
  (every? #(= all-values %) (rows board)))

(defn valid-cols?
  "Predicate for whether every col in board is valid."
  [board]
  (every? #(= all-values %) (cols board)))

(defn valid-blocks?
  "Predicate for whether every block in board is valid."
  [board]
  (every? #(= all-values %) (blocks board)))

(defn valid-solution?
  "Predicate for whether every row, column and block in board is valid."
  [board]
  (and (valid-rows? board)
       (valid-cols? board)
       (valid-blocks? board)))

(defn set-value-at
  "Update the board with new-value at coord."
  [board coord new-value]
  (assoc-in board coord new-value))

(defn find-empty-point
  "Locate the first empty point in board."
  [board]
  (->> (range (* block-size block-size))
       coord-pairs
       (filter #(not (has-value? board %)))
       first))

(defn possible-boards
  "Calculate all possible boards given the input board.
  Each possible board may not be valid."
  [board]
  (let [coord (find-empty-point board)
        is-complete (not coord)]
    (if is-complete
      ;; No points unfilled, we're done
      [board]
      ;; We have an unfilled point, our solution is incomplete
      ;; so search all possible boards with this point filled
      (let [poss-vals (valid-values-for board coord)]
        (for [val poss-vals
              poss-board [(set-value-at board coord val)]
              next (possible-boards poss-board)]
          next)))))

(defn solve
  "Return the solution for the board."
  [board]
  (->> board
       possible-boards
       (filter valid-solution?)
       first))
