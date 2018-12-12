package org.obiba.analysis.opal.validate;

import org.json.JSONArray;
import org.json.JSONObject;
import org.obiba.opal.spi.analysis.AnalysisReportType;
import org.obiba.opal.spi.r.AbstractROperationWithResult;
import org.obiba.opal.spi.r.analysis.RAnalysis;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.stream.Collectors;

import com.google.common.base.Strings;

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
    JSONObject parameters = analysis.getParameters();
    eval(String.format("base::assign(\"data\", %s)", analysis.getSymbol()), false);
    eval(String.format("base::assign(\"payload\", %s)", parametersToRListStringRepresentation(parameters)), false);
    eval(String.format("rmarkdown::render('report.Rmd')"));

    log.debug("Executing analysis for {}", analysis);

    setResult(eval("result", false));
  }

  private String parametersToRListStringRepresentation(JSONObject parameters) {
    return String.format("list(%s)",
        parameters.keySet().stream()
            .map(key -> String.format("%s=%s", key, jsonToRConverter(parameters.opt(key))))
            .collect(Collectors.joining(",")));
  }

  private String removeBracesForArrayString(String  arrayString) {
    return Strings.isNullOrEmpty(arrayString) ? "\"\"" : arrayString.replaceAll("^\\[|\\]$", "");
  }

  private String jsonToRConverter(Object object) {
    if (object instanceof JSONArray) {
      return String.format("c(%s)", removeBracesForArrayString(((JSONArray) object).toString()));
    }

    return removeBracesForArrayString(object.toString());
  }
}
