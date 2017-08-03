package com.odde.bbuddy.budget.view;

import com.odde.bbuddy.budget.repo.Budget;

import java.text.DecimalFormat;

public class BudgetInView {
    private String month;
    private String amount;

    public void setMonth(String month) {
        this.month = month;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public BudgetInView(Budget budget){
        this.month = budget.getMonth();
        DecimalFormat dt = new DecimalFormat("TWD #,###.00");
        this.amount =dt.format(budget.getAmount());
    }
}
