package de.wsthst.opendata.mosmix;

import javax.measure.Unit;
import javax.measure.quantity.*;

import static si.uom.NonSI.DEGREE_ANGLE;
import static tec.units.ri.unit.MetricPrefix.MILLI;
import static tec.units.ri.unit.Units.*;

/**
 * Contains constants describing the KML input.
 */
final class MosmixKmlSchema {

    static final String XML_TIMESTEPS_ELEMENT = "ForecastTimeSteps";
    static final String XML_TIMESTEP_ELEMENT = "TimeStep";
    static final String XML_NAME_ELEMENT = "name";
    static final String XML_EXTENDED_DATA_ELEMENT = "ExtendedData";
    static final String XML_FORECAST_ELEMENT = "Forecast";
    static final String XML_VALUE_ELEMENT = "value";
    static final String XML_COORDINATES_ELEMENT = "coordinates";
    static final String COORDINATES_DELIMTER = ",";
    static final String VALUES_DELIMITER  = "\\s+";

    static final String PPPP_SYMBOL = "PPPP";
    static final String TX_SYMBOL = "TX";
    static final String TTT_SYMBOL = "TTT";
    static final String TD_SYMBOL = "Td";
    static final String TN_SYMBOL = "TN";
    static final String T5CM_SYMBOL = "T5cm";
    static final String DD_SYMBOL = "DD";
    static final String FF_SYMBOL = "FF";
    static final String FX1_SYMBOL = "FX1";
    static final String FX3_SYMBOL = "FX3";
    static final String N_SYMBOL = "N";
    static final String NEFF_SYMBOL = "Neff";
    static final String WW_SYMBOL = "ww";
    static final String RR1_SYMBOL = "RR1c";
    static final String RR3_SYMBOL = "RR3c";
    static final String SUND1_SYMBOL = "SunD1";

    static final Unit<Pressure> PPPP_UNIT = PASCAL;
    static final Unit<Temperature> T_UNIT = KELVIN;
    static final Unit<Angle> DD_UNIT = DEGREE_ANGLE;
    static final Unit<Speed> F_UNIT = METRE_PER_SECOND;
    static final Unit<Dimensionless> N_UNIT = PERCENT;
    static final Unit<Length> RR_UNIT = MILLI(METRE);
    static final Unit<Time> SUND_UNIT = SECOND;
    static final Unit<Angle> STATION_COORDINATES_UNIT = DEGREE_ANGLE;
    static final Unit<Length> STATION_HEIGHT_UNIT = METRE;

    static final String NO_VALUE_STRING = "-";

    private MosmixKmlSchema() {}
}
