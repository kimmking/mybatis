package org.apache.ibatis.submitted.multidb;

import static org.junit.Assert.assertEquals;

import java.io.Reader;
import java.sql.Connection;
import java.sql.DriverManager;

import org.apache.ibatis.io.Resources;
import org.apache.ibatis.jdbc.ScriptRunner;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.junit.BeforeClass;
import org.junit.Test;

public class MultiDbTest {

  protected static SqlSessionFactory sqlSessionFactory;
  protected static SqlSessionFactory sqlSessionFactory2;

  @BeforeClass
  public static void setUp() throws Exception {
    Connection conn = null;

    try {
      Class.forName("org.hsqldb.jdbcDriver");
      conn = DriverManager.getConnection("jdbc:hsqldb:mem:gname", "sa", "");

      Reader reader = Resources.getResourceAsReader("org/apache/ibatis/submitted/multidb/CreateDB.sql");

      ScriptRunner runner = new ScriptRunner(conn);
      runner.setLogWriter(null);
      runner.setErrorLogWriter(null);
      runner.runScript(reader);
      conn.commit();
      reader.close();

      reader = Resources.getResourceAsReader("org/apache/ibatis/submitted/multidb/MultiDbConfig.xml");
      sqlSessionFactory = new SqlSessionFactoryBuilder().build(reader);
      reader.close();
    } finally {
      if (conn != null) {
        conn.close();
      }
    }
  }

  @Test
  public void shouldExecuteHsqlQuery() {
    SqlSession sqlSession = sqlSessionFactory.openSession();
    try {
      MultiDbMapper mapper = sqlSession.getMapper(MultiDbMapper.class);
      String answer = mapper.select1();
      assertEquals("1-hsql", answer);
    } finally {
      sqlSession.close();
    }
  }

  @Test
  public void shouldExecuteCommonQuery() {
    SqlSession sqlSession = sqlSessionFactory.openSession();
    try {
      MultiDbMapper mapper = sqlSession.getMapper(MultiDbMapper.class);
      String answer = mapper.select2();
      assertEquals("2-common", answer);
    } finally {
      sqlSession.close();
    }
  }
}