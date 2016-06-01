package org.tsaap.notes

import org.tsaap.questions.ResponseNotificationService

/**
 * Created by dorian on 26/05/15.
 */
class ResponseNotificationJob {

    ResponseNotificationService responseNotificationService

    static triggers = {
        // every 5 minutes
        simple name: 'responseNotificationTrigger', startDelay: 10000, repeatInterval: 300000
    }

    def execute() {
        log.debug("Start response notification job...")
        responseNotificationService.notifyUsersOnResponsesAndMentions()
        log.debug("End response notification job.")
    }
}
