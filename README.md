# TennisSimulation

Ein Java-Projekt zur Simulation eines Tennismatches. Zwei Spieler treten mit individuellen Attributen und optionalen Schlägerboni gegeneinander an. Das Programm simuliert einzelne Punkte, Spiele, Sätze und Tiebreaks und gibt anschließend das vollständige Match-Ergebnis aus.

## Funktionen

- Spielerattribute für Aufschlagkraft, Schnelligkeit, Beweglichkeit und Ausdauer
- optionale Schlägerboni für Kraft und Kontrolle
- Berechnung eines Gesamtratings zwischen 60 und 99
- nicht-lineare Gewinnwahrscheinlichkeit mithilfe einer logistischen Funktion
- Aufschlagvorteil abhängig von der Aufschlagkraft
- Simulation von Punkten, Spielen, Sätzen und Tiebreaks
- Formkurve anhand der tatsächlichen und erwarteten Punktgewinne
- Turnierkategorien mit Ranglistenpunkten und Preisgeld
- formatierte Ausgabe des Match-Ergebnisses

## Turnierkategorien

| Kategorie | Punkte für den Sieger | Preisgeld |
|---|---:|---:|
| ATP 250 | 250 | 250.000 USD |
| ATP 500 | 500 | 500.000 USD |
| ATP 1000 | 1.000 | 1.500.000 USD |
| Grand Slam | 2.000 | 3.000.000 USD |

## Voraussetzungen

- Java Development Kit (JDK), empfohlen: Java 17 oder neuer
- Terminal, IntelliJ IDEA, Eclipse oder eine andere Java-Entwicklungsumgebung

## Projekt starten

Wenn sich die Datei `TennisSimulation.java` im aktuellen Ordner befindet:

```bash
javac TennisSimulation.java
java TennisSimulation
```

Alternativ kann die Datei in einer Java-Entwicklungsumgebung geöffnet und die `main`-Methode gestartet werden.

## Beispiel

In der vorhandenen `main`-Methode werden zwei Beispielspieler angelegt. Spieler 2 erhält zusätzlich einen Schlägerbonus. Danach wird ein Grand-Slam-Match im Best-of-3-Format simuliert.

Da das Programm Zufallswerte verwendet, kann das Ergebnis bei jedem Start unterschiedlich ausfallen.

## Aufbau des Programms

- `TournamentCategory`: speichert Turnierpunkte und Preisgeld
- `RacketBonus`: beschreibt Kraft- und Kontrollboni eines Schlägers
- `Player`: enthält Attribute, Rating und Formkurve eines Spielers
- `MatchResult`: speichert und formatiert das Match-Ergebnis
- `simulateGame()`: simuliert ein einzelnes Aufschlagspiel
- `simulateTiebreak()`: simuliert einen Tiebreak
- `simulateMatch()`: simuliert das vollständige Match

## Prompt Diary

Die Datei [`prompt-diary-tennissimulation.md`](prompt-diary-tennissimulation.md) dokumentiert den KI-gestützten Entwicklungsprozess, wichtige Entscheidungen, Tests, Grenzen und mögliche Präsentationsfragen.

## Mögliche Erweiterungen

- automatisierte Tests mit JUnit
- reproduzierbare Ergebnisse durch einen festen Zufalls-Seed
- realistischere Preisgeldverteilung
- grafische Benutzeroberfläche
- Speicherung von Spielern und Ergebnissen
- genauere Modellierung von Belag, Ermüdung und individuellen Spielstilen

## Hinweis zum KI-Einsatz

KI wurde zur Ideenfindung, Strukturierung und Unterstützung bei der Code-Erstellung verwendet. Die Vorschläge müssen fachlich geprüft, lokal getestet und bei der Präsentation vollständig erklärt werden können.

## Projektgruppe

**Gruppe 2 - Software Engineering**
