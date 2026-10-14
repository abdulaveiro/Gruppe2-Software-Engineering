package com.example.tennismanager;

import javafx.animation.KeyFrame;
import javafx.animation.PauseTransition;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Polygon;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.Serializable;
import java.util.*;

public class TennisManagerApp extends Application {

    // ----------------------------------------------------
    // ENUMS & ENTTITÄTEN
    // ----------------------------------------------------
    public enum Surface {
        CLAY("Sandplatz", "#b45309"),
        GRASS("Rasen", "#15803d"),
        HARD("Hartplatz", "#0284c7");

        final String name;
        final String hexColor;

        Surface(String name, String hexColor) {
            this.name = name;
            this.hexColor = hexColor;
        }
    }

    public enum TournamentCategory {
        ATP_250("ATP 250", 2, 250, 25000, 32, new String[]{"1. Runde (R32)", "Achtelfinale", "Viertelfinale", "Halbfinale", "Finale"}),
        ATP_500("ATP 500", 2, 500, 50000, 32, new String[]{"1. Runde (R32)", "Achtelfinale", "Viertelfinale", "Halbfinale", "Finale"}),
        ATP_1000("ATP Masters 1000", 2, 1000, 120000, 64, new String[]{"1. Runde (R64)", "2. Runde (R32)", "Achtelfinale", "Viertelfinale", "Halbfinale", "Finale"}),
        GRAND_SLAM("Grand Slam (BO5)", 3, 2000, 350000, 128, new String[]{"1. Runde (R128)", "2. Runde (R64)", "3. Runde (R32)", "Achtelfinale", "Viertelfinale", "Halbfinale", "Finale"});

        final String name;
        final int setsToWin;
        final int maxPoints;
        final double maxPrizeMoney;
        final int drawSize;
        final String[] rounds;

        TournamentCategory(String name, int setsToWin, int maxPoints, double maxPrizeMoney, int drawSize, String[] rounds) {
            this.name = name;
            this.setsToWin = setsToWin;
            this.maxPoints = maxPoints;
            this.maxPrizeMoney = maxPrizeMoney;
            this.drawSize = drawSize;
            this.rounds = rounds;
        }
    }

    public static class CalendarEvent {
        public int week;
        public String name;
        public TournamentCategory category;
        public Surface surface;

        public CalendarEvent(int week, String name, TournamentCategory category, Surface surface) {
            this.week = week;
            this.name = name;
            this.category = category;
            this.surface = surface;
        }
    }

    public static class RacketItem {
        public String id;
        public String name;
        public double price;
        public int powerBonus;
        public int controlBonus;
        public String desc;

        public RacketItem(String id, String name, double price, int powerBonus, int controlBonus, String desc) {
            this.id = id;
            this.name = name;
            this.price = price;
            this.powerBonus = powerBonus;
            this.controlBonus = controlBonus;
            this.desc = desc;
        }
    }

    public static class Player implements Serializable {
        private static final long serialVersionUID = 1L;

        public String name = "Der Herausforderer";
        public int ranking = 200;
        public int previousRanking = 200;
        public int age = 21;
        public int heightCm = 188;
        public int weightKg = 80;
        public String nationality = "GER";
        public boolean isLeftHanded = false;
        public boolean isTwoHandedBackhand = true;

        // STARTWERT 65 ALS ABSOLUTES MINIMUM
        public int servePower = 65;
        public int serveControl = 65;
        public int forehand = 65;
        public int backhand = 65;
        public int speed = 65;
        public int agility = 65;
        public int stamina = 65;
        public int mentalToughness = 65;

        public int rankingPoints = 250;
        public double money = 3500.0;
        public int morale = 80;

        public int highestMilestoneOvr = 65;

        public RacketItem equippedRacket = null;

        public Player(String name, int ranking, String nationality, int rankingPoints) {
            this.name = name;
            this.ranking = ranking;
            this.previousRanking = ranking;
            this.nationality = nationality;
            this.rankingPoints = rankingPoints;
            updateStatsBasedOnRank();
        }

        public void updateStatsBasedOnRank() {
            int base = Math.max(65, 96 - (ranking / 4));
            this.servePower = base;
            this.serveControl = base;
            this.forehand = base;
            this.backhand = base;
            this.speed = base;
            this.agility = base;
            this.stamina = base;
            this.mentalToughness = base;
            this.highestMilestoneOvr = Math.max(65, (getOverallRating() / 5) * 5);
        }

        public void applyPhysicalModifiers() {
            int hDiff = heightCm - 185;
            int wDiff = weightKg - 80;

            this.servePower = Math.max(65, Math.min(99, this.servePower + (hDiff / 3)));
            this.serveControl = Math.max(65, Math.min(99, this.serveControl + (hDiff / 4)));
            this.speed = Math.max(65, Math.min(99, this.speed - (hDiff / 4)));
            this.agility = Math.max(65, Math.min(99, this.agility - (hDiff / 3)));

            this.forehand = Math.max(65, Math.min(99, this.forehand + (wDiff / 4)));
            this.backhand = Math.max(65, Math.min(99, this.backhand + (wDiff / 5)));
            this.stamina = Math.max(65, Math.min(99, this.stamina - (wDiff / 3)));
            this.speed = Math.max(65, Math.min(99, this.speed - (wDiff / 5)));

            updateMilestone();
        }

        public void modifyAllStatsDirectly(int change) {
            if (change > 0) {
                this.servePower = Math.min(99, this.servePower + change);
                this.serveControl = Math.min(99, this.serveControl + change);
                this.forehand = Math.min(99, this.forehand + change);
                this.backhand = Math.min(99, this.backhand + change);
                this.speed = Math.min(99, this.speed + change);
                this.agility = Math.min(99, this.agility + change);
                this.stamina = Math.min(99, this.stamina + change);
                this.mentalToughness = Math.min(99, this.mentalToughness + change);
            } else if (change < 0) {
                int oldOvr = getOverallRating();
                int minAllowed = Math.max(65, highestMilestoneOvr); // Nie unter 65!
                int newTargetOvr = oldOvr + change;

                if (newTargetOvr < minAllowed) {
                    change = minAllowed - oldOvr;
                }

                if (change == 0) return;

                this.servePower = Math.max(65, Math.min(99, this.servePower + change));
                this.serveControl = Math.max(65, Math.min(99, this.serveControl + change));
                this.forehand = Math.max(65, Math.min(99, this.forehand + change));
                this.backhand = Math.max(65, Math.min(99, this.backhand + change));
                this.speed = Math.max(65, Math.min(99, this.speed + change));
                this.agility = Math.max(65, Math.min(99, this.agility + change));
                this.stamina = Math.max(65, Math.min(99, this.stamina + change));
                this.mentalToughness = Math.max(65, Math.min(99, this.mentalToughness + change));
            }
            updateMilestone();
        }

        private void updateMilestone() {
            int currentOvr = getOverallRating();
            int milestone = (currentOvr / 5) * 5;
            if (milestone > highestMilestoneOvr) {
                highestMilestoneOvr = Math.max(65, milestone);
            }
        }

        public int getOverallRating() {
            int rPower = (equippedRacket != null) ? equippedRacket.powerBonus : 0;
            int rCtrl = (equippedRacket != null) ? equippedRacket.controlBonus : 0;
            int sum = (servePower + rPower) + (serveControl + rCtrl) + forehand + backhand + speed + agility + stamina + mentalToughness;
            return Math.max(65, Math.min(99, (int) Math.round(sum / 8.0)));
        }
    }

    public static class MatchPairing {
        public Player p1;
        public Player p2;
        public Player winner;

        public MatchPairing(Player p1, Player p2) {
            this.p1 = p1;
            this.p2 = p2;
        }
    }

