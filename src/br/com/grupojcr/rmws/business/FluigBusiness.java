package br.com.grupojcr.rmws.business;

import javax.ejb.Stateless;
import javax.xml.rpc.ServiceException;

import org.apache.log4j.Logger;

import com.totvs.technology.ecm.workflow.ws.ECMWorkflowEngineServiceServiceLocator;
import com.totvs.technology.ecm.workflow.ws.ECMWorkflowEngineServiceServiceSoapBindingStub;

@Stateless
public class FluigBusiness {
	
	protected static Logger LOG = Logger.getLogger(FluigBusiness.class);
	
	/**
	 * 
	 * Método responsável por obter stub do cliente WS - ECMWorkflowEngine
	 *
	 * @since 18 de fev de 2018 14:26:23 (Projeto)
	 * @author Leonan Mattos - <leonan.mattos@sigma.com.br>
	 * @since 18 de fev de 2018 14:26:23  (Implementação)
	 * @author Leonan Mattos - <leonan.mattos@sigma.com.br>
	 * @return
	 * @throws ServiceException 
	 *
	 * @rastreabilidade_requisito
	 */
	private ECMWorkflowEngineServiceServiceSoapBindingStub obterProxy() throws ServiceException {
        ECMWorkflowEngineServiceServiceLocator locator = new ECMWorkflowEngineServiceServiceLocator();
        ECMWorkflowEngineServiceServiceSoapBindingStub cliente = (ECMWorkflowEngineServiceServiceSoapBindingStub) locator.getWorkflowEngineServicePort();
        return cliente;
    }
	
	public void iniciarProcessoFluig() {
		try {
			ECMWorkflowEngineServiceServiceSoapBindingStub cliente = obterProxy();
				
			cliente.startProcess("leonan", "123", 1, "Teste_Leonan", 2, new String[] {"leonan"}, null, "leonan", true, null, new String[][]{{"codcoligada", "1"}}, null, false);
		} catch (ServiceException e) {
			LOG.error(e.getStackTrace());
		} catch (Exception e) {
			LOG.error(e.getStackTrace());
		}
	}

}
