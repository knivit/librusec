var requestFileSystem = obj.webkitRequestFileSystem || obj.mozRequestFileSystem || obj.requestFileSystem;
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

var model = (function() {
  var URL = obj.webkitURL || obj.mozURL || obj.URL;

  return {
    getEntries : function(file, onend) {
      zip.createReader(new zip.BlobReader(file), function(zipReader) {
	    zipReader.getEntries(onend);
	  }, onerror);
	},

	getEntryFile : function(entry, creationMethod, onend, onprogress) {
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
	}
  };
})();

function download(zipFileName, fileName, a) {
  model.getEntryFile(zipFileName, creationMethodInput.value, function(blobURL) {
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

function dwl(zipFileName, fileName) {
  if (!this.download) {
    download(entry, li, this);
    event.preventDefault();
    return false;
}