    // ----------------------------------------------------
    // STATE & VARIABLES
    // ----------------------------------------------------
    private Player humanPlayer;
    private ObservableList<Player> top200List;
    private List<CalendarEvent> atpCalendar;
    private List<RacketItem> shopRackets;
    private int currentWeekIndex = 0;

    // Turnier-State
    private boolean tournamentStarted = false;
    private List<Player> activeTournamentDraw = new ArrayList<>();
    private List<MatchPairing> currentRoundPairings = new ArrayList<>();
    private int currentRoundIndex = 0;
    private Player currentOpponent = null;

    private int playerSetsWon = 0;
    private int opponentSetsWon = 0;
    private List<String> currentMatchSetsLog = new ArrayList<>();
    private List<String> currentMatchScoresOnly = new ArrayList<>();
    String matchStatusMessage = "";

    private boolean matchFinished = false;
    private Runnable nextActionAfterMatch = null;

    private final Random rng = new Random();

    // GUI Labels
    private Label moneyLabel;
    private Label rankLabel;
    private Label pointsLabel;
    private Label ovrLabel;
    private Label moraleLabel;
    private Label weekLabel;
    private VBox mainContentArea;
    private Pane confettiPane;

    @Override
    public void start(Stage primaryStage) {
        humanPlayer = new Player("Der Herausforderer", 200, "GER", 250);
        top200List = generateFixedTop200(); // 199 Bots
        top200List.add(humanPlayer);        // Exakt 200 Spieler
        sortTop200Ranking();

        atpCalendar = generateATP2026Calendar();
        initShopItems();

        primaryStage.setTitle("ATP Tennis Manager 2026 Pro - Full Edition");

        // ANGEPASSTER CSS-STYLESHEET FÜR OPTIMALE LESBARKEIT IN DER TABELLE
        String css =
                ".root { -fx-background-color: linear-gradient(to bottom right, #0f172a, #1e1b4b, #0f172a); }" +
                        ".header-bar { -fx-background-color: rgba(30, 41, 59, 0.85); -fx-padding: 15px; -fx-border-color: #312e81; -fx-border-width: 0 0 2px 0; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.4), 10, 0, 0, 4); }" +
                        ".primary-button { -fx-background-color: linear-gradient(to right, #0284c7, #2563eb); -fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 13px; -fx-padding: 10px 18px; -fx-background-radius: 8px; -fx-cursor: hand; -fx-effect: dropshadow(three-pass-box, rgba(2,132,199,0.3), 5, 0, 0, 2); }" +
                        ".primary-button:hover { -fx-background-color: linear-gradient(to right, #0369a1, #1d4ed8); }" +
                        ".primary-button:disabled { -fx-background-color: #475569; -fx-effect: none; }" +
                        ".next-button { -fx-background-color: linear-gradient(to right, #f59e0b, #d97706); -fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 14px; -fx-padding: 12px 22px; -fx-background-radius: 8px; -fx-cursor: hand; -fx-effect: dropshadow(three-pass-box, rgba(245,158,11,0.4), 8, 0, 0, 3); }" +
                        ".next-button:hover { -fx-background-color: linear-gradient(to right, #d97706, #b45309); }" +
                        ".card-box { -fx-background-color: rgba(30, 41, 59, 0.75); -fx-background-radius: 12px; -fx-padding: 20px; -fx-border-color: rgba(255,255,255,0.1); -fx-border-width: 1px; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.3), 10, 0, 0, 4); }" +
                        ".bracket-card { -fx-background-color: #0f172a; -fx-padding: 12px; -fx-background-radius: 8px; -fx-border-color: #334155; -fx-border-width: 1px; }" +
                        ".label { -fx-text-fill: #f8fafc; }" +
                        ".text-field { -fx-background-color: #1e293b; -fx-text-fill: white; -fx-border-color: #475569; -fx-border-radius: 6px; -fx-padding: 6px; }" +
                        ".combo-box { -fx-background-color: #1e293b; -fx-mark-color: white; }" +
                        ".combo-box .list-cell { -fx-text-fill: white; -fx-background-color: #1e293b; }" +
                        ".table-view { -fx-background-color: #1e293b; -fx-background-radius: 8px; }" +
                        ".table-view .column-header-background { -fx-background-color: #0f172a; }" +
                        ".table-view .column-header { -fx-background-color: #0f172a; -fx-border-color: #334155; }" +
                        ".table-view .column-header .label { -fx-text-fill: #ffffff; -fx-font-weight: bold; -fx-font-size: 13px; }" +
                        ".table-row-cell { -fx-background-color: #1e293b; -fx-border-color: #334155; -fx-border-width: 0 0 1px 0; }" +
                        ".table-row-cell:odd { -fx-background-color: #0f172a; }" +
                        ".table-row-cell .text { -fx-fill: #ffffff !important; -fx-font-weight: bold; }" +
                        ".table-row-cell:selected { -fx-background-color: #0284c7; }" +
                        ".table-row-cell:selected .text { -fx-fill: #ffffff !important; }";

        BorderPane rootLayout = new BorderPane();

        StackPane rootStack = new StackPane();
        confettiPane = new Pane();
        confettiPane.setMouseTransparent(true);

        HBox header = new HBox(15);
        header.getStyleClass().add("header-bar");
        header.setAlignment(Pos.CENTER_LEFT);

        Label title = new Label("🎾 TENNIS MANAGER PRO");
        title.setFont(Font.font("System", FontWeight.BOLD, 18));
        title.setTextFill(Color.web("#38bdf8"));

        weekLabel = new Label("Woche 1");
        weekLabel.setFont(Font.font("System", FontWeight.BOLD, 12));

        moneyLabel = new Label("Konto: $" + String.format("%.2f", humanPlayer.money));
        moneyLabel.setFont(Font.font("System", FontWeight.BOLD, 12));

        rankLabel = new Label("Rang: #" + humanPlayer.ranking);
        rankLabel.setFont(Font.font("System", FontWeight.BOLD, 12));

        pointsLabel = new Label("Punkte: " + humanPlayer.rankingPoints + " Pkt.");
        pointsLabel.setFont(Font.font("System", FontWeight.BOLD, 12));

        ovrLabel = new Label("OVR: " + humanPlayer.getOverallRating() + " (🔒" + humanPlayer.highestMilestoneOvr + ")");
        ovrLabel.setFont(Font.font("System", FontWeight.BOLD, 12));

        moraleLabel = new Label("Moral: " + humanPlayer.morale + "%");
        moraleLabel.setFont(Font.font("System", FontWeight.BOLD, 12));

        header.getChildren().addAll(title, weekLabel, moneyLabel, rankLabel, pointsLabel, ovrLabel, moraleLabel);
        rootLayout.setTop(header);

        VBox navBar = new VBox(10);
        navBar.setPadding(new Insets(20));
        navBar.setStyle("-fx-background-color: rgba(15, 23, 42, 0.8); -fx-pref-width: 210px; -fx-border-color: #1e293b; -fx-border-width: 0 2px 0 0;");

        Button btnProfile = createNavButton("👤 Profil & Setup");
        Button btnShop = createNavButton("🛒 Schläger-Shop");
        Button btnCasino = createNavButton("🎰 Casino Arena");
        Button btnTournament = createNavButton("🏆 ATP Tour & Match");
        Button btnRanking = createNavButton("📊 Top 200 Rangliste");

        btnProfile.setOnAction(e -> showProfileView());
        btnShop.setOnAction(e -> showShopView());
        btnCasino.setOnAction(e -> showCasinoView());
        btnTournament.setOnAction(e -> showTournamentView());
        btnRanking.setOnAction(e -> showRankingView());

        navBar.getChildren().addAll(btnProfile, btnShop, btnCasino, btnTournament, btnRanking);
        rootLayout.setLeft(navBar);

        mainContentArea = new VBox(15);
        mainContentArea.setPadding(new Insets(20));
        rootLayout.setCenter(mainContentArea);

        rootStack.getChildren().addAll(rootLayout, confettiPane);

        showTournamentView();

        Scene scene = new Scene(rootStack, 1120, 740);
        scene.getStylesheets().add("data:text/css," + css.replace(" ", "%20"));
        primaryStage.setScene(scene);
        primaryStage.show();

        showCharacterCreationWindow(primaryStage);
    }

