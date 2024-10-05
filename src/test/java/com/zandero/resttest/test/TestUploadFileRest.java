package com.zandero.resttest.test;

import com.zandero.rest.utils.StringUtils;
import io.vertx.ext.web.*;

import jakarta.ws.rs.*;
import jakarta.ws.rs.core.*;
import java.util.*;

@Path("/upload")
public class TestUploadFileRest {

    @POST
    @Path("/file")
    public String upload(@Context RoutingContext context) {

        List<FileUpload> fileUploadSet = context.fileUploads();
        if (fileUploadSet == null || fileUploadSet.isEmpty()) {
            return "missing upload file!";
        }

        Iterator<FileUpload> fileUploadIterator = fileUploadSet.iterator();
        List<String> urlList = new ArrayList<>();
        while (fileUploadIterator.hasNext()) {
            FileUpload fileUpload = fileUploadIterator.next();
            urlList.add(fileUpload.uploadedFileName());
        }

        return StringUtils.join(urlList, ", ");
    }
}