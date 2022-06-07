# GDRv3
Generatore dati riassuntivi v3 - Programma per generare il file xsl del pdf riassuntivo in modo automatico partendo dallo zip del template di BOL

# AVVIO
per avviare il programma è necessario aver installato java v12 o superiore.

# FUNZIONAMENTO
1. estrarre lo zip del template di cui si vuole generare il pdf riassuntivo
2. estrarre dallo zip il file template.json
3. avviare il programma cliccando sul file GDRv3.1.jar e selezionare il file template.json
4. selezionare i moduli che si vogliono inserire nel pdf riassuntivo, nell'ordine in cui si vogliono far visualizzare (tenere premuto Ctrl per selezionarne più di uno) e poi inserire il nome della fase
5. premere avanti, selezionare lo stile (Consigliato Quadratoni in quanto più avanzato) e l'immagine di intestazione e premere Genera
6. sarà generata una cartella, nello stesso percorso dove è presente il file template.json selezionato precedentemente, chiamata in questo modo: PDF_%NOME FASE%_%NOME TEMPLATE%. Essa conterrà il file xsl generato e un file .bat contenente il comando per poter generare il pdf tramite il fop (bisogna avere il fop correttamente installato e aver creato il file xml dei dati chiamato dati_def.xml)
7. il file xsl generato conterrà tutti gli item di tutti i moduli selezionati (sottomoduli compresi), indipendentemente dal fatto che siano nascosti o meno a template; è dunque spesso necessario sistemare i dettagli nel file (ad esempio rimuovendo campi superflui oppure modificando le etichette dei DN)

# STILE GRAFICO
È possibile modificare lo stile grafico del pdf modificando i file Classico.xml e Quadratoni.xml contenuti nella cartella settings, modificando soltanto i valori degli attributi dello stile (ad esempio color o font-size);
ATTENZIONE: non bisogna eliminare gli attribute-set o crearne di nuovi.
