package demo.generalFunctions;

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
