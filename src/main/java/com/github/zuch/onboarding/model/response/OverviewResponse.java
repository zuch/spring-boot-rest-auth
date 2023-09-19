package com.github.zuch.onboarding.model.response;

import com.github.zuch.onboarding.model.AccountType;
import com.github.zuch.onboarding.model.Currency;
import com.github.zuch.onboarding.model.Validation;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OverviewResponse {

    private String iban;
    private BigDecimal accountBalance;
    private AccountType typeOfAccount;
    private Currency currency;
    private LocalDateTime openingDate;
    private Validation validation;
}
