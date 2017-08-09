@user @budget
Feature: Budget

  Scenario: Add a budget
    When add a budget of month '2017-08' with amount 1000
    Then list budgets as below
      | month   | amount       |
      | 2017-08 | TWD 1,000.00 |

  Scenario: add budget with wrong format month and 0 amount
    When add a budget of month '2017/08' with amount 0
    Then there is an error message for invalid date month
    And there is an error message for number amount should be larger than or equal to 1

  Scenario: Add a budget with existing month
    Given exist a budget of month '2017-10' with amount 10000
    When add a budget of month '2017-10' with amount 3000
    Then list budgets as below
      | month   | amount       |
      | 2017-10 | TWD 3,000.00 |
    And list doesn't include a budget of month '2017-10' with amount 10000

  Scenario: Query budget with a date range
    Given existing budgets as below
      | month   | amount  |
      | 2017-04 | 3000    |
    When query budget with start "2017-04-01" and end "2017-04-30"
    Then should get total amount of 3000