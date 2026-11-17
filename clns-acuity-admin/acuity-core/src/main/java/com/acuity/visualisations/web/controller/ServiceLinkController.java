package com.acuity.visualisations.web.controller;

import com.acuity.visualisations.web.service.wizard.study.ServiceLinkService;
import com.acuity.visualisations.web.service.wizard.study.vo.ServiceLink;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.List;

/**
 * REST controller for the service block. Base path: /service-links.
 * GET loads the list from the database, POST saves the list to the database.
 */
@Controller
@RequestMapping("/service-links")
@PreAuthorize("@permissionHelper.isGlobalAdmin(authentication)")
public class ServiceLinkController {

    @Autowired
    private ServiceLinkService serviceLinkService;

    @RequestMapping(method = RequestMethod.GET)
    @ResponseBody
    public List<ServiceLink> list() {
        return serviceLinkService.getServiceLinks();
    }

    @RequestMapping(method = RequestMethod.POST)
    @ResponseStatus(HttpStatus.OK)
    public void save(@RequestBody List<ServiceLink> links) {
        serviceLinkService.saveServiceLinks(links);
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
    @ResponseStatus(HttpStatus.OK)
    public void delete(@PathVariable("id") long id) {
        serviceLinkService.deleteServiceLink(id);
    }

    /**
     * Handles validation errors and returns 400 with a clear message.
     */
    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ResponseBody
    public String handleValidationError(IllegalArgumentException e) {
        return e.getMessage();
    }
}
