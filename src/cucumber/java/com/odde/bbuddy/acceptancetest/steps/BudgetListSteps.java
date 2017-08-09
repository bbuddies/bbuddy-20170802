package com.odde.bbuddy.acceptancetest.steps;

import com.odde.bbuddy.acceptancetest.data.EditableBudget;
import com.odde.bbuddy.acceptancetest.data.Messages;
import com.odde.bbuddy.acceptancetest.data.budget.BudgetRepoForTest;
import com.odde.bbuddy.acceptancetest.driver.UiDriver;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.*;

public class BudgetListSteps {
    @Autowired
    UiDriver driver;

    @Autowired
    BudgetRepoForTest repo;

    @Autowired
    Messages messages;

    @When("^add a budget of month '(.+)' with amount (\\d+)$")
    public void add_a_budget_of_month_with_amount(String month, int amount) {
        driver.navigateTo("/budgets/add");
        driver.inputTextByName(month, "month");
        driver.inputTextByName(String.valueOf(amount), "amount");
        driver.clickByText("Save");
    }

    @Then("^list budgets as below$")
    public void list_budgets_as_below(List<EditableBudget> budgets) throws Throwable {
        driver.waitForTextPresent(budgets.get(0).month);
        driver.waitForTextPresent(budgets.get(0).amount);
    }

    @Given("^exist a budget of month '(.+)' with amount (\\d+)$")
    public void exist_a_budget_of_month_with_amount(String month, int amount) throws Throwable {
        add_a_budget_of_month_with_amount(month, amount);
    }

    @Then("^list doesn't include a budget of month '(.+)' with amount (\\d+)$")
    public void list_doesn_t_include_a_budget_of_month_with_amount(String month,
                                                                   int amount) throws Throwable {
        String allTextInPage = driver.getAllTextInPage();
        List<String> allText = Arrays.asList(allTextInPage.split("\\n"));
        for (String str : allText) {
            if (str.equals(month + " " + amount)) {
                fail("There's still a budget... " + month + " " + amount);
            }
        }
    }

    @Given("^existing budgets as below$")
    public void existing_budgets_as_below(List<EditableBudget> budgets) throws Throwable {
        budgets.forEach(budget -> {
            add_a_budget_of_month_with_amount(budget.month, Integer.valueOf(budget.amount));
        });
    }

    @When("^query budget with start \"([^\"]*)\" and end \"([^\"]*)\"$")
    public void query_budget_with_start_and_end(String start, String end) throws Throwable {
        driver.navigateTo("/budgets/query");
        driver.inputTextByName(start, "start");
        driver.inputTextByName(end, "end");
        driver.clickByText("query");
    }

    @Then("^should get total amount of (\\d+)$")
    public void should_get_total_amount_of(int total) throws Throwable {
        driver.waitForTextPresent(total + "");
    }
}
