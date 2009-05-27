package pm.ui.util.autocomplete;

import javax.swing.*;
import java.util.Vector;

/**
 * An editable combo class that will autocomplete the user entered text to the
 * entries in the combo drop down.
 * <p/>
 * You can directly add auto-complete to existing JComboBox derived classes
 * using: ComboCompleterFilter.addCompletion(yourCombo);
 *
 * @author ncochran
 */
public class CompleterComboBox extends JComboBox {

    public CompleterComboBox(ComboBoxModel aModel) {
        super(aModel);
    }

    public CompleterComboBox(Object[] items) {
        super(items);
        _init();
    }

    public CompleterComboBox(Vector<?> items) {
        super(items);
    }

    public CompleterComboBox() {
        super();
    }

    private void _init() {
        setEditable(true);

        _filter = ComboCompleterFilter.addCompletionMechanism(this);
    }

    public boolean isCaseSensitive() {
        return _filter.isCaseSensitive();
    }

    public boolean isCorrectingCase() {
        return _filter.isCorrectingCase();
    }

    public void setCaseSensitive(boolean caseSensitive) {
        _filter.setCaseSensitive(caseSensitive);
    }

    public void setCorrectCase(boolean correctCase) {
        _filter.setCorrectCase(correctCase);
    }

    private ComboCompleterFilter _filter;
}
