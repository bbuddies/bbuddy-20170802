package com.odde.bbuddy.budget.domain;

class BudgetDetail {

    public int getActualDays() {
        return actualDays;
    }

    public int getLengthOfMonth() {
        return lengthOfMonth;
    }

    private int actualDays;
    private int lengthOfMonth;

    BudgetDetail(int actualDays, int lengthOfMonth) {
        this.actualDays = actualDays;
        this.lengthOfMonth = lengthOfMonth;
    }
}
