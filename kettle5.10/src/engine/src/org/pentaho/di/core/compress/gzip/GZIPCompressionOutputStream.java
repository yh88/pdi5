package org.pentaho.di.core.compress.gzip;

import java.io.IOException;
import java.io.OutputStream;
import java.util.zip.GZIPOutputStream;
import java.util.zip.ZipOutputStream;

import org.pentaho.di.core.compress.CompressionOutputStream;
import org.pentaho.di.core.compress.CompressionProvider;

public class GZIPCompressionOutputStream extends CompressionOutputStream {

  public GZIPCompressionOutputStream( OutputStream out, CompressionProvider provider ) throws IOException {
    super( getDelegate( out ), provider );

  }

  protected static GZIPOutputStream getDelegate( OutputStream out ) throws IOException {
    GZIPOutputStream delegate = null;
    if ( out instanceof ZipOutputStream ) {
      delegate = (GZIPOutputStream) out;
    } else {
      delegate = new GZIPOutputStream( out );
    }
    return delegate;
  }

  @Override
  public void close() throws IOException {
    GZIPOutputStream zos = (GZIPOutputStream) delegate;
    zos.finish();
  }
}
