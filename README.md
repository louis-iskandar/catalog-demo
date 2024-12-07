# README
@Author: Louis Iskandar (07.12.2024)

Arbeitsaufwand: 10 Stunden inklusive Frontend


## Projektbeschreibung

Diese Anwendung wurde nach dem **12-Factor-App-Pattern** entwickelt, wodurch sie portabel und flexibel bleibt. Alle Konfigurationen und sensitiven Daten werden ausschließlich über **Environment-Variablen** verwaltet.

Das Projekt basiert vollständig auf **Spring Boot** und verwendet die folgenden Module:

- **Spring Boot Starter Web**: Zur Bereitstellung von REST-APIs.
- **Spring Boot Actuator**: Für Health Checks und Monitoring.
- **Spring Boot Security**: Für Authentifizierung und Autorisierung (in dieser Anwendung deaktiviert, um die Implementierung zu vereinfachen).

**Hinweis zur Sicherheit:** In einer produktiven Umgebung sollte niemals eine Anwendung ohne Sicherheitsmechanismen betrieben werden. Für die Authentifizierung wird die Verwendung von **Third-Party OAUTH2-Anbietern** (z. B. Google) empfohlen, anstatt eigene Authentifizierungsmechanismen zu entwickeln.

---

## Infrastruktur

- **Build-Tool:** Maven wird für das Management von Abhängigkeiten und den Build-Prozess verwendet.
- **Containerisierung:**
  - **Dockerfile:** Zum Erstellen eines Images.
  - **docker-compose.yml:** Ermöglicht eine einfache lokale Entwicklung ohne aufwändige Systemkonfiguration.
- **Datenbank:** Die Anwendung verwendet PostgreSQL.
- **Datenbankversionierung:**
  - **Flyway:** Für einfache Migrationen, insbesondere wenn SQL-Syntax ausreichend ist, daher vernwenden wir in unsere Anwendung flyway.
  - **Liquibase:** Für komplexere Datenbankänderungen wie bedingte Logik oder dynamische Anpassungen.

---

## Datenbankdesign

Die Datenbank wurde einfach gestaltet, ist jedoch in der Lage, Internationalisierung abzubilden:

- **Tabelle `product`:**
  - Felder: `id`, `price`, `tax_percent`.
- **Tabelle `product_i18n`:**
  - Felder: `product_id`, `locale`, `name`, `description`.
  - Ermöglicht die Speicherung internationalisierter Produktdaten.
- **Naming-Konvention:** Tabellen sind in Kleinbuchstaben, Singular und mit Unterstrichen getrennt (`snake_case`).

