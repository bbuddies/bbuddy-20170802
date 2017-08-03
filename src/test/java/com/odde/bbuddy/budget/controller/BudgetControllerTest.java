package com.odde.bbuddy.budget.controller;

import com.odde.bbuddy.budget.domain.Budgets;
import com.odde.bbuddy.budget.repo.Budget;
import com.odde.bbuddy.budget.view.BudgetInView;
import org.junit.Test;
import org.springframework.web.servlet.ModelAndView;

import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.*;

public class BudgetControllerTest {
    Budgets budgets = mock(Budgets.class);
    BudgetController controller = new BudgetController(budgets);

    @Test
    public void save_budget() throws Exception {
        Budget budget = saveBudget("2017-08", 1000);

        controller.save(budget);

        verify(budgets).save(budget);
    }

    @Test
    public void save_budget_with_wrong_format_month() throws Exception {
        String wrongMonth = "2017/08";
        Budget budget = saveBudget(wrongMonth, 0);

        ModelAndView mav = controller.save(budget);

        assertTrue(mav.getModel().containsKey("monthErrMsg"));
        assertTrue(mav.getModel().containsKey("amountErrMsg"));

        verify(budgets, never()).save(budget);
    }

    @Test
    public void get_budgets_list() throws Exception {

        when(budgets.getAll()).thenReturn(budgets(1000, "2017-10"));
        BudgetInView budgetsInView = budgetsInView("TWD 1,000.00", "2017-10");

        ModelAndView result = controller.index();

        assertEquals("budgets/index", result.getViewName());
        BudgetInView  actualBudgetInView = ((List<BudgetInView>) result.getModel().get("budgets")).get(0);
        assertEquals(budgetsInView.getAmount(), actualBudgetInView.getAmount());
        assertEquals(budgetsInView.getMonth(), actualBudgetInView.getMonth());
    }

    @Test
    public void getTotal() {
        Integer total = 5000;
        String startDate = "2017-04-01";
        String endDate = "2017-04-30";
        when(budgets.getTotal(startDate, endDate)).thenReturn(total);

        ModelAndView modelAndView = controller.getTotal(startDate, endDate);

        assertThat(modelAndView.getViewName()).isEqualTo("redirect:/budgets/getTotal");
        assertThat(modelAndView.getModel().get("total")).isEqualTo(total);
    }

    private Budget saveBudget(String month,
                              int amount) {
        Budget budget = new Budget();
        budget.setMonth(month);
        budget.setAmount(amount);
        return budget;
    }

    private BudgetInView budgetsInView(String amount, String month) {
        BudgetInView budgetInView = new BudgetInView();
        budgetInView.setAmount(amount);
        budgetInView.setMonth(month);
        return budgetInView;
    }

    private List<Budget> budgets(int amount, String month) {
        Budget budget = new Budget();
        budget.setAmount(amount);
        budget.setMonth(month);
        return Arrays.asList(budget);
    }
}
