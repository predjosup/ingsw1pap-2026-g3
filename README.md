# Applicazione Desktop Blackjack

## Descrizione del Progetto

Il presente progetto consiste nello sviluppo di una **applicazione desktop del gioco Blackjack**, destinata a un pubblico generico.

L’applicazione è realizzata seguendo i principi dell’**Ingegneria del Software**, includendo:

* analisi dei requisiti
* progettazione architetturale
* sviluppo iterativo e incrementale

Il sistema consente all’utente di giocare contro un banco automatizzato, gestendo lo stato della partita e controllando l’accesso tramite un sistema di licenza.

---

## Problema Affrontato

L’obiettivo è progettare e sviluppare un’applicazione completa in assenza di specifiche completamente definite.

Questo comporta:

* analisi e definizione dei requisiti
* identificazione delle ambiguità
* formulazione di assunzioni progettuali
* progettazione di una soluzione coerente

---

## Obiettivi

Gli obiettivi principali del progetto sono:

* Sviluppare la logica di gioco del Blackjack
* Realizzare un’interfaccia grafica tramite **JavaFX**
* Garantire la separazione tra **Frontend e Backend**
* Integrare un sistema di **validazione della licenza sviluppato in C**
* Implementare il **salvataggio e ripristino dello stato** senza l’uso di database relazionali
* Applicare buone pratiche di ingegneria del software (modularità, UML, documentazione)

---

## Human Interface Guidelines (HIG)

L’interfaccia grafica è progettata seguendo le:

**Linee guida di Windows (Fluent Design System)**

Principi adottati:

* Layout pulito e minimalista
* Utilizzo di controlli standard Windows (menu, pulsanti, finestre)
* Gerarchia visiva chiara
* Interazioni intuitive per l’utente

---

## Tecnologie Utilizzate

* **Java (JDK 21)** – Logica applicativa
* **JavaFX** – Interfaccia grafica
* **C** – Modulo esterno per la validazione della licenza
* **Maven** – Gestione del build e delle dipendenze
* **Git / GitHub** – Versionamento del codice

---

## Architettura del Sistema

Il sistema è strutturato in modo modulare:

* **Frontend (JavaFX)**
  Gestisce la presentazione e l’interazione con l’utente

* **Backend (Java)**
  Contiene la logica di gioco e le regole del dominio

* **Modulo Licenza (C)**
  Componente esterno per la verifica dell’autorizzazione all’utilizzo

---

## Funzionalità Principali

* Giocare a Blackjack contro un banco automatizzato
* Eseguire azioni di gioco (Hit, Stand, Split)
* Gestire puntate e saldo
* Salvare e caricare lo stato della partita
* Verificare la licenza all’avvio dell’applicazione
* Supporto per più lingue (opzionale)

---

## Membri del Gruppo

* Davide Somazzi
* Predrag Djordjevic

---

## Istruzioni per Build ed Esecuzione

### Prerequisiti

* Java JDK 21
* Maven 3.9+

### Build del progetto

```bash
mvn clean install
```

### Avvio dell’applicazione

```bash
mvn javafx:run
```

---

## Convenzioni di Commit

Per garantire coerenza, tracciabilità e chiarezza nello sviluppo, il team adotta una convenzione standard per i messaggi di commit.

### Formato del messaggio

Ogni commit deve seguire il seguente formato:

```text
#<numero-issue> - <tipo>: <breve descrizione>
```

### Esempi

```text
#1 - feat: creata struttura base progetto Maven
#2 - feat: implementata logica base del mazzo di carte
#3 - fix: corretto calcolo punteggio con asso
#4 - refactor: separata logica frontend/backend
#5 - docs: aggiornato README
```

---

### Tipologie di commit

I principali tipi utilizzati sono:

* `feat` → nuova funzionalità
* `fix` → correzione di bug
* `refactor` → modifica del codice senza cambiamenti funzionali
* `docs` → modifiche alla documentazione
* `style` → modifiche di formattazione (indentazione, naming, ecc.)
* `test` → aggiunta o modifica di test

---

## Note

Il progetto è sviluppato nell’ambito del corso:

**Ingegneria e Sviluppo Software 1 – Semestre Primaverile 2026**
