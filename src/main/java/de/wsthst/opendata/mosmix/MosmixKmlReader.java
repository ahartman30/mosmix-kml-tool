package de.wsthst.opendata.mosmix;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.EvictingQueue;
import com.google.common.collect.ListMultimap;
import org.apache.commons.math3.util.Precision;
import tec.uom.se.quantity.Quantities;

import javax.measure.Quantity;
import javax.measure.quantity.Angle;
import javax.measure.quantity.Length;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import java.io.InputStream;
import java.time.Instant;
import java.util.*;

import static java.util.Comparator.naturalOrder;
import static tec.units.ri.unit.MetricPrefix.MILLI;
import static tec.units.ri.unit.Units.*;

/**
 * Responsible for parsing a MOSMIX KML file and extracting the given stations as value objects.
 */
public class MosmixKmlReader {

    private XMLInputFactory xmlFactory;
    private XMLStreamReader parser;

    public MosmixKmlReader() {
        xmlFactory = XMLInputFactory.newFactory();
    }

    /**
     * Reads a KML file and extracts the given stations.
     *
     * @param kmlInput         The KML file.
     * @param stationIdsToRead Station IDs to extract.
     * @param modelRunTime     The model run time.
     * @return Collection with extracted forecasts.
     */
    public List<PointTimeForecast> read(InputStream kmlInput, Instant modelRunTime, String... stationIdsToRead) throws XMLStreamException {
        ListMultimap<String, String> dataForElements = ArrayListMultimap.create();
        Set<String> requiredStationIds = new HashSet<>(Arrays.asList(stationIdsToRead));
        parser = xmlFactory.createXMLStreamReader(kmlInput);

        // Read forecast times
        List<PointTimeForecast> ptfcs = new ArrayList<>();
        List<Instant> forecastTimes = new ArrayList<>();
        while (gotoNextTimeStepElement()) {
            Instant forecastTime = Instant.parse(parser.getText());
            forecastTimes.add(forecastTime);
        }

        // Read data and fill forecast objects
        while (gotoNextStationNameElement()) {
            String stationId = parser.getText();
            if (requiredStationIds.contains(stationId)) {
                dataForElements.clear();
                while (gotoNextForecastElementWithinCurrentStation()) {
                    String elementName = parser.getAttributeValue(0);
                    gotoNextContent(MosmixKmlSchema.XML_VALUE_ELEMENT);
                    String[] values = parser.getText().trim().split(MosmixKmlSchema.VALUES_DELIMITER);
                    dataForElements.putAll(elementName, Arrays.asList(values));
                }
                gotoNextContent(MosmixKmlSchema.XML_COORDINATES_ELEMENT);
                String[] coordinates = parser.getText().trim().split(MosmixKmlSchema.COORDINATES_DELIMTER);

                Quantity<Angle> lat = Quantities.getQuantity(Double.valueOf(coordinates[0]), MosmixKmlSchema.STATION_COORDINATES_UNIT);
                Quantity<Angle> lon = Quantities.getQuantity(Double.valueOf(coordinates[1]), MosmixKmlSchema.STATION_COORDINATES_UNIT);
                Quantity<Length> height = Quantities.getQuantity(Double.valueOf(coordinates[2]), MosmixKmlSchema.STATION_HEIGHT_UNIT);
                List<Forecast> forecasts = new ArrayList<>();
                forecastTimes.forEach(forecastTime -> forecasts.add(new Forecast(forecastTime)));
                PointTimeForecast ptfc = new PointTimeForecast(stationId, lat, lon, height, modelRunTime, forecasts);

                /*
                TODO: SRP - Extract class, call after parsing
                 */
                Queue<Integer> ww3Queue = EvictingQueue.create(3);
                Queue<Double> rr3Queue = EvictingQueue.create(3);
                Queue<Double> rr12Queue = EvictingQueue.create(12);
                Queue<Double> rr24Queue = EvictingQueue.create(24);
                Queue<Double> sund3Queue = EvictingQueue.create(3);
                Queue<Double> sund24Queue = EvictingQueue.create(24);
                Queue<Double> ttt24Queue = EvictingQueue.create(24);
                for (int step = 0; step < forecastTimes.size(); step++) {
                    Forecast fc = ptfc.getForecast(step + 1);
                    fc.setPPPP(parseNumericValue(dataForElements.get(MosmixKmlSchema.PPPP_SYMBOL).get(step)), MosmixKmlSchema.PPPP_UNIT);
                    fc.setTX(parseNumericValue(dataForElements.get(MosmixKmlSchema.TX_SYMBOL).get(step)), MosmixKmlSchema.T_UNIT);
                    fc.setTTT(parseNumericValue(dataForElements.get(MosmixKmlSchema.TTT_SYMBOL).get(step)), MosmixKmlSchema.T_UNIT);
                    fc.setTD(parseNumericValue(dataForElements.get(MosmixKmlSchema.TD_SYMBOL).get(step)), MosmixKmlSchema.T_UNIT);
                    fc.setTN(parseNumericValue(dataForElements.get(MosmixKmlSchema.TN_SYMBOL).get(step)), MosmixKmlSchema.T_UNIT);
                    fc.setT5cm(parseNumericValue(dataForElements.get(MosmixKmlSchema.T5CM_SYMBOL).get(step)), MosmixKmlSchema.T_UNIT);
                    fc.setDD(parseNumericValue(dataForElements.get(MosmixKmlSchema.DD_SYMBOL).get(step)), MosmixKmlSchema.DD_UNIT);
                    fc.setFF(parseNumericValue(dataForElements.get(MosmixKmlSchema.FF_SYMBOL).get(step)), MosmixKmlSchema.F_UNIT);
                    fc.setFX1(parseNumericValue(dataForElements.get(MosmixKmlSchema.FX1_SYMBOL).get(step)), MosmixKmlSchema.F_UNIT);
                    fc.setFX3(parseNumericValue(dataForElements.get(MosmixKmlSchema.FX3_SYMBOL).get(step)), MosmixKmlSchema.F_UNIT);
                    fc.setN(parseNumericValue(dataForElements.get(MosmixKmlSchema.N_SYMBOL).get(step)), MosmixKmlSchema.N_UNIT);
                    fc.setNEFF(parseNumericValue(dataForElements.get(MosmixKmlSchema.NEFF_SYMBOL).get(step)), MosmixKmlSchema.N_UNIT);
                    fc.setWW((int) parseNumericValue(dataForElements.get(MosmixKmlSchema.WW_SYMBOL).get(step)));
                    fc.setRR1(parseNumericValue(dataForElements.get(MosmixKmlSchema.RR1_SYMBOL).get(step)), MosmixKmlSchema.RR_UNIT);
                    fc.setRR3(parseNumericValue(dataForElements.get(MosmixKmlSchema.RR3_SYMBOL).get(step)), MosmixKmlSchema.RR_UNIT);
                    fc.setSUND1(parseNumericValue(dataForElements.get(MosmixKmlSchema.SUND1_SYMBOL).get(step)), MosmixKmlSchema.SUND_UNIT);

                    // WW3
                    ww3Queue.offer(fc.getWW());
                    if (step >= 2) {
                        int ww3 = ww3Queue.stream().max(naturalOrder()).get();
                        fc.setWW3(ww3);
                    }

                    // RR3
                    rr3Queue.offer(fc.getRR1(MILLI(METRE)));
                    if (step >= 2) {
                        double rr3 = rr3Queue.stream().mapToDouble(Double::doubleValue).sum();
                        fc.setRR3(rr3, MILLI(METRE));
                    }

                    // RR12
                    rr12Queue.offer(fc.getRR1(MILLI(METRE)));
                    if (step >= 11) {
                        double rr12 = rr12Queue.stream().mapToDouble(Double::doubleValue).sum();
                        fc.setRR12(rr12, MILLI(METRE));
                    }

                    // RR24
                    rr24Queue.offer(fc.getRR1(MILLI(METRE)));
                    if (step >= 23) {
                        double rr24 = rr24Queue.stream().mapToDouble(Double::doubleValue).sum();
                        fc.setRR24(rr24, MILLI(METRE));
                    }

                    // SUND3
                    sund3Queue.offer(fc.getSUND1(SECOND));
                    if (step >= 2) {
                        double sund3 = sund3Queue.stream().mapToDouble(Double::doubleValue).sum();
                        fc.setSUND3(sund3, SECOND);
                    }

                    // SUND24
                    sund24Queue.offer(fc.getSUND1(SECOND));
                    if (step >= 23) {
                        double sund24 = sund24Queue.stream().mapToDouble(Double::doubleValue).sum();
                        fc.setSUND24(sund24, SECOND);
                    }

                    // TM
                    ttt24Queue.offer(fc.getTTT(KELVIN));
                    if (step >= 23) {
                        double tm = ttt24Queue.stream().mapToDouble(Double::doubleValue).average().getAsDouble();
                        tm = Precision.round(tm, 2);
                        fc.setTM(tm, KELVIN);
                    }
                }
                ptfcs.add(ptfc);
            }
        }
        parser.close();
        return ptfcs;
    }

