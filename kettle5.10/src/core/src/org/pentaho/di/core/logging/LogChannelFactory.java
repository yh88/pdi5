package org.pentaho.di.core.logging;

public class LogChannelFactory implements LogChannelInterfaceFactory {
  public LogChannel create( Object subject ) {
    return new LogChannel( subject );
  }

  public LogChannel create( Object subject, boolean gatheringMetrics ) {
    return new LogChannel( subject, gatheringMetrics );
  }

  public LogChannel create( Object subject, LoggingObjectInterface parentObject ) {
    return new LogChannel( subject, parentObject );
  }

  public LogChannel create( Object subject, LoggingObjectInterface parentObject, boolean gatheringMetrics ) {
    return new LogChannel( subject, parentObject, gatheringMetrics );
  }
}
