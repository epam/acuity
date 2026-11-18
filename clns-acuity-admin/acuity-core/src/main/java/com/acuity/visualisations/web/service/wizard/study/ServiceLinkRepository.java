package com.acuity.visualisations.web.service.wizard.study;

import com.acuity.visualisations.web.service.wizard.study.vo.ServiceLink;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repository for the service block. SQL is defined in mappers/ServiceLinkRepository.xml.
 * The interface name must match the namespace in the XML.
 */
@Repository
public interface ServiceLinkRepository {

    List<ServiceLink> selectAll();

    void insertServiceLink(@Param("link") ServiceLink link);

    void updateServiceLink(@Param("link") ServiceLink link);

    void deleteServiceLink(@Param("id") long id);
}
