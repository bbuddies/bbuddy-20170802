package com.odde.bbuddy.budget.controller;

import com.odde.bbuddy.budget.domain.Budgets;
import com.odde.bbuddy.budget.repo.Budget;
import com.odde.bbuddy.budget.view.BudgetInView;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


@Controller
@RequestMapping("budgets")
public class BudgetController {

    private final Budgets budgets;

    @Autowired
    public BudgetController(Budgets budgets) {
        this.budgets = budgets;
    }

    @GetMapping("add")
    public String add() {
        return "budgets/add";
    }

    @PostMapping("add")
    public ModelAndView save(Budget budget) {
        Map<String, String> errMSg = new BudgetInView(budget).validate();
        if (!errMSg.isEmpty()) {
            return modelAndViewWithError(errMSg);
        }

        budgets.save(budget);
        return getModelAndView("redirect:/budgets");
    }

    class BudgetsInView {
        private final List<BudgetInView> budgetsInView;

        public BudgetsInView(List<Budget> budgets){
            budgetsInView = budgets.stream().map(BudgetInView::new).collect(Collectors.toList());
        }

        public List<BudgetInView> getBudgets(){
            return budgetsInView;
        }
    }

    @GetMapping
    public ModelAndView index() {
        ModelAndView modelAndView = getModelAndView("budgets/index");

        List<Budget> budgets = this.budgets.getAll();

        modelAndView.getModel().put("budgets", new BudgetsInView(budgets).getBudgets());

        return modelAndView;
    }

    private ModelAndView getModelAndView(String viewName) {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName(viewName);
        return modelAndView;
    }


    private ModelAndView modelAndViewWithError(Map<String, String> errMSg) {
        ModelAndView modelAndView = getModelAndView("budgets/add");
        errMSg.forEach((k, v) -> modelAndView.getModel()
                                             .put(k, v));
        return modelAndView;
    }

}
