package com.acuity.visualisations.web.service.wizard.study;

import com.acuity.visualisations.web.service.wizard.study.vo.Feedback;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

/**
 * Repository for the feedback block. SQL is defined in mappers/FeedbackRepository.xml.
 * The interface name must match the namespace in the XML.
 */
@Repository
public interface FeedbackRepository {

    /** Returns the stored feedback row, or null if the table is empty. */
    Feedback selectFeedback();

    void insertFeedback(@Param("feedback") Feedback feedback);

    void updateFeedback(@Param("feedback") Feedback feedback);
}
