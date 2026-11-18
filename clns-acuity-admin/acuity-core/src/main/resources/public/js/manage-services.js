var ManageServices = function () {
    // Endpoint of the service block (ServiceLinkController): GET loads, POST saves all blocks at once
    var SERVICE_LINKS_URL = "/service-links";

    // The page always shows these three blocks, in this order (sortOrder 1..3)
    var DEFAULT_TITLES = [
        "\u2018Omics\u2019 Services",
        "Patient Services",
        "AI and Machine Learning Services"
    ];

    var $saveBtn = $("#saveServicesBtn");
    var $blocks = $(".service-block");

    // last saved state, used to detect changes
    var savedServices = [];

    var init = function () {
        $saveBtn.on("click", saveServices);
        $blocks.on("input", ".service-title, .service-link", updateSaveBtn);
        loadData();
    };

    // Fills the three blocks from the DB. Missing rows fall back to the default title and an empty link.
    var loadData = function () {
        $.ajax({
            url: SERVICE_LINKS_URL,
            type: "GET",
            dataType: "json",
            headers: {"X-Requested-With": "XMLHttpRequest"}
        }).done(function (data) {
            var rows = _.isArray(data) ? data : [];
            savedServices = _.map(DEFAULT_TITLES, function (defaultTitle, index) {
                var row = rows[index] || {};
                return {
                    dbId: row.id || null,
                    title: row.title || defaultTitle,
                    url: row.url || "",
                    sortOrder: index + 1
                };
            });
            render();
        }).fail(handleError);
    };

    var render = function () {
        _.forEach(savedServices, function (service, index) {
            var $block = $blocks.eq(index);
            $block.find(".service-heading").text(service.title);
            $block.find(".service-title").val(service.title);
            $block.find(".service-link").val(service.url);
        });
        updateSaveBtn();
    };

    // Reads all three blocks in display order
    var readServicesFromPage = function () {
        return _.map(savedServices, function (service, index) {
            var $block = $blocks.eq(index);
            return {
                id: service.dbId,
                title: $block.find(".service-title").val(),
                url: $block.find(".service-link").val(),
                sortOrder: index + 1
            };
        });
    };

    var hasChanges = function () {
        var current = readServicesFromPage();
        return _.some(current, function (c, index) {
            var s = savedServices[index];
            return c.title !== s.title || c.url !== s.url;
        });
    };

    var updateSaveBtn = function () {
        $saveBtn.prop("disabled", !hasChanges());
    };

    // Sends all three blocks in one POST. The backend creates rows without id and updates rows with id.
    var saveServices = function () {
        $.ajax({
            url: SERVICE_LINKS_URL,
            type: "POST",
            contentType: "application/json",
            data: JSON.stringify(readServicesFromPage()),
            headers: {"X-Requested-With": "XMLHttpRequest"}
        }).done(loadData).fail(handleError);
    };

    var handleError = function (jqXHR) {
        if (jqXHR.status == 401 || jqXHR.status == 419) {
            wizardCommonModule.showSessionExpiredDialog();
        } else {
            wizardCommonModule.showErrorDialog(jqXHR.statusText, jqXHR.responseText);
        }
    };

    init();
};

/**
 * Feedback block: a single email. Stored in the map_feedback table, loaded and saved via /feedback.
 */
var ManageFeedback = function () {
    var FEEDBACK_URL = "/feedback";
    var DEFAULT_EMAIL = "support@digitalecmt.org";

    var $email = $("#feedbackEmail");
    var $saveBtn = $("#saveFeedbackBtn");
    var savedEmail = "";

    var init = function () {
        $saveBtn.on("click", saveFeedback);
        $email.on("input", updateSaveBtn);
        loadData();
    };

    var loadData = function () {
        $.ajax({
            url: FEEDBACK_URL,
            type: "GET",
            dataType: "json",
            headers: {"X-Requested-With": "XMLHttpRequest"}
        }).done(function (data) {
            savedEmail = (data && data.email) || DEFAULT_EMAIL;
            $email.val(savedEmail);
            updateSaveBtn();
        }).fail(handleError);
    };

    var updateSaveBtn = function () {
        $saveBtn.prop("disabled", $email.val() === savedEmail);
    };

    var saveFeedback = function () {
        $.ajax({
            url: FEEDBACK_URL,
            type: "POST",
            contentType: "application/json",
            data: JSON.stringify({email: $email.val()}),
            headers: {"X-Requested-With": "XMLHttpRequest"}
        }).done(loadData).fail(handleError);
    };

    var handleError = function (jqXHR) {
        if (jqXHR.status == 401 || jqXHR.status == 419) {
            wizardCommonModule.showSessionExpiredDialog();
        } else {
            wizardCommonModule.showErrorDialog(jqXHR.statusText, jqXHR.responseText);
        }
    };

    init();
};

jQuery(function ($) {
    new ManageServices();
    new ManageFeedback();
});
