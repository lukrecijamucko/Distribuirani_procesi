# Petersonov algoritam za izbor vođe u jednosmjernom prstenu

## Ideja algoritma

Petersonov algoritam rješava problem izbora vođe u jednosmjernom prstenu. Za razliku od algoritma Hirschberg & Sinclair, koji koristi dvosmjernu komunikaciju, Petersonov algoritam koristi samo jedan smjer komunikacije, ali i dalje postiže komunikacijsku složenost reda O(N log N).

Osnovna ideja algoritma je da se procesi postupno eliminiraju iz izbora. Na početku su svi procesi aktivni kandidati. Tijekom izvođenja algoritma neki procesi ostaju aktivni, a neki postaju pasivni. Pasivni procesi više ne sudjeluju kao kandidati, ali i dalje prosljeđuju poruke kroz prsten.

Na kraju ostaje jedan aktivni kandidat, odnosno proces s najvećim identifikatorom, koji se proglašava vođom.

## Model

Pretpostavljamo:

* procese povezane u prsten,
* jednosmjernu komunikaciju,
* jedinstvene identifikatore procesa,
* svaki proces poznaje samo svog sljedećeg susjeda u prstenu,
* poruke se šalju u jednom smjeru,
* pasivni procesi samo prosljeđuju poruke.

## Aktivni i pasivni procesi

Proces može biti u jednom od dva stanja:

1. aktivan proces,
2. pasivan proces.

Aktivan proces još uvijek sudjeluje u izboru vođe.
Pasivan proces je eliminiran iz izbora, ali se ne gasi — on i dalje prosljeđuje poruke kako bi komunikacija kroz prsten ostala moguća.

Ova razlika je važna jer se Petersonov algoritam ponaša kao da se prsten kandidata postupno smanjuje, iako fizički svi procesi i dalje postoje.

## Visoka ideja rada

U svakoj fazi aktivni proces uspoređuje svoj trenutni kandidatni identifikator s identifikatorima drugih aktivnih procesa koji dolaze prije njega u jednosmjernom prstenu.

Ako njegov kandidatni identifikator nije dovoljno velik, proces prestaje biti aktivan i prelazi u pasivno stanje.

Ako kandidatni identifikator preživi usporedbu, proces ostaje aktivan i sudjeluje u sljedećoj fazi.

Time se broj aktivnih kandidata smanjuje iz faze u fazu.

## Pseudokod — pojednostavljena verzija

```text
Na početku:
    svaki proces je aktivan
    svaki proces ima svoj ID kao kandidatni ID

Dok vođa nije izabran:
    svaki aktivni proces šalje svoj kandidatni ID u prsten

    ako proces primi vlastiti kandidatni ID natrag:
        taj proces proglašava vođu

    inače:
        aktivni proces uspoređuje kandidatne ID-eve koje je primio

        ako njegov kandidatni ID preživi usporedbu:
            ostaje aktivan u sljedećoj fazi

        inače:
            postaje pasivan

    pasivni procesi samo prosljeđuju poruke
```

## Zašto algoritam radi

Petersonov algoritam radi jer se u svakoj fazi eliminiraju kandidati koji sigurno ne mogu biti vođe.

Proces s najvećim identifikatorom ne može biti eliminiran usporedbom s većim identifikatorom, jer takav ne postoji. Zato najveći ID ostaje kandidat kroz sve faze.

S druge strane, svaki proces s manjim identifikatorom prije ili kasnije dolazi u situaciju u kojoj se uspoređuje s većim kandidatom. Tada se eliminira iz izbora.

Na kraju ostaje samo kandidat s najvećim identifikatorom, koji se proglašava vođom.

## Složenost

Petersonov algoritam postiže komunikacijsku složenost O(N log N).

Intuitivno, razlog je taj što se broj aktivnih kandidata smanjuje kroz faze, a u svakoj fazi aktivni kandidati šalju ograničen broj poruka kroz prsten.

Broj faza je reda O(log N), a ukupan broj poruka po fazi je reda O(N), pa ukupna komunikacijska složenost iznosi:

```text
O(N log N)
```

## Usporedba s drugim algoritmima

| Algoritam             | Smjer komunikacije | Najgora komunikacijska složenost | Komentar                                                                                |
| --------------------- | ------------------ | -------------------------------- | --------------------------------------------------------------------------------------- |
| Chang & Roberts       | jednosmjerno       | O(N²)                            | jednostavan, ali u najgorem slučaju šalje puno poruka                                   |
| Hirschberg & Sinclair | dvosmjerno         | O(N log N)                       | efikasniji, ali traži dvosmjernu komunikaciju                                           |
| Peterson              | jednosmjerno       | O(N log N)                       | efikasan i radi u jednosmjernom prstenu, ali je složeniji za razumjeti i implementirati |

## Što ćemo pokazati eksperimentima

U eksperimentalnom dijelu usporedit ćemo broj poruka koje algoritmi šalju za različite veličine prstena.

Plan je testirati:

* male prstene, npr. N = 3, 5, 10,
* srednje prstene, npr. N = 50, 100,
* veće prstene, npr. N = 500, 1000,
* slučajan raspored identifikatora,
* najgori ili nepovoljan raspored identifikatora.

Cilj je provjeriti pokazuju li eksperimentalni rezultati očekivano ponašanje:

* Chang & Roberts raste približno kvadratno u najgorem slučaju,
* Hirschberg & Sinclair raste približno kao N log N,
* Peterson također raste približno kao N log N.
