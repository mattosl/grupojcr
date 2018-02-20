package br.com.grupojcr.rmws.ws;

import javax.ejb.EJB;
import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebService;
import javax.jws.soap.SOAPBinding;
import javax.jws.soap.SOAPBinding.Style;
import javax.jws.soap.SOAPBinding.Use;

import br.com.grupojcr.rmws.business.FluigBusiness;
import br.com.grupojcr.rmws.dao.RMDAO;

@WebService
@SOAPBinding(style = Style.RPC, use = Use.LITERAL)
public class AprovacaoWS {
	
	@EJB
	private RMDAO rmDAO;
	
	@EJB
	private FluigBusiness fluigBusiness;

	@WebMethod
	public void iniciarAprovacao(@WebParam(name="movimento") Integer idMovimento, @WebParam(name="coligada") Integer idColigada) {
		System.out.println("Movimento: " + idMovimento);
		System.out.println("Coligada: " + idColigada);
		
		// Obter a aprovação
		// Criar 1º registro do monitor de aprovação
		// Obter aprovadores conforme valores e centro de custo
		// Startar processo
		
		fluigBusiness.iniciarProcessoFluig();
		
		
	}
}
