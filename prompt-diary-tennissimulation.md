# Prompt Diary - TennisSimulation

## 1. Projektüberblick

**Projekt:** Tennis-Match-Simulation in Java  
**Lehrveranstaltung:** Software Engineering  
**Gruppe:** Gruppe 2  
**Ziel:** Entwicklung eines Java-Programms, das ein Tennismatch unter Berücksichtigung von Spielerwerten, Schlägerboni, Aufschlagvorteil, Formkurve, Turnierkategorie, Weltranglistenpunkten und Preisgeld simuliert.

> Hinweis: Dieses Prompt Diary rekonstruiert den im Gruppenchat dokumentierten KI-gestützten Entwicklungsprozess. Der vollständige Wortlaut des extern geteilten KI-Chats lag in der Exportdatei nicht vor. Deshalb werden die Prompts sinngemäß und passend zum vorhandenen Java-Code dokumentiert.

## 2. Ausgangsidee

Die Grundidee war eine objektorientierte Tennissimulation. Zwei Spieler erhalten individuelle Werte für:

- Aufschlagkraft (`power`)
- Schnelligkeit (`speed`)
- Beweglichkeit (`agility`)
- Ausdauer (`stamina`)
- optionale Schlägerboni für Kraft und Kontrolle

Aus diesen Eigenschaften berechnet das Programm ein Gesamtrating. Anschließend werden Punkte, Spiele, Sätze und das vollständige Match simuliert.

## 3. Verwendete KI

**KI-Werkzeug:** Gemini  
**Einsatzgebiet:** Ideenfindung, Strukturierung, Java-Implementierung, mathematische Modellierung, Überprüfung und Verbesserung des Codes.

Die KI wurde als Hilfsmittel verwendet. Die generierten Vorschläge mussten verstanden, geprüft, angepasst und lokal ausgeführt werden.

## 4. Prompt-Verlauf

### Prompt 1 - Grundstruktur des Programms

**Ziel:** Eine erste objektorientierte Struktur für die Tennissimulation erstellen.

**Sinngemäßer Prompt:**

> Erstelle in Java eine Tennissimulation für zwei Spieler. Jeder Spieler soll Werte für Aufschlagkraft, Schnelligkeit, Beweglichkeit und Ausdauer besitzen. Das Programm soll ein Match simulieren und am Ende den Sieger sowie das Satzergebnis ausgeben. Verwende objektorientierte Klassen und verständliche Kommentare.

**Ergebnis:**

- Klasse `TennisSimulation`
- innere Klasse `Player`
- zufallsbasierte Entscheidung über gewonnene Punkte
- Ausgabe eines Match-Ergebnisses

**Bewertung:**

Die erste Struktur bildete eine gute Grundlage, war aber noch zu einfach. Turnierkategorien, Schlägerboni, Tennisregeln und dynamische Spielerform fehlten.

---

### Prompt 2 - Turnierkategorien ergänzen

**Ziel:** Unterschiedliche Turnierarten mit Ranglistenpunkten und Preisgeld modellieren.

**Sinngemäßer Prompt:**

> Ergänze Turnierkategorien ATP 250, ATP 500, ATP 1000 und Grand Slam. Jede Kategorie soll eigene Weltranglistenpunkte und ein Preisgeld für den Sieger enthalten. Nutze dafür möglichst ein Java-Enum.

**Ergebnis:**

Das Enum `TournamentCategory` wurde mit folgenden Werten ergänzt:

| Kategorie | Siegerpunkte | Preisgeld |
|---|---:|---:|
| ATP 250 | 250 | 250.000 |
| ATP 500 | 500 | 500.000 |
| ATP 1000 | 1.000 | 1.500.000 |
| Grand Slam | 2.000 | 3.000.000 |

**Bewertung:**

Das Enum verhindert doppelte Konstanten und verbindet jede Kategorie direkt mit ihren Daten. Für ein Lernprojekt ist diese Lösung übersichtlich und gut erweiterbar.

---

### Prompt 3 - Schlägerboni und Gesamtrating

**Ziel:** Ausrüstung in die Spielerbewertung einbeziehen.

**Sinngemäßer Prompt:**

> Füge einen optionalen Schlägerbonus hinzu. Der Schläger soll Kraft und Kontrolle verbessern. Berechne aus den vier Spielerattributen und den Boni ein Gesamtrating. Das Rating darf nur zwischen 60 und 99 liegen.

**Ergebnis:**

- Klasse `RacketBonus`
- Methoden `getEffectivePower()` und `getEffectiveControl()`
- Methode `getOverallRating()`
- Begrenzung des Ratings auf den Bereich 60 bis 99

