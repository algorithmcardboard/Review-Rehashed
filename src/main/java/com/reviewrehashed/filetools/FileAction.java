package com.reviewrehashed.filetools;

import java.nio.file.Path;

public interface FileAction {
  public void doWithMatchingFiles(Path path);
}
