package br.com.grupojcr.rmws.dao;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;

import org.apache.log4j.Logger;

import br.com.grupojcr.rmws.dto.AprovadorDTO;
import br.com.grupojcr.rmws.dto.ItemDTO;
import br.com.grupojcr.rmws.dto.MonitorAprovacaoDTO;
import br.com.grupojcr.rmws.dto.MovimentoDTO;
import br.com.gupojcr.rmws.util.TreatDate;
import br.com.gupojcr.rmws.util.TreatNumber;
import br.com.gupojcr.rmws.util.Util;

@Stateless
public class RMDAO extends GenericDAO {
	
	protected static Logger LOG = Logger.getLogger(RMDAO.class);
	
	@TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
    public MovimentoDTO obterMovimento(Integer codMovimento, Integer codColigada) {
        
        Connection conn = null;
        PreparedStatement ps = null;
        
        try {
            
            conn = datasource.getConnection();
            
            StringBuilder sb = new StringBuilder();
            sb.append("SELECT DISTINCT ")
	            .append("FILIAL.CODCOLIGADA AS CODIGO_EMPRESA, ")
	            .append("FILIAL.NOME AS EMPRESA, ")
	            .append("USUARIO.NOME AS SOLICITANTE, ")
	            .append("TMOV.IDMOV AS IDENTIFICADOR_RM, ")
	            .append("TMOV.DATAEMISSAO AS DT_EMISSAO, ")
	            .append("FORNECEDOR.CODCFO AS CODIGO_FORNECEDOR, ")
	            .append("FORNECEDOR.NOME AS FORNECEDOR, ")
	            .append("COND_PAGAMENTO.CODCPG AS CODIGO_COND_PAGAMENTO, ")
	            .append("COND_PAGAMENTO.NOME AS CONDICAO_PAGAMENTO, ")
	            .append("TMOV.VALORBRUTOORIG AS VALOR_TOTAL, ")
	            .append("CCUSTO.CODCCUSTO AS CODIGO_CCUSTO, ")
	            .append("CCUSTO.NOME AS CCUSTO, ")
	            .append("CAST(HISTORICO.HISTORICOLONGO AS VARCHAR(8000)) AS OBSERVACAO, ")
	            .append("TMOV.CODMOEVALORLIQUIDO AS MOEDA ")
	            .append("FROM ")
	            .append("TMOV AS TMOV (NOLOCK) ")
	            .append("JOIN GFILIAL  (NOLOCK) AS FILIAL ON (FILIAL.CODCOLIGADA = TMOV.CODCOLIGADA AND FILIAL.CODFILIAL = TMOV.CODFILIAL) ")
	            .append("JOIN GCCUSTO (NOLOCK) AS CCUSTO ON (CCUSTO.CODCCUSTO = TMOV.CODCCUSTO) ")
	            .append("JOIN FCFO (NOLOCK) AS FORNECEDOR ON (FORNECEDOR.CODCFO = TMOV.CODCFO) ")
	            .append("JOIN GUSUARIO (NOLOCK) AS USUARIO ON (USUARIO.CODUSUARIO LIKE TMOV.CODUSUARIO) ")
	            .append("JOIN TCPG (NOLOCK) AS COND_PAGAMENTO ON (COND_PAGAMENTO.CODCPG = TMOV.CODCPG AND COND_PAGAMENTO.CODCOLIGADA = TMOV.CODCOLIGADA) ")
	            .append("JOIN TMOVHISTORICO (NOLOCK) AS HISTORICO ON (HISTORICO.CODCOLIGADA = TMOV.CODCOLIGADA AND HISTORICO.IDMOV = TMOV.IDMOV) ")
	            .append("WHERE TMOV.IDMOV = ? ")
	            .append("AND TMOV.CODCOLIGADA = ?");
            ps = conn.prepareStatement(sb.toString());

            ps.setInt(1, codMovimento);
            ps.setInt(2, codColigada);
            
            ResultSet set = ps.executeQuery();
            
            if(set.next()) {
            	MovimentoDTO dto = new MovimentoDTO();
            	dto.setIdColigada(set.getInt("CODIGO_EMPRESA"));
            	dto.setNomeEmpresa(set.getString("EMPRESA"));
            	dto.setSolicitante(set.getString("SOLICITANTE"));
            	dto.setIdMov(set.getInt("IDENTIFICADOR_RM"));
            	dto.setDataEmissao(TreatDate.format("dd/MM/yyyy", set.getDate("DT_EMISSAO")));
            	dto.setIdFornecedor(set.getString("CODIGO_FORNECEDOR"));
            	dto.setFornecedor(set.getString("FORNECEDOR"));
            	dto.setIdCondicaoPagamento(set.getString("CODIGO_COND_PAGAMENTO"));
            	dto.setCondicaoPagamento(set.getString("CONDICAO_PAGAMENTO"));
            	BigDecimal valor = set.getBigDecimal("VALOR_TOTAL");
            	dto.setValorTotal(TreatNumber.formatMoney(valor).toString());
            	dto.setIdCentroCusto(set.getString("CODIGO_CCUSTO"));
            	dto.setCentroCusto(set.getString("CCUSTO"));
            	dto.setObservacao(set.getString("OBSERVACAO"));
            	dto.setMoeda(set.getString("MOEDA"));
            	dto.setListaItem(listarItensMovimento(codMovimento, codColigada));
                return dto;
            } else {
                return null;
            }
        } catch (Exception e) {
            LOG.error("Erro ao obter movimento");
        } finally {
            if (ps != null) {
                try {
                    ps.close();
                } catch (SQLException e) {
                	LOG.error(e.getMessage(), e);
                } finally {
                    ps = null;
                }
            }
            if (conn != null) {
                try {
                    conn.close();
                } catch (SQLException e) {
                	LOG.error(e.getMessage(), e);
                } finally {
                    conn = null;
                }
            }
        }
		return null;
    }
	
