(ns epic-sax-marathon.sudoku-solver-test
  (:require [epic-sax-marathon.sudoku-solver :as sut]
            [clojure.test :refer [deftest testing is]]))

(def sudoku-board
  (sut/board [[5 3 0 0 7 0 0 0 0]
              [6 0 0 1 9 5 0 0 0]
              [0 9 8 0 0 0 0 6 0]
              [8 0 0 0 6 0 0 0 3]
              [4 0 0 8 0 3 0 0 1]
              [7 0 0 0 2 0 0 0 6]
              [0 6 0 0 0 0 2 8 0]
              [0 0 0 4 1 9 0 0 5]
              [0 0 0 0 8 0 0 7 9]]))

(def solved-board
  (sut/board [[5 3 4 6 7 8 9 1 2]
              [6 7 2 1 9 5 3 4 8]
              [1 9 8 3 4 2 5 6 7]
              [8 5 9 7 6 1 4 2 3]
              [4 2 6 8 5 3 7 9 1]
              [7 1 3 9 2 4 8 5 6]
              [9 6 1 5 3 7 2 8 4]
              [2 8 7 4 1 9 6 3 5]
              [3 4 5 2 8 6 1 7 9]]))

(deftest value-at
  (testing "Should return value at 2d coords in a 9x9 board"
    (is (= 3 (sut/value-at sudoku-board [0 1])))
    (is (= 5 (sut/value-at sudoku-board [0 0])))))

(deftest has-value?
  (testing "Should return false if value is 0"
    (is (= false (sut/has-value? sudoku-board [0 2]))))
  (testing "Should return true if value is not 0"
    (is (= true (sut/has-value? sudoku-board [0 0])))))

