package de.wsthst.opendata.mosmix;

import org.junit.jupiter.api.Test;

import java.util.stream.IntStream;

import static java.lang.Double.NaN;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

/**
 * Unit tests for derived meteo value calculations.
 */
class MeteoCalculatorTest {

  @Test
  void testWW3() {
    MeteoCalculator calculator = new MeteoCalculator();
    calculator.addWW(2);
    assertThat(calculator.getWW3(), is(0));
    calculator.addWW(3);
    assertThat(calculator.getWW3(), is(0));
    calculator.addWW(1);
    assertThat(calculator.getWW3(), is(3));
  }

  @Test
  void testRR3() {
    MeteoCalculator calculator = new MeteoCalculator();
    calculator.addRR1(10);
    calculator.addRR1(20);
    assertThat(calculator.getRR3(), is(NaN));
    calculator.addRR1(30);
    assertThat(calculator.getRR3(), is(60.0));
    calculator.addRR1(NaN);
    assertThat(calculator.getRR3(), is(NaN));
  }

  @Test
  void testRR12() {
    MeteoCalculator calculator = new MeteoCalculator();
    IntStream
        .rangeClosed(1, 11)
        .boxed()
        .forEach(value -> calculator.addRR1(value));
    assertThat(calculator.getRR12(), is(NaN));

    calculator.addRR1(12);
    assertThat(calculator.getRR12(), is(78.0));

    calculator.addRR1(13);
    assertThat(calculator.getRR12(), is(90.0));
  }

  @Test
  void testRR24() {
    MeteoCalculator calculator = new MeteoCalculator();
    IntStream
        .rangeClosed(1, 23)
        .boxed()
        .forEach(value -> calculator.addRR1(value));
    assertThat(calculator.getRR24(), is(NaN));

    calculator.addRR1(24);
    assertThat(calculator.getRR24(), is(300.0));

    calculator.addRR1(25);
    assertThat(calculator.getRR24(), is(324.0));
  }

  @Test
  void testSUND3() {
    MeteoCalculator calculator = new MeteoCalculator();
    calculator.addSUND1(5);
    calculator.addSUND1(20);
    assertThat(calculator.getSUND3(), is(NaN));
    calculator.addSUND1(10);
    assertThat(calculator.getSUND3(), is(35.0));
    calculator.addSUND1(NaN);
    assertThat(calculator.getSUND3(), is(NaN));
  }

  @Test
  void testSUND24() {
    MeteoCalculator calculator = new MeteoCalculator();
    IntStream
        .rangeClosed(1, 23)
        .boxed()
        .forEach(value -> calculator.addSUND1(value));
    assertThat(calculator.getSUND24(), is(NaN));

    calculator.addSUND1(24);
    assertThat(calculator.getSUND24(), is(300.0));

    calculator.addSUND1(25);
    assertThat(calculator.getSUND24(), is(324.0));
  }

  @Test
  void testTM() {
    MeteoCalculator calculator = new MeteoCalculator();
    IntStream
        .rangeClosed(1, 23)
        .boxed()
        .forEach(value -> calculator.addTTT(value));
    assertThat(calculator.getTM(), is(NaN));

    calculator.addTTT(24);
    assertThat(calculator.getTM(), is(12.5));
  }
}