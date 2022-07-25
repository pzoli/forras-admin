currentDialogWidget = "";

function handleComplete(xhr, status, args) {
	if (status == "success") {
		try {
			if (args.validationFailed) {
				return false;
			} else {
				PF(currentDialogWidget).hide();
				return true;
			}
		} catch (e) {
			
		}
	}
}
