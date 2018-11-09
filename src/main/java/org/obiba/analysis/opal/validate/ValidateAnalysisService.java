package org.obiba.analysis.opal.validate;

import org.obiba.opal.spi.r.analysis.AbstractRAnalysisService;
import org.obiba.opal.spi.r.analysis.RAnalysis;
import org.rosuda.REngine.REXP;

public class ValidateAnalysisService extends AbstractRAnalysisService {

  @Override
  protected REXP run(RAnalysis analysis) {
    ValidateAnalysisROperation validateAnalysisROperation = new ValidateAnalysisROperation(analysis);
    analysis.getSession().execute(validateAnalysisROperation);
    return validateAnalysisROperation.getResult();
  }

}
