package com.crpt.Crypto.Model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum Interval {

    DAILY("1d"),
    HOURLY("1h"),
    MINUTELY("1m");

    private final String interval;
}
