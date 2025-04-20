package com.example.seabattles;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.seabattles.models.AI;
import com.example.seabattles.models.MusicPlayer;
import com.example.seabattles.models.Position;
import com.example.seabattles.models.Ship;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class ShipLoading extends AppCompatActivity {
    private GridView Bot, P1;
    private Button btnDirection, btnRanDom;
    private AI myAI;
    private List<Ship> P1Ships;
    private List<Ship> BotShips;
    private Button btnBack;
    private boolean turn = true;
    private boolean isPlacingShips = true;
    private final int[] shipSizes = {5, 4, 3, 3, 2};
    static final int Width = 10;
    static final int Length = 10;
    private int currentShipIndex = 0;
    private List<Position> currentShipPositions;
    private boolean isHorizontal = true;
    private String[] rowsBot;
    private String[] rowsP1;
    private ArrayAdapter<String> adapterBot;
    private ArrayAdapter<String> adapterP1;
    private Random random;
    private String mode;

    public void init() {
        Bot = findViewById(R.id.Bot);
        P1 = findViewById(R.id.P1);
        btnDirection = findViewById(R.id.btndirection);
        btnBack = findViewById(R.id.btnBack);
        btnRanDom = findViewById(R.id.btnRanDom);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ship_loading);

        init();
        Intent i = getIntent();
        mode = i.getStringExtra("DIFFICULTY");
        random = new Random();
        myAI = new AI(Width, Length);

        P1Ships = new ArrayList<>();
        currentShipPositions = new ArrayList<>();

        BotShips = generateRandomShips(Width, Length, shipSizes, random);
        if (BotShips.isEmpty()) {
            Toast.makeText(this, "Không thể tạo tàu cho AI!", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        Bot.setNumColumns(Length);
        rowsBot = new String[Width * Length];
        Arrays.fill(rowsBot, "");
        adapterBot = new ArrayAdapter<>(this, R.layout.item, rowsBot);
        Bot.setAdapter(adapterBot);
        Bot.setEnabled(false);

        // Thiết lập GridView cho người chơi
        P1.setNumColumns(Length);
        rowsP1 = new String[Width * Length];
        Arrays.fill(rowsP1, "");
        adapterP1 = new ArrayAdapter<>(this, R.layout.item, rowsP1);
        P1.setAdapter(adapterP1);
        Toast.makeText(this, "Đặt tàu kích thước " + shipSizes[currentShipIndex], Toast.LENGTH_SHORT).show();

        P1.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                MusicPlayer.getInstance().playClickSound();
                if (!isPlacingShips) return;
                int shipSize = shipSizes[currentShipIndex];
                int row = position / Length;
                int col = position % Length;
                List<Position> newPositions = new ArrayList<>();

                boolean canPlace = true;
                if (isHorizontal) {
                    if (col + shipSize > Length) return;
                    for (int i = 0; i < shipSize; i++) {
                        int pos = row * Length + (col + i);
                        if (rowsP1[pos].equals("s")) {
                            canPlace = false;
                            break;
                        }
                        newPositions.add(new Position(col + i, row));
                    }
                } else {
                    if (row + shipSize > Width) return;
                    for (int i = 0; i < shipSize; i++) {
                        int pos = (row + i) * Length + col;
                        if (rowsP1[pos].equals("s")) {
                            canPlace = false;
                            break;
                        }
                        newPositions.add(new Position(col, row + i));
                    }
                }

                if (canPlace) {
                    for (Position pos : currentShipPositions) {
                        int posIndex = pos.y() * Length + pos.x();
                        rowsP1[posIndex] = "";
                    }
                    currentShipPositions.clear();

                    for (int i = 0; i < newPositions.size(); i++) {
                        Position pos = newPositions.get(i);
                        int posIndex = pos.y() * Length + pos.x();
                        rowsP1[posIndex] = "s";
                        currentShipPositions.add(pos);

                        View cellView = P1.getChildAt(posIndex);
                        if (cellView != null) {
                            if (i == 0) {
                                // Ô đầu tàu
                                cellView.setBackgroundResource(isHorizontal ? R.drawable.ship_head_horizontal : R.drawable.ship_head_vertical);
                            } else if(i == newPositions.size()-1) {
                                // Ô thân tàu
                                cellView.setBackgroundResource(isHorizontal ? R.drawable.ship_tail_horizontal : R.drawable.ship_tail_vertical);
                            } else{
                                cellView.setBackgroundResource(R.drawable.ship_body);
                            }
                        }
                    }

                    if (currentShipPositions.size() == shipSize) {
                        P1Ships.add(new Ship(new ArrayList<>(currentShipPositions), isHorizontal));
                        currentShipPositions.clear();
                        currentShipIndex++;

                        if (currentShipIndex < shipSizes.length) {
                            Toast.makeText(ShipLoading.this, "Đặt tàu kích thước " + shipSizes[currentShipIndex], Toast.LENGTH_SHORT).show();
                        } else {
                            isPlacingShips = false;
                            P1.setEnabled(false);
                            Bot.setEnabled(true);
                            btnDirection.setText("SẴN SÀNG!!!");
                            btnDirection.setTextColor(Color.WHITE);
                            btnDirection.setEnabled(false);
                            setupGamePlay(rowsBot, adapterBot, Width, Length);
                        }
                    }
                }
            }
        });

        btnDirection.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MusicPlayer.getInstance().playClickSound();
                isHorizontal = !isHorizontal;
                btnDirection.setText("Hướng: " + (isHorizontal ? "Ngang" : "Dọc"));
            }
        });

        btnRanDom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MusicPlayer.getInstance().playClickSound();
                if (isPlacingShips) {
                    for (Ship ship : P1Ships) {
                        List<Position> positions = ship.shipPosition();
                        for (int i = 0; i < positions.size(); i++) {
                            Position pos = positions.get(i);
                            int posIdx = pos.y() * Length + pos.x();
                            View cellView = P1.getChildAt(posIdx);
                            cellView.setBackgroundResource(R.drawable.item_shape);
                        }
                    }
                    P1Ships.clear();
                    currentShipPositions.clear();
                    Arrays.fill(rowsP1, "");

                    P1Ships = generateRandomShips(Width, Length, shipSizes, random);
                    if (P1Ships.isEmpty()) {
                        Toast.makeText(ShipLoading.this, "Không thể xếp tàu, thử lại!", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    for (Ship ship : P1Ships) {
                        List<Position> positions = ship.shipPosition();
                        boolean isHorizontal = ship.isHorizontal();

                        for (int i = 0; i < positions.size(); i++) {
                            Position pos = positions.get(i);
                            int posIndex = pos.y() * Length + pos.x();
                            rowsP1[posIndex] = "s";

                            View cellView = P1.getChildAt(posIndex);
                            if (cellView != null) {
                                if (i == 0) {
                                    // Ô đầu tàu
                                    cellView.setBackgroundResource(isHorizontal ? R.drawable.ship_head_horizontal : R.drawable.ship_head_vertical);
                                } else if (i == positions.size() - 1) {
                                    // Ô cuối tàu
                                    cellView.setBackgroundResource(isHorizontal ? R.drawable.ship_tail_horizontal : R.drawable.ship_tail_vertical);
                                } else {
                                    // Ô giữa tàu
                                    cellView.setBackgroundResource(R.drawable.ship_body);
                                }
                            }
                        }
                    }

                    isPlacingShips = false;
                    P1.setEnabled(false);
                    Bot.setEnabled(true);
                    btnDirection.setText("SẴN SÀNG!!!");
                    btnDirection.setTextColor(Color.WHITE);
                    btnDirection.setEnabled(false);
                    btnRanDom.setEnabled(false);
                    btnRanDom.setTextColor(Color.WHITE);
                    currentShipIndex = shipSizes.length;
                    setupGamePlay(rowsBot, adapterBot, Width, Length);

                    Toast.makeText(ShipLoading.this, "Đã xếp ngẫu nhiên tàu!", Toast.LENGTH_SHORT).show();
                }
            }
        });

        btnBack.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                MusicPlayer.getInstance().playClickSound();
                finish();
                return false;
            }
        });
    }

    // Hàm tạo tàu ngẫu nhiên
    private List<Ship> generateRandomShips(int width, int length, int[] shipSizes, Random random) {
        List<Ship> ships = new ArrayList<>();
        List<Position> usedPositions = new ArrayList<>();

        for (int shipSize : shipSizes) {
            boolean placed = false;
            int maxAttempts = 100;

            while (!placed && maxAttempts > 0) {
                boolean isHorizontal = random.nextBoolean();
                List<Position> shipPositions = new ArrayList<>();
                int startX, startY;

                if (isHorizontal) {
                    startX = random.nextInt(length - shipSize + 1);
                    startY = random.nextInt(width);
                } else {
                    startX = random.nextInt(length);
                    startY = random.nextInt(width - shipSize + 1);
                }

                boolean canPlace = true;
                for (int i = 0; i < shipSize; i++) {
                    int x = isHorizontal ? startX + i : startX;
                    int y = isHorizontal ? startY : startY + i;
                    Position pos = new Position(x, y);

                    if (usedPositions.contains(pos)) {
                        canPlace = false;
                        break;
                    }
                    shipPositions.add(pos);
                }

                if (canPlace) {
                    Ship newShip = new Ship(shipPositions, isHorizontal);
                    ships.add(newShip);
                    usedPositions.addAll(shipPositions);
                    placed = true;
                }
                maxAttempts--;
            }

            if (!placed) {
                ships.clear();
                return ships;
            }
        }
        return ships;
    }

    private void setupGamePlay(String[] rowsBot, ArrayAdapter<String> adapterBot, int Width, int Length) {
        Bot.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                MusicPlayer.getInstance().playClickSound();
                View cellView = Bot.getChildAt(position);
                if (turn && !isPlacingShips) {
                    if (!rowsBot[position].isEmpty()) {
                        return;
                    }
                    Position hitPosition = new Position(position % Length, position / Length);
                    if (hitted(hitPosition, BotShips)) {
                        cellView.setBackgroundResource(R.drawable.hit);
                        rowsBot[position] = "O";
                    } else {
                        cellView.setBackgroundResource(R.drawable.miss);
                        rowsBot[position] = "X";
                        turn = false;
                        Bot.setEnabled(false);
                        autoShot(rowsP1, adapterP1, P1Ships, myAI, Length);
                    }
                    if (Wining(BotShips, rowsBot, Length)) {
                        showWinDialog("VICTORY");
                    }
                }
            }
        });
    }

    public boolean hitted(Position hitPosition, List<Ship> ships) {
        for (Ship a : ships) {
            if (a.hitted(hitPosition)) {
                return true;
            }
        }
        return false;
    }

    public void autoShot(String[] rowsP1, ArrayAdapter<String> adapterP1, List<Ship> P1Ships, AI myAI, int Length) {
        final Handler handler = new Handler(Looper.getMainLooper());
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                Position AIShot = null;
                if ((mode.equals("NORMAL") || mode.equals("HARD")) && !myAI.getHighValueHits().isEmpty()) {
                    AIShot = myAI.getHighValueHits().pollFirst();
                    myAI.getValueHits().remove(AIShot);
                } else{
                    AIShot = myAI.rand();
                }
                int position = AIShot.x() + AIShot.y() * Length;
                View cellView = P1.getChildAt(position);

                if (hitted(AIShot, P1Ships)) {
                    MusicPlayer.getInstance().playClickSound();
                    cellView.setBackgroundResource(R.drawable.hit);
                    rowsP1[position] = "O";
                    myAI.hitShip(AIShot);
                    if (mode.equals("NORMAL")) {
                        myAI.addHighValueHits(AIShot);
                    } else if (mode.equals("HARD")){
                        // Kiểm tra xem có tàu nào chìm không
                        Ship sunkShip = getSunkShip(P1Ships, myAI.shipHits());
                        if (sunkShip != null) {
                            // Tàu chìm: xóa vị trí của tàu khỏi shipHits
                            myAI.removeShipPositions(sunkShip.shipPosition());
                            myAI.setCurrentDirection(null);
                        } else {
                            // Tàu chưa chìm: thêm ô lân cận thông minh
                            if (myAI.shipHits().size() == 1) {
                                // Trúng ô đầu tiên: thêm tất cả ô lân cận
                                myAI.addHighValueHits(AIShot);
                                myAI.setCurrentDirection(null);
                            } else if (myAI.shipHits().size() >= 2) {
                                // Trúng ít nhất 2 ô: xác định hướng và thêm ô tiếp theo
                                Position lastHit = myAI.shipHits().get(myAI.shipHits().size() - 1);
                                Position secondLastHit = myAI.shipHits().get(myAI.shipHits().size() - 2);
                                String direction = myAI.getCurrentDirection();

                                if (direction == null) {
                                    direction = myAI.getDirection(secondLastHit, lastHit);
                                    myAI.setCurrentDirection(direction);
                                }

                                if (direction != null) {
                                    // Thêm ô tiếp theo theo hướng hiện tại
                                    Position nextPos = myAI.getNextPosition(lastHit, direction);
                                    if (nextPos != null && myAI.getValueHits().contains(nextPos)) {
                                        myAI.getHighValueHits().addFirst(nextPos);
                                    } else {
                                        // Nếu trượt hướng này, thử hướng ngược lại
                                        String oppositeDirection = myAI.getOppositeDirection(direction);
                                        Position oppositePos = myAI.getNextValidPosition(secondLastHit, oppositeDirection);
                                        if (oppositePos != null && myAI.getValueHits().contains(oppositePos)) {
                                            myAI.getHighValueHits().addFirst(oppositePos);
                                            myAI.setCurrentDirection(oppositeDirection);
                                            if (myAI.getValueHits().contains(oppositePos)) {
                                                myAI.getHighValueHits().addFirst(oppositePos);
                                            }
                                        }
                                    }
                                }
                                else{
                                    myAI.addHighValueHits(AIShot);
                                }
                            }
                        }
                    }
                } else {
                    MusicPlayer.getInstance().playClickSound();
                    cellView.setBackgroundResource(R.drawable.miss);
                    rowsP1[position] = "X";
                    turn = true;
                    Bot.setEnabled(true);
                    if (mode.equals("HARD") && myAI.getCurrentDirection() != null) {
                        String direction = myAI.getCurrentDirection();
                        String oppositeDirection = myAI.getOppositeDirection(direction);
                        myAI.setCurrentDirection(oppositeDirection);
                        Position secondLastHit = myAI.shipHits().get(myAI.shipHits().size() - 1);
                        Position oppositePos = myAI.getNextValidPosition(secondLastHit, oppositeDirection);
                        if (oppositePos != null && myAI.getValueHits().contains(oppositePos)) {
                            myAI.getHighValueHits().addFirst(oppositePos);
                        }
                    }
                }

                if (Wining(P1Ships, rowsP1, Length)) {
                    showWinDialog("DEFEAT");
                }

                if (!turn) {
                    autoShot(rowsP1, adapterP1, P1Ships, myAI, Length);
                }
            }
        }, 500);
    }

    private Ship getSunkShip(List<Ship> ships, List<Position> shipHits) {
        for (Ship ship : ships) {
            List<Position> shipPositions = ship.shipPosition();
            if (shipHits.containsAll(shipPositions)) {
                return ship;
            }
        }
        return null;
    }

    private void showWinDialog(String message) {
        AlertDialog dialog = new AlertDialog.Builder(this)
                .setMessage(message)
                .setCancelable(false)
                .create();
        dialog.show();

        new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
            @Override
            public void run() {
                dialog.dismiss();
                finish();
            }
        }, 3000);
    }

    public boolean Wining(List<Ship> ships, String[] grid, int Length) {
        for (Ship s : ships) {
            if (!s.isSunk(grid, Length)) {
                return false;
            }
        }
        Bot.setEnabled(false);
        P1.setEnabled(false);
        return true;
    }

    @Override
    protected void onPause() {
        super.onPause();
        MusicPlayer.getInstance().pause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        MusicPlayer.getInstance().resume();
    }
}