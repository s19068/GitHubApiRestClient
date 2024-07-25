# GitHub Repository Info API

## Opis projektu

Ta aplikacja Spring Boot dostarcza API do pobierania informacji o repozytoriach GitHub użytkownika. Dla podanej nazwy użytkownika, aplikacja zwraca listę repozytoriów, które nie są forkami, wraz z informacjami o gałęziach każdego repozytorium.

### Funkcjonalności

- Pobieranie listy repozytoriów użytkownika GitHub, które nie są forkami
- Dla każdego repozytorium zwracane są:
    - Nazwa repozytorium
    - Login właściciela
    - Lista gałęzi z nazwami i SHA ostatniego commita
- Obsługa błędów, w tym przypadku nieistniejącego użytkownika

## Wymagania

- Java 11 lub nowsza
- Maven
- GitHub Personal Access Token (PAT)

## Konfiguracja

1. Utwórz plik application.yml w katalogu src/main/resources z następującą zawartością:
   ```yml
   github:
    api:
        base-url: https://api.github.com
        token: ${GITHUB_API_TOKEN}
   
2. Wygeneruj Private Access Token na githubie i wklej go jako wartość token:

## Plany na przyszłośc :
1. Przyspieszyć wywołanie, poprzez zastosowanie ParallelFlux

