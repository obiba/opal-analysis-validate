#
# Retrieve script parameters and transform to validation rules
#
rulesValues <- payload$expressions
rules <- unlist(strsplit(rulesValues, split=','))


df <- data.frame(rule=rules)
v <- validator(.data=df)
c <- confront(data, v)
summaryResult <- summary(c)

if(sum(summaryResult$fails) > 0) {
  result <- list('status' = 'FAILURE', 'message' = sprintf("Validation test %s failed!", rulesValues))
} else {
  result <- list('status' = 'PASSED',  'message' = sprintf("Validation test %s passed!", rulesValues))
}