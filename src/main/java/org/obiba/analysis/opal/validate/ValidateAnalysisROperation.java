package org.obiba.analysis.opal.validate;

import java.util.stream.Collectors;

import org.json.JSONObject;
import org.obiba.opal.spi.r.AbstractROperationWithResult;
import org.obiba.opal.spi.r.RUtils;
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
    eval(String.format("base::assign(\"payload\", %s)", parametersToRListStringRepresentation(analysis.getParameters())), false);

    log.debug("Executing analysis for {}", analysis);

    setResult(eval(String.format("summary(check_that(%s, SUKUP > 1))", RUtils.getSymbol(analysis.getSymbol())), false));
  }

  private String parametersToRListStringRepresentation(JSONObject parameters) {
    return String.format("list(%s)", parameters.keySet().stream().map(key -> String.format("%s=%s", key, parameters.optString(key))).collect(Collectors.joining(",")));
  }

}