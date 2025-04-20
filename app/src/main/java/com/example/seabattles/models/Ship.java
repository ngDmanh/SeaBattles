package com.example.seabattles.models;

import android.util.Log;

import java.util.List;

public class Ship {
    private List<Position> shipPosition;

    public Ship(List<Position> shipPosition) {
        this.shipPosition = shipPosition;
    }

    public List<Position> shipPosition() {
        return shipPosition;
    }

    public Ship setShipPosition(List<Position> shipPosition) {
        this.shipPosition = shipPosition;
        return this;
    }


    public boolean hitted(Position hitPosition){
        return shipPosition.contains(hitPosition);
    }

    public String toString(){
        String str = "";
        for(Position a : shipPosition){
            str += a.toString() + "\t";
        }
        return str;
    }

    public boolean isSunk(String[] grid, int length) {
        for (Position pos : shipPosition) {
            int index = pos.x() + pos.y() * length;
            if (!grid[index].equals("O")) {
                return false;
            }
        }
        Log.d("isSunk", "ship " + shipPosition.size() + " is sunk");
        return true;
    }
}
