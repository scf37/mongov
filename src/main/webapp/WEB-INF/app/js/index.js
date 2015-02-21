$('input.search').keypress(function (e) {
	if (e.which == 13) {
		var q = web.decodeParams(true)
		var field = $(this).attr('field')
		var value = $(this).val()
		if (value != '') {
			q['search.' + field] = value;
		} else {
			delete q['search.' + field];
		}
		document.location = '?' + web.encodeParams(q, true);
	}
});

$('a.delete_element').click(function() {
	//id="del_123456"
	var id = $(this).attr('id').substring(4)
	if (!confirm('Delete ' + id + '?')) return
	$.ajax({
		type: 'POST',
		url: 'delete/' + id,
		success: function() {
			window.location.reload();
		},
		error: function() {
			alert('error deleting element');
		}
	});
})