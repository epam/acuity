/*
 * Copyright 2021 The University of Manchester
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.acuity.visualisations.rest.resources;

import com.acuity.visualisations.rawdatamodel.filters.AeFilters;
import com.acuity.visualisations.rawdatamodel.filters.AssessmentFilters;
import com.acuity.visualisations.rawdatamodel.filters.ChemotherapyFilters;
import com.acuity.visualisations.rawdatamodel.filters.ConmedFilters;
import com.acuity.visualisations.rawdatamodel.filters.DiseaseExtentFilters;
import com.acuity.visualisations.rawdatamodel.filters.DoseDiscFilters;
import com.acuity.visualisations.rawdatamodel.filters.DrugDoseFilters;
import com.acuity.visualisations.rawdatamodel.filters.LabFilters;
import com.acuity.visualisations.rawdatamodel.filters.MedicalHistoryFilters;
import com.acuity.visualisations.rawdatamodel.filters.NonTargetLesionFilters;
import com.acuity.visualisations.rawdatamodel.filters.PathologyFilters;
import com.acuity.visualisations.rawdatamodel.filters.PopulationFilters;
import com.acuity.visualisations.rawdatamodel.filters.RadiotherapyFilters;
import com.acuity.visualisations.rawdatamodel.filters.SecondTimeOfProgressionFilters;
import com.acuity.visualisations.rawdatamodel.filters.SurgicalHistoryFilters;
import com.acuity.visualisations.rawdatamodel.filters.SurvivalStatusFilters;
import com.acuity.visualisations.rawdatamodel.filters.TargetLesionFilters;
import com.acuity.visualisations.rawdatamodel.service.event.AeService;
import com.acuity.visualisations.rawdatamodel.service.event.AssessmentService;
import com.acuity.visualisations.rawdatamodel.service.event.ConmedsService;
import com.acuity.visualisations.rawdatamodel.service.event.CurrentMedicalHistoryService;
import com.acuity.visualisations.rawdatamodel.service.event.DiseaseExtentService;
import com.acuity.visualisations.rawdatamodel.service.event.DoseDiscService;
import com.acuity.visualisations.rawdatamodel.service.event.DoseLimitingService;
import com.acuity.visualisations.rawdatamodel.service.event.DrugDoseService;
import com.acuity.visualisations.rawdatamodel.service.event.LabService;
import com.acuity.visualisations.rawdatamodel.service.event.NonTargetLesionService;
import com.acuity.visualisations.rawdatamodel.service.event.PastChemotherapyService;
import com.acuity.visualisations.rawdatamodel.service.event.PastMedicalHistoryService;
import com.acuity.visualisations.rawdatamodel.service.event.PathologyService;
import com.acuity.visualisations.rawdatamodel.service.event.PatientOutcomeSummaryService;
import com.acuity.visualisations.rawdatamodel.service.PopulationService;
import com.acuity.visualisations.rawdatamodel.service.event.PostChemotherapyService;
import com.acuity.visualisations.rawdatamodel.service.event.RadiotherapyService;
import com.acuity.visualisations.rawdatamodel.service.event.SecondTimeOfProgressionService;
import com.acuity.visualisations.rawdatamodel.service.event.SurgicalHistoryService;
import com.acuity.visualisations.rawdatamodel.service.event.SurvivalStatusService;
import com.acuity.visualisations.rawdatamodel.service.event.TargetLesionService;
import com.acuity.visualisations.rawdatamodel.service.ssv.PatientSummaryDocumentService;
import com.acuity.visualisations.rawdatamodel.service.ssv.SingleSubjectViewSummaryService;
import com.acuity.visualisations.rest.model.request.patient.summary.PatientSummaryDocumentRequest;
import com.acuity.visualisations.rest.model.request.SingleSubjectRequest;
import com.acuity.va.security.acl.domain.Dataset;
import com.acuity.visualisations.rest.util.Constants;
import org.docx4j.openpackaging.exceptions.Docx4JException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.PermissionEvaluator;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import jakarta.xml.bind.JAXBException;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static com.acuity.visualisations.rawdatamodel.service.ssv.SingleSubjectViewSummaryService.SsvTableMetadata;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.web.bind.annotation.RequestMethod.POST;

@RestController
@RequestMapping(value = "/resources/summary/", consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
@PreAuthorize(Constants.PRE_AUTHORISE_VISUALISATION)
@CacheConfig(keyGenerator = "datasetsKeyGenerator", cacheResolver = "refreshableCacheResolver")
public class PatientSummaryResource {

    @Autowired
    private PopulationService populationService;
    @Autowired
    private PatientOutcomeSummaryService patientSummaryService;
    @Autowired
    private PastMedicalHistoryService pastMedicalHistoryService;
    @Autowired
    private CurrentMedicalHistoryService currentMedicalHistoryService;
    @Autowired
    private NonTargetLesionService nonTargetLesionService;
    @Autowired
    private PermissionEvaluator permissionEvaluator;
    @Autowired
    private AssessmentService assessmentService;
    @Autowired
    private SurgicalHistoryService surgicalHistoryService;
    @Autowired
    private ConmedsService conmedService;
    @Autowired
    private LabService labService;
    @Autowired
    private PathologyService pathologyService;
    @Autowired
    private DiseaseExtentService diseaseExtentService;
    @Autowired
    private PastChemotherapyService pastChemotherapyService;
    @Autowired
    private SingleSubjectViewSummaryService singleSubjectViewSummaryService;
    @Autowired
    private PatientSummaryDocumentService documentService;
    @Autowired
    private RadiotherapyService radiotherapyService;
    @Autowired
    private PostChemotherapyService postChemotherapyService;
    @Autowired
    private TargetLesionService targetLesionService;
    @Autowired
    private DoseDiscService doseDiscService;
    @Autowired
    private DrugDoseService drugDoseService;
    @Autowired
    private DoseLimitingService doseLimitingService;
    @Autowired
    private SecondTimeOfProgressionService secondTimeOfProgressionService;
    @Autowired
    private AeService aeService;
    @Autowired
    private SurvivalStatusService survivalStatusService;


    @RequestMapping(value = "/document", method = POST)
    public void getDocument(@RequestBody @Valid PatientSummaryDocumentRequest requestBody, HttpServletResponse response)
            throws JAXBException, IOException, Docx4JException {

        final Optional<ByteArrayOutputStream> byteArrayOutputStream = documentService.generateDocument(requestBody.getDatasetsObject(),
                requestBody.getSubjectId(), hasTumourAccess(requestBody.getDatasets()), requestBody.getTimeZoneOffset());

        if (byteArrayOutputStream.isPresent()) {
            response.setContentType("application/vnd.openxmlformats-officedocument.wordprocessingml.document");
            response.getOutputStream().write(byteArrayOutputStream.get().toByteArray());
            response.flushBuffer();
        } else {
            // sendError() would forward POST /error to the view controller (GET-only), causing 405.
            // Write the error response directly to avoid Tomcat's error-page forwarding.
            response.setStatus(HttpStatus.NOT_IMPLEMENTED.value());
            response.setContentType("application/json");
            response.getWriter().write("{\"message\":\"Document export is unavailable: docx4j not initialised\"}");
            response.getWriter().flush();
        }
    }


    @RequestMapping(value = "/metadata", method = POST)
    public List<SsvTableMetadata> getMetadata(
            @RequestBody @Valid SingleSubjectRequest<PopulationFilters> requestBody) {
        return singleSubjectViewSummaryService.getMetadata(requestBody.getDatasetsObject(), hasTumourAccess(requestBody.getDatasets()));
    }

    @RequestMapping(value = "/demography", method = POST)
    @Cacheable
    public List<Map<String, String>> getDemography(
            @RequestBody @Valid SingleSubjectRequest<PopulationFilters> requestBody) {
        return populationService.getSingleSubjectData(
                requestBody.getDatasetsObject(), requestBody.getSubjectId(), requestBody.getEventFilters());
    }

    @RequestMapping(value = "/outcome-summary", method = POST)
    public List<Map<String, String>> getOutcomeSummary(
            @RequestBody @Valid SingleSubjectRequest<PopulationFilters> requestBody) {

        return patientSummaryService.getSingleSubjectData(
                requestBody.getDatasetsObject(), requestBody.getSubjectId(), hasTumourAccess(requestBody.getDatasets()));
    }

    private boolean hasTumourAccess(List<Dataset> datasets) {
        return permissionEvaluator.hasPermission(SecurityContextHolder.getContext().getAuthentication(),
                datasets, "VIEW_ONCOLOGY_PACKAGE");
    }

    @RequestMapping(value = "/past-medical-history", method = POST)
    @Cacheable
    public List<Map<String, String>> getPastMedicalHistory(
            @RequestBody @Valid SingleSubjectRequest<MedicalHistoryFilters> requestBody) {
        return pastMedicalHistoryService.getSingleSubjectData(requestBody.getDatasetsObject(), requestBody.getSubjectId(), requestBody.getEventFilters());
    }

    @PreAuthorize(Constants.PRE_AUTHORISE_VISUALISATION_AND_ONCOLOGY)
    @RequestMapping(value = "/assessment", method = POST)
    public List<Map<String, String>> getAssessment(
            @RequestBody @Valid SingleSubjectRequest<AssessmentFilters> requestBody) {
        return assessmentService.getSingleSubjectData(
                requestBody.getDatasetsObject(), requestBody.getSubjectId(), requestBody.getEventFilters());
    }

    @RequestMapping(value = "/surgical-history", method = POST)
    @Cacheable
    public List<Map<String, String>> getSurgicalHistory(
            @RequestBody @Valid SingleSubjectRequest<SurgicalHistoryFilters> requestBody) {
        return surgicalHistoryService.getSingleSubjectData(requestBody.getDatasetsObject(), requestBody.getSubjectId(), requestBody.getEventFilters());
    }

    @RequestMapping(value = "/concurrent-conditions", method = POST)
    @Cacheable
    public List<Map<String, String>> getConcurrentConditionsAtStudyEntry(
            @RequestBody @Valid SingleSubjectRequest<MedicalHistoryFilters> requestBody) {
        return currentMedicalHistoryService.getSingleSubjectData(requestBody.getDatasetsObject(), requestBody.getSubjectId(),
                requestBody.getEventFilters());
    }

    @RequestMapping(value = "/conmeds", method = POST)
    @Cacheable
    public List<Map<String, String>> getConmeds(
            @RequestBody @Valid SingleSubjectRequest<ConmedFilters> requestBody) {
        return conmedService.getSingleSubjectData(requestBody.getDatasetsObject(), requestBody.getSubjectId(), requestBody.getEventFilters());
    }

    @RequestMapping(value = "/labs", method = POST)
    @Cacheable
    public List<Map<String, String>> getLabs(
            @RequestBody @Valid SingleSubjectRequest<LabFilters> requestBody) {
        return labService.getOutOfRangeSingleSubjectData(requestBody.getDatasetsObject(), requestBody.getSubjectId(), requestBody.getEventFilters());
    }

    @PreAuthorize(Constants.PRE_AUTHORISE_VISUALISATION_AND_ONCOLOGY)
    @RequestMapping(value = "/pathgen", method = POST)
    public List<Map<String, String>> getPathology(
            @RequestBody @Valid SingleSubjectRequest<PathologyFilters> requestBody) {
        return pathologyService.getSingleSubjectData(requestBody.getDatasetsObject(), requestBody.getSubjectId(), requestBody.getEventFilters());
    }

    @PreAuthorize(Constants.PRE_AUTHORISE_VISUALISATION_AND_ONCOLOGY)
    @RequestMapping(value = "/disease-extent", method = POST)
    public List<Map<String, String>> getDiseaseExtent(
            @RequestBody @Valid SingleSubjectRequest<DiseaseExtentFilters> requestBody) {
        return diseaseExtentService.getSingleSubjectData(requestBody.getDatasetsObject(), requestBody.getSubjectId(), requestBody.getEventFilters());
    }

    @PreAuthorize(Constants.PRE_AUTHORISE_VISUALISATION_AND_ONCOLOGY)
    @RequestMapping(value = "/past-chemotherapy", method = POST)
    public List<Map<String, String>> getPastChemotherapy(
            @RequestBody @Valid SingleSubjectRequest<ChemotherapyFilters> requestBody) {
        return pastChemotherapyService.getSingleSubjectData(requestBody.getDatasetsObject(), requestBody.getSubjectId(), requestBody.getEventFilters());
    }

    @PreAuthorize(Constants.PRE_AUTHORISE_VISUALISATION_AND_ONCOLOGY)
    @RequestMapping(value = "/past-radiotherapy", method = POST)
    public List<Map<String, String>> getPastRadiotherapy(
            @RequestBody @Valid SingleSubjectRequest<RadiotherapyFilters> requestBody) {
        return radiotherapyService.getSingleSubjectData(requestBody.getDatasetsObject(), requestBody.getSubjectId(), requestBody.getEventFilters());
    }

    @PreAuthorize(Constants.PRE_AUTHORISE_VISUALISATION_AND_ONCOLOGY)
    @RequestMapping(value = "/target-lesion", method = POST)
    public List<Map<String, String>> getTargetLesion(
            @RequestBody @Valid SingleSubjectRequest<TargetLesionFilters> requestBody) {
        return targetLesionService.getSingleSubjectData(requestBody.getDatasetsObject(),
                requestBody.getSubjectId(), requestBody.getEventFilters());
    }

    @PreAuthorize(Constants.PRE_AUTHORISE_VISUALISATION_AND_ONCOLOGY)
    @RequestMapping(value = "/non-target-lesion", method = POST)
    @Cacheable
    public List<Map<String, String>> getNonTargetLesion(
            @RequestBody @Valid SingleSubjectRequest<NonTargetLesionFilters> requestBody) {
        return nonTargetLesionService.getSingleSubjectData(requestBody.getDatasetsObject(),
                requestBody.getSubjectId(), requestBody.getEventFilters());
    }

    @PreAuthorize(Constants.PRE_AUTHORISE_VISUALISATION_AND_ONCOLOGY)
    @RequestMapping(value = "/post-chemotherapy", method = POST)
    public List<Map<String, String>> getPostChemotherapy(
            @RequestBody @Valid SingleSubjectRequest<ChemotherapyFilters> requestBody) {
        return postChemotherapyService.getSingleSubjectData(requestBody.getDatasetsObject(), requestBody.getSubjectId(),
                requestBody.getEventFilters());
    }

    @RequestMapping(value = "/dosedisc", method = POST)
    @Cacheable
    public List<Map<String, String>> getDrugDisc(
            @RequestBody @Valid SingleSubjectRequest<DoseDiscFilters> requestBody) {
        return doseDiscService.getSingleSubjectData(requestBody.getDatasetsObject(), requestBody.getSubjectId(),
                requestBody.getEventFilters());
    }

    @RequestMapping(value = "/drug-dose", method = POST)
    @Cacheable
    public List<Map<String, String>> getDrugDos(
            @RequestBody @Valid SingleSubjectRequest<DrugDoseFilters> requestBody) {
        return drugDoseService.getSingleSubjectData(requestBody.getDatasetsObject(), requestBody.getSubjectId(),
                requestBody.getEventFilters());
    }

    @RequestMapping(value = "/dose-limiting", method = POST)
    @Cacheable
    public List<Map<String, String>> getDoseLimiting(
            @RequestBody @Valid SingleSubjectRequest<AeFilters> requestBody) {
        return doseLimitingService.getSingleSubjectData(requestBody.getDatasetsObject(), requestBody.getSubjectId(),
                requestBody.getEventFilters());
    }

    @PreAuthorize(Constants.PRE_AUTHORISE_VISUALISATION_AND_ONCOLOGY)
    @RequestMapping(value = "/second-time-of-progression", method = POST)
    public List<Map<String, String>> getSecondTimeOfProgression(
            @RequestBody @Valid SingleSubjectRequest<SecondTimeOfProgressionFilters> requestBody) {
        return secondTimeOfProgressionService.getSingleSubjectData(requestBody.getDatasetsObject(), requestBody.getSubjectId(),
                requestBody.getEventFilters());
    }

    @RequestMapping(value = "/aes", method = POST)
    @Cacheable
    public List<Map<String, String>> getAes(
            @RequestBody @Valid SingleSubjectRequest<AeFilters> requestBody) {
        return aeService.getSingleSubjectData(requestBody.getDatasetsObject(), requestBody.getSubjectId(),
                requestBody.getEventFilters());
    }

    @RequestMapping(value = "/header", method = POST)
    @Cacheable
    public List<Map<String, String>> getHeader(
            @RequestBody @Valid SingleSubjectRequest<SecondTimeOfProgressionFilters> requestBody) {
        return singleSubjectViewSummaryService.getHeaderData(requestBody.getDatasetsObject(), requestBody.getSubjectId());
    }

    @PreAuthorize(Constants.PRE_AUTHORISE_VISUALISATION_AND_ONCOLOGY)
    @RequestMapping(value = "/survival-status", method = POST)
    public List<Map<String, String>> getSurvivalStatus(
            @RequestBody @Valid SingleSubjectRequest<SurvivalStatusFilters> requestBody) {
        return survivalStatusService.getSingleSubjectData(requestBody.getDatasetsObject(), requestBody.getSubjectId(),
                requestBody.getEventFilters());
    }
}
