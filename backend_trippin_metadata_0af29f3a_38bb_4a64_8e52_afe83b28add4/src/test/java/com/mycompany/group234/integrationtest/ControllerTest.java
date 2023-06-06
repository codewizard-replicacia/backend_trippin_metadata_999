package com.mycompany.group234.integrationtest;
import static io.restassured.module.mockmvc.RestAssuredMockMvc.given;
import static org.hamcrest.CoreMatchers.*;
import static org.junit.jupiter.api.Assertions.*;

import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mycompany.group234.SpringApp;
import org.apache.olingo.commons.api.http.HttpStatusCode;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.web.context.WebApplicationContext;

import io.restassured.http.ContentType;
import io.restassured.module.mockmvc.RestAssuredMockMvc;
import io.restassured.path.xml.XmlPath;
import io.restassured.path.xml.element.Node;

@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = SpringApp.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestPropertySource(properties = { "spring.config.location=classpath:application-test.yml" })
class ControllerTest {

  @Autowired
  private JdbcTemplate jdbcTemplate;

  private final ObjectMapper mapper = new ObjectMapper();

  @Autowired
  private WebApplicationContext context;
  @LocalServerPort
  private int port;

  @BeforeEach
  void setup() {
    RestAssuredMockMvc.webAppContextSetup(context);
  }

  
  
   private JsonNode getJSONFromFile(String filePath) throws IOException {
    try(InputStream in=Thread.currentThread().getContextClassLoader().getResourceAsStream(filePath)){
      JsonNode jsonNode = mapper.readValue(in, JsonNode.class);
      return jsonNode;
    }
    catch(Exception e){
      throw new RuntimeException(e);
    }
  }
  
  private String getPayload(String filePath) throws IOException {
	  String jsonString = mapper.writeValueAsString( getJSONFromFile(filePath) );
	  return jsonString;
  }

  @Test
  void testRetrieveServiceDocument() {
    final String xml = given()
        .accept(ContentType.XML)
        .when()
        .get("/trippin_metadata/")
        .then()
        .statusCode(HttpStatusCode.OK.getStatusCode())
        .contentType(ContentType.XML)
        .extract()
        .asString();

    final XmlPath path = new XmlPath(xml);
    final Collection<Node> n = ((Node) ((Node) path.get("service")).get("workspace")).get("collection");
    assertNotNull(n);
    assertFalse(n.isEmpty());
  }

  @Test
  void  testRetrieveMetadataDocument() {
    final String xml = given()
        .when()
        .get("/trippin_metadata/$metadata")
        .then()
        .statusCode(HttpStatusCode.OK.getStatusCode())
        .contentType(ContentType.XML)
        .extract()
        .asString();

    final XmlPath path = new XmlPath(xml);
    final Node n = ((Node) ((Node) path.get("edmx:Ed mx")).get("DataServices")).get("Schema");
    assertNotNull(n);
    assertEquals("Model", n.getAttribute("Namespace"));
    assertNotNull(n.get("EntityContainer"));
  }

	

	
  @Test
  void  testCreateVet_Visit_HistoryInstance() throws IOException {
    given()
        .contentType("application/json")
        .body(getPayload("Vet_Visit_HistoryInstance.json"))
        .when()
        .post("/trippin_metadata/Vet_Visit_Historys")
        .then()
        .statusCode(HttpStatusCode.CREATED.getStatusCode());
    
  }
	
	
  
   
  
   @Test
  public void testSystemFilterOptionsVet_Visit_History() throws IOException {
  
   given()
            .when()
            .get("/trippin_metadata/Vet_Visit_Historys?$top=1")
            .then()
            .statusCode(HttpStatusCode.fromStatusCode(200).getStatusCode())
            .body("value.get(0).Vet_visit_history_id", equalTo("<<replace_with_keyFieldValue>>"))
            .body("value.size()", is(1));
    given()
            .when()
            .get("/TripPinRESTierService/Vet_Visit_Historys?$skip=1")
            .then()
            .statusCode(HttpStatusCode.fromStatusCode(200).getStatusCode())
            .body("value.get(0).Vet_visit_history_id", equalTo("<<replace_with_keyFieldValue>>"))
            .body("value.size()", is(1));
    given()
            .when()
            .get("/TripPinRESTierService/Vet_Visit_Historys/$count")
            .then()
            .statusCode(HttpStatusCode.fromStatusCode(200).getStatusCode())
            .body(is("2"));
            
            
    } 
	
	

	
  @Test
  void  testCreatePet_InfoInstance() throws IOException {
    given()
        .contentType("application/json")
        .body(getPayload("Pet_InfoInstance.json"))
        .when()
        .post("/trippin_metadata/Pet_Infos")
        .then()
        .statusCode(HttpStatusCode.CREATED.getStatusCode());
    
  }
	
	
  
   
  
