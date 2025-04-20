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
    private Position lastHit1; // Lần trúng đầu tiên trong chuỗi
    private Position lastHit2; // Lần trúng thứ hai trong chuỗi
    private String direction;

    public AI(String mode, int width, int length) {
        Mode = mode;
        valueHits = createNewValueHits(width, length);
        highValueHits = new ArrayDeque<Position>();
        shipHits = new ArrayList<Position>();
        lastHit1 = null;
        lastHit2 = null;
        direction = null;
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

    // Xác định hướng dựa trên hai lần trúng
    private void determineDirection(Position hit1, Position hit2) {
        if (hit1.x() == hit2.x() && Math.abs(hit1.y() - hit2.y()) == 1) {
            direction = "VERTICAL";
        } else if (hit1.y() == hit2.y() && Math.abs(hit1.x() - hit2.x()) == 1) {
            direction = "HORIZONTAL";
        } else {
            direction = null; // Các lần trúng không liền kề
        }
    }

    // Lấy vị trí tiếp theo để bắn theo hướng đã xác định
    public Position getNextDirectionalShot(Position lastHit) {
        if (direction == null || lastHit == null) return null;
        List<Position> candidates = new ArrayList<>();
        if (direction.equals("HORIZONTAL")) {
            Position left = lastHit.getNeibourLeft();
            Position right = lastHit.getNeibourRight();
            if (valueHits.contains(left)) candidates.add(left);
            if (valueHits.contains(right)) candidates.add(right);
        } else if (direction.equals("VERTICAL")) {
            Position up = lastHit.getNeibourUp();
            Position down = lastHit.getNeibourDown();
            if (valueHits.contains(up)) candidates.add(up);
            if (valueHits.contains(down)) candidates.add(down);
        }
        if (candidates.isEmpty()) {
            // Không có vị trí hợp lệ theo hướng này, thử đầu kia nếu có hai lần trúng
            if (lastHit2 != null && lastHit.equals(lastHit2)) {
                return getNextDirectionalShot(lastHit1);
            }
            return null;
        }
        Random random = new Random();
        return candidates.get(random.nextInt(candidates.size()));
    }

    // Cập nhật theo dõi lần trúng cho chế độ HARD
    public void updateHitTracking(Position hit) {
        if (lastHit1 == null) {
            lastHit1 = hit;
        } else if (lastHit2 == null) {
            lastHit2 = hit;
            determineDirection(lastHit1, lastHit2);
        } else {
            // Tiếp tục theo hướng, cập nhật lần trúng cuối
            lastHit2 = hit;
        }
        shipHits.add(hit);
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
