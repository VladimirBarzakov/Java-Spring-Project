/* 
 * Atia & Tiger technology 2019.
 */
// Handle notifications
$(document).on({
    ajaxStart: () => $("#loadingBox").show(),
    ajaxStop: () => $('#loadingBox').fadeOut()
});

function showInfo() {
    let infoBox = $('#infoBox');
    infoBox.show();
    setTimeout(() => infoBox.fadeOut(), 3000);
}

function showError() {
    let errorBox = $('#errorBox');
    errorBox.show();
}

function handleError() {
    showError();
}