**Berechnung:**

Die Kraft wird direkt erhöht. Der Kontrollbonus wird zu gleichen Teilen auf Schnelligkeit und Beweglichkeit verteilt. Danach wird das arithmetische Mittel der vier Werte gebildet.

**Bewertung:**

Die Lösung ist nachvollziehbar und verhindert unrealistische Werte. Die Aufteilung des Kontrollbonus ist eine Modellannahme und keine reale sportwissenschaftliche Formel.

---

### Prompt 4 - Gewinnwahrscheinlichkeit verbessern

**Ziel:** Stärkere Spieler sollen häufiger, aber nicht automatisch gewinnen.

**Sinngemäßer Prompt:**

> Berechne die Gewinnwahrscheinlichkeit nicht nur linear. Verwende eine logistische Funktion, damit kleine Ratingunterschiede moderate Auswirkungen haben und sehr große Unterschiede deutlich werden. Bei Rating 99 gegen 60 soll der bessere Spieler ungefähr 99 Prozent Grundchance besitzen.

**Ergebnis:**

Die Methode `calculateBaseWinProbability()` verwendet eine logistische Funktion:

```java
return 1.0 / (1.0 + Math.exp(-k * diff));
```

Dabei ist `diff` die Differenz zwischen den beiden Gesamtratings. Der Faktor `k` wurde mit `0.1178` kalibriert.

**Bewertung:**

Die logistische Funktion ist für Wahrscheinlichkeitsmodelle geeigneter als eine einfache lineare Berechnung. Trotzdem bleibt die konkrete Kalibrierung eine Annahme des Modells.

---

### Prompt 5 - Aufschlagvorteil berücksichtigen

**Ziel:** Der aktuelle Aufschläger soll abhängig von seiner Aufschlagkraft einen Vorteil erhalten.

**Sinngemäßer Prompt:**

> Berücksichtige bei jedem Punkt den Aufschläger. Spieler mit hoher Aufschlagkraft sollen beim eigenen Service eine höhere Punktchance haben. Begrenze die endgültige Wahrscheinlichkeit auf einen sinnvollen Bereich, damit kein Punkt vollkommen sicher ist.

**Ergebnis:**

- Methode `calculatePointProbability()`
- Aufschlagbonus abhängig von `getEffectivePower()`
- Begrenzung der Punktwahrscheinlichkeit auf 1 bis 99 Prozent

**Bewertung:**

Das Modell verbindet Spielerstärke und Aufschlag. Die Begrenzung verhindert unmögliche Wahrscheinlichkeiten unter 0 oder über 1.

---

### Prompt 6 - Tennisregeln für Spiele und Sätze

**Ziel:** Die Simulation soll den grundlegenden Tennisregeln folgen.

**Sinngemäßer Prompt:**

> Simuliere einzelne Spiele punktweise. Ein Spiel ist gewonnen, wenn ein Spieler mindestens vier Punkte und zwei Punkte Vorsprung hat. Ein Satz endet normalerweise bei mindestens sechs Spielen und zwei Spielen Vorsprung. Bei 6:6 soll ein Tiebreak gespielt werden.

**Ergebnis:**

- Methode `simulateGame()`
- Spielgewinn mit mindestens vier Punkten und zwei Punkten Vorsprung
- Satzgewinn mit mindestens sechs Spielen und zwei Spielen Vorsprung
- Tiebreak bei 6:6

**Bewertung:**

Damit bildet das Programm die wesentliche Zählstruktur eines Tennismatches ab. Die Darstellung 0, 15, 30, 40 wird intern nicht benötigt, weil nur die Anzahl gewonnener Punkte entscheidend ist.

---

### Prompt 7 - Tiebreak und Aufschlagwechsel

**Ziel:** Den Tiebreak gesondert und regelähnlich simulieren.

**Sinngemäßer Prompt:**

> Ergänze einen Tiebreak bis mindestens sieben Punkte mit zwei Punkten Vorsprung. Der Aufschlag soll nach dem ersten Punkt und anschließend nach jeweils zwei Punkten wechseln.

**Ergebnis:**

Die Methode `simulateTiebreak()` zählt die Punkte und wechselt den Aufschläger nach der vorgesehenen Reihenfolge.

**Bewertung:**

Der Tiebreak wurde als eigene Methode sauber von normalen Spielen getrennt. Dadurch bleibt der Code übersichtlicher.

---

### Prompt 8 - Formkurve und Momentum

**Ziel:** Die Spielerwerte sollen sich während des Matches dynamisch verändern können.

**Sinngemäßer Prompt:**