	@TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
    public List<ItemDTO> listarItensMovimento(Integer codMovimento, Integer codColigada) {
        
		List<ItemDTO> listaDTO = new ArrayList<ItemDTO>();
        Connection conn = null;
        PreparedStatement ps = null;
        
        try {
            
            conn = datasource.getConnection();
            
            StringBuilder sb = new StringBuilder();
            sb.append("SELECT ")
	            .append("PRODUTO.CODIGOPRD AS CODIGO_PRODUTO, ")
	            .append("PRODUTO.NOMEFANTASIA AS NOME, ")
	            .append("ITEM.QUANTIDADETOTAL AS QUANTIDADE, ")
	            .append("ITEM.CODUND AS UNIDADE, ")
	            .append("ITEM.VALORBRUTOITEMORIG AS VALOR, ")
	            .append("CC.CODCCUSTO AS CODIGO_CCUSTO, ")
	            .append("CC.NOME AS CENTRO_CUSTO, ")
	            .append("ORCAMENTO.CODTBORCAMENTO AS CODIGO_NATUREZA, ")
	            .append("ORCAMENTO.DESCRICAO AS NATUREZA, ")
	            .append("HISTORICO.HISTORICOLONGO AS OBSERVACAO ")
	            .append("FROM ")
	            .append("TITMMOV AS ITEM (NOLOCK) ")
	            .append("JOIN TPRODUTO (NOLOCK) AS PRODUTO ON (PRODUTO.IDPRD = ITEM.IDPRD) ")
	            .append("JOIN GCCUSTO (NOLOCK) AS CC ON (CC.CODCCUSTO = ITEM.CODCCUSTO AND CC.CODCOLIGADA = ITEM.CODCOLIGADA) ")
	            .append("JOIN TTBORCAMENTO (NOLOCK) AS ORCAMENTO ON (ORCAMENTO.CODTBORCAMENTO = ITEM.CODTBORCAMENTO) ")
	            .append("JOIN TITMMOVHISTORICO (NOLOCK) AS HISTORICO ON (HISTORICO.IDMOV = ITEM.IDMOV AND HISTORICO.CODCOLIGADA = ITEM.CODCOLIGADA) ")
	            .append("WHERE ITEM.CODCOLIGADA = ? ")
	            .append("AND ITEM.IDMOV = ? ");
	            
            ps = conn.prepareStatement(sb.toString());

            ps.setInt(1, codColigada);
            ps.setInt(2, codMovimento);
            
            ResultSet set = ps.executeQuery();
            
            if(set.next()) {
            	ItemDTO dto = new ItemDTO();
            	dto.setIdProduto(set.getString("CODIGO_PRODUTO"));
            	dto.setProduto(set.getString("NOME"));
            	dto.setQuantidade(set.getInt("QUANTIDADE"));
            	dto.setUnidade(set.getString("UNIDADE"));
            	BigDecimal valor = set.getBigDecimal("VALOR");
            	dto.setPrecoUnitario(TreatNumber.formatMoney(valor).toString());
            	dto.setIdCentroCusto(set.getString("CODIGO_CCUSTO"));
            	dto.setCentroCusto(set.getString("CENTRO_CUSTO"));
            	dto.setIdNaturezaOrcamentaria(set.getString("CODIGO_NATUREZA"));
            	dto.setNaturezaOrcamentaria(set.getString("NATUREZA"));
            	dto.setObservacao(set.getString("OBSERVACAO"));
            	
                listaDTO.add(dto);
            }
        } catch (Exception e) {
            LOG.error("Erro ao obter itens do movimento");
        } finally {
            if (ps != null) {
                try {
                    ps.close();
                } catch (SQLException e) {
                	LOG.error(e.getMessage(), e);
                } finally {
                    ps = null;
                }
            }
            if (conn != null) {
                try {
                    conn.close();
                } catch (SQLException e) {
                	LOG.error(e.getMessage(), e);
                } finally {
                    conn = null;
                }
            }
        }
		return listaDTO;
    }
	
