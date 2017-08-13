package com.odde.bbuddy.budget.repo;

import com.odde.bbuddy.budget.domain.Budgets;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.validator.constraints.NotBlank;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;

@Entity
@Table(name = "budgets")
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class Budget {

    @Id
    @GeneratedValue
    private long id;

    @NotBlank
    private String month;

    @NotNull
    private Integer amount;

    private static final DateTimeFormatter MONTH_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM");

    public int getOneDayAmountOfBudget() {
        return getAmount() / YearMonth.parse(getMonth(), MONTH_FORMATTER).lengthOfMonth();
    }
}
