// Copyright (c) 2008-2015  Esko Luontola <www.orfjackal.net>
// You may use and modify this source code freely for personal non-commercial use.
// This source code may NOT be used as course material without prior written agreement.

package tetris;

import java.util.Arrays;

public class Board implements Grid {

    private final int rows;
    private final int columns;
    private char[][] stationary;

    private MovableGrid falling;

    public Board(int rows, int columns) {
        this.rows = rows;
        this.columns = columns;
        this.stationary = emptyBoard(rows, columns);
    }

    private static char[][] emptyBoard(int rows, int columns) {
        char[][] board = new char[rows][columns];
        for (char[] row : board) {
            Arrays.fill(row, EMPTY);
        }
        return board;
    }

    public String toString() {
        return Grid.toString(this);
    }

    @Override
    public int rows() {
        return rows;
    }

    @Override
    public int columns() {
        return columns;
    }

    @Override
    public char cellAt(int row, int col) {
        char cell = fallingCellAt(row, col);
        if (cell != EMPTY) {
            return cell;
        }
        return stationary[row][col];
    }

    private char fallingCellAt(int row, int col) {
        if (hasFalling()) {
            int pieceRow = row - falling.row;
            int pieceCol = col - falling.column;
            if (pieceRow >= 0
                    && pieceRow < falling.rows()
                    && pieceCol >= 0
                    && pieceCol < falling.columns()) {
                return falling.cellAt(pieceRow, pieceCol);
            }
        }
        return EMPTY;
    }

    public void drop(Grid piece) {
        if (hasFalling()) {
            throw new IllegalStateException("a block is already falling");
        }
        startFalling(piece);
    }

    public void tick() {
        if (fallingHitsFloor() || fallingHitsStationary()) {
            stopFalling();
        } else {
            moveDown();
        }
    }

    public boolean hasFalling() {
        return falling != null;
    }

    private void startFalling(Grid piece) {
        this.falling = new MovableGrid(this, piece);
    }

    private void stopFalling() {
        char[][] newStationary = new char[rows()][columns()];
        for (int row = 0; row < rows(); row++) {
            for (int col = 0; col < columns(); col++) {
                newStationary[row][col] = cellAt(row, col);
            }
        }
        stationary = newStationary;
        falling = null;
    }

    private boolean fallingHitsFloor() {
        for (int row = 0; row < falling.rows(); row++) {
            for (int col = 0; col < falling.columns(); col++) {
                if (falling.hasCellAt(row, col)) {
                    if (falling.row + row >= this.rows() - 1) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    private boolean fallingHitsStationary() {
        for (int row = 0; row < falling.rows(); row++) {
            for (int col = 0; col < falling.columns(); col++) {
                if (falling.hasCellAt(row, col)) {
                    int boardRow = falling.row + row;
                    int boardCol = falling.column + col;
                    if (stationary[boardRow + 1][boardCol] != EMPTY) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public void moveDown() {
        falling = falling.moveDown();
    }

    public void moveLeft() {
        falling = falling.moveLeft();
    }

    public void moveRight() {
        falling = falling.moveRight();
    }
}