    private Button createNavButton(String text) {
        Button b = new Button(text);
        b.getStyleClass().add("primary-button");
        b.setMaxWidth(Double.MAX_VALUE);
        return b;
    }

    private void updateHeader() {
        CalendarEvent ev = atpCalendar.get(currentWeekIndex);
        weekLabel.setText("Woche " + ev.week + " (" + ev.name + ")");
        moneyLabel.setText("Konto: $" + String.format("%.2f", humanPlayer.money));
        rankLabel.setText("Rang: #" + humanPlayer.ranking);
        pointsLabel.setText("Punkte: " + humanPlayer.rankingPoints + " Pkt.");
        ovrLabel.setText("OVR: " + humanPlayer.getOverallRating() + " (🔒" + humanPlayer.highestMilestoneOvr + ")");
        moraleLabel.setText("Moral: " + humanPlayer.morale + "%");
    }

    private void showCharacterCreationWindow(Stage ownerStage) {
        Stage dialogStage = new Stage();
        dialogStage.initModality(Modality.WINDOW_MODAL);
        dialogStage.initOwner(ownerStage);
        dialogStage.setTitle("Spieler-Erstellung");

        VBox root = new VBox(15);
        root.setPadding(new Insets(20));
        root.setStyle("-fx-background-color: #0f172a;");

        Label title = new Label("Erstelle deinen eigenen Tennis-Profi!");
        title.setFont(Font.font("System", FontWeight.BOLD, 16));
        title.setTextFill(Color.web("#38bdf8"));

        GridPane grid = new GridPane();
        grid.setHgap(10); grid.setVgap(10);

        TextField nameInput = new TextField(humanPlayer.name);

        ComboBox<String> natBox = new ComboBox<>();
        natBox.getItems().addAll(
                "ARG (Argentinien)", "AUS (Australien)", "AUT (Österreich)", "BEL (Belgien)",
                "BRA (Brasilien)", "BUL (Bulgarien)", "CAN (Kanada)", "CHI (Chile)",
                "CHN (China)", "CRO (Kroatien)", "CZE (Tschechien)", "DEN (Dänemark)",
                "ESP (Spanien)", "FIN (Finnland)", "FRA (Frankreich)", "GBR (Großbritannien)",
                "GER (Deutschland)", "GRE (Griechenland)", "HUN (Ungarn)", "IND (Indien)",
                "ITA (Italien)", "JPN (Japan)", "KAZ (Kasachstan)", "NED (Niederlande)",
                "NOR (Norwegen)", "POL (Polen)", "POR (Portugal)", "ROU (Rumänien)",
                "RSA (Südafrika)", "RUS (Russland)", "SRB (Serbien)", "SUI (Schweiz)",
                "SVK (Slowakei)", "SWE (Schweden)", "TUN (Tunesien)", "TUR (Türkei)",
                "UKR (Ukraine)", "USA (USA)"
        );
        natBox.setValue("GER (Deutschland)");

        TextField heightInput = new TextField("188");
        TextField weightInput = new TextField("80");

        ComboBox<String> handBox = new ComboBox<>();
        handBox.getItems().addAll("Rechtshänder", "Linkshänder");
        handBox.setValue("Rechtshänder");

        ComboBox<String> backhandBox = new ComboBox<>();
        backhandBox.getItems().addAll("2-händige Rückhand", "1-händige Rückhand");
        backhandBox.setValue("2-händige Rückhand");

        grid.add(new Label("Name:"), 0, 0); grid.add(nameInput, 1, 0);
        grid.add(new Label("Nationalität:"), 0, 1); grid.add(natBox, 1, 1);
        grid.add(new Label("Größe (155 - 220 cm):"), 0, 2); grid.add(heightInput, 1, 2);
        grid.add(new Label("Gewicht (55 - 150 kg):"), 0, 3); grid.add(weightInput, 1, 3);
        grid.add(new Label("Dominante Hand:"), 0, 4); grid.add(handBox, 1, 4);
        grid.add(new Label("Rückhand:"), 0, 5); grid.add(backhandBox, 1, 5);

        Button btnCreate = new Button("Profi Erstellen");
        btnCreate.getStyleClass().add("primary-button");

        btnCreate.setOnAction(e -> {
            try {
                int h = Integer.parseInt(heightInput.getText().trim());
                int w = Integer.parseInt(weightInput.getText().trim());

                if (h < 155 || h > 220 || w < 55 || w > 150) {
                    showAlert("Eingabefehler", "Ungültige Werte!\n\n• Größe: 155 bis 220 cm\n• Gewicht: 55 bis 150 kg");
                    return;
                }

                humanPlayer.name = nameInput.getText().trim().isEmpty() ? "Der Herausforderer" : nameInput.getText().trim();

                String selectedNat = natBox.getValue();
                humanPlayer.nationality = selectedNat.substring(0, 3);

                humanPlayer.heightCm = h;
                humanPlayer.weightKg = w;
                humanPlayer.isLeftHanded = handBox.getValue().equals("Linkshänder");
                humanPlayer.isTwoHandedBackhand = backhandBox.getValue().equals("2-händige Rückhand");

                humanPlayer.applyPhysicalModifiers();
                updateHeader();
                dialogStage.close();

            } catch (NumberFormatException ex) {
                showAlert("Eingabefehler", "Bitte trage gültige Zahlen für Größe und Gewicht ein!");
            }
        });

        root.getChildren().addAll(title, grid, btnCreate);

        Scene dialogScene = new Scene(root, 400, 380);
        dialogScene.getStylesheets().add(ownerStage.getScene().getStylesheets().get(0));
        dialogStage.setScene(dialogScene);
        dialogStage.setOnCloseRequest(e -> e.consume());
        dialogStage.showAndWait();
    }

    private void showProfileView() {
        mainContentArea.getChildren().clear();

        Label header = new Label("Spielerprofil: " + humanPlayer.name + " (" + humanPlayer.nationality + ")");
        header.setFont(Font.font("System", FontWeight.BOLD, 18));
        header.setTextFill(Color.web("#38bdf8"));

        VBox detailsCard = new VBox(8);
        detailsCard.getStyleClass().add("card-box");
        detailsCard.getChildren().addAll(
                new Label("Größe: " + humanPlayer.heightCm + " cm | Gewicht: " + humanPlayer.weightKg + " kg"),
                new Label("Hand: " + (humanPlayer.isLeftHanded ? "Linkshänder" : "Rechtshänder") + " | Rückhand: " + (humanPlayer.isTwoHandedBackhand ? "2-händig" : "1-händig")),
                new Label("Erreichter Meilenstein-Schutz: Overall " + humanPlayer.highestMilestoneOvr + " (Kann nicht unter 65 bzw. Meilenstein fallen!)"),
                new Label("Ausgerüsteter Schläger: " + (humanPlayer.equippedRacket != null ? humanPlayer.equippedRacket.name + " (" + humanPlayer.equippedRacket.desc + ")" : "Kein Schläger ausgerüstet"))
        );

        GridPane grid = new GridPane();
        grid.setHgap(15); grid.setVgap(8);
        grid.getStyleClass().add("card-box");

        addStatRow(grid, "Aufschlagkraft (Serve):", humanPlayer.servePower, 0);
        addStatRow(grid, "Aufschlagpräzision:", humanPlayer.serveControl, 1);
        addStatRow(grid, "Vorhand (Forehand):", humanPlayer.forehand, 2);
        addStatRow(grid, "Rückhand (Backhand):", humanPlayer.backhand, 3);
        addStatRow(grid, "Schnelligkeit (Speed):", humanPlayer.speed, 4);
        addStatRow(grid, "Beweglichkeit (Agility):", humanPlayer.agility, 5);
        addStatRow(grid, "Ausdauer (Stamina):", humanPlayer.stamina, 6);
        addStatRow(grid, "Mentale Stärke:", humanPlayer.mentalToughness, 7);

        mainContentArea.getChildren().addAll(header, detailsCard, grid);
    }

