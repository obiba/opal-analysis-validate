package org.obiba.analysis.opal.validate;

import org.obiba.opal.spi.r.AbstractROperationWithResult;
import org.obiba.opal.spi.r.analysis.RAnalysis;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ValidateAnalysisROperation extends AbstractROperationWithResult {

  private static final Logger log = LoggerFactory.getLogger(ValidateAnalysisROperation.class);

  private final RAnalysis analysis;

  public ValidateAnalysisROperation(RAnalysis analysis) {
    this.analysis = analysis;
  }

  @Override
  protected void doWithConnection() {
    ensurePackage("validate");
    eval("library(validate)");
    String parametersAsString = analysis.getParameters().toString().replaceAll("\"", "\\\\\"");
    eval(String.format("is.null(base::assign(\"data\", %s))", analysis.getSymbol()), false);
    eval(String.format("is.null(base::assign(\"payload\", \"%s\"))", parametersAsString, false));
    eval(String.format("rmarkdown::render('report.Rmd')"));

    log.debug("Executing analysis for {}", analysis);

    setResult(eval("result", false));
  }

}
