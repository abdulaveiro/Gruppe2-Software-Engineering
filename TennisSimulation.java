import java.util.Random;

public class TennisSimulation {

    public enum TournamentCategory {
        ATP_250(250, 250000),
        ATP_500(500, 500000),
        ATP_1000(1000, 1500000),
        GRAND_SLAM(2000, 3000000);

        final int winnerPoints;
        final double prizePool;

        TournamentCategory(int winnerPoints, double prizePool) {
            this.winnerPoints = winnerPoints;
            this.prizePool = prizePool;
        }
    }

    public static class RacketBonus {
        int power;
        int control;

        public RacketBonus(int power, int control) {
            this.power = power;
            this.control = control;
        }
    }

    public static class Player {
        String name;
        int power;      // Aufschlagkraft
        int speed;      // Schnelligkeit
        int agility;    // Beweglichkeit
        int stamina;    // Ausdauer
        RacketBonus bonus;

        // Statistische Werte fuer Momentum und Formkurve
        int totalPointsWon = 0;
        double expectedPointsWon = 0.0;

        public Player(String name, int power, int speed, int agility,
                      int stamina, RacketBonus bonus) {
            this.name = name;
            this.power = power;
            this.speed = speed;
            this.agility = agility;
            this.stamina = stamina;
            this.bonus = bonus;
        }

        public double getEffectivePower() {
            return power + (bonus != null ? bonus.power : 0);
        }

        public double getEffectiveControl() {
            return bonus != null ? bonus.control : 0;
        }

        /**
         * Berechnet das Gesamtrating als Mittelwert der vier Attribute
         * inklusive Boni und begrenzt es auf den Bereich 60 bis 99.
         */
        public double getOverallRating() {
            double totalPower = getEffectivePower();
            double totalControl = getEffectiveControl();

            double average = (
                totalPower
                    + (speed + totalControl / 2.0)
                    + (agility + totalControl / 2.0)
                    + stamina
            ) / 4.0;

            return Math.max(60.0, Math.min(99.0, average));
        }

        /**
         * Passt die Form nach jedem Satz anhand der Abweichung zwischen
         * tatsaechlich und statistisch erwartet gewonnenen Punkten an.
         */
        public void adjustForm(int pointsInSet, double expectedPointsInSet) {
            double difference = pointsInSet - expectedPointsInSet;
            int adjustment = (int) Math.round(
                Math.max(-5.0, Math.min(5.0, difference * 0.25))
            );

            power = clampAttribute(power + adjustment);
            speed = clampAttribute(speed + adjustment);
            agility = clampAttribute(agility + adjustment);
            stamina = clampAttribute(stamina + adjustment);
        }

        private int clampAttribute(int value) {
            return Math.max(60, Math.min(99, value));
        }
    }

    public static class MatchResult {
        Player winner;
        Player loser;
        int p1SetsWon;
        int p2SetsWon;
        String scoreDetails;
        int pointsAwarded;
        double prizeMoneyAwarded;

        @Override
        public String toString() {
            return String.format(
                "==========================================%n"
                    + "MATCH-ERGEBNIS%n"
                    + "==========================================%n"
                    + "Sieger: %s (%d : %d Saetze)%n"
                    + "Satz-Ergebnisse: %s%n"
                    + "Weltranglistenpunkte fuer Sieger: %d Pkt.%n"
                    + "Preisgeld fuer Sieger: $%.2f%n"
                    + "==========================================",
                winner.name,
                p1SetsWon,
                p2SetsWon,
                scoreDetails,
                pointsAwarded,
                prizeMoneyAwarded
            );
        }
    }

    private final Player p1;
    private final Player p2;
    private final TournamentCategory category;
    private final Random random = new Random();

    public TennisSimulation(Player p1, Player p2,
                            TournamentCategory category) {
        this.p1 = p1;
        this.p2 = p2;
        this.category = category;
    }

    /**
     * Berechnet die Grundwahrscheinlichkeit fuer Spieler 1 mithilfe
     * einer logistischen Funktion.
     */
    private double calculateBaseWinProbability(double rating1, double rating2) {
        if (rating1 == rating2) {
            return 0.50;
        }

        // Kalibriert fuer ungefaehr 99 Prozent bei Rating 99 gegen 60.
        double k = 0.1178;
        double difference = rating1 - rating2;
        return 1.0 / (1.0 + Math.exp(-k * difference));
    }

    /**
     * Berechnet die Punktchance von Spieler 1 unter Beruecksichtigung
     * des aktuellen Aufschlaegers.
     */
    private double calculatePointProbability(Player server) {
        double serverDirection = server == p1 ? 1.0 : -1.0;

        double baseProbabilityP1 = calculateBaseWinProbability(
            p1.getOverallRating(),
            p2.getOverallRating()
        );

        double serverPower = server.getEffectivePower();
        double serveBoost = ((serverPower - 60.0) / 39.0) * 0.35;
        double finalProbabilityP1 =
            baseProbabilityP1 + serverDirection * serveBoost;

        return Math.max(0.01, Math.min(0.99, finalProbabilityP1));
    }

