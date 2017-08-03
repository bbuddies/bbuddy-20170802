package com.odde.bbuddy.budget.view;

import com.odde.bbuddy.budget.repo.Budget;

import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;

public class BudgetInView {
    private String month;
    private String amount;
    private Budget budget;

    public void setMonth(String month) {
        this.month = month;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    class Validation {
        Predicate<Budget> validate;
        String key;
        String message;

        Validation(Predicate<Budget> validate, String key, String message){
            this.validate = validate;
            this.validate.negate();
            this.key = key;
            this.message = message;
        }
    }

    class Validations {
        List<Validation> validations;

        public Validations(Validation... validations){
            this.validations = Arrays.asList(validations);
        }
        public Map<String, String> validate(Budget budget){
            Map<String, String> errMSg = new HashMap<>();
            for (Validation validation : validations) {
                if (validation.validate.test(budget)){
                    errMSg.put(validation.key, validation.message);
                    return errMSg;
                }
            }
            return errMSg;
        }
    }
    Validations validations = new Validations(
            new Validation(entity -> entity.getMonth().isEmpty(), "monthErrMsg", "Month should not be empty")
    );

    private boolean validateFormat(String month){
        SimpleDateFormat format1 = new SimpleDateFormat("yyyy-MM");
        format1.setLenient(false);
        try {
            format1.parse(month);
            return true;
        } catch (ParseException e) {
            return false;
        }
    }

    public BudgetInView(Budget budget){
        this.budget = budget;
        this.month = budget.getMonth();
        DecimalFormat dt = new DecimalFormat("TWD #,###.00");
        this.amount =dt.format(budget.getAmount());
    }

    public Map<String, String> validate(){
        return validations.validate(budget);
    }
}