	@TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
    public String obterLotacao(Integer codColigada, String codCentroCusto) {
        
        Connection conn = null;
        PreparedStatement ps = null;
        
        try {
            
            conn = datasource.getConnection();
            
            StringBuilder sb = new StringBuilder();
            sb.append("SELECT LOTACAO FROM CCUSTOCOMPL WHERE CODCOLIGADA = ? AND CODCCUSTO LIKE ?");
	            
            ps = conn.prepareStatement(sb.toString());

            ps.setInt(1, codColigada);
            ps.setString(2, codCentroCusto);
            
            ResultSet set = ps.executeQuery();
            
            if(set.next()) {
            	return set.getString(1);
            } else {
            	return null;
            }
        } catch (Exception e) {
            LOG.error("Erro ao obter lotacao");
        } finally {
            if (ps != null) {
                try {
                    ps.close();
                } catch (SQLException e) {
                	LOG.error(e.getMessage(), e);
                } finally {
                    ps = null;
                }
            }
            if (conn != null) {
                try {
                    conn.close();
                } catch (SQLException e) {
                	LOG.error(e.getMessage(), e);
                } finally {
                    conn = null;
                }
            }
        }
		return null;
    }
	
	@TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
    public List<AprovadorDTO> obterAprovadores(String lotacao) {
        
		List<AprovadorDTO> listaDTO = new ArrayList<AprovadorDTO>();
        Connection conn = null;
        PreparedStatement ps = null;
        
        try {
            
            conn = datasource.getConnection();
            
            StringBuilder sb = new StringBuilder();
            sb.append("SELECT ")
            	.append("CODCOLIGADA, CODCCUSTO, USRAPROV, VALORDEMOV, VALORATEMOV, VALORDECNT, VALORATECNT ")
            	.append("FROM ")
            	.append("ZMDAPROVADOR ")
            	.append("WHERE CODCCUSTO = ?");
	            
            ps = conn.prepareStatement(sb.toString());

            ps.setString(1, lotacao);
            
            ResultSet set = ps.executeQuery();
            
            if(set.next()) {
            	AprovadorDTO dto = new AprovadorDTO();
            	dto.setIdColigada(set.getInt("CODCOLIGADA"));
            	dto.setLotacao(set.getString("CODCCUSTO"));
            	dto.setUsuarioAprovacao(set.getString("USRAPROV"));
            	dto.setValorInicialMovimento(set.getBigDecimal("VALORDEMOV"));
            	dto.setValorFinalMovimento(set.getBigDecimal("VALORATEMOV"));
            	dto.setValorInicialContrato(set.getBigDecimal("VALORDECNT"));
            	dto.setValorFinalContrato(set.getBigDecimal("VALORATECNT"));
            	
                listaDTO.add(dto);
            }
        } catch (Exception e) {
            LOG.error("Erro ao obter aprovadores");
        } finally {
            if (ps != null) {
                try {
                    ps.close();
                } catch (SQLException e) {
                	LOG.error(e.getMessage(), e);
                } finally {
                    ps = null;
                }
            }
            if (conn != null) {
                try {
                    conn.close();
                } catch (SQLException e) {
                	LOG.error(e.getMessage(), e);
                } finally {
                    conn = null;
                }
            }
        }
		return listaDTO;
    }
	