> Füge eine Formkurve hinzu. Vergleiche nach jedem Satz die tatsächlich gewonnenen Punkte eines Spielers mit den statistisch erwarteten Punkten. Passe die Attribute leicht nach oben oder unten an. Begrenze die Änderung und halte alle Werte zwischen 60 und 99.

**Ergebnis:**

- Tracking über `totalPointsWon`
- Tracking über `expectedPointsWon`
- Methode `adjustForm()`
- Anpassung der vier Attribute nach jedem Satz
- maximale Veränderung von plus oder minus fünf Punkten

**Bewertung:**

Das Feature erzeugt Momentum und macht die Simulation dynamischer. Allerdings werden alle vier Attribute gleichzeitig gleich stark verändert. Eine spätere Version könnte differenzierter reagieren.

---

### Prompt 9 - Ergebnisobjekt und Ausgabe

**Ziel:** Alle relevanten Resultate strukturiert zurückgeben.

**Sinngemäßer Prompt:**

> Erstelle eine Klasse für das Match-Ergebnis. Sie soll Sieger, Verlierer, gewonnene Sätze, Satzresultate, Ranglistenpunkte und Preisgeld enthalten. Formatiere die Konsolenausgabe übersichtlich.

**Ergebnis:**

Die Klasse `MatchResult` speichert:

- Sieger und Verlierer
- Anzahl gewonnener Sätze
- einzelne Satzstände
- Weltranglistenpunkte
- Preisgeld

Die Methode `toString()` erzeugt eine formatierte Konsolenausgabe.

**Bewertung:**

Die Trennung zwischen Simulation und Ergebnisdarstellung verbessert die Struktur. Das Resultat könnte später auch für eine grafische Oberfläche oder eine Datei-Ausgabe verwendet werden.

---

### Prompt 10 - Beispiel und Programmstart

**Ziel:** Das Programm direkt ausführbar machen.

**Sinngemäßer Prompt:**

> Ergänze eine `main`-Methode mit zwei Beispielspielern. Einer der Spieler soll einen Schlägerbonus haben. Gib zuerst beide Start-Ratings und danach das Ergebnis eines Best-of-3-Matches bei einem Grand Slam aus.

**Ergebnis:**

- zwei Beispielspieler
- Schlägerbonus für Spieler 2
- Ausgabe der Start-Ratings
- Grand-Slam-Simulation im Best-of-3-Modus
- Ausgabe des vollständigen Ergebnisses

**Bewertung:**

Durch die `main`-Methode kann das Projekt unmittelbar getestet und bei der Präsentation demonstriert werden.

## 5. Wichtige manuelle Entscheidungen

Die KI-Vorschläge wurden nicht unverändert als fachlich exakte Realität betrachtet. Folgende Modellentscheidungen mussten bewusst getroffen oder überprüft werden:

- Wertebereich der Spielerattribute: 60 bis 99
- Verteilung des Kontrollbonus auf Schnelligkeit und Beweglichkeit
- Stärke des Aufschlagbonus
- Kalibrierung der logistischen Funktion
- maximale Formänderung nach einem Satz
- Höhe der hinterlegten Preisgelder
- Verwendung eines Best-of-3-Beispiels beim Grand Slam

Diese Werte dienen der Simulation und können später angepasst werden.

## 6. Tests und Überprüfung

Für die Präsentation sollten mindestens folgende Fälle lokal ausgeführt werden:

| Testfall | Erwartetes Verhalten |
|---|---|
| Zwei gleich starke Spieler | Gewinnchancen ungefähr ausgeglichen |
| Rating 99 gegen Rating 60 | Sehr deutlicher Vorteil für den stärkeren Spieler |
| Spieler mit hohem Aufschlagwert | Höhere Punktchance beim eigenen Service |
| Satzstand 6:6 | Tiebreak wird gestartet |
| Langer Gleichstand im Spiel | Spiel endet erst mit zwei Punkten Vorsprung |
| Spieler übertrifft Erwartungswert | Attribute steigen nach dem Satz leicht |
| Spieler unterschreitet Erwartungswert | Attribute sinken nach dem Satz leicht |
| Schlägerbonus ist `null` | Programm läuft ohne Fehler |

Da `Random` ohne festen Seed verwendet wird, kann dasselbe Match bei jedem Programmstart anders ausgehen. Für reproduzierbare Tests könnte später ein fester Seed oder ein übergebbares `Random`-Objekt verwendet werden.

## 7. Erkannte Grenzen und mögliche Verbesserungen

