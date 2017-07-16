package org.pentaho.di.job.entries.ftpsput;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mockito.InOrder;
import org.mockito.Mockito;
import org.pentaho.di.job.entries.ftpsget.FTPSConnection;

public class JobEntryFTPSPUTTest {

  @BeforeClass
  public static void setUpBeforeClass() throws Exception {
  }

  @AfterClass
  public static void tearDownAfterClass() throws Exception {
  }

  @Before
  public void setUp() throws Exception {
  }

  @After
  public void tearDown() throws Exception {
  }

  /**
   * PDI-6868, attempt to set binary mode is after the connection.connect() succeeded.
   * @throws Exception
   */
  @Test
  public void testBinaryModeSetAfterConnectionSuccess() throws Exception {
    JobEntryFTPSPUT job = new JobEntryFTPSPUTCustom();
    FTPSConnection connection = Mockito.mock( FTPSConnection.class );
    InOrder inOrder = Mockito.inOrder( connection );
    job.buildFTPSConnection( connection );
    inOrder.verify( connection ).connect();
    inOrder.verify( connection ).setBinaryMode( Mockito.anyBoolean() );
  }

  class JobEntryFTPSPUTCustom extends JobEntryFTPSPUT {
    @Override
    public boolean isBinaryMode() {
      return true;
    }
  }

}
