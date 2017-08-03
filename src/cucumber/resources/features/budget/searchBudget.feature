@user @budget
Feature: search budget

  Scenario: search budget with date range
    Given Then list budgets as below
      | month   | amount       |
      | 2017-03 | 3100 |
      | 2017-04 | 3000 |
      | 2017-05 | 3100 |
    When search budget between '2017-10-01' and '2017-10-01'
    Then total budget is 0