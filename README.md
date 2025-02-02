1. bookFolder - the root folder for all the book files (*.zip and *.fb2 with subdirectories)
2. systemFolder - this folder keeps
   - files.csv: list all full-name files within the book folder
     fileIndex;fileName
     1;mystery books/files.zip          // zip-archive with fb2 files
     2;fiction books/fiction1.fb2       // fb2-file
     ...
   - books.csv: list all files
     bookIndex;fileIndex;fileName
     1;1;file1.fb                       // fb2 file in a zip-archive
     2;1;file2.fb                       // another fb2-file in a zip-archive
     3;2;fiction1.fb2                   // regular f2-file
     ...
   - authors.csv: list of authors
     authorIndex;name
     1;Twain Mark
     ...
   - index.csv: books info
     bookIndex;authors;year;lang;genre;title;desc
     1;1,2,3;1880;en;fiction;A short tale;Small child's adventures
     ...
     
3. cacheFolder