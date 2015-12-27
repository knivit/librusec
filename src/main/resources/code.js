var obj = this;
var requestFileSystem = obj.webkitRequestFileSystem || obj.mozRequestFileSystem || obj.requestFileSystem;
var URL = obj.webkitURL || obj.mozURL || obj.URL;
var unzipProgress = document.createElement("progress");

function onerror(message) {
    alert(message);
};

function createTempFile(callback) {
    var tmpFilename = "tmp.dat";

    requestFileSystem(TEMPORARY, 4 * 1024 * 1024 * 1024, function(filesystem) {
        function create() {
            filesystem.root.getFile(tmpFilename, {
                create : true
	        }, function(zipFile) {
	            callback(zipFile);
	        });
        }

        filesystem.root.getFile(tmpFilename, null, function(entry) {
 	        entry.remove(create, create);
        }, create);
    });
};

function getEntryFile(entry, creationMethod, onend, onprogress) {
    var writer, zipFileEntry;

	function getData() {
	    entry.getData(writer, function(blob) {
	        var blobURL = creationMethod == "Blob" ? URL.createObjectURL(blob) : zipFileEntry.toURL();
		    onend(blobURL);
		}, onprogress);
    }

	if (creationMethod == "Blob") {
	    writer = new zip.BlobWriter();
		getData();
    } else {
	    createTempFile(function(fileEntry) {
	        zipFileEntry = fileEntry;
		    writer = new zip.FileWriter(zipFileEntry);
		    getData();
		});
    }
};

function getEntries(zipFileName, fileName, onend, onprogress) {
    zip.createReader(new zip.BlobReader(zipFileName), function(zipReader) {
        zipReader.getEntries(function(entries) {
            if (entries) getEntryFile(entries[0], "File", onend, onprogress);
            else alert("File '" + zipFileName + "#" + fileName + "' not found");
        }, fileName);
     }, onerror);
};

function download(zipFileName, fileName, a) {
    getEntries(zipFileName, fileName, function(blobURL) {
        var clickEvent = document.createEvent("MouseEvent");
	    if (unzipProgress.parentNode)
		    unzipProgress.parentNode.removeChild(unzipProgress);

	    unzipProgress.value = 0;
	    unzipProgress.max = 0;
	    clickEvent.initMouseEvent("click", true, true, window, 0, 0, 0, 0, 0, false, false, false, false, 0, null);
	    a.href = blobURL;
	    a.download = filename;
	    a.dispatchEvent(clickEvent);
    }, function(current, total) {
	    unzipProgress.value = current;
	    unzipProgress.max = total;
	    a.appendChild(unzipProgress);
    });
};

function dwl(elmnt, zipFileName, fileName) {
    if (!elmnt.download) {
        download("../" + zipFileName, fileName, elmnt);
        event.preventDefault();
        return false;
    }
};
