/*
 * Copyright 2013 Tsaap Development Group
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.tsaap.questions.gift

import org.tsaap.questions.Answer
import org.tsaap.questions.AnswerFragment
import org.tsaap.questions.QuestionType
import org.tsaap.questions.impl.DefaultQuestion
import org.tsaap.questions.impl.gift.GiftQuizContentHandler
import org.tsaap.questions.impl.gift.GiftReader
import spock.lang.Shared
import spock.lang.Specification

/**
 * See the API for {@link grails.test.mixin.domain.DomainClassUnitTestMixin} for usage instructions*/

class GiftReaderSpec extends Specification {

  @Shared
  def ec_q1_ok = '::Question 1:: What\'s between orange and green in the spectrum ? \n { =yellow ~red  ~blue }'
  @Shared
  def ec_q2_ok = '::Question 2:: What\'s between orange and green in the spectrum ? \n { =yellow # congrats ! ~red # try again  ~blue #not yet }'


  def "test the parsing of well formed Exclusive Choice question"() {

    given: "a text containing one well formated gift EC question"
    def questionText = currentQText

    when: "parsing the text with the GiftReader and the default gift content handler"
    GiftQuizContentHandler handler = new GiftQuizContentHandler()
    def quizReader = new GiftReader(quizContentHandler: handler)
    def reader = new StringReader(currentQText)
    quizReader.parse(reader)

    then: "the obtained quiz has one question with the title and type correctly set"
    def quiz = handler.quiz
    quiz.questionList.size() == 1
    DefaultQuestion question = quiz.questionList[0]
    question.title == title
    question.questionType == QuestionType.ExclusiveChoice

    and: "the question is composed with one answer fragment and at least one text fragment"
    question.answerFragmentList.size() == 1
    question.textFragmentList.size() == nbTextFragments
    question.fragmentList.size() == nbFragments

    and: "the answer fragment has at least two answers"
    AnswerFragment answerFragment = question.answerFragmentList[0]
    answerFragment.answerList.size() == nbAnswers
    Answer ans1 = answerFragment.answerList[0]
    Answer ans2 = answerFragment.answerList[1]

    and: "the answers have properties correctly set."
    ans1.identifier != null
    ans1.textValue == answerText1
    ans1.feedBack == answerFeedback1
    ans1.percentCredit == answerCredit1
    ans2.identifier != null
    ans2.textValue == answerText2
    ans2.feedBack == answerFeedback2
    ans2.percentCredit == answerCredit2



    where: "the given texts are representative of relevant use cases"
    currentQText | title        | nbFragments | nbTextFragments | nbAnswers | answerText1 | answerCredit1 | answerFeedback1 | answerText2 | answerCredit2 | answerFeedback2
    ec_q1_ok     | 'Question 1' | 2           | 1               | 3         | 'yellow'    | 100f          | null            | 'red'       | 0f            | null
    //ec_q2_ok     | 'Question 2' | 2           | 1               | 3         | 'yellow'    | 100f          | 'congrats !'    | 'red'       | 0f            | 'try again'

  }

}
