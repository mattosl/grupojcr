package br.com.grupojcr.rmws.ws;

import javax.ejb.EJB;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import br.com.grupojcr.rmws.dao.RMDAO;
import br.com.grupojcr.rmws.dto.MovimentoDTO;
import br.com.gupojcr.rmws.util.TreatString;

@Path("/aprovacao")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class AprovacaoWSRest {
	
	@EJB
	private RMDAO rmDAO;
	
	@POST
	@Path("/dados") 
	public Response obterDadosMovimento(String data) {
		if(TreatString.isNotBlank(data)) {
			MovimentoDTO dto = rmDAO.obterMovimento(14970, 7);
			return Response.status(200).entity(dto).build();
		} else {
			return Response.status(406).build();
		}
	}

}
