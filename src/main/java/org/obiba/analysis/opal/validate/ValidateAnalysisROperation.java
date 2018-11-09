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
    log.debug("Executing analysis for {}", analysis);
    ensurePackage("validate");
    eval("library(validate)");
    setResult(eval(String.format("summary(check_that(%s, SUKUP > 1))", analysis.getSymbol()), false));
  }

}