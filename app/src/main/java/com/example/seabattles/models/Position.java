package com.example.seabattles.models;

import androidx.annotation.NonNull;

public class Position {
    private int x;
    private int y;

    public Position(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public int x() {
        return x;
    }

    public Position setX(int x) {
        this.x = x;
        return this;
    }

    public int y() {
        return y;
    }

    public Position setY(int y) {
        this.y = y;
        return this;
    }

    public Position getNeibourLeft(){
        return new Position(x-1, y);
    }

    public Position getNeibourRight(){
        return new Position(x+1, y);
    }

    public Position getNeibourUp(){
        return new Position(x, y+1);
    }

    public Position getNeibourDown(){
        return new Position(x, y-1);
    }

    @NonNull
    public String toString(){
        return "x: " + x + "; y: " +y;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Position position = (Position) obj;
        return x == position.x && y == position.y;
    }
}
