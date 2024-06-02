package com.stc.celestia.dto;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;

@FieldDefaults(level = AccessLevel.PRIVATE)
public class CelestiaValidatorDto {
     Integer rank;
     String operatorAddress;
     Boolean jailed;
     String bondStatus;
     Integer uptime;
     Integer commission;
     String moniker;
     Double votingPowerPercent;
     String avatar;
     Long tokens;
     BigDecimal cumulativeShare;
}
