package com.acuity.visualisations.web.service.wizard.study.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Model for the feedback block: the contact email. Maps to the map_feedback table.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Feedback {
    private Long id;
    private String email;
}
