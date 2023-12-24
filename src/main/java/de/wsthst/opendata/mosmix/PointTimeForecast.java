package de.wsthst.opendata.mosmix;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.jetbrains.annotations.NotNull;

import javax.measure.Quantity;
import javax.measure.Unit;
import javax.measure.quantity.Angle;
import javax.measure.quantity.Length;
import java.time.Instant;
import java.util.*;

/**
 * Immutable value object for a complete point time forecast. Single forecasts are sorted ascending for forecast time.
 */
public final class PointTimeForecast implements Iterable<Forecast> {

    private final String stationId;
    private final Quantity<Angle> latitude;
    private final Quantity<Angle> longitude;
    private final Quantity<Length> height;
    private final Instant modelRunTime;
    private final List<Forecast> forecasts;

    /**
     * Constructor.
     */
    public PointTimeForecast(
            String stationId,
            Quantity<Angle> latitude, Quantity<Angle> longitude, Quantity<Length> height,
            Instant modelRunTime,
            List<Forecast> forecasts) {
        this.stationId = stationId;
        this.latitude = latitude;
        this.longitude = longitude;
        this.height = height;
        this.modelRunTime = modelRunTime;
        this.forecasts = new ArrayList<>(forecasts);
        Collections.sort(this.forecasts);
    }

    /**
     * Returns the nth forecast beginning with 1.
     */
    public Forecast getForecast(int step) {
        return forecasts.get(step - 1);
    }

    public String getStationId() {
        return stationId;
    }

    public Instant getModelRunTime() {
        return modelRunTime;
    }

    public double getStationLatitude(Unit<Angle> unit) {
        return latitude.to(unit).getValue().doubleValue();
    }

    public double getStationLongitude(Unit<Angle> unit) {
        return longitude.to(unit).getValue().doubleValue();
    }

    public double getStationHeight(Unit<Length> unit) {
        return height.to(unit).getValue().doubleValue();
    }

    @Override
    public @NotNull Iterator<Forecast> iterator() {
        return forecasts.iterator();
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.MULTI_LINE_STYLE);
    }
}
