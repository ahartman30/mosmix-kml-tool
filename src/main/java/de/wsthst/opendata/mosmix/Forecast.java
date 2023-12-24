package de.wsthst.opendata.mosmix;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import javax.measure.Quantity;
import javax.measure.Unit;
import javax.measure.quantity.*;
import java.time.Instant;
import java.util.Objects;

import static java.lang.Double.NaN;
import static si.uom.NonSI.DEGREE_ANGLE;
import static tec.units.ri.unit.MetricPrefix.MILLI;
import static tec.units.ri.unit.Units.*;
import static tec.uom.se.quantity.Quantities.getQuantity;

/**
 * Value object for one meteorological forecast.
 */
public final class Forecast implements Comparable<Forecast> {

    private Instant forecastTime;
    private Quantity<Pressure> pppp;
    private Quantity<Temperature> ttt;
    private Quantity<Temperature> td;
    private Quantity<Temperature> t5cm;
    private Quantity<Temperature> tx;
    private Quantity<Temperature> tn;
    private Quantity<Temperature> tm;
    private Quantity<Angle> dd;
    private Quantity<Speed> ff;
    private Quantity<Speed> fx1;
    private Quantity<Speed> fx3;
    private Quantity<Dimensionless> n;
    private Quantity<Dimensionless> neff;
    private int ww;
    private int ww3;
    private Quantity<Length> rr1;
    private Quantity<Length> rr3;
    private Quantity<Length> rr12;
    private Quantity<Length> rr24;
    private Quantity<Time> sund1;
    private Quantity<Time> sund3;
    private Quantity<Time> sund24;

    /**
     * Constructor.
     *
     * @param forecastTime The forecast time.
     */
    public Forecast(Instant forecastTime) {
        this.forecastTime = forecastTime;
        pppp = getQuantity(NaN, PASCAL);
        tx = getQuantity(NaN, KELVIN);
        ttt = getQuantity(NaN, KELVIN);
        td = getQuantity(NaN, KELVIN);
        tn = getQuantity(NaN, KELVIN);
        t5cm = getQuantity(NaN, KELVIN);
        tm = getQuantity(NaN, KELVIN);
        dd = getQuantity(NaN, DEGREE_ANGLE);
        ff = getQuantity(NaN, METRE_PER_SECOND);
        fx1 = getQuantity(NaN, METRE_PER_SECOND);
        fx3 = getQuantity(NaN, METRE_PER_SECOND);
        n = getQuantity(NaN, PERCENT);
        neff = getQuantity(NaN, PERCENT);
        rr1 = getQuantity(NaN, MILLI(METRE));
        rr3 = getQuantity(NaN, MILLI(METRE));
        rr12 = getQuantity(NaN, MILLI(METRE));
        rr24 = getQuantity(NaN, MILLI(METRE));
        sund1 = getQuantity(NaN, SECOND);
        sund3 = getQuantity(NaN, SECOND);
        sund24 = getQuantity(NaN, SECOND);
    }

    public Instant getForecastTime() {
        return forecastTime;
    }

    void setForecastTime(Instant forecastTime) {
        this.forecastTime = forecastTime;
    }

    void setPPPP(double value, Unit<Pressure> unit) {
        pppp = getQuantity(value, unit);
    }

    /**
     * Returns the air pressure at sea level.
     */
    public double getPPPP(Unit<Pressure> unit) {
        return pppp.to(unit).getValue().doubleValue();
    }

    void setTX(double value, Unit<Temperature> unit) {
        tx = getQuantity(value, unit);
    }

    public double getTX(Unit<Temperature> unit) {
        return tx.to(unit).getValue().doubleValue();
    }

    void setTTT(double value, Unit<Temperature> unit) {
        ttt = getQuantity(value, unit);
    }

    /**
     * Return the temperature air.
     */
    public double getTTT(Unit<Temperature> unit) {
        return ttt.to(unit).getValue().doubleValue();
    }

    void setTD(double value, Unit<Temperature> unit) {
        td = getQuantity(value, unit);
    }

    /**
     * Returns the dew point.
     */
    public double getTD(Unit<Temperature> unit) {
        return td.to(unit).getValue().doubleValue();
    }

    void setTN(double value, Unit<Temperature> unit) {
        tn = getQuantity(value, unit);
    }

    public double getTN(Unit<Temperature> unit) {
        return tn.to(unit).getValue().doubleValue();
    }

    void setTM(double value, Unit<Temperature> unit) {
        tm = getQuantity(value, unit);
    }

    /**
     * Returns the temperature air daily mean of the last 24 hours.
     */
    public double getTM(Unit<Temperature> unit) {
        return tm.to(unit).getValue().doubleValue();
    }

    void setT5cm(double value, Unit<Temperature> unit) {
        t5cm = getQuantity(value, unit);
    }

