package com.acuity.visualisations.web.service.wizard.study.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.ibatis.type.Alias;

/**
 * Model for a service block item: title and URL. Maps to the map_service_link table.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Alias("ServiceLink")
public class ServiceLink {
    private Long id;
    private String title;
    private String url;
    private Integer sortOrder;
}
