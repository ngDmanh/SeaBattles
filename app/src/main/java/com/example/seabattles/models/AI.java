package com.example.seabattles.models;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class AI {
    private String Mode;
    private List<Position> shipHits;
    private List<Position> valueHits;
    private ArrayDeque<Position> highValueHits;

    public AI(String mode, int width, int length) {
        Mode = mode;
        valueHits = createNewValueHits(width, length);
        highValueHits = new ArrayDeque<>();

    }

    public List<Position> createNewValueHits(int width, int length){
        List<Position> newValueHits = new ArrayList<Position>();
        for(int i = 0; i < width; i++){
            for(int j = 0; j < length; j++){
                newValueHits.add(new Position(j, i));
            }
        }
        return newValueHits;
    }

    public Position rand(){
        Random random = new Random();
        return valueHits.remove(random.nextInt(valueHits.size()));
    }

    public void resetShipHits(){
        shipHits.clear();
    }

    public List<Position> getValueNeighbours(Position position){
        List<Position> valueNeighbours = new ArrayList<Position>();
        if(valueHits.contains(position.getNeibourLeft())){
            valueNeighbours.add(position.getNeibourLeft());
        }
        if(valueHits.contains(position.getNeibourRight())){
            valueNeighbours.add(position.getNeibourRight());
        }
        if(valueHits.contains(position.getNeibourUp())){
            valueNeighbours.add(position.getNeibourUp());
        }
        if(valueHits.contains(position.getNeibourDown())){
            valueNeighbours.add(position.getNeibourDown());
        }
        return valueNeighbours;
    }

    public void addHighValueHits(Position hittedPosition){
        List<Position> neighbours = getValueNeighbours(hittedPosition);
        highValueHits.addAll(neighbours);
    }

    public void resetHighValueHits() {
        highValueHits.clear();
    }

    public void removeHighValueHitsForShip(List<Position> shipPositions) {
        List<Position> positionsToRemove = new ArrayList<>();
        for (Position shipPos : shipPositions) {
            // Lấy các ô lân cận của mỗi vị trí trên tàu
            List<Position> neighbours = new ArrayList<>();
            Position left = shipPos.getNeibourLeft();
            Position right = shipPos.getNeibourRight();
            Position up = shipPos.getNeibourUp();
            Position down = shipPos.getNeibourDown();
            if (valueHits.contains(left) || highValueHits.contains(left)) neighbours.add(left);
            if (valueHits.contains(right) || highValueHits.contains(right)) neighbours.add(right);
            if (valueHits.contains(up) || highValueHits.contains(up)) neighbours.add(up);
            if (valueHits.contains(down) || highValueHits.contains(down)) neighbours.add(down);
            positionsToRemove.addAll(neighbours);
        }
        // Xóa các vị trí lân cận khỏi highValueHits
        highValueHits.removeAll(positionsToRemove);
    }

    public List<Position> getValueHits() {
        return valueHits;
    }

    public void setValueHits(List<Position> valueHits) {
        this.valueHits = valueHits;
    }

    public void hitShip(Position position){
        shipHits.add(position);
    }

    public List<Position> shipHits() {
        return shipHits;
    }

    public AI setShipHits(List<Position> shipHits) {
        this.shipHits = shipHits;
        return this;
    }

    public ArrayDeque<Position> getHighValueHits() {
        return highValueHits;
    }

    public void setHighValueHits(ArrayDeque<Position> highValueHits) {
        this.highValueHits = highValueHits;
    }
}