    /**
     * Returns the temperature air at 5 cm over ground.
     */
    public double getT5cm(Unit<Temperature> unit) {
        return t5cm.to(unit).getValue().doubleValue();
    }

    void setDD(double value, Unit<Angle> unit) {
        dd = getQuantity(value, unit);
    }

    /**
     * Returns the wind direction.
     */
    public double getDD(Unit<Angle> unit) {
        return dd.to(unit).getValue().doubleValue();
    }

    void setFF(double value, Unit<Speed> unit) {
        ff = getQuantity(value, unit);
    }

    /**
     * Returns the wind speed.
     */
    public double getFF(Unit<Speed> unit) {
        return ff.to(unit).getValue().doubleValue();
    }

    void setFX1(double value, Unit<Speed> unit) {
        fx1 = getQuantity(value, unit);
    }

    /**
     * Returns the wind highest gust within 1 hour.
     */
    public double getFX1(Unit<Speed> unit) {
        return fx1.to(unit).getValue().doubleValue();
    }

    void setFX3(double value, Unit<Speed> unit) {
        fx3 = getQuantity(value, unit);
    }

    /**
     * Returns the wind highest gust within 3 hours.
     */
    public double getFX3(Unit<Speed> unit) {
        return fx3.to(unit).getValue().doubleValue();
    }

    void setN(double value, Unit<Dimensionless> unit) {
        n = getQuantity(value, unit);
    }

    /**
     * Returns the total cloud cover.
     */
    public double getN(Unit<Dimensionless> unit) {
        return n.to(unit).getValue().doubleValue();
    }

    void setNEFF(double value, Unit<Dimensionless> unit) {
        neff = getQuantity(value, unit);
    }

    /**
     * Returns the effective cloud cover.
     */
    public double getNEFF(Unit<Dimensionless> unit) {
        return neff.to(unit).getValue().doubleValue();
    }

    void setWW(int ww) {
        this.ww = ww;
    }

    /**
     * Returns the significant weather code.
     */
    public int getWW() {
        return ww;
    }

    void setWW3(int ww3) {
        this.ww3 = ww3;
    }

    /**
     * Returns the maximum significant weather code over 3 hours.
     */
    public int getWW3() {
        return ww3;
    }

    void setRR1(double value, Unit<Length> unit) {
        rr1 = getQuantity(value, unit);
    }

    /**
     * Returns the 1-hour precipitation amount.
     */
    public double getRR1(Unit<Length> unit) {
        return rr1.to(unit).getValue().doubleValue();
    }

    void setRR3(double value, Unit<Length> unit) {
        rr3 = getQuantity(value, unit);
    }

    /**
     * Returns the 3-hours precipitation amount.
     */
    public double getRR3(Unit<Length> unit) {
        return rr3.to(unit).getValue().doubleValue();
    }

    void setRR12(double value, Unit<Length> unit) {
        rr12 = getQuantity(value, unit);
    }

    /**
     * Returns the 12-hours precipitation amount.
     */
    public double getRR12(Unit<Length> unit) {
        return rr12.to(unit).getValue().doubleValue();
    }

    void setRR24(double value, Unit<Length> unit) {
        rr24 = getQuantity(value, unit);
    }

    /**
     * Returns the 24-hours precipitation amount.
     */
    public double getRR24(Unit<Length> unit) {
        return rr24.to(unit).getValue().doubleValue();
    }

    void setSUND1(double value, Unit<Time> unit) {
        sund1 = getQuantity(value, unit);
    }

    /**
     * Returns the 1-hour sunshine duration.
     */
    public double getSUND1(Unit<Time> unit) {
        return sund1.to(unit).getValue().doubleValue();
    }

    void setSUND24(double value, Unit<Time> unit) {
        sund24 = getQuantity(value, unit);
    }

    /**
     * Returns the 24-hours sunshine duration.
     */
    public double getSUND24(Unit<Time> unit) {
        return sund24.to(unit).getValue().doubleValue();
    }

    void setSUND3(double value, Unit<Time> unit) {
        sund3 = getQuantity(value, unit);
    }

    /**
     * Returns the 3-hours sunshine duration.
     */
    public double getSUND3(Unit<Time> unit) {
        return sund3.to(unit).getValue().doubleValue();
    }

    /**
     * Two objects are considered equal on equal forecast time.
     */
    @Override
    public boolean equals(Object other) {
        if (this == other) return true;
        if (other == null || getClass() != other.getClass()) return false;
        Forecast forecast = (Forecast) other;
        return forecastTime.equals(forecast.forecastTime);
    }

    @Override
    public int hashCode() {
        return Objects.hash(forecastTime);
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.MULTI_LINE_STYLE);
    }

    /**
     * Sorts ascending for forecast time.
     */
    public int compareTo(Forecast forecast) {
        return forecastTime.compareTo(forecast.forecastTime);
    }

}
