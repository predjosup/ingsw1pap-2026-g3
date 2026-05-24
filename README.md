# Blackjack JavaFX

Autori: Somazzi Davide, Djordjevic Predrag

Ingegneria e Sviluppo Software 1  
Java · Maven Multi-Module · JavaFX · Validatore licenza C

## Overview

Questo progetto implementa un gioco Blackjack desktop in JavaFX.

Funzionalita principali:

- controllo licenza tramite modulo C
- puntata e saldo giocatore
- distribuzione carte
- azioni `Hit` e `Stand`
- turno automatico del dealer
- calcolo vincitore
- salvataggio stato e storico partite

## Project Structure

```text
ingsw1pap-2026-g3/
│
├── pom.xml               parent Maven
├── backend/              logica di gioco e servizi
├── frontend/             interfaccia JavaFX
└── license-validator/    validatore licenza in C
```

## Requirements

- Java
- Maven
- IntelliJ IDEA
- MSYS2 UCRT64 con `gcc` e OpenSSL

Verifica:

```bash
java -version
mvn -version
```

## Preparare il modulo C della licenza

Aprire **MSYS2 UCRT64**:

```bash
cd /c/Users/dsoma/IdeaProjects/ingsw1pap-2026-g3/license-validator
mkdir -p bin
gcc license_validator.c -O2 -o bin/license_validator.exe -lcrypto
cp /ucrt64/bin/libcrypto-3-x64.dll bin/
```

La cartella `license-validator/bin` deve contenere:

```text
license_validator.exe
libcrypto-3-x64.dll
```

Test da MSYS2:

```bash
./bin/license_validator.exe BLACKJACK-2026-VALID
```

Test da PowerShell:

```powershell
C:\Users\dsoma\IdeaProjects\ingsw1pap-2026-g3\license-validator\bin\license_validator.exe BLACKJACK-2026-VALID
```

Output atteso:

```text
LICENZA_VALIDA
```

## Build

Dalla root del progetto:

```bash
mvn clean package
```

Il fat JAR viene creato in:

```text
frontend/target/frontend-1.0.0-SNAPSHOT-all.jar
```

## Run da IntelliJ IDEA

Aprire in IntelliJ la root:

```text
C:\Users\dsoma\IdeaProjects\ingsw1pap-2026-g3
```

Poi eseguire dal terminale di IntelliJ:

```bash
mvn -f frontend/pom.xml javafx:run
```

Chiave licenza:

```text
BLACKJACK-2026-VALID
```

Se appare `Errore verifica licenza`, controllare che questi file siano entrambi presenti:

```text
license-validator/bin/license_validator.exe
license-validator/bin/libcrypto-3-x64.dll
```

## Educational Purpose

Il progetto mostra una separazione tra `frontend` JavaFX e `backend`, integrazione con un modulo C esterno, persistenza dello stato e gestione completa di un turno Blackjack.