    private void addStatRow(GridPane grid, String labelText, int val, int row) {
        Label l1 = new Label(labelText);
        Label l2 = new Label(String.valueOf(val));
        l2.setStyle("-fx-font-weight: bold; -fx-text-fill: #38bdf8;");
        grid.add(l1, 0, row);
        grid.add(l2, 1, row);
    }

    private void initShopItems() {
        shopRackets = new ArrayList<>();
        shopRackets.add(new RacketItem("babolat", "Babolat Pure Drive", 1200, 5, 0, "+5 Aufschlagkraft"));
        shopRackets.add(new RacketItem("wilson", "Wilson Pro Staff", 1400, 2, 4, "+2 Power, +4 Kontrolle"));
        shopRackets.add(new RacketItem("head", "Head Speed Pro", 1100, 3, 3, "Allrounder: +3 Power, +3 Kontrolle"));
        shopRackets.add(new RacketItem("yonex", "Yonex EZONE 98", 1300, 4, 2, "+4 Power, +2 Kontrolle"));
    }

    private void showShopView() {
        mainContentArea.getChildren().clear();

        Label header = new Label("Profi Schläger-Shop (Gültig für 1 Turnier)");
        header.setFont(Font.font("System", FontWeight.BOLD, 18));
        header.setTextFill(Color.web("#38bdf8"));

        FlowPane shopPane = new FlowPane(15, 15);

        for (RacketItem r : shopRackets) {
            VBox card = new VBox(10);
            card.getStyleClass().add("card-box");
            card.setPrefWidth(220);

            Label name = new Label(r.name);
            name.setFont(Font.font("System", FontWeight.BOLD, 14));
            name.setTextFill(Color.web("#38bdf8"));

            Label desc = new Label(r.desc);
            Label price = new Label("Preis: $" + String.format("%.2f", r.price));

            Button buyBtn = new Button();
            buyBtn.getStyleClass().add("primary-button");

            boolean isEquipped = (humanPlayer.equippedRacket != null && humanPlayer.equippedRacket.id.equals(r.id));

            if (isEquipped) {
                buyBtn.setText("Ausgerüstet");
                buyBtn.setDisable(true);
            } else {
                buyBtn.setText("Kaufen");
                buyBtn.setDisable(false);
                buyBtn.setOnAction(e -> {
                    if (humanPlayer.money >= r.price) {
                        humanPlayer.money -= r.price;
                        humanPlayer.equippedRacket = r;
                        updateHeader();
                        showShopView();
                        showAlert("Erfolg", r.name + " wurde ausgerüstet!");
                    } else {
                        showAlert("Fehler", "Nicht genügend Geld auf dem Konto!");
                    }
                });
            }

            card.getChildren().addAll(name, desc, price, buyBtn);
            shopPane.getChildren().add(card);
        }

        mainContentArea.getChildren().addAll(header, shopPane);
    }

    private void showCasinoView() {
        mainContentArea.getChildren().clear();

        Label header = new Label("Casino Arena - Animiertes Roulette");
        header.setFont(Font.font("System", FontWeight.BOLD, 18));
        header.setTextFill(Color.web("#38bdf8"));

        VBox box = new VBox(15);
        box.getStyleClass().add("card-box");
        box.setAlignment(Pos.CENTER);

        TextField betInput = new TextField("200");
        betInput.setMaxWidth(150);

        HBox wheelBox = new HBox(8);
        wheelBox.setAlignment(Pos.CENTER);
        wheelBox.setStyle("-fx-background-color: #0f172a; -fx-padding: 15px; -fx-background-radius: 8px; -fx-border-color: #334155; -fx-border-width: 1px;");

        List<Label> wheelSlots = new ArrayList<>();
        for (int i = 0; i < 7; i++) {
            Label slot = new Label("?");
            slot.setFont(Font.font("System", FontWeight.BOLD, 16));
            slot.setPrefSize(45, 45);
            slot.setAlignment(Pos.CENTER);
            slot.setStyle("-fx-background-color: #334155; -fx-text-fill: white; -fx-background-radius: 6px;");
            wheelSlots.add(slot);
            wheelBox.getChildren().add(slot);
        }

        Polygon arrowDown = new Polygon(0.0, 20.0, 15.0, 0.0, -15.0, 0.0);
        arrowDown.setFill(Color.YELLOW);

        Label resLabel = new Label("Wähle deine Farbe:");

        Button btnRed = new Button("Rot (x2)");
        btnRed.getStyleClass().add("primary-button");

        Button btnBlack = new Button("Schwarz (x2)");
        btnBlack.getStyleClass().add("primary-button");

        Runnable startSpin = () -> {
            try {
                double bet = Double.parseDouble(betInput.getText());
                if (bet <= 0 || bet > humanPlayer.money) {
                    resLabel.setText("Ungültiger Einsatz!");
                    return;
                }

                btnRed.setDisable(true);
                btnBlack.setDisable(true);

                Timeline timeline = new Timeline();
                int totalSteps = 25;

                for (int step = 0; step < totalSteps; step++) {
                    int finalStep = step;
                    KeyFrame frame = new KeyFrame(Duration.millis(step * 100 + (step * step * 4)), e -> {
                        for (Label s : wheelSlots) {
                            int num = rng.nextInt(37);
                            boolean isRed = (num % 2 != 0 && num != 0);
                            s.setText(String.valueOf(num));
                            if (num == 0) s.setStyle("-fx-background-color: #16a34a; -fx-text-fill: white;");
                            else if (isRed) s.setStyle("-fx-background-color: #dc2626; -fx-text-fill: white;");
                            else s.setStyle("-fx-background-color: #0f172a; -fx-text-fill: white;");
                        }

                        if (finalStep == totalSteps - 1) {
                            Label centerSlot = wheelSlots.get(3);
                            int winningNum = Integer.parseInt(centerSlot.getText());
                            boolean isRedWin = (winningNum % 2 != 0 && winningNum != 0);

                            if (winningNum == 0) {
                                humanPlayer.money -= bet;
                                resLabel.setText("0 getroffen! Haus gewinnt. -$ " + bet);
                            } else if (isRedWin) {
                                humanPlayer.money += bet;
                                resLabel.setText("ROT gewinnt (" + winningNum + ")! +$" + bet);
                            } else {
                                humanPlayer.money -= bet;
                                resLabel.setText("SCHWARZ gewinnt (" + winningNum + ")! -$ " + bet);
                            }

                            updateHeader();
                            btnRed.setDisable(false);
                            btnBlack.setDisable(false);
                        }
                    });
                    timeline.getKeyFrames().add(frame);
                }
                timeline.play();

            } catch (Exception ex) {
                resLabel.setText("Einsatz eingeben!");
            }
        };

        btnRed.setOnAction(e -> startSpin.run());
        btnBlack.setOnAction(e -> startSpin.run());

        box.getChildren().addAll(new Label("Einsatz wählen:"), betInput, arrowDown, wheelBox, new HBox(10, btnRed, btnBlack), resLabel);
        mainContentArea.getChildren().addAll(header, box);
    }

