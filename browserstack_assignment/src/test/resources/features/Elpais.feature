Feature: Scrape and translate El Pais Opinion section

  Scenario: Fetch and process articles
    Given I navigate to the El Pais Opinion section
    And Ensure that whole text is in Spanish
    And I navigate to the Opinion section
    When I fetch the first five articles
    Then I print titles and contents in Spanish
    And I download cover images if available
    And I translate and print the titles of the first five articles in english
    And I analyze translated headers for repeated words and print them