    private double parseNumericValue(String value) {
        return Double.parseDouble(value.trim().replace(MosmixKmlSchema.NO_VALUE_STRING, "NaN"));
    }

    private void gotoNextContent(String xmlElementName) throws XMLStreamException {
        gotoNext(xmlElementName);
        if (parser.hasNext()) parser.next();
    }

    private void gotoNext(String xmlElementName) throws XMLStreamException {
        while (parser.hasNext() && !isAtStartOf(xmlElementName)) {
            parser.next();
        }
    }

    private boolean isAtStartOf(String xmlElementName) {
        return parser.isStartElement() && parser.getLocalName().equals(xmlElementName);
    }

    private boolean isAtEndOf(String xmlElementName) {
        return parser.isEndElement() && parser.getLocalName().equals(xmlElementName);
    }

    private boolean gotoNextTimeStepElement() throws XMLStreamException {
        while (parser.hasNext() && !isAtStartOf(MosmixKmlSchema.XML_TIMESTEP_ELEMENT) && !isAtEndOf(MosmixKmlSchema.XML_TIMESTEPS_ELEMENT)) {
            parser.next();
        }
        if (isAtStartOf(MosmixKmlSchema.XML_TIMESTEP_ELEMENT)) {
            parser.next();
            return true;
        }
        return false;
    }

    private boolean gotoNextForecastElementWithinCurrentStation() throws XMLStreamException {
        while (parser.hasNext() && !isAtStartOf(MosmixKmlSchema.XML_FORECAST_ELEMENT) && !isAtEndOf(MosmixKmlSchema.XML_EXTENDED_DATA_ELEMENT)) {
            parser.next();
        }
        return isAtStartOf(MosmixKmlSchema.XML_FORECAST_ELEMENT);
    }

    private boolean gotoNextStationNameElement() throws XMLStreamException {
        while (parser.hasNext() && !isAtStartOf(MosmixKmlSchema.XML_NAME_ELEMENT)) {
            parser.next();
        }
        if (isAtStartOf(MosmixKmlSchema.XML_NAME_ELEMENT)) {
            parser.next();
            return true;
        }
        return false;
    }

}
