package com.odde.bbuddy.budget.repo;

import com.odde.bbuddy.budget.domain.Period;
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
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import static java.time.Period.between;

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

    public Budget(String month,
                  Integer amount) {
        this.month = month;
        this.amount = amount;
    }

    public double overlappingAmount(Period period) {
        LocalDate startOfBudget = LocalDate.parse(getMonth() + "-01", DateTimeFormatter.ISO_LOCAL_DATE);
        LocalDate endOfBudget = startOfBudget.withDayOfMonth(startOfBudget.lengthOfMonth());
        double dailyAmount = amount / startOfBudget.lengthOfMonth();
        return period.overlappingDayCount(new Period(startOfBudget, endOfBudget)) * dailyAmount;
    }
}
