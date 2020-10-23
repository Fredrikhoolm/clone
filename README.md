# pgr203eksamen-hkonn
![Master Branchen](https://github.com/kristiania/pgr203eksamen-hkonn/workflows/Java%20CI%20with%20Maven/badge.svg?branch=master)

-----------------------------
	Sjekkliste for innlevering:
	• [ ] Dere har lastet opp en ZIP-fil med navn basert på navnet på deres Github repository 
	• [ ] Koden er sjekket inn på github.com/kristiania-repository
	• [ ] Dere har committed kode med begge prosjektdeltagernes GitHub konto (alternativt: README beskriver arbeidsform) 
	
	README.md:
	• [ ] README.md inneholder en korrekt link til Github Actions
	• [ ] README.md beskriver prosjektets funksjonalitet, hvordan man bygger det og hvordan man kjører det
	• [ ] README.md beskriver eventuell ekstra leveranse utover minimum 
	• [ ] README.md inneholder et diagram som viser datamodellen 
	• [ ] Dere har gitt minst 2 positive og 2 korrektive GitHub issues til en annen gruppe og inkluderer link til disse fra README.md
	
	KODEN:
	• [ ] mvn package bygger en executable jar-fil
	• [ ] Koden inneholder et godt sett med tester 
	• [ ] java -jar target/...jar (etter mvn package ) lar bruker legge til og liste ut data fra databasen via webgrensesnitt 
	• [ ] Programmet leser dataSource.url , dataSource.username og dataSource.password fra pgr203.properties for å connecte til databasen 
	• [ ] Programmet bruker Flywaydb for å sette opp databaseskjema 
	• [ ] Server skriver nyttige loggmeldinger, inkludert informasjon om hvilken URL den kjører på ved oppstart 


	FUNKSJONALITET:
	• [ ] Programmet kan liste prosjektdeltagere fra databasen 
	• [ ] Programmet lar bruker opprette nye prosjektdeltagere i databasen 
	• [ ] Programmet kan opprette og liste prosjektoppgaver fra databasen 
	• [ ] Programmet lar bruker tildele prosjektdeltagere til oppgaver 
	• [ ] Flere prosjektdeltagere kan tildeles til samme oppgave
	• [ ] Programmet lar bruker endre status på en oppgave
	
	
	Vedlegg for ekstrapoeng:
	• [ ] Håndtering av request target "/" 
	• [ ] Avansert datamodell (mer enn 3 tabeller)
	• [ ] Avansert funksjonalitet (redigering av prosjektmedlemmer, statuskategorier, prosjekter) 
	• [ ] Implementasjon av cookies for å konstruere sesjoner: https://tools.ietf.org/html/rfc6265#section-3
	• [ ] UML diagram som dokumenterer datamodell og/eller arkitektur (presentert i README.md) 
	• [ ] Rammeverk rundt Http-håndtering (en god HttpMessage class med HttpRequest og HttpResponse subtyper) som gjenspeiler RFC7230 
	• [ ] God bruk av DAO-pattern [ ] God bruk av Controller-pattern [ ] Korrekt håndtering av norske tegn i HTTP 
	• [ ] Link til video med god demonstrasjon av ping-pong programmering 
	• [ ] Automatisk rapportering av testdekning i Github Actions 
	• [ ] Implementasjon av Chunked Transfer Encoding: https://tools.ietf.org/html/rfc7230#section-4.1 
   [ ] Annet



-------------------------
