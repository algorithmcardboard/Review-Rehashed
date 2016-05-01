package com.reviewrehashed.indexer;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Date;

import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.slf4j.Logger;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.Parameter;
import com.reviewrehashed.filetools.FileAction;
import com.reviewrehashed.filetools.FileWalker;

import uk.org.lidalia.slf4jext.LoggerFactory;

public class Indexer {

  private static final Logger logger = LoggerFactory.getLogger(Indexer.class);

  private HTMLParser parser = new HTMLParser();

  @Parameter(names = { "-index", "-i" }, required = true)
  private String indexDirPath;
  @Parameter(names = { "-docs", "-d" }, required = true)
  private String dataDirPath;

  public static void main(String[] args) throws Exception {
    Indexer indexer = new Indexer();
    new JCommander(indexer, args);
    indexer.start();
  }

  public void start() throws IOException {
    File indexDir = new File(indexDirPath);
    File dataDir = new File(dataDirPath);
    long start = new Date().getTime();
    int numIndexed = index(indexDir, dataDir);
    long end = new Date().getTime();
    System.out.println("Indexing " + numIndexed + " files took " + (end - start) + " milliseconds");
  }

  // open an index and start file directory traversal
  public int index(File indexDir, File dataDir) throws IOException {
    if (!dataDir.exists() || !dataDir.isDirectory()) {
      throw new IOException(dataDir + " does not exist or is not a directory");
    }
    Path path = indexDir.toPath();
    Directory indexDirectory = FSDirectory.open(path);

    IndexWriterConfig c = new IndexWriterConfig(new StandardAnalyzer());
    final IndexWriter writer = new IndexWriter(indexDirectory, c);

    FileWalker walker = new FileWalker("*.{html}", new FileAction() {

      public void doWithMatchingFiles(Path path) {
        try {
          indexFile(writer, path.toFile());
        } catch (IOException e) {
          logger.error("Exception while indexing ", e);
        }
      }
    });

    try {
      Files.walkFileTree(Paths.get(dataDir.getAbsolutePath()), walker);
    } catch (IOException e) {
      logger.error("File Walking Exception: ", e);
    }
    int numIndexed = writer.maxDoc();
    writer.close();
    return numIndexed;
  }

  // method to actually index a file using Lucene
  private void indexFile(IndexWriter writer, File f) throws IOException {
    if (f.isHidden() || !f.exists() || !f.canRead()) {
      return;
    }
    System.out.println("Indexing " + f.getCanonicalPath());
    for (Document document : parser.getDocument(f)) {
      writer.addDocument(document);
    }
  }
}
