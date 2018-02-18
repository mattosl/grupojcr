package br.com.grupojcr.rmws.ws;

import javax.ejb.EJB;
import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebService;
import javax.jws.soap.SOAPBinding;
import javax.jws.soap.SOAPBinding.Style;
import javax.jws.soap.SOAPBinding.Use;

import br.com.grupojcr.rmws.dao.TMOVDAO;

@WebService
@SOAPBinding(style = Style.RPC, use = Use.LITERAL)
public class AprovacaoWS {
	
	@EJB
	private TMOVDAO daoTMOV;

	@WebMethod
	public void iniciarAprovacao(@WebParam(name="movimento") Integer idMovimento, @WebParam(name="coligada") Integer idColigada) {
		System.out.println("Movimento: " + idMovimento);
		System.out.println("Coligada: " + idColigada);
		
		daoTMOV.obterMovimento(idMovimento, idColigada);
		
		
	}
}
