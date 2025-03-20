# ExchangeCurrencyApp

Aplikacja REST API do zarządzania kontami walutowymi oraz wymiany walut PLN <-> USD.

#### ✅ **1. Uruchomienie aplikacji**
1. otwórz terminal
2. pobierz repozytirium 
'git clone https://github.com/tmakowski/ExchangeCurrencyApp.git'
3. przejdź do katalogu z repozytorium 'cd ExchangeCurrencyApp'
4. uruchom komendy 'mvn clean install' oraz 'mvn spring-boot:run'
5. aplikacja powinna być dostępna pod 'http://localhost:8080'
6. poniżej znajdują się przykłady użycia poszczególnych API w Postmanie

#### ✅ **2. Przykłady użycia API**
```md
## Przykłady użycia API

### 1. Założenie konta
**Endpoint:** `POST /api/accounts`

**Przykładowe żądanie:**
```json
{
  "firstName": "Jan",
  "lastName": "Kowalski",
  "initialPlnBalance": 500
}


### 2. Wymiana PLN -> USD
**Endpoint:** `POST /api/exchange/{accountId}`

{
    "id": "123e4567-e89b-12d3-a456-426614174000",
    "firstName": "Jan",
    "lastName": "Kowalski",
    "plnBalance": 0,
    "usdBalance": 129.96,
    "createdAt": "2025-03-20T11:02:04.59064",
    "updatedAt": "2025-03-20T11:19:29.788209"
}


### 3. Pobranie informacji o koncie
**Endpoint:** `GET /api/accounts/{accountId}`

{
    "id": "123e4567-e89b-12d3-a456-426614174000",
    "firstName": "Jan",
    "lastName": "Kowalski",
    "plnBalance": 500.00,
    "usdBalance": 0.00,
    "createdAt": "2025-03-20T11:02:04.59064",
    "updatedAt": "2025-03-20T11:03:33.54693"
}