    // ----------------------------------------------------
    // TURNIER & MATCHSIMULATION (ENDGAME-LOGIK AB OVR 90)
    // ----------------------------------------------------
    private void showTournamentView() {
        mainContentArea.getChildren().clear();

        CalendarEvent event = atpCalendar.get(currentWeekIndex);
        TournamentCategory cat = event.category;

        Label header = new Label("ATP Saison-Kalender - Woche " + event.week + " / 52");
        header.setFont(Font.font("System", FontWeight.BOLD, 18));
        header.setTextFill(Color.web("#38bdf8"));

        VBox tournamentCard = new VBox(15);
        tournamentCard.setPadding(new Insets(20));
        tournamentCard.setStyle("-fx-background-color: " + event.surface.hexColor + "; -fx-background-radius: 12px; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.5), 10, 0, 0, 4);");

        Label tName = new Label(event.name + " (" + cat.name + " - " + event.surface.name + ")");
        tName.setFont(Font.font("System", FontWeight.BOLD, 20));
        tName.setTextFill(Color.WHITE);

        if (!tournamentStarted) {
            Button btnStartTournament = new Button("Turnier starten");
            btnStartTournament.getStyleClass().add("primary-button");
            btnStartTournament.setOnAction(e -> {
                startNewTournament(cat);
                showTournamentView();
            });
            tournamentCard.getChildren().addAll(tName, btnStartTournament);
            mainContentArea.getChildren().addAll(header, tournamentCard);
            return;
        }

        VBox bracketBox = new VBox(12);
        bracketBox.setStyle("-fx-background-color: rgba(15, 23, 42, 0.7); -fx-padding: 15px; -fx-background-radius: 8px;");

        String currentRoundTitle = cat.rounds[currentRoundIndex];
        Label roundLabel = new Label("TURNIERBAUM: " + currentRoundTitle);
        roundLabel.setFont(Font.font("System", FontWeight.BOLD, 16));
        roundLabel.setTextFill(Color.WHITE);

        VBox playerMatchCard = new VBox(8);
        playerMatchCard.getStyleClass().add("bracket-card");

        Label matchVs = new Label("MATCH: " + humanPlayer.name + " (#" + humanPlayer.ranking + ") vs. " + (currentOpponent != null ? currentOpponent.name + " (#" + currentOpponent.ranking + ")" : "Kein Gegner"));
        matchVs.setFont(Font.font("System", FontWeight.BOLD, 14));
        matchVs.setTextFill(Color.WHITE);

        Label setScoreBoard = new Label("Sätze: " + humanPlayer.name + " [" + playerSetsWon + "] - [" + opponentSetsWon + "] " + (currentOpponent != null ? currentOpponent.name : "-"));
        setScoreBoard.setFont(Font.font("System", FontWeight.BOLD, 14));
        setScoreBoard.setTextFill(Color.CYAN);

        VBox setsLogBox = new VBox(3);
        for (String setLog : currentMatchSetsLog) {
            Label l = new Label("• " + setLog);
            l.setTextFill(Color.YELLOW);
            setsLogBox.getChildren().add(l);
        }

        Label statusLabel = new Label(matchStatusMessage);
        statusLabel.setFont(Font.font("System", FontWeight.BOLD, 12));
        statusLabel.setTextFill(Color.WHITE);

        if (!matchFinished) {
            Button btnSimulateSet = new Button("Nächsten Satz simulieren");
            btnSimulateSet.getStyleClass().add("primary-button");

            btnSimulateSet.setOnAction(e -> {
                if (currentOpponent == null) return;

                int currentSetNumber = playerSetsWon + opponentSetsWon + 1;

                boolean p1WinsSet = (humanPlayer.getOverallRating() + rng.nextInt(10)) >= (currentOpponent.getOverallRating() + rng.nextInt(10));
                int p1Games, p2Games;

                double randChance = rng.nextDouble();

                if (randChance < 0.20) {
                    p1Games = p1WinsSet ? 7 : 6;
                    p2Games = p1WinsSet ? 6 : 7;
                } else if (randChance < 0.35) {
                    p1Games = p1WinsSet ? 7 : 5;
                    p2Games = p1WinsSet ? 5 : 7;
                } else {
                    p1Games = p1WinsSet ? 6 : rng.nextInt(5);
                    p2Games = p1WinsSet ? rng.nextInt(5) : 6;
                }

                if (p1WinsSet) playerSetsWon++;
                else opponentSetsWon++;

                String scoreText = p1Games + "-" + p2Games;
                String setLogText = currentSetNumber + ". Satz: " + humanPlayer.name + " " + scoreText + " " + currentOpponent.name;
                currentMatchSetsLog.add(setLogText);
                currentMatchScoresOnly.add(scoreText);

                int setsToWin = cat.setsToWin;

                if (playerSetsWon == setsToWin || opponentSetsWon == setsToWin) {
                    boolean matchWon = playerSetsWon == setsToWin;
                    double basePrize = cat.maxPrizeMoney;
                    int basePoints = cat.maxPoints;
                    double roundShare = (double) (currentRoundIndex + 1) / cat.rounds.length;

                    String fullMatchScore = String.join(", ", currentMatchScoresOnly);
                    matchFinished = true;

                    int currentOvr = humanPlayer.getOverallRating();

                    if (matchWon) {
                        triggerConfettiAnimation();

                        // PRO MATCH-SIEG:
                        // Ab 90: Keinerlei Stat-Gains in normalen Matches!
                        // 85-89: +1 Stat
                        // Unter 85: +3 bis +5 Stats
                        int ratingBoost = 0;
                        if (currentOvr >= 90) {
                            ratingBoost = 0;
                        } else if (currentOvr >= 85) {
                            ratingBoost = 1;
                        } else {
                            ratingBoost = 3 + rng.nextInt(3); // +3, +4 oder +5
                        }

                        if (ratingBoost > 0) {
                            humanPlayer.modifyAllStatsDirectly(ratingBoost);
                        }

                        if (currentRoundIndex < cat.rounds.length - 1) {
                            double prize = basePrize * roundShare * 0.4;
                            int pts = (int)(basePoints * roundShare * 0.4);
                            humanPlayer.money += prize;
                            humanPlayer.rankingPoints += pts;

                            matchStatusMessage = "SIEG in " + currentRoundTitle + "!\nGesamtergebnis: " + fullMatchScore + " (+$" + String.format("%.0f", prize) + " & +" + pts + " Pkt." + (ratingBoost > 0 ? " | Stats +" + ratingBoost : " | 90+ OVR: Nur Turniersiege bringen Stats!") + ")";

                            nextActionAfterMatch = () -> {
                                simulateOtherMatchesOfCurrentRound();
                                currentRoundIndex++;
                                setupNextRoundMatch();
                            };
                        } else {
                            // TURNIERSIEGER-BONUS:
                            // Ab OVR 90: EXAKT +2 AUF ALLE STATS BEI TURNIERSIEG!
                            // Unter OVR 90: Standard Turniersieg-Bonus +2
                            humanPlayer.modifyAllStatsDirectly(2);

                            humanPlayer.money += basePrize;
                            humanPlayer.rankingPoints += basePoints;
                            matchStatusMessage = "🏆 TURNIERSIEGER: " + humanPlayer.name.toUpperCase() + "!\nGesamtergebnis: " + fullMatchScore + " (+$" + String.format("%.0f", basePrize) + " | +" + basePoints + " Pkt. | Stats +2 für Turniersieg!)";
                            humanPlayer.equippedRacket = null;

                            nextActionAfterMatch = () -> finishTournamentReport(event);
                        }
                    } else {
                        double prize = basePrize * roundShare * 0.15;
                        int pts = (int)(basePoints * roundShare * 0.15);
                        humanPlayer.money += prize;
                        humanPlayer.rankingPoints += pts;

                        // PRO NIEDERLAGE:
                        // Ab 90 OVR: Egal in welcher Runde man verliert -> EXAKT -1!
                        // Ab 85 OVR: -2
                        // Unter 85 OVR: -1 bis -2
                        int ratingDrop;
                        if (currentOvr >= 90) {
                            ratingDrop = 1; // Ab 90 OVR: -1 egal welche Runde!
                        } else if (currentOvr >= 85) {
                            ratingDrop = 2;
                        } else {
                            ratingDrop = 1 + rng.nextInt(2); // -1 oder -2
                        }

                        humanPlayer.modifyAllStatsDirectly(-ratingDrop);

                        matchStatusMessage = "NIEDERLAGE gegen " + currentOpponent.name + ".\nGesamtergebnis: " + fullMatchScore + " (Turnier nicht gewonnen: Stats -" + ratingDrop + ")";

                        humanPlayer.equippedRacket = null;
                        nextActionAfterMatch = () -> finishTournamentReport(event);
                    }
                } else {
                    matchStatusMessage = "Satz " + currentSetNumber + " beendet. Klicke für den nächsten Satz!";
                }

                sortTop200Ranking();
                updateHeader();
                showTournamentView();
            });

            playerMatchCard.getChildren().addAll(matchVs, setScoreBoard, setsLogBox, btnSimulateSet, statusLabel);
        } else {
            Button btnNextMatch = new Button("Weiter zum nächsten Match / Runde ▶");
            btnNextMatch.getStyleClass().add("next-button");

            btnNextMatch.setOnAction(e -> {
                matchFinished = false;
                if (nextActionAfterMatch != null) {
                    nextActionAfterMatch.run();
                }
                showTournamentView();
            });

            playerMatchCard.getChildren().addAll(matchVs, setScoreBoard, setsLogBox, statusLabel, btnNextMatch);
        }

        ScrollPane bracketScroll = new ScrollPane();
        bracketScroll.setFitToWidth(true);
        bracketScroll.setStyle("-fx-background: transparent; -fx-background-color: transparent;");

        FlowPane bracketGrid = new FlowPane(10, 10);
        bracketGrid.setPadding(new Insets(5));

        for (MatchPairing mp : currentRoundPairings) {
            VBox matchBox = new VBox(4);
            matchBox.getStyleClass().add("bracket-card");
            matchBox.setPrefWidth(210);

            Label p1Lbl = new Label((mp.p1 == humanPlayer ? "⭐ " : "") + mp.p1.name + " (#" + mp.p1.ranking + ")");
            p1Lbl.setTextFill(mp.winner == mp.p1 ? Color.LIME : Color.WHITE);
            p1Lbl.setFont(Font.font("System", FontWeight.BOLD, 11));

            Label vsLbl = new Label("   vs");
            vsLbl.setTextFill(Color.GRAY);
            vsLbl.setFont(Font.font("System", FontWeight.BOLD, 10));

            Label p2Lbl = new Label((mp.p2 == humanPlayer ? "⭐ " : "") + mp.p2.name + " (#" + mp.p2.ranking + ")");
            p2Lbl.setTextFill(mp.winner == mp.p2 ? Color.LIME : Color.WHITE);
            p2Lbl.setFont(Font.font("System", FontWeight.BOLD, 11));

            matchBox.getChildren().addAll(p1Lbl, vsLbl, p2Lbl);
            bracketGrid.getChildren().add(matchBox);
        }

        bracketScroll.setContent(bracketGrid);

        bracketBox.getChildren().addAll(roundLabel, playerMatchCard, new Separator(), bracketScroll);
        tournamentCard.getChildren().addAll(tName, bracketBox);
        mainContentArea.getChildren().addAll(header, tournamentCard);
    }