	@TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
    public void incluirMonitorAprovacao(Integer idColigada, Integer idMov, String idTipoMovimento, String usuarioRequisitante,
    		String situacao, String usuarioAprovacao, String usuarioAprovacaoAlternativo, Date dtAprovacao, String quemAprova,
    		String usuarioCriacao, Date dtCriacao, String usuarioAlteracao, Date dtAlteracao) {
        
        Connection conn = null;
        PreparedStatement ps = null;
        
        try {
            
            conn = datasource.getConnection();
            
            StringBuilder sb = new StringBuilder();
            sb.append("INSERT INTO ZMDMONITORAPROVACAO ")
            	.append("(CODCOLIGADA, IDMOV, CODTMV, NUMEROMOV, USRREQUIS, SITUACAO, USRAPROV, USRAPROVALTERN, DATAAPROV, QUEMAPROVA, RECCREATEDBY, RECCREATEDON, RECMODIFIEDBY, RECMODIFIEDON) ")
            	.append("VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?) ");
            
            ps = conn.prepareStatement(sb.toString());

            ps.setInt(1, idColigada);
            ps.setInt(2, idMov);
            ps.setString(3, idTipoMovimento);
            ps.setString(4, idMov.toString());
            ps.setString(5, usuarioRequisitante);
            ps.setString(6, situacao);
            ps.setString(7, usuarioAprovacao);
            ps.setString(8, usuarioAprovacaoAlternativo);
            if(Util.isNotNull(dtAprovacao)) {
            	ps.setDate(9, new java.sql.Date(dtAprovacao.getTime()));
            } else {
            	ps.setNull(9, Types.DATE);
            }
            ps.setString(10, quemAprova);
            ps.setString(11, usuarioCriacao);
            if(Util.isNotNull(dtCriacao)) {
            	ps.setDate(12, new java.sql.Date(dtCriacao.getTime()));
            } else {
            	ps.setNull(12, Types.DATE);
            }
            ps.setString(13, usuarioAlteracao);
            if(Util.isNotNull(dtAlteracao)) {
            	ps.setDate(14, new java.sql.Date(dtAlteracao.getTime()));
            } else {
            	ps.setNull(14, Types.DATE);
            }
            
            
            ps.execute();
        } catch (Exception e) {
            LOG.error("Erro ao obter aprovadores");
        } finally {
            if (ps != null) {
                try {
                    ps.close();
                } catch (SQLException e) {
                	LOG.error(e.getMessage(), e);
                } finally {
                    ps = null;
                }
            }
            if (conn != null) {
                try {
                    conn.close();
                } catch (SQLException e) {
                	LOG.error(e.getMessage(), e);
                } finally {
                    conn = null;
                }
            }
        }
    }
	
	@TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
    public MonitorAprovacaoDTO obterUltimaAprovacao(Integer idColigada, Integer idMov) {
        
        Connection conn = null;
        PreparedStatement ps = null;
        
        try {
            
            conn = datasource.getConnection();
            
            StringBuilder sb = new StringBuilder();
            sb.append("SELECT ")
            	.append("SITUACAO, USRAPROV, DATAAPROV ")
            	.append("FROM ZMDMONITORAPROVACAO ")
            	.append("WHERE CODCOLIGADA = ? ")
            	.append("AND IDMOV = ? ")
            	.append("AND DATAAPROV = (SELECT MAX(DATAAPROV) FROM ZMDMONITORAPROVACAO WHERE CODCOLIGADA = ? and IDMOV = ?)");
            
            ps = conn.prepareStatement(sb.toString());

            ps.setInt(1, idColigada);
            ps.setInt(2, idMov);
            ps.setInt(3, idColigada);
            ps.setInt(4, idMov);
            
            ResultSet set = ps.executeQuery();
            
            if(set.next()) {
            	MonitorAprovacaoDTO dto = new MonitorAprovacaoDTO();
            	dto.setDtAprovacao(set.getDate("DATAAPROV"));
            	dto.setUsuarioAprovou(set.getString("USRAPROV"));
            	dto.setSituacao(set.getString("SITUACAO"));
            	
                return dto;
            } else {
            	return null;
            }
        } catch (Exception e) {
            LOG.error("Erro ao obter ultima aprovação");
        } finally {
            if (ps != null) {
                try {
                    ps.close();
                } catch (SQLException e) {
                	LOG.error(e.getMessage(), e);
                } finally {
                    ps = null;
                }
            }
            if (conn != null) {
                try {
                    conn.close();
                } catch (SQLException e) {
                	LOG.error(e.getMessage(), e);
                } finally {
                    conn = null;
                }
            }
        }
        return null;
    }

}
