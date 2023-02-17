package com.dreamGames.rowMatchBackend;
import com.dreamGames.rowMatchBackend.requests.LeaderboardRequest;
import com.dreamGames.rowMatchBackend.requests.UserRequest;
import com.dreamGames.rowMatchBackend.responses.*;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.jayway.jsonpath.DocumentContext;
import com.jayway.jsonpath.JsonPath;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class RowMatchBackendApplicationTests {

	@Autowired
	private MockMvc mockMvc;

	public static String asJsonString(final Object obj) {
		try {
			return new ObjectMapper().writeValueAsString(obj);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	public CreateUserResponse CreateUserRequest(String username, String password) throws
			Exception {
		MvcResult mvcResult = this.mockMvc.perform( MockMvcRequestBuilders.post("/api/user/CreateUserRequest")
						.content(asJsonString(new UserRequest(username, password)))
						.contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))

				.andExpect(status().isCreated())
				.andExpect(MockMvcResultMatchers.jsonPath("$.level", is(1)))
				.andExpect(MockMvcResultMatchers.jsonPath("$.coin", is(5000)))
				.andReturn();
		String result = mvcResult.getResponse().getContentAsString();
		DocumentContext jsonResult = JsonPath.parse(result);

		Integer IDi = jsonResult.read("$.userID");
		long ID = IDi;
		Integer level = jsonResult.read("$.level");
		Integer coin = jsonResult.read("$.coin");

		return new CreateUserResponse(ID, level, coin);
	}

	public AuthenticationResponse AuthenticateUserRequest(String username, String password) throws
			Exception {
		MvcResult mvcResult = this.mockMvc.perform( MockMvcRequestBuilders
						.post("/api/user/AuthenticateUserRequest")
						.content(asJsonString(new UserRequest(username, password)))
						.contentType(MediaType.APPLICATION_JSON)
						.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isCreated())
				.andExpect(MockMvcResultMatchers.jsonPath("$.username", is(username)))
				.andExpect(MockMvcResultMatchers.jsonPath("$.jwtToken").exists())
				.andReturn();
		String result = mvcResult.getResponse().getContentAsString();
		DocumentContext jsonResult = JsonPath.parse(result);

		String usernameGet = jsonResult.read("$.username");
		String jwtToken = jsonResult.read("$.jwtToken");

		return new AuthenticationResponse(usernameGet, jwtToken);
	}

	public OneLevelProgressResponse UpdateLevelRequest(CreateUserResponse userResponse, AuthenticationResponse token) throws
			Exception {
		MvcResult mvcResult = this.mockMvc.perform( MockMvcRequestBuilders
						.post("/api/user/UpdateLevelRequest")
						.header("authorization", "Bearer " + token.getJwtToken())
						.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andExpect(MockMvcResultMatchers.jsonPath("$.level", is(userResponse.getLevel() + 1)))
				.andExpect(MockMvcResultMatchers.jsonPath("$.coin", is(userResponse.getCoin() + 25)))
				.andReturn();
		String result = mvcResult.getResponse().getContentAsString();
		DocumentContext jsonResult = JsonPath.parse(result);
		Integer level = jsonResult.read("$.level");
		Integer coin = jsonResult.read("$.coin");
		userResponse.setLevel(level);
		userResponse.setCoin(coin);

		return new OneLevelProgressResponse(level+1, coin+1);
	}

	public String ManuelTournamentStartRequest(AuthenticationResponse token) throws
			Exception {
		MvcResult mvcResult = this.mockMvc.perform( MockMvcRequestBuilders
						.post("/api/tournament/ManuelTournamentStartRequest")
						.header("authorization", "Bearer " + token.getJwtToken())
						.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andReturn();
		String result = mvcResult.getResponse().getContentAsString();
		return result;
	}
	public Long EnterTournamentRequest(CreateUserResponse userResponse, AuthenticationResponse token) throws
			Exception {
		MvcResult mvcResult = this.mockMvc.perform( MockMvcRequestBuilders
						.post("/api/tournament/EnterTournamentRequest")
						.header("authorization", "Bearer " + token.getJwtToken())
						.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andReturn();
		String result = mvcResult.getResponse().getContentAsString();
		DocumentContext jsonResult = JsonPath.parse(result);
		userResponse.setCoin(userResponse.getCoin() - 1000);

		/*I could not parse the list*/
		/*Integer group = jsonResult.read("$.group");
		long groupID = group;*/
		return 1L;
	}


	public String GetLeaderboardRequest(Long group,CreateUserResponse userResponse, AuthenticationResponse token) throws
			Exception {
		MvcResult mvcResult = this.mockMvc.perform( MockMvcRequestBuilders
						.post("/api/tournament/GetLeaderboardRequest")
						.content(asJsonString(new LeaderboardRequest(group)))
						.header("authorization", "Bearer " + token.getJwtToken())
						.accept(MediaType.APPLICATION_JSON))
				.andReturn();

		String result = mvcResult.getResponse().getContentAsString();
		return result;
	}

	public String GetRankRequest(CreateUserResponse userResponse, AuthenticationResponse token) throws
			Exception {
		MvcResult mvcResult = this.mockMvc.perform( MockMvcRequestBuilders
						.post("/api/tournament/GetRankRequest")
						.header("authorization", "Bearer " + token.getJwtToken())
						.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andReturn();
		String result = mvcResult.getResponse().getContentAsString();
		return result;
	}

	@Test
	public void testCase1() throws Exception {
		CreateUserResponse user1 = CreateUserRequest("DreamGames1", "1234");
		AuthenticationResponse jwt1 = AuthenticateUserRequest("DreamGames1", "1234");

		CreateUserResponse user2 = CreateUserRequest("JavaTechie1", "fadsfas2123dfasf");
		AuthenticationResponse jwt2 = AuthenticateUserRequest("JavaTechie1", "fadsfas2123dfasf");

		Integer ct = 0;
		while (ct != 30) {
			UpdateLevelRequest(user1, jwt1);
			UpdateLevelRequest(user2, jwt2);
			ct = ct+1;
		}
		ManuelTournamentStartRequest(jwt1);
		long group = EnterTournamentRequest(user1, jwt1);
		EnterTournamentRequest(user2, jwt2);

		Integer ct2 = 0;
		while (ct2 != 10) {
			UpdateLevelRequest(user1, jwt1);
			UpdateLevelRequest(user2, jwt2);
			ct2 = ct2+1;
		}

		System.out.println(GetLeaderboardRequest(group, user1, jwt1));
	}

}
