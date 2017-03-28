package org.tsaap.assignments.statement

import org.tsaap.assignments.JsonDefaultSpecification

/**
 * Created by qsaieb on 28/03/2017.
 */
class ExplanationChoice extends JsonDefaultSpecification {
  private static final String INDEX = "index"
  private static final String EXPLANATION = "explanation"


  ExplanationChoice () {}

  /**
   * Create an explanation choice object
   *
   * @param index the index of the choice in the list of choice starting from 1
   * @param explanation the asscociated to this choice
   */
  ExplanationChoice(Integer index, String explanation) {
    this.index = index
    this.explanation = explanation
  }

  Integer getIndex() {
    getSpecificationProperty(INDEX)
  }

  void setIndex(Integer index) {
    setSpecificationProperty(INDEX, index)
  }

  String getExplanation() {
    getSpecificationProperty(EXPLANATION)
  }

  void setExplanation(String explanation) {
    setSpecificationProperty(EXPLANATION, explanation)
  }


  static constraints = {
    index nullable: false
    explanation nullable: false
  }
}
