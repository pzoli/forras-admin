/*
index = -1;

var updateTable = function() {
	// unselect all rows
	PF('table').unselectAllRows();
	// select previous row
	PF('table').selectRow(index);
	// set index to current selected row
	PF('table').originRowIndex = index;
};

var down = function() {
	// If no rows selected, select first row
	if (PF('table').selection.length === 0) {
		PF('table').selectRow(0);
		return;
	}
	// get index of selected row, if no row is selected return 0
	if (index === -1) {
		index = 0;
	} else {
		index = PF('table').originRowIndex;
	}
	// get amount of rows in the table
	var rows = PF('table').tbody[0].childElementCount;
	// increase row index
	index++;
	// if new index equals number of rows, set index to first row
	if (index === rows) {
		index = 0;
	}
	
	updateTable();
};

var up = function() {
	// get amount of rows in the table
	var rows = PF('table').tbody[0].childElementCount;
	// get index of selected row, if no row is selected return 0
	if (index === -1) {
		index = 0;
	} else {
		index = PF('table').originRowIndex;
	}
	// if this is first row, set index to last row
	if (index === 0) {
		index = rows - 1;
	} else {
		index--;
	}
	
	updateTable();
};

var showDetails = function() {
	var button = $('#lazyForm\\:tableView\\:lazyTable\\:' + index
			+ '\\:showClientButton');
	button.click();
};

$("#table_div").keydown(function(event) {
	if (event.which === 38) {
		event.preventDefault();
		up();
	}

	if (event.which === 40) {
		event.preventDefault();
		down();
	}

	if (event.which == 13) {
		event.preventDefault();
		showDetails();
	}
});

$("#table_div").focus(function() {
	if (index === -1) {
		index = 0;
	}
	updateTable();
});
*/