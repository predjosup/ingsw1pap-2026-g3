# BlackjackG3 - Progetto IngSw1

Implementazione JavaFX + backend Java con validazione licenza tramite eseguibile C esterno.

## Stack

- Java 17
- Maven multi modulo
- JavaFX (frontend)
- Backend separato con logica di gioco
- Persistenza su file (no DB)
- Validatore licenza in C (`license-validator`)

## Struttura

```text
BlackjackG3/
├── pom.xml
├── backend/
├── frontend/
│   └── src/main/resources/i18n/
├── license-validator/
├── mockup/windows-minimal/
├── GUIDELINE_MOCKUP_WINDOWS_IT.md
└── README.md
```

File generati durante build o utilizzo:

- `backend/target/`
- `frontend/target/`
- `frontend/saved/`
- `license-validator/bin/`

## Requisiti v1 implementati

1. Azioni principali: `Hit`, `Stand`.
2. Dealer sta a 17.
3. Blackjack naturale pagato 3:2.
4. Versione base con 1 mazzo.
5. Saldo iniziale 100, puntata minima 10.
6. Se saldo < puntata minima: stato `Game Over`, partita bloccata.
7. Persistenza minima: profilo, saldo, stato partita, cronologia mani.
8. Scelta iniziale tra ripresa salvataggio e nuova partita.
9. Salvataggio automatico a fine mano e agli eventi principali.
10. Licenza integrata con CLI esterna in C chiamata da Java.
11. Inserimento manuale del codice o caricamento da file locale.
12. Schermata iniziale di licenza prima di entrare nel gioco.
13. Se licenza non valida: il gioco non viene aperto.
14. Interfaccia disponibile in `Italiano` e `English`.

## UI implementata

- Schermata `Licenza`:
  - inserimento manuale del codice
  - caricamento da file locale
  - validazione prima dell'accesso al gioco
  - selettore lingua `IT / EN`
- Tab `Gioco`:
  - Playfield con sfondo tavolo blackjack.
  - Carte banco e giocatore centrate.
  - Carte visuali in stile carta da gioco, con retro per la carta coperta del banco.
  - Area carte con scroll orizzontale (gestione molte carte senza rompere layout).
  - Pannello puntata: `+10`, `+25`, `+50`, `Annulla`, `Nuova mano`.
  - Azioni mano: `Hit`, `Stand / Chiudi round`.
  - Stato partita, riepilogo ultima mano e pulsante `Nuova partita`.
  - selettore lingua `IT / EN` nella barra titolo
- Tab `Profilo`:
  - Aggiornamento nome giocatore.
  - Visualizzazione saldo.
- Tab `Cronologia`:
  - Storico mani concluse con esito e saldo.

## Regole implementate

- `Hit`: pesca una carta.
- `Stand`: chiude il turno giocatore e il round, poi gioca il dealer.
- Dealer pesca finche punteggio < 17.
- Esiti supportati: `WIN`, `LOSE`, `PUSH`, `BLACKJACK`.
- Aggiornamento saldo:
  - Win: `+puntata`
  - Lose: `-puntata`
  - Push: `0`
  - Blackjack naturale: `+puntata * 1.5`
- Se il saldo finale scende sotto la puntata minima, la partita entra in `Game Over`.

## Persistenza

File di salvataggio:

```text
saved/game-state.dat
```

La cartella `frontend/saved/` viene creata durante l'esecuzione se non esiste.

Contenuto persistito:

- nome profilo
- saldo
- puntata corrente
- stato mano (carte/punteggi/fase)
- cronologia mani

Comportamento:

- all'avvio, dopo la licenza, l'utente puo scegliere tra `Riprendi partita` e `Nuova partita`
- `Nuova partita` resetta saldo, mano e cronologia del salvataggio precedente
- il salvataggio viene aggiornato automaticamente a fine mano e durante gli eventi principali di gioco

## Licenza esterna (C)

Cartella:

```text
license-validator/
```

File principali:

- `license_validator.c`
- `license.key`
- `build.sh` (Linux/macOS)
- `build.bat` (Windows)

Flusso implementato:

- Java mostra una schermata grafica iniziale.
- L'utente puo inserire un codice o caricare un file locale.
- Java legge il contenuto e passa il codice al validatore in C.
- Il componente in C restituisce esito valido/non valido.

Chiave valida di esempio:

```text
BLACKJACK-2026-VALID
```

## Build e avvio

1. Compilare il validatore C.

Linux/macOS:

```bash
cd license-validator
./build.sh
```

Windows:

```bat
cd license-validator
build.bat
```

2. Build/install Maven completo:

```bash
mvn -DskipTests install
```

3. Avvio frontend JavaFX:

```bash
mvn -f frontend/pom.xml javafx:run
```

All'avvio viene mostrata una schermata licenza.
Puoi digitare manualmente il codice oppure caricare un file locale prima di entrare nel gioco.

Parametri opzionali per percorsi personalizzati:

```bash
-Dblackjack.validator.path=/percorso/validator
-Dblackjack.license.path=/percorso/license.key
```

## Mockup e guideline

- Mockup HTML/CSS: `mockup/windows-minimal/`
- Guideline UI seguite: `GUIDELINE_MOCKUP_WINDOWS_IT.md`

## Internationalization

Cartella risorse:

```text
frontend/src/main/resources/i18n/
```

File:

- `messages_it.properties`
- `messages_en.properties`

Approccio usato:

- stringhe UI esternalizzate in file `.properties`
- `ResourceBundle` caricato dal frontend
- testi statici FXML letti con chiavi `%...`
- messaggi dinamici di stato e cronologia gestiti tramite chiavi e tradotti nel controller
- cambio lingua semplice con pulsanti `IT / EN` e reload della scena corrente
