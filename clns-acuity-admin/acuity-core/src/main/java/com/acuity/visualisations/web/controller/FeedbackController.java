package com.acuity.visualisations.web.controller;

import com.acuity.visualisations.web.service.wizard.study.FeedbackService;
import com.acuity.visualisations.web.service.wizard.study.vo.Feedback;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * REST controller for the feedback block (email for the home page). Base path: /feedback.
 * GET loads the email from the database, POST saves it to the database.
 */
@Controller
@RequestMapping("/feedback")
@PreAuthorize("@permissionHelper.isGlobalAdmin(authentication)")
public class FeedbackController {

    @Autowired
    private FeedbackService feedbackService;

    @RequestMapping(method = RequestMethod.GET)
    @ResponseBody
    public Feedback get() {
        return feedbackService.getFeedback();
    }

    @RequestMapping(method = RequestMethod.POST)
    @ResponseStatus(HttpStatus.OK)
    public void save(@RequestBody Feedback feedback) {
        feedbackService.saveFeedback(feedback);
    }

    /**
     * Handles validation errors (invalid email) and returns 400 with a clear message.
     */
    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ResponseBody
    public String handleValidationError(IllegalArgumentException e) {
        return e.getMessage();
    }
}
