package org.pentaho.di.trans.steps.mapping;

import junit.framework.Assert;

import org.junit.Test;

public class MappingValueRenameTest {

  /**
   * PDI-11420 target and source values does not becomes nulls.
   */
  @Test
  public void testMappingValueRenameNullAwareSetters() {
    MappingValueRename mr = new MappingValueRename( "a", "b" );

    Assert.assertEquals( "Source value name is correct", "a", mr.getSourceValueName() );
    Assert.assertEquals( "Target value name is correct", "b", mr.getTargetValueName() );

    mr.setSourceValueName( null );
    mr.setTargetValueName( null );

    Assert.assertEquals( "Source value name is set to empty String", "", mr.getSourceValueName() );
    Assert.assertEquals( "Target value name is set to empty String", "", mr.getTargetValueName() );

    mr.setSourceValueName( "c" );
    mr.setTargetValueName( "d" );

    Assert.assertEquals( "Source value name is correct", "c", mr.getSourceValueName() );
    Assert.assertEquals( "Target value name is correct", "d", mr.getTargetValueName() );
  }

}
