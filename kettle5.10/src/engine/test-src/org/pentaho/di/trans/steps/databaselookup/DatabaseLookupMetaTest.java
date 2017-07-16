package org.pentaho.di.trans.steps.databaselookup;

import static org.junit.Assert.*;

import org.junit.Test;
import org.pentaho.di.core.exception.KettleStepException;
import org.pentaho.di.core.row.RowMeta;
import org.pentaho.di.core.row.RowMetaInterface;
import org.pentaho.di.core.row.ValueMetaInterface;
import org.pentaho.di.core.row.value.ValueMetaString;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class DatabaseLookupMetaTest {

  private DatabaseLookupMeta databaseLookupMeta = new DatabaseLookupMeta();

  @Test
  public void getFieldWithValueUsedTwice() throws KettleStepException {

    databaseLookupMeta.setReturnValueField( new String[] { "match", "match", "mismatch" } );
    databaseLookupMeta.setReturnValueNewName( new String[] { "v1", "v2", "v3" } );

    ValueMetaInterface v1 = new ValueMetaString( "match" );
    ValueMetaInterface v2 = new ValueMetaString( "match1" );
    RowMetaInterface[] info = new RowMetaInterface[1];
    info[0] = new RowMeta();
    info[0].setValueMetaList( Arrays.asList( v1, v2 ) );

    ValueMetaInterface r1 = new ValueMetaString( "value" );
    RowMetaInterface row = new RowMeta();
    row.setValueMetaList( new ArrayList<ValueMetaInterface>( Arrays.asList( r1 ) ) );

    databaseLookupMeta.getFields( row, "", info, null, null, null, null );

    List<ValueMetaInterface> expectedRow =
      Arrays.asList( new ValueMetaInterface[] {
        new ValueMetaString( "value" ), new ValueMetaString( "v1" ), new ValueMetaString( "v2" ), } );
    assertEquals( 3, row.getValueMetaList().size() );
    for ( int i = 0; i < 3; i++ ) {
      assertEquals( expectedRow.get( i ).getName(), row.getValueMetaList().get( i ).getName() );
    }
  }
}
