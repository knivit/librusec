function dwl(elmnt, zipFileName, fileName) {
    if (!elmnt.download) {
	    clickEvent.initMouseEvent("click", true, true, window, 0, 0, 0, 0, 0, false, false, false, false, 0, null);
	    a.href = "/books/" + zipFileName + "/" + filename;
	    a.dispatchEvent(clickEvent);

        event.preventDefault();
        return false;
    }
};
