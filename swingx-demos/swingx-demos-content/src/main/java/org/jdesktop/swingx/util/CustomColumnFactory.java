/*
 * Created on 29.02.2008
 *
 */
package org.jdesktop.swingx.util;

import java.util.ArrayList;
import java.util.List;

import javax.swing.table.TableModel;

import org.jdesktop.swingx.JXTable;
import org.jdesktop.swingx.renderer.StringValue;
import org.jdesktop.swingx.table.ColumnFactory;
import org.jdesktop.swingx.table.TableColumnExt;

/**
 * A ColumnFactory is used by JXTable to create and configure all columns.
 * It can be set per-application (before creating an JXTable) or per-table
 * (before setting the model). <p>
 * 
 * Additional features:
 * <ul>
 * <li> changes the column titles to a more human readable form. 
 * <li> support list of identifiers to exclude from column creation
 * <li> support list of identifiers to hide on column configuration
 * <li> support pluggable maximal rowCount to measure
 * </ul>
 */
public  class CustomColumnFactory extends ColumnFactory {
    
    List<Object> excludes;
    List<Object> hidden;
    private StringValue titleStringValue;

    /**
     * 
     * @param names the identifiers to exclude.
     */
    public void addExcludeNames(Object... names) {
        for (Object object : names) {
            getExcludeNames().add(object);
        }
    }
    
    /**
     * 
     * @param names the identifiers to exclude.
     */
    public void addHiddenNames(Object... names) {
        for (Object object : names) {
            getHiddenNames().add(object);
        }
    }

    /**
     * 
     * @param sv the StringValue to use for column header "beautification".
     */
    public void setTitleStringValue(StringValue sv) {
        this.titleStringValue = sv;
    }
    
    /**
     * {@inheritDoc} <p>
     * Overridden to not create columns for the model columns which are 
     * listed in the excludes.
     */
    @Override
    public TableColumnExt createAndConfigureTableColumn(TableModel model,
            int modelIndex) {
        if (getExcludeNames().contains(model.getColumnName(modelIndex))) {
            return null;
        }
        return super.createAndConfigureTableColumn(model, modelIndex);
    }



    /**
     * {@inheritDoc} <p>
     * 
     * Overridden to set the column's identifier, beautify the display title.
     */
    @Override
    public void configureTableColumn(TableModel model,
            TableColumnExt columnExt) {
        super.configureTableColumn(model, columnExt);
        columnExt.setIdentifier(model.getColumnName(columnExt.getModelIndex()));
        beautifyColumnTitle(columnExt);
        if (getHiddenNames().contains(columnExt.getIdentifier())) {
            columnExt.setVisible(false);
        }
    }

    /**
     * {@inheritDoc} <p>
     * Overridden to check if the wants to limit the count of rows when
     * measuring. 
     */
    @Override
    protected int getRowCount(JXTable table) {
        if (table.getClientProperty("ColumnFactory.maxRowCount") instanceof Integer) {
            return Math.min(table.getRowCount(), 
                    (Integer) table.getClientProperty("ColumnFactory.maxRowCount"));
        }
        return super.getRowCount(table);
    }

    private void beautifyColumnTitle(TableColumnExt columnExt) {
        if (titleStringValue != null) {
            columnExt.setTitle(titleStringValue.getString(columnExt.getTitle()));
        }
    }

    private List<Object> getExcludeNames() {
        if (excludes == null) {
            excludes = new ArrayList<Object>();
        }
        return excludes;
    }
    
    private List<Object> getHiddenNames() {
        if (hidden == null) {
            hidden = new ArrayList<Object>();
        }
        return hidden;
    }
}

