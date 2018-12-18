library(validate)
library(rlang)
library(jsonlite)

getStatusName <- function(status) {
  if (status) 'PASSED' else 'FAILED'
}

parseExpressions <- function(payload) {
  # Parses the payload to extract validation rule expressions
  #
  # Args:
  #   payload
  #
  # Returns:
  #   Array of rules

  if (!is.null(payload) & !is.null(payload$expressions)) {
    rules <- payload$expressions
  }
}

generateSummary <- function(rules, data) {
  # Generates the summary using input rules
  #
  # Args:
  #   rules: array of rule expressions
  #   data: data to be validated
  # Returns:
  #   Summary

  v <- validator(.data=rules)
  c <- confront(data, v)
  list("summary" = summary(c), "confront" = c)
}

processSummary <- function(summaryList) {
  # Iterates through confront summary and build a result of list
  #
  # Args:
  #   summaryResult
  #
  # Returns:
  #   Result object

  result <- list()
  items <- list()
  allPassed = TRUE

  for(summaryItem in 1:nrow(summaryList)) {
    passed = summaryList[summaryItem, "fails"] < 1 && !summaryList[summaryItem, "error"]
    allPassed = allPassed & passed
    item <- list('status' = getStatusName(passed), 'message' = summaryList[summaryItem, "expression"])
    items[[length(items) + 1]] <- item
  }

  result[["status"]]  = getStatusName(allPassed)
  result[["message"]] = if (allPassed) "All validations passed." else "Some validations failed."
  result[["items"]] = items

  jsonlite::toJSON(result, auto_unbox=TRUE)
}


getErrors <- function(confrontData) {
  # Iterates through errors and create an array of error messages
  #
  # Args:
  #   confrontData
  #
  # Returns:
  #   Result array of errors

  result = c()
  errorList <- errors(confrontData)
  for(errorItem in errorList) {
    result <- c(result, errorItem[[1]])
  }

  result
}

# Executes the validation
rules <- parseExpressions(payload)

if (is.null(rules)) {
  result <- list("status" = "ERROR", "message" = "Missing or invlid validation expressions.")
} else if (is.null(data)) {
  result <- list("status" = "ERROR", "message" = "No data is avialable.")
} else {
  summaryData <- generateSummary(rules, data)
  result = processSummary(summaryData[["summary"]])
  errorList <- getErrors(summaryData[["confront"]])
}
