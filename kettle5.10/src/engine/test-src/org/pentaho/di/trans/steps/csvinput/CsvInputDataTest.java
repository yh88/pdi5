package org.pentaho.di.trans.steps.csvinput;

import static junit.framework.Assert.assertEquals;

import org.junit.Test;

public class CsvInputDataTest {
  @Test
  public void testRemoveEscapedEnclosuresWithOneEscapedInMiddle() {
    CsvInputData csvInputData = new CsvInputData();
    csvInputData.enclosure = "\"".getBytes();
    String result = new String( csvInputData.removeEscapedEnclosures( "abcd \"\" defg".getBytes(), 1 ) );
    assertEquals( "abcd \" defg", result );
  }

  @Test
  public void testRemoveEscapedEnclosuresWithTwoEscapedInMiddle() {
    CsvInputData csvInputData = new CsvInputData();
    csvInputData.enclosure = "\"".getBytes();
    String result = new String( csvInputData.removeEscapedEnclosures( "abcd \"\"\"\" defg".getBytes(), 2 ) );
    assertEquals( "abcd \"\" defg", result );
  }

  @Test
  public void testRemoveEscapedEnclosuresWithOneByItself() {
    CsvInputData csvInputData = new CsvInputData();
    csvInputData.enclosure = "\"".getBytes();
    String result = new String( csvInputData.removeEscapedEnclosures( "\"\"".getBytes(), 1 ) );
    assertEquals( "\"", result );
  }

  @Test
  public void testRemoveEscapedEnclosuresWithTwoByThemselves() {
    CsvInputData csvInputData = new CsvInputData();
    csvInputData.enclosure = "\"".getBytes();
    String result = new String( csvInputData.removeEscapedEnclosures( "\"\"\"\"".getBytes(), 2 ) );
    assertEquals( "\"\"", result );
  }

  @Test
  public void testRemoveEscapedEnclosuresWithCharacterInTheMiddleOfThem() {
    CsvInputData csvInputData = new CsvInputData();
    csvInputData.enclosure = "\"".getBytes();
    String result = new String( csvInputData.removeEscapedEnclosures( "345\"\"1\"\"abc".getBytes(), 2 ) );
    assertEquals( "345\"1\"abc", result );
  }
}
