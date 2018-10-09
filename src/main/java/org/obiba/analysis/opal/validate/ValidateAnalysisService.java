package org.obiba.analysis.opal.validate;

import com.google.common.collect.Lists;
import org.obiba.opal.spi.analysis.AnalysisTemplate;
import org.obiba.opal.spi.analysis.NoSuchAnalysisTemplateException;
import org.obiba.opal.spi.r.analysis.RAnalysis;
import org.obiba.opal.spi.r.analysis.RAnalysisResult;
import org.obiba.opal.spi.r.analysis.RAnalysisService;

import java.util.List;
import java.util.Properties;
import java.util.stream.Collectors;

public class ValidateAnalysisService implements RAnalysisService {

  protected Properties properties;

  protected boolean running;

  @Override
  public List<AnalysisTemplate> getAnalysisTemplates() {
    return Lists.newArrayList();
  }

  @Override
  public List<RAnalysisResult> analyse(List<RAnalysis> analyses) throws NoSuchAnalysisTemplateException {
    return analyses.stream().map(a -> RAnalysisResult.create(a).build()).collect(Collectors.toList());
  }

  @Override
  public String getName() {
    return "opal-analysis-validate";
  }

  @Override
  public Properties getProperties() {
    return properties;
  }

  @Override
  public void configure(Properties properties) {
    this.properties = properties;
  }

  @Override
  public boolean isRunning() {
    return running;
  }

  @Override
  public void start() {
    running = true;
  }

  @Override
  public void stop() {
    running = false;
  }
}