   @Test
  public void testSystemFilterOptionsPet_Info() throws IOException {
  
   given()
            .when()
            .get("/trippin_metadata/Pet_Infos?$top=1")
            .then()
            .statusCode(HttpStatusCode.fromStatusCode(200).getStatusCode())
            .body("value.get(0).Pet_info_id", equalTo("<<replace_with_keyFieldValue>>"))
            .body("value.size()", is(1));
    given()
            .when()
            .get("/TripPinRESTierService/Pet_Infos?$skip=1")
            .then()
            .statusCode(HttpStatusCode.fromStatusCode(200).getStatusCode())
            .body("value.get(0).Pet_info_id", equalTo("<<replace_with_keyFieldValue>>"))
            .body("value.size()", is(1));
    given()
            .when()
            .get("/TripPinRESTierService/Pet_Infos/$count")
            .then()
            .statusCode(HttpStatusCode.fromStatusCode(200).getStatusCode())
            .body(is("2"));
            
            
		
	     given()
	            .when()
	            .get("/trippin_metadata/Pet_Infos(<<replace_with_keyFieldValue>>)?$expand=Pet_vet_visit_history")
	            .then()
	            .statusCode(HttpStatusCode.fromStatusCode(200).getStatusCode())
	            .body("Pet_vet_visit_history.get(0).Vet_visit_history_id",is(1));
	    given()
	            .when()
	            .get("/trippin_metadata/Pet_Infos(<<replace_with_keyFieldValue>>)?$expand=Pet_vet_visit_history($top=1)")
	            .then()
	            .statusCode(HttpStatusCode.fromStatusCode(200).getStatusCode())
	            .body("Pet_vet_visit_history.get(0).Vet_visit_history_id",is(1));
	    given()
	        .when()
	        .get("/trippin_metadata/Pet_Infos('lewisblack1')?$expand=Pet_vet_visit_history($select=Vet_visit_history_id,Date)")
	        .then()
	        .statusCode(HttpStatusCode.fromStatusCode(200).getStatusCode())
	        .body("Pet_vet_visit_history.get(0).Vet_visit_history_id",is(1))
	        .body("Pet_vet_visit_history.get(0).size()", is(2));
	        
	    
        
  
		
	     given()
	            .when()
	            .get("/trippin_metadata/Pet_Infos(<<replace_with_keyFieldValue>>)?$expand=Pet_info_remainder")
	            .then()
	            .statusCode(HttpStatusCode.fromStatusCode(200).getStatusCode())
	            .body("Pet_info_remainder.get(0).Remainder_id",is(1));
	    given()
	            .when()
	            .get("/trippin_metadata/Pet_Infos(<<replace_with_keyFieldValue>>)?$expand=Pet_info_remainder($top=1)")
	            .then()
	            .statusCode(HttpStatusCode.fromStatusCode(200).getStatusCode())
	            .body("Pet_info_remainder.get(0).Remainder_id",is(1));
	    given()
	        .when()
	        .get("/trippin_metadata/Pet_Infos('lewisblack1')?$expand=Pet_info_remainder($select=Remainder_id,Date)")
	        .then()
	        .statusCode(HttpStatusCode.fromStatusCode(200).getStatusCode())
	        .body("Pet_info_remainder.get(0).Remainder_id",is(1))
	        .body("Pet_info_remainder.get(0).size()", is(2));
	        
	    
        
  
    } 
	
	

	
  @Test
  void  testCreateRemainderInstance() throws IOException {
    given()
        .contentType("application/json")
        .body(getPayload("RemainderInstance.json"))
        .when()
        .post("/trippin_metadata/Remainders")
        .then()
        .statusCode(HttpStatusCode.CREATED.getStatusCode());
    
  }
	
	
  
   
  
   @Test
  public void testSystemFilterOptionsRemainder() throws IOException {
  
   given()
            .when()
            .get("/trippin_metadata/Remainders?$top=1")
            .then()
            .statusCode(HttpStatusCode.fromStatusCode(200).getStatusCode())
            .body("value.get(0).Remainder_id", equalTo("<<replace_with_keyFieldValue>>"))
            .body("value.size()", is(1));
    given()
            .when()
            .get("/TripPinRESTierService/Remainders?$skip=1")
            .then()
            .statusCode(HttpStatusCode.fromStatusCode(200).getStatusCode())
            .body("value.get(0).Remainder_id", equalTo("<<replace_with_keyFieldValue>>"))
            .body("value.size()", is(1));
    given()
            .when()
            .get("/TripPinRESTierService/Remainders/$count")
            .then()
            .statusCode(HttpStatusCode.fromStatusCode(200).getStatusCode())
            .body(is("2"));
            
            
    } 
	
           
       
  
  
  
  
 
  @AfterEach
  void  teardown() {
    jdbcTemplate.execute("DELETE FROM trippin_metadata.Vet_Visit_History");
    jdbcTemplate.execute("DELETE FROM trippin_metadata.Pet_Info");
    jdbcTemplate.execute("DELETE FROM trippin_metadata.Remainder");
     jdbcTemplate.execute("DELETE FROM trippin_metadata.Pet_InfoPet_vet_visit_history");

    RestAssuredMockMvc.reset();
  }
}
