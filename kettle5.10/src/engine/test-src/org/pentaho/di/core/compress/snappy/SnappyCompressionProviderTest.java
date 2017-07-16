package org.pentaho.di.core.compress.snappy;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.pentaho.di.core.compress.CompressionPluginType;
import org.pentaho.di.core.compress.CompressionProviderFactory;
import org.pentaho.di.core.plugins.PluginRegistry;
import org.xerial.snappy.SnappyInputStream;
import org.xerial.snappy.SnappyOutputStream;

public class SnappyCompressionProviderTest {

  public static final String PROVIDER_NAME = "Snappy";

  public CompressionProviderFactory factory = null;

  @BeforeClass
  public static void setUpBeforeClass() throws Exception {
    PluginRegistry.addPluginType( CompressionPluginType.getInstance() );
    PluginRegistry.init( true );
  }

  @AfterClass
  public static void tearDownAfterClass() throws Exception {
  }

  @Before
  public void setUp() throws Exception {
    factory = CompressionProviderFactory.getInstance();
  }

  @After
  public void tearDown() throws Exception {
  }

  @Test
  public void testCtor() {
    SnappyCompressionProvider ncp = new SnappyCompressionProvider();
    assertNotNull( ncp );
  }

  @Test
  public void testGetName() {
    SnappyCompressionProvider provider =
      (SnappyCompressionProvider) factory.getCompressionProviderByName( PROVIDER_NAME );
    assertNotNull( provider );
    assertEquals( PROVIDER_NAME, provider.getName() );
  }

  @Test
  public void testGetProviderAttributes() {
    SnappyCompressionProvider provider =
      (SnappyCompressionProvider) factory.getCompressionProviderByName( PROVIDER_NAME );
    assertEquals( "Snappy compression", provider.getDescription() );
    assertTrue( provider.supportsInput() );
    assertTrue( provider.supportsOutput() );
    assertNull( provider.getDefaultExtension() );
  }

  @Test
  public void testCreateInputStream() throws IOException {
    SnappyCompressionProvider provider =
      (SnappyCompressionProvider) factory.getCompressionProviderByName( PROVIDER_NAME );
    SnappyInputStream in = createSnappyInputStream();
    SnappyCompressionInputStream inStream = new SnappyCompressionInputStream( in, provider );
    assertNotNull( inStream );
    SnappyCompressionInputStream ncis = provider.createInputStream( in );
    assertNotNull( ncis );
  }

  @Test
  public void testCreateOutputStream() throws IOException {
    SnappyCompressionProvider provider =
      (SnappyCompressionProvider) factory.getCompressionProviderByName( PROVIDER_NAME );
    ByteArrayOutputStream out = new ByteArrayOutputStream();
    SnappyCompressionOutputStream outStream = new SnappyCompressionOutputStream( out, provider );
    assertNotNull( outStream );
    SnappyCompressionOutputStream ncis = provider.createOutputStream( out );
    assertNotNull( ncis );
  }

  private SnappyInputStream createSnappyInputStream() throws IOException {
    // Create an in-memory ZIP output stream for use by the input stream (to avoid exceptions)
    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    SnappyOutputStream sos = new SnappyOutputStream( baos );
    byte[] testBytes = "Test".getBytes();
    sos.write( testBytes );
    ByteArrayInputStream in = new ByteArrayInputStream( baos.toByteArray() );
    sos.close();

    return new SnappyInputStream( in );
  }
}
