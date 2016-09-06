package org.tsaap.assignments

import grails.transaction.Transactional
import org.tsaap.attachement.Attachement
import org.tsaap.contracts.Contract
import org.tsaap.directory.User

@Transactional
class AssignmentService {

    /**
     * Save an assignment
     * @param assignment the assignment to save
     *
     * @return the assignment saved or with errors
     */
    Assignment saveAssignment(Assignment assignment, Schedule schedule = null) {
        if (schedule) {
            schedule.save()
            schedule.assignment = assignment
        }
        if (assignment.globalId == null) {
            assignment.globalId = UUID.randomUUID().toString()
        }
        assignment.lastUpdated = new Date();
        assignment.save()
        assignment
    }

    /**
     * Delete assignment
     * @param assignment the assignment to delete
     * @param user the user performing deletion
     * @param flush force flush
     */
    def deleteAssignment(Assignment assignment, User user, boolean flush = false) {
        Contract.requires(assignment.owner == user, USER__MUST__BE__ASSIGNMENT__OWNER)
        assignment.schedule?.delete(flush: flush)
        Interaction.executeUpdate("delete Interaction i where i.sequence in (from Sequence s where s.assignment = ?)",[assignment])
        Attachement.executeUpdate("update Attachement attach set toDelete=true, statement=null where attach.statement in (select s.statement from Sequence s where s.assignment = ?)",[assignment])
        Statement.executeUpdate("delete Statement st  where st in (select s.statement from Sequence s where s.assignment = ?)",[assignment])
        //sequences are deleted by cascade
//        def query = Sequence.where {
//            assignment == assignment
//        }
//        query.deleteAll()
        assignment.delete(flush: flush)
    }


    /**
     * Remove sequence from assignment
     * @param sequence the sequence to remove
     * @param assignment the assignment containing the sequence
     * @param user the user performing the operation
     * @return the modified assignment
     */
    Assignment removeSequenceFromAssignment(Sequence sequence, Assignment assignment, User user) {
        Contract.requires(assignment.owner == user, USER__MUST__BE__ASSIGNMENT__OWNER)
        Contract.requires(assignment.sequences?.contains(sequence), SEQUENCE__DOESN__T__BELONG__TO__ASSIGNMENT)
        Attachement attachement = Attachement.findByStatement(sequence.statement)
        if (attachement) {
            attachement.toDelete = true
            attachement.statement = null
            attachement.save()
        }
        def query = Interaction.where {
            sequence == sequence
        }
        query.deleteAll()
        sequence.statement.delete(flush: true)
        // sequence is deleted by cascade
        //sequence.delete()
        assignment.lastUpdated = new Date()
        assignment.save()
        assignment
    }

    /**
     * Swap two sequences in an assignment
     * @param assignment the assignment
     * @param user the user performing the oeration
     * @param sequence1 the first sequence
     * @param sequence2 the second sequence
     * @return the assignement after swapping
     */
    Assignment swapSequences(Assignment assignment, User user, Sequence sequence1, Sequence sequence2) {
        Contract.requires(assignment.owner == user, USER__MUST__BE__ASSIGNMENT__OWNER)
        Contract.requires(assignment.sequences?.contains(sequence1), SEQUENCE__DOESN__T__BELONG__TO__ASSIGNMENT)
        Contract.requires(assignment.sequences?.contains(sequence2), SEQUENCE__DOESN__T__BELONG__TO__ASSIGNMENT)
        def rank1 = sequence1.rank
        sequence1.rank = sequence2.rank
        sequence2.rank = rank1
        sequence1.save()
        sequence2.save()
        assignment.lastUpdated = new Date()
        assignment.save()
        assignment
    }

    public static final String USER__MUST__BE__ASSIGNMENT__OWNER = "USER_MUST_BE_ASSIGNMENT_OWNER"
    public static final String SEQUENCE__DOESN__T__BELONG__TO__ASSIGNMENT = "SEQUENCE_DOESN_T_BELONG_TO_ASSIGNMENT"


}