    public MatchResult simulateMatch(int bestOfSets) {
        if (bestOfSets < 1 || bestOfSets % 2 == 0) {
            throw new IllegalArgumentException(
                "bestOfSets muss eine positive ungerade Zahl sein."
            );
        }

        int setsToWin = bestOfSets / 2 + 1;
        int p1Sets = 0;
        int p2Sets = 0;

        StringBuilder scoreBuilder = new StringBuilder();
        Player currentServer = p1;

        while (p1Sets < setsToWin && p2Sets < setsToWin) {
            int p1PointsBefore = p1.totalPointsWon;
            int p2PointsBefore = p2.totalPointsWon;
            double p1ExpectedBefore = p1.expectedPointsWon;
            double p2ExpectedBefore = p2.expectedPointsWon;

            int p1Games = 0;
            int p2Games = 0;

            while (true) {
                if (p1Games == 6 && p2Games == 6) {
                    boolean p1WonTiebreak = simulateTiebreak(currentServer);

                    if (p1WonTiebreak) {
                        p1Games++;
                    } else {
                        p2Games++;
                    }

                    break;
                }

                boolean p1WonGame = simulateGame(currentServer);

                if (p1WonGame) {
                    p1Games++;
                } else {
                    p2Games++;
                }

                // Aufschlagwechsel nach jedem Spiel
                currentServer = currentServer == p1 ? p2 : p1;

                if ((p1Games >= 6 || p2Games >= 6)
                    && Math.abs(p1Games - p2Games) >= 2) {
                    break;
                }
            }

            if (p1Games > p2Games) {
                p1Sets++;
            } else {
                p2Sets++;
            }

            if (scoreBuilder.length() > 0) {
                scoreBuilder.append(" ");
            }
            scoreBuilder.append(p1Games).append("-").append(p2Games);

            p1.adjustForm(
                p1.totalPointsWon - p1PointsBefore,
                p1.expectedPointsWon - p1ExpectedBefore
            );
            p2.adjustForm(
                p2.totalPointsWon - p2PointsBefore,
                p2.expectedPointsWon - p2ExpectedBefore
            );
        }

        MatchResult result = new MatchResult();
        boolean p1Won = p1Sets > p2Sets;

        result.winner = p1Won ? p1 : p2;
        result.loser = p1Won ? p2 : p1;
        result.p1SetsWon = p1Sets;
        result.p2SetsWon = p2Sets;
        result.scoreDetails = scoreBuilder.toString();
        result.pointsAwarded = category.winnerPoints;
        result.prizeMoneyAwarded = category.prizePool;

        return result;
    }

    private boolean simulateGame(Player server) {
        int p1Score = 0;
        int p2Score = 0;

        while (true) {
            boolean p1WonPoint = simulatePoint(server);

            if (p1WonPoint) {
                p1Score++;
            } else {
                p2Score++;
            }

            if (p1Score >= 4 && p1Score - p2Score >= 2) {
                return true;
            }
            if (p2Score >= 4 && p2Score - p1Score >= 2) {
                return false;
            }
        }
    }

    private boolean simulatePoint(Player server) {
        double p1Probability = calculatePointProbability(server);

        p1.expectedPointsWon += p1Probability;
        p2.expectedPointsWon += 1.0 - p1Probability;

        boolean p1WonPoint = random.nextDouble() <= p1Probability;
        if (p1WonPoint) {
            p1.totalPointsWon++;
        } else {
            p2.totalPointsWon++;
        }

        return p1WonPoint;
    }

    private boolean simulateTiebreak(Player initialServer) {
        int p1Points = 0;
        int p2Points = 0;
        int totalPoints = 0;
        Player currentServer = initialServer;

        while (true) {
            boolean p1WonPoint = simulatePoint(currentServer);

            if (p1WonPoint) {
                p1Points++;
            } else {
                p2Points++;
            }

            totalPoints++;

            // Wechsel nach dem ersten Punkt, danach nach jeweils zwei Punkten.
            if (totalPoints % 2 == 1) {
                currentServer = currentServer == p1 ? p2 : p1;
            }

            if (p1Points >= 7 && p1Points - p2Points >= 2) {
                return true;
            }
            if (p2Points >= 7 && p2Points - p1Points >= 2) {
                return false;
            }
        }
    }

    public static void main(String[] args) {
        Player p1 = new Player(
            "Spieler 1",
            80,
            75,
            75,
            90,
            null
        );

        RacketBonus bonusP2 = new RacketBonus(5, 3);
        Player p2 = new Player(
            "Spieler 2",
            83,
            70,
            70,
            79,
            bonusP2
        );

        System.out.printf(
            "Start-Rating Spieler 1: %.2f%n",
            p1.getOverallRating()
        );
        System.out.printf(
            "Start-Rating Spieler 2: %.2f%n",
            p2.getOverallRating()
        );

        TennisSimulation simulation = new TennisSimulation(
            p1,
            p2,
            TournamentCategory.GRAND_SLAM
        );

        MatchResult result = simulation.simulateMatch(3);
        System.out.println();
        System.out.println(result);
    }
}
