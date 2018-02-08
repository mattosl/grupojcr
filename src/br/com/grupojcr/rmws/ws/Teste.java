package br.com.grupojcr.rmws.ws;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebService;
import javax.jws.soap.SOAPBinding;
import javax.jws.soap.SOAPBinding.Style;
import javax.jws.soap.SOAPBinding.Use;

@WebService
@SOAPBinding(style = Style.RPC, use = Use.LITERAL)
public class Teste {

	@WebMethod
	public String sayHello(@WebParam(name="nome") String nome) {
		return "Olá " + nome;
	}
}
