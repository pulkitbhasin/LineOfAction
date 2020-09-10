/* Skeleton Copyright (C) 2015, 2020 Paul N. Hilfinger and the Regents of the
 * University of California.  All rights reserved. */
package loa;

import java.util.Collections;

import static loa.Piece.*;

/** An automated Player.
 *  @author Pulkit Bhasin
 */
class MachinePlayer extends Player {

    /** A position-score magnitude indicating a win (for white if positive,
     *  black if negative). */
    private static final int WINNING_VALUE = Integer.MAX_VALUE - 20;
    /** A magnitude greater than a normal value. */
    private static final int INFTY = Integer.MAX_VALUE;

    /** A new MachinePlayer with no piece or controller (intended to produce
     *  a template). */
    MachinePlayer() {
        this(null, null);
    }

    /** A MachinePlayer that plays the SIDE pieces in GAME. */
    MachinePlayer(Piece side, Game game) {
        super(side, game);
    }

    @Override
    String getMove() {
        Move choice;

        assert side() == getGame().getBoard().turn();
        int depth;
        choice = searchForMove();
        getGame().reportMove(choice);
        return choice.toString();
    }

    @Override
    Player create(Piece piece, Game game) {
        return new MachinePlayer(piece, game);
    }

    @Override
    boolean isManual() {
        return false;
    }

    /** Return a move after searching the game tree to DEPTH>0 moves
     *  from the current position. Assumes the game is not over. */
    private Move searchForMove() {
        Board work = new Board(getBoard());
        int value;
        assert side() == work.turn();
        _foundMove = null;
        if (side() == WP) {
            value = findMove(work, chooseDepth(), true, 1, -INFTY, INFTY);
        } else {
            value = findMove(work, chooseDepth(), true, -1, -INFTY, INFTY);
        }
        return _foundMove;
    }

    /** Find a move from position BOARD and return its value, recording
     *  the move found in _foundMove iff SAVEMOVE. The move
     *  should have maximal value or have value > BETA if SENSE==1,
     *  and minimal value or value < ALPHA if SENSE==-1. Searches up to
     *  DEPTH levels.  Searching at level 0 simply returns a static estimate
     *  of the board value and does not set _foundMove. If the game is over
     *  on BOARD, does not set _foundMove. */
    private int findMove(Board board, int depth, boolean saveMove,
                         int sense, int alpha, int beta) {

        if (depth == 0 || board.gameOver()) {
            return (int) heuristic(board, board.turn());
        }
        int bestScore;
        if (sense == -1) {
            bestScore = INFTY;
        } else {
            bestScore = -INFTY;
        }
        for (Move move : board.legalMoves()) {
            Board updatedBoard = new Board(board);
            updatedBoard.makeMove(move);
            int score = findMove(updatedBoard, depth
                    - 1, false, -sense, alpha, beta);
            if (sense == 1 && score > bestScore) {
                bestScore = score;
                if (saveMove) {
                    _foundMove = move;
                }
            } else if (score < bestScore) {
                bestScore = score;
                if (saveMove) {
                    _foundMove = move;
                }
            }
            if (sense == 1) {
                alpha = Math.max(score, alpha);
            } else {
                beta = Math.min(score, beta);
            }
            if (alpha >= beta) {
                return score;
            }
        }
        return bestScore;
    }

    /** Heuristic function to evaluate the board.
     * @param board "board to evaluated"
     * @param turn "piece whose turn it currently is"
     * @return val "static value to be returned"
     * */
    private double heuristic(Board board, Piece turn) {
        final double pulkit = 0.5;
        final double urjasvi = 1.2;
        int turn1 = board.getRegionSizes(turn.opposite()).size();
        int turn2 = board.getRegionSizes(turn).size();
        int max1 =  Collections.max(board.getRegionSizes(turn));
        int max2 =  Collections.max(board.getRegionSizes(turn.opposite()));
        int factor1 = turn1  - turn2;
        int factor2 = max1 - max2;
        double val = pulkit * factor2 + urjasvi * factor1;
        return val;
    }

    /** Return a search depth for the current position. */
    private int chooseDepth() {
        return 3;
    }

    /** Used to convey moves discovered by findMove. */
    private Move _foundMove;

}
