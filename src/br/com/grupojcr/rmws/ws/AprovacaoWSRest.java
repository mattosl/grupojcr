package br.com.grupojcr.rmws.ws;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import br.com.grupojcr.rmws.dto.MovimentoDTO;

@Path("/aprovacao")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class AprovacaoWSRest {
	
	@POST
	@Path("/dados")
	public Response obterDadosMovimento(MovimentoDTO dto) {
		System.out.println("Teste");
		return Response.status(200).build();
	}

}
