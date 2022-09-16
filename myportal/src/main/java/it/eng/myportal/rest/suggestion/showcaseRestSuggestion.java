package it.eng.myportal.rest.suggestion;

import javax.ejb.Stateless;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;

import org.json.JSONArray;

@Stateless
@Path("rest/showcase/")
public class showcaseRestSuggestion {

	@GET
	@Path("suggestion")
	@Produces("application/json; charset=UTF-8")
	public String showcaseReturnList(@QueryParam("term") String par) {
		JSONArray array = new JSONArray();
		array.put("aaaa 12345");
		array.put("aabb 12345");
		array.put("bbbb 12345");
		array.put("cccc 12345");
		array.put("ccdd 12345");
		array.put("ccde 12345");

		try {
			JSONArray arrayReturn = new JSONArray();
			if (par != null && !par.equals("")) {
				for (int i = 0; i < array.length(); i++) {
					if (array.get(i).toString().contains(par))
						arrayReturn.put(array.get(i));
				}
			}
			return arrayReturn.toString();

		} catch (Exception e) {
			JSONArray arrayReturn = new JSONArray();
			return arrayReturn.toString();
		}
	}
}
