/*
 * ZakLineTableModel.java
 *
 * Created on July 19, 2005, 8:49 AM
 */

package blue.soundObject.editor.lineEditor;

import blue.BlueSystem;
import blue.automation.LineColors;
import blue.components.lines.Line;
import blue.components.lines.LineListTableModel;
import javax.swing.JOptionPane;

/**
 * TableModel for zak lines
 * 
 * @author mbechard
 */
public class ZakLineListTableModel extends LineListTableModel {

    /** Creates a new instance of ZakLineTableModel */
    public ZakLineListTableModel() {
    }

    /**
     * Adds a Line object, appropriately flagged as a zak line
     */
    @Override
    public void addLine(int index) {
        if (lines == null) {
            return;
        }

        Line line = new Line();

        line.setZak(true);
        line.setChannel(1);
        line.setColor(LineColors.getColor(lines.size()));

        if (index < 0 || index == lines.size() - 1) {
            lines.add(line);
            int row = lines.size() - 1;
            fireTableRowsInserted(row, row);
        } else {
            lines.add(index, line);
            fireTableRowsInserted(index, index);
        }
    }

    /**
     * Gets value stored in table, difference from base class being the 2nd
     * column is zak channel
     */
    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        if (lines == null) {
            return null;
        }

        if (columnIndex == 1) {
            Line line = lines.get(rowIndex);
            Integer tempChannel = new Integer(line.getChannel());
            return tempChannel.toString();
        } else {
            return super.getValueAt(rowIndex, columnIndex);
        }
    }

    /**
     * Sets value stored in table, difference from base class being the 2nd
     * column is zak channel
     */
    @Override
    public void setValueAt(Object value, int row, int col) {
        if (col == 1) {
            Line line = lines.get(row);
            String strChannel = (String) value;
            try {
                int tempChannel = Integer.parseInt(strChannel);
                if (tempChannel < 0) {
                    throw new NumberFormatException(
                            "Zak Channel numbers must be 0 or greater");
                }
                line.setChannel(tempChannel);
                fireTableCellUpdated(row, col);
            } catch (NumberFormatException e) {
                String errorMessage = BlueSystem
                        .getString("message.line.channelParseErr");
                JOptionPane.showMessageDialog(null, errorMessage, BlueSystem
                        .getString("message.line.badChannel"),
                        JOptionPane.ERROR_MESSAGE);
            }
        } else {
            super.setValueAt(value, row, col);
        }
    }

    /**
     * Returns name of table column, difference from base class being the 2nd
     * column is zak channel
     */
    @Override
    public String getColumnName(int column) {
        if (column == 1) {
            return BlueSystem.getString("lineObject.zakChannel");
        } else {
            return super.getColumnName(column);
        }
    }
}