(deftest row-values
  (testing "Should give a set values at given row"
    (is (= #{0 3 5 7} (sut/row-values sudoku-board [0 2])))
    (is (= #{0 3 6 8} (sut/row-values sudoku-board [3 2])))))

(deftest col-values
  (testing "Should give a set values at given col"
    (is (= #{0 8} (sut/col-values sudoku-board [0 2])))
    (is (= #{0 1 3 5 6 9} (sut/col-values sudoku-board [4 8])))))

(deftest coord-pairs
  (testing "Should return all coords where row or col are in sequence."
    (is (= [[0 0] [0 1]
            [1 0] [1 1]]
           (sut/coord-pairs [0 1])))
    (is (= [[0 0] [0 1] [0 2]
            [1 0] [1 1] [1 2]
            [2 0] [2 1] [2 2]]
           (sut/coord-pairs [0 1 2])))))

(deftest block-origin
  (testing "Should return the top-left coords for block at given coords."
    (is (= [0 0] (sut/block-origin sudoku-board [1 2])))
    (is (= [3 0] (sut/block-origin sudoku-board [3 0])))
    (is (= [3 0] (sut/block-origin sudoku-board [4 2])))
    (is (= [6 3] (sut/block-origin sudoku-board [8 5])))))

(deftest block-coords
  (testing "Should return every coord in the block"
    (is (= [[0 0] [0 1] [0 2]
            [1 0] [1 1] [1 2]
            [2 0] [2 1] [2 2]]
           (sut/block-coords sudoku-board [2 1])))
    (is (= [[3 6] [3 7] [3 8]
            [4 6] [4 7] [4 8]
            [5 6] [5 7] [5 8]]
           (sut/block-coords sudoku-board [4 6])))))

(deftest block-values
  (testing "Should get set of numbers in the block at coordinates."
    (is (= #{0 5 3 6 8 9}
           (sut/block-values sudoku-board [0 2])))
    (is (= #{0 6 8 3 2}
           (sut/block-values sudoku-board [4 5])))))

(deftest valid-values-for
  (testing "Should return empty set if coordinates not empty."
    (is (= #{} (sut/valid-values-for sudoku-board [0 0]))))
  (testing "Should get all the numbers not yet used in block or col."
    (is (= #{1 2 4} (sut/valid-values-for sudoku-board [0 2])))))

(deftest filled?
  (testing "Should be false if board is not solved."
    (is (= false (sut/filled? sudoku-board))))
  (testing "Should be true if board is solved."
    (is (= true (sut/filled? solved-board)))))

(deftest rows
  (testing "Should return set for each row."
    (is (= [#{5 3 0 7}
            #{6 0 1 9 5}
            #{0 9 8 6}
            #{8 0 6 3}
            #{4 0 8 3 1}
            #{7 0 2 6}
            #{0 6 2 8}
            #{0 4 1 9 5}
            #{0 8 7 9}]
           (sut/rows sudoku-board)))
    (is (=
         [#{1 2 3 4 5 6 7 8 9}
          #{1 2 3 4 5 6 7 8 9}
          #{1 2 3 4 5 6 7 8 9}
          #{1 2 3 4 5 6 7 8 9}
          #{1 2 3 4 5 6 7 8 9}
          #{1 2 3 4 5 6 7 8 9}
          #{1 2 3 4 5 6 7 8 9}
          #{1 2 3 4 5 6 7 8 9}
          #{1 2 3 4 5 6 7 8 9}]
         (sut/rows solved-board)))))

(deftest cols
  (testing "Should return set for each row."
    (is (= [#{5 6 0 8 4 7}
            #{3 0 9 6}
            #{0 8}
            #{0 1 8 4}
            #{7 9 0 6 2 1 8}
            #{0 5 3 9}
            #{0 2}
            #{0 6 8 7}
            #{0 3 1 6 5 9}]
           (sut/cols sudoku-board)))
    (is (=
         [#{1 2 3 4 5 6 7 8 9}
          #{1 2 3 4 5 6 7 8 9}
          #{1 2 3 4 5 6 7 8 9}
          #{1 2 3 4 5 6 7 8 9}
          #{1 2 3 4 5 6 7 8 9}
          #{1 2 3 4 5 6 7 8 9}
          #{1 2 3 4 5 6 7 8 9}
          #{1 2 3 4 5 6 7 8 9}
          #{1 2 3 4 5 6 7 8 9}]
         (sut/cols solved-board)))))

(deftest block-origins
  (testing "Should get origin coords for each block."
    (is (= [[0 0] [0 3] [0 6]
            [3 0] [3 3] [3 6]
            [6 0] [6 3] [6 6]]
           (sut/block-origins 3)))))

(deftest blocks
  (testing "Should return set for each block."
    (is (= [#{5 3 0 6 9 8}
            #{0 7 1 9 5}
            #{0 6}
            #{8 0 4 7}
            #{0 6 8 3 2}
            #{0 3 1 6}
            #{0 6}
            #{0 4 1 9 8}
            #{2 8 0 5 7 9}]
           (sut/blocks sudoku-board)))
    (is (= [#{1 2 3 4 5 6 7 8 9}
            #{1 2 3 4 5 6 7 8 9}
            #{1 2 3 4 5 6 7 8 9}
            #{1 2 3 4 5 6 7 8 9}
            #{1 2 3 4 5 6 7 8 9}
            #{1 2 3 4 5 6 7 8 9}
            #{1 2 3 4 5 6 7 8 9}
            #{1 2 3 4 5 6 7 8 9}
            #{1 2 3 4 5 6 7 8 9}]
           (sut/blocks solved-board)))))

(deftest valid-rows?
  (testing "Should be truthy for a solved board."
    (is (sut/valid-rows? solved-board)))
  (testing "Should be falsey for an invalid board."
    (is (not (sut/valid-rows? sudoku-board)))))

(deftest valid-cols?
  (testing "Should be truthy for a solved board."
    (is (sut/valid-cols? solved-board)))
  (testing "Should be falsey for an invalid board."
    (is (not (sut/valid-cols? sudoku-board)))))

(deftest valid-blocks?
  (testing "Should be truthy for a solved board."
    (is (sut/valid-blocks? solved-board)))
  (testing "Should be falsey for an invalid board."
    (is (not (sut/valid-blocks? sudoku-board)))))

(deftest valid-solution?
  (testing "Should be truthy for a solved board."
    (is (sut/valid-solution? solved-board)))
  (testing "Should be falsy for an invalid board."
    (is (not (sut/valid-solution? sudoku-board)))))

(deftest set-value-at
  (testing "Should update board with new value"
    (let [before-change (sut/board [[5 3 0 0 7 0 0 0 0]
                                    [6 0 0 1 9 5 0 0 0]
                                    [0 9 8 0 0 0 0 6 0]
                                    [8 0 0 0 6 0 0 0 3]
                                    [4 0 0 8 0 3 0 0 1]
                                    [7 0 0 0 2 0 0 0 6]
                                    [0 6 0 0 0 0 2 8 0]
                                    [0 0 0 4 1 9 0 0 5]
                                    [0 0 0 0 8 0 0 7 9]])
          after-change (sut/board [[5 3 0 0 7 0 0 0 0]
                                   [6 0 0 1 9 5 0 0 0]
                                   [0 4 8 0 0 0 0 6 0]
                                   [8 0 0 0 6 0 0 0 3]
                                   [4 0 0 8 0 3 0 0 1]
                                   [7 0 0 0 2 0 0 0 6]
                                   [0 6 0 0 0 0 2 8 0]
                                   [0 0 0 4 1 9 0 0 5]
                                   [0 0 0 0 8 0 0 7 9]])]
      (is (= after-change (sut/set-value-at before-change [2 1] 4))))))

(deftest find-empty-point
  (testing "Should return empty list if board is filled."
    (is (= nil (sut/find-empty-point solved-board))))
  (testing "Should return first empty point in board."
    (is (= [0 2] (sut/find-empty-point sudoku-board)))))

(deftest solve
  (testing "Should solve the board!"
    (is (= solved-board (sut/solve sudoku-board)))))
