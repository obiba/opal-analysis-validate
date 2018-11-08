package org.obiba.analysis.opal.validate;

import java.nio.file.Path;
import java.util.List;

import org.json.JSONObject;
import org.obiba.magma.ValueType;
import org.obiba.opal.spi.analysis.AnalysisTemplate;

public class ValidateAnalysisTemplate implements AnalysisTemplate {

  private String name;
  private String title;
  private String description;
  private JSONObject schemaForm;
  private List<ValueType> valueTypes;

  private Path routinePath;
  private Path reportPath;

  public ValidateAnalysisTemplate(String name) {
    this.name = name;
  }

  /**
   * @param name the name to set
   */
  public void setName(String name) {
    this.name = name;
  }

  @Override
  public String getName() {
    return name;
  }

  /**
   * @param title the title to set
   */
  public void setTitle(String title) {
    this.title = title;
  }

  @Override
  public String getTitle() {
    return title;
  }

  /**
   * @param description the description to set
   */
  public void setDescription(String description) {
    this.description = description;
  }

  @Override
  public String getDescription() {
    return description;
  }

  /**
   * @param schemaForm the schemaForm to set
   */
  public void setSchemaForm(JSONObject schemaForm) {
    this.schemaForm = schemaForm;
  }

  @Override
  public JSONObject getJSONSchemaForm() {
    return schemaForm;
  }

  /**
   * @param valueTypes the valueTypes to set
   */
  public void setValueTypes(List<ValueType> valueTypes) {
    this.valueTypes = valueTypes;
  }

	@Override
	public List<ValueType> getValueTypes() {
		return valueTypes;
  }

  /**
   * @return the reportPath
   */
  public Path getReportPath() {
    return reportPath;
  }

  /**
   * @param reportPath the reportPath to set
   */
  public void setReportPath(Path reportPath) {
    this.reportPath = reportPath;
  }

  /**
   * @return the routinePath
   */
  public Path getRoutinePath() {
    return routinePath;
  }

  /**
   * @param routinePath the routinePath to set
   */
  public void setRoutinePath(Path routinePath) {
    this.routinePath = routinePath;
  }

}