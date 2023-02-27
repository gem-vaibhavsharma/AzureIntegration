package demo.generalFunctions;

import ch.qos.logback.core.net.SyslogOutputStream;
import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.HttpClientBuilder;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;

import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import demo.model.AzurePatch;
import demo.model.AzurePost;
import org.springframework.http.HttpEntity;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;

import org.springframework.http.ResponseEntity;

import java.util.ArrayList;
import java.util.Base64;
import java.util.Map;

public class CommonFunctions {

    public static ResponseEntity<Object> callGetAPI(String url, HttpEntity<?> entity) {
        RestTemplate rest = new RestTemplate();
        return rest.exchange(url, HttpMethod.GET, entity, Object.class);

    }

    public static ResponseEntity<Object> callPostAPI(String url, HttpEntity<?> entity) {
        RestTemplate rest = new RestTemplate();
        return rest.exchange(url, HttpMethod.POST, entity, Object.class);

    }

    public static ResponseEntity<Object> callPatchAPI(String url, HttpEntity<?> entity) {
        RestTemplate restTemplate = new RestTemplate();
        HttpClient httpClient = HttpClientBuilder.create().build();
        restTemplate.setRequestFactory(new HttpComponentsClientHttpRequestFactory(httpClient));
        return restTemplate.exchange(url, HttpMethod.PATCH, entity, Object.class);

    }
    public static int titleQuery(String title, String project,String PAT){
        Object query="{\n" +
                "    \"query\": \"Select [System.Title] From WorkItems Where [System.Title] Contains '"+title+"'And [System.TeamProject] = '"+project+"'\"\n" +
                "}\n";
        String url="https://dev.azure.com/GEM-QualityEngineering/"+project+"/_apis/wit/wiql?api-version=6.0\n";
        String userpass = "" + ":" + PAT;
        String basicAuth = new String(Base64.getEncoder().encode(userpass.getBytes()));
        HttpHeaders headers = new HttpHeaders();

        headers.setBasicAuth(basicAuth);
        headers.add("Content-Type", "application/json");
        HttpEntity entity = new HttpEntity(query, headers);
        ResponseEntity<Object> response = callPostAPI(url, entity);
        Map<Object,Object> body= (Map<Object, Object>) response.getBody();

        ArrayList<Object> workItem= (ArrayList<Object>) body.get("workItems");

        int id=0;
        if (workItem.size()==0){

            return id;
        }
        Map<Object,Object> fields= (Map<Object, Object>) workItem.get(0);
        if(fields==null){

            return id;
        }

        if(fields.containsKey("id")){
            id= (int) fields.get("id");
        }
       if(id!=0){

           return id;
       }


        return id;
    }

    public static String createPostBody(AzurePost azurePostBody, String parentUrl) {
        if (azurePostBody.getAssignedTo() == null || azurePostBody.getDescription() == null
                || azurePostBody.getTitle() == null) {
            return null;
        }
        String body = "[{\"op\": \"add\",\"path\": \"/fields/System.AssignedTo\",\"value\":\""
                + azurePostBody.getAssignedTo()
                + "\" },{\"op\":\"add\",\"path\": \"/fields/System.Title\",\"value\":\""
                + azurePostBody.getTitle()
                + "\"},{\"op\": \"add\",\"path\": \"/fields/System.Description\",\"value\": \""
                + azurePostBody.getDescription() + "\"}";

        if (parentUrl != null) {

            body = body
                    + ",{\r\n    \"op\": \"add\",\r\n    \"path\": \"/relations/-\",\r\n    \"value\": {\r\n      \"rel\": \"System.LinkTypes.Hierarchy-Reverse\",\r\n      \"url\": \""
                    + parentUrl + "\"\r\n    }\r\n    }";
        }
        body = body + "\r\n\r\n]";
        // System.out.println(body);
        return body;
    }

    public static String createPatchBody(AzurePatch azurePatchBody, String parentUrl, int rev) {
        if (azurePatchBody == null) {
            return null;
        }

        String body = "[\r\n    {\r\n        \"op\": \"test\",\r\n        \"path\": \"/rev\",\r\n        \"value\":"
                + rev + "\r\n    }";
        if (azurePatchBody.getTitle() != null) {
            body = body
                    + ",\r\n    {\r\n        \"op\": \"add\",\r\n        \"path\": \"/fields/System.Title\",\r\n        \"value\": \""
                    + azurePatchBody.getTitle() + "\"\r\n    }";
        }
        if (azurePatchBody.getAssignedTo() != null) {
            body = body
                    + ",\r\n    {\r\n        \"op\": \"add\",\r\n        \"path\": \"/fields/System.AssignedTo\",\r\n        \"value\": \""
                    + azurePatchBody.getAssignedTo() + "\"\r\n    }";
        }
        if (azurePatchBody.getDescription() != null) {
            body = body
                    + ",\r\n    {\r\n        \"op\": \"add\",\r\n        \"path\": \"/fields/System.Description\",\r\n        \"value\": \""
                    + azurePatchBody.getDescription() + "\"\r\n    }";
        }
        if (azurePatchBody.getState() != null) {
            body = body
                    + ",\r\n    {\r\n        \"op\": \"add\",\r\n        \"path\": \"/fields/System.State\",\r\n        \"value\": \""
                    + azurePatchBody.getState() + "\"\r\n    }";
        }
        if (azurePatchBody.getType() != null) {
            body = body
                    + ",\r\n    {\r\n        \"op\": \"add\",\r\n        \"path\": \"/fields/System.WorkItemType\",\r\n        \"value\": \""
                    + azurePatchBody.getType() + "\"\r\n    }";
        }

        if (parentUrl != null) {

            body = body
                    + ",{\r\n    \"op\": \"add\",\r\n    \"path\": \"/relations/-\",\r\n    \"value\": {\r\n      \"rel\": \"System.LinkTypes.Hierarchy-Reverse\",\r\n      \"url\": \""
                    + parentUrl + "\"\r\n    }\r\n    }";
        }
        body = body + "\r\n\r\n]";
        // System.out.println(body);
        return body;
    }

}
