# Plan testiranja i usporedbe algoritama

## Cilj testiranja

Cilj testiranja je usporediti algoritme za izbor vođe u prstenu prema broju poslanih poruka.

Uspoređujemo:

* Chang & Roberts,
* Hirschberg & Sinclair,
* Petersonov algoritam.

Za svaki algoritam provjeravamo:

* je li izabran ispravan vođa,
* koliko je poruka poslano,
* koliko je faza ili koraka bilo potrebno,
* kako se broj poruka mijenja s veličinom prstena.

## Ispravnost

Za svaki test vrijedi:

```text
izabrani vođa mora biti proces s najvećim ID-em
```

Ako algoritam ne izabere najveći ID, test nije prošao.

## Ulazni podaci

Algoritme ćemo pokretati za različite veličine prstena:

```text
N = 3
N = 5
N = 10
N = 50
N = 100
N = 500
N = 1000
```

Za svaku veličinu prstena koristit ćemo različite rasporede identifikatora.

## Vrste testova

### 1. Mali ručni testovi

Primjeri:

```text
[3, 1, 2]
[10, 5, 20, 7]
[4, 1, 3, 2]
```

Ovi testovi služe za provjeru osnovne ispravnosti.

### 2. Slučajni raspored ID-eva

Za svaki N generiramo slučajnu permutaciju identifikatora.

Primjer:

```text
N = 10
ID-evi = [7, 2, 10, 1, 5, 9, 3, 8, 4, 6]
```

Ovo pokazuje prosječno ponašanje algoritma.

### 3. Nepovoljan raspored ID-eva

Za Chang & Roberts posebno je zanimljiv poredak koji dovodi do velikog broja poruka.

Primjer:

```text
ID-evi = [1, 2, 3, 4, 5, ..., N]
```

ili obrnuti raspored, ovisno o smjeru slanja poruka.

Ovaj test služi za prikaz najgoreg slučaja.

## Metrike koje mjerimo

Za svako pokretanje zapisujemo:

* naziv algoritma,
* broj procesa N,
* tip testa,
* izabrani ID vođe,
* očekivani ID vođe,
* broj poruka,
* broj faza ili koraka,
* je li rezultat točan.

## Format rezultata

Rezultate spremamo u CSV datoteku `results/results.csv`.

Stupci:

```text
algorithm,n,case,run,leader_id,expected_leader_id,message_count,rounds_or_phases,correct
```

Primjer retka:

```text
HirschbergSinclair,100,random,1,100,100,734,7,true
```

## Očekivani zaključak

Očekujemo da će Chang & Roberts u nepovoljnom rasporedu imati puno veći broj poruka od H&S i Petersonovog algoritma.

Hirschberg & Sinclair i Petersonov algoritam trebali bi pokazati rast koji odgovara složenosti O(N log N).

Petersonov algoritam posebno je zanimljiv jer postiže sličnu složenost kao H&S, ali u jednosmjernom prstenu.
