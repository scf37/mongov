if (!jsedit) {
	var jsedit = new Object();
}

function JsonEditor (root, onsave, oncancel) {
	root.html(root.html());
	
	var select = root.find('.jsonedit-select');
	var edit = root.find('.jsonedit-textarea');
	var applyButton = root.find('.jsonedit-apply');
	var saveButton = root.find('.jsonedit-save');
	var cancelButton = root.find('.jsonedit-cancel');
	//full json object we are editing
	var json;
	//list of json items can be set as current
	var items;
	//current json item (by default - first one aka ROOT)
	var currentItem;
	
	var modified;
	var modifiedText; //original text for modified
	var self = this;
	
	select.change(function(event) {
		if (modified) {
			if (confirm('Data has been modified. Apply changes?')) {
				apply();
				return;
			}
		}
		var ix = this.options[this.selectedIndex].value;
		setEdit(items[ix]);
	});
	
	applyButton.click(function() {
		apply();
	});
	
	saveButton.click(function() {
		if (modified) {
			apply();
		}
		
		onsave(currentItem.value);
	});
	
	cancelButton.click(function() {
		if (modified) {
			if (confirm('Data has been modified. Discard changes?')) {
				return;
			}
		}
		
		oncancel();
	});
	
	edit.bind("keyup paste", function(e) {
		var element = this;
		
		setTimeout(function () {
			if ($(element).val() != modifiedText)
		    {
		        modified = true;
		        edit.css("background-color", 'white');
		    } else {
		    	modified = false;
		        edit.css("background-color", '#f0ffff');
		    }
		  }, 100);
	});
	
	function apply() {
		if (modified) {
			var val;
			try {
				if (typeof currentItem.value == 'string'){
					val = edit.val();
				} else {
					val = JSON.parse(edit.val());
				}
			} catch (err) {
				alert('Invalid json: ' + err.message);
				return;
			}
			currentItem.setter(val);
			self.edit(json);
		}
	}
	
	function buildSelectItems(json_) {
		items = new Array();
		doBuildItems(json_, 'ROOT', items, function(v) {json = v});
		
		var s = '';
		for (var i = 0 ; i < items.length ; i++) {
			s += '<option value="' + i + '">' + items[i].title + "</option>";
		}
		select.html(s);
	}
	
	function doBuildItems(json, prefix, result, setter) {
		result.push({title: prefix, value: json, setter: setter});
		var type = typeof json
		if (json == null || type != 'object') {
			
		} else if ($.isArray(json)) {
			for (var i = 0 ; i < json.length ; i++) {
				var setter = (function (json, i) {
					return function(v) {
						json[i] = v;
					}
				})(json, i);
				doBuildItems(json[i], prefix + '[' + i + ']', result, setter);
			}
		} else {
			for (var key in json) {
				var setter = (function (json, key) {
					return function(v) {
						json[key] = v;
					}
				})(json, key);
				doBuildItems(json[key], prefix + '.' + key, result, setter);
			}	
		}
	}
	
	function setEdit(item) {
		currentItem = item;
		var json = item.value;
		if (typeof json == 'string') {
			edit.val(json);
		} else {
			edit.val(JSON.stringify(json, null, '  '));
		}
		edit.css("background-color", '#f0ffff');
		modified = false;
		modifiedText = edit.val();
		
	}

	
	this.edit = function(json_) {
		json = json_;
		buildSelectItems(json);
		
		setEdit(items[0]);
		
		
	}
}
