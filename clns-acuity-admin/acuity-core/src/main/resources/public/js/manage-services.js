var ManageServices = function () {
    // TODO: replace the "X" placeholders with real endpoints when the BE is ready
    var LOAD_URL = "X";
    var SAVE_SERVICES_URL = "X";
    var SAVE_FEEDBACK_URL = "X";

    var FEEDBACK_ID = "feedback";
    var DEFAULT_SERVICES = {
        "1": "‘Omics’ Services",
        "2": "Patient Services",
        "3": "AI and Machine Learning Services"
    };

    var $saveServicesBtn = $("#saveServicesBtn");
    var $saveFeedbackBtn = $("#saveFeedbackBtn");
    var $feedbackEmail = $("#feedbackEmail");

    // last known (saved) values, used to detect changes
    var savedServices = {};
    var savedEmail = "";

    var init = function () {
        // fill all defaults first: rendering a single service recalculates the Save button state for all of them
        _.forEach(DEFAULT_SERVICES, function (title, id) {
            savedServices[id] = {title: title, link: ""};
        });
        _.forEach(DEFAULT_SERVICES, function (title, id) {
            renderService(id);
        });
        renderFeedback();

        $(".service-block").on("input", "input", updateSaveServicesBtn);
        $feedbackEmail.on("input", updateSaveFeedbackBtn);
        $saveServicesBtn.on("click", saveServices);
        $saveFeedbackBtn.on("click", saveFeedback);

        $('a[data-toggle="tab"]').on("shown.bs.tab", function (e) {
            $("#adminSearch").closest(".col-md-4").toggle($(e.target).attr("href") === "#summaryTab");
        });

        loadData();
    };

    var loadData = function () {
        // On failure (e.g. BE is not ready) the defaults are kept, so no error dialog is shown.
        $.ajax({
            url: LOAD_URL,
            type: "GET",
            dataType: "json",
            headers: {"X-Requested-With": "XMLHttpRequest"}
        }).done(function (data) {
            var items = _.isArray(data) ? data : [];
            _.forEach(DEFAULT_SERVICES, function (defaultTitle, id) {
                var item = _.find(items, function (i) {
                    return String(i.id) === id;
                });
                if (item) {
                    savedServices[id] = {title: item.title || "", link: item.link || ""};
                    renderService(id);
                }
            });
            var feedback = _.find(items, function (i) {
                return i.id === FEEDBACK_ID;
            });
            if (feedback) {
                savedEmail = feedback.email || "";
                renderFeedback();
            }
        });
    };

    var getServiceBlock = function (id) {
        return $('.service-block[data-service-id="' + id + '"]');
    };

    var renderService = function (id) {
        var $block = getServiceBlock(id);
        $block.find(".service-title").val(savedServices[id].title);
        $block.find(".service-link").val(savedServices[id].link);
        updateServiceHeading(id);
        updateSaveServicesBtn();
    };

    var renderFeedback = function () {
        $feedbackEmail.val(savedEmail);
        updateSaveFeedbackBtn();
    };

    // the heading shows the last saved title, it does not follow the Title input while typing
    var updateServiceHeading = function (id) {
        getServiceBlock(id).find(".service-heading").text(savedServices[id].title);
    };

    var readService = function ($block) {
        return {
            id: $block.data("service-id"),
            title: $block.find(".service-title").val(),
            link: $block.find(".service-link").val()
        };
    };

    var getChangedServices = function () {
        var changed = [];
        $(".service-block").each(function () {
            var service = readService($(this));
            var saved = savedServices[service.id];
            if (service.title !== saved.title || service.link !== saved.link) {
                changed.push(service);
            }
        });
        return changed;
    };

    var updateSaveServicesBtn = function () {
        setEnabled($saveServicesBtn, getChangedServices().length > 0);
    };

    var updateSaveFeedbackBtn = function () {
        setEnabled($saveFeedbackBtn, $feedbackEmail.val() !== savedEmail);
    };

    var setEnabled = function ($btn, enabled) {
        $btn.prop("disabled", !enabled);
    };

    var sendSave = function (url, payload, onSuccess) {
        $.ajax({
            url: url,
            type: "PUT",
            contentType: "application/json",
            data: JSON.stringify(payload),
            headers: {"X-Requested-With": "XMLHttpRequest"}
        }).done(onSuccess).fail(function (jqXHR) {
            if (jqXHR.status == 401 || jqXHR.status == 419) {
                wizardCommonModule.showSessionExpiredDialog();
            } else {
                wizardCommonModule.showErrorDialog(jqXHR.statusText, jqXHR.responseText);
            }
        });
    };

    var saveServices = function () {
        var changed = getChangedServices();
        if (changed.length === 0) {
            return;
        }
        sendSave(SAVE_SERVICES_URL, changed, function () {
            _.forEach(changed, function (service) {
                savedServices[service.id] = {title: service.title, link: service.link};
                updateServiceHeading(service.id);
            });
            updateSaveServicesBtn();
        });
    };

    var saveFeedback = function () {
        var email = $feedbackEmail.val();
        sendSave(SAVE_FEEDBACK_URL, {id: FEEDBACK_ID, email: email}, function () {
            savedEmail = email;
            updateSaveFeedbackBtn();
        });
    };

    init();
};

jQuery(function ($) {
    new ManageServices();
});
