package com.odde.bbuddy.budget.domain;

import com.odde.bbuddy.budget.repo.Budget;
import com.odde.bbuddy.budget.repo.BudgetRepo;

import java.util.List;

import static java.time.Period.between;

public class BudgetPlan {
    private final BudgetRepo repo;

    public BudgetPlan(BudgetRepo repo) {
        this.repo = repo;
    }

    public double query(Period period) {
        return repo.findAll().stream().mapToDouble(budget -> budget.overlappingAmount(period)).sum();
    }

}
