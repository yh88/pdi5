/**
 * Author = Shailesh Ahuja
 */

package org.pentaho.di.trans.steps.excelinput.staxpoi;

import org.pentaho.di.core.spreadsheet.KCell;
import org.pentaho.di.core.spreadsheet.KCellType;

public class StaxPoiCell implements KCell {

  private KCellType type;
  private Object value;
  private int row;

  public StaxPoiCell( String value, int row ) {
    this.value = value;
    this.row = row;
    type = KCellType.STRING_FORMULA;
  }

  public StaxPoiCell( Object value, KCellType type, int row ) {
    this.value = value;
    this.type = type;
    this.row = row;
  }

  @Override
  public KCellType getType() {
    return type;
  }

  @Override
  public Object getValue() {
    return value;
  }

  @Override
  public String getContents() {
    return value == null ? null : value.toString();
  }

  @Override
  public int getRow() {
    return row;
  }

}
