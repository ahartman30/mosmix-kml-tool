# MOSMIX KML Tool
Java command line tool to extract data from DWD Open Data MOSMIX KML model run.

Extracs data from a meteorological [DWD MOSMIX KML model run file](http://opendata.dwd.de/weather/local_forecasts/mos/ "DWD Opendata") into a readable CSV format for a given list of weather stations.
More information at https://www.dwd.de/opendata.

## Usage
    usage: mosmix-kml-tool --kml <KML File> [--out <Output directory>] --stations <station1,station2,...>
    
    --kml <KML File>                     MOSMIX KML file, underscore delimites. Model run time yyyyMMddHH has to be at third position.
    --out <Output directory>             Output directory for the CSV file, else output to console standard out.
    --stations <station1,station2,...>   Comma delimited station identifiers, whose data will be extracted.

## Example Output
    01025
    forecast;parameter;TT;Td;Tx;Tn;Tm;Tg;dd;ff;fx;fx3;RR1;RR3;RR12;RR24;ww;ww3;N;Nf;PPPP;SS1;SS3;SS24
    today 07 UTC;unit;°C;°C;°C;°C;°C;°C;°;km/h;km/h;km/h;mm;mm;mm;mm;WW Code;WW Code;1/8;1/8;hPa;h;h;h
    29.03.18;07:00;---;---;---;---;---;---;---;---;---;---;---;---;---;---;---;---;---;---;---;---;---;---
    29.03.18;08:00;-1.3;-2.9;---;---;---;---;306;13.0;---;25.9;0.4;---;---;---;85;0;7;7;1008.2;---;---;---
    29.03.18;09:00;-1.4;-3.1;---;---;---;---;323;13.0;---;22.2;0.3;1.1;---;---;85;0;7;7;1008.5;---;---;---
    
## MOSMIX 2 JSON
An additional Python script `mosmix2json.py` can be applied to a MOSMIX CSV ouput for conversation to JSON format. Some additional meteorological values, like WWN, are calculated and postprocessed in this output.
