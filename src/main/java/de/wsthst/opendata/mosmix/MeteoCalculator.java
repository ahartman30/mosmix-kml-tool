package de.wsthst.opendata.mosmix;

import com.google.common.collect.EvictingQueue;
import org.apache.commons.math3.util.Precision;

import java.util.Queue;

import static java.lang.Double.NaN;
import static java.util.Comparator.naturalOrder;

/**
 * Encapsulates the functionality for calculating derived meteorological values
 * for a single forecast time.
 * Note that a calulcated meteo is returned only if all values for all time steps are
 * existing and are not NaN. If a value for one time step is missing then NaN will be returned.
 */
final class MeteoCalculator {
  private final Queue<Integer> ww3Queue;
  private final Queue<Double> rr3Queue;
  private final Queue<Double> rr12Queue;
  private final Queue<Double> rr24Queue;
  private final Queue<Double> sund3Queue;
  private final Queue<Double> sund24Queue;
  private final Queue<Double> ttt24Queue;

  /**
   * Constructor.
   */
  public MeteoCalculator() {
    ww3Queue = EvictingQueue.create(3);
    rr3Queue = EvictingQueue.create(3);
    rr12Queue = EvictingQueue.create(12);
    rr24Queue = EvictingQueue.create(24);
    sund3Queue = EvictingQueue.create(3);
    sund24Queue = EvictingQueue.create(24);
    ttt24Queue = EvictingQueue.create(24);
  }

  /**
   * Adds the current significant weather.
   *
   * @param ww The key number of the current significant weather to add.
   */
  public void addWW(int ww) {
    ww3Queue.offer(ww);
  }

  /**
   * Adds the current precipitation amount.
   *
   * @param rr1 The current precipitation amount in millimetres.
   */
  public void addRR1(double rr1) {
    rr3Queue.offer(rr1);
    rr12Queue.offer(rr1);
    rr24Queue.offer(rr1);
  }

  /**
   * Adds the current sunshine duration.
   *
   * @param sund1 The current sunshine duration in seconds.
   */
  public void addSUND1(double sund1) {
    sund3Queue.offer(sund1);
    sund24Queue.offer(sund1);
  }

  /**
   * Adds the current temperature air.
   *
   * @param ttt The current temperature air in Kelvin.
   */
  public void addTTT(double ttt) {
    ttt24Queue.offer(ttt);
  }

  /**
   * Returns the current maximum significant weather over 3 hours.
   *
   * @return The current WW3 or 0 if less values.
   */
  public int getWW3() {
    if (ww3Queue.size() < 3) return 0;
    return ww3Queue.stream().max(naturalOrder()).get();
  }

  /**
   * Returns the current 3-hours precipitation amount accumulated.
   *
   * @return 3-hours precipitation in millimetres.
   */
  public double getRR3() {
    if (rr3Queue.size() < 3) return NaN;
    return rr3Queue.stream().mapToDouble(Double::doubleValue).sum();
  }

  /**
   * Returns the current 12-hours precipitation amount accumulated.
   *
   * @return 12-hours precipitation in millimetres.
   */
  public double getRR12() {
    if (rr12Queue.size() < 12) return NaN;
    return rr12Queue.stream().mapToDouble(Double::doubleValue).sum();
  }

  /**
   * Returns the current 24-hours precipitation amount accumulated.
   *
   * @return 24-hours precipitation in millimetres.
   */
  public double getRR24() {
    if (rr24Queue.size() < 24) return NaN;
    return rr24Queue.stream().mapToDouble(Double::doubleValue).sum();
  }

  /**
   * Returns the current 3-hours sunshine duration.
   *
   * @return 3-hours sunshine duration in seconds.
   */
  public double getSUND3() {
    if (sund3Queue.size() < 3) return NaN;
    return sund3Queue.stream().mapToDouble(Double::doubleValue).sum();
  }

  /**
   * Returns the current 24-hours sunshine duration.
   *
   * @return 24-hours sunshine duration in seconds.
   */
  public double getSUND24() {
    if (sund24Queue.size() < 24) return NaN;
    return sund24Queue.stream().mapToDouble(Double::doubleValue).sum();
  }

  /**
   * Returns the temperature air daily mean of the last 24 hours.
   *
   * @return The temperature air daily mean of the last 24 hours in Kelvin.
   */
  public double getTM() {
    if (ttt24Queue.size() < 24) return NaN;
    double tm = ttt24Queue.stream().mapToDouble(Double::doubleValue).average().getAsDouble();
    tm = Precision.round(tm, 2);
    return tm;
  }

}
