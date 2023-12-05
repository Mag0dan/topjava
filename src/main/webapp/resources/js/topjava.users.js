const userAjaxUrl = "admin/users/";

// https://stackoverflow.com/a/5064235/548473
const ctx = {
    ajaxUrl: userAjaxUrl,
    updateTable: function () {
        $.ajax({
            type: "GET",
            url: userAjaxUrl
        }).done(updateTableByData);
    }
};

// $(document).ready(function () {
$(function () {
    makeEditable(
        $("#datatable").DataTable({
            "paging": false,
            "info": true,
            "columns": [
                {
                    "data": "name"
                },
                {
                    "data": "email"
                },
                {
                    "data": "roles"
                },
                {
                    "data": "enabled"
                },
                {
                    "data": "registered"
                },
                {
                    "defaultContent": "Edit",
                    "orderable": false
                },
                {
                    "defaultContent": "Delete",
                    "orderable": false
                }
            ],
            "order": [
                [
                    0,
                    "asc"
                ]
            ]
        })
    );
});

function setEnable(checkbox) {
    let id = $(checkbox).closest('tr').attr("id");
    $.ajax({
        type: "POST",
        url: ctx.ajaxUrl + id,
        data: {'enable': checkbox.checked},
    }).done(function () {
        $(checkbox).closest('tr').attr("data-user-enabled", checkbox.checked);
        successNoty(checkbox.checked ? "Enabled" : "Disabled");
    }).fail(function () {
        $(checkbox).prop("checked", !checkbox.checked);
    });
}