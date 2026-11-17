package com.acuity.visualisations.web.service.wizard.study;

import com.acuity.visualisations.web.service.wizard.study.vo.ServiceLink;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Locale;

/**
 * Business logic for the service block.
 */
@Service
public class ServiceLinkService {

    @Autowired
    private ServiceLinkRepository serviceLinkRepository;

    public List<ServiceLink> getServiceLinks() {
        return serviceLinkRepository.selectAll();
    }

    /**
     * Saves the list of items: creates those without an id and updates the rest.
     */
    @Transactional
    public void saveServiceLinks(List<ServiceLink> links) {
        for (ServiceLink link : links) {
            if (link.getId() == null) {
                serviceLinkRepository.insertServiceLink(link);
            } else {
                serviceLinkRepository.updateServiceLink(link);
            }
        }
    }

    public void deleteServiceLink(long id) {
        serviceLinkRepository.deleteServiceLink(id);
    }
}
