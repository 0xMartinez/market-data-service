package com.crpt.Crypto.Model;

import org.ta4j.core.BarSeries;
import org.ta4j.core.indicators.AbstractIndicator;
import org.ta4j.core.indicators.helpers.ClosePriceIndicator;
import org.ta4j.core.indicators.helpers.HighPriceIndicator;
import org.ta4j.core.indicators.helpers.LowPriceIndicator;
import org.ta4j.core.num.DecimalNum;
import org.ta4j.core.num.Num;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.stream.Stream;

public class HLC3Indicator extends AbstractIndicator<Num> {

    private final HighPriceIndicator highPriceIndicator;
    private final LowPriceIndicator lowPriceIndicator;
    private final ClosePriceIndicator closePriceIndicator;

    public HLC3Indicator(BarSeries series) {
        super(series);
        this.highPriceIndicator = new HighPriceIndicator(series);
        this.lowPriceIndicator = new LowPriceIndicator(series);
        this.closePriceIndicator = new ClosePriceIndicator(series);
    }

    @Override
    public Num getValue(int index) {

        final BigDecimal highRounded = highPriceIndicator.getValue(index).bigDecimalValue().setScale(1, RoundingMode.DOWN);
        final BigDecimal lowRounded = lowPriceIndicator.getValue(index).bigDecimalValue().setScale(1, RoundingMode.DOWN);
        final BigDecimal closeRounded = closePriceIndicator.getValue(index).bigDecimalValue().setScale(1, RoundingMode.DOWN);

        final Num high = DecimalNum.valueOf(highRounded);
        final Num low = DecimalNum.valueOf(lowRounded);
        final Num close = DecimalNum.valueOf(closeRounded);

        return high.plus(low).plus(close).dividedBy(numOf(3));
    }

    @Override
    public int getUnstableBars() {
        return 0;
    }

    @Override
    public Num zero() {
        return super.zero();
    }

    @Override
    public Num one() {
        return super.one();
    }

    @Override
    public Num hundred() {
        return super.hundred();
    }

    @Override
    public Num numOf(Number number) {
        return super.numOf(number);
    }

    @Override
    public Stream<Num> stream() {
        return super.stream();
    }
}
