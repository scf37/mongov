if (!web) {
	var web = new Object();
}

web.decodeParams = function(qs, url) {
	if (typeof qs !== 'string') {
		qs = window.location.search;
		url = true;
	}
	if (url) {
		var of = qs.indexOf('?');
		if (of >= 0) {
			if (of == qs.length - 1) { //'?' is the last char
				return {};
			}
			qs = qs.substring(of + 1);
		} else {
			return {};
		}
	}
	return $.deparam(qs);
}

web.encodeParams = function (obj) {
	return $.param(obj);
}
