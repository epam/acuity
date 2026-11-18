package com.acuity.visualisations.web.service.wizard.study;

import com.acuity.visualisations.web.service.wizard.study.vo.Feedback;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Business logic for the feedback block.
 */
@Service
public class FeedbackService {

    @Autowired
    private FeedbackRepository feedbackRepository;

    /** Returns the stored feedback, or an empty object if nothing is saved yet. */
    public Feedback getFeedback() {
        Feedback stored = feedbackRepository.selectFeedback();
        return stored != null ? stored : new Feedback();
    }

    /**
     * Saves the feedback: inserts the row if the table is empty, otherwise updates the existing row.
     */
    @Transactional
    public void saveFeedback(Feedback feedback) {
        validateEmail(feedback.getEmail());
        Feedback stored = feedbackRepository.selectFeedback();
        if (stored == null) {
            feedback.setId(null);
            feedbackRepository.insertFeedback(feedback);
        } else {
            feedback.setId(stored.getId());
            feedbackRepository.updateFeedback(feedback);
        }
    }

    /**
     * Accepts an empty value or a value that looks like an email address (something@something.tld).
     */
    private void validateEmail(String email) {
        if (email == null || email.isBlank()) {
            return;
        }
        if (!email.trim().matches("[^@\\s]+@[^@\\s]+\\.[^@\\s]+")) {
            throw new IllegalArgumentException("Invalid email address");
        }
    }
}