![diagram](https://raw.githubusercontent.com/louis-iskandar/catalog-demo/refs/heads/main/src/main/resources/img/db.svg)

---

## REST-Design

### Allgemeines Design

Die REST-API wurde strikt nach REST-Prinzipien entwickelt:

- **Namensgebung:** Ressourcen werden in der Mehrzahl benannt (`/products`, `/files`).
- **HTTP-Methoden:**
  - `POST /products`: Produkt erstellen.
  - `GET /products/{id}`: Produkt abrufen.
  - `PATCH /products/{id}`: Produkt aktualisieren oder ergänzen.
  - `DELETE /products/{id}`: Produkt löschen.
  - `POST /files`: Batch-Upload (z. B. CSV-Import).  
    Beim CSV-Import wird dasselbe `productId` als Update betrachtet. Vorhandene Werte werden durch neue ersetzt.

### Batch-Import-API (`/files`)

- **Synchroner Import (aktuell):**
  - Erfolg: HTTP 201 (Created).
  - Fehler:
    - **4xx:** Client-Fehler (z. B. falsches Dateiformat).
    - **5xx:** Serverfehler.
- **Asynchroner Import (zukünftig):**
  - Rückgabe einer **CorrelationId** mit HTTP 201.
  - Statusabfrage mit `GET /files/{id}`:
    - Status des Imports.
    - Fehlerhafte und erfolgreiche Einträge.
  - Umsetzung über **Spring Job Scheduler** mit Retry-Mechanismen.

---

## HATEOAS

Ich setze standardmäßig auf HATEOAS. In dieser Anwendung beschränke ich es auf minimale Selbstreferenzen, doch in realen Szenarien bietet HATEOAS erhebliche Vorteile:

- **Selbstbeschreibende APIs:** Ressourcen enthalten Links zu relevanten Aktionen.
- **Dynamische Navigation:** Clients können die API flexibel erkunden.
- **Erweiterbarkeit:** Links lassen sich einfach anpassen.
- **Paging:** Effiziente Navigation großer Datenmengen.
- **Internationalisierung:** Unterstützung sprachspezifischer Inhalte.

---

## Fehlerbehandlung

Fehler werden global über eine zentrale Klasse mit **`@ControllerAdvice`** gehandhabt:

- **Einheitliche Fehlerantworten.**
- **Standard-HTTP-Statuscodes:**
  - **4xx:** Client-Fehler (z. B. ungültiges CSV-Format → 422 Unprocessable Entity).
  - **5xx:** Serverfehler (z. B. Datenbankfehler).

---

## Package-Struktur

Die Package-Struktur der Anwendung ist bewusst einfach und klar gestaltet, jedoch eindeutig getrennt. Diese Struktur fördert eine bessere Wartbarkeit und sorgt für eine logische Trennung der Verantwortlichkeiten.

### Übersicht der Package-Struktur

#### 1. **Controller Package**
- **Verantwortung:** Enthält die REST-Controller für die einzelnen Ressourcen.
- **Struktur:** Jeder Controller befindet sich in einem eigenen Unterpackage, das nach der Ressource benannt ist.
  - **Beispiel:** Ein `ProductController` für die Ressource `products`.
- **Zusammenhang:** Jede Ressource hat ihr eigenes Package, das folgende Dateien beinhaltet:
  - REST-Controller.
  - Konfigurationen.
  - Request- und Response-Objekte.
- **Philosophie:** Ich bin ein Fan davon, alle benötigten Dateien für eine Ressource in einem einzigen Package zu finden, um die Übersichtlichkeit zu erhöhen.

#### 2. **Service Package**
- **Verantwortung:** Enthält die gesamte Geschäftslogik.
- **Domain-Driven Design (DDD):**
  - Die Logik für eine Ressource ist in einem dedizierten Service organisiert.
  - Beispiel: `ProductService` verarbeitet die gesamte Logik für Produkte.
- **Domain-Model:** Allgemeine Entitäten wie `Product` und `ProductDetail` befinden sich hier. Diese kennen auch die Details für Internationalisierung.

#### 3. **Exception Package**
- **Verantwortung:** Enthält alle benutzerdefinierten Exceptions der Anwendung.
- **Ziel:** Fehler zentral und einheitlich behandeln, z. B. mit globalem Error Handling.

#### 4. **Mapper Package**
- **Verwendung von MapStruct:**
  - MapStruct wird als Mapper-Framework verwendet.
  - **Warum Mapper?** Sie unterstützen die Isolation der Packages, indem sie Domain-Modelle (`DTOs`) sauber zwischen verschiedenen Schichten der Anwendung übersetzen.
- **Isolation:**
  - Die einzige globale DTO ist die Domain.
  - Ressourcenspezifische DTOs (Request/Response) werden nur innerhalb ihres eigenen Packages verwendet.
  - **Visibilität:** Die meisten Klassen innerhalb eines Packages haben die Standard-Visibilität (default), um die Kapselung sicherzustellen.

#### 5. **Repository Package**
- **Verantwortung:**
  - Enthält die Repository-Klassen für den Zugriff auf Datenquellen.
  - Die Klassen implementieren die spezifische Datenzugriffsschicht.
- **Umsetzung:**
  - Die einzige öffentliche Schnittstelle im Repository-Package ist die Repository-Klasse.
  - Das zugrundeliegende **JPA-Repository** ist auf die Standard-Visibilität (default) beschränkt.
  - **Aktueller Ansatz**: CRUDRepository, **Warum**? Für diese Test-Anwendung genügt CRUDRepository, da nur grundlegende Operationen wie Speichern, Lesen, Aktualisieren und Löschen benötigt werden.
  - Zukünftige Anpassung: JpaRepository In Produktionsumgebungen mit Millionen von Datensätzen wird **JpaRepository** bevorzugt, da es erweiterte Funktionen wie CriteriaBuilder, Sortierung und Paging unterstützt.

- **Grund für Kapselung:**
  - Andere Packages müssen nicht wissen, dass es sich um ein JPA-Repository handelt oder dass eine Datenbank dahinterliegt.
  - Im Sinne des 12-Factor-Prinzips sind Ressourcen austauschbar (mountable). Beispielsweise könnte das Repository auf ein Dateisystem oder eine NoSQL-Datenbank wechseln.
  - **Mapping:** Die Übersetzung zwischen Modell und Entität findet innerhalb des Repository-Packages statt.

### Vorteile der Struktur

1. **Modularität:** Die klare Trennung der Verantwortlichkeiten erleichtert die Wartung und Erweiterung.
2. **Kapselung:** Durch die Verwendung von Standard-Visibilität (default) werden Abhängigkeiten zwischen den Packages minimiert.
3. **Flexibilität:** Die Struktur erlaubt es, die Datenquelle oder Implementierung zu ändern, ohne dass andere Schichten der Anwendung davon betroffen sind.
4. **Lesbarkeit:** Entwickler können sich auf einzelne Packages konzentrieren, ohne andere Bereiche der Anwendung durchsuchen zu müssen.

Die beschriebene Struktur unterstützt die Prinzipien von sauberem Code und fördert eine robuste, skalierbare Architektur.

---

## **CSV-Parser**

Ich benutze **OpenCSV** als Parser, um eine CSV-Datei in Java-POJOs zu konvertieren. Das Mapping erfolgt derzeit über die **Position der Spalten**, was jedoch nicht optimal ist. Stattdessen sollten wir eine feste, eindeutige Header-Naming-Konvention für das CSV-Format definieren.

### Verbesserungsvorschläge

1. **Feste Header-Namen**  
   Die Spalten sollten anhand fester Header-Namen identifiziert werden, nicht durch ihre Position. Das sorgt für bessere Lesbarkeit, Wartbarkeit und vermeidet Fehler, wenn sich die Reihenfolge der Spalten ändert.

2. **Keine Internationalisierung der Header**  
   Ähnlich wie bei Datenbankfeldern sollten die Header-Namen **nicht internationalisiert** werden. Der Inhalt der CSV-Datei kann internationalisiert sein, aber die Struktur (Header-Namen) sollte festgelegt bleiben.

3. **CSV-Format-Konvention**  
   Wir sollten von Anfang an eine klare **Konvention für das CSV-Format** definieren, um Missverständnisse und Fehler zu vermeiden.

### Problem mit der `ProductNumber`

In der letzten Zeile der CSV-Datei ist die `ProductNumber` mit einem zusätzlichen "A" am Ende angegeben (`45353A`). Es ist unklar, ob dies beabsichtigt oder ein Tippfehler ist.

Da diese `ProductNumber` als ID in der Datenbank verwendet wird und ich eine Neuzuweisung vermeiden möchte, habe ich die CSV-Datei so angepasst, dass `45353A` zu `453533` geändert wird.

Falls das "A" gewollt ist, müssten wir die Implementierung entsprechend anpassen. Ich bitte um Klärung, um entweder die Datei dauerhaft zu korrigieren oder die Logik im Code zu aktualisieren.

### Problem mit Trennzeichen und Text-Anführungszeichen

Beim Umgang mit der CSV-Datei sind folgende Probleme aufgefallen:

1. **Text-Anführungszeichen im `productname`**  
   In der Spalte `productname` befinden sich mehrere Anführungszeichen, die nicht korrekt als Anfang und Ende eines Textes interpretiert werden.  
   **Lösung:**  
   Ich habe die Daten korrigiert, sodass Texte immer innerhalb von Anführungszeichen stehen (`"Text"`).

2. **Trennzeichen `;` (Semikolon)**  
   Das Trennzeichen Semikolon stellt kein Problem dar, solange die Inhalte, die Semikola enthalten, korrekt von Anführungszeichen umschlossen sind. Es wird jedoch darauf hingewiesen, dass die Einhaltung dieser Regel essenziell ist.

3. **Zahlenkonvertierung und Lokalisierung**  
   Bei Zahlen müssen wir eine klare Konvention definieren. Dies betrifft insbesondere Dezimaltrennzeichen, die entweder als Punkt (`.`) oder Komma (`,`) dargestellt werden können.  
   **Vorschlag:**
  - Festlegung auf ein einheitliches Format (z. B. Punkt als Dezimaltrennzeichen).
  - Falls erforderlich, Anpassung nach `locale`-Einstellungen.

### Korrigierte Daten

Die korrigierten Daten befinden sich im Verzeichnis:  
`src/resources/csv`  
Bitte nutzen Sie diese Datei für den Import.

### Empfehlung für die Zukunft

- **Konvention vor Flexibilität:**  
  Ich empfehle, von Anfang an klare Konventionen zu definieren. Eine feste Struktur vereinfacht die Automatisierung des Imports und reduziert potenzielle Fehlerquellen.

- **Saubere/normalisierte CSV-Dateien:**  
  Es sollten nur saubere und normalisierte CSV-Dateien importiert werden.

- **Zukunftssicherheit:**  
  Bei Bedarf kann ein komplexerer CSV-Parser mit flexiblerem Mapping eingebaut werden. Allerdings sollten die Grundprinzipien der Datenkonsistenz und Automatisierung weiterhin Vorrang haben.
---

## TEST im Entwicklungsprozess

In einem ordnungsgemäßen Entwicklungsprozess schreibe ich immer Tests für jede Klasse sowie für alle zusammenarbeitenden Klassen oder den gesamten Spring Application Context. Dabei benutze ich in den meisten Fällen **Mockito** für das Mocking.

In diesem Projekt war die Zeit jedoch leider begrenzt, sodass ich keine Tests schreiben konnte. Falls Zeit zur Verfügung steht, werde ich vollständige Unit-Tests mit einer Testabdeckung von über 90% erstellen. Diese Abdeckung überprüfe ich in der Regel mit der **IntelliJ Coverage**-Funktion.

---

## Dokumentation der REST API

Derzeit befindet sich die Dokumentation der REST API in der **README**-Datei.  
In einem professionellen Entwicklungsprozess würde ich die Dokumentation im **OpenAPI-Format** erstellen und mit **Swagger UI** als Endpoint bereitstellen, um eine benutzerfreundliche und interaktive API-Dokumentation anzubieten.

---

## Einrichtung

### Anforderungen

- **Java:** Version 11 oder höher.
- **Spring Boot:** Version 3.3 oder höher.
- **Datenbank:** PostgreSQL (andere relationale Datenbanken wie MySQL möglich).
- **Docker und Docker Compose:** Für Containerisierung und lokale Entwicklung.

#### Schritt 1: Environment-Variablen setzen
Liste der Variablen:

    export DB_HOST=localhost
    export DB_PORT=5432
    export DB_NAME=catalog
    export DB_USERNAME=user
    export DB_PASSWORD=password

#### Schritt 2: Datenbank bereitstellen
Stellen Sie sicher, dass die PostgreSQL-Datenbank vorhanden ist, entweder nativ oder als Docker-Container. Passen Sie die Environment-Variablen entsprechend an.

#### Schritt 3: Build-Prozess
Bauen Sie die Anwendung mit Maven:

    mvn clean install

#### Schritt 4: Docker-Image erstellen
Erstellen Sie ein Docker-Image:

    docker build -t catalog-app .

#### Schritt 5: Anwendung starten
Starten Sie die Anwendung mit Docker:

    docker run -itd --name catalog \
     -eDB_HOST=localhost \
     -eDB_PORT=5432 \
     -eDB_NAME=catalog \
     -eDB_USERNAME=user \
     -eDB_PASSWORD=password \
     catalog-app

### Docker-Compose

Eine einsatzbereite **Docker-Compose**-Konfiguration ist vorhanden, mit der die REST-API direkt gestartet werden kann. So können Sie sofort mit dem Testen beginnen.

**Wichtig:** Vor dem Start mit Docker-Compose muss ein `mvn clean install` ausgeführt werden, da das Dockerfile auf ein zuvor gebautes Artefakt angewiesen ist.

---