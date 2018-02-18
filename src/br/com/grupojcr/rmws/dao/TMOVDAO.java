package br.com.grupojcr.rmws.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;

import org.apache.log4j.Logger;

@Stateless
public class TMOVDAO extends GenericDAO {
	
	protected static Logger LOG = Logger.getLogger(TMOVDAO.class);
	
	@TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
    public Integer obterMovimento(Integer codMovimento, Integer codColigada) {
        
        Connection conn = null;
        PreparedStatement ps = null;
        
        try {
            
            conn = datasource.getConnection();
            
            ps = conn.prepareStatement("SELECT TMOV.IDMOV FROM TMOV TMOV WHERE TMOV.CODCOLIGADA = ? AND TMOV.IDMOV = ?");

            ps.setInt(1, codColigada);
            ps.setInt(2, codMovimento);
            
            ResultSet set = ps.executeQuery();
            
            if(set.next()) {
                return set.getInt(1);
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

}
