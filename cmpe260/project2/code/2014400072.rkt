#lang scheme
; compiling: yes
; complete: yes
; 2014400072

; (card-color one-card) -> symbol?
; one-card : pair?   (the first element is a letter of card suit, the second element is a letter or a number of card rank)
;
; Returns the color of "one-card".
; If the first element of "one-card" is H or D, the color is red. Else (if it is S or C), the color is black.
;
;
; Examples:
; > (card-color '(H . 3))
; => red
; > (card-color '(S . A))
; => black
(define (card-color one-card)
        (cond ( (or (eqv? (car one-card) 'H) (eqv? (car one-card) 'D)) 'red )
              ( else 'black )   ; assuming the first element of "one-card" is S or C
        )
)



; (card-rank one-card) -> number?
; one-card : pair?   (the first element is a letter of card suit, the second element is a letter or a number of card rank)
;
; Returns the rank of "one-card".
; If the second element of "one-card" is A, the rank is 11.
; If the second element of "one-card" is K , Q or J, the rank is 10.
; Else (if the second element of "one-card" is a number), the rank is the second element of "one-card".
;
;
; Examples:
; > (card-rank '(H . Q))
; => 10
; > (card-rank '(S . 3))
; => 3
(define (card-rank one-card)
        (let ( (theRank (cdr one-card)) )
             (cond ( (eqv? theRank 'A) 11 )
                   ( (or (eqv? theRank 'K) (eqv? theRank 'Q) (eqv? theRank 'J)) 10 )
                   ( else theRank )   ; assuming the second element of "one-card" is a number
             )
        )
)



; (all-same-color list-of-cards) -> boolean?
; list-of-cards : list?   (its members are pairs)
;
; Returns #t if all the elements (cards) in "list-of-cards" have the same color, #f otherwise.
; When there is 0 or 1 element in "list-of-cards", it is assumed that all the cards have the same color.
;
; This function works in that way:
; If there is 0 or 1 element in "list-of-cards", returns #t.
; If there are 2 or more elements in "list-of-cards", compares the ith element with the (i+1)th element (1 <= i <= length-1)
; recursively until it finds a pair of adjacent cards that are of different color.
; If such a pair is found, returns #f, else returns #t.
;
;
; Examples:
; > (all-same-color '((H . Q) (D . A) (S . 3) (H . Q)))
; => #f
; > (all-same-color '((H . Q) (D . A) (H . 3) (H . Q)))
; => #t
(define (all-same-color list-of-cards)
        (cond ( (< (length list-of-cards) 2) #t )   ; base condition for recursion
              ( (equal? (card-color (car list-of-cards)) (card-color (cadr list-of-cards))) (all-same-color (cdr list-of-cards)) )
              ( else #f )
        )
)



; (fdraw list-of-cards held-cards) -> list?
; list-of-cards : list?   (its members are pairs)
; held-cards    : list?   (its members are pairs)
;
; Returns a new list of held cards after the action draw is taken.
;
; This function works in that way:
; If "list-of-cards" is empty, returns a copy of "held-cards".
; Else, creates a new list by adding the first element of "list-of-cards" into (the first position of) "held-cards".
;
;
; Examples:
; > (fdraw '((H . Q) (D . A) (S . 3) (H . Q)) '())
; => ((H . Q))
; > (fdraw '((D . A) (H . 3) (H . Q)) '((S . 3) (C . Q)))
; => ((D . A) (S . 3) (C . Q))
(define (fdraw list-of-cards held-cards)
        (cond ( (null? list-of-cards) held-cards )
              ( else (cons (car list-of-cards) held-cards) )
        )
)



; (fdiscard list-of-cards list-of-moves goal held-cards) -> list?
; list-of-cards : list?   (its members are pairs)
; list-of-moves : list?   (its members are 'draw or 'discard)
; goal          : number?
; held-cards    : list?   (its members are pairs)
;
; Returns a new list of held cards after the action discard is taken.
;
; Since my strategy is always discarding the card that has the least rank value,
; "list-of-moves" and "goal" are not used in this function.
;
; This function works in that way:
; If there is 0 or 1 element in "held-cards", returns the empty list.
; Else, it learns (from "minRankedCard" function) the card that has the least rank value
; in "held-cards" and creates a new list by removing that minimum-ranked card from "held-cards".
;
;
; Examples:
; > (fdiscard '((H . Q) (D . A) (S . 3)) '(draw discard) 10 '((S . 3) (C . Q)))
; => ((C . Q))
; > (fdiscard '((H . Q)) '(draw draw) 5 '((S . 8) (C . Q) (H . 2) (D . A)))
; => ((S . 8) (C . Q) (D . A))
(define (fdiscard list-of-cards list-of-moves goal held-cards)
        (cond ( (< (length held-cards) 2) '() )
              ( else (remove (minRankedCard held-cards) held-cards) )
        )
)


; (minRankedCard list-of-cards) -> pair?
; list-of-cards : list?   (its members are pairs)
;
; Returns a pair (card) which is the minimum-ranked card in "list-of-cards".
;
; This function works in that way:
; It assumes that "list-of-cards" is not empty.
; It uses built-in "foldl" function. The "proc" argument of "foldl" is a "lambda" function
; which takes two pairs as its arguments and returns the pair (card) that has lesser rank value.
; The "init" argument of "foldl" is the first element of "list-of-cards" and the "lst" argument of "foldl"
; is the tail of "list-of-cards". So, it compares the first element of "list-of-cards" with the second element
; of the list and keep the card that has the lesser rank. Then, it compares that card with the third element
; of "list-of-cards" and keep the one that has lesser rank. It continues like this until the end of "list-of-cards".
;
;
; Examples:
; > (minRankedCard '((S . 3) (C . Q)))
; => (S . 3)
; > (minRankedCard '((S . 8) (C . Q) (H . 2) (D . A)))
; => (H . 2)
(define (minRankedCard list-of-cards)
        (foldl (lambda (x y) (if (<= (card-rank x) (card-rank y)) x y)) ; "proc" argument of "foldl"
               (car list-of-cards) ; "init" argument of "foldl"
               (cdr list-of-cards) ; "lst" argument of "foldl"
        )
)



; (find-steps list-of-cards list-of-moves goal) -> list?
; list-of-cards : list?   (its members are pairs)
; list-of-moves : list?   (its members are 'draw or 'discard)
; goal          : number?
;
; Returns a list of steps that is a list of pairs of moves and corresponding cards along the game.
;
; This function works as the driver function for "findSteps" below.
;
;
; Examples:
; > (find-steps '((H . 3) (D .5)) '(draw draw draw) 2)
; => ((draw (H . 3)))
; > (find-steps '((H . 3) (D . 5) (S . A) (C . Q) (C . 1) (H . J)) '(draw discard draw discard draw draw) 25)
; => ((draw (H . 3)) (discard (H . 3)) (draw (D . 5)) (discard (D . 5)) (draw (S . A)) (draw (C . Q))
(define (find-steps list-of-cards list-of-moves goal)
        (findSteps list-of-cards list-of-moves goal '())
)


; (findSteps list-of-cards list-of-moves goal held-cards) -> list?
; list-of-cards : list?   (its members are pairs)
; list-of-moves : list?   (its members are 'draw or 'discard)
; goal          : number?
; held-cards    : list?   (its members are pairs, initially empty)
;
; Returns a list of steps that is a list of pairs of moves and corresponding cards along the game.
;
; This function works in that way:
; First, checks whether current player point (the sum of ranks in current "held-cards") is bigger than "goal".
; If so, returns the empty list (to the caller function).
;
; If not, checks whether "list-of-moves" is empty. If so, returns the empty list (to the caller function).
;
; If not, looks at the first element of "list-of-moves".
; If it is 'draw and if "list-of-cards" is empty, returns the empty list (to the caller function).
; If it is 'draw and if "list-of-cards" is not empty, creates a list whose first element is 'draw and
;                                                     second element is the first element of "list-of-cards".
;                                                     Then, inserts this list into (the first position of) the list
;                                                     that will be returned from "findSteps", which is called with those
;                                                     arguments: tail of "list-of-cards" , tail of "list-of-moves" , "goal"
;                                                                and list of held cards that is formed after the action
;                                                                draw is taken.
;
; If it is 'discard and if "held-cards" is empty, returns the empty list (to the caller function).
; If it is 'discard and if "held-cards" is not empty, creates a list whose first element is 'discard and
;                                                     second element is the minimum-ranked card in "held-cards".
;                                                     Then, inserts this list into (the first position of) the list
;                                                     that will be returned from "findSteps", which is called with those
;                                                     arguments: "list-of-cards" , tail of "list-of-moves" , "goal" and
;                                                                list of held cards that is formed after the minimum-ranked
;                                                                card is removed from "held-cards".
; 
;
; Examples:
; > (findSteps '((H . 3)) '(draw) 25 '())
; => ((draw (H . 3)))
; > (findSteps '((H . 3) (C . Q)) '(draw draw discard discard discard discard) 15 '())
; => ((draw (H . 3)) (draw (C . Q)) (discard (H . 3)) (discard (C . Q)))
(define (findSteps list-of-cards list-of-moves goal held-cards)
        (cond ( (> (calc-playerpoint held-cards) goal) '() ) ; base condition for recursion
              ( (null? list-of-moves) '() ) ; base condition for recursion
              ( (equal? (car list-of-moves) 'draw) (if (null? list-of-cards)
                                                       '()
                                                       (cons (list 'draw (car list-of-cards)) (findSteps (cdr list-of-cards) (cdr list-of-moves) goal (fdraw list-of-cards held-cards)))
                                                   )
                                                   
              )
              ( else (if (null? held-cards)   ; else: the move is 'discard'
                         '()
                         (let ( (minRankedCard (minRankedCard held-cards)) )
                              (cons (list 'discard minRankedCard) (findSteps list-of-cards (cdr list-of-moves) goal (remove minRankedCard held-cards)))
                         )
                     )
              )
        )
)



; (find-held-cards list-of-steps) -> list?
; list-of-steps : list?   (its members are lists in the form of (<moveName> <aCard(pair)>) )
;
; Returns the list of held cards after the "list-of-steps" is applied.
;
; This function works as the driver function for "findHeldCards" below.
;
;
; Examples:
; > (find-held-cards '((draw (C . Q)) (draw (H . 2)) (draw (D . J)) (discard (H . 2))))
; => ((D . J) (C . Q))
; > ( find-held-cards '((draw (C . Q)) (draw (H . 2)) (discard (C . Q)) (discard (H . 2))) )
; => ()
(define (find-held-cards list-of-steps)
        (findHeldCards list-of-steps '())
)


; (findHeldCards list-of-steps result-list) -> list?
; list-of-steps : list?   (its members are lists in the form of (<moveName> <aCard(pair)>) )
; result-list   : list?   (its members are pairs, initially empty)
;
; Returns the list of held cards after the "list-of-steps" is applied.
;
; This function works in that way:
; It works recursively and uses "result-list" as an accumulator. When "list-of-steps" becomes the empty list,
; "result-list" becomes equal to the list of held cars.
;
; If "list-of-steps" is empty, returns "result-list" (to the caller function).
; Else, looks at the first element of the first element of "list-of-steps" (which is a move name).
;
; If the move is 'draw, returns the result of "findHeldCards" which is called with those arguments:
;                       tail of "list-of-steps" and a list which is formed by adding the current card ("cadar list-of-steps")
;                                                   into the first position of "result-list".
;
; If the move is 'discard, returns the result of "findHeldCards" which is called with those arguments:
;                          tail of "list-of-steps" and a list which is formed by removing the current card ("cadar list-of-steps")
;                                                      from result-list.
;
;
; Examples:
; > (findHeldCards '((draw (C . Q)) (discard (C . Q)) (draw (H . A))) '())
; => ((H . A))
; > (findHeldCards '() '())
; => ()
(define (findHeldCards list-of-steps result-list)
        (cond ( (null? list-of-steps) result-list ) ; base condition for recursion
              ( (equal? (caar list-of-steps) 'draw) (findHeldCards (cdr list-of-steps) (cons (cadar list-of-steps) result-list)) )
              ( else (findHeldCards (cdr list-of-steps) (remove (cadar list-of-steps) result-list)) )
        )
)



; (calc-playerpoint list-of-cards) -> number?
; list-of-cards : list?   (its members are pairs)
;
; Calculates and returns the corresponding player point for "list-of-cards".
; Player point is the sum of the ranks of cards in "list-of-cards".
;
; This function works in that way:
; It assumes that the player point of the empty list is 0.
;
; It works recursively and returns the result which is obtained by adding the rank of the first element of "list-of-cards"
; to the result of "calc-playerpoint", which is called with the tail of "list-of-cards".
; 
;
; Examples:
; > (calc-playerpoint '())
; => 0
; > (calc-playerpoint '((C . Q) (D . J) (S . A) (H . 9)))
; => 40
(define (calc-playerpoint list-of-cards)
        (cond ( (null? list-of-cards) 0 ) ; base condition for recursion
              ( else (+ (card-rank (car list-of-cards)) (calc-playerpoint (cdr list-of-cards))) )
        )
)



; (calc-score list-of-cards goal) -> number?
; list-of-cards : list?   (its members are pairs)
; goal          : number?
;
; Calculates and returns the final score according to "list-of-cards" and "goal".
;
; This function works in that way:
; It defines 2 local variables in "let*" function: "playerPoint" and "preScore".
; playerPoint is the sum of ranks of the cards in "list-of-cards". It is calculated by the
; function "calc-playerPoint".
; preScore is calculated as following: if playerPoint > goal, preScore is 5*(playerPoint-goal)
;                                        else preScore is goal-playerPoint
;
; After preScore is calculated, the function checks for a last condition:
; If all the cards in "list-of-cards" have the same color, the function returns preScore/2 (rounded down) as the final score,
; else returns preScore as the final score.
; 
;
; Examples:
; > (calc-score '((C . Q) (D . J) (S . A) (H . 9)) 40)
; => 0
; > (calc-score '((C . Q) (S . J) (S . A) (C . K)) 30)
; => 27
(define (calc-score list-of-cards goal)
        (let* ( (playerPoint (calc-playerpoint list-of-cards))
                (preScore (cond ( (> playerPoint goal) (* 5 (- playerPoint goal)) )
                                ( else (- goal playerPoint) )
                          )
                )
              )
              (if (all-same-color list-of-cards)
                  (quotient preScore 2)
                  preScore
              )
        )
)



; (play list-of-cards list-of-moves goal) -> number?
; list-of-cards : list?   (its members are pairs)
; list-of-moves : list?   (its members are 'draw or 'discard)
; goal          : number?
;
; Returns the final score at the end of the game
; after processing (some or all of) the moves in "list-of-moves" in order.
;
; This function works in that way:
; Step 1: Gets a list of steps (list1) by calling "find-steps" with those arguments: "list-of-cards" , "list-of-moves" and "goal".
; Step 2: Gets a list of held cards (list2) by calling "find-held-cards" with those arguments: "list1" and "goal".
; Step 3: Gets a final score by calling "calc-score" with those arguments: "list2" and "goal".
; Step 4: Returns the final score calculated in Step 3.
; 
;
; Examples:
; > (play '((H . 3) (C . Q) (S . A) (D . J) (C . A) (C . 2)) '(draw draw draw discard) 16)
; => 40
; > (play '((H . 3) (C . Q) (S . A) (D . J) (C . A) (C . 2)) '(draw discard draw discard) 11)
; => 5
(define (play list-of-cards list-of-moves goal)
       (calc-score (find-held-cards (find-steps list-of-cards list-of-moves goal)) goal)
)