package de.wsthst.opendata.mosmix;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.BufferedInputStream;
import java.io.InputStream;
import java.net.URL;
import java.time.Duration;
import java.time.Instant;
import java.util.Collection;

import static java.lang.Double.NaN;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static si.uom.NonSI.DEGREE_ANGLE;
import static tec.units.ri.unit.MetricPrefix.HECTO;
import static tec.units.ri.unit.MetricPrefix.MILLI;
import static tec.units.ri.unit.Units.*;

public class MosmixKmlReaderTest {

  private MosmixKmlReader reader;
  private InputStream kmlTestData;
  private Instant modelRunTime;

  @BeforeEach
  public void setUp() throws Exception {
    reader = new MosmixKmlReader();
    modelRunTime = Instant.parse("2018-03-29T07:00:00.00Z");
    URL kmlFile = this.getClass().getResource("/MOSMIX_S_2018032907_240.kml");
    kmlTestData = new BufferedInputStream(kmlFile.openStream());
  }

  @AfterEach
  public void shutDown() throws Exception {
    kmlTestData.close();
  }

  @Test
  public void testReadAllStations() throws Exception {
    Collection<PointTimeForecast> ptfcs = reader.read(kmlTestData, modelRunTime, "10637", "01025");
    assertThat(ptfcs.size(), is(2));
  }

  @Test
  public void test10637() throws Exception {
    Collection<PointTimeForecast> ptfcs = reader.read(kmlTestData, modelRunTime, "10637");

    assertThat(ptfcs.size(), is(1));
    PointTimeForecast ptfc = ptfcs.iterator().next();
    assertThat(ptfc.getStationId(), is("10637"));
    assertThat(ptfc.getModelRunTime(), is(modelRunTime));
    assertThat(ptfc.getStationLatitude(DEGREE_ANGLE), is(8.6));
    assertThat(ptfc.getStationLongitude(DEGREE_ANGLE), is(50.05));
    assertThat(ptfc.getStationHeight(METRE), is(111.0));

    Forecast fc = ptfc.getForecast(1);
    assertThat(fc.getForecastTime(), is(modelRunTime.plus(Duration.ofHours(1))));
    assertThat(ptfc.getForecast(240).getForecastTime(), is(modelRunTime.plus(Duration.ofHours(240))));
    assertThat("PPPP", fc.getPPPP(HECTO(PASCAL)), is(1007.70));
    assertThat("PPPP step 240", ptfc.getForecast(240).getPPPP(HECTO(PASCAL)), is(1018.50));
    assertThat("TX", fc.getTX(KELVIN), is(NaN));
    assertThat("TX step 10", ptfc.getForecast(10).getTX(KELVIN), is(290.15));
    assertThat("TTT", fc.getTTT(KELVIN), is(284.05));
    assertThat("TD", fc.getTD(KELVIN), is(281.05));
    assertThat("TN step 22", ptfc.getForecast(22).getTN(KELVIN), is(282.75));
    assertThat("TM step 24", ptfc.getForecast(24).getTM(KELVIN), is(286.04));
    assertThat("T5CM", fc.getT5cm(KELVIN), is(286.25));
    assertThat("DD", fc.getDD(DEGREE_ANGLE), is(197.0));
    assertThat("FF", fc.getFF(METRE_PER_SECOND), is(2.57));
    assertThat("FX1", fc.getFX1(METRE_PER_SECOND), is(5.66));
    assertThat("FX3", fc.getFX3(METRE_PER_SECOND), is(6.17));
    assertThat("N", fc.getN(PERCENT), is(95.0));
    assertThat("NEFF", fc.getNEFF(PERCENT), is(89.0));
    assertThat("WW", fc.getWW(), is(61));
    assertThat("WW3", fc.getWW3(), is(0));
    assertThat("WW3 step 6", ptfc.getForecast(6).getWW3(), is(61));
    assertThat("RR1", fc.getRR1(MILLI(METRE)), is(1.0));
    assertThat("RR3", ptfc.getForecast(4).getRR3(MILLI(METRE)), is(0.5));
    assertThat("RR12", ptfc.getForecast(12).getRR12(MILLI(METRE)), is(1.5));
    assertThat("RR24", ptfc.getForecast(24).getRR24(MILLI(METRE)), is(1.5));
    assertThat("SUND1", fc.getSUND1(SECOND), is(420.0));
    assertThat("SUND3 step 3", ptfc.getForecast(3).getSUND3(SECOND), is(1800.0));
    assertThat("SUND24 step 24", ptfc.getForecast(24).getSUND24(SECOND), is(16740.0));
    assertThat("SUND24 step 24", ptfc.getForecast(24).getSUND24(MINUTE), is(279.0));
    assertThat("SUND24 step 24", ptfc.getForecast(24).getSUND24(HOUR), is(4.65));
  }
}