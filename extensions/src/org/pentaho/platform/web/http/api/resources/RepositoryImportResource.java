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
 *
 * @created 1/14/2011
 * @author Aaron Phillips
 * 
 */
package org.pentaho.platform.web.http.api.resources;

import com.sun.jersey.core.header.FormDataContentDisposition;
import com.sun.jersey.multipart.FormDataParam;
import org.pentaho.platform.api.repository2.unified.IUnifiedRepository;
import org.pentaho.platform.engine.core.system.PentahoSystem;
import org.pentaho.platform.repository2.unified.importexport.Converter;
import org.pentaho.platform.repository2.unified.importexport.DefaultImportHandler;
import org.pentaho.platform.repository2.unified.importexport.ImportException;
import org.pentaho.platform.repository2.unified.importexport.ImportProcessor;
import org.pentaho.platform.repository2.unified.importexport.InitializationException;
import org.pentaho.platform.repository2.unified.importexport.MetadataImportHandler;
import org.pentaho.platform.repository2.unified.importexport.MondrianImportHandler;
import org.pentaho.platform.repository2.unified.importexport.SimpleImportProcessor;
import org.pentaho.platform.repository2.unified.importexport.StreamConverter;
import org.pentaho.platform.repository2.unified.importexport.legacy.FileSolutionRepositoryImportSource;
import org.pentaho.platform.repository2.unified.importexport.legacy.ZipSolutionRepositoryImportSource;
import org.pentaho.platform.web.http.messages.Messages;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.zip.ZipInputStream;

@Path("/repo/files/import")
public class RepositoryImportResource {

  private IUnifiedRepository repository;

  public RepositoryImportResource() {
    repository = PentahoSystem.get(IUnifiedRepository.class);
  }

  /**
   * @param uploadDir: JCR Directory to which the zip structure or single file will be uploaded to.
   * @param fileIS:    Input stream for the file.
   * @param fileInfo:  Info about he file (
   * @return http ok response of everything went well... some other error otherwise
   *         <p/>
   *         This REST method takes multi-part form data and imports it to a JCR repository.
   */
  @POST
  @Consumes(MediaType.MULTIPART_FORM_DATA)
  @Produces(MediaType.TEXT_HTML)
  public Response doPostImport(@FormDataParam("importDir") String uploadDir,
                               @FormDataParam("fileUpload") InputStream fileIS,
                               @FormDataParam("fileUpload") FormDataContentDisposition fileInfo) {
    Map<String, Converter> converters = new HashMap<String, Converter>();
    StreamConverter streamConverter = new StreamConverter();
    converters.put("prpt", streamConverter); //$NON-NLS-1$
    converters.put("mondrian.xml", streamConverter); //$NON-NLS-1$
    converters.put("kjb", streamConverter); //$NON-NLS-1$
    converters.put("ktr", streamConverter); //$NON-NLS-1$
    converters.put("report", streamConverter); //$NON-NLS-1$
    converters.put("rptdesign", streamConverter); //$NON-NLS-1$
    converters.put("svg", streamConverter); //$NON-NLS-1$
    converters.put("url", streamConverter); //$NON-NLS-1$
    converters.put("xaction", streamConverter); //$NON-NLS-1$
    converters.put("xanalyzer", streamConverter); //$NON-NLS-1$
    converters.put("xcdf", streamConverter); //$NON-NLS-1$
    converters.put("xdash", streamConverter); //$NON-NLS-1$
    converters.put("xreportspec", streamConverter); //$NON-NLS-1$
    converters.put("waqr.xaction", streamConverter); //$NON-NLS-1$
    converters.put("xwaqr", streamConverter);//$NON-NLS-1$
    converters.put("gif", streamConverter); //$NON-NLS-1$
    converters.put("css", streamConverter); //$NON-NLS-1$
    converters.put("html", streamConverter); //$NON-NLS-1$
    converters.put("htm", streamConverter); //$NON-NLS-1$
    converters.put("jpg", streamConverter); //$NON-NLS-1$
    converters.put("jpeg", streamConverter); //$NON-NLS-1$
    converters.put("js", streamConverter); //$NON-NLS-1$
    converters.put("cfg.xml", streamConverter); //$NON-NLS-1$
    converters.put("jrxml", streamConverter); //$NON-NLS-1$
    converters.put("png", streamConverter); //$NON-NLS-1$
    converters.put("properties", streamConverter); //$NON-NLS-1$
    converters.put("sql", streamConverter); //$NON-NLS-1$
    converters.put("xmi", streamConverter); //$NON-NLS-1$
    converters.put("xml", streamConverter); //$NON-NLS-1$
    converters.put("cda", streamConverter); //$NON-NLS-1$

    try {
      final ImportProcessor importProcessor = new SimpleImportProcessor(uploadDir, null);
      // TODO - create a SolutionRepositoryImportHandler which delegates to these three automatically
      importProcessor.addImportHandler(new MondrianImportHandler(repository));
      importProcessor.addImportHandler(new MetadataImportHandler(repository));
      importProcessor.addImportHandler(new DefaultImportHandler(repository));
      if (fileInfo.getFileName().toLowerCase().endsWith(".zip")) {
        importProcessor.setImportSource(new ZipSolutionRepositoryImportSource(new ZipInputStream(fileIS), "UTF-8"));
      } else {
        final File outFile = File.createTempFile("import", null);
        outFile.deleteOnExit();
        importProcessor.setImportSource(new FileSolutionRepositoryImportSource(outFile, fileInfo.getFileName(), "UTF-8"));
      }
      importProcessor.performImport();
    } catch (ImportException e) {
      return Response.serverError().entity(e.toString()).build();
    } catch (InitializationException e) {
      return Response.serverError().entity(e.toString()).build();
    } catch (IOException e) {
      return Response.serverError().entity(e.toString()).build();
    }

    return Response.ok(Messages.getInstance().getString("FileResource.IMPORT_SUCCESS")).build();
  }
}
