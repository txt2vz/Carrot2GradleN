package index

import java.nio.file.Path
import java.nio.file.Paths

import org.apache.lucene.analysis.Analyzer
import org.apache.lucene.analysis.standard.StandardAnalyzer
import org.apache.lucene.document.Document
import org.apache.lucene.document.Field
import org.apache.lucene.document.StringField
import org.apache.lucene.document.TextField
import org.apache.lucene.index.IndexWriter
import org.apache.lucene.index.IndexWriterConfig
import org.apache.lucene.index.IndexWriterConfig.OpenMode

//import org.apache.lucene.queryparser.classic.QueryParser
import org.apache.lucene.store.Directory
import org.apache.lucene.store.FSDirectory

class IndexDataset {
    // Create Lucene index in this directory
    def indexPath =

      //      'indexes/20NG5WindowsmiscForsaleHockeySpaceChristian'
       //       'indexes/20NG6GraphicsHockeyCryptSpaceChristianGuns'
    //'indexes/R5'
    'indexes/R4'

    // Index files in this directory
    def docsPath =

//          /C:\Users\aceslh\Dataset\GAclusterPaper2018\20NG6GraphicsHockeyCryptSpaceChristianGuns/
//            /C:\Users\aceslh\Dataset\GAclusterPaper2018\20NG5WindowsmiscForsaleHockeySpaceChristian/
//    /C:\Users\aceslh\Dataset\GAclusterPaper2018\r5/
  /C:\Users\aceslh\Dataset\GAclusterPaper2018\R4/


    Path path = Paths.get(indexPath)
    Directory directory = FSDirectory.open(path)
    Analyzer analyzer = //new EnglishAnalyzer();
            new StandardAnalyzer();
    def catsFreq = [:]

    static main(args) {
        def i = new IndexDataset()
        i.buildIndex()
    }

    def buildIndex() {
        IndexWriterConfig iwc = new IndexWriterConfig(analyzer);

        // Create a new index in the directory, removing any
        // previously indexed documents:
        iwc.setOpenMode(OpenMode.CREATE);
        IndexWriter writer = new IndexWriter(directory, iwc);

        Date start = new Date();
        println("Indexing to directory '" + indexPath + "'...");
        def catNumber = 0;
        new File(docsPath).eachDir {
            it.eachFileRecurse {
                if (!it.hidden && it.exists() && it.canRead() && !it.directory) {
                    indexDocs(writer, it, catNumber)
                }
            }
            catNumber++;
        }

        println "catsFreq $catsFreq"
        Date end = new Date();
        println(end.getTime() - start.getTime() + " total milliseconds");
        println "Total docs: " + writer.maxDoc()
        writer.close()
        println "End ***************************************************************"
    }

    //index the doc adding fields for path, category, test/train and contents
    def indexDocs(IndexWriter writer, File file, def categoryNumber) throws IOException {

        Document doc = new Document()
        Field pathField = new StringField("path", file.getPath(), Field.Store.YES);
        doc.add(pathField);
        String parent = file.getParent()
        String catName = parent.substring(parent.lastIndexOf(File.separator) + 1, parent.length())
        Field catNameField = new StringField("category", catName, Field.Store.YES);
        doc.add(catNameField)

        def n = catsFreq.get((catName)) ?: 0
        catsFreq.put((catName), n + 1)

        doc.add(new TextField("contents", file.text, Field.Store.YES))
        writer.addDocument(doc)
    }
}
