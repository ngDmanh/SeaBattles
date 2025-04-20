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
    private Button btnDirection;
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
    private String[] rowsP1;
    private ArrayAdapter<String> adapterP1;
    private Random random;
    private String mode;

    public void init() {
        Bot = findViewById(R.id.Bot);
        P1 = findViewById(R.id.P1);
        btnDirection = findViewById(R.id.btndirection);
        btnBack = findViewById(R.id.btnBack);
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ship_loading);

        init();
        Intent i = getIntent();
        mode = i.getStringExtra("DIFFICULTY");
        random = new Random();
        myAI = new AI(mode, Width, Length);

        P1Ships = new ArrayList<>(); // Khởi tạo danh sách tàu người chơi
        currentShipPositions = new ArrayList<>(); // Khởi tạo danh sách vị trí tạm

        // Đặt tàu ngẫu nhiên cho AI
        BotShips = generateRandomShips(Width, Length);

        // Thiết lập GridView cho AI
        Bot.setNumColumns(Length); // Đặt số cột cho lưới (10)
        final String[] rowsP1 = new String[Width * Length]; // Mảng lưu trạng thái ô lưới
        Arrays.fill(rowsP1, ""); // Khởi tạo lưới trống

        ArrayAdapter<String> adapterP1 = new ArrayAdapter<String>(this, R.layout.item, rowsP1) {};
        Bot.setAdapter(adapterP1);
        Bot.setEnabled(false);

        P1.setNumColumns(Length);
        this.rowsP1 = new String[Width * Length];
        Arrays.fill(this.rowsP1, "");
        this.adapterP1 = new ArrayAdapter<String>(this, R.layout.item, ShipLoading.this.rowsP1) {};
        P1.setAdapter(this.adapterP1);
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
                        if (ShipLoading.this.rowsP1[pos].equals("s")) {
                            canPlace = false;
                            break;
                        }
                        newPositions.add(new Position(col + i, row));
                    }
                } else {
                    if (row + shipSize > Width) return;
                    for (int i = 0; i < shipSize; i++) {
                        int pos = (row + i) * Length + col;
                        if (ShipLoading.this.rowsP1[pos].equals("s")) {
                            canPlace = false;
                            break;
                        }
                        newPositions.add(new Position(col, row + i));
                    }
                }

                if (canPlace) {
                    for (Position pos : currentShipPositions) {
                        int posIndex = pos.y() * Length + pos.x();
                        ShipLoading.this.rowsP1[posIndex] = "";
                    }
                    currentShipPositions.clear();

                    for (Position pos : newPositions) {
                        int posIndex = pos.y() * Length + pos.x();
                        ShipLoading.this.rowsP1[posIndex] = "s";
                        currentShipPositions.add(pos);
                    }
                    ShipLoading.this.adapterP1.notifyDataSetChanged();

                    if (currentShipPositions.size() == shipSize) {
                        P1Ships.add(new Ship(new ArrayList<>(currentShipPositions)));
                        currentShipPositions.clear();
                        currentShipIndex++;

                        if (currentShipIndex < shipSizes.length) {
                            Toast.makeText(ShipLoading.this, "Đặt tàu kích thước " + shipSizes[currentShipIndex], Toast.LENGTH_LONG).show();
                        } else {
                            isPlacingShips = false;
                            P1.setEnabled(false);
                            Bot.setEnabled(true);
                            btnDirection.setText("Tất cả tàu đã đặt! Bắt đầu trò chơi!");
                            btnDirection.setTextColor(Color.WHITE);
                            btnDirection.setEnabled(false);
                            setupGamePlay(rowsP1, adapterP1, Width, Length);
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
                btnDirection.setText("Đổi chiều xếp tàu: " + (isHorizontal ? "Ngang" : "Dọc"));
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

    // Tạo tàu ngẫu nhiên cho AI
    private List<Ship> generateRandomShips(int width, int length) {
        List<Ship> ships = new ArrayList<>();
        List<Position> usedPositions = new ArrayList<Position>();

        for (int shipSize : shipSizes) {
            boolean placed = false;
            while (!placed) {

                boolean isHorizontal = random.nextBoolean();
                List<Position> shipPositions = new ArrayList<Position>();

                int startX, startY;
                if (isHorizontal) {
                    startX = random.nextInt(length - shipSize + 1);
                    startY = random.nextInt(width);
                } else {
                    startX = random.nextInt(length);
                    startY = random.nextInt(width - shipSize + 1);
                }

                // Tạo vị trí cho tàu
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
                    ships.add(new Ship(shipPositions));
                    usedPositions.addAll(shipPositions);
                    placed = true;
                }
            }
        }
        return ships;
    }

    private void setupGamePlay(String[] rowsP1, ArrayAdapter<String> adapterP1, int Width, int Length) {
        Bot.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                MusicPlayer.getInstance().playClickSound();
                if (turn && !isPlacingShips) {
                    if (!rowsP1[position].isEmpty()) {
                        return;
                    }
                    Position hitPosition = new Position(position % Length, position / Length);
                    if (hitted(hitPosition, BotShips)) {
                        rowsP1[position] = "O";
                    } else {
                        rowsP1[position] = "X";
                        turn = false;
                        Bot.setEnabled(false);
                        autoShot(ShipLoading.this.rowsP1, ShipLoading.this.adapterP1, BotShips, myAI, Length);
                    }
                    if (Wining(BotShips, rowsP1, Length)) {
                        showWinDialog("VICTORI");
                    }
                    adapterP1.notifyDataSetChanged();
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

    // Logic bắn tự động của AI
    public void autoShot(String[] rowsP2, ArrayAdapter<String> adapterP2, List<Ship> P2Ships, AI myAI, int Length) {
        final Handler handler = new Handler(Looper.getMainLooper());
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                Position AIShot;
                if (mode.equals("HARD") && !myAI.getHighValueHits().isEmpty()) {
                    AIShot = myAI.getHighValueHits().pollFirst();
                    myAI.getValueHits().remove(AIShot);
                } else {
                    AIShot = myAI.rand();
                }
                int position = AIShot.x() + AIShot.y() * Length;

                if (hitted(AIShot, P1Ships)) {
                    MusicPlayer.getInstance().playClickSound();
                    rowsP2[position] = "O";
                    if (mode.equals("HARD")) {
                        myAI.addHighValueHits(AIShot);
                        for (Ship ship : P1Ships) {
                            if (ship.isSunk(rowsP2, Length) && ship.shipPosition().contains(AIShot)) {
                                myAI.removeHighValueHitsForShip(ship.shipPosition());
                                break;
                            }
                        }
                    }
                } else {
                    MusicPlayer.getInstance().playClickSound();
                    rowsP2[position] = "X";
                    turn = true;
                    Bot.setEnabled(true);
                }
                adapterP2.notifyDataSetChanged();

                if (Wining(P1Ships, rowsP2, Length)) {
                    showWinDialog("DEFEAT");
                }

                if (!turn) {
                    autoShot(rowsP2, adapterP2, P2Ships, myAI, Length);
                }
            }
        }, 1000);
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