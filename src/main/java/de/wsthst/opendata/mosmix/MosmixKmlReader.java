package de.wsthst.opendata.mosmix;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.ListMultimap;
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

import static tec.units.ri.unit.MetricPrefix.MILLI;
import static tec.units.ri.unit.Units.*;

/**
 * Responsible for parsing a MOSMIX KML file and extracting the given stations as value objects.
 */
public final class MosmixKmlReader {

    private final XMLInputFactory xmlFactory;
    private XMLStreamReader parser;

    /**
     * Constructor.
     */
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

                MeteoCalculator calculator = new MeteoCalculator();
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

                    calculator.addTTT(fc.getTTT(KELVIN));
                    calculator.addRR1(fc.getRR1(MILLI(METRE)));
                    calculator.addWW(fc.getWW());
                    calculator.addSUND1(fc.getSUND1(SECOND));

                    fc.setWW3(calculator.getWW3());
                    fc.setTM(calculator.getTM(), KELVIN);
                    fc.setRR3(calculator.getRR3(), MILLI(METRE));
                    fc.setRR12(calculator.getRR12(), MILLI(METRE));
                    fc.setRR24(calculator.getRR24(), MILLI(METRE));
                    fc.setSUND3(calculator.getSUND3(), SECOND);
                    fc.setSUND24(calculator.getSUND24(), SECOND);
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
