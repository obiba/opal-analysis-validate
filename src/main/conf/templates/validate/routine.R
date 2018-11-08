library(googlesheets4)
library(validate)
library(magrittr)
library(rlang)

ssId <- "https://docs.google.com/spreadsheets/d/1Hu9hZGa44mnwqnW21A-IDMAXobPiWJMVdzXxC6xLpHg/edit#gid=742454028"

loadTibble <- function(ssId) {
  ss <- sheets_get(ssId)
  sheetName <- ss$sheets$name[1]
  sheets_read(ss$spreadsheet_id, sheetName)
}

df <- loadTibble(ssId)

idf <- data.frame(rule=c("sex > 0", "age < 80"), label=c("Sex", "Age"))
i <- validator(.data=idf)
c <- confront(df, i)
summary(c)