- **Reproduzierbarkeit:** Zufallsergebnisse sind ohne festen Seed nicht wiederholbar.
- **Preisgeldmodell:** Das gesamte hinterlegte Preisgeld wird aktuell dem Sieger zugewiesen. In realen Turnieren ist die Verteilung komplexer.
- **Grand-Slam-Regeln:** Das Beispiel nutzt Best-of-3; je nach Wettbewerb können andere Formate gelten.
- **Aufschlagmodell:** Die Formel kann bei hohen Grundwahrscheinlichkeiten schnell an die Begrenzung von 99 Prozent stoßen.
- **Formkurve:** Alle Attribute verändern sich gemeinsam, obwohl einzelne Bereiche getrennt modelliert werden könnten.
- **Testbarkeit:** Automatisierte Unit-Tests, etwa mit JUnit, fehlen noch.
- **Benutzeroberfläche:** Die Anwendung läuft nur in der Konsole.
- **Datenhaltung:** Spieler und Ergebnisse werden nicht dauerhaft gespeichert.

## 8. Verantwortlicher Umgang mit KI

Die KI unterstützte bei Struktur, Formeln und Codevorschlägen. Für die Abgabe und Präsentation bleibt entscheidend:

- den gesamten Code selbst erklären zu können,
- das Projekt auf dem eigenen Rechner auszuführen,
- Annahmen und Grenzen offen zu benennen,
- generierten Code auf Fehler zu prüfen,
- keine KI-Ausgabe ungeprüft als fachlich korrekt zu übernehmen.

## 9. Kurzfassung für die Präsentation

1. **Problem:** Ein Tennismatch soll realistisch genug, aber verständlich simuliert werden.
2. **Modell:** Spieler besitzen vier Attribute und optionale Schlägerboni.
3. **Wahrscheinlichkeit:** Eine logistische Funktion bewertet den Ratingunterschied.
4. **Matchlogik:** Punkte ergeben Spiele, Spiele ergeben Sätze, bei 6:6 folgt ein Tiebreak.
5. **Dynamik:** Eine Formkurve verändert die Spielerwerte nach jedem Satz.
6. **Turnierdaten:** Kategorie, Ranglistenpunkte und Preisgeld werden über ein Enum verwaltet.
7. **KI-Einsatz:** Die KI lieferte Vorschläge; Struktur, Parameter, Prüfung und Verständnis blieben Aufgabe der Gruppe.
8. **Ausblick:** Unit-Tests, reproduzierbarer Zufall, GUI und differenziertere Modelle.

## 10. Mögliche Fragen bei der Präsentation

### Warum wurde eine logistische Funktion verwendet?

Sie liefert Werte zwischen 0 und 1 und bildet Ratingunterschiede nicht linear ab. Dadurch haben ähnlich starke Spieler ähnliche Chancen, während große Unterschiede deutlicher wirken.

### Warum wird die Wahrscheinlichkeit begrenzt?

Damit kein Punkt absolut sicher oder vollständig unmöglich ist. Selbst ein schwächerer Spieler behält eine kleine Chance.

### Was bedeutet `expectedPointsWon`?

Der Wert summiert vor jedem simulierten Punkt die rechnerische Gewinnwahrscheinlichkeit. So kann nach einem Satz verglichen werden, ob ein Spieler besser oder schlechter als erwartet gespielt hat.

### Wie wird ein Spiel gewonnen?

Ein Spieler benötigt mindestens vier gewonnene Punkte und zwei Punkte Vorsprung. Dadurch sind auch längere Einstandssituationen möglich.

### Wie wird der Tiebreak simuliert?

Er endet ab sieben Punkten mit mindestens zwei Punkten Vorsprung. Nach dem ersten Punkt wechselt der Aufschlag, anschließend nach jeweils zwei Punkten.

### Warum kann jedes Ergebnis anders sein?

Die Klasse `Random` erzeugt bei jedem Lauf neue Zufallswerte. Ein fester Seed würde reproduzierbare Ergebnisse ermöglichen.

### Welche Teile könnten verbessert werden?

Vor allem automatisierte Tests, ein genaueres Aufschlag- und Formmodell, realistischere Preisgeldverteilung, Datenhaltung und eine grafische Benutzeroberfläche.

## 11. Fazit

Das Projekt zeigt die Kombination aus objektorientierter Java-Programmierung, Wahrscheinlichkeitsrechnung und regelbasierter Simulation. Die KI beschleunigte die Entwicklung und half bei Lösungsansätzen. Für ein belastbares Ergebnis waren dennoch menschliche Entscheidungen, Prüfung, lokale Tests und ein vollständiges Verständnis des Codes notwendig.
