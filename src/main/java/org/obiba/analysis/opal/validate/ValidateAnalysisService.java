package org.obiba.analysis.opal.validate;

import org.obiba.opal.spi.r.RServerResult;
import org.obiba.opal.spi.r.analysis.AbstractRAnalysisService;
import org.obiba.opal.spi.r.analysis.RAnalysis;

public class ValidateAnalysisService extends AbstractRAnalysisService {


  @Override
  public String getName() {
    return "opal-analysis-validate";
  }

  @Override
  protected RServerResult run(RAnalysis analysis) {
    ValidateAnalysisROperation validateAnalysisROperation = new ValidateAnalysisROperation(analysis);
    analysis.getSession().execute(validateAnalysisROperation);
    return validateAnalysisROperation.getResult();
  }

}