    private void startNewTournament(TournamentCategory cat) {
        tournamentStarted = true;
        currentRoundIndex = 0;

        List<Player> pool = new ArrayList<>(top200List);
        pool.remove(humanPlayer);
        Collections.shuffle(pool);

        activeTournamentDraw = new ArrayList<>();
        activeTournamentDraw.add(humanPlayer);

        int botsToTake = Math.min(cat.drawSize - 1, pool.size());
        for (int i = 0; i < botsToTake; i++) {
            activeTournamentDraw.add(pool.get(i));
        }

        Collections.shuffle(activeTournamentDraw);
        setupRoundPairings();
    }

    private void setupRoundPairings() {
        currentRoundPairings.clear();
        for (int i = 0; i < activeTournamentDraw.size(); i += 2) {
            if (i + 1 < activeTournamentDraw.size()) {
                MatchPairing mp = new MatchPairing(activeTournamentDraw.get(i), activeTournamentDraw.get(i + 1));
                currentRoundPairings.add(mp);

                if (mp.p1 == humanPlayer) currentOpponent = mp.p2;
                else if (mp.p2 == humanPlayer) currentOpponent = mp.p1;
            }
        }

        playerSetsWon = 0;
        opponentSetsWon = 0;
        matchFinished = false;
        currentMatchSetsLog.clear();
        currentMatchScoresOnly.clear();
        matchStatusMessage = "Bereit für das Match!";
    }

    private void simulateOtherMatchesOfCurrentRound() {
        List<Player> nextRoundDraw = new ArrayList<>();

        for (MatchPairing mp : currentRoundPairings) {
            if (mp.p1 == humanPlayer || mp.p2 == humanPlayer) {
                mp.winner = humanPlayer;
                nextRoundDraw.add(humanPlayer);
            } else {
                boolean p1Wins = (mp.p1.getOverallRating() + rng.nextInt(10)) >= (mp.p2.getOverallRating() + rng.nextInt(10));
                mp.winner = p1Wins ? mp.p1 : mp.p2;
                mp.winner.rankingPoints += 45;
                nextRoundDraw.add(mp.winner);
            }
        }

        activeTournamentDraw = nextRoundDraw;
    }

    private void setupNextRoundMatch() {
        setupRoundPairings();
    }

    private void finishTournamentReport(CalendarEvent event) {
        tournamentStarted = false;
        advanceToNextWeekAndSimulateBots(event);
    }

    private void advanceToNextWeekAndSimulateBots(CalendarEvent event) {
        for (Player p : top200List) {
            if (p != humanPlayer && rng.nextDouble() < 0.3) {
                p.rankingPoints += rng.nextInt(event.category.maxPoints / 2);
            }
        }

        currentWeekIndex = (currentWeekIndex + 1) % 52;
        currentRoundIndex = 0;
        currentOpponent = null;
    }

    private void triggerConfettiAnimation() {
        confettiPane.getChildren().clear();
        Timeline fallTimeline = new Timeline();

        for (int i = 0; i < 90; i++) {
            Circle c = new Circle(4 + rng.nextInt(5), Color.color(rng.nextDouble(), rng.nextDouble(), rng.nextDouble()));
            c.setTranslateX(rng.nextInt(1000));
            c.setTranslateY(rng.nextInt(200));
            confettiPane.getChildren().add(c);

            KeyFrame kf = new KeyFrame(Duration.millis(30 + rng.nextInt(20)), e -> {
                c.setTranslateY(c.getTranslateY() + 4 + rng.nextInt(4));
                c.setTranslateX(c.getTranslateX() + (rng.nextBoolean() ? 1 : -1));
            });
            fallTimeline.getKeyFrames().add(kf);
        }

        fallTimeline.setCycleCount(150);
        fallTimeline.play();

        PauseTransition pause = new PauseTransition(Duration.seconds(5));
        pause.setOnFinished(e -> {
            fallTimeline.stop();
            confettiPane.getChildren().clear();
        });
        pause.play();
    }

