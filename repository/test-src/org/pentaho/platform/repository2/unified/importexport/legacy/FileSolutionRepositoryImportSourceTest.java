/*
 * This program is free software; you can redistribute it and/or modify it under the 
 * terms of the GNU Lesser General Public License, version 2.1 as published by the Free Software 
 * Foundation.
 *
 * You should have received a copy of the GNU Lesser General Public License along with this 
 * program; if not, you can obtain a copy at http://www.gnu.org/licenses/old-licenses/lgpl-2.1.html 
 * or from the Free Software Foundation, Inc., 
 * 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; 
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Lesser General Public License for more details.
 *
 * Copyright 2011 Pentaho Corporation.  All rights reserved.
 *  
 * @author dkincade
 */
package org.pentaho.platform.repository2.unified.importexport.legacy;

import junit.framework.TestCase;
import org.pentaho.platform.repository2.unified.importexport.ImportSource;

import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

/**
 * Class Description
 *
 * @author <a href="mailto:dkincade@pentaho.com">David M. Kincade</a>
 */
public class FileSolutionRepositoryImportSourceTest extends TestCase {
  public void testInitialize() throws Exception {
    {
      final File tempFile = File.createTempFile("junit", "tmp");
      final FileSolutionRepositoryImportSource importSource
          = new FileSolutionRepositoryImportSource(tempFile, "sample.xaction", "UTF-8");
      assertEquals(1, importSource.getCount());
      final Iterable<ImportSource.IRepositoryFileBundle> files = importSource.getFiles();
      assertNotNull(files);
      final ImportSource.IRepositoryFileBundle bundle = files.iterator().next();
      assertNotNull(bundle);
      assertEquals("", bundle.getPath());
      assertNotNull(bundle.getFile());
      assertEquals("sample.xaction", bundle.getFile().getName());
      assertFalse(bundle.getFile().isFolder());
    }

    {
      final File sourceFile = new File("./test-src/org/pentaho/platform/repository2/unified/importexport/testdata");
      assertTrue("Make sure your current directory is the repository project", sourceFile.exists());
      FileSolutionRepositoryImportSource importSource
          = new FileSolutionRepositoryImportSource(sourceFile, "UTF-8");
      assertEquals(11, importSource.getCount());
    }

  }

  public void testGetFiles() throws Exception {
    {
      final File invalidFile = createTempFile("tmp");
      assertTrue(invalidFile.delete());
      final FileSolutionRepositoryImportSource importSource = new FileSolutionRepositoryImportSource(invalidFile, "UTF-8");
      assertEquals(0, importSource.getCount());
      final Iterable<ImportSource.IRepositoryFileBundle> files = importSource.getFiles();
      assertNotNull(files);
      assertFalse(files.iterator().hasNext());
    }

    {
      final File validFile = createTempFile("xaction");
      final FileSolutionRepositoryImportSource importSource = new FileSolutionRepositoryImportSource(validFile, "UTF-8");
      assertEquals(1, importSource.getCount());
      assertNotNull(importSource.getFiles());
      final Iterator<ImportSource.IRepositoryFileBundle> iterator = importSource.getFiles().iterator();
      assertNotNull(iterator);
      final ImportSource.IRepositoryFileBundle file = iterator.next();
      assertNotNull(file);

      // Make sure remove works
      iterator.remove();
      assertEquals(0, importSource.getCount());

      assertFalse(iterator.hasNext());
    }

    {
      final Set<String> foldersFound = new HashSet<String>();
      final Set<String> filesFound = new HashSet<String>();

      final File sourceFile = new File("./test-src/org/pentaho/platform/repository2/unified/importexport/testdata");
      assertTrue("Make sure your current directory is the repository project", sourceFile.exists());
      FileSolutionRepositoryImportSource importSource = new FileSolutionRepositoryImportSource(sourceFile, "UTF-8");
      assertEquals(11, importSource.getCount());
      final Iterable<ImportSource.IRepositoryFileBundle> files = importSource.getFiles();
      assertNotNull(files);
      for (Iterator<ImportSource.IRepositoryFileBundle> it = files.iterator(); it.hasNext(); ) {
        final ImportSource.IRepositoryFileBundle bundle = it.next();
        assertNotNull(bundle);
        assertNotNull(bundle.getFile());
        if (bundle.getFile().isFolder()) {
          foldersFound.add(bundle.getFile().getName());
        } else {
          filesFound.add(bundle.getFile().getName());
        }
      }

      assertEquals(8, filesFound.size());
      assertTrue(filesFound.contains("Empty.zip"));
      assertTrue(filesFound.contains("Success.zip"));
      assertTrue(filesFound.contains("TestZipFile.zip"));
      assertTrue(filesFound.contains("pentaho-solutions.zip"));
      assertTrue(filesFound.contains("Example1.xaction"));
      assertTrue(filesFound.contains("Example2.xaction"));
      assertTrue(filesFound.contains("Example3.xaction"));
      assertTrue(filesFound.contains("HelloWorld.xaction"));

      assertEquals(3, foldersFound.size());
      assertTrue(foldersFound.contains("testdata"));
      assertTrue(foldersFound.contains("pentaho-solutions"));
      assertTrue(foldersFound.contains("getting-started"));

    }
  }

  public void testCreate() throws Exception {
    final File tempFile = File.createTempFile("junit", "tmp");
    try {
      new FileSolutionRepositoryImportSource(null, "UTF-8");
      fail();
    } catch (Exception success) {
    }

    try {
      new FileSolutionRepositoryImportSource(tempFile, null);
      fail();
    } catch (Exception success) {
    }

    try {
      new FileSolutionRepositoryImportSource(tempFile, "");
      fail();
    } catch (Exception success) {
    }

    try {
      new FileSolutionRepositoryImportSource(null, "filename.txt", "UTF-8");
      fail();
    } catch (Exception success) {
    }

    try {
      new FileSolutionRepositoryImportSource(tempFile, null, "UTF-8");
      fail();
    } catch (Exception success) {
    }

    try {
      new FileSolutionRepositoryImportSource(tempFile, "", "UTF-8");
      fail();
    } catch (Exception success) {
    }

    try {
      new FileSolutionRepositoryImportSource(tempFile, "filename.txt", null);
      fail();
    } catch (Exception success) {
    }

    try {
      new FileSolutionRepositoryImportSource(tempFile, "filename.txt", "");
      fail();
    } catch (Exception success) {
    }

    new FileSolutionRepositoryImportSource(tempFile, "UTF-8");
  }

  private static File createTempFile(final String extension) throws IOException {
    return File.createTempFile("FileSolutionRepositoryImportSourceTest-", extension == null ? "" : "." + extension);
  }

  private static File createTempDir() throws IOException {
    final File dir = File.createTempFile("FileSolutionRepositoryImportSourceTest-", "");
    assertTrue(dir.delete());
    assertTrue(dir.mkdir());
    return dir;
  }
}
