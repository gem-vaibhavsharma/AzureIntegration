package demo.service;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Service;

import demo.generalFunctions.CommonFunctions;
import demo.model.AzurePatch;
import demo.model.AzurePost;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;

import org.springframework.http.ResponseEntity;

public class AzureService {
    public static void main(String[] args) {
        // SpringApplication.run(DemoApplication.class, args);
        // AzureService azureService = new AzureService();
        // azureService.getWorkItem("Azure API", 1758L,
        // "aofm6ujy5vny5rrc72tvp5nrapovstrbzqiajqarf7ognqqkfftq");
    }

    public static ResponseEntity<?> getWorkItem(String project, Long id,
            final String PAT) {
        CommonFunctions commonFunctions = new CommonFunctions();
        String url = "https://dev.azure.com/GEM-QualityEngineering/" + project + "/_apis/wit/workitems/" + id
                + "?api-version=7.0";
        if (PAT == null) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(null);
        }

        String userpass = "" + ":" + PAT;

        String basicAuth = new String(Base64.getEncoder().encode(userpass.getBytes()));
        HttpHeaders headers = new HttpHeaders();
        headers.setBasicAuth(basicAuth);
        // headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity entity = new HttpEntity(null, headers);

        ResponseEntity<Object> res = commonFunctions.callGetAPI(url, entity);

        if (res.getStatusCodeValue() != 200) {
            return ResponseEntity.status(res.getStatusCode()).body(null);
        }
        // Map<String, String> res = (Map<String, String>) response;

        return ResponseEntity.status(HttpStatus.OK).body(res.getBody());

    }

    public static ResponseEntity<?> postWorkItem(AzurePost azurePostBody, String project, String type,
            final String PAT) throws IOException {

        CommonFunctions commonFunctions = new CommonFunctions();

        if (azurePostBody.getAssignedTo() == null || azurePostBody.getDescription() == null
                || azurePostBody.getTitle() == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }
        if (PAT == null) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(null);
        }

        String userpass = "" + ":" + PAT;
        String url = "https://dev.azure.com/GEM-QualityEngineering/" + project +
                "/_apis/wit/workitems/$" + type
                + "?api-version=7.0";
        String parentUrl = null;
        if (azurePostBody.getParent() != null) {

            Map<Object, Object> body = (Map<Object, Object>) getWorkItem(project,
                    azurePostBody.getParent(),
                    PAT).getBody();

            if (body != null) {

                Map<Object, Object> link = (Map<Object, Object>) ((Map<Object, Object>) body.get("_links")).get("html");
                // Object html1 = html.get("html");

                if (link != null) {
                    parentUrl = (String) link.get("href");
                }
            }

        }

        Object body = commonFunctions.createPostBody(azurePostBody, parentUrl);
        if (body == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }

        String basicAuth = new String(Base64.getEncoder().encode(userpass.getBytes()));
        HttpHeaders headers = new HttpHeaders();
        headers.setBasicAuth(basicAuth);
        headers.add("Content-Type", "application/json-patch+json");
        HttpEntity entity = new HttpEntity(body, headers);
        ResponseEntity<Object> response = commonFunctions.callPostAPI(url, entity);
        System.out.println(response.getStatusCodeValue());
        if (response.getStatusCodeValue() != 200) {
            return ResponseEntity.status(response.getStatusCode()).body(null);
        }
        return ResponseEntity.status(HttpStatus.CREATED).body(response.getBody());
    }

    public static ResponseEntity<?> patchWorkItem(AzurePatch azurePatchBody, String project, Long id,
            final String PAT) throws IOException {
        CommonFunctions commonFunctions = new CommonFunctions();

        if (azurePatchBody == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }
        if (PAT == null) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(null);
        }

        String userpass = "" + ":" + PAT;
        String url = "https://dev.azure.com/GEM-QualityEngineering/" + project +
                "/_apis/wit/workitems/" + id
                + "?api-version=7.0";
        String parentUrl = null;
        Map<Object, Object> body = (Map<Object, Object>) getWorkItem(project, id,
                PAT).getBody();
        int rev = 0;
        if (body != null) {
            rev = (int) body.get("rev");
        }

        if (azurePatchBody.getParent() != null && body != null) {

            Map<Object, Object> link = (Map<Object, Object>) ((Map<Object, Object>) body.get("_links")).get("html");
            // Object html1 = html.get("html");

            if (link != null) {
                parentUrl = (String) link.get("href");
            }

        }

        Object patchBody = commonFunctions.createPatchBody(azurePatchBody, parentUrl,
                rev);
        if (patchBody == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }

        String basicAuth = new String(Base64.getEncoder().encode(userpass.getBytes()));
        HttpHeaders headers = new HttpHeaders();
        headers.setBasicAuth(basicAuth);
        headers.add("Content-Type", "application/json-patch+json");
        HttpEntity entity = new HttpEntity(patchBody, headers);
        ResponseEntity<Object> response = commonFunctions.callPatchAPI(url, entity);
        if (response.getStatusCodeValue() != 200) {
            return ResponseEntity.status(response.getStatusCode()).body(null);
        }
        return ResponseEntity.status(HttpStatus.OK).body(response.getBody());
    }

}
