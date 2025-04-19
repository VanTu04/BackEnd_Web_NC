package com.vawndev.spring_boot_readnovel.Utils;

import lombok.experimental.UtilityClass;

import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;

@UtilityClass
public class TimeZoneConvert {
    public  String convertUtcToUserTimezone(Instant utcTime) {
        ZoneId systemZone = ZoneId.systemDefault();
        ZonedDateTime localTime = utcTime.atZone(ZoneId.of("UTC")).withZoneSameInstant(systemZone);
        return localTime.toString();
    }

}
