package com.apple.interview;
/**
 * @author Rajeswari
 * Interview question 2 - iTunes search api
 * Used RestAssured to process RESTful responses
 * Date 03/09/2017
 * Data provider has set of test data
 * parameters are term,country,media and limit
 * 
 */
import org.testng.annotations.*;
import com.jayway.restassured.RestAssured;
import com.jayway.restassured.builder.RequestSpecBuilder;
import com.jayway.restassured.config.EncoderConfig;
import com.jayway.restassured.http.ContentType;
import com.jayway.restassured.parsing.Parser;
import com.jayway.restassured.response.Response;
import static com.jayway.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;
import java.util.List;
import org.testng.Assert;

public class searchAPITest  {
	RequestSpecBuilder builder = new RequestSpecBuilder();
	public static String jsonAsString;
	@BeforeClass
	public static void setUp() {
	RestAssured.baseURI = "https://itunes.apple.com/search";	
	RestAssured.basePath ="/search";
	RestAssured.registerParser("text/javascript", Parser.JSON);
	}
	
@DataProvider(name = "allparamsmusic")
public String[][] createallparamsTestData() {
		
	return new String[][] {
				{"madonna", "US","music"},
				{"taylorswift","US", "music"},
				{"maria carey", "US","music"}
		};
	}
@DataProvider(name = "allparamsmusicelvis")
public String[][] createallparamsMusicTestData() {
		
	return new String[][] {
				{"elvis", "US","music"},
				{"Elvis","US", "music"},
				{"Elvis Presley", "US","music"}
		};
	}
@DataProvider(name = "termAndCountry")
public String[][] createtermandCountryTestData() {
		
	return new String[][] {
				{"madonna", "US"},
				{"taylorswift","US"},
				{"maria carey", "US"}
		};
	}
	
@DataProvider(name = "invalidcountries")
public String[][] createMediaTestData() {
		
		return new String[][] {
				{"maria carey", "USA"},
				{"Britney", "CAANDA"},
				{"Adale", "China"}
		};
	}
@DataProvider(name = "mediaMovies")
public String[][] createMovieTestData() {
	return new String[][] {
				{"Clooney", "US","movie"},
				{"George clooney", "US", "movie"},
				{"CLOONEY", "US", "movie"}
		};
	}
@DataProvider(name = "allparamsmusicIndia")
public String[][] createallparamsOtherCountryTestData() {	
		return new String[][] {
				{"ilayaraja", "IN","music"},
				{"A R rahman","IN", "music"},
				{"K J Yesudas","IN","music"}
		};
	}

@DataProvider(name = "mediaTvshows")
public String[][] createallparamTvshowTestData() {	
		return new String[][] {
				{"dora", "US","tvShow"},
				{"DORA","US", "tvShow"},
				{"DOra","US","tvShow"}
		};
	}
@DataProvider(name = "mediaMusicVideo")
public String[][] createallparamMusicVideoTestData() {	
		return new String[][] {
				{"Britney", "US","musicVideo"},
				{"riky martin","US", "musicVideo"},
				{"Adale","GB","musicVideo"}
		};
	}
//Test case 1: Basic check to verify if the service is up 
@Test(description="TestCase 1 checking if the service is up and running")
public void validateitunesIsUp() {
		int expectedStatusCode=200;
		given().
		when().
		get().
		then().
		assertThat().
		statusCode(expectedStatusCode);
			
}

// Test case 2: Search with all parameters - Positive scenario with limit=1 - verify response has song,
@Test(description="TestCase 2 Search with all parameters - Positive scenario verify response has song",dataProvider = "allparamsmusic")
public void validateAllparams(String term, String country, String media) {
		given().
		queryParam("term",term).queryParam("country",country).queryParam("media","all").queryParam("limit", 1).
		when().
		get("").
		then().
		assertThat().
		body("resultCount",equalTo(1)).
		assertThat().
		body(containsString("song"));		
}
// Test case 3: Search with all parameters - Positive Search media as movie and with country and term parameters 
@Test(description="TestCase 3 Search with all parameters - Positive Search media as movie",dataProvider = "mediaMovies")
public void validateMoviemedia(String term, String country, String media) {
		given().
		queryParam("term",term).queryParam("country",country).queryParam("media","movie").queryParam("limit", 1).
		when().
	    get("").
		then().
	    assertThat().
		body("resultCount",equalTo(1)).
	    assertThat().
		body(containsString("George Clooney"));
}
//Test case 4: Search with 2 parameterized data term and country with media and limit parameters -verify currency is USD
@Test(description="TestCase 4 Search with term and country parameters -verify currency is USD",dataProvider = "termAndCountry")
public void validateAllparamsMusic(String term, String country) {
		given().
		queryParam("term",term).queryParam("country",country).queryParam("media","all").queryParam("limit", 1).
		when().
	    get("").
		then().
		assertThat().
		body("resultCount",equalTo(1)).
		assertThat().
		body(containsString("USD"));		
}

//Test case 5: verify record count and track names by passing term, country and media from data provider
@Test(description= "Test case 5: verify record count and track names",dataProvider="allparamsmusicelvis")
public void validateCountAndContent(String term, String country, String media) {
	
		Response resp = given().
		queryParam("term",term).queryParam("country",country).queryParam("media","all").
		get("");
		List<String> results = resp.jsonPath().getList("results");
		Assert.assertEquals(results.size(), 50);
		given().
		queryParam("term",term).queryParam("country",country).queryParam("media","all").
		get("").
		then().
		body("results.findAll { it.trackId >= 388128266 }.trackName", hasItems("Suspicious Minds","Way Down"));
		
}

// Test case 6: Search with all parameters - Positive Search music from other countries
@Test(description="Test case 6: Search with all parameters - Positive Search music from other countries",dataProvider = "allparamsmusicIndia")
public void validateOtherCountries(String term, String country, String media) {
		given().
		queryParam("term",term).queryParam("country",country).queryParam("media","all").queryParam("limit", 1).
		get("").
		then().
		assertThat().
		body("resultCount",equalTo(1)).
		assertThat().
		body(containsString("song"));
}
	
// Test case 7: Negative scenario to check error message for invalid country"
@Test(description="Test case 7:  Negative scenario to check error message for invalid country",dataProvider = "invalidcountries")
public void validateInvalidCountycode(String artist, String country) {
		given().
		queryParam("term",artist).queryParam("country",country).queryParam("media","music").queryParam("limit", 1).
		when().
		get("").
		then().
		assertThat().
		body("errorMessage",containsString("Invalid value"));

		}
// Test case 8: Verify content type as Json for the search media as tvshow with all parameters 
@Test(description="Test case 8: Verify content type as Json for the search media as tvshow with all parameters", dataProvider="mediaTvshows")
public void validateMediaTvshowsContenType(String artist, String country, String media) {	
		Response response =given().
				log().all().
                config(RestAssured.config().encoderConfig(EncoderConfig.encoderConfig().appendDefaultContentCharsetToContentTypeIfUndefined(false))).
				queryParam("term",artist).queryParam("country",country).queryParam("media",media).queryParam("limit", 4).
				when().
			        get("").
			    then().
			        contentType(ContentType.JSON).extract().response(); // check that the content type return from the API is JSON
}
@Test(description="TestCase 9: Verify artistId with the search media as tvshow", dataProvider="mediaTvshows")
public void validateMediaTvshowsArtistId(String artist, String country, String media) {
		 given().
		 queryParam("term",artist).queryParam("country",country).queryParam("media",media).queryParam("limit", 4).
		 when().
		 get("").
		 then().
		 assertThat().
		 body("results.artistId[-1]",equalTo(24013706));
	
}
@Test(description="TestCase 10: Verify the response for the kind as  music-videos", dataProvider="mediaMusicVideo")
public void validateMediaMusicVideo(String artist, String country, String media) {
		 given().
		 queryParam("term",artist).queryParam("country",country).queryParam("media",media).queryParam("limit", 4).
		 when().
		 get("").
		 then().
		 assertThat().
		 body("results.kind[-1]",equalTo("music-video"));
}

@Test(description="TestCase 11: Verify response for podcasts with all parameters")
public void validateMediaPodcast() {
		 given().
		 queryParam("term","This American life").queryParam("country","US").queryParam("media","podcast").queryParam("limit", 4).
		 when().
		 get("").
		 then().
		 assertThat().
		 body("results.kind[-1]",equalTo("podcast"));
}

@Test(description="TestCase 12: Verify response for shortFilms with all parameters")
public void validateMediashortFilm() {
		 given().
		 queryParam("term","History").queryParam("country","US").queryParam("media","shortFilm").queryParam("limit", 4).
		 when().
		 get("").
		 then().
		 assertThat().
		 body("resultCount",equalTo(0));
}
//https://itunes.apple.com/search?term=adobe acrobat&media=software&country=US&limit=25

@Test(description="TestCase 13: Verify response for the search media software")
public void validateMediaSoftware() {
		 given().
		 queryParam("term","adobe acrobat").queryParam("country","US").queryParam("media","software").queryParam("limit", 4).
		 when().
		 get("").
		 then().
		 assertThat().
		 body("results.kind[-1]",equalTo("software"));
}
@Test(description="TestCase 14: Verify response for the search media ebook")
public void validateMediaEbook() {
		 given().
		 queryParam("term","potter").queryParam("country","US").queryParam("media","ebook").queryParam("limit", 4).
		 when().
		 get("").
		 then().
		 assertThat().
		 body("results.kind[-1]",equalTo("ebook"));
}
@Test(description="TestCase 15: Verify response for the search media movie other countries")
public void validateMediaGlobalmovies() {
		 given().
		 queryParam("term","potter").queryParam("country","GB").queryParam("media","movie").queryParam("limit", 4).
		 when().
		 get("").
		 then().
		 assertThat().
		 body("results.kind[-1]",equalTo("feature-movie"));
}
}

