rulesValues <- payload$expressions
rules <- unlist(strsplit(rulesValues, split=','))

df <- loadTibble(ssId)

idf <- data.frame(rule=rules)
i <- indicator(.data=idf)
c <- confront(df, i)
summary(c)