function dwl(a, zipFileName, fileName) {
    if (!a.download) {
        var clickEvent = document.createEvent("MouseEvent");
	    clickEvent.initMouseEvent("click", true, true, window, 0, 0, 0, 0, 0, false, false, false, false, 0, null);

	    a.href = "/books/" + zipFileName + "/" + fileName;
	    a.download = fileName;
	    a.dispatchEvent(clickEvent);

        event.preventDefault();
        return false;
    }
};
