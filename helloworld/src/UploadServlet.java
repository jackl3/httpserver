import javax.servlet.*;  
import javax.servlet.http.*;  
import java.io.*;  
import java.util.*;  
import org.apache.commons.fileupload.*;  
import org.apache.commons.fileupload.servlet.*;  
import org.apache.commons.fileupload.disk.*;  
 
public class UploadServlet extends HttpServlet  
{  
    private String filePath;    
    private String tempPath; 
 
    public void init(ServletConfig config) throws ServletException  
    {  
        super.init(config);  
        filePath = config.getInitParameter("filepath");  
        tempPath = config.getInitParameter("temppath");  
 
        ServletContext context = getServletContext();  
 
        filePath = context.getRealPath(filePath);  
        tempPath = context.getRealPath(tempPath);  
        System.out.println("file path, temp path ok");  
    }  
      
    // doPost  
    public void doPost(HttpServletRequest req, HttpServletResponse res)  
        throws IOException, ServletException  
    {  
       res.setContentType("text/plain;charset=gbk");
	Enumeration headerNames = req.getHeaderNames();
        while (headerNames.hasMoreElements()) {
        String key = (String) headerNames.nextElement();
        String value = req.getHeader(key);
	System.out.println("the key is "+key+" the value is "+value +"\n");
	};


        PrintWriter pw = res.getWriter();  
        try{  
            DiskFileItemFactory diskFactory = new DiskFileItemFactory();  
            diskFactory.setSizeThreshold(1024 * 1024);  
            diskFactory.setRepository(new File(tempPath));  
            
            ServletFileUpload upload = new ServletFileUpload(diskFactory);  
            upload.setSizeMax(1024 * 1024 * 1024);  
            List fileItems = upload.parseRequest(req);  
		System.out.println("the request is "+ fileItems);
            Iterator iter = fileItems.iterator();  
            while(iter.hasNext())  
            {  
                FileItem item = (FileItem)iter.next();  
                if(item.isFormField())  
                {  
                    System.out.println("handle form content ...");  
                    processFormField(item, pw);  
                }else{  
                    System.out.println("handle uploading file ...");  
                    processUploadFile(item, pw);  
                }  
            }// end while()  
 
            pw.close();  
        }catch(Exception e){  
            System.out.println("got exception while using fileupload ...");  
            e.printStackTrace();  
        }// end try ... catch ...  
    }// end doPost()  
 
 
    private void processFormField(FileItem item, PrintWriter pw)  
        throws Exception  
    {  
        String name = item.getFieldName();  
        String value = item.getString();          
        pw.println(name + " : " + value + "\r\n");  
    }  
      
    private void processUploadFile(FileItem item, PrintWriter pw)  
        throws Exception  
    {  
        String filename = item.getName();         
        System.out.println("full file name" + filename);  
        int index = filename.lastIndexOf("\\");  
        filename = filename.substring(index + 1, filename.length());  
 
        long fileSize = item.getSize();  
 
        if("".equals(filename) && fileSize == 0)  
        {             
            System.out.println("file name is empty ...");  
            return;  
        }  
 
        File uploadFile = new File(filePath + "/" + filename);  
        item.write(uploadFile);  
        pw.println(filename + " file saved ...");  
        pw.println("file size is:" + fileSize + "\r\n");  
    }  
      
    // doGet  
    public void doGet(HttpServletRequest req, HttpServletResponse res)  
        throws IOException, ServletException  
    {  
        doPost(req, res);  
    }  

    // doPut 
    public void doPut(HttpServletRequest req, HttpServletResponse res)  
        throws IOException, ServletException  
    {  
        doPost(req, res);  
    }  
} 
