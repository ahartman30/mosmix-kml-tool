package de.wsthst.opendata.mosmix;

import org.apache.commons.math3.util.Precision;
import org.apache.commons.text.TextStringBuilder;

import java.io.PrintWriter;
import java.time.Duration;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

import static java.time.ZoneOffset.UTC;
import static si.uom.NonSI.DEGREE_ANGLE;
import static tec.units.ri.unit.MetricPrefix.HECTO;
import static tec.units.ri.unit.MetricPrefix.MILLI;
import static tec.units.ri.unit.Units.*;

public class CsvWriter {

    public void write(PointTimeForecast ptfc, PrintWriter writer) {
        ZonedDateTime modelRunTimeUtc = ptfc.getModelRunTime().atZone(UTC);
        DateTimeFormatter timeFormat = DateTimeFormatter.ofPattern("dd.MM.yy;HH:mm");
        writer.println("forecast;parameter;TT;Td;Tx;Tn;Tm;Tg;dd;ff;fx;fx3;RR1;RR3;RR12;RR24;ww;ww3;N;Nf;PPPP;SS1;SS3;SS24");
        writer.printf("today %02d UTC;unit;°C;°C;°C;°C;°C;°C;°;km/h;km/h;km/h;mm;mm;mm;mm;WW Code;WW Code;1/8;1/8;hPa;h;h;h", modelRunTimeUtc.getHour());
        writer.println();

        TextStringBuilder line = new TextStringBuilder();
        ZonedDateTime modelRunTimeUtcMidnight = modelRunTimeUtc.withHour(0);
        Duration durationToMidnight = Duration.between(modelRunTimeUtcMidnight, modelRunTimeUtc);
        for (int hour = 0; hour <= durationToMidnight.toHours(); hour++) {
            line
                .append(modelRunTimeUtcMidnight.plusHours(hour).format(timeFormat)).append(";")
                .append("---;---;---;---;---;---;---;---;---;---;---;---;---;---;---;---;---;---;---;---;---;---");
            writer.println(line.toString());
            line.clear();
        }

        for (Forecast fc : ptfc) {
            line
                .append(fc.getForecastTime().atZone(UTC).format(timeFormat)).append(";")
                .append(Precision.round(fc.getTTT(CELSIUS), 1)).append(";")
                .append(Precision.round(fc.getTD(CELSIUS), 1)).append(";")
                .append(Precision.round(fc.getTX(CELSIUS), 1)).append(";")
                .append(Precision.round(fc.getTN(CELSIUS), 1)).append(";")
                .append(Precision.round(fc.getTM(CELSIUS), 1)).append(";")
                .append(Precision.round(fc.getT5cm(CELSIUS), 1)).append(";")
                .append("%.0f", Precision.round(fc.getDD(DEGREE_ANGLE), 0)).append(";")
                .append(Precision.round(fc.getFF(KILOMETRE_PER_HOUR), 1)).append(";")
                .append(Precision.round(fc.getFX1(KILOMETRE_PER_HOUR), 1)).append(";")
                .append(Precision.round(fc.getFX3(KILOMETRE_PER_HOUR), 1)).append(";")
                .append(Precision.round(fc.getRR1(MILLI(METRE)), 1)).append(";")
                .append(Precision.round(fc.getRR3(MILLI(METRE)), 1)).append(";")
                .append(Precision.round(fc.getRR12(MILLI(METRE)), 1)).append(";")
                .append(Precision.round(fc.getRR24(MILLI(METRE)), 1)).append(";")
                .append(fc.getWW()).append(";")
                .append(fc.getWW3()).append(";")
                .append("%.0f", Precision.round(fc.getN(PERCENT), 2) / 100.0 * 8.0).append(";")
                .append("%.0f", Precision.round(fc.getNEFF(PERCENT), 2) / 100.0 * 8.0).append(";")
                .append(Precision.round(fc.getPPPP(HECTO(PASCAL)), 1)).append(";")
                .append(Precision.round(fc.getSUND1(HOUR), 1)).append(";")
                .append(Precision.round(fc.getSUND3(HOUR), 1)).append(";")
                .append(Precision.round(fc.getSUND24(HOUR), 1))
                .replaceAll("NaN", "---");
            writer.println(line.toString());
            line.clear();
        }
    }

}
