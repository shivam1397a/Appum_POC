Feature: LoginTest

  Scenario Outline: Validate login access for <User>
    Given I navigate to Teamie login page in "android" browser
#    When I login to Teamie as "<User>"
#    Then I verify that user is "<Results>"
#    And I close the automation browser
    Examples: Users and access
      | User         | Results                |
      | Active User  | successfully logged in |
#      | Blocked User | not logged in          |
