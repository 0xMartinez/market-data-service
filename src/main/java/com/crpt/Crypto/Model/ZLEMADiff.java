package com.crpt.Crypto.Model;

import org.ta4j.core.BarSeries;
import org.ta4j.core.indicators.AbstractIndicator;
import org.ta4j.core.indicators.ZLEMAIndicator;
import org.ta4j.core.indicators.helpers.ClosePriceIndicator;
import org.ta4j.core.indicators.helpers.HighPriceIndicator;
import org.ta4j.core.indicators.helpers.LowPriceIndicator;
import org.ta4j.core.num.DecimalNum;
import org.ta4j.core.num.Num;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.stream.Stream;

public class ZLEMADiff extends AbstractIndicator<Num> {

    private final ZLEMAIndicator zlemaIndicator8;
    private final ZLEMAIndicator zlemaIndicator21;

    public ZLEMADiff(BarSeries series, ClosePriceIndicator closePriceIndicator ) {
        super(series);
        this.zlemaIndicator8 = new ZLEMAIndicator(closePriceIndicator, 8);
        this.zlemaIndicator21 = new ZLEMAIndicator(closePriceIndicator, 21);
    }

    @Override
    public Num getValue(int index) {
        // Obliczenie wartości HLC3

        final BigDecimal zlemaIndicator8Value = zlemaIndicator8.getValue(index).bigDecimalValue().setScale(1, RoundingMode.DOWN);
        final BigDecimal zlemaIndicator21Value = zlemaIndicator21.getValue(index).bigDecimalValue().setScale(1, RoundingMode.DOWN);

        return DecimalNum.valueOf(zlemaIndicator21Value.doubleValue() - zlemaIndicator8Value.doubleValue());
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
