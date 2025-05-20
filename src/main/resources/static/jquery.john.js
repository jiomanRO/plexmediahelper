$(document).ready(function() {
    fetchInfo();
});

function fetchInfo() {
    $('#loadingIndicator').show();
    $.ajax({
        type: "GET",
        url : "/mainView",
        success : function(data) {
            $("#mainDisplayPanel").html(data);
        },
        error: function(xhr, status, error) {
            // Handle errors here
            console.error('AJAX Error:', status, error);
            $('#mainDisplayPanel').html('<p>An error occurred while fetching data.</p>');
        },
        complete: function() {
            // Hide the loading indicator after request completion
            $('#loadingIndicator').hide();
        }
    });
}
