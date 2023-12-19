# MOSMIX KML Tool
Java command line tool to extract data from DWD Open Data MOSMIX KML model run.

Extracts data from a meteorological [DWD MOSMIX KML model run file](http://opendata.dwd.de/weather/local_forecasts/mos/ "DWD Opendata") into a readable CSV format for a given list of weather stations.

## Usage
    usage: mosmix-kml-tool --kml <KML-Datei> [--out <Ausgabeverzeichnis>]
    --stations <Station1,Station2,...>
    --kml <KML-Datei>                    MOSMIX KML Datei. Modellaufzeit
                                         yyyMMddHH muss an dritter Stelle
                                         stehen.
    --out <Ausgabeverzeichnis>           Ausgabeverzeichnis fĂĽr die
                                         CSV-Dateien. Ohne Angabe erfoglt
                                         die Ausgabe auf die Konsole.
    --stations <Station1,Station2,...>   Stationskennungen welche
                                         extrahiert werden sollen, durch
                                         Komma getrennt.
## Example Output
    01025
    forecast;parameter;TT;Td;Tx;Tn;Tm;Tg;dd;ff;fx;fx3;RR1;RR3;RR12;RR24;ww;ww3;N;Nf;PPPP;SS1;SS3;SS24
    today 07 UTC;unit;°C;°C;°C;°C;°C;°C;°;km/h;km/h;km/h;mm;mm;mm;mm;WW Code;WW Code;1/8;1/8;hPa;h;h;h
    29.03.18;07:00;---;---;---;---;---;---;---;---;---;---;---;---;---;---;---;---;---;---;---;---;---;---
    29.03.18;08:00;-1.3;-2.9;---;---;---;---;306;13.0;---;25.9;0.4;---;---;---;85;0;7;7;1008.2;---;---;---
    29.03.18;09:00;-1.4;-3.1;---;---;---;---;323;13.0;---;22.2;0.3;1.1;---;---;85;0;7;7;1008.5;---;---;---
    