    private void sortTop200Ranking() {
        top200List.sort((p1, p2) -> Integer.compare(p2.rankingPoints, p1.rankingPoints));
        for (int i = 0; i < top200List.size(); i++) {
            Player p = top200List.get(i);
            p.ranking = i + 1;
        }
    }

    private void showRankingView() {
        mainContentArea.getChildren().clear();

        Label header = new Label("ATP Weltrangliste (Top 200)");
        header.setFont(Font.font("System", FontWeight.BOLD, 18));
        header.setTextFill(Color.web("#38bdf8"));

        TableView<Player> table = new TableView<>();
        table.setItems(top200List);

        TableColumn<Player, Number> colRank = new TableColumn<>("Rang");
        colRank.setCellValueFactory(data -> new SimpleIntegerProperty(data.getValue().ranking));

        TableColumn<Player, String> colName = new TableColumn<>("Name");
        colName.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().name));

        TableColumn<Player, String> colNat = new TableColumn<>("Land");
        colNat.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().nationality));

        TableColumn<Player, Number> colPoints = new TableColumn<>("ATP Punkte");
        colPoints.setCellValueFactory(data -> new SimpleIntegerProperty(data.getValue().rankingPoints));

        TableColumn<Player, Number> colRating = new TableColumn<>("OVR Rating");
        colRating.setCellValueFactory(data -> new SimpleIntegerProperty(data.getValue().getOverallRating()));

        table.getColumns().addAll(colRank, colName, colNat, colPoints, colRating);
        mainContentArea.getChildren().addAll(header, table);
    }

    // ----------------------------------------------------
    // DATENBANK: EXAKT 199 BOTS + 1 HAUPTSPIELER = 200 SPIELER
    // ----------------------------------------------------
    private ObservableList<Player> generateFixedTop200() {
        ObservableList<Player> list = FXCollections.observableArrayList();

        Object[][] top200Data = {
                {1, "Jannik Sinner", "ITA", 10330}, {2, "Alexander Zverev", "GER", 7355}, {3, "Carlos Alcaraz", "ESP", 6710},
                {4, "Novak Djokovic", "SRB", 5560}, {5, "Daniil Medvedev", "RUS", 5480}, {6, "Andrey Rublev", "RUS", 4645},
                {7, "Casper Ruud", "NOR", 4010}, {8, "Hubert Hurkacz", "POL", 4005}, {9, "Alex de Minaur", "AUS", 3835},
                {10, "Stefanos Tsitsipas", "GRE", 3465}, {11, "Taylor Fritz", "USA", 3290}, {12, "Tommy Paul", "USA", 2920},
                {13, "Ben Shelton", "USA", 2480}, {14, "Ugo Humbert", "FRA", 2450}, {15, "Holger Rune", "DEN", 2300},
                {16, "Sebastian Korda", "USA", 2250}, {17, "Karen Khachanov", "RUS", 2100}, {18, "Arthur Fils", "FRA", 1980},
                {19, "Alejandro Tabilo", "CHI", 1910}, {20, "Lorenzo Musetti", "ITA", 1890}, {21, "Nicolas Jarry", "CHI", 1820},
                {22, "Alexander Bublik", "KAZ", 1750}, {23, "Jiří Lehečka", "CZE", 1680}, {24, "Jack Draper", "GBR", 1650},
                {25, "Matteo Berrettini", "ITA", 1600}, {26, "Gaël Monfils", "FRA", 1520}, {27, "Dominic Thiem", "AUT", 1480},
                {28, "Stan Wawrinka", "SUI", 1420}, {29, "Kei Nishikori", "JPN", 1380}, {30, "Milos Raonic", "CAN", 1350},
                {31, "Frances Tiafoe", "USA", 1320}, {32, "Sebastian Baez", "ARG", 1290}, {33, "Francisco Cerundolo", "ARG", 1260},
                {34, "Tomas Martin Etcheverry", "ARG", 1230}, {35, "Tallon Griekspoor", "NED", 1210}, {36, "Jan-Lennard Struff", "GER", 1190},
                {37, "Mariano Navone", "ARG", 1160}, {38, "Luciano Darderi", "ITA", 1130}, {39, "Matteo Arnaldi", "ITA", 1110},
                {40, "Tomas Machac", "CZE", 1090}, {41, "Alexei Popyrin", "AUS", 1070}, {42, "Jordan Thompson", "AUS", 1050},
                {43, "Nuno Borges", "POR", 1030}, {44, "Pedro Martinez", "ESP", 1010}, {45, "Flavio Cobolli", "ITA", 990},
                {46, "Zhizhen Zhang", "CHN", 970}, {47, "Marcos Giron", "USA", 950}, {48, "Miomir Kecmanovic", "SRB", 930},
                {49, "Alexander Shevchenko", "KAZ", 910}, {50, "Lorenzo Sonego", "ITA", 890}, {51, "Giovanni Mpetshi Perricard", "FRA", 870},
                {52, "Alex Michelsen", "USA", 850}, {53, "Roberto Carballes Baena", "ESP", 830}, {54, "Facundo Diaz Acosta", "ARG", 810},
                {55, "Arthur Rinderknech", "FRA", 790}, {56, "Brandon Nakashima", "USA", 770}, {57, "Sumit Nagal", "IND", 750},
                {58, "Thiago Seyboth Wild", "BRA", 730}, {59, "Corentin Moutet", "FRA", 710}, {60, "Pavel Kotov", "RUS", 690},
                {61, "Roberto Bautista Agut", "ESP", 670}, {62, "Jaume Munar", "ESP", 650}, {63, "Botic van de Zandschulp", "NED", 630},
                {64, "Aleksandar Vukic", "AUS", 610}, {65, "Daniel Altmaier", "GER", 590}, {66, "Yoshihito Nishioka", "JPN", 570},
                {67, "Fabio Fognini", "ITA", 550}, {68, "Dušan Lajović", "SRB", 530}, {69, "Arthur Cazaux", "FRA", 510},
                {70, "Thanasi Kokkinakis", "AUS", 490}, {71, "Christopher Eubanks", "USA", 470}, {72, "Rinky Hijikata", "AUS", 450},
                {73, "Zizou Bergs", "BEL", 430}, {74, "Hugo Gaston", "FRA", 410}, {75, "Thiago Monteiro", "BRA", 390},
                {76, "Taro Daniel", "JPN", 370}, {77, "David Goffin", "BEL", 350}, {78, "Aleksandar Kovacevic", "USA", 330},
                {79, "Maximilian Marterer", "GER", 310}, {80, "Borna Coric", "CRO", 290}, {81, "Hamad Medjedovic", "SRB", 270},
                {82, "Diego Schwartzman", "ARG", 260}, {83, "Richard Gasquet", "FRA", 250}, {84, "Denis Shapovalov", "CAN", 240},
                {85, "Cristian Garin", "CHI", 230}, {86, "Benoit Paire", "FRA", 220}, {87, "Radu Albot", "MDA", 210},
                {88, "Lloyd Harris", "RSA", 200}, {89, "Dominic Stricker", "SUI", 190}, {90, "Jurij Rodionov", "AUT", 180},
                {91, "Sebastian Ofner", "AUT", 170}, {92, "Felix Auger-Aliassime", "CAN", 160}, {93, "Bernard Tomic", "AUS", 150},
                {94, "Marco Cecchinato", "ITA", 140}, {95, "Benjamin Bonzi", "FRA", 130}, {96, "Rudolf Molleker", "GER", 120},
                {97, "Daniel Masur", "GER", 110}, {98, "Peter Gojowczyk", "GER", 100}, {99, "Cedrik-Marcel Stebe", "GER", 90}, {100, "Tobias Kamke", "GER", 80},
                {101, "Lukas Klein", "SVK", 78}, {102, "Otto Virtanen", "FIN", 76}, {103, "Yosuke Watanuki", "JPN", 74}, {104, "Terence Atmane", "FRA", 72},
                {105, "Harold Mayot", "FRA", 70}, {106, "Pierre-Hugues Herbert", "FRA", 68}, {107, "Gregoire Barrere", "FRA", 66}, {108, "Ugo Blanchet", "FRA", 64},
                {109, "Shintaro Mochizuki", "JPN", 62}, {110, "Marc-Andrea Huesler", "SUI", 60}, {111, "Alexander Ritschard", "SUI", 58}, {112, "Leandro Riedi", "SUI", 56},
                {113, "Filip Misolic", "AUT", 54}, {114, "Lukas Neumayer", "AUT", 52}, {115, "Dennis Novak", "AUT", 50}, {116, "Joel Schwärzler", "AUT", 48},
                {117, "Jan Choinski", "GBR", 46}, {118, "Liam Broady", "GBR", 44}, {119, "Billy Harris", "GBR", 42}, {120, "Ryan Peniston", "GBR", 40},
                {121, "Paul Jubb", "GBR", 39}, {122, "Arthur Fery", "GBR", 38}, {123, "Gabriel Diallo", "CAN", 37}, {124, "Alexis Galarneau", "CAN", 36},
                {125, "Vasek Pospisil", "CAN", 35}, {126, "Liam Draxl", "CAN", 34}, {127, "Emilio Nava", "USA", 33}, {128, "Patrick Kypson", "USA", 32},
                {129, "Zachary Svajda", "USA", 31}, {130, "Tristan Boyer", "USA", 30}, {131, "Martin Damm", "USA", 29}, {132, "Ethan Quinn", "USA", 28},
                {133, "Eliot Spizzirri", "USA", 27}, {134, "Aidan Mayo", "USA", 26}, {135, "Murphy Cassone", "USA", 25}, {136, "Micah Braswell", "USA", 24},
                {137, "Alex Bolt", "AUS", 23}, {138, "Tristan Schoolkate", "AUS", 22}, {139, "Li Tu", "AUS", 21}, {140, "Marc Polmans", "AUS", 20},
                {141, "Omar Jasika", "AUS", 19}, {142, "Philip Sekulic", "AUS", 18}, {143, "Dane Sweeny", "AUS", 17}, {144, "James McCabe", "AUS", 16},
                {145, "Andrea Pellegrino", "ITA", 15}, {146, "Stefano Travaglia", "ITA", 14}, {147, "Matteo Gigante", "ITA", 13}, {148, "Francesco Passaro", "ITA", 12},
                {149, "Mattia Bellucci", "ITA", 11}, {150, "Stefano Napolitano", "ITA", 10}, {151, "Gianluca Mager", "ITA", 9}, {152, "Franco Agamenone", "ITA", 8},
                {153, "Enrico Dalla Valle", "ITA", 7}, {154, "Oriol Roca Batalla", "ESP", 6}, {155, "Alejandro Moro Canas", "ESP", 5}, {156, "Daniel Rincon", "ESP", 4},
                {157, "Martin Landaluce", "ESP", 3}, {158, "Carlos Taberner", "ESP", 2}, {159, "Javier Barranco Cosano", "ESP", 1}, {160, "Hugo Grenier", "FRA", 1},
                {161, "Kyrian Jacquet", "FRA", 1}, {162, "Clement Chidekh", "FRA", 1}, {163, "Geoffrey Blancaneaux", "FRA", 1}, {164, "Antoine Escoffier", "FRA", 1},
                {165, "Enzo Couacaud", "FRA", 1}, {166, "Manuel Guinard", "FRA", 1}, {167, "Jules Marie", "FRA", 1}, {168, "Titouan Droguet", "FRA", 1},
                {169, "Henri Squire", "GER", 1}, {170, "Marko Topo", "GER", 1}, {171, "Timo Stodder", "GER", 1}, {172, "Louis Wessels", "GER", 1}, {173, "Sebastian Fanselow", "GER", 1},
                {174, "Mats Moraing", "GER", 1}, {175, "Jeremy Jahn", "GER", 1}, {176, "Elmar Ejupovic", "GER", 1}, {177, "Altuğ Çelikbilek", "TUR", 1},
                {178, "Cem İlkel", "TUR", 1}, {179, "Ergi Kırkın", "TUR", 1}, {180, "Yankı Erel", "TUR", 1}, {181, "Khumoyun Sultanov", "UZB", 1},
                {182, "Sergey Fomin", "UZB", 1}, {183, "Beibit Zhukayev", "KAZ", 1}, {184, "Denis Yevseyev", "KAZ", 1}, {185, "Timofey Skatov", "KAZ", 1},
                {186, "Dmitry Popko", "KAZ", 1}, {187, "Goncalo Oliveira", "POR", 1}, {188, "Gastao Elias", "POR", 1}, {189, "Henrique Rocha", "POR", 1},
                {190, "Jaime Faria", "POR", 1}, {191, "Frederico Ferreira Silva", "POR", 1}, {192, "Dino Prizmic", "CRO", 1}, {193, "Duje Ajdukovic", "CRO", 1},
                {194, "Mili Poljicak", "CRO", 1}, {195, "Nerman Fatic", "BIH", 1}, {196, "Mirza Basic", "BIH", 1}, {197, "Damir Dzumhur", "BIH", 1},
                {198, "Kalin Ivanovski", "MKD", 1}, {199, "Cezar Cretu", "ROU", 1}
        };

        for (Object[] row : top200Data) {
            list.add(new Player((String) row[1], (int) row[0], (String) row[2], (int) row[3]));
        }

        return list;
    }

    private List<CalendarEvent> generateATP2026Calendar() {
        List<CalendarEvent> cal = new ArrayList<>();

        cal.add(new CalendarEvent(1, "Australian Open", TournamentCategory.GRAND_SLAM, Surface.HARD));
        cal.add(new CalendarEvent(2, "Rotterdam Open", TournamentCategory.ATP_500, Surface.HARD));
        cal.add(new CalendarEvent(3, "Indian Wells Masters", TournamentCategory.ATP_1000, Surface.HARD));
        cal.add(new CalendarEvent(4, "Miami Open", TournamentCategory.ATP_1000, Surface.HARD));
        cal.add(new CalendarEvent(5, "Monte-Carlo Masters", TournamentCategory.ATP_1000, Surface.CLAY));
        cal.add(new CalendarEvent(6, "Madrid Open", TournamentCategory.ATP_1000, Surface.CLAY));
        cal.add(new CalendarEvent(7, "Roland Garros", TournamentCategory.GRAND_SLAM, Surface.CLAY));
        cal.add(new CalendarEvent(8, "Halle Open", TournamentCategory.ATP_500, Surface.GRASS));
        cal.add(new CalendarEvent(9, "Wimbledon", TournamentCategory.GRAND_SLAM, Surface.GRASS));
        cal.add(new CalendarEvent(10, "US Open", TournamentCategory.GRAND_SLAM, Surface.HARD));

        String[] cities = {"Wien", "München", "Doha", "Acapulco", "Barcelona", "Kitzbühel", "Gstaad", "Tokio", "Basel", "Metz", "Stockholm", "Genf"};
        for (int w = 11; w <= 52; w++) {
            String city = cities[rng.nextInt(cities.length)];
            TournamentCategory cat = (w % 4 == 0) ? TournamentCategory.ATP_500 : TournamentCategory.ATP_250;
            Surface surf = (w % 3 == 0) ? Surface.CLAY : Surface.HARD;
            cal.add(new CalendarEvent(w, city + " Open", cat, surf));
        }

        return cal;
    }

    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

    public static void main(String[] args) {
        launch(args);
    }
}