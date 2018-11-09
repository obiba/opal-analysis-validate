package org.obiba.analysis.opal.validate;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Properties;
import java.util.stream.Collectors;

import com.google.common.base.Strings;
import com.google.common.collect.Lists;

import org.json.JSONObject;
import org.obiba.opal.spi.analysis.AnalysisStatus;
import org.obiba.opal.spi.analysis.AnalysisTemplate;
import org.obiba.opal.spi.analysis.NoSuchAnalysisTemplateException;
import org.obiba.opal.spi.r.FileWriteROperation;
import org.obiba.opal.spi.r.ROperationTemplate;
import org.obiba.opal.spi.r.analysis.AbstractRAnalysisService;
import org.obiba.opal.spi.r.analysis.RAnalysis;
import org.obiba.opal.spi.r.analysis.RAnalysisResult;
import org.obiba.opal.spi.r.analysis.RAnalysisResult.Builder;
import org.rosuda.REngine.REXP;
import org.rosuda.REngine.REXPMismatchException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ValidateAnalysisService extends AbstractRAnalysisService {

  private static final String TEMPLATES_DIR = "templates";
  private static final String SCHEMA_FORM_FILE_NAME = "form.json";
  private static final String ROUTINE_FILE_NAME = "routine.R";
  private static final String REPORT_FILE_NAME = "report.Rmd";

  private static final Logger log = LoggerFactory.getLogger(ValidateAnalysisService.class);

  protected Properties properties;

  protected boolean running;

  protected List<AnalysisTemplate> analysisTemplates;

  @Override
  public List<AnalysisTemplate> getAnalysisTemplates() {
    return analysisTemplates;
  }

  @Override
  public List<RAnalysisResult> analyse(List<RAnalysis> analyses) throws NoSuchAnalysisTemplateException {
    return analyses.stream().map(analysis -> {
      Builder analysisResultBuilder = RAnalysisResult.create(analysis);
      ValidateAnalysisTemplate template = (ValidateAnalysisTemplate) getTemplate(analysis.getTemplateName());

      analysisResultBuilder.start();
      if (Files.isRegularFile(template.getRoutinePath()) && Files.isRegularFile(template.getReportPath())) {
        analysisResultBuilder.status(AnalysisStatus.IGNORED.name());
        analysisResultBuilder.message("No analysis to run.");
      } else {
        prepare(analysis.getSession(), template);

        try {
          HashMap result = (HashMap) run(analysis).asNativeJavaObject();
          result.forEach((k, v) ->  log.info("######  {} - {}", v, k));
        } catch (REXPMismatchException e) {
          e.printStackTrace();
        }
      }

      analysisResultBuilder.end();
      return analysisResultBuilder.build();
    }).collect(Collectors.toList());
  }

  private REXP run(RAnalysis analysis) {
    ValidateAnalysisROperation validateAnalysisROperation = new ValidateAnalysisROperation(analysis);
    analysis.getSession().execute(validateAnalysisROperation);
    return validateAnalysisROperation.getResult();
  }

  private void prepare(ROperationTemplate session, ValidateAnalysisTemplate template) {
    if (Files.isRegularFile(template.getRoutinePath())) {
      File routineFile = template.getRoutinePath().toFile();
      FileWriteROperation routineWriteOperation = new FileWriteROperation(routineFile.getName(), routineFile);
      session.execute(routineWriteOperation);
    }

    if (Files.isRegularFile(template.getReportPath())) {
      File reportFile = template.getRoutinePath().toFile();
      FileWriteROperation reportWriteOperation = new FileWriteROperation(reportFile.getName(), reportFile);
      session.execute(reportWriteOperation);
    }
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
    analysisTemplates = initAnalysisTemplates();
  }

  @Override
  public void stop() {
    running = false;
  }

  private List<AnalysisTemplate> initAnalysisTemplates() {
    Path templateDirectoryPath = Paths.get(getProperties().getProperty(INSTALL_DIR_PROPERTY), TEMPLATES_DIR)
        .toAbsolutePath();

    if (Files.isDirectory(templateDirectoryPath)) {
      try {
        return Files.list(templateDirectoryPath).filter(p -> Files.isDirectory(p)).map(p -> {
          ValidateAnalysisTemplate validateAnalysisTemplate = new ValidateAnalysisTemplate(p.getFileName().toString());

          Path schemaFormPath = Paths.get(p.toString(), SCHEMA_FORM_FILE_NAME);
          validateAnalysisTemplate.setRoutinePath(Paths.get(p.toString(), ROUTINE_FILE_NAME));
          validateAnalysisTemplate.setReportPath(Paths.get(p.toString(), REPORT_FILE_NAME));

          if (Files.isRegularFile(schemaFormPath)) {
            try {
              String schemaForm = Files.lines(schemaFormPath).reduce("", String::concat).trim();
              validateAnalysisTemplate
                  .setSchemaForm(new JSONObject(Strings.isNullOrEmpty(schemaForm) ? "{}" : schemaForm));

              validateAnalysisTemplate.setTitle(validateAnalysisTemplate.getJSONSchemaForm().optString("title"));
              validateAnalysisTemplate.setTitle(validateAnalysisTemplate.getJSONSchemaForm().optString("description"));
            } catch (IOException e) {
              log.error("Error reading file at path {}", schemaFormPath);
            }
          }

          return validateAnalysisTemplate;
        }).collect(Collectors.toList());
      } catch (IOException e) {
        log.error("No templates directory.");
      }
    }

    return Lists.newArrayList();
  }

  private AnalysisTemplate getTemplate(String name) throws NoSuchAnalysisTemplateException {
    return getAnalysisTemplates().stream().filter(t -> name.equals(t.getName())).findFirst()
        .orElseThrow(() -> new NoSuchAnalysisTemplateException(name));
  }
}
