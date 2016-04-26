
package org.alfresco.module.org_alfresco_module_wcmquickstart.benchmark;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import org.alfresco.repo.exporter.ACPExportPackageHandler;
import org.alfresco.service.cmr.repository.MimetypeService;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.service.cmr.view.ExporterCrawlerParameters;
import org.alfresco.service.cmr.view.ExporterService;
import org.alfresco.service.cmr.view.Location;

/**
 * @author Nick Smith
 * @since 4.0
 *
 */
public class AcpGenerator
{
    private static final String ACP_EXT = ".acp";
    
    private ExporterService exporterService;
    private NodeService nodeService;
    private MimetypeService mimetypeService;
    
    public File generateACP(NodeRef root, String destination) throws Exception
    {
        ExporterCrawlerParameters params = new ExporterCrawlerParameters();
        params.setCrawlSelf(true);
        params.setCrawlChildNodes(true);
        params.setExportFrom(new Location(root));

        File outputFile = getOutputFile(destination);

        int lastDotIndex = destination.lastIndexOf('.');
        if(lastDotIndex != - 1)
        {
            destination = destination.substring(0, lastDotIndex);
        }
        File dataFile = new File(destination + "Data.xml");
        File contentDir = new File(destination + File.separator + "Content");

        FileOutputStream outputStream = new FileOutputStream(outputFile);
        ACPExportPackageHandler acpHandler = new ACPExportPackageHandler(outputStream, dataFile, contentDir, mimetypeService);
        acpHandler.setNodeService(nodeService);
        acpHandler.setExportAsFolders(false);
        exporterService.exportView(acpHandler, params, null);
        return outputFile;
    }

    /**
     * @param destination String
     * @return File
     * @throws IOException
     */
    private File getOutputFile(String destination) throws IOException
    {
        if(destination.endsWith(ACP_EXT)==false)
        {
            destination = destination + ACP_EXT;
        }
        File outputFile = new File(destination);

        //Create output file if it does not exist
        if(outputFile.exists()==false)
        {
            outputFile.createNewFile();
        }
        return outputFile;
    }
    
    /**
     * @param exporterService the exporterService to set
     */
    public void setExporterService(ExporterService exporterService)
    {
        this.exporterService = exporterService;
    }
    
    /**
     * @param nodeService the nodeService to set
     */
    public void setNodeService(NodeService nodeService)
    {
        this.nodeService = nodeService;
    }
    
    /**
     * @param mimetypeService the mimetypeService to set
     */
    public void setMimetypeService(MimetypeService mimetypeService)
    {
        this.mimetypeService = mimetypeService;
    }
}